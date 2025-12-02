# 📼 Retro-SIMS | 微服务学生信息管理系统

<div align="center">

![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Cloud Alibaba](https://img.shields.io/badge/Spring_Cloud_Alibaba-2021.0.5-orange?style=for-the-badge&logo=spring&logoColor=white)
![Nacos](https://img.shields.io/badge/Nacos-Registry_%26_Config-blue?style=for-the-badge&logo=nacos&logoColor=white)
![Vue 3](https://img.shields.io/badge/Vue-3.0-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white)
![Style](https://img.shields.io/badge/Style-Cyberpunk_Retro-ff00ff?style=for-the-badge)

<p>🎹 <b>基于 Spring Cloud Alibaba 微服务架构与 80 年代复古美学的全栈实践</b> 🎹</p>

</div>

## 📖 项目简介

**Retro-SIMS** 是一个基于 **Spring Cloud Alibaba (Nacos) + Redis + Vue3** 的微服务架构学生信息管理系统。

本项目不仅仅是一个简单的 CRUD 演示，它将**硬核的微服务治理**与**复古未来主义 (Retro-Futurism)** 视觉风格完美融合：
1.  **沉浸式复古 UI**：
    *   **登录页**：致敬 80 年代 **Sony Walkman** 磁带机设计，伴随机械按键与磁带转动动画。
    *   **管理页**：采用 **Synthwave (合成波)** 风格，模拟绿色荧光终端机与寻呼机 (Pager) 界面，配备 CRT 扫描线滤镜。
2.  **微服务架构**：采用 **Gateway 网关 (8080) + 业务服务 (8082)** 的经典模式，集成 Nacos 实现服务注册与发现。
3.  **数据联动**：实现了学生与班级数据的深度联动（下拉框动态加载）。
4.  **健壮性设计**：包含 Redis 验证码校验、全局异常处理、CORS 跨域配置及数据自动清洗。

---

## 🏗️ 技术栈与架构

### 系统架构
```text
sims-project-root
├── gateway (8080)           # 网关服务：负载均衡、路由转发、跨域处理
├── Student_service (8082)   # 业务服务：核心 CRUD、Redis 交互、Nacos 注册
└── sims-ui (前端)             # Vue3 + Vite + Element Plus (深度定制 CSS)
```

### 核心技术
*   **后端**：Java 17, Spring Boot 2.7.18, Spring Cloud Alibaba 2021.0.5
*   **中间件**：Nacos (注册/配置中心), Redis (缓存/验证码), MySQL 8.0
*   **前端**：Vue 3, Vite, Axios, Element Plus, Sass, Google Fonts (VT323)

---

## ⚡ 快速启动指南 (Windows环境)

### 1. 环境准备 (Prerequisites)
请确保本地已安装以下环境：
*   **JDK 1.8 或 17**
*   **MySQL 8.0+**
*   **Redis** (默认端口 6379)
*   **Nacos Server 2.x**
*   **Node.js & npm**

### 2. 数据库初始化 (Database Setup)
在 MySQL 中创建数据库 `sims_cloud`，并执行以下 SQL 脚本（包含最新的 Postcode 字段）：

```sql
CREATE DATABASE IF NOT EXISTS `sims_cloud` DEFAULT CHARACTER SET utf8mb4;
USE `sims_cloud`;

-- 1. 学生表
CREATE TABLE `student` (
  `Sno` char(8) NOT NULL COMMENT '学号',
  `Sname` varchar(8) DEFAULT NULL COMMENT '姓名',
  `Sex` char(2) DEFAULT NULL COMMENT '性别',
  `Birth` datetime DEFAULT NULL COMMENT '出生日期',
  `Classno` char(3) DEFAULT NULL COMMENT '班级号',
  `Entrance_date` datetime DEFAULT NULL COMMENT '入学时间',
  `Home_addr` varchar(40) DEFAULT NULL COMMENT '家庭住址',
  `Sdept` varchar(20) DEFAULT NULL COMMENT '所在系',
  `Postcode` char(6) DEFAULT NULL COMMENT '邮编',
  PRIMARY KEY (`Sno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 班级表
CREATE TABLE `class_info` (
  `Classno` char(3) NOT NULL,
  `Major` varchar(20) NOT NULL,
  PRIMARY KEY (`Classno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `class_info` VALUES ('051', '计算机科学与技术');
INSERT INTO `class_info` VALUES ('05Z', '计科（卓越计划）');
INSERT INTO `class_info` VALUES ('152', '软件工程');

-- 3. 管理员表
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `sys_user` (username, password) VALUES ('admin', '123456');
```

根据你最近遇到的 **MySQL 8.0 连接报错**（Public Key Retrieval）以及 **数据过长导致 500 错误**（姓名超长），我重新优化了运行步骤和踩坑指南。

请使用以下内容替换你 `README.md` 中的对应部分：

---

### 3. 中间件启动与配置 (关键步骤)

#### 🟢 步骤 A：启动 Nacos
在您的 Nacos 安装路径下（例如 `D:\nacos\nacos\bin`）打开 CMD，输入：
```cmd
startup.cmd -m standalone
```
*启动成功后访问：http://localhost:8848/nacos (默认账号密码: nacos/nacos)*

#### ⚠️ 步骤 B：配置 Nacos (必做！)
**为了防止 MySQL 8.0 连接报错，必须在 Nacos 控制台发布配置。**
1.  进入 **配置管理 -> 配置列表**。
2.  新建或编辑 Data ID: `studentmanage-test-dev.yaml` (Group: DEFAULT_GROUP)。
3.  **配置内容** (注意 `allowPublicKeyRetrieval=true`)：
    ```yaml
    spring:
      datasource:
        driver-class-name: com.mysql.cj.jdbc.Driver
        # 👇 关键：必须添加 &allowPublicKeyRetrieval=true，否则连接失败
        url: jdbc:mysql://localhost:3306/student_info_other?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true
        username: root
        password: YOUR_PASSWORD  # ⚠️ 修改为你的数据库密码
      redis:
        host: localhost
        port: 6379
        database: 0
      jpa:
        show-sql: true
        database-platform: org.hibernate.dialect.MySQL8Dialect
        hibernate:
          ddl-auto: update
    ```
4.  点击 **发布**。

#### 🔴 步骤 C：启动 Redis
确保本地 Redis 服务已启动。
*   **检查方法**：按 `Ctrl + Shift + Esc` 打开任务管理器，搜索 `redis-server` 进程。

### 4. 后端服务启动

1.  **启动 `Student_service` (8082)**:
*   IDEA 中运行主启动类 `StudentsManageSysApplication`,在service路径下运行：
```cmd
mvn spring-boot:run
```
* **验证**：观察控制台日志，确保没有 `Public Key Retrieval is not allowed` 报错，且成功加载 Nacos 配置。
2.  **启动 `gateway` (8080)**:
    在gateway路径下运行：
```cmd
mvn spring-boot:run
```
*   启动后，网关会自动从 Nacos 拉取服务列表。

### 5. 前端 UI 启动
进入 `sims-ui` 目录：

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev
```
访问终端输出的地址（通常是 `http://localhost:5173`）。

---

## 🕹️ 界面与功能演示

### 1. 📼 磁带机登录 (Walkman Login)
*   **视觉风格**：Sony Walkman 实体风格，手写标签式输入框。
*   **交互细节**：
    *   点击 **LCD 屏幕** 刷新 Redis 验证码。
    *   点击 **PLAY (▶)** 按钮提交登录。
    *   点击 **STOP (■)** 按钮重置表单。
    *   **REC/BATT** 指示灯在请求时会亮起红灯。

### 2. 📟 赛博终端管理 (Cyber Terminal)
*   **视觉风格**：黑底绿字荧光屏，配备 CRT 扫描线滤镜，VT323 像素字体。
*   **交互细节**：
    *   **查询**：命令行风格的搜索栏。
    *   **表格**：高对比度霓虹边框，包含像素风头像生成。
    *   **弹窗**：高亮荧光边框与深色遮罩，解决传统弹窗在深色模式下对比度不足的问题。
    *   **时钟**：右上角实时显示系统时间，模拟寻呼机状态栏。

---

## 🛠️ 踩坑指南与解决方案 (Troubleshooting)

在项目开发过程中遇到的典型问题及解决方案汇总：

| 问题分类 | 现象描述 | 解决方案 |
| :--- | :--- | :--- |
| **MySQL 连接** | **后端报错：Public Key Retrieval is not allowed** | MySQL 8.0+ 在 `useSSL=false` 且使用默认加密插件时，禁止获取公钥。**解决**：在 Nacos 配置的 JDBC URL 后添加 `&allowPublicKeyRetrieval=true`。 |
| **数据提交** | **前端报错 500 / 后端报错 Data truncation** <br> *(例如输入较长姓名时)* | 数据库字段长度不足（如 `varchar(8)`）。**解决**：1. 修改数据库表结构 `ALTER TABLE student_table MODIFY COLUMN Sname VARCHAR(20);` <br> 2. 同步放宽后端 Java 代码中的长度校验逻辑。 |
| **微服务网关** | **Gateway 报错 503/500** <br> *(Unable to find instance)* | 1. 确保 `Student_service` 已成功注册到 Nacos。<br>2. 检查 Gateway 的 `pom.xml` 是否引入了 `spring-cloud-starter-loadbalancer`。<br>3. 检查路由配置 `lb://` 后的服务名是否与 Nacos 中一致。 |
| **Vite 构建** | **[plugin:vite:vue] At least one template is required** | `App.vue` 文件为空导致。需在 `App.vue` 中添加 `<template><router-view/></template>` 作为路由出口。 |
| **UI 体验** | **页面加载前白屏闪烁** | 在 `index.html` 的 `<style>` 中设置 `body { background-color: #000; }`，并添加 `SYSTEM_INITIALIZING...` 的 Loading 动画。 |
| **视觉设计** | **弹窗内容看不清** | 针对 Element Plus 的 Dialog 进行深度 CSS 覆盖：加深遮罩层透明度，将输入框背景改为深墨绿，边框改为高亮荧光绿，并加粗文字权重。 |

## 📄 许可证
本项目仅供学习交流使用。

---
*Created by [AndyXuPrime]*
