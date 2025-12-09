# Docker镜像自动构建与部署指南

## 1. 概述

本指南介绍如何通过GitHub Actions自动构建和推送Docker镜像到GitHub Container Registry和Docker Hub，以及如何使用这些镜像部署智能考试系统。

## 2. GitHub Actions配置

### 2.1 启用GitHub Actions

1. 确保你的代码已经托管在GitHub上
2. 进入仓库的Settings页面
3. 在左侧菜单中选择"Actions" -> "General"
4. 确保"Allow all actions and reusable workflows"选项已选中
5. 点击"Save"按钮保存设置

### 2.2 配置GitHub Container Registry访问权限

GitHub Actions默认使用`GITHUB_TOKEN`，它已经有足够的权限来推送镜像到GitHub Container Registry，无需额外配置。

### 2.3 配置Docker Hub访问权限（可选）

如果你需要将镜像推送到Docker Hub，需要配置以下 secrets：

1. 进入仓库的Settings页面
2. 在左侧菜单中选择"Security" -> "Secrets and variables" -> "Actions"
3. 点击"New repository secret"按钮
4. 添加以下secrets：
   - `DOCKER_HUB_USERNAME`：你的Docker Hub用户名
   - `DOCKER_HUB_TOKEN`：你的Docker Hub访问令牌（从Docker Hub的Settings -> Security -> Access Tokens获取）

## 3. 自动构建流程

当你推送到`main`分支或合并PR到`main`分支时，GitHub Actions将自动执行以下操作：

1. 检出代码
2. 设置Docker Buildx
3. 登录到GitHub Container Registry
4. 登录到Docker Hub（如果配置了相关secrets）
5. 构建并推送完整的应用镜像
6. 构建并推送后端镜像（可选）
7. 构建并推送前端镜像（可选）
8. 输出构建结果

## 4. 镜像说明

### 4.1 完整应用镜像

- **GitHub Container Registry**：`ghcr.io/OWNER/REPOSITORY:latest` 或 `ghcr.io/OWNER/REPOSITORY:COMMIT_SHA`
- **Docker Hub**（可选）：`DOCKER_HUB_USERNAME/smart-exam-system:latest` 或 `DOCKER_HUB_USERNAME/smart-exam-system:COMMIT_SHA`

这个镜像包含了完整的应用，包括前端静态文件和后端服务，可以直接运行。

### 4.2 后端镜像

- **GitHub Container Registry**：`ghcr.io/OWNER/REPOSITORY/backend:latest` 或 `ghcr.io/OWNER/REPOSITORY/backend:COMMIT_SHA`
- **Docker Hub**（可选）：`DOCKER_HUB_USERNAME/smart-exam-system-backend:latest` 或 `DOCKER_HUB_USERNAME/smart-exam-system-backend:COMMIT_SHA`

这个镜像只包含后端服务，需要单独部署前端静态文件。

### 4.3 前端镜像

- **GitHub Container Registry**：`ghcr.io/OWNER/REPOSITORY/frontend:latest` 或 `ghcr.io/OWNER/REPOSITORY/frontend:COMMIT_SHA`
- **Docker Hub**（可选）：`DOCKER_HUB_USERNAME/smart-exam-system-frontend:latest` 或 `DOCKER_HUB_USERNAME/smart-exam-system-frontend:COMMIT_SHA`

这个镜像只包含前端静态文件，需要使用Nginx等Web服务器部署。

## 5. 使用Docker镜像部署

### 5.1 使用完整应用镜像

#### 5.1.1 直接运行

```bash
# 从GitHub Container Registry拉取镜像
docker pull ghcr.io/OWNER/REPOSITORY:latest

# 运行容器
docker run -d -p 8080:8080 --name smart-exam-system \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://mysql:3306/smart_exam?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai" \
  -e SPRING_DATASOURCE_USERNAME="smart_exam_user" \
  -e SPRING_DATASOURCE_PASSWORD="smart_exam_password" \
  ghcr.io/OWNER/REPOSITORY:latest
```

#### 5.1.2 使用Docker Compose

