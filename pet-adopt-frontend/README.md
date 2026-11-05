# 宠物领养系统前端（pet-adopt-frontend）

配套 `pet-adopt-backend` 的 Vue3 前端，前台（领养用户）与后台（管理员）一体。

## 技术栈

| 技术 | 版本 | 说明 |
|---|---|---|
| Vue | 3.5 | 全部使用 `<script setup>` 语法糖，纯 JavaScript |
| Vite | 6.x | 开发服务器 + 构建 |
| Vue Router | 4.x | 路由 + 全局守卫 |
| Pinia | 4.x | 登录态、宠物列表状态（手动持久化到 localStorage） |
| Element Plus | 2.14 | UI 组件库，中文语言包 |
| Axios | 1.x | 请求封装，统一处理 `{ code, data, msg }` |

- 开发端口：`http://localhost:5173`
- 请求基址：`/api`，由 Vite 代理转发到 `http://localhost:8080`（后端 context-path 就是 `/api`，因此**不做 rewrite**）
- 不使用 mock，全部直接对接后端接口

---

## 一、目录结构

```
pet-adopt-frontend
├── index.html
├── vite.config.js                 # @ 别名、5173 端口、/api 代理
├── public/favicon.svg
└── src
    ├── main.js                    # 注册 pinia / router / Element Plus（zh-cn）
    ├── App.vue
    ├── assets
    │   ├── images/placeholder.svg # 图片兜底占位
    │   └── styles/global.css      # 暖橙主题变量 + 通用样式
    ├── api
    │   ├── index.js               # 接口统一出口
    │   ├── pet.js                 # 宠物相关接口
    │   ├── user.js                # 登录、用户列表
    │   └── apply.js               # 领养申请、审核
    ├── components
    │   ├── FrontNav.vue           # 前台导航栏
    │   ├── AdminSidebar.vue       # 后台侧边菜单
    │   ├── PetCard.vue            # 宠物卡片
    │   └── Pagination.vue         # 通用分页
    ├── router/index.js            # 路由表 + 权限守卫
    ├── stores
    │   ├── index.js               # createPinia 单例
    │   ├── userStore.js           # token / userId / username / role
    │   └── petStore.js            # 前台宠物列表状态
    ├── utils
    │   ├── request.js             # axios 实例 + 双拦截器
    │   ├── auth.js                # localStorage 读写
    │   └── format.js              # 状态、类型、日期格式化
    └── views
        ├── userView               # 前台页面
        │   ├── FrontLayout.vue
        │   ├── Home.vue
        │   ├── PetList.vue
        │   ├── PetDetail.vue
        │   ├── Apply.vue
        │   ├── MyApply.vue
        │   ├── Login.vue
        │   └── About.vue
        ├── adminView              # 后台页面
            ├── AdminLayout.vue
            ├── AdminLogin.vue
            ├── PetManage.vue
            ├── ApplyManage.vue
            └── UserManage.vue
        └── error                  # 错误页
            └── NotFound.vue       # 404 占位页
```

---

## 二、启动步骤

> 前端所有数据都来自后端，请**先启动后端**（MySQL + Spring Boot 8080）。

```powershell
# 1. 启动 MySQL（本机为绿色版时）
D:\devc\MySQL\mysql-26.7.0-winx64\bin\mysqld.exe --standalone

# 2. 启动后端
cd d:\devcProgram\VSCode_Pro\pet_adopt\pet-adopt-backend
mvn spring-boot:run
# 或： java -jar target\pet-adopt-backend.jar

# 3. 启动前端
cd d:\devcProgram\VSCode_Pro\pet_adopt\pet-adopt-frontend
npm install
npm run dev
```

浏览器打开 `http://localhost:5173`。

### 演示账号（密码均为 123456）

| 角色 | 账号 | 入口 |
|---|---|---|
| 普通用户 | `zhangsan`、`lisi` | `/login` |
| 管理员 | `admin` | `/admin/login` |

---

## 三、页面与路由

### 前台

