<template>
  <div class="practice-start">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>开始练习</h1>
      <p class="page-subtitle">认真答题，提高你的专业技能</p>
    </div>
    
    <!-- 练习信息 -->
    <el-card class="practice-info-card" shadow="hover">
      <div class="info-content">
        <el-row :gutter="30">
          <el-col :span="8">
            <div class="info-item">
              <span class="info-label">题型</span>
              <span class="info-value">{{ questionTypeLabel }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="info-label">题目数量</span>
              <span class="info-value">{{ questionCount }}题</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="info-label">练习模式</span>
              <span class="info-value">{{ modeLabel }}</span>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
    
    <!-- 题目展示区域 -->
    <el-card class="question-card" shadow="hover">
      <template v-if="loading">
        <div class="loading-container">
          <el-skeleton :rows="10" animated></el-skeleton>
        </div>
      </template>
      <template v-else-if="currentQuestion">
        <div class="question-content">
          <!-- 题目序号 -->
          <div class="question-header">
            <div class="question-number">第 {{ currentIndex + 1 }}/{{ questionCount }}题</div>
            <div class="question-type">
              <el-tag :type="questionTypeTagType">
                {{ questionTypeLabel }}
              </el-tag>
            </div>
          </div>
          
          <!-- 题干 -->
          <div class="question-title">
            {{ currentQuestion.content }}
          </div>
          
          <!-- 选项 -->
          <div class="options-container">
            <div 
              v-for="(option, key) in currentOptions" 
              :key="key"
              class="option-item"
              :class="{
                'selected': userAnswers[currentIndex] === key,
                'correct': showResult && correctOptions.includes(key),
                'incorrect': showResult && userAnswers[currentIndex] === key && !correctOptions.includes(key)
              }"
              @click="selectOption(key)"
            >
              <div class="option-letter">{{ key }}</div>
              <div class="option-text">{{ option }}</div>
              <div v-if="showResult" class="option-result">
                <el-icon v-if="correctOptions.includes(key)"><CircleCheck /></el-icon>
                <el-icon v-else-if="userAnswers[currentIndex] === key"><CircleClose /></el-icon>
              </div>
            </div>
          </div>
          
          <!-- 解析 -->
          <div v-if="showResult && currentQuestion.analysis" class="analysis-section">
            <h4>解析：</h4>
            <p>{{ currentQuestion.analysis }}</p>
          </div>
        </div>
      </template>
      <template v-else>
        <div class="no-questions">
          <el-icon class="empty-icon"><DocumentRemove /></el-icon>
          <p>暂无题目</p>
        </div>
      </template>
    </el-card>
    
    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button 
        type="default" 
        @click="prevQuestion"
        :disabled="loading || currentIndex === 0"
      >
        上一题
      </el-button>
      
      <el-button 
          type="info" 
          @click="showResult = !showResult"
        >
          <el-icon v-if="!showResult"><HelpFilled /></el-icon>
          <el-icon v-else><Hide /></el-icon>
          {{ showResult ? '隐藏解析' : '查看解析' }}
        </el-button>
      
      <el-button 
        type="primary" 
        @click="nextQuestion"
        :loading="loading"
        :disabled="loading"
      >
        {{ isLastQuestion ? '提交练习' : '下一题' }}
      </el-button>
    </div>
    
    <!-- 练习结果 -->
    <el-dialog v-model="showResultDialog" title="练习结果" width="800px" destroy-on-close>
      <div class="result-content">
        <div class="result-stats">
          <div class="stat-item">
            <h3>{{ totalScore }}</h3>
            <p>得分</p>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <h3>{{ correctCount }}</h3>
            <p>答对题数</p>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <h3>{{ questionCount }}</h3>
            <p>总题数</p>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <h3>{{ score }}%</h3>
            <p>正确率</p>
          </div>
        </div>
        
        <div class="result-actions">
          <el-button type="primary" @click="restartPractice">重新练习</el-button>
          <el-button @click="backToPracticeCenter">返回练习中心</el-button>
          <el-button type="info" @click="viewErrorNotebook">查看错题集</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { 
  CircleCheck, CircleClose, 
  HelpFilled, Hide, DocumentRemove 
} from '@element-plus/icons-vue'
import axios from '@/utils/axios'

const router = useRouter()
const route = useRoute()

