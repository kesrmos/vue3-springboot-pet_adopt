# ============================================================
# 宠物领养系统 环境自检脚本（PowerShell 5.1+）
# 用法：powershell -ExecutionPolicy Bypass -File scripts\check-env.ps1
# 检查项：3306 / 8080 / 5173 端口、pet_adopt 库表与数据、后端接口、Vite 代理
# ============================================================

$ErrorActionPreference = 'Continue'
$script:failed = 0
$script:passed = 0

function Write-Check {
    param([string]$Name, [bool]$Ok, [string]$Detail)
    if ($Ok) {
        $script:passed++
        Write-Host ("[PASS] {0} {1}" -f $Name, $Detail) -ForegroundColor Green
    } else {
        $script:failed++
        Write-Host ("[FAIL] {0} {1}" -f $Name, $Detail) -ForegroundColor Red
    }
}

function Test-Port {
    param([int]$Port)
    return [bool](Get-NetTCPConnection -State Listen -LocalPort $Port -ErrorAction SilentlyContinue)
}

Write-Host '===== 1. 端口检查 ====='
Write-Check 'MySQL 3306' (Test-Port 3306) '数据库服务'
Write-Check '后端 8080' (Test-Port 8080) '接口服务（未启动时前端 /api 请求会返回 HTTP 500 空响应）'
Write-Check '前端 5173' (Test-Port 5173) 'vite dev server'

Write-Host ''
Write-Host '===== 2. MySQL 客户端与库表检查 ====='

function Find-Mysql {
    $cmd = Get-Command mysql.exe -ErrorAction SilentlyContinue
    if ($cmd) { return $cmd.Source }
    $candidates = @(
        $env:MYSQL_HOME,
        'D:\devc\MySQL\mysql-26.7.0-winx64',
        'C:\Program Files\MySQL\MySQL Server 8.0',
        'C:\Program Files\MySQL\MySQL Server 8.4',
        'C:\Program Files\MySQL\MySQL Server 9.0'
    ) | Where-Object { $_ }
    foreach ($dir in $candidates) {
        $exe = Join-Path $dir 'bin\mysql.exe'
        if (Test-Path $exe) { return $exe }
    }
    return $null
}

$mysql = Find-Mysql
if (-not $mysql) {
    Write-Check 'mysql 客户端' $false '未找到 mysql.exe，可设置环境变量 MYSQL_HOME 指向 MySQL 安装目录'
} else {
    Write-Host ("     使用客户端: {0}" -f $mysql)
    $dbUser = if ($env:DB_USERNAME) { $env:DB_USERNAME } else { 'root' }
    $dbPass = if ($null -ne $env:DB_PASSWORD) { $env:DB_PASSWORD } else { '' }
    $sql = "SELECT CONCAT(" +
           "(SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA='pet_adopt' AND TABLE_NAME IN ('user','pet','adopt_apply')), ',', " +
           "(SELECT COUNT(*) FROM pet_adopt.`user`), ',', " +
           "(SELECT COUNT(*) FROM pet_adopt.pet), ',', " +
           "(SELECT COUNT(*) FROM pet_adopt.adopt_apply));"
    $out = & $mysql "-u$dbUser" "--password=$dbPass" '-h' '127.0.0.1' '-P' '3306' '--default-character-set=utf8mb4' '-N' '-B' '-e' $sql 2>$null
    $line = ($out | Where-Object { $_ -match '^\d+,\d+,\d+,\d+$' } | Select-Object -First 1)
    if (-not $line) {
        Write-Check 'pet_adopt 库表' $false "查询失败，请确认库已初始化、账号密码正确（当前账号 $dbUser）"
    } else {
        $parts = $line.Split(',')
        Write-Check 'pet_adopt 库表' ($parts[0] -eq '3') ("表数量=" + $parts[0] + "（期望 3）")
        Write-Check 'user 数据'      ($parts[1] -eq '3') ("行数=" + $parts[1] + "（期望 3）")
        Write-Check 'pet 数据'       ($parts[2] -eq '6') ("行数=" + $parts[2] + "（期望 6）")
        Write-Check 'adopt_apply 数据' ($parts[3] -eq '2') ("行数=" + $parts[3] + "（期望 2）")
    }
}

