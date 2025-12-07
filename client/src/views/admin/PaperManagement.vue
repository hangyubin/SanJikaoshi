<template>
  <div class="paper-management">
    <h1>卷题管理</h1>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">试卷总数</p>
              <h3 class="stat-value">{{ totalPaperCount }}</h3>
            </div>
            <div class="stat-icon paper-icon">
              <el-icon><DocumentCopy /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">题目总数</p>
              <h3 class="stat-value">{{ totalQuestionCount }}</h3>
            </div>
            <div class="stat-icon question-icon">
              <el-icon><EditPen /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">活跃试卷</p>
              <h3 class="stat-value">{{ activePaperCount }}</h3>
            </div>
            <div class="stat-icon active-icon">
              <el-icon><Check /></el-icon>
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
          <el-card class="nav-item" @click="navigateTo('paper')">
            <div class="nav-content">
              <el-icon class="nav-icon"><DocumentCopy /></el-icon>
              <div class="nav-info">
                <h4>试卷管理</h4>
                <p>查看和管理所有试卷</p>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="12">
          <el-card class="nav-item" @click="navigateTo('question-management')">
            <div class="nav-content">
              <el-icon class="nav-icon"><EditPen /></el-icon>
              <div class="nav-info">
                <h4>题目管理</h4>
                <p>查看和管理所有题目</p>
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
import { DocumentCopy, EditPen, Check } from '@element-plus/icons-vue'

const router = useRouter()

// 统计数据
const totalPaperCount = ref(85)
const totalQuestionCount = ref(2500)
const activePaperCount = ref(45)

const navigateTo = (path: string) => {
  router.push(`/dashboard/${path}`)
}
</script>

<style scoped>
.paper-management {
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

.paper-icon {
  background-color: #409eff;
}

.question-icon {
  background-color: #67c23a;
}

.active-icon {
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