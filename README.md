# 📼 Retro-SIMS | 微服务学生信息管理系统

<div align="center">

![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Cloud Alibaba](https://img.shields.io/badge/Spring_Cloud_Alibaba-2021.0.5-orange?style=for-the-badge&logo=spring&logoColor=white)
![Nacos](https://img.shields.io/badge/Nacos-Registry_%26_Config-blue?style=for-the-badge&logo=nacos&logoColor=white)
![Vue 3](https://img.shields.io/badge/Vue-3.0-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white)
![Style](https://img.shields.io/badge/Style-Retro_80s-ff00ff?style=for-the-badge)

<p>🎹 <b>基于 Spring Cloud Alibaba 微服务架构与 80 年代复古美学的全栈实践</b> 🎹</p>

</div>

## 📖 项目简介

**Retro-SIMS** 是一个基于 **Spring Cloud Alibaba (Nacos) + Redis + Vue3** 的微服务架构学生信息管理系统。

本项目不仅仅是一个简单的 CRUD 演示，它具有以下特色：
1.  **复古 UI 设计**：登录页致敬 80 年代风格，管理页采用 Synthwave（合成波）终端风格。
2.  **微服务架构**：采用 **Gateway 网关 + 业务服务** 的经典模式，集成 Nacos 实现服务注册与发现。
3.  **数据联动**：实现了学生与班级数据的深度联动（下拉框动态加载）。
4.  **健壮性设计**：包含 Redis 验证码校验、全局异常处理、CORS 跨域配置及数据自动清洗。

---

## 🏗️ 技术栈与架构

### 系统架构
```text
sims-project-root
├── gateway (8080)   # 网关服务：负载均衡、路由转发、跨域处理
├── Student_service (8082)   # 业务服务：核心 CRUD、Redis 交互、Nacos 注册
└── sims-ui (前端)         # Vue3 + Vite + Element Plus (Retro Style)
```

### 核心技术
*   **后端**：Java 17, Spring Boot 2.7.18, Spring Cloud Alibaba 2021.0.5
*   **中间件**：Nacos (注册/配置中心), Redis (缓存/验证码), MySQL 8.0
*   **前端**：Vue 3, Vite, Axios, Element Plus, Sass
*   **ORM**：Spring Data JPA / Mybatis Plus (根据具体实现调整)

---

## ⚡ 快速开始 (完美复刻步骤)

### 1. 环境准备 (Prerequisites)
请确保本地已安装以下环境：
*   **JDK 1.8 或 17**
*   **MySQL 8.0+**
*   **Redis** (默认端口 6379)
*   **Nacos Server 2.x**
*   **Node.js & npm**

### 2. 数据库初始化 (Database Setup)
在 MySQL 中创建数据库 `sims_cloud`，并执行以下 SQL 脚本：

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

### 3. 中间件启动

#### 启动 Redis
确保 Redis 服务已运行。

#### 启动 Nacos
进入 Nacos 的 `bin` 目录，执行启动命令：
*   **Windows/Mac/Linux**:
    ```bash
    startup.cmd -m standalone
    # 或者
    sh startup.sh -m standalone
    ```
*   访问 `http://localhost:8848/nacos` (默认账号密码: `nacos`/`nacos`)。

### 4. 后端配置与启动

#### A. 配置 Nacos (可选，推荐)
虽然项目支持本地 `bootstrap.yml` 配置，但建议在 Nacos 中发布配置以体验微服务特性：
1.  **Data ID**: `sims-service-dev.yaml`
2.  **Group**: `DEFAULT_GROUP`
3.  **内容**:
    ```yaml
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/sims_cloud?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
        username: root
        password: YOUR_PASSWORD # ⚠️ 修改为你的数据库密码
      redis:
        host: localhost
        port: 6379
        database: 0
    ```

#### B. 启动服务
1.  **启动 `sims-service`**: 
    *   修改 `bootstrap.yml` 中的 Nacos 地址（如果不是 localhost）。
    *   运行主启动类。
    *   *注意：服务名需为 `sims-service` 或 `studentmanage-test` (需与网关路由一致)。*
2.  **启动 `sims-gateway`**:
    *   确保端口为 `8080`。
    *   启动后，网关会自动从 Nacos 拉取服务列表。

### 5. 前端启动
进入 `sims-ui` 目录：

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev
```
访问终端输出的地址（通常是 `http://localhost:5173`）。

---

## 🕹️ 功能演示

### 1. 复古登录 (Login)
*   **风格**：Sony Walkman 磁带机元素。
*   **操作**：输入 `admin` / `123456`，点击验证码区域刷新图片（Redis 缓存），点击 "PLAY/LOGIN" 按钮登录。

### 2. 终端管理 (Dashboard)
*   **风格**：Synthwave 霓虹终端。
*   **操作**：
    *   **查询**：支持按姓名模糊搜索。
    *   **新增**：点击 "INSERT"，班级下拉框会自动从数据库加载选项。
    *   **修改/删除**：实时同步数据库。

---

## 🛠️ 常见问题与解决方案 (Troubleshooting)

如果在运行过程中遇到问题，请参考以下实战经验总结：

| 问题现象 | 可能原因 | 解决方案 |
| :--- | :--- | :--- |
| **Gateway 报错 503/500** | 找不到服务实例 | 1. 确保后端服务已成功注册到 Nacos。<br>2. 检查网关 `pom.xml` 是否引入了 `spring-cloud-starter-loadbalancer`。<br>3. 检查路由配置 `lb://服务名` 是否拼写正确。 |
| **Gateway 报错 Invalid host** | 服务名含下划线 | Spring Cloud 规范建议服务名使用中划线（如 `sims-service`），不要使用下划线（`sims_service`）。 |
| **数据库报错 Data truncation** | 字段超长 | 检查前端输入框，添加 `maxlength` 属性（例如学号限制8位，班级号限制3位）。 |
| **数据库报错 Non-null constraint** | 必填项为空 | 后端 Service 层增加了增强逻辑：如果前端未传 `Entrance_date`，后端自动填充当前时间；未传 `Sdept`，填充默认院系。 |
| **日期显示异常** | 格式化问题 | 实体类日期字段需添加注解：`@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")`。 |
| **前后端跨域 (CORS)** | 网关未配置 | 在 Gateway 的配置类中添加全局 CORS 过滤器，允许 `AllowedOrigins: *`。 |

---

## 📄 许可证
本项目仅供学习交流使用。

---
*Created by [AndyXuPrime]*
