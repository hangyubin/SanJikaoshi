# 第一阶段：构建前端
FROM node:18 AS frontend-build

WORKDIR /app

# 复制前端目录
COPY client/ ./client

WORKDIR /app/client

# 安装前端依赖
RUN npm install --legacy-peer-deps

# 构建前端项目
RUN npm run build

# 第二阶段：构建后端
FROM amazoncorretto:11-alpine AS backend-build

WORKDIR /app

# 复制后端目录
COPY server/ ./server

WORKDIR /app/server

# 安装Maven
RUN apk update && apk add --no-cache maven

# 创建Maven settings文件使用阿里云镜像源
RUN mkdir -p /root/.m2 && echo '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd"><mirrors><mirror><id>alimaven</id><name>aliyun maven</name><url>https://maven.aliyun.com/repository/public</url><mirrorOf>central</mirrorOf></mirror></mirrors></settings>' > /root/.m2/settings.xml

# 下载依赖（使用阿里云镜像源）
RUN mvn dependency:go-offline -B

# 构建后端项目（跳过前端构建，使用阿里云镜像源）
RUN mvn clean package -DskipTests

# 第三阶段：最终镜像
FROM amazoncorretto:11-alpine

WORKDIR /app

# 从后端构建阶段复制后端jar文件
COPY --from=backend-build /app/server/target/backend-0.0.1-SNAPSHOT.jar /app/app.jar

# 从前端构建阶段复制前端构建结果到后端的static目录
RUN mkdir -p /app/static
COPY --from=frontend-build /app/client/dist /app/static

# 暴露端口
EXPOSE 8080

# 启动服务，添加静态资源目录参数
CMD ["java", "-jar", "/app/app.jar", "--spring.resources.static-locations=file:/app/static"]
