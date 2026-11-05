# 宠物领养系统（Vue3 + SpringBoot）
> 前后端分离宠物领养平台，支持用户浏览宠物、提交领养申请，管理员对宠物、申请、用户进行管理。

## 📌 核心功能

- ✅ 用户模块：注册、登录、退出登录
- ✅ 宠物模块：宠物列表、宠物详情、宠物检索
- ✅ 领养模块：提交领养申请、查看申请记录
- ✅ 管理员模块：宠物管理、领养申请审核、用户管理

## ✨ 项目介绍
本项目是一个面向爱心人士的线上宠物领养平台：
- 用户：注册登录、查看宠物详情、提交领养申请、查看我的申请、退出登录
- 管理员：宠物信息增删改查、领养申请审核、用户管理
- 后端提供完整 RESTful 接口，集成 Knife4j 在线接口文档

## 🛠️ 技术栈
### 前端 pet-adopt-frontend
- Vue 3 + Vite
- Element Plus
- Pinia
- Vue Router
- Axios

### 后端 pet-adopt-backend
- SpringBoot 3
- MyBatis-Plus
- MySQL 8.0
- Knife4j
- Maven

## 📂 目录结构
- ue3-springboot-pet_adopt/
- ├── pet-adopt-frontend    # Vue3 前端项目
- ├── pet-adopt-backend     # SpringBoot 后端项目
- ├── _stray-node-root      # 临时目录
- └── package.json

## 📋 环境要求
- Node.js >= 18
- JDK >= 17
- MySQL >= 8.0
- Maven 3.8+

## 🚀 启动教程
### 启动后端
```bash
cd pet-adopt-backend
mvn clean package -DskipTests
mvn spring-boot:run
```

### 启动前端
```bash
cd d:\devcProgram\VSCode_Pro\pet_adopt\pet-adopt-frontend
npm install
npm run dev
```