// 从路由参数获取练习配置
const questionType = parseInt(route.query.questionType as string) || 1
const questionCount = parseInt(route.query.questionCount as string) || 10
const mode = parseInt(route.query.mode as string) || 1

// 加载状态
const loading = ref(true)
// 题目列表
const questions = ref<any[]>([])
// 当前题目索引
const currentIndex = ref(0)
// 用户答案
const userAnswers = ref<string[]>([])
// 是否显示答案解析
const showResult = ref(false)
// 是否显示练习结果
const showResultDialog = ref(false)

// 获取题型标签
const questionTypeLabel = computed(() => {
  const labels: Record<number, string> = {
    1: '单选题',
    2: '多选题',
    3: '是非题'
  }
  return labels[questionType] || '未知题型'
})

// 获取题型标签类型
const questionTypeTagType = computed(() => {
  const types: Record<number, string> = {
    1: 'primary',
    2: 'success',
    3: 'warning'
  }
  return types[questionType] || 'info'
})

// 获取练习模式标签
const modeLabel = computed(() => {
  return mode === 1 ? '顺序练习' : '随机练习'
})

// 获取当前题目
const currentQuestion = computed(() => {
  return questions.value[currentIndex.value]
})

// 格式化选项
const currentOptions = computed(() => {
  if (!currentQuestion.value) return {}
  
  const options: Record<string, string> = {}
  const question = currentQuestion.value
  
  // 适配多种选项格式
  if (question.options) {
    try {
      // 尝试解析JSON格式
      const parsedOptions = JSON.parse(question.options)
      
      if (typeof parsedOptions === 'object') {
        // 处理对象格式的选项
        if (Array.isArray(parsedOptions)) {
          // 如果是数组格式，如["选项1", "选项2"]
          parsedOptions.forEach((option, index) => {
            if (typeof option === 'string' && option.trim()) {
              const key = String.fromCharCode(65 + index)
              options[key] = option.trim()
            }
          })
        } else {
          // 如果是对象格式，如{A: "选项1", B: "选项2"}
          Object.entries(parsedOptions).forEach(([key, value]) => {
            if (typeof value === 'string') {
              options[key.toUpperCase()] = value
            }
          })
        }
      } else if (typeof parsedOptions === 'string') {
        // 处理字符串格式
        processStringOptions(parsedOptions, options)
      }
    } catch (e) {
      // 直接处理字符串格式
      processStringOptions(question.options, options)
    }
  } 
  
  // 处理独立选项字段
  if (!Object.keys(options).length) {
    // 兼容旧格式：optionA、optionB等
    for (let i = 0; i < 6; i++) {
      const key = String.fromCharCode(65 + i)
      const optionKey = `option${key}`
      const optionValue = question[optionKey]
      if (optionValue && typeof optionValue === 'string' && optionValue.trim()) {
        options[key] = optionValue.trim()
      }
    }
  }
  
  // 处理直接的选项字段（如A、B、C、D）
  if (!Object.keys(options).length) {
    for (let i = 0; i < 6; i++) {
      const key = String.fromCharCode(65 + i)
      const optionValue = question[key]
      if (optionValue && typeof optionValue === 'string' && optionValue.trim()) {
        options[key] = optionValue.trim()
      }
    }
  }
  
  return options
})

// 处理字符串格式的选项
const processStringOptions = (optionsString: string, optionsObj: Record<string, string>) => {
  // 支持多种分隔符：; 、\n 、\r\n 、| 等
  const separators = [';', '、', '\n', '\r\n', '|']
  let bestSeparator = ';'
  let maxParts = 1
  
  // 找到最佳分隔符
  separators.forEach(sep => {
    const parts = optionsString.split(sep)
    if (parts.length > maxParts) {
      maxParts = parts.length
      bestSeparator = sep
    }
  })
  
  // 使用最佳分隔符分割
  const optionArray = optionsString.split(bestSeparator)
  
  optionArray.forEach((option, index) => {
    if (typeof option === 'string') {
      const trimmedOption = option.trim()
      if (trimmedOption) {
        const key = String.fromCharCode(65 + index)
        optionsObj[key] = trimmedOption
      }
    }
  })
}

// 获取正确答案
const correctOptions = computed(() => {
  if (!currentQuestion.value) return []
  const question = currentQuestion.value
  
  // 获取正确答案，适配不同字段名
  let correctAnswer = question.correctAnswer || question.answer
  
  if (Array.isArray(correctAnswer)) {
    return correctAnswer
  } else if (typeof correctAnswer === 'string') {
    return questionType === 2 ? correctAnswer.split(',') : [correctAnswer]
  }
  return []
})

