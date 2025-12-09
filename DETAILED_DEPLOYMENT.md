# 智能考试系统 - 详细部署步骤

## 0. 前置条件检查

在开始部署之前，请确保您的计算机满足以下条件：

- ✅ 有稳定的网络连接
- ✅ 操作系统：Windows 10/11、macOS 或 Linux
- ✅ 足够的磁盘空间（至少10GB）
- ✅ 足够的内存（至少4GB）

## 1. 安装必要软件

### 1.1 安装 Docker

#### Windows 系统

1. 访问 Docker 官网：https://www.docker.com/get-started
2. 下载 Docker Desktop for Windows
3. 双击安装包，按照提示完成安装
4. 安装完成后，重启计算机
5. 打开 Docker Desktop，同意服务条款
6. 等待 Docker 初始化完成

#### macOS 系统

1. 访问 Docker 官网：https://www.docker.com/get-started
2. 下载 Docker Desktop for Mac
3. 双击安装包，将 Docker 拖拽到 Applications 文件夹
4. 打开 Docker Desktop，同意服务条款
5. 等待 Docker 初始化完成

#### Linux 系统（以 Ubuntu 为例）

```bash
# 更新软件包列表
sudo apt update

# 安装必要的依赖包
sudo apt install -y apt-transport-https ca-certificates curl software-properties-common

# 添加 Docker 的官方 GPG 密钥
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# 添加 Docker 仓库
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 更新软件包列表
sudo apt update

# 安装 Docker
sudo apt install -y docker-ce docker-ce-cli containerd.io

# 启动 Docker 服务
sudo systemctl start docker

# 设置 Docker 开机自启
sudo systemctl enable docker

# 将当前用户添加到 docker 组（避免每次使用 sudo）
sudo usermod -aG docker $USER
```

### 1.2 验证 Docker 安装

打开终端，执行以下命令验证 Docker 是否安装成功：

```bash
docker --version
```

预期输出：
```
Docker version 20.10.23, build 7155243
```

### 1.3 安装 Docker Compose

大多数 Docker 安装包已经包含了 Docker Compose，您可以通过以下命令验证：

```bash
docker-compose --version
```

如果没有安装，请按照以下步骤安装：

#### Windows 和 macOS

Docker Desktop 已经包含了 Docker Compose，无需单独安装。

#### Linux 系统

```bash
# 下载 Docker Compose 二进制文件
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# 赋予执行权限
sudo chmod +x /usr/local/bin/docker-compose

# 验证安装
docker-compose --version
```

## 2. 获取项目代码

请确保您已经获取了完整的项目代码。如果还没有获取，可以通过以下方式获取：

### 2.1 通过 Git 克隆（如果有 Git 环境）

```bash
git clone <仓库地址>
cd smart-exam-system
```

### 2.2 通过压缩包下载

1. 下载项目压缩包
2. 解压到本地目录
3. 进入解压后的目录

## 3. 详细部署步骤（使用 Docker Compose）

### 步骤 1：打开终端

#### Windows 系统

- 按下 `Win + R`，输入 `cmd`，回车打开命令提示符
- 或者使用 PowerShell：按下 `Win + X`，选择 "Windows PowerShell"
- 或者使用 Windows Terminal（推荐）

#### macOS 系统

- 打开 Launchpad，找到 "终端"
- 或者使用 Spotlight 搜索 "终端"

#### Linux 系统

- 按下 `Ctrl + Alt + T` 打开终端

### 步骤 2：进入项目根目录

使用 `cd` 命令进入项目根目录，例如：

```bash
# Windows 系统
cd D:\Projects\smart-exam-system

# macOS 或 Linux 系统
cd /home/user/Projects/smart-exam-system
```

### 步骤 3：查看目录结构

执行以下命令，确认目录结构正确：

```bash
# Windows 系统
dir

# macOS 或 Linux 系统
ls -la
```

预期输出应包含以下文件和目录：
```
docker-compose.yml
Dockerfile
server/
client/
```

### 步骤 4：构建并启动服务

