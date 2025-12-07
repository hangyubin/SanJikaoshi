# 智能考试系统

## 项目简介

智能考试系统是一个基于前后端分离架构的在线考试平台，支持用户注册登录、考试管理、题目管理、在线考试、成绩统计等功能。该系统采用现代化的技术栈，具有良好的可扩展性和可维护性。

## 技术栈

### 后端
- Spring Boot 2.7.18
- Spring Security
- Spring Data JPA
- MySQL 5.7+
- Java 8
- Maven 3.8.8

### 前端
- Vue 3
- TypeScript
- Element Plus
- Vite
- Vue Router
- Pinia

### 部署
- Docker
- Docker Compose
- GitHub Actions

## 项目结构

```
smart-exam-system/
├── .github/workflows/ # GitHub Actions工作流
├── server/           # 后端Spring Boot项目
├── client/           # 前端Vue 3 + TypeScript项目
├── docker-compose.yml # Docker Compose配置
├── README.md         # 项目说明文档
└── 宝塔面板部署指南    # 宝塔面板部署说明
```

## 功能特性

### 用户管理
- 用户注册、登录、注销
- 角色管理（管理员、普通用户）
- 用户信息管理

### 考试管理
- 考试创建、编辑、删除
- 考试题目管理
- 考试时间设置
- 考试状态管理

### 题目管理
- 题目创建、编辑、删除
- 支持多种题型（选择题、判断题、填空题、简答题等）
- 题目分类管理
- 题目导入导出

### 在线考试
- 考试页面倒计时
- 题目随机打乱
- 自动提交考试
- 实时保存答案

### 成绩统计
- 考试结果自动统计
- 成绩排名
- 错题分析
- 成绩导出

## 部署说明

### Docker部署（推荐）

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 停止服务
docker-compose down
```

### 手动部署

#### 后端部署

```bash
# 进入后端目录
cd server

# 构建jar包
mvn clean package -DskipTests

# 运行jar包
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

#### 前端部署

```bash
# 进入前端目录
cd client

# 安装依赖
npm install

# 构建静态文件
npm run build

# 部署到Nginx
# 将dist目录下的文件复制到Nginx的web目录
```

### 数据库配置

1. 创建数据库：`smart_exam`
2. 创建用户：`smart_exam_user`
3. 赋予权限：`GRANT ALL PRIVILEGES ON smart_exam.* TO 'smart_exam_user'@'localhost' IDENTIFIED BY 'your-password' WITH GRANT OPTION;`
4. 刷新权限：`FLUSH PRIVILEGES;`
5. 修改 `server/src/main/resources/application.properties` 中的数据库连接配置

## 使用说明

### 访问地址

- 前端：http://localhost:80
- 后端API：http://localhost:8080/api

### 初始账号

- **管理员账号**：admin
- **密码**：admin123

## 开发说明

### 后端开发

```bash
# 进入后端目录
cd server

# 运行开发服务器
mvn spring-boot:run

# 运行测试
mvn test
```

### 前端开发

```bash
# 进入前端目录
cd client

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产版本
npm run preview
```

## 贡献指南

1. Fork 本仓库
2. 创建特性分支：`git checkout -b feature/your-feature`
3. 提交更改：`git commit -m 'Add some feature'`
4. 推送到分支：`git push origin feature/your-feature`
5. 提交 Pull Request

## 许可证

MIT License

## 联系方式

如有问题或建议，请通过以下方式联系：

- 项目地址：https://github.com/hangyubin/sanjicms
- Issues：https://github.com/hangyubin/sanjicms/issues

## 更新日志

### v1.0.0 (2025-12-08)
- 初始版本发布
- 完成基本功能开发
- 支持Docker部署
- 支持GitHub Actions自动构建

---

**智能考试系统** - 专业、高效的在线考试平台
