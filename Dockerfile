# 第一阶段：构建前端
FROM node:18 AS frontend-build

WORKDIR /app/client

# 复制前端依赖文件
COPY client/package*.json ./

# 安装前端依赖
RUN npm install

# 复制前端源代码
COPY client/ .

# 构建前端项目
RUN npm run build

# 第二阶段：构建后端
FROM eclipse-temurin:11-jdk-alpine AS backend-build

WORKDIR /app/server

# 复制后端依赖文件
COPY server/pom.xml ./

# 安装Maven并下载依赖
RUN apk add --no-cache maven && \
    mvn dependency:go-offline -B

# 复制后端源代码
COPY server/src ./src

# 从前端构建阶段复制前端构建结果到后端的static目录
COPY --from=frontend-build /app/client/dist /app/server/src/main/resources/static

# 构建后端项目
RUN mvn clean package -DskipTests

# 第三阶段：最终镜像
FROM eclipse-temurin:11-jre-alpine

WORKDIR /app

# 从后端构建阶段复制后端jar文件
COPY --from=backend-build /app/server/target/backend-0.0.1-SNAPSHOT.jar /app/app.jar

# 暴露端口
EXPOSE 8080

# 启动服务
CMD ["java", "-jar", "/app/app.jar"]