// 是否是最后一题
const isLastQuestion = computed(() => {
  return currentIndex.value === questionCount - 1
})

// 计算答对题数
const correctCount = computed(() => {
  let count = 0
  for (let i = 0; i < questions.value.length; i++) {
    const question = questions.value[i]
    let correct: string[] = []
    
    // 获取正确答案，适配不同字段名
    const correctAnswer = question.correctAnswer || question.answer
    
    if (Array.isArray(correctAnswer)) {
      correct = correctAnswer
    } else if (typeof correctAnswer === 'string') {
      correct = questionType === 2 ? correctAnswer.split(',') : [correctAnswer]
    }
    
    const userAnswer = userAnswers.value[i]
    if (questionType === 2) {
      // 多选题，需要全部答对才算正确
      const userAnswersArray = typeof userAnswer === 'string' ? userAnswer.split(',') : []
      if (correct.length === userAnswersArray.length && correct.every(val => userAnswersArray.includes(val))) {
        count++
      }
    } else {
      // 单选题或是非题
      if (userAnswer === correct[0]) {
        count++
      }
    }
  }
  return count
})

// 计算正确率
const score = computed(() => {
  return Math.round((correctCount.value / questionCount) * 100)
})

// 计算总得分，总分100分，根据题目数量动态计算每题分值
const totalScore = computed(() => {
  // 总分100分，根据题目数量动态计算每题分值
  const perQuestionScore = questionCount > 0 ? 100 / questionCount : 0
  // 计算总得分，保留一位小数
  return Math.round(correctCount.value * perQuestionScore * 10) / 10
})

// 前端题型到后端题型的映射
const mapFrontendTypeToBackendType = (frontendType: number): number => {
  // 简化题型映射，直接返回前端题型
  // 前端使用1:单选题, 2:多选题, 3:是非题
  return frontendType
}

// 获取练习题
const fetchPracticeQuestions = async () => {
  try {
    loading.value = true
    
    // 前端题型转换为后端题型
    const backendQuestionType = mapFrontendTypeToBackendType(questionType)
    
    // 尝试多种参数组合，确保兼容性
    let questionsData: any[] = []
    let responseData: any = null
    
    console.log(`尝试获取练习题，题型: ${backendQuestionType}, 数量: ${questionCount}`)
    
    // 尝试多种API端点和参数组合
    const apiEndpoints = [
      // 标准RESTful端点
      { url: '/questions', params: { page: 1, pageSize: questionCount, type: backendQuestionType } },
      // 使用pageNum参数
      { url: '/questions', params: { pageNum: 1, pageSize: questionCount, type: backendQuestionType } },
      // 不使用分页
      { url: '/questions', params: { type: backendQuestionType } },
      // 使用/api前缀
      { url: '/api/questions', params: { page: 1, pageSize: questionCount, type: backendQuestionType } },
      // 使用/api前缀，不使用分页
      { url: '/api/questions', params: { type: backendQuestionType } },
      // 练习专用端点
      { url: '/practice/questions', params: { type: backendQuestionType, count: questionCount } },
      // 直接获取所有题目
      { url: '/questions/all', params: {} }
    ]
    
    // 尝试所有端点，直到成功获取题目
    for (const endpoint of apiEndpoints) {
      try {
        console.log(`尝试API端点: ${endpoint.url}，参数:`, endpoint.params)
        const response = await axios.get(endpoint.url, { params: endpoint.params })
        responseData = response.data
        console.log(`API响应:`, responseData)
        
        // 处理不同的返回格式
        if (responseData.records) {
          questionsData = responseData.records
          console.log(`从records字段获取到 ${questionsData.length} 道题目`)
        } else if (Array.isArray(responseData)) {
          questionsData = responseData
          console.log(`直接从响应获取到 ${questionsData.length} 道题目`)
        } else if (responseData.data) {
          questionsData = responseData.data
          console.log(`从data字段获取到 ${questionsData.length} 道题目`)
        } else if (responseData.list) {
          questionsData = responseData.list
          console.log(`从list字段获取到 ${questionsData.length} 道题目`)
        }
        
        if (questionsData.length > 0) {
          console.log(`成功获取题目，跳出循环`)
          break
        }
      } catch (e: any) {
        console.log(`API端点 ${endpoint.url} 失败:`, e.message)
        continue
      }
    }
    
    // 限制题目数量
    questions.value = questionsData.slice(0, questionCount)
    
    // 初始化用户答案数组
    userAnswers.value = new Array(questions.value.length).fill('')
    
    // 如果没有获取到题目，显示提示信息
    if (questions.value.length === 0) {
      ElMessage.warning('暂无相关题型的练习题，请联系管理员添加')
      console.log('未获取到任何题目')
    } else {
      console.log(`成功获取 ${questions.value.length} 道题目`)
      // 打印题目详情，便于调试
      questions.value.forEach((q, index) => {
        console.log(`题目 ${index + 1}:`, q.content)
        console.log(`选项:`, q.options)
        console.log(`正确答案:`, q.answer || q.correctAnswer)
      })
    }  
  } catch (error: any) {
    console.error('获取练习题失败:', error)
    ElMessage.error(`获取练习题失败：${error.message || '请检查网络连接或联系管理员'}`)
    questions.value = []
    userAnswers.value = []
  } finally {
    loading.value = false
  }
}

