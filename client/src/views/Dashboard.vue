<template>
  <div class="dashboard">
    <h1>欢迎使用医疗卫生系统三基考试培训</h1>
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">总用户数</p>
              <h3 class="stat-value">{{ userCount }}</h3>
            </div>
            <div class="stat-icon user-icon">
              <el-icon><User /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">科目数量</p>
              <h3 class="stat-value">{{ subjectCount }}</h3>
            </div>
            <div class="stat-icon subject-icon">
              <el-icon><Document /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">题目数量</p>
              <h3 class="stat-value">{{ questionCount }}</h3>
            </div>
            <div class="stat-icon question-icon">
              <el-icon><EditPen /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">试卷数量</p>
              <h3 class="stat-value">{{ paperCount }}</h3>
            </div>
            <div class="stat-icon paper-icon">
              <el-icon><DocumentCopy /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 最近考试列表 -->
    <el-card class="exam-list-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <h3>最近考试安排</h3>
          <el-button type="primary" size="small">查看全部</el-button>
        </div>
      </template>
      
      <el-table :data="recentExams" stripe style="width: 100%">
        <el-table-column prop="name" label="考试名称" min-width="180"></el-table-column>
        <el-table-column prop="subject" label="科目" width="120"></el-table-column>
        <el-table-column prop="totalScore" label="总分" width="80"></el-table-column>
        <el-table-column prop="duration" label="时长(分钟)" width="100"></el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '进行中' ? 'success' : scope.row.status === '未开始' ? 'info' : 'danger'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="">
            <el-button type="primary" size="small">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { User, Document, EditPen, DocumentCopy } from '@element-plus/icons-vue'

// 统计数据
const userCount = ref(120)
const subjectCount = ref(15)
const questionCount = ref(2500)
const paperCount = ref(85)

// 最近考试数据
const recentExams = ref([
  {
    id: 1,
    name: '2025年春季数学期中考试',
    subject: '数学',
    totalScore: 100,
    duration: 90,
    startTime: '2025-12-10 09:00:00',
    status: '未开始'
  },
  {
    id: 2,
    name: '2025年春季英语期中考试',
    subject: '英语',
    totalScore: 100,
    duration: 120,
    startTime: '2025-12-10 14:00:00',
    status: '未开始'
  },
  {
    id: 3,
    name: '2025年春季语文期中考试',
    subject: '语文',
    totalScore: 150,
    duration: 150,
    startTime: '2025-12-11 09:00:00',
    status: '未开始'
  },
  {
    id: 4,
    name: '2025年秋季物理期末考试',
    subject: '物理',
    totalScore: 100,
    duration: 100,
    startTime: '2025-12-05 09:00:00',
    status: '已结束'
  }
])

onMounted(() => {
  // 模拟从后端获取数据
  console.log('Dashboard mounted')
})
</script>

<style scoped>
.dashboard {
  padding: 20px 0;
}

.stat-card {
  height: 120px;
  margin-bottom: 20px;
}

/* 适配小屏幕设备 */
@media (max-width: 768px) {
  .stat-card {
    margin-bottom: 20px;
  }
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
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

.subject-icon {
  background-color: #67c23a;
}

.question-icon {
  background-color: #e6a23c;
}

.paper-icon {
  background-color: #f56c6c;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
}
</style>