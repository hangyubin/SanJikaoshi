# GitHub Releases Docker镜像下载与使用指南

## 1. 概述

本指南介绍如何通过GitHub Releases下载Docker镜像，并使用这些镜像部署智能考试系统。

## 2. 自动发布流程

当你在GitHub上创建并发布一个Release时，GitHub Actions会自动执行以下操作：

1. 检出代码
2. 设置Docker Buildx
3. 登录到GitHub Container Registry
4. 构建并推送Docker镜像到GitHub Container Registry
5. 下载镜像并打包成tar文件
6. 上传tar文件到GitHub Releases

## 3. 创建Release

1. 进入GitHub仓库页面
2. 点击右侧的"Releases"标签
3. 点击"Draft a new release"按钮
4. 填写Release信息：
   - **Tag version**：输入版本号，如`v1.0.0`
   - **Target**：选择要发布的分支，通常是`main`
   - **Release title**：输入Release标题，如`v1.0.0 Release`
   - **Description**：输入Release描述，说明本次发布的主要内容
5. 点击"Publish release"按钮发布Release

## 4. 下载镜像

Release发布成功后，GitHub Actions会自动上传Docker镜像到Release中。你可以通过以下方式下载：

1. 进入Release页面
2. 在"Assets"部分找到需要下载的镜像文件
3. 点击下载链接，将镜像文件下载到本地

### 4.1 可用镜像文件

- `smart-exam-system-amd64.tar.gz`：完整应用镜像（amd64架构）
- `smart-exam-system-arm64.tar.gz`：完整应用镜像（arm64架构）
- `smart-exam-system-backend-amd64.tar.gz`：后端镜像（amd64架构）
- `smart-exam-system-backend-arm64.tar.gz`：后端镜像（arm64架构）
- `smart-exam-system-frontend-amd64.tar.gz`：前端镜像（amd64架构）
- `smart-exam-system-frontend-arm64.tar.gz`：前端镜像（arm64架构）

## 5. 使用下载的镜像

### 5.1 加载Docker镜像

下载镜像文件后，需要将其加载到Docker中：

```bash
# 解压镜像文件
gunzip smart-exam-system-amd64.tar.gz

# 加载镜像到Docker
docker load -i smart-exam-system-amd64.tar
```

### 5.2 查看加载的镜像

```bash
docker images
```

你应该能看到加载的镜像，标签为`ghcr.io/OWNER/REPOSITORY:TAG_NAME`

### 5.3 运行镜像

#### 5.3.1 直接运行完整应用镜像

```bash
docker run -d -p 8080:8080 --name smart-exam-system \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://mysql:3306/smart_exam?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai" \
  -e SPRING_DATASOURCE_USERNAME="smart_exam_user" \
  -e SPRING_DATASOURCE_PASSWORD="smart_exam_password" \
  ghcr.io/OWNER/REPOSITORY:TAG_NAME
```

#### 5.3.2 使用Docker Compose

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
    image: ghcr.io/OWNER/REPOSITORY:TAG_NAME
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

## 6. 从GitHub Container Registry直接拉取

除了从Releases下载镜像，你也可以直接从GitHub Container Registry拉取镜像：

```bash
# 拉取完整应用镜像
docker pull ghcr.io/OWNER/REPOSITORY:TAG_NAME

# 拉取后端镜像
docker pull ghcr.io/OWNER/REPOSITORY/backend:TAG_NAME

# 拉取前端镜像
docker pull ghcr.io/OWNER/REPOSITORY/frontend:TAG_NAME
```

### 6.1 配置GitHub Container Registry访问权限

如果镜像所在的仓库是私有的，你需要先登录到GitHub Container Registry：

```bash
docker login ghcr.io
```

然后输入你的GitHub用户名和个人访问令牌（从GitHub的Settings -> Developer settings -> Personal access tokens获取）

## 7. 镜像管理

### 7.1 列出本地镜像

```bash
docker images
```

### 7.2 删除本地镜像

```bash
docker rmi ghcr.io/OWNER/REPOSITORY:TAG_NAME
```

### 7.3 清理未使用的镜像

```bash
docker image prune
```

## 8. 常见问题

### 8.1 镜像下载缓慢

- 尝试使用国内镜像加速服务
- 考虑从GitHub Container Registry直接拉取

### 8.2 镜像加载失败

- 检查镜像文件是否完整
- 确保Docker版本支持该镜像格式
- 检查系统架构是否与镜像架构匹配

### 8.3 应用启动失败

- 检查容器日志：`docker logs smart-exam-system`
- 确保数据库配置正确
- 确保数据库服务已经启动

### 8.4 镜像标签不正确

- 检查Release标签是否与镜像标签匹配
- 确保GitHub Actions工作流已成功执行

## 9. 自动化更新

你可以使用以下命令自动更新镜像：

```bash
# 拉取最新版本
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

## 10. 总结

通过GitHub Releases，你可以方便地下载和管理Docker镜像。GitHub Actions自动完成了镜像的构建、打包和上传工作，简化了发布流程。你可以根据需要选择从Releases下载镜像或直接从GitHub Container Registry拉取镜像，然后使用Docker或Docker Compose部署应用。

## 11. 相关链接

- [GitHub Releases文档](https://docs.github.com/en/repositories/releasing-projects-on-github/about-releases)
- [Docker文档](https://docs.docker.com/)
- [GitHub Actions文档](https://docs.github.com/en/actions)

---

**智能考试系统** - 专业、高效的医疗人员三基考试平台