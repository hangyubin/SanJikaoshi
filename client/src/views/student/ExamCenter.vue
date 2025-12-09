<template>
  <div class="exam-center">
    <h1>考试中心</h1>
    
    <el-card class="exam-list-card">
      <template #header>
        <div class="card-header">
          <h3>可参加的考试</h3>
        </div>
      </template>
      
      <el-table :data="examList" stripe style="width: 100%">
        <el-table-column prop="name" label="考试名称" min-width="180"></el-table-column>
        <el-table-column prop="subject" label="科目" width="120"></el-table-column>
        <el-table-column prop="totalScore" label="总分" width="80"></el-table-column>
        <el-table-column prop="duration" label="时长(分钟)" width="100"></el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180"></el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '进行中' ? 'success' : scope.row.status === '未开始' ? 'info' : 'danger'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small" 
              @click="joinExam(scope.row)"
              :disabled="scope.row.status !== '进行中'"
            >
              参加考试
            </el-button>
            <el-button 
              type="info" 
              size="small" 
              @click="viewExamDetail(scope.row.id)"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 已完成的考试 -->
    <el-card class="exam-list-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <h3>已完成的考试</h3>
        </div>
      </template>
      
      <el-table :data="finishedExams" stripe style="width: 100%">
        <el-table-column prop="name" label="考试名称" min-width="180"></el-table-column>
        <el-table-column prop="subject" label="科目" width="120"></el-table-column>
        <el-table-column prop="totalScore" label="总分" width="80"></el-table-column>
        <el-table-column prop="obtainedScore" label="得分" width="80"></el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180"></el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180"></el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button 
              type="info" 
              size="small" 
              @click="viewExamDetail(scope.row.id)"
            >
              查看试卷
            </el-button>
            <el-button 
              type="success" 
              size="small" 
              @click="viewErrorNotebook()"
            >
              查看错题
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 模拟可参加的考试数据（三基考试科目）
const examList = ref([
  {
    id: 1,
    name: '2025年秋季基本理论考试',
    subject: '基本理论',
    totalScore: 100,
    duration: 90,
    startTime: '2025-12-10 09:00:00',
    endTime: '2025-12-10 10:30:00',
    status: '进行中'
  },
  {
    id: 2,
    name: '2025年秋季基础知识考试',
    subject: '基础知识',
    totalScore: 100,
    duration: 120,
    startTime: '2025-12-11 09:00:00',
    endTime: '2025-12-11 11:00:00',
    status: '未开始'
  },
  {
    id: 3,
    name: '2025年秋季基本技能考试',
    subject: '基本技能',
    totalScore: 100,
    duration: 150,
    startTime: '2025-12-12 09:00:00',
    endTime: '2025-12-12 11:30:00',
    status: '未开始'
  }
])

// 模拟已完成的考试数据（三基考试科目）
const finishedExams = ref([
  {
    id: 4,
    name: '2025年春季基本理论考试',
    subject: '基本理论',
    totalScore: 100,
    obtainedScore: 85,
    startTime: '2025-12-05 09:00:00',
    endTime: '2025-12-05 10:30:00',
    status: '已结束'
  },
  {
    id: 5,
    name: '2025年春季基础知识考试',
    subject: '基础知识',
    totalScore: 100,
    obtainedScore: 90,
    startTime: '2025-12-06 09:00:00',
    endTime: '2025-12-06 11:00:00',
    status: '已结束'
  }
])

const joinExam = (exam: any) => {
  // 参加考试逻辑
  console.log('参加考试', exam)
  router.push(`/dashboard/exam-taking?examId=${exam.id}`)
}

const viewExamDetail = (examId: number) => {
  // 查看考试详情逻辑
  router.push(`/dashboard/exam-detail/${examId}`)
}

const viewErrorNotebook = () => {
  // 查看错题集逻辑
  router.push('/dashboard/error-notebook')
}
</script>

<style scoped>
.exam-center {
  padding: 20px 0;
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