// 选择选项
const selectOption = (optionKey: string) => {
  if (showResult.value) return
  
  if (questionType === 2) {
    // 多选题，支持多选
    let currentAnswer = userAnswers.value[currentIndex.value]
    let answersArray = currentAnswer ? currentAnswer.split(',') : []
    
    if (answersArray.includes(optionKey)) {
      // 取消选择
      answersArray = answersArray.filter(key => key !== optionKey)
    } else {
      // 添加选择
      answersArray.push(optionKey)
    }
    
    userAnswers.value[currentIndex.value] = answersArray.join(',')
  } else {
    // 单选题或是非题，只能选择一个
    userAnswers.value[currentIndex.value] = optionKey
    
    // 直接显示结果
    showResult.value = true
    
    // 检查是否答对
    const isCorrect = userAnswers.value[currentIndex.value] === correctOptions.value[0]
    if (!isCorrect) {
      ElMessage.warning('答错了，看看解析吧！')
    }
  }
}

// 上一题
const prevQuestion = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
    showResult.value = false
  }
}

// 下一题
const nextQuestion = async () => {
  // 允许跳过未答题
  if (!userAnswers.value[currentIndex.value] && !showResult.value) {
    // 提示用户，但允许继续
    ElMessage.warning('您还未选择答案，是否继续？')
    // 继续执行，不阻止
  }
  
  if (isLastQuestion.value) {
    // 最后一题，提交练习
    await submitPractice()
  } else {
    // 下一题
    currentIndex.value++
    showResult.value = false
  }
}

// 提交练习
const submitPractice = async () => {
  try {
    loading.value = true
    
    // 计算练习结果
    const practiceResult = {
      questions: questions.value.map((question, index) => ({
        questionId: question.id,
        userAnswer: userAnswers.value[index],
        correctAnswer: question.answer,
        isCorrect: userAnswers.value[index] === question.answer
      })),
      correctCount: correctCount.value,
      totalCount: questionCount,
      score: score.value,
      totalScore: totalScore.value
    }
    
    // 收集错题信息
    const wrongQuestions = practiceResult.questions
      .filter(item => !item.isCorrect)
      .map(item => {
        const originalQuestion = questions.value.find(q => q.id === item.questionId)
        return {
          questionId: item.questionId,
          content: originalQuestion?.content || '',
          options: originalQuestion?.options || '',
          correctAnswer: item.correctAnswer,
          userAnswer: item.userAnswer,
          analysis: originalQuestion?.analysis || '',
          type: originalQuestion?.type || 1,
          subject: originalQuestion?.subject || { id: 1, name: '默认科目' }
        }
      })
    
    // 提交练习结果和错题信息
    try {
      // 尝试提交练习结果
      await axios.post('/practice/result', practiceResult)
      
      // 如果有错题，提交到错题集
      if (wrongQuestions.length > 0) {
        await axios.post('/practice/wrong-questions', wrongQuestions)
      }
      
      console.log('练习结果提交成功')
    } catch (apiError) {
      console.warn('提交练习结果到后端失败，可能后端未实现该功能:', apiError)
      // 继续执行，不影响前端显示
    }
    
    // 显示练习结果
    showResultDialog.value = true
  } catch (error) {
    console.error('提交练习结果失败:', error)
    ElMessage.error('提交练习结果失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 重新练习
const restartPractice = () => {
  currentIndex.value = 0
  userAnswers.value = new Array(questions.value.length).fill('')
  showResult.value = false
  showResultDialog.value = false
  fetchPracticeQuestions()
}

// 返回练习中心
const backToPracticeCenter = () => {
  router.push('/dashboard/practice')
}

// 查看错题集
const viewErrorNotebook = () => {
  router.push('/dashboard/error-notebook')
}

// 组件挂载时获取练习题
onMounted(() => {
  fetchPracticeQuestions()
})
</script>

<style scoped>
.practice-start {
  padding: 20px 0;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

/* 页面标题 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 40px;
  color: white;
  margin-bottom: 30px;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.page-header h1 {
  margin: 0 0 15px 0;
  font-size: 36px;
  font-weight: bold;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.page-subtitle {
  font-size: 18px;
  opacity: 0.95;
  margin: 0;
}

/* 卡片样式 */
.practice-info-card,
.question-card {
  margin-bottom: 25px;
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  background: white;
  overflow: hidden;
  position: relative;
}

.practice-info-card::before,
.question-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2);
}

/* 练习信息卡片 */
.info-content {
  padding: 30px;
}

.info-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: #fafafa;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.info-item:hover {
  background: #ecf5ff;
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.15);
}

