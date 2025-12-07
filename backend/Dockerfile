# 第一阶段：构建阶段
FROM maven:3.8.8-eclipse-temurin-8-alpine AS build

# 设置工作目录
WORKDIR /app

# 复制pom.xml文件，下载依赖
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建项目，生成jar文件
RUN mvn clean package -DskipTests

# 第二阶段：运行阶段
FROM eclipse-temurin:8-alpine

# 设置工作目录
WORKDIR /app

# 从构建阶段复制jar文件
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
CMD ["java", "-jar", "app.jar"]