# 宠物领养系统后端（pet-adopt-backend）

配套 Vue3 前端的宠物领养系统后端，学习用简化版本。

## 技术栈

| 技术 | 版本 | 说明 |
|---|---|---|
| Spring Boot | 2.7.18 | Web + Validation |
| MyBatis-Plus | 3.5.3.1 | 单表 CRUD + 分页插件 + XML 联表查询 |
| MySQL | 8.x | 数据库 |
| jjwt | 0.11.5 | JWT 登录令牌（HS256） |
| Lombok | 随 Spring Boot | 简化实体类 |

- 端口：`8080`
- 上下文路径：`/api`（所有接口都以 `http://localhost:8080/api` 开头）
- 无 Redis、无 Spring Security，密码**明文**存储（仅用于学习）
- 统一返回：`{ "code": 200, "data": ..., "msg": "..." }`，`code` 取值 200/400/401/403/404/500

---

## 一、目录结构

```
pet-adopt-backend
├── pom.xml
├── sql/pet_adopt.sql                     # 建库建表 + 初始化数据
├── scripts/api-test.ps1                  # 一键接口自测脚本（可选）
├── scripts/check-env.ps1                 # 环境自检脚本（端口/库表/接口/代理）
└── src/main
    ├── java/com/example/petadopt
    │   ├── PetAdoptApplication.java      # 启动类
    │   ├── common/                       # Result、PageResult
    │   ├── config/                       # MyBatis-Plus 分页、跨域、Jackson 时间格式
    │   ├── controller/                   # UserController、PetController、ApplyController、AdminController
    │   ├── dto/                          # 登录、提交申请、审核参数
    │   ├── entity/                       # User、Pet、AdoptApply
    │   ├── exception/                    # BizException、全局异常处理器
    │   ├── jwt/                          # JwtUtil、JwtInterceptor、UserContext
    │   ├── mapper/                       # 三个 Mapper
    │   ├── service/impl/                 # 业务实现
    │   └── vo/                           # LoginVO、UserVO、ApplyVO
    └── resources
        ├── application.yml
        └── mapper/AdoptApplyMapper.xml   # 领养申请联表查询
```

---

## 二、数据库导入步骤

### 方式 1：命令行（推荐）

```bash
# 1. 启动 MySQL 服务后执行（会先 DROP 再 CREATE 数据库 pet_adopt）
mysql -u root -p --default-character-set=utf8mb4 -e "source D:/devcProgram/VSCode_Pro/pet_adopt/pet-adopt-backend/sql/pet_adopt.sql"

# 2. 验证
mysql -u root -p -e "use pet_adopt; show tables; select id,name,status from pet;"
```

> Windows 下路径分隔符用 `/`，不要直接复制反斜杠。

### 方式 2：图形化工具

1. 打开 Navicat / DataGrip / MySQL Workbench，连接本机 MySQL；
2. 打开 `sql/pet_adopt.sql`；
3. 全选执行（脚本已包含建库语句 `CREATE DATABASE pet_adopt`）。

### 初始化账号

| 角色 | 用户名 | 密码 |
|---|---|---|
| 管理员 | `admin` | `123456` |
| 普通用户 | `zhangsan` | `123456` |
| 普通用户 | `lisi` | `123456` |

---

## 三、修改数据库连接

打开 `src/main/resources/application.yml`，按本机情况修改：

```yaml
spring:
  datasource:
    # 也支持环境变量覆盖：DB_URL / DB_USERNAME / DB_PASSWORD
    url: "${DB_URL:jdbc:mysql://127.0.0.1:3306/pet_adopt?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true}"
    username: "${DB_USERNAME:root}"
    password: "${DB_PASSWORD:}"      # 默认 root 空密码，有密码就填这里
```

JWT 相关配置（可选修改，也支持环境变量 `JWT_SECRET` / `JWT_EXPIRE`）：

```yaml
jwt:
  secret: "${JWT_SECRET:pet-adopt-jwt-secret-key-please-change-2024}"   # 长度必须 >= 32 字节
  expire: ${JWT_EXPIRE:86400000}                                       # 24 小时
```

---

## 四、启动方式

### 方式 1：Maven（无需 IDEA）

```bash
cd pet-adopt-backend
mvn clean package -DskipTests
mvn spring-boot:run
# 或直接运行 jar
java -jar target/pet-adopt-backend.jar
```

### 方式 2：IDEA

1. `File -> Open` 选择 `pet-adopt-backend` 目录（识别为 Maven 项目，等待依赖下载）；
2. 运行 `PetAdoptApplication`；
3. 控制台出现 `宠物领养系统后端启动成功` 即成功。

启动后访问：`http://localhost:8080/api/pet/list?page=1&size=10`

### 方式 3：一键接口自测脚本（可选）

项目自带 PowerShell 自测脚本，会依次调用登录、宠物列表、提交申请、审核、增删宠物、用户列表等接口，并打印每个接口的 `code`：

