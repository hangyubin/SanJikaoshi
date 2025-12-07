# 使用官方Nginx镜像作为基础镜像
FROM nginx:1.24-alpine

# 设置工作目录
WORKDIR /usr/share/nginx/html

# 复制前端构建文件到Nginx的静态文件目录
COPY dist/ .

# 复制自定义Nginx配置文件（如果需要）
# COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露80端口
EXPOSE 80

# 启动Nginx服务
CMD ["nginx", "-g", "daemon off;"]