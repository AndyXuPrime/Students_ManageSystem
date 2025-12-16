# 📼 Retro-SIMS | 微服务学生信息管理系统

<div align="center">

![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Cloud Alibaba](https://img.shields.io/badge/Spring_Cloud_Alibaba-2021.0.5-orange?style=for-the-badge&logo=spring&logoColor=white)
![MinIO](https://img.shields.io/badge/MinIO-Object_Storage-blueviolet?style=for-the-badge&logo=minio&logoColor=white)
![Vue 3](https://img.shields.io/badge/Vue-3.0-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white)
![Style](https://img.shields.io/badge/Style-Atom_Punk_%7C_Synthwave-ff00ff?style=for-the-badge)

<p>🎹 <b>基于 Spring Cloud Alibaba 微服务架构与原子朋克 (Atom Punk) 复古美学的全栈实践</b> 🎹</p>

</div>

## 📖 项目简介

**Retro-SIMS** 是一个基于 **Spring Cloud Alibaba (Nacos) + Redis + MinIO + Vue3** 的微服务架构教务管理系统。

本项目不仅仅是一个简单的 CRUD 演示，它将**硬核的微服务治理**与**复古未来主义 (Retro-Futurism)** 视觉风格完美融合，致敬 80-90 年代的科技幻想：

1.  **沉浸式复古 UI**：
    *   **登录页 (The Portal)**：复刻 **Motorola Fix Beeper** 寻呼机设计，悬浮于 3D 霓虹网格与原子光环构成的赛博空间中。
    *   **主控台 (The Main Console)**：采用 **Cassette Futurism (磁带未来主义)** 风格，重构为左右分栏的**多功能数据控制台**。左侧模拟物理磁带插槽导航，右侧为 CRT 风格数据视窗，支持多模块无缝切换。
2.  **全栈微服务架构**：采用 **Gateway (网关) + User_service (核心业务) + Resource_service (资源服务)** 的经典模式，集成 Nacos 实现服务治理。
3.  **完整业务逻辑**：包含**学生、班级、课程、教师**四大核心模块，并新增**课程资源管理**，支持课程附件的上传、下载、删除及**文件名模糊搜索**。
4.  **数据联动**：实现了学生-班级、课程-教师的数据联动。

---

## 🏗️ 技术栈与架构

### 系统架构
```text
sims-project-root
├── gateway (8080)           # 网关服务：路由转发、跨域处理
├── User_service (8082)      # 业务服务：核心 CRUD (Student/Class/Course/Teacher)
├── Resource_service (8083)  # 资源服务：对接 MinIO，负责文件上传、下载、元数据存储与搜索
└── UI (前端)                  # Vue3 + Vite + Element Plus (深度定制 Retro CSS)
```

### 核心技术
*   **后端**：Java 17, Spring Boot 2.7.18, Spring Cloud Alibaba 2021.0.5, Spring Data JPA
*   **中间件**：Nacos, Redis, MySQL 8.0, **MinIO**
*   **前端**：Vue 3, Vite, Axios, Element Plus

---

## ⚡ 快速启动指南 (Windows环境)

### 1. 环境准备 (Prerequisites)
*   **JDK 1.8 或 17**
*   **MySQL 8.0+**
*   **Redis**
*   **Nacos Server 2.x**
*   **MinIO Server**
*   **Node.js & npm**

### 2. 数据库初始化 (Database Setup)
在 MySQL 中创建数据库 `sims_cloud`，并执行以下 SQL 脚本：

```sql
CREATE DATABASE IF NOT EXISTS `sims_cloud` DEFAULT CHARACTER SET utf8mb4;
USE `sims_cloud`;

-- 1. 班级表 (Class Info)
CREATE TABLE `class_info` (
  `Classno` char(3) NOT NULL COMMENT '班级号',
  `Major` varchar(20) NOT NULL COMMENT '专业名',
  PRIMARY KEY (`Classno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 学生表 (Student)
CREATE TABLE `student` (
  `Sno` char(8) NOT NULL COMMENT '学号',
  `Sname` varchar(20) DEFAULT NULL COMMENT '姓名',
  `Sex` char(2) DEFAULT NULL COMMENT '性别',
  `Birth` datetime DEFAULT NULL COMMENT '出生日期',
  `Classno` char(3) DEFAULT NULL COMMENT '班级号 (FK)',
  `Entrance_date` datetime DEFAULT NULL COMMENT '入学时间',
  `Home_addr` varchar(40) DEFAULT NULL COMMENT '家庭住址',
  `Sdept` varchar(20) DEFAULT NULL COMMENT '所在系',
  `Postcode` char(6) DEFAULT NULL COMMENT '邮编',
  PRIMARY KEY (`Sno`),
  CONSTRAINT `fk_student_class` FOREIGN KEY (`Classno`) REFERENCES `class_info` (`Classno`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. 教师表 (Teacher)
CREATE TABLE `teacher` (
  `tno` varchar(20) NOT NULL COMMENT '教师工号',
  `tname` varchar(50) NOT NULL COMMENT '教师姓名',
  `title` varchar(20) DEFAULT NULL COMMENT '职称',
  PRIMARY KEY (`tno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 课程表 (Course)
CREATE TABLE `course` (
  `cno` varchar(20) NOT NULL COMMENT '课程号',
  `cname` varchar(100) NOT NULL COMMENT '课程名',
  `credit` int DEFAULT NULL COMMENT '学分',
  `period` int DEFAULT NULL COMMENT '学时',
  `type` varchar(20) DEFAULT NULL COMMENT '类型',
  `tno` varchar(20) DEFAULT NULL COMMENT '任课教师 (FK)',
  PRIMARY KEY (`cno`),
  CONSTRAINT `fk_course_teacher` FOREIGN KEY (`tno`) REFERENCES `teacher` (`tno`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. 课程资源表 (Course Resource)
CREATE TABLE `course_resource` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) NOT NULL COMMENT '文件名',
  `file_type` varchar(20) DEFAULT NULL COMMENT '文件类型 (pdf, docx)',
  `file_url` varchar(500) NOT NULL COMMENT 'MinIO下载地址',
  `course_name` varchar(100) DEFAULT NULL COMMENT '所属课程',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小 (字节)',
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. 管理员表 (Sys User)
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始化数据
INSERT INTO `sys_user` (username, password) VALUES ('admin', '123456');
INSERT INTO `class_info` VALUES ('051', '计算机科学与技术'), ('05Z', '计科(卓越)'), ('152', '软件工程');
INSERT INTO `teacher` VALUES ('T001', 'Dr. Sbaitso', '教授'), ('T002', 'Kavinsky', '副教授');
```

### 3. 中间件启动与配置

#### 🟢 步骤 A：启动 Nacos
```cmd
startup.cmd -m standalone
```
*访问：http://localhost:8848/nacos*

#### 🔴 步骤 B：启动 Redis
确保本地 Redis 服务已启动。

#### 🔵 步骤 C：启动 MinIO (命令行)
1.  下载 `minio.exe` 和 `mc.exe` (MinIO Client)。
2.  打开 CMD，运行以下命令启动服务器：
    ```cmd
    minio.exe server D:\minio_data --console-address ":9090"
    ```
    *   `D:\minio_data` 是你的数据存储目录，可自行修改。
    *   **保持此窗口不要关闭**。
3.  **另外打开一个 CMD**，配置权限：
    ```cmd
    mc mb local/studentmanagement
    mc anonymous set download local/studentmanagement
    ```
    *   `mb` = make bucket (创建桶)，`anonymous set download` = 设置为公开可读。

#### ⚠️ 步骤 D：配置 Nacos
1.  在 Nacos 新建/编辑 `user-service.yaml` (Group: DEFAULT_GROUP)：
    ```yaml
    spring:
      datasource:
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://localhost:3306/sims_cloud?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true
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
2.  在 Nacos 新建配置 `resource-service.yaml` (Group: DEFAULT_GROUP)：
    ```yaml
    spring:
      datasource:
        driver-class-name: com.mysql.cj.jdbc.Driver
        url: jdbc:mysql://localhost:3306/sims_cloud?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true
        username: root
        password: YOUR_PASSWORD  # ⚠️ 修改为你的数据库密码
      jpa:
        hibernate:
          ddl-auto: update
    minio:
      endpoint: http://localhost:9000
      accessKey: minioadmin
      secretKey: minioadmin
      bucketName: studentmanagement
    ```

### 4. 后端服务启动
1.  启动 `User_service` (8082)。
2.  启动 `Resource_service` (8083)。
3.  启动 `gateway` (8080)。

### 5. 前端 UI 启动
```bash
npm install && npm run dev
```

---

## 🕹️ 界面与功能演示

### 1. 📟 寻呼机登录 (The Portal)
*   **视觉风格**：致敬 **Motorola Fix Beeper**，悬浮于原子朋克风格的深空网格之上。

### 2. 📼 主控制台 (Main Console)
*   **视觉风格**：**Retro-Futurism Poster (复古未来海报)** 风格，左右分栏布局。
*   **模块化管理**：
    *   **👤 STUDENT_DB**：学生全字段管理。支持**班级下拉联动**。
    *   **🏫 CLASS_DB**：班级信息管理（增删改查）。
    *   **📚 COURSE_DATA**：课程信息管理。支持**教师下拉联动**。
    *   **👨‍🏫 TEACHER_LOG**：教师信息管理（工号、姓名、职称）。
*   **课程资源编辑器**：
    *   在“编辑课程”弹窗中，新增 **RESOURCES (资源)** 标签页。
    *   **上传**：支持上传课程附件 (Word/PDF/PPT) 到 MinIO。
    *   **下载/删除**：提供文件列表，支持一键下载或删除。
    *   **搜索**：文件列表数据通过**文件名**与当前课程关联，实现了课程附件的搜索功能。

---

## 🛠️ 踩坑指南与解决方案 (Troubleshooting)

| 问题分类 | 现象描述 | 解决方案 |
| :--- | :--- | :--- |
| **MinIO 权限** | **下载文件时返回 `Access Denied` XML 错误** | 新版 MinIO 需使用命令行工具 `mc` 设置 Bucket 权限。执行 `mc anonymous set download local/your-bucket`。 |
| **Gateway 404** | **访问 `/api/file/upload` 报 404** | Gateway 路由匹配顺序问题。**解决**：将更具体的路由（如 `/api/file/**`）放在更通用的路由（`/api/**`）**之前**。 |
| **MySQL 连接** | **Public Key Retrieval is not allowed** | 在 Nacos 配置的 JDBC URL 后添加 `&allowPublicKeyRetrieval=true`。 |
| **数据关联** | **添加课程/学生时报错 (Foreign Key Constraint)** | 确保你选择的“班级”或“教师”在对应的数据库表中真实存在。 |
| **JPA 映射** | **后端返回数据前端显示不全** | 涉及到多对一关联（如 Course -> Teacher），后端返回的是嵌套对象。前端需修改表格列绑定，例如从 `prop="teacher"` 改为模板插值 `{{ scope.row.teacher.tname }}`。 |
| **编译错误** | **Result.success() 报错** | 泛型方法参数匹配问题。修改 Controller 调用为 `Result.success(null)` 或在 Result 类中重载无参方法。 |

## 📄 许可证
本项目仅供学习交流使用。

---
*Created by [AndyXuPrime]*