```powershell
# 先启动后端，再在项目根目录执行
powershell -ExecutionPolicy Bypass -File scripts\api-test.ps1
```

> 脚本会写入测试数据；想恢复初始数据，重新执行一次 `sql/pet_adopt.sql` 即可。

### 方式 4：环境自检脚本（排查 500 / 白屏用）

`scripts/check-env.ps1` 会一次性检查：3306/8080/5173 端口、`pet_adopt` 三张表与数据量、后端关键接口（宠物列表/详情/登录/管理员列表）、Vite 代理是否可用。

```powershell
# 推荐先跑这个，能直接定位"前端 500 但后端没收到请求"的问题
powershell -ExecutionPolicy Bypass -File scripts\check-env.ps1
```

> 出现 `[FAIL] 后端 8080` 或 Vite 终端 `http proxy error: ... ECONNREFUSED`，说明后端没启动；此时浏览器里所有 `/api` 请求都会显示 **HTTP 500 且响应体为空**，并不是接口代码报错。


> 项目使用 JDK 11 编译级别，实测在 JDK 21 上可正常编译运行；若使用 JDK 8，请把 `pom.xml` 的 `<java.version>` 改成 `1.8`。


---

## 五、接口文档 + Postman 测试示例

**通用约定**

- 需要登录的接口在 Header 中带 token，三种写法都支持：
  - `Authorization: Bearer <token>`（推荐）
  - `Authorization: <token>`
  - `token: <token>`
- 未登录/过期：`{"code":401,"data":null,"msg":"未登录，请先登录"}`（HTTP 状态码仍为 200，前端按 code 判断）
- 权限不足：`{"code":403,"data":null,"msg":"权限不足，仅管理员可访问"}`
- 业务失败：`{"code":500,"data":null,"msg":"具体原因"}`

### 1. 用户登录

```
POST http://localhost:8080/api/user/login
Content-Type: application/json

{ "username": "zhangsan", "password": "123456" }
```

响应：

```json
{
  "code": 200,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9....",
    "userInfo": {
      "id": 2, "username": "zhangsan", "name": "张三",
      "phone": "13811112222", "address": "北京市海淀区中关村1号",
      "role": 0, "createTime": "2026-11-04 21:20:00"
    },
    "role": 0
  },
  "msg": "登录成功"
}
```

### 2. 宠物分页列表（免登录）

```
GET http://localhost:8080/api/pet/list?page=1&size=10&type=狗&gender=公&status=0
```

响应 `data`：

```json
{
  "total": 3,
  "records": [
    { "id": 1, "name": "豆豆", "type": "狗", "gender": "公", "age": 8,
      "photo": "https://picsum.photos/id/237/600/400",
      "description": "金毛幼犬...", "status": 0, "createTime": "2026-11-04 21:20:00" }
  ],
  "page": 1,
  "size": 10
}
```

> 参数均可省略：`type`、`gender`、`status` 不传表示不限；`page` 默认 1，`size` 默认 10（最大 100）。

### 3. 宠物详情（免登录）

```
GET http://localhost:8080/api/pet/1
```

### 4. 提交领养申请（需登录）

```
POST http://localhost:8080/api/apply/submit
Authorization: Bearer <zhangsan 的 token>
Content-Type: application/json

{ "petId": 2, "liveEnv": "自有住房，两室一厅", "exp": "养过一只橘猫3年", "remark": "希望给它一个家" }
```

常见失败：`该宠物已被领养`、`您已提交过该宠物的领养申请`。

### 5. 我的领养申请（需登录）

```
GET http://localhost:8080/api/apply/my
Authorization: Bearer <zhangsan 的 token>
```

`data` 为数组：

```json
[{ "id": 3, "userId": 2, "petId": 2, "liveEnv": "自有住房，两室一厅",
   "exp": "养过一只橘猫3年", "remark": "希望给它一个家",
   "auditStatus": 0, "auditRemark": null, "createTime": "2026-11-04 21:25:00",
   "petName": "咪咪", "petType": "猫", "petGender": "母", "petAge": 12,
   "petPhoto": "https://picsum.photos/id/40/600/400", "petStatus": 0,
   "userName": "张三", "userPhone": "13811112222", "userAddress": "北京市海淀区中关村1号" }]
```

### 6. 管理员登录

```
POST http://localhost:8080/api/admin/login
Content-Type: application/json

{ "username": "admin", "password": "123456" }
```

普通用户调用后台登录会返回 `code=403`。

### 7. 管理员宠物分页列表

```
GET http://localhost:8080/api/admin/pet/list?page=1&size=10&name=豆&type=狗&status=0
Authorization: Bearer <admin 的 token>
```

### 8. 新增 / 编辑宠物

