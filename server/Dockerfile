# 第一阶段：前端构建阶段
FROM node:18-alpine AS frontend-build

# 设置工作目录
WORKDIR /app/frontend

# 复制前端代码
COPY ../client/package.json ../client/package-lock.json ./
RUN npm install

# 复制前端源代码
COPY ../client/ ./

# 构建前端项目
RUN npm run build

# 第二阶段：后端构建阶段
FROM eclipse-temurin:11-jdk AS backend-build

# 设置工作目录
WORKDIR /app/backend

# 设置 Java 运行参数以允许不安全的 SSL/TLS
ENV MAVEN_OPTS="-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true"

# 安装 Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# 创建 Maven 配置文件，使用阿里云镜像
RUN mkdir -p /root/.m2 && \
    echo '<?xml version="1.0" encoding="UTF-8"?>' > /root/.m2/settings.xml && \
    echo '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"' >> /root/.m2/settings.xml && \
    echo '          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"' >> /root/.m2/settings.xml && \
    echo '          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">' >> /root/.m2/settings.xml && \
    echo '    <mirrors>' >> /root/.m2/settings.xml && \
    echo '        <mirror>' >> /root/.m2/settings.xml && \
    echo '            <id>aliyunmaven</id>' >> /root/.m2/settings.xml && \
    echo '            <mirrorOf>*</mirrorOf>' >> /root/.m2/settings.xml && \
    echo '            <name>阿里云公共仓库</name>' >> /root/.m2/settings.xml && \
    echo '            <url>https://maven.aliyun.com/repository/public</url>' >> /root/.m2/settings.xml && \
    echo '        </mirror>' >> /root/.m2/settings.xml && \
    echo '    </mirrors>' >> /root/.m2/settings.xml && \
    echo '</settings>' >> /root/.m2/settings.xml

# 复制pom.xml文件，下载依赖
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 从前端构建阶段复制构建好的静态资源到后端的src/main/resources/static目录
RUN mkdir -p src/main/resources/static
COPY --from=frontend-build /app/frontend/dist/* src/main/resources/static/
COPY --from=frontend-build /app/frontend/dist/assets src/main/resources/static/assets

# 构建项目，生成jar文件
RUN mvn clean package -DskipTests

# 第二阶段：运行阶段
FROM eclipse-temurin:11-jre

# 设置工作目录
WORKDIR /app

# 从构建阶段复制jar文件
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
CMD ["java", "-jar", "app.jar"]