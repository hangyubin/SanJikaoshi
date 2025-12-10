<template>
  <div class="exam-detail">
    <h1>考试详情</h1>
    
    <!-- 考试基本信息 -->
    <el-card class="exam-info-card">
      <h3>考试信息</h3>
      <el-row :gutter="20">
        <el-col :span="8">
          <div class="info-item">
            <label>考试名称：</label>
            <span>{{ examInfo.name }}</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <label>科目：</label>
            <span>{{ examInfo.subject }}</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <label>总分：</label>
            <span>{{ examInfo.totalScore }}</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <label>时长：</label>
            <span>{{ examInfo.duration }}分钟</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <label>开始时间：</label>
            <span>{{ examInfo.startTime }}</span>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="info-item">
            <label>结束时间：</label>
            <span>{{ examInfo.endTime }}</span>
          </div>
        </el-col>
        <el-col :span="24">
          <div class="info-item">
            <label>考试描述：</label>
            <span>{{ examInfo.description }}</span>
          </div>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 试卷题目 -->
    <el-card class="exam-paper-card" style="margin-top: 20px;">
      <h3>试卷题目</h3>
      <div class="questions-container">
        <div class="question-item" v-for="(question, index) in questions" :key="question.id">
          <div class="question-header">
            <span class="question-type">{{ getQuestionType(question.type) }}</span>
            <span class="question-score">({{ question.score }}分)</span>
          </div>
          <div class="question-content">
            <p class="question-title">{{ index + 1 }}. {{ question.title }}</p>
            <div class="question-options" v-if="question.type === 1">
              <div v-for="(option, optIndex) in question.options" :key="optIndex">
                <el-radio :label="optIndex + 1" disabled>
                  {{ String.fromCharCode(65 + optIndex) }}. {{ option }}
                </el-radio>
              </div>
            </div>
            <div class="question-options" v-else-if="question.type === 2">
              <el-radio-group disabled>
                <el-radio label="1">正确</el-radio>
                <el-radio label="0">错误</el-radio>
              </el-radio-group>
            </div>
          </div>
          <div class="question-answer" v-if="examInfo.status === '已结束'">
            <div class="answer-item">
              <label>正确答案：</label>
              <span class="correct-answer">{{ question.answer }}</span>
            </div>
            <div class="answer-item" v-if="question.userAnswer">
              <label>你的答案：</label>
              <span :class="question.userAnswer === question.answer ? 'correct' : 'wrong'">{{ question.userAnswer }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from '@/utils/axios'
import { ElMessage } from 'element-plus'

const route = useRoute()
const examId = ref(Number(route.params.id))

// 考试信息，初始为空
const examInfo = ref({
  id: 0,
  name: '',
  subject: '',
  totalScore: 0,
  duration: 0,
  startTime: '',
  endTime: '',
  status: '',
  description: ''
})

// 题目数据，初始为空数组
const questions = ref<any[]>([])

// 加载状态
const loading = ref(false)

const getQuestionType = (type: number) => {
  switch (type) {
    case 1: return '选择题'
    case 2: return '判断题'
    case 3: return '填空题'
    case 4: return '简答题'
    default: return '未知类型'
  }
}

// 获取考试详情
const fetchExamDetail = () => {
  loading.value = true
  axios.get(`/exams/${examId.value}`)
    .then(res => {
      examInfo.value = res.examInfo || examInfo.value
      questions.value = res.questions || []
    })
    .catch(error => {
      console.error('获取考试详情失败:', error)
      ElMessage.error('获取考试详情失败')
    })
    .finally(() => {
      loading.value = false
    })
}

onMounted(() => {
  fetchExamDetail()
})
</script>

<style scoped>
.exam-detail {
  padding: 20px 0;
}

.exam-info-card,
.exam-paper-card {
  margin-bottom: 20px;
}

.info-item {
  margin-bottom: 10px;
}

.info-item label {
  font-weight: bold;
  margin-right: 10px;
}

.questions-container {
  margin-top: 20px;
}

.question-item {
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.question-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.question-type {
  background-color: #409eff;
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-right: 10px;
}

.question-score {
  color: #f56c6c;
  font-weight: bold;
}

.question-title {
  margin: 0 0 15px 0;
  font-size: 16px;
}

.question-options {
  margin-left: 20px;
}

.question-options div {
  margin-bottom: 8px;
}

.question-answer {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #e4e7ed;
}

.answer-item {
  margin-bottom: 8px;
}

.answer-item label {
  font-weight: bold;
  margin-right: 10px;
}

.correct-answer {
  color: #67c23a;
  font-weight: bold;
}

.correct {
  color: #67c23a;
  font-weight: bold;
}

.wrong {
  color: #f56c6c;
  font-weight: bold;
}
</style>