执行以下命令，构建并启动所有服务：

```bash
docker-compose up -d
```

#### 预期输出（部分）

```
Creating network "smart-exam-system_smart-exam-network" with driver "bridge"
Creating volume "smart-exam-system_mysql_data" with local driver
Pulling mysql (mysql:5.7)...
5.7: Pulling from library/mysql
...
Building app
[+] Building 10.0s (20/20) FINISHED
...
Creating smart-exam-mysql ... done
Creating smart-exam-app   ... done
```

### 步骤 5：等待服务启动完成

首次启动可能需要 1-5 分钟（取决于网络速度和计算机性能），请耐心等待。

### 步骤 6：查看服务状态

执行以下命令，查看服务是否正常启动：

```bash
docker-compose ps
```

#### 预期输出

```
NAME                COMMAND                  SERVICE             STATUS              PORTS
smart-exam-app      "java -jar /app/app.j…"   app                 running             0.0.0.0:8080->8080/tcp
smart-exam-mysql    "docker-entrypoint.s…"   mysql               running             0.0.0.0:3306->3306/tcp
```

如果看到 `STATUS` 列显示 `running`，表示服务已经成功启动。

### 步骤 7：验证服务日志

执行以下命令，查看应用服务日志，确认没有错误：

```bash
docker-compose logs app
```

预期输出应包含类似以下内容（表示服务已成功启动）：

```
smart-exam-app | 2025-12-09 08:23:45.678  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
smart-exam-app | 2025-12-09 08:23:45.689  INFO 1 --- [           main] c.s.b.SmartExamBackendApplication        : Started SmartExamBackendApplication in 12.345 seconds (JVM running for 13.567)
```

### 步骤 8：访问应用

打开浏览器，访问以下地址：

```
http://localhost:8080
```

#### 预期结果

您应该看到智能考试系统的登录页面。

### 步骤 9：登录系统

使用以下初始账号登录：

- **用户名**：admin
- **密码**：admin123

#### 预期结果

登录成功后，您应该看到系统的仪表盘页面。

## 4. 手动部署详细步骤

如果您希望手动部署，可以按照以下步骤操作：

### 4.1 部署数据库

#### 步骤 1：启动 MySQL 容器

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

#### 步骤 2：验证数据库连接

```bash
# 使用 MySQL 客户端连接数据库
mysql -h localhost -P 3306 -u smart_exam_user -p
```

输入密码 `smart_exam_password`，如果能成功连接，则数据库部署成功。

### 4.2 部署后端服务

#### 步骤 1：安装 Java JDK

请参考：https://www.oracle.com/java/technologies/downloads/ 或 https://adoptium.net/

#### 步骤 2：安装 Maven

请参考：https://maven.apache.org/install.html

#### 步骤 3：构建后端服务

```bash
cd server
mvn clean package -DskipTests
```

#### 步骤 4：运行后端服务

```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### 4.3 部署前端服务

#### 步骤 1：安装 Node.js

请参考：https://nodejs.org/en/download/

#### 步骤 2：安装前端依赖

```bash
cd client
npm install
```

#### 步骤 3：构建前端项目

```bash
npm run build
```

#### 步骤 4：部署前端静态文件

将 `client/dist` 目录下的所有文件复制到后端服务的 `static` 目录下：

```bash
# 创建 static 目录
mkdir -p ../server/src/main/resources/static