创建一个`docker-compose.yml`文件：

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:5.7
    container_name: smart-exam-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: smart_exam
      MYSQL_USER: smart_exam_user
      MYSQL_PASSWORD: smart_exam_password
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./mysql-init/init.sql:/docker-entrypoint-initdb.d/init.sql

  app:
    image: ghcr.io/OWNER/REPOSITORY:latest
    container_name: smart-exam-app
    restart: always
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: "jdbc:mysql://mysql:3306/smart_exam?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai"
      SPRING_DATASOURCE_USERNAME: smart_exam_user
      SPRING_DATASOURCE_PASSWORD: smart_exam_password
    ports:
      - "8080:8080"

volumes:
  mysql-data:
```

然后运行：

```bash
docker compose up -d
```

### 5.2 使用后端和前端镜像（分离部署）

创建一个`docker-compose.yml`文件：

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:5.7
    container_name: smart-exam-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: smart_exam
      MYSQL_USER: smart_exam_user
      MYSQL_PASSWORD: smart_exam_password
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./mysql-init/init.sql:/docker-entrypoint-initdb.d/init.sql

  backend:
    image: ghcr.io/OWNER/REPOSITORY/backend:latest
    container_name: smart-exam-backend
    restart: always
    depends_on:
      - mysql
    environment:
      SPRING_DATASOURCE_URL: "jdbc:mysql://mysql:3306/smart_exam?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai"
      SPRING_DATASOURCE_USERNAME: smart_exam_user
      SPRING_DATASOURCE_PASSWORD: smart_exam_password
    ports:
      - "8080:8080"

  frontend:
    image: ghcr.io/OWNER/REPOSITORY/frontend:latest
    container_name: smart-exam-frontend
    restart: always
    depends_on:
      - backend
    ports:
      - "80:80"
    environment:
      API_URL: "http://backend:8080/api"

volumes:
  mysql-data:
```

然后运行：

```bash
docker compose up -d
```

## 6. 访问应用

应用启动成功后，可以通过以下地址访问：

- **完整应用镜像**：http://localhost:8080
- **分离部署**：http://localhost

## 7. 获取镜像

### 7.1 从GitHub Container Registry获取

```bash
# 获取最新版本
docker pull ghcr.io/OWNER/REPOSITORY:latest

# 获取指定版本
docker pull ghcr.io/OWNER/REPOSITORY:COMMIT_SHA
```

### 7.2 从Docker Hub获取（如果配置了）

```bash
# 获取最新版本
docker pull DOCKER_HUB_USERNAME/smart-exam-system:latest

# 获取指定版本
docker pull DOCKER_HUB_USERNAME/smart-exam-system:COMMIT_SHA
```

## 8. 镜像标签说明

- `latest`：最新构建的镜像
- `COMMIT_SHA`：对应Git提交的SHA值，用于获取特定版本的镜像

## 9. 常见问题

### 9.1 镜像构建失败

- 检查GitHub Actions日志，查看具体错误信息
- 确保Dockerfile文件正确
- 确保依赖项配置正确

### 9.2 镜像推送失败

- 检查GitHub Actions日志，查看具体错误信息
- 确保GitHub Container Registry访问权限配置正确
- 确保Docker Hub secrets配置正确

### 9.3 应用启动失败

- 检查容器日志：`docker logs smart-exam-system`
- 确保数据库配置正确
- 确保数据库服务已经启动

## 10. 自定义构建

### 10.1 修改GitHub Actions配置

你可以根据需要修改`.github/workflows/docker-build.yml`文件，调整构建参数：

- 更改构建触发条件
- 添加或移除镜像标签
- 调整构建上下文
- 添加额外的构建步骤

### 10.2 修改Dockerfile

你可以根据需要修改`Dockerfile`文件，调整镜像构建过程：

- 更改基础镜像版本
- 调整构建步骤
- 修改应用启动命令
- 添加额外的依赖项

## 11. 版本更新

当你推送到`main`分支或合并PR到`main`分支时，GitHub Actions将自动构建并推送新版本的镜像，你可以通过以下方式更新应用：

```bash
# 拉取最新镜像
docker pull ghcr.io/OWNER/REPOSITORY:latest

# 停止并删除旧容器
docker stop smart-exam-system
docker rm smart-exam-system

# 运行新容器
docker run -d -p 8080:8080 --name smart-exam-system ghcr.io/OWNER/REPOSITORY:latest
```

或者使用Docker Compose：

```bash
docker compose pull
docker compose up -d
```

---

**智能考试系统** - 专业、高效的在线考试平台