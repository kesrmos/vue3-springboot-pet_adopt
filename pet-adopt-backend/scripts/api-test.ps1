# ============================================================
# 宠物领养系统 后端接口自测脚本（PowerShell 5.1+）
# 用法：
#   1. 先启动后端（http://localhost:8080/api）
#   2. 在 pet-adopt-backend 目录执行：
#      powershell -ExecutionPolicy Bypass -File scripts\api-test.ps1
# ============================================================

$ErrorActionPreference = 'Continue'
$base = 'http://localhost:8080/api'
$script:failed = 0
$script:passed = 0

function Invoke-Api {
    param(
        [string]$Method,
        [string]$Path,
        [string]$Token,
        [string]$Body
    )
    $headers = @{}
    if ($Token) { $headers['Authorization'] = "Bearer $Token" }
    $params = @{
        Uri             = "$base$Path"
        Method          = $Method
        Headers         = $headers
        UseBasicParsing = $true
        TimeoutSec      = 15
    }
    if ($Body) {
        $params['Body'] = [System.Text.Encoding]::UTF8.GetBytes($Body)
        $params['ContentType'] = 'application/json; charset=utf-8'
    }

    try {
        $resp = Invoke-WebRequest @params
        $text = [System.Text.Encoding]::UTF8.GetString($resp.RawContentStream.ToArray())
        return [pscustomobject]@{ Http = [int]$resp.StatusCode; Json = ($text | ConvertFrom-Json); Error = $null }
    } catch {
        $status = 0
        $text = $null
        if ($_.Exception.Response) {
            $status = [int]$_.Exception.Response.StatusCode
            try {
                $stream = $_.Exception.Response.GetResponseStream()
                if ($stream) { $text = (New-Object System.IO.StreamReader($stream)).ReadToEnd() }
            } catch { }
        }
        $json = $null
        if ($text) {
            try { $json = $text | ConvertFrom-Json } catch { }
        }
        return [pscustomobject]@{ Http = $status; Json = $json; Error = $_.Exception.Message }
    }
}

function Assert-Code {
    param([string]$Title, $Result, [int]$Expected)
    $code = if ($Result.Json) { $Result.Json.code } else { $null }
    $msg = if ($Result.Json) { $Result.Json.msg } else { $Result.Error }
    if ($code -eq $Expected) {
        $script:passed++
        Write-Host ("[PASS] {0} HTTP={1} code={2} msg={3}" -f $Title, $Result.Http, $code, $msg) -ForegroundColor Green
    } else {
        $script:failed++
        Write-Host ("[FAIL] {0} HTTP={1} code={2} msg={3}（期望 code={4}）" -f $Title, $Result.Http, $code, $msg, $Expected) -ForegroundColor Red
    }
}

function Assert-CodeIn {
    param([string]$Title, $Result, [int[]]$Expected)
    $code = if ($Result.Json) { $Result.Json.code } else { $null }
    $msg = if ($Result.Json) { $Result.Json.msg } else { $Result.Error }
    if ($Expected -contains $code) {
        $script:passed++
        Write-Host ("[PASS] {0} HTTP={1} code={2} msg={3}" -f $Title, $Result.Http, $code, $msg) -ForegroundColor Green
    } else {
        $script:failed++
        Write-Host ("[FAIL] {0} HTTP={1} code={2} msg={3}（期望 code={4}）" -f $Title, $Result.Http, $code, $msg, ($Expected -join '/')) -ForegroundColor Red
    }
}

Write-Host '===== 1. 宠物分页列表（免登录） ====='
$r = Invoke-Api -Method GET -Path '/pet/list?page=1&size=3'
Assert-Code 'pet/list' $r 200
$total = if ($r.Json) { $r.Json.data.total } else { 0 }
Write-Host ("     total={0}" -f $total)

Write-Host '===== 2. 用户登录 ====='
$r = Invoke-Api -Method POST -Path '/user/login' -Body '{"username":"zhangsan","password":"123456"}'
Assert-Code 'user/login' $r 200
$userToken = if ($r.Json) { $r.Json.data.token } else { $null }

Write-Host '===== 3. 未登录访问我的申请（应 401） ====='
$r = Invoke-Api -Method GET -Path '/apply/my'
Assert-Code 'apply/my 无token' $r 401

Write-Host '===== 4. 非法 token（应 401） ====='
$r = Invoke-Api -Method GET -Path '/apply/my' -Token 'bad.token.value'
Assert-Code 'apply/my 非法token' $r 401

