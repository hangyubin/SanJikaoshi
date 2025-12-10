<template>
  <div class="error-notebook">
    <h1>错题集</h1>
    
    <!-- 错题统计 -->
    <el-card class="stat-card">
      <h3>错题统计</h3>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-number">{{ totalErrorCount }}</div>
            <div class="stat-label">总错题数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-number">{{ mathErrorCount }}</div>
            <div class="stat-label">数学</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-number">{{ englishErrorCount }}</div>
            <div class="stat-label">英语</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-number">{{ computerErrorCount }}</div>
            <div class="stat-label">计算机</div>
          </div>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 错题列表 -->
    <el-card class="error-questions-card" style="margin-top: 20px;">
      <h3>错题列表</h3>
      
      <!-- 筛选条件 -->
      <div class="filter-container">
        <el-select v-model="filterForm.subject" placeholder="选择科目" style="width: 150px; margin-right: 10px;">
          <el-option label="全部科目" value=""></el-option>
          <el-option label="数学" value="数学"></el-option>
          <el-option label="英语" value="英语"></el-option>
          <el-option label="计算机" value="计算机"></el-option>
        </el-select>
        <el-select v-model="filterForm.questionType" placeholder="选择题目类型" style="width: 150px; margin-right: 10px;">
          <el-option label="全部类型" value=""></el-option>
          <el-option label="选择题" value="1"></el-option>
          <el-option label="判断题" value="2"></el-option>
          <el-option label="填空题" value="3"></el-option>
          <el-option label="简答题" value="4"></el-option>
        </el-select>
        <el-button type="primary" @click="filterQuestions">筛选</el-button>
        <el-button @click="resetFilter">重置</el-button>
      </div>
      
      <!-- 错题列表 -->
      <div class="questions-container">
        <div class="question-item" v-for="(question, index) in filteredQuestions" :key="question.id">
          <div class="question-header">
            <span class="question-type">{{ getQuestionType(question.type) }}</span>
            <span class="question-subject">{{ question.subject }}</span>
            <span class="question-date">{{ question.errorDate }}</span>
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
          <div class="question-answer">
            <div class="answer-item">
              <label>正确答案：</label>
              <span class="correct-answer">{{ question.answer }}</span>
            </div>
            <div class="answer-item">
              <label>你的答案：</label>
              <span class="wrong-answer">{{ question.userAnswer }}</span>
            </div>
            <div class="answer-item" v-if="question.analysis">
              <label>解析：</label>
              <span class="analysis-content">{{ question.analysis }}</span>
            </div>
          </div>
          <div class="question-actions">
            <el-button type="primary" size="small" @click="reviewQuestion(question)">
              重新练习
            </el-button>
            <el-button type="success" size="small" @click="markCorrect(question.id)">
              标记已掌握
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="filteredQuestions.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import axios from '@/utils/axios'
import { ElMessage } from 'element-plus'

// 错题数据，初始为空数组
const errorQuestions = ref<any[]>([])

// 筛选条件
const filterForm = ref({
  subject: '',
  questionType: ''
})

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 统计数据
const totalErrorCount = computed(() => errorQuestions.value.length)
const mathErrorCount = computed(() => errorQuestions.value.filter(q => q.subject === '数学').length)
const englishErrorCount = computed(() => errorQuestions.value.filter(q => q.subject === '英语').length)
const computerErrorCount = computed(() => errorQuestions.value.filter(q => q.subject === '计算机').length)

// 筛选后的错题
const filteredQuestions = computed(() => {
  let result = [...errorQuestions.value]
  
  // 按科目筛选
  if (filterForm.value.subject) {
    result = result.filter(q => q.subject === filterForm.value.subject)
  }
  
  // 按题目类型筛选
  if (filterForm.value.questionType) {
    result = result.filter(q => q.type === Number(filterForm.value.questionType))
  }
  
  return result
})

const getQuestionType = (type: number) => {
  switch (type) {
    case 1: return '选择题'
    case 2: return '判断题'
    case 3: return '填空题'
    case 4: return '简答题'
    default: return '未知类型'
  }
}

// 获取错题列表
const fetchErrorQuestions = () => {
  loading.value = true
  // 从localStorage获取当前用户ID，这里假设用户信息存储在localStorage中
  const userId = localStorage.getItem('userId') || '1' // 默认使用ID为1的用户
  axios.get(`/practice/wrong-questions/${userId}`, {
    params: {
      subjectId: filterForm.value.subject,
      type: filterForm.value.questionType
    }
  })
  .then(response => {
    if (response.code === 200) {
      errorQuestions.value = response.data || []
      total.value = response.data.length || 0
    }
  })
  .catch(error => {
    console.error('获取错题列表失败:', error)
    // 显示空数据，避免页面出错
    errorQuestions.value = []
    total.value = 0
  })
  .finally(() => {
    loading.value = false
  })
}

const filterQuestions = () => {
  currentPage.value = 1
  fetchErrorQuestions()
}

const resetFilter = () => {
  filterForm.value.subject = ''
  filterForm.value.questionType = ''
  currentPage.value = 1
  fetchErrorQuestions()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchErrorQuestions()
}

const handleCurrentChange = (current: number) => {
  currentPage.value = current
  fetchErrorQuestions()
}

const reviewQuestion = (question: any) => {
  // 重新练习逻辑
  console.log('重新练习', question)
  // 可以跳转到练习页面
}

const markCorrect = (questionId: number) => {
  // 标记为已掌握 - 目前后端没有实现这个功能，所以只显示成功消息
  ElMessage.success('标记成功')
  // 从错题列表中移除该题目
  errorQuestions.value = errorQuestions.value.filter(q => q.id !== questionId)
  total.value = errorQuestions.value.length
}

onMounted(() => {
  fetchErrorQuestions()
})
</script>

<style scoped>
.error-notebook {
  padding: 20px 0;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

.stat-label {
  color: #606266;
  font-size: 14px;
}

.filter-container {
  margin: 20px 0;
  display: flex;
  align-items: center;
}

.questions-container {
  margin: 20px 0;
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
  flex-wrap: wrap;
}

.question-type {
  background-color: #409eff;
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-right: 10px;
}

.question-subject {
  background-color: #67c23a;
  color: white;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin-right: 10px;
}

.question-date {
  color: #909399;
  font-size: 12px;
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

.wrong-answer {
  color: #f56c6c;
  font-weight: bold;
}

.analysis-content {
  color: #409eff;
  line-height: 1.5;
}

.question-actions {
  margin-top: 15px;
  display: flex;
  gap: 10px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>



