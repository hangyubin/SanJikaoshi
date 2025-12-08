#!/bin/sh

# 启动后端服务
echo "Starting backend service..."
java -jar /app/backend.jar > /app/backend.log 2>&1 &
BACKEND_PID=$!

echo "Backend service started with PID: $BACKEND_PID"

# 等待后端服务启动
echo "Waiting for backend service to start..."
sleep 10

# 检查后端服务是否启动成功
if ps -p $BACKEND_PID > /dev/null;
then
    echo "Backend service started successfully"
else
    echo "Backend service failed to start. Checking logs..."
    tail -20 /app/backend.log
    exit 1
fi

# 启动