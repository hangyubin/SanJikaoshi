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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/utils/axios'

const router = useRouter()

// 可参加的考试数据，初始为空数组
const examList = ref<any[]>([])

// 已完成的考试数据，初始为空数组
const finishedExams = ref<any[]>([])

const loading = ref(false)

// 获取可参加的考试列表
const fetchAvailableExams = () => {
  loading.value = true
  // 从localStorage获取当前用户ID，这里假设用户信息存储在localStorage中
  const userId = localStorage.getItem('userId') || '1' // 默认使用ID为1的用户
  axios.get(`/exam/available/${userId}`)
    .then(res => {
      examList.value = res.data || []
    })
    .catch(error => {
      console.error('获取可参加的考试列表失败:', error)
      // 显示空数据，避免页面出错
      examList.value = []
    })
    .finally(() => {
      loading.value = false
    })
}

// 获取已完成的考试列表
const fetchFinishedExams = () => {
  loading.value = true
  // 从localStorage获取当前用户ID，这里假设用户信息存储在localStorage中
  const userId = localStorage.getItem('userId') || '1' // 默认使用ID为1的用户
  axios.get(`/exam/user/${userId}`)
    .then(res => {
      finishedExams.value = res.data || []
    })
    .catch(error => {
      console.error('获取已完成的考试列表失败:', error)
      // 显示空数据，避免页面出错
      finishedExams.value = []
    })
    .finally(() => {
      loading.value = false
    })
}

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

onMounted(() => {
  fetchAvailableExams()
  fetchFinishedExams()
})
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



