<template>
  <div class="user-management">
    <h1>用户管理</h1>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">总用户数</p>
              <h3 class="stat-value">{{ totalUserCount }}</h3>
            </div>
            <div class="stat-icon user-icon">
              <el-icon><User /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">学生人数</p>
              <h3 class="stat-value">{{ studentCount }}</h3>
            </div>
            <div class="stat-icon student-icon">
              <el-icon><Avatar /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">管理员人数</p>
              <h3 class="stat-value">{{ adminCount }}</h3>
            </div>
            <div class="stat-icon admin-icon">
              <el-icon><Setting /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 功能导航 -->
    <el-card class="nav-card" style="margin-top: 20px;">
      <h3>功能导航</h3>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="nav-item" @click="navigateTo('student-list')">
            <div class="nav-content">
              <el-icon class="nav-icon"><Avatar /></el-icon>
              <div class="nav-info">
                <h4>学生管理</h4>
                <p>查看和管理所有学生用户</p>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="12">
          <el-card class="nav-item" @click="navigateTo('admin-list')">
            <div class="nav-content">
              <el-icon class="nav-icon"><User /></el-icon>
              <div class="nav-info">
                <h4>管理员管理</h4>
                <p>查看和管理所有管理员用户</p>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Avatar, Setting } from '@element-plus/icons-vue'

const router = useRouter()

// 统计数据
const totalUserCount = ref(150)
const studentCount = ref(120)
const adminCount = ref(10)

const navigateTo = (path: string) => {
  router.push(`/dashboard/${path}`)
}
</script>

<style scoped>
.user-management {
  padding: 20px 0;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 120px;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 24px;
  color: white;
}

.user-icon {
  background-color: #409eff;
}

.student-icon {
  background-color: #67c23a;
}

.admin-icon {
  background-color: #e6a23c;
}

.nav-card {
  margin-top: 20px;
}

.nav-item {
  cursor: pointer;
  transition: all 0.3s;
  height: 120px;
  display: flex;
  align-items: center;
}

.nav-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.nav-content {
  display: flex;
  align-items: center;
  width: 100%;
}

.nav-icon {
  font-size: 48px;
  color: #409eff;
  margin-right: 20px;
}

.nav-info h4 {
  margin: 0 0 5px 0;
  color: #303133;
}

.nav-info p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}
</style>