.info-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
  font-weight: 500;
}

.info-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

/* 题目卡片 */
.question-content {
  padding: 30px;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.question-number {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.question-title {
  font-size: 18px;
  line-height: 1.6;
  color: #303133;
  margin-bottom: 30px;
  font-weight: 500;
}

/* 选项容器 */
.options-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 30px;
}

.option-item {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #fafafa;
  border: 2px solid transparent;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.option-item:hover {
  background: #ecf5ff;
  border-color: #667eea;
  transform: translateX(5px);
}

.option-item.selected {
  background: #ecf5ff;
  border-color: #667eea;
}

.option-item.correct {
  background: #f0f9eb;
  border-color: #67c23a;
}

.option-item.incorrect {
  background: #fef0f0;
  border-color: #f56c6c;
}

.option-letter {
  width: 32px;
  height: 32px;
  background: #667eea;
  color: white;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: bold;
  margin-right: 15px;
  flex-shrink: 0;
}

.option-item.correct .option-letter {
  background: #67c23a;
}

.option-item.incorrect .option-letter {
  background: #f56c6c;
}

.option-text {
  flex: 1;
  font-size: 16px;
  line-height: 1.5;
  color: #303133;
}

.option-result {
  margin-left: 15px;
  font-size: 20px;
}

.option-item.correct .option-result {
  color: #67c23a;
}

.option-item.incorrect .option-result {
  color: #f56c6c;
}

/* 解析区域 */
.analysis-section {
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
  border-radius: 12px;
  padding: 20px;
  margin-top: 25px;
}

.analysis-section h4 {
  margin: 0 0 15px 0;
  color: #67c23a;
  font-size: 16px;
  font-weight: bold;
}

.analysis-section p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 30px;
}

/* 练习结果对话框 */
.result-content {
  padding: 20px;
}

.result-stats {
  display: flex;
  justify-content: space-around;
  align-items: center;
  margin-bottom: 30px;
  padding: 30px;
  background: #fafafa;
  border-radius: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.stat-item h3 {
  margin: 0;
  font-size: 36px;
  font-weight: bold;
  color: #667eea;
}

.stat-item p {
  margin: 0;
  font-size: 16px;
  color: #606266;
}

.stat-divider {
  width: 2px;
  height: 80px;
  background: #e4e7ed;
}

.result-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
}

/* 无题目状态 */
.no-questions {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #909399;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

/* 加载状态 */
.loading-container {
  padding: 30px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .practice-start {
    padding: 10px 0;
  }
  
  .page-header {
    padding: 25px;
    margin-bottom: 20px;
  }
  
  .page-header h1 {
    font-size: 28px;
  }
  
  .question-content {
    padding: 20px;
  }
  
  .question-title {
    font-size: 16px;
  }
  
  .option-item {
    padding: 15px;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 10px;
  }
  
  .result-stats {
    flex-direction: column;
    gap: 20px;
    padding: 20px;
  }
  
  .stat-divider {
    width: 80%;
    height: 2px;
  }
}
</style>