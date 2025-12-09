# 智能考试系统 - 本地部署指南

## 1. 环境准备

在开始部署之前，请确保您的计算机上已经安装了以下软件：

### 1.1 必要软件

- **Docker**：用于容器化部署
  - 下载地址：[Docker官网](https://www.docker.com/get-started)
  - 安装完成后，请确保Docker服务已经启动

- **Docker Compose**：用于编排多个Docker容器
  - 大多数Docker安装包已经包含了Docker Compose
  - 验证安装：`docker-compose --version`

### 1.2 可选软件（用于开发）

如果您需要进行本地开发，可以安装以下软件：

- **Node.js** (v18+): 用于前端开发
  - 下载地址：[Node.js官网](https://nodejs.org/)
  - 验证安装：`node --version` 和 `npm --version`

- **Maven** (v3.8+): 用于后端开发
  - 下载地址：[Maven官网](https://maven.apache.org/)
  - 验证安装：`mvn --version`

- **Java JDK** (v8+): 用于后端开发
  - 下载地址：[Oracle JDK](https://www.oracle.com/java/technologies/downloads/) 或 [OpenJDK](https://adoptium.net/)
  - 验证安装：`java -version` 和 `javac -version`

## 2. 项目结构

确保您已经获取了完整的项目代码，项目结构如下：

```
smart-exam-system/
├── server/           # 后端Spring Boot项目
├── client/           # 前端Vue 3 + TypeScript项目
├── docker-compose.yml # Docker Compose配置
├── Dockerfile        # 前后端合一的Docker构建文件
├── README.md         # 项目说明文档
└── LOCAL_DEPLOYMENT.md # 本地部署指南（本文件）
```

## 3. 本地部署方法

### 3.1 方法一：使用一键部署脚本（推荐）

我们提供了一个一键部署脚本，简化了整个部署过程。

#### 步骤1：进入项目根目录

打开终端，进入项目根目录：

```bash
cd /path/to/smart-exam-system
```

#### 步骤2：赋予脚本执行权限

```bash
chmod +x start.sh
```

#### 步骤3：执行部署脚本

```bash
./start.sh
```

这个脚本会自动：
- 检查Docker和Docker Compose是否安装
- 拉取最新的Docker镜像
- 停止并删除旧的容器（如果存在）
- 启动所有服务
- 显示服务状态

#### 步骤4：访问应用

服务启动成功后，您可以通过以下地址访问应用：

- **应用访问地址**：http://localhost:8080

### 3.2 方法二：手动使用Docker Compose

如果您希望手动控制部署过程，可以使用以下方法：

#### 步骤1：进入项目根目录

打开终端，进入项目根目录：

```bash
cd /path/to/smart-exam-system
```

#### 步骤2：拉取最新镜像

```bash
docker-compose pull
```

#### 步骤3：启动服务

执行以下命令，启动所有服务：

```bash
docker-compose up -d
```

这个命令会：
- 拉取MySQL 5.7镜像
- 拉取预构建的应用镜像（ghcr.io/hangyubin/sanjikaoshi:latest）
- 创建并启动两个容器：
  - `smart-exam-mysql`：MySQL数据库服务
  - `smart-exam-app`：应用服务（包含前端和后端）

#### 步骤4：查看服务状态

执行以下命令，查看服务是否正常启动：

```bash
docker-compose ps
```

您应该看到类似以下输出：

```
NAME                COMMAND                  SERVICE             STATUS              PORTS
smart-exam-app      "java -jar /app/app.j…"   app                 running             0.0.0.0:8080->8080/tcp
smart-exam-mysql    "docker-entrypoint.s…"   mysql               running             0.0.0.0:3306->3306/tcp
```

#### 步骤5：访问应用

服务启动成功后，您可以通过以下地址访问应用：

- **应用访问地址**：http://localhost:8080

### 3.3 方法三：手动部署（分步骤）

如果您需要更精细地控制部署过程，可以选择手动部署。

#### 3.3.1 部署数据库

1. **启动MySQL容器**：

```bash
docker run -d \
  --name smart-exam-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=smart_exam \
  -e MYSQL_USER=smart_exam_user \
  -e MYSQL_PASSWORD=smart_exam_password \
  -p 3306:3306 \
  mysql:5.7
```

2. **验证数据库连接**：

您可以使用MySQL客户端工具（如Navicat、DBeaver或命令行）连接数据库，验证是否正常运行：

```bash
mysql -h localhost -P 3306 -u smart_exam_user -p
```

输入密码 `smart_exam_password` 后，如果能成功连接，则数据库部署成功。

#### 3.3.2 部署后端服务

1. **进入后端目录**：

```bash
cd server
```

2. **构建后端服务**：

```bash
mvn clean package -DskipTests
```

3. **运行后端服务**：

```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

后端服务将在 `http://localhost:8080` 上运行。

#### 3.3.3 部署前端服务

1. **进入前端目录**：

```bash
cd client
```

2. **安装依赖**：

```bash
npm install
```

3. **构建前端项目**：

```bash
npm run build
```

4. **部署前端静态文件**：

将构建生成的 `dist` 目录下的文件复制到后端服务的 `static` 目录下，或者使用Nginx等Web服务器部署。

## 4. 应用配置

### 4.1 数据库配置

默认数据库配置如下：

- **数据库地址**：localhost:3306
- **数据库名称**：smart_exam
- **用户名**：smart_exam_user
- **密码**：smart_exam_password
- **ROOT密码**：root

### 4.2 应用配置

如果需要修改应用配置，可以编辑 `server/src/main/resources/application.properties` 文件。

## 5. 访问应用

### 5.1 应用访问地址

服务启动成功后，您可以通过以下地址访问应用：

- **应用访问地址**：http://localhost:8080

### 5.2 初始账号

系统默认提供以下初始账号：

- **管理员账号**：admin
- **密码**：admin123

## 6. 服务管理

### 6.1 启动服务

```bash
docker-compose up -d
```

### 6.2 停止服务

```bash
docker-compose down
```

### 6.3 重启服务

```bash
docker-compose restart
```

### 6.4 查看日志

**查看所有服务日志**：

```bash
docker-compose logs
```

**查看特定服务日志**：

```bash
docker-compose logs app
```

**实时查看日志**：

```bash
docker-compose logs -f app
```

### 6.5 进入容器

```bash
docker exec -it smart-exam-app /bin/sh
```

## 7. 常见问题及解决方案

### 7.1 端口被占用

如果出现端口被占用的错误，可以修改 `docker-compose.yml` 文件中的端口映射：

```yaml
# 将宿主机的8080端口映射到容器的8080端口
# 可以修改宿主机端口，例如：8081:8080
ports:
  - "8080:8080"
```

### 7.2 数据库连接失败

检查数据库配置是否正确，包括：
- 数据库地址
- 数据库名称
- 用户名和密码

### 7.3 服务启动缓慢

首次启动时，需要拉取镜像和构建应用，可能会比较缓慢，请耐心等待。

### 7.4 前端页面无法访问

检查：
- 后端服务是否正常运行
- 前端静态文件是否正确部署
- 浏览器控制台是否有错误信息

## 8. 开发模式

### 8.1 后端开发模式

进入后端目录，运行：

```bash
mvn spring-boot:run
```

### 8.2 前端开发模式

进入前端目录，运行：

```bash
npm run dev
```

前端开发服务器将在 `http://localhost:5173` 上运行。

## 9. 项目结构说明

### 9.1 后端结构

```
server/
├── src/                 # 源代码目录
│   └── main/           # 主代码目录
│       ├── java/       # Java源代码
│       │   └── com/    # 包结构
│       └── resources/  # 资源文件
│           └── application.properties  # 应用配置文件
├── Dockerfile          # Docker构建文件
└── pom.xml             # Maven配置文件
```

### 9.2 前端结构

```
client/
├── src/                # 源代码目录
│   ├── assets/         # 静态资源
│   ├── components/     # Vue组件
│   ├── layout/         # 布局组件
│   ├── router/         # 路由配置
│   ├── store/          # 状态管理
│   ├── utils/          # 工具函数
│   ├── views/          # 页面组件
│   ├── App.vue         # 根组件
│   └── main.ts         # 入口文件
├── Dockerfile          # Docker构建文件
└── package.json        # npm配置文件
```

## 10. 技术支持

如果您在部署过程中遇到问题，可以：

1. 查看服务日志，定位问题
2. 检查配置文件是否正确
3. 确保所有依赖软件都已正确安装
4. 尝试重新构建和启动服务

---

**智能考试系统** - 专业、高效的在线考试平台
**本地部署指南** - v1.0.0