Write-Host ''
Write-Host '===== 3. 后端接口检查 ====='
$base = 'http://localhost:8080/api'

function Invoke-Api {
    param([string]$Method, [string]$Url, [string]$Token, [string]$Body)
    $headers = @{}
    if ($Token) { $headers['Authorization'] = "Bearer $Token" }
    $params = @{
        Uri             = $Url
        Method          = $Method
        Headers         = $headers
        UseBasicParsing = $true
        TimeoutSec      = 10
    }
    if ($Body) {
        $params['Body'] = [System.Text.Encoding]::UTF8.GetBytes($Body)
        $params['ContentType'] = 'application/json; charset=utf-8'
    }
    try {
        $resp = Invoke-WebRequest @params
        $text = [System.Text.Encoding]::UTF8.GetString($resp.RawContentStream.ToArray())
        return ($text | ConvertFrom-Json)
    } catch {
        $text = $null
        if ($_.Exception.Response) {
            try {
                $stream = $_.Exception.Response.GetResponseStream()
                if ($stream) { $text = (New-Object System.IO.StreamReader($stream)).ReadToEnd() }
            } catch { }
        }
        if ($text) {
            try { return ($text | ConvertFrom-Json) } catch { return $null }
        }
        return $null
    }
}

if (Test-Port 8080) {
    $r = Invoke-Api -Method GET -Url "$base/pet/list?page=1&size=1"
    Write-Check 'GET /pet/list' ($r -and $r.code -eq 200) ("code=" + $(if ($r) { $r.code } else { '无响应' }))

    $r = Invoke-Api -Method GET -Url "$base/pet/1"
    Write-Check 'GET /pet/1' ($r -and $r.code -eq 200) ("code=" + $(if ($r) { $r.code } else { '无响应' }))

    $r = Invoke-Api -Method GET -Url "$base/pet/999999"
    Write-Check 'GET /pet/999999（应 404）' ($r -and $r.code -eq 404) ("code=" + $(if ($r) { $r.code } else { '无响应' }))

    $r = Invoke-Api -Method POST -Url "$base/user/login" -Body '{"username":"zhangsan","password":"123456"}'
    $hasToken = ($r -and $r.code -eq 200 -and $r.data.token)
    Write-Check 'POST /user/login' ([bool]$hasToken) ("code=" + $(if ($r) { $r.code } else { '无响应' }))

    $r = Invoke-Api -Method POST -Url "$base/admin/login" -Body '{"username":"admin","password":"123456"}'
    $adminToken = if ($r -and $r.data) { $r.data.token } else { $null }
    Write-Check 'POST /admin/login' ([bool]$adminToken) ("code=" + $(if ($r) { $r.code } else { '无响应' }))

    if ($adminToken) {
        $r = Invoke-Api -Method GET -Url "$base/admin/pet/list?page=1&size=1" -Token $adminToken
        Write-Check 'GET /admin/pet/list' ($r -and $r.code -eq 200) ("code=" + $(if ($r) { $r.code } else { '无响应' }))
    }
} else {
    Write-Host '     [SKIP] 后端 8080 未启动，跳过接口检查' -ForegroundColor Yellow
}

Write-Host ''
Write-Host '===== 4. Vite 代理检查 ====='
if ((Test-Port 5173) -and (Test-Port 8080)) {
    $r = Invoke-Api -Method GET -Url 'http://localhost:5173/api/pet/list?page=1&size=1'
    Write-Check 'Vite 代理 /api' ($r -and $r.code -eq 200) ("code=" + $(if ($r) { $r.code } else { '无响应/HTTP 500' }))
} elseif (Test-Port 5173) {
    Write-Check 'Vite 代理 /api' $false '后端 8080 未启动：浏览器里所有 /api 请求都会显示 HTTP 500（响应体为空）'
} else {
    Write-Host '     [SKIP] 前端 5173 未启动，跳过代理检查' -ForegroundColor Yellow
}

Write-Host ''
Write-Host ('===== 自检完成：通过 {0} 项，失败 {1} 项 =====' -f $script:passed, $script:failed)
if ($script:failed -gt 0) { exit 1 }