| 路由 | 页面 | 说明 |
|---|---|---|
| `/` | 首页 | 轮播式 Hero、待领养推荐、领养流程 |
| `/petList` | 宠物列表 | 按类型 / 性别 / 状态筛选 + 分页 |
| `/pet/:id` | 宠物详情 | 宠物信息、发起领养申请 |
| `/apply` | 领养申请 | 需登录；支持 `?petId=1` 带入宠物 |
| `/myApply` | 我的申请 | 需登录；按状态统计与展示审核意见 |
| `/login` | 用户登录 | 登录后回跳 `redirect` |
| `/about` | 领养须知 | 条件、流程、注意事项、FAQ |

### 后台（仅 `role=1` 管理员）

| 路由 | 页面 | 功能 |
|---|---|---|
| `/admin/login` | 管理员登录 | 非管理员账号会被拒绝 |
| `/admin/petManage` | 宠物管理 | 新增 / 编辑 / 标记已领养（下架）/ 恢复待领养 / 删除 |
| `/admin/applyManage` | 领养审核 | 状态 Tab 筛选、查看申请、通过 / 驳回 |
| `/admin/userManage` | 用户管理 | 关键字搜索 + 分页查看（后端无增删改接口） |

### 权限守卫规则

1. 未登录访问 `requiresAuth` 页面 → 跳 `/login`（后台页面跳 `/admin/login`）并带 `redirect`；
2. 普通用户访问 `/admin/**` → 提示「权限不足」并回首页；
3. 已登录再访问登录页 → 按角色回到 `/` 或 `/admin/petManage`；
4. 后端返回 `code=401` 时自动清除本地登录信息并跳转登录页。

---

## 四、接口对照

| 前端位置 | 接口 |
|---|---|
| 登录 / 后台登录 | `POST /api/user/login`、`POST /api/admin/login` |
| 宠物列表 / 详情 | `GET /api/pet/list`、`GET /api/pet/{id}` |
| 提交申请 / 我的申请 | `POST /api/apply/submit`、`GET /api/apply/my` |
| 后台宠物 | `GET /api/admin/pet/list`、`POST /api/admin/pet/save`、`DELETE /api/admin/pet/{id}` |
| 后台审核 | `GET /api/admin/apply/list`、`PUT /api/admin/apply/audit` |
| 后台用户 | `GET /api/admin/user/list` |

请求头：`Authorization: Bearer <token>`；响应 `{ code, data, msg }`，`code=200` 时拦截器直接返回 `data`。

> 关于「下架」：后端 `pet.status` 只有 `0 待领养 / 1 已领养`，没有独立的下架字段，因此后台用「标记已领养」表示下架，「恢复待领养」表示重新上架。

---

## 五、打包部署

```powershell
cd d:\devcProgram\VSCode_Pro\pet_adopt\pet-adopt-frontend
npm run build      # 产物输出到 dist/
npm run preview    # 本地预览构建结果（预览不会自动代理 /api）
```

### 方式 1：Nginx 托管 + 反向代理（推荐）

```nginx
server {
    listen       80;
    server_name  localhost;

    root   D:/devcProgram/VSCode_Pro/pet_adopt/pet-adopt-frontend/dist;
    index  index.html;

    # 前端 history 路由
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 把 /api 原样转发给后端（后端自带 /api 前缀）
    location /api/ {
        proxy_pass http://127.0.0.1:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

部署后访问 `http://localhost/`，接口走 `http://localhost/api/...`，同源无跨域问题。

### 方式 2：与后端分开部署（不同域名/端口）

1. 修改 `vite.config.js` 或部署环境的反向代理，把 `/api` 指向后端地址；
2. 若浏览器直连后端（不使用代理），需要后端 `WebMvcConfig` 的 `allowedOriginPatterns` 放行前端域名（当前只放行 `http://localhost:5173`、`http://127.0.0.1:5173`）。

---

## 六、常见问题

