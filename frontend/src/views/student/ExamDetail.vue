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

const route = useRoute()
const examId = ref(Number(route.params.id))

// 模拟考试信息
const examInfo = ref({
  id: 1,
  name: '2025年春季数学期中考试',
  subject: '数学',
  totalScore: 100,
  duration: 90,
  startTime: '2025-12-10 09:00:00',
  endTime: '2025-12-10 10:30:00',
  status: '已结束',
  description: '这是2025年春季数学期中考试，主要考察学生对高等数学基础知识的掌握情况。'
})

// 模拟题目数据
const questions = ref([
  {
    id: 1,
    title: '下列哪个是奇函数？',
    type: 1, // 选择题
    options: ['y = x²', 'y = sinx', 'y = cosx', 'y = e^x'],
    answer: 'B',
    score: 5,
    userAnswer: 'B'
  },
  {
    id: 2,
    title: '函数y = x³在x=0处可导。',
    type: 2, // 判断题
    answer: '正确',
    score: 3,
    userAnswer: '正确'
  },
  {
    id: 3,
    title: '计算定积分∫₀¹x²dx = ',
    type: 3, // 填空题
    answer: '1/3',
    score: 5,
    userAnswer: '1/2'
  }
])

const getQuestionType = (type: number) => {
  switch (type) {
    case 1: return '选择题'
    case 2: return '判断题'
    case 3: return '填空题'
    case 4: return '简答题'
    default: return '未知类型'
  }
}

onMounted(() => {
  // 根据examId获取考试详情
  console.log('获取考试详情', examId.value)
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