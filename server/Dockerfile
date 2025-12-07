# 第一阶段：构建阶段
FROM eclipse-temurin:11-jdk-alpine AS build

# 设置工作目录
WORKDIR /app

# 更新 Alpine Linux 的 ca-certificates 并配置 Java SSL
RUN apk update && apk add ca-certificates && update-ca-certificates \
    && apk add openssl

# 设置 Java 运行参数以允许不安全的 SSL/TLS
ENV MAVEN_OPTS="-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true -Dmaven.wagon.http.ssl.ignore.validity.dates=true"

# 安装 Maven
RUN apk add --no-cache maven

# 创建 Maven 配置文件，使用阿里云镜像
RUN mkdir -p /root/.m2
RUN cat > /root/.m2/settings.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    <mirrors>
        <mirror>
            <id>aliyunmaven</id>
            <mirrorOf>*</mirrorOf>
            <name>阿里云公共仓库</name>
            <url>https://maven.aliyun.com/repository/public</url>
        </mirror>
    </mirrors>
</settings>
EOF

# 复制pom.xml文件，下载依赖
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建项目，生成jar文件
RUN mvn clean package -DskipTests

# 第二阶段：运行阶段
FROM eclipse-temurin:11-jre-alpine

# 设置工作目录
WORKDIR /app

# 更新 Alpine Linux 的 ca-certificates
RUN apk update && apk add ca-certificates && update-ca-certificates

# 从构建阶段复制jar文件
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
CMD ["java", "-jar", "app.jar"]