| 问题 | 解决办法 |
|---|---|
| 页面数据加载失败 / 提示网络异常 | 确认后端已在 `8080` 启动，且数据库已导入 |
| 登录后仍提示未登录 | 检查浏览器是否禁用 localStorage；清理 `pet_adopt_token` 后重试 |
| 访问后台被拦截 | 必须使用 `admin/123456` 登录，普通用户无法进入 |
| 图片显示「暂无图片」 | 后端演示图片来自 `picsum.photos`，需要联网；离线时自动使用本地占位图 |
| `npm install` 提示 esbuild install scripts 被拦截 | npm 12+ 的脚本安全策略（本机 npm 12.0.2 存在此提示）。若 `npm run dev` 报 esbuild 二进制错误：先 `npm install-scripts approve esbuild` 再 `npm install`，或直接 `npm rebuild esbuild`；二者都无需时说明二进制已就绪，可忽略该警告 |
| 5173 端口被占用 | 已开启 `strictPort`，Vite 会直接报错而不再自动换端口：先释放端口，或显式指定 `npm run dev -- --port 5174` |
| 刷新后台页面 404 | 生产环境需配置 `try_files $uri $uri/ /index.html` |

---

## 七、启动目录与白屏排查（重要）

### 1. 必须在 `pet-adopt-frontend` 目录启动

前端入口是 `pet-adopt-frontend/index.html`。**如果在仓库根目录 `pet_adopt` 直接 `npm run dev`，会启动一个没有 `index.html` 的 Vite 服务，浏览器访问 `http://localhost:5173/` 时 `GET /` 返回 404 空响应 —— 表现为整页白屏。**

```powershell
# 方式 A（推荐）：进入前端目录
cd d:\devcProgram\VSCode_Pro\pet_adopt\pet-adopt-frontend
npm install
npm run dev

# 方式 B：在仓库根目录用启动器脚本（内部转发到前端目录）
cd d:\devcProgram\VSCode_Pro\pet_adopt
npm run dev        # 等价于 npm --prefix pet-adopt-frontend run dev
```

仓库根目录的 `package.json` 只是启动器（`dev / build / preview / install:fe`），本身没有任何前端依赖，也不会生成白屏服务。
历史上根目录误建的 Node 工程已移动到 `pet_adopt/_stray-node-root/`，确认无用后可直接删除。

### 2. 白屏三步自查

| 现象 | 原因 | 处理 |
|---|---|---|
| 5173 纯白，F12 → Network 中 `GET /` 为 404 且 Response 为空 | 在错误目录启动了没有 `index.html` 的 Vite | 结束该服务，按方式 A/B 从正确目录重启 |
| 页面停在 🐾「正在加载，请稍候…」不动 | JS 执行报错，Vue 未挂载 | 打开 F12 → Console，查看 `[pet-adopt]` 开头的错误 |
| 终端提示 `Port 5173 is already in use` | 端口被占用（已开启 `strictPort`，不再自动换端口） | `netstat -ano \| findstr :5173` 查 PID，再 `taskkill /PID <pid> /F` |
| 大量 `504 (Outdated Optimize Dep)` / 模块加载失败 | 依赖不完整或改过 `node_modules` | 重新 `npm install` 后重启 dev |

### 3. 已内置的兜底能力

- `index.html`：`#app` 内首屏 loading 占位 + `<noscript>` 提示，JS 未执行时不再是纯白；
- `src/main.js`：`app.config.errorHandler` + `unhandledrejection` 统一打印错误；
- `src/stores/index.js`：`createPinia()` 单例，`main.js` 先装 pinia 再装 router（守卫里才能取 store）；
- `vite.config.js`：`server.strictPort = true`，端口占用直接报错；
- 路由兜底：未知路径渲染 `views/error/NotFound.vue` 404 占位页；
- `utils/request.js`：`baseURL` 支持 `VITE_API_BASE`（默认 `/api`，见 `.env.example`）。

---

## 八、说明

- 本项目为学习演示，密码明文传输/存储，**不可直接用于生产**；
- 未实现注册、找回密码、图片上传：宠物图片使用 URL 地址填写；
- 未引入 TypeScript、Tailwind、ESLint/Prettier，保持依赖与配置精简。