```
POST http://localhost:8080/api/admin/pet/save
Authorization: Bearer <admin 的 token>
Content-Type: application/json

// 新增（不传 id）
{ "name": "小白", "type": "狗", "gender": "母", "age": 5,
  "photo": "https://picsum.photos/id/1005/600/400",
  "description": "萨摩耶幼犬，性格温顺", "status": 0 }

// 编辑（传 id）
{ "id": 7, "name": "小白", "type": "狗", "gender": "母", "age": 6,
  "photo": "https://picsum.photos/id/1005/600/400",
  "description": "萨摩耶，已打疫苗", "status": 1 }
```

### 9. 删除宠物

```
DELETE http://localhost:8080/api/admin/pet/7
Authorization: Bearer <admin 的 token>
```

> 删除宠物时会同步删除该宠物的领养申请记录。

### 10. 全部领养申请分页

```
GET http://localhost:8080/api/admin/apply/list?page=1&size=10&auditStatus=0
Authorization: Bearer <admin 的 token>
```

### 11. 审核领养申请

```
PUT http://localhost:8080/api/admin/apply/audit
Authorization: Bearer <admin 的 token>
Content-Type: application/json

{ "applyId": 3, "auditStatus": 1, "auditRemark": "条件符合，同意领养" }
```

- `auditStatus`：1 通过，2 驳回
- 通过后：宠物 `status` 变成 1（已领养），该宠物其它"待审核"申请自动驳回

### 12. 用户管理分页

```
GET http://localhost:8080/api/admin/user/list?page=1&size=10&keyword=张
Authorization: Bearer <admin 的 token>
```

返回的用户信息不含密码。

---

## 六、前端对接提示（Vue3 + axios）

```js
// src/utils/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

request.interceptors.response.use(res => {
  const { code, data, msg } = res.data
  if (code === 200) return data
  if (code === 401) {
    localStorage.removeItem('token')
    location.href = '/login'
  }
  ElMessage.error(msg)
  return Promise.reject(new Error(msg))
})
```

接口与页面对应关系：

| 前端页面 | 接口 |
|---|---|
| 登录页 | `POST /user/login` |
| 宠物列表 | `GET /pet/list` |
| 宠物详情 | `GET /pet/{id}` |
| 提交领养 | `POST /apply/submit` |
| 我的申请 | `GET /apply/my` |
| 后台登录 | `POST /admin/login` |
| 宠物管理 | `GET /admin/pet/list`、`POST /admin/pet/save`、`DELETE /admin/pet/{id}` |
| 领养审核 | `GET /admin/apply/list`、`PUT /admin/apply/audit` |
| 用户管理 | `GET /admin/user/list` |

---

## 七、常见问题

| 问题 | 解决办法 |
|---|---|
| 前端所有接口返回 **HTTP 500 且响应体为空** | 后端没启动/启动失败：Vite 代理 `ECONNREFUSED` 时会自己返回 500。先 `netstat -ano | findstr :8080`，再看后端控制台；用 `scripts\check-env.ps1` 一键确认 |
| 接口返回 `code=500` 且 `msg=数据库访问异常...` | MySQL 未启动、库表未初始化或字段不匹配；执行 `sql/pet_adopt.sql` 后重试，后端日志里有真实堆栈 |
| 接口返回 `code=500` 且 `msg=服务器开小差了` | 后端日志搜 `系统异常`，按堆栈定位 |
| 接口返回 `code=400` | 参数不合法（如 `page=abc`、必填项为空），按 `msg` 修正入参 |
| 接口返回 `code=404` | 资源不存在（如宠物已被删除）或接口路径写错 |
| 启动报 `Access denied for user 'root'@'localhost'` | 修改 `application.yml` 中的 `username/password`，或设置环境变量 `DB_USERNAME/DB_PASSWORD` |
| 启动报 `Unknown database 'pet_adopt'` | 先执行 `sql/pet_adopt.sql` |
| 启动报 `jwt.secret 长度必须 >= 32 字节` | `jwt.secret` 至少 32 个字符（或设置环境变量 `JWT_SECRET`） |
| 请求返回 401 | 确认请求头带了 `Authorization: Bearer <token>`，且 token 未过期（24 小时）；非法 token 也返回 401，不再变成 500 |
| 请求返回 403 | 该接口需要管理员 token（用 `admin/123456` 登录） |
| 浏览器报跨域 | 已放行 `http://localhost:*` 与 `http://127.0.0.1:*`；若用其它域名，改 `WebMvcConfig` 的 `allowedOriginPatterns` |
| 中文乱码 | 数据库使用 utf8mb4，导入时加 `--default-character-set=utf8mb4` |
| 8080 端口被占用 | 改 `application.yml` 的 `server.port`，或设置环境变量 `SERVER_PORT` |

---

## 八、安全说明（学习项目）

- 密码明文存储、JWT 密钥硬编码，仅用于教学演示，**不可直接用于生产环境**；
- 生产环境请使用 BCrypt 加密密码、把密钥放入环境变量、启用 HTTPS，并考虑加入 Redis 做 token 失效控制与接口限流。

