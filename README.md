# 📼 Retro-SIMS | 微服务学生信息管理系统
<div align="center">

![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Cloud Alibaba](https://img.shields.io/badge/Spring_Cloud_Alibaba-2021.0.5-orange?style=for-the-badge&logo=spring&logoColor=white)
![Nacos](https://img.shields.io/badge/Nacos-Registry_%26_Config-blue?style=for-the-badge&logo=nacos&logoColor=white)
![Vue 3](https://img.shields.io/badge/Vue-3.0-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white)
![Style](https://img.shields.io/badge/Style-Atom_Punk_%7C_Synthwave-ff00ff?style=for-the-badge)

<p>🎹 <b>基于 Spring Cloud Alibaba 微服务架构与原子朋克 (Atom Punk) 复古美学的全栈实践</b> 🎹</p>

</div>

## 📖 项目简介

**Retro-SIMS** 是一个基于 **Spring Cloud Alibaba (Nacos) + Redis + Vue3** 的微服务架构教务管理系统。

本项目不仅仅是一个简单的 CRUD 演示，它将**硬核的微服务治理**与**复古未来主义 (Retro-Futurism)** 视觉风格完美融合，致敬 80-90 年代的科技幻想：

1.  **沉浸式复古 UI**：
    *   **登录页 (The Portal)**：复刻 **Motorola Fix Beeper** 寻呼机设计，悬浮于 3D 霓虹网格与原子光环构成的赛博空间中，拥有真实的物理按键反馈与悬浮动画。
    *   **主控台 (The Main Console)**：采用 **Cassette Futurism (磁带未来主义)** 风格，重构为左右分栏的**多功能数据控制台**。左侧模拟物理磁带插槽导航，右侧为 CRT 风格数据视窗，支持多模块无缝切换。
2.  **全栈微服务架构**：采用 **Gateway 网关 (8080) + 业务服务 (8082)** 的经典模式，集成 Nacos 实现服务注册与配置管理。
3.  **完整业务逻辑**：包含**学生、班级、课程、教师**四大核心模块，实现了模块间的**外键关联**与**数据联动**（如：添加课程时下拉选择教师，添加学生时下拉选择班级）。
4.  **健壮性设计**：包含 Redis 验证码校验、全局异常处理、CORS 跨域配置及数据自动清洗。

---

## 🏗️ 技术栈与架构

### 系统架构
```text
sims-project-root
├── gateway (8080)           # 网关服务：负载均衡、路由转发、跨域处理
├── Student_service (8082)   # 业务服务：核心 CRUD (Student/Class/Course/Teacher)、Redis 交互
└── sims-ui (前端)             # Vue3 + Vite + Element Plus (深度定制 Retro CSS)
```

### 核心技术
*   **后端**：Java 17, Spring Boot 2.7.18, Spring Cloud Alibaba 2021.0.5, Spring Data JPA
*   **中间件**：Nacos (注册/配置中心), Redis (缓存/验证码), MySQL 8.0
*   **前端**：Vue 3, Vite, Axios, Element Plus, Sass, Google Fonts (VT323/Courier New)

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
在 MySQL 中创建数据库 `sims_cloud`，并执行以下 SQL 脚本（包含完整的 4 张表结构）：

```sql
CREATE DATABASE IF NOT EXISTS `sims_cloud` DEFAULT CHARACTER SET utf8mb4;
USE `sims_cloud`;

-- 1. 班级表 (Class Info) - 被学生表关联
CREATE TABLE `class_info` (
  `Classno` char(3) NOT NULL COMMENT '班级号',
  `Major` varchar(20) NOT NULL COMMENT '专业名',
  PRIMARY KEY (`Classno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 学生表 (Student) - 关联班级表
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

-- 3. 教师表 (Teacher) - 被课程表关联
CREATE TABLE `teacher` (
  `tno` varchar(20) NOT NULL COMMENT '教师工号',
  `tname` varchar(50) NOT NULL COMMENT '教师姓名',
  `title` varchar(20) DEFAULT NULL COMMENT '职称',
  PRIMARY KEY (`tno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. 课程表 (Course) - 关联教师表
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

-- 5. 管理员表 (Sys User)
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

### 3. 中间件启动与配置 (关键步骤)

#### 🟢 步骤 A：启动 Nacos
在您的 Nacos 安装路径下打开 CMD，输入：
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
        # 👇 关键：必须添加 &allowPublicKeyRetrieval=true
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
4.  点击 **发布**。

#### 🔴 步骤 C：启动 Redis
确保本地 Redis 服务已启动。

### 4. 后端服务启动

1.  **启动 `Student_service` (8082)**:
    *   运行主启动类 `StudentsManageSysApplication`。
    *   **验证**：观察日志，确保成功连接 Nacos 和 MySQL。
2.  **启动 `gateway` (8080)**:
    *   运行主启动类 `GatewayApplication`。

### 5. 前端 UI 启动
进入 `sims-ui` 目录：

```bash
npm install
npm run dev
```
访问终端输出的地址（通常是 `http://localhost:5173`）。

---

## 🕹️ 界面与功能演示

### 1. 📟 寻呼机登录 (The Portal)
*   **视觉风格**：致敬 **Motorola Fix Beeper**，悬浮于原子朋克风格的深空网格之上。
*   **交互细节**：
    *   **物理反馈**：登录按钮具有机械按键的下压动画。
    *   **屏幕交互**：输入框模拟老式液晶屏显示，LED 指示灯在请求时闪烁。

### 2. 📼 主控制台 (Main Console)
*   **视觉风格**：**Retro-Futurism Poster (复古未来海报)** 风格。深蓝灰底色搭配高饱和度的明黄/暖橙色调，全屏叠加 SVG 噪点与 CRT 扫描线。
*   **模块化管理**：
    *   **👤 STUDENT_DB**：学生全字段管理。支持**班级下拉联动**（自动加载 Class DB 数据）。
    *   **🏫 CLASS_DB**：班级信息管理（增删改查）。
    *   **📚 COURSE_DATA**：课程信息管理。支持**教师下拉联动**（自动加载 Teacher Log 数据）。
    *   **👨‍🏫 TEACHER_LOG**：教师信息管理（工号、姓名、职称）。
*   **交互细节**：
    *   **Side Nav**：左侧导航栏模拟磁带插槽，点击切换模块时右侧视窗无缝刷新。
    *   **Data Viewport**：深度定制 Element Plus 表格，采用虚线分割与大写字母表头。
    *   **Retro Dialog**：带有警示条纹装饰的弹窗，拒绝扁平化，强调“执行(EXECUTE)”的仪式感。

---

## 🛠️ 踩坑指南与解决方案 (Troubleshooting)

| 问题分类 | 现象描述 | 解决方案 |
| :--- | :--- | :--- |
| **MySQL 连接** | **Public Key Retrieval is not allowed** | 在 Nacos 配置的 JDBC URL 后添加 `&allowPublicKeyRetrieval=true`。 |
| **数据关联** | **添加课程/学生时报错 (Foreign Key Constraint)** | 确保你选择的“班级”或“教师”在对应的数据库表中真实存在。前端已通过下拉框限制选择范围，但若手动调用 API 需注意外键约束。 |
| **JPA 映射** | **后端返回数据前端显示不全** | 涉及到多对一关联（如 Course -> Teacher），后端返回的是嵌套对象。前端需修改表格列绑定，例如从 `prop="teacher"` 改为模板插值 `{{ scope.row.teacher.tname }}`。 |
| **编译错误** | **Result.success() 报错** | 泛型方法参数匹配问题。修改 Controller 调用为 `Result.success(null)` 或在 Result 类中重载无参方法。 |
| **UI 样式** | **下拉框背景是白色的，很突兀** | Element Plus 的 Popper 挂载在 body 上。需在全局样式或未 scoped 的样式中覆盖 `.el-select-dropdown` 相关 CSS 变量，强制指定背景色为深色。 |

## 📄 许可证
本项目仅供学习交流使用。

---
*Created by [AndyXuPrime]*