Write-Host '===== 5. 提交领养申请（petId=2，重复提交返回 400 属正常） ====='
$r = Invoke-Api -Method POST -Path '/apply/submit' -Token $userToken -Body '{"petId":2,"liveEnv":"自有住房，两室一厅","exp":"养过一只橘猫3年","remark":"希望给它一个家"}'
Assert-CodeIn 'apply/submit' $r @(200, 400)

Write-Host '===== 6. 我的领养申请 ====='
$r = Invoke-Api -Method GET -Path '/apply/my' -Token $userToken
Assert-Code 'apply/my' $r 200
Write-Host '===== 7. 普通用户访问管理接口（应 403） ====='
$r = Invoke-Api -Method GET -Path '/admin/user/list' -Token $userToken
Assert-Code 'admin/user/list 普通用户' $r 403

Write-Host '===== 8. 管理员登录 ====='
$r = Invoke-Api -Method POST -Path '/admin/login' -Body '{"username":"admin","password":"123456"}'
Assert-Code 'admin/login' $r 200
$adminToken = if ($r.Json) { $r.Json.data.token } else { $null }

Write-Host '===== 9. 管理员：领养申请分页 ====='
$r = Invoke-Api -Method GET -Path '/admin/apply/list?page=1&size=10' -Token $adminToken
Assert-Code 'admin/apply/list' $r 200

$pendingId = $null
if ($r.Json -and $r.Json.data.records.Count -gt 0) {
    $pending = $r.Json.data.records | Where-Object { $_.auditStatus -eq 0 } | Select-Object -First 1
    if ($pending) { $pendingId = $pending.id }
}
if ($pendingId) {
    Write-Host ("     待审核 applyId={0}" -f $pendingId)
    Write-Host '===== 10. 管理员：审核通过 ====='
    $body = "{`"applyId`":$pendingId,`"auditStatus`":1,`"auditRemark`":`"条件符合，同意领养`"}"
    $r = Invoke-Api -Method PUT -Path '/admin/apply/audit' -Token $adminToken -Body $body
    Assert-Code 'admin/apply/audit' $r 200
} else {
    Write-Host '     [SKIP] 没有待审核申请，跳过审核接口' -ForegroundColor Yellow
}

Write-Host '===== 11. 管理员：新增宠物 -> 查询 -> 删除 ====='
$r = Invoke-Api -Method POST -Path '/admin/pet/save' -Token $adminToken -Body '{"name":"接口自测宠物","type":"狗","gender":"母","age":5,"photo":"https://picsum.photos/id/1005/600/400","description":"接口自测创建","status":0}'
Assert-Code 'admin/pet/save(新增)' $r 200

$newPetId = $null
$r = Invoke-Api -Method GET -Path '/admin/pet/list?page=1&size=10&name=%E6%8E%A5%E5%8F%A3%E8%87%AA%E6%B5%8B%E5%AE%A0%E7%89%A9' -Token $adminToken
Assert-Code 'admin/pet/list(按名称)' $r 200
if ($r.Json -and $r.Json.data.records.Count -gt 0) { $newPetId = $r.Json.data.records[0].id }

if ($newPetId) {
    Write-Host ("     新增宠物 id={0}" -f $newPetId)
    $r = Invoke-Api -Method DELETE -Path "/admin/pet/$newPetId" -Token $adminToken
    Assert-Code 'admin/pet 删除' $r 200
} else {
    Write-Host '     [SKIP] 未查到自测宠物，跳过删除' -ForegroundColor Yellow
}

Write-Host '===== 12. 管理员：用户管理分页 ====='
$r = Invoke-Api -Method GET -Path '/admin/user/list?page=1&size=10' -Token $adminToken
Assert-Code 'admin/user/list' $r 200
if ($r.Json) {
    $hasPassword = $null -ne $r.Json.data.records[0].password
    Write-Host ("     共 {0} 个用户，是否包含 password 字段: {1}" -f $r.Json.data.total, $hasPassword)
}

Write-Host '===== 13. 边界校验 ====='
$r = Invoke-Api -Method GET -Path '/pet/999999'
Assert-Code 'pet/999999（不存在）' $r 404

$r = Invoke-Api -Method GET -Path '/pet/list?page=abc'
Assert-Code 'pet/list?page=abc（类型错误）' $r 400

$r = Invoke-Api -Method GET -Path '/no-such-api'
Assert-Code '未知接口' $r 404

Write-Host ''
Write-Host ('===== 自测完成：通过 {0} 项，失败 {1} 项 =====' -f $script:passed, $script:failed)
if ($script:failed -gt 0) { exit 1 }