# 复制文件
cp -r dist/* ../server/src/main/resources/static/
```

### 4.4 重启后端服务

重启后端服务，使前端静态文件生效。

## 5. 服务验证

### 验证 1：访问登录页面

- 地址：http://localhost:8080
- 预期：看到登录页面

### 验证 2：登录系统

- 用户名：admin
- 密码：admin123
- 预期：登录成功，看到仪表盘页面

### 验证 3：测试 API 接口

使用 curl 或 Postman 测试以下接口：

```bash
curl -X POST -H "Content-Type: application/json" -d '{"username":"admin","password":"admin123"}' http://localhost:8080/auth/login
```

预期输出：包含 token 的 JSON 响应。

## 6. 服务管理命令

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

### 6.4 查看所有服务状态

```bash
docker-compose ps
```

### 6.5 查看应用日志

```bash
# 查看最新日志
docker-compose logs app

# 实时查看日志
docker-compose logs -f app

# 查看指定数量的日志行
docker-compose logs --tail 100 app
```

### 6.6 查看数据库日志

```bash
docker-compose logs mysql
```

### 6.7 进入应用容器

```bash
docker exec -it smart-exam-app /bin/sh
```

### 6.8 进入数据库容器

```bash
docker exec -it smart-exam-mysql /bin/bash
```

## 7. 常见问题排查

### 问题 1：端口被占用

**现象**：启动服务时提示端口已被占用

**解决方案**：
1. 修改 `docker-compose.yml` 文件中的端口映射
2. 找到占用端口的进程并关闭

**操作步骤**：

```bash
# Windows 系统：查看端口占用
netstat -ano | findstr :8080
# 关闭占用端口的进程
taskkill /PID <进程ID> /F

# macOS 或 Linux 系统：查看端口占用
lsof -i :8080
# 关闭占用端口的进程
kill -9 <进程ID>
```

### 问题 2：服务启动失败

**现象**：执行 `docker-compose ps` 时，服务状态显示 `Exit` 或 `Restarting`

**解决方案**：
1. 查看服务日志，定位错误原因
2. 根据日志信息进行修复

**操作步骤**：

```bash
docker-compose logs -f app
```

### 问题 3：访问页面显示 404

**现象**：浏览器访问 http://localhost:8080 时显示 404

**解决方案**：
1. 检查服务是否正常运行
2. 查看应用日志，确认服务启动成功
3. 检查前端静态文件是否正确部署

### 问题 4：数据库连接失败

**现象**：应用日志中显示数据库连接失败

**解决方案**：
1. 检查数据库服务是否正常运行
2. 检查数据库配置是否正确
3. 检查网络连接是否正常

### 问题 5：构建镜像失败

**现象**：执行 `docker-compose up -d` 时，构建镜像失败

**解决方案**：
1. 检查网络连接是否正常
2. 检查 Dockerfile 是否正确
3. 查看构建日志，定位错误原因

## 8. 开发模式

### 8.1 后端开发模式

```bash
cd server
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 上运行。

### 8.2 前端开发模式

```bash
cd client
npm run dev
```

前端开发服务器将在 http://localhost:5173 上运行。

## 9. 项目配置文件

### 9.1 后端配置

文件路径：`server/src/main/resources/application.properties`

主要配置项：
- 数据库连接配置
- 服务器端口配置
- JWT 配置
- 日志配置

### 9.2 Docker Compose 配置

文件路径：`docker-compose.yml`

主要配置项：
- 服务定义
- 端口映射
- 环境变量
- 卷挂载
- 网络配置

## 10. 安全注意事项

1. **生产环境中请修改默认密码**：
   - 数据库密码
   - 初始管理员密码

2. **生产环境中请配置 HTTPS**：
   - 使用 SSL 证书
   - 配置 Nginx 或其他反向代理

3. **生产环境中请关闭不必要的端口**：
   - 只开放必要的端口
   - 配置防火墙规则

4. **定期备份数据库**：
   ```bash
   docker exec smart-exam-mysql mysqldump -u root -proot smart_exam > smart_exam_backup.sql
   ```

## 11. 技术支持

如果您在部署过程中遇到问题，可以：

1. **查看日志**：
   ```bash
   docker-compose logs -f
   ```

2. **检查服务状态**：
   ```bash
   docker-compose ps
   ```

3. **重启服务**：
   ```bash
   docker-compose restart
   ```

4. **重新构建镜像**：
   ```bash
   docker-compose up -d --build
   ```

5. **完全重置**：
   ```bash
   docker-compose down -v
   docker-compose up -d --build
   ```

---

**智能考试系统** - 专业、高效的在线考试平台
**详细部署步骤** - v1.0.0
