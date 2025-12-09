#!/bin/bash

# 智能考试系统 Docker 部署脚本

echo "========================================"
echo "智能考试系统 Docker 部署"
echo "========================================"

echo "1. 检查 Docker 和 Docker Compose 是否已安装..."
if ! command -v docker &> /dev/null; then
    echo "错误: Docker 未安装，请先安装 Docker"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "错误: Docker Compose 未安装，请先安装 Docker Compose"
    exit 1
fi

echo "Docker 和 Docker Compose 已安装，版本信息:"
docker --version
docker-compose --version

echo "\n2. 拉取最新的 Docker 镜像..."
docker-compose pull

echo "\n3. 停止并删除旧的容器（如果存在）..."
docker-compose down

echo "\n4. 启动服务..."
docker-compose up -d

echo "\n5. 显示服务状态..."
docker-compose ps

echo "\n========================================"
echo "部署完成！"
echo "系统访问地址: http://localhost:8080"
echo "数据库地址: localhost:3306"
echo "数据库用户名: smart_exam_user"
echo "数据库密码: smart_exam_password"
echo "========================================"
