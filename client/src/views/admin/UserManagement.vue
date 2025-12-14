<template>
  <div class="user-management">
    <h1>用户管理</h1>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">总用户数(注册人数)</p>
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
              <p class="stat-label">在线人数</p>
              <h3 class="stat-value">{{ onlineCount }}</h3>
            </div>
            <div class="stat-icon online-icon">
              <el-icon><VideoPlay /></el-icon>
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
                <h4>用户管理</h4>
                <p>查看和管理所有用户</p>
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
import { User, Setting, VideoPlay } from '@element-plus/icons-vue'

const router = useRouter()

// 统计数据，初始值设为空，等待API获取
const totalUserCount = ref(0)
const adminCount = ref(0)
const onlineCount = ref(0)

const navigateTo = (path: string) => {
  router.push(`/dashboard/${path}`)
}
</script>

<style scoped>
.user-management {
  padding: 20px 0;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

/* 页面标题 */
.user-management h1 {
  margin: 0 0 30px 0;
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 0 0 20px 0;
  border-bottom: 2px solid #e4e7ed;
}

.user-management h1::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 32px;
  background: linear-gradient(180deg, #667eea, #764ba2);
  border-radius: 2px;
}

/* 统计卡片 */
.stat-card {
  margin-bottom: 20px;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  background: white;
  overflow: hidden;
  position: relative;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.stat-card:hover::before {
  transform: scaleX(1);
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 140px;
  padding: 0 30px;
  position: relative;
  z-index: 1;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
  display: block;
  font-weight: 500;
  letter-spacing: 0.5px;
  animation: fadeInUp 0.6s ease 0.2s both;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #303133;
  margin: 0;
  display: block;
  animation: countUp 1s ease-out;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

@keyframes countUp {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.stat-icon {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 32px;
  color: white;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  overflow: hidden;
  animation: fadeInUp 0.6s ease 0.4s both;
}

.stat-icon::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s ease;
}

.stat-card:hover .stat-icon::before {
  left: 100%;
}

.stat-card:hover .stat-icon {
  transform: scale(1.15) rotate(10deg);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.25);
}

.user-icon {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.admin-icon {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
}

.online-icon {
  background: linear-gradient(135deg, #909399 0%, #a6a9ad 100%);
}

/* 导航卡片 */
.nav-card {
  margin-top: 30px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  background: white;
  overflow: hidden;
  position: relative;
}

.nav-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2);
}

.nav-card h3 {
  margin: 0 0 25px 0;
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 30px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.nav-card h3::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 20px;
  background: linear-gradient(180deg, #667eea, #764ba2);
  border-radius: 2px;
}

/* 导航项 */
.nav-item {
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  background: white;
  margin: 0 30px 20px;
  height: 140px;
  display: flex;
  align-items: center;
  overflow: hidden;
  position: relative;
}

.nav-item:last-child {
  margin-bottom: 30px;
}

.nav-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(180deg, #667eea, #764ba2);
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.nav-item:hover::before {
  transform: scaleY(1);
}

.nav-item:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.2);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
}

.nav-content {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 0 30px;
  gap: 25px;
  position: relative;
  z-index: 1;
}

.nav-icon {
  font-size: 56px;
  color: #667eea;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  filter: drop-shadow(0 4px 8px rgba(102, 126, 234, 0.2));
}

.nav-item:hover .nav-icon {
  transform: scale(1.15) rotate(15deg);
  color: #764ba2;
  filter: drop-shadow(0 6px 12px rgba(102, 126, 234, 0.3));
}

.nav-info {
  flex: 1;
}

.nav-info h4 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 18px;
  font-weight: bold;
  transition: color 0.3s ease;
}

.nav-item:hover .nav-info h4 {
  color: #667eea;
}

.nav-info p {
  margin: 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.5;
  transition: color 0.3s ease;
  font-weight: 500;
}

.nav-item:hover .nav-info p {
  color: #303133;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-management {
    padding: 10px 0;
  }
  
  .user-management h1 {
    font-size: 24px;
    padding: 0 0 15px 0;
  }
  
  .user-management h1::before {
    height: 24px;
  }
  
  .stat-card {
    margin-bottom: 20px;
  }
  
  .stat-content {
    height: 120px;
    padding: 0 20px;
  }
  
  .stat-value {
    font-size: 28px;
  }
  
  .stat-icon {
    width: 60px;
    height: 60px;
    font-size: 28px;
  }
  
  .nav-card h3 {
    padding: 20px;
    font-size: 18px;
  }
  
  .nav-item {
    margin: 0 20px 20px;
    height: 120px;
  }
  
  .nav-item:last-child {
    margin-bottom: 20px;
  }
  
  .nav-content {
    padding: 0 20px;
    gap: 15px;
  }
  
  .nav-icon {
    font-size: 48px;
  }
  
  .nav-info h4 {
    font-size: 16px;
  }
  
  .nav-info p {
    font-size: 13px;
  }
}
</style>



