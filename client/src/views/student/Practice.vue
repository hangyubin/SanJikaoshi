<template>
  <div class="practice">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>练习中心</h1>
      <p class="page-subtitle">选择题型，设置练习参数，开始你的学习之旅</p>
    </div>
    
    <!-- 题型选择 -->
    <el-card class="subject-select-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h3>题型练习</h3>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card 
            class="subject-item" 
            @click="selectQuestionType(1)"
            :class="{ 'selected': selectedQuestionType === 1 }"
            shadow="hover"
          >
            <div class="subject-content">
              <div class="subject-header">
                <h4>单选题</h4>
                <el-tag size="small" type="success">{{ questionTypeCount[1] || 0 }}题</el-tag>
              </div>
              <p class="subject-description">从多个选项中选择一个正确答案</p>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card 
            class="subject-item" 
            @click="selectQuestionType(2)"
            :class="{ 'selected': selectedQuestionType === 2 }"
            shadow="hover"
          >
            <div class="subject-content">
              <div class="subject-header">
                <h4>多选题</h4>
                <el-tag size="small" type="success">{{ questionTypeCount[2] || 0 }}题</el-tag>
              </div>
              <p class="subject-description">从多个选项中选择所有正确答案</p>
            </div>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card 
            class="subject-item" 
            @click="selectQuestionType(3)"
            :class="{ 'selected': selectedQuestionType === 3 }"
            shadow="hover"
          >
            <div class="subject-content">
              <div class="subject-header">
                <h4>是非题</h4>
                <el-tag size="small" type="success">{{ questionTypeCount[3] || 0 }}题</el-tag>
              </div>
              <p class="subject-description">判断陈述是否正确</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 练习设置 -->
    <el-card class="practice-setting-card" v-if="selectedQuestionType" shadow="hover">
      <template #header>
        <div class="card-header">
          <h3>练习设置</h3>
          <div class="selected-subject-info">
            <el-tag type="primary">{{ getQuestionTypeLabel(selectedQuestionType!) }}</el-tag>
            <span class="subject-question-count">{{ questionTypeCount[selectedQuestionType!] || 0 }}题</span>
          </div>
        </div>
      </template>
      <el-form :model="practiceSetting" label-width="120px" class="setting-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="题目数量">
              <el-input-number 
                v-model="practiceSetting.questionCount" 
                :min="5" 
                :max="Math.min(50, questionTypeCount[selectedQuestionType!] || 50)" 
                :step="5"
                size="large"
              ></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="练习模式">
              <el-radio-group v-model="practiceSetting.mode" size="large">
                <el-radio :value="1">
                  <el-icon><Menu /></el-icon>
                  顺序练习
                </el-radio>
                <el-radio :value="2">
                  <el-icon><RefreshRight /></el-icon>
                  随机练习
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <div class="form-actions">
          <el-button type="primary" size="large" @click="startPractice">
              <el-icon><Star /></el-icon>
              开始练习
            </el-button>
          <el-button size="large" @click="resetSetting">
            <el-icon><RefreshRight /></el-icon>
            重置设置
          </el-button>
          <el-button size="large" @click="clearQuestionTypeSelection">
            <el-icon><Delete /></el-icon>
            重新选择题型
          </el-button>
        </div>
      </el-form>
    </el-card>
    
    <!-- 练习提示 -->
    <el-card class="practice-tips-card" shadow="hover" v-else>
      <template #header>
        <h3>练习提示</h3>
      </template>
      <div class="tips-content">
        <el-timeline>
          <el-timeline-item type="primary" timestamp="第一步">
            从上方选择一个练习题型
          </el-timeline-item>
          <el-timeline-item type="success" timestamp="第二步">
            设置练习参数，如题目数量、练习模式等
          </el-timeline-item>
          <el-timeline-item type="warning" timestamp="第三步">
            点击「开始练习」按钮，进入练习界面
          </el-timeline-item>
          <el-timeline-item type="info" timestamp="第四步">
            完成练习后，查看练习报告和错题分析
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  RefreshRight, Star, Menu,
  Delete
} from '@element-plus/icons-vue'
import axios from '@/utils/axios'

const router = useRouter()

// 题型选择
const selectedQuestionType = ref<number | null>(null)
// 各题型的题目数量
const questionTypeCount = ref<Record<number, number>>({
  1: 100, // 单选题数量
  2: 80,  // 多选题数量
  3: 50   // 是非题数量
})

// 练习设置，简化版本
const practiceSetting = reactive({
  questionCount: 10,
  mode: 1
})

// 获取题型标签
const getQuestionTypeLabel = (type: number) => {
  const labels: Record<number, string> = {
    1: '单选题',
    2: '多选题',
    3: '是非题'
  }
  return labels[type] || '未知题型'
}

// 选择题型
const selectQuestionType = (type: number) => {
  selectedQuestionType.value = type
  // 重置练习设置
  resetSetting()
}

// 开始练习
const startPractice = () => {
  if (!selectedQuestionType.value) {
    return
  }
  
  // 开始练习逻辑
  console.log('开始练习', selectedQuestionType.value, practiceSetting)
  // 跳转到练习页面
  router.push({
    path: '/dashboard/practice/start',
    query: {
      questionType: selectedQuestionType.value,
      questionCount: practiceSetting.questionCount,
      mode: practiceSetting.mode
    }
  })
}

// 重置设置
const resetSetting = () => {
  practiceSetting.questionCount = 10
  practiceSetting.mode = 1
}

// 清除题型选择
const clearQuestionTypeSelection = () => {
  selectedQuestionType.value = null
  resetSetting()
}

// 初始化数据
onMounted(async () => {
  try {
    // 从API获取各题型的题目数量
    const response = await axios.get('/questions/count')
    const countData = response.data
    // 确保countData是对象格式
    if (typeof countData === 'object' && countData !== null) {
      questionTypeCount.value = countData
    } else {
      // 处理无效数据，设置默认值
      questionTypeCount.value = {
        1: 0, // 单选题数量
        2: 0,  // 多选题数量
        3: 0   // 是非题数量
      }
    }
  } catch (error) {
    console.error('获取题型数量失败:', error)
    // 发生错误时，使用默认值
    questionTypeCount.value = {
      1: 0, // 单选题数量
      2: 0,  // 多选题数量
      3: 0   // 是非题数量
    }
  }
})
</script>

<style scoped>
.practice {
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
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.page-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #ffffff, transparent);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.page-header:hover {
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.4);
  transform: translateY(-2px);
}

.page-header h1 {
  margin: 0 0 15px 0;
  font-size: 36px;
  font-weight: bold;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  animation: fadeInUp 0.6s ease;
}

.page-subtitle {
  font-size: 18px;
  opacity: 0.95;
  margin: 0;
  animation: fadeInUp 0.6s ease 0.2s both;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 卡片样式 */
.subject-select-card,
.practice-setting-card,
.practice-tips-card {
  margin-bottom: 25px;
  border-radius: 16px;
  border: none;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  background: white;
  overflow: hidden;
  position: relative;
}

.subject-select-card::before,
.practice-setting-card::before,
.practice-tips-card::before {
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

.subject-select-card:hover::before,
.practice-setting-card:hover::before,
.practice-tips-card:hover::before {
  transform: scaleX(1);
}

.subject-select-card:hover,
.practice-setting-card:hover,
.practice-tips-card:hover {
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  transform: translateY(-8px);
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  margin-bottom: 20px;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.card-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-header h3::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 20px;
  background: linear-gradient(180deg, #667eea, #764ba2);
  border-radius: 2px;
}

.selected-subject-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.subject-question-count {
  color: #606266;
  font-size: 14px;
  font-weight: 500;
}

/* 科目卡片 */
.subject-item {
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  border-radius: 12px;
  border: 2px solid transparent;
  margin-bottom: 20px;
  height: 200px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  background: white;
}

.subject-item:hover {
  transform: translateY(-10px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  border-color: #667eea;
}

.subject-item.selected {
  border-color: #667eea;
  background-color: #ecf5ff;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.2);
}

.subject-item.selected::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  clip-path: polygon(100% 0, 0 0, 100% 100%);
  z-index: 1;
}

.subject-item.selected::after {
  content: '✓';
  position: absolute;
  top: 8px;
  right: 8px;
  color: white;
  font-size: 18px;
  font-weight: bold;
  z-index: 2;
}

.subject-content {
  padding: 25px;
  height: 100%;
  display: flex;
  flex-direction: column;
  position: relative;
}

.subject-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.subject-header h4 {
  margin: 0;
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  transition: color 0.3s ease;
}

.subject-item:hover .subject-header h4 {
  color: #667eea;
}

.subject-description {
  flex: 1;
  font-size: 14px;
  color: #606266;
  margin-bottom: 20px;
  line-height: 1.6;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.subject-stats {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: auto;
}

.subject-stats .stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #606266;
  font-weight: 500;
  transition: all 0.3s ease;
}

.subject-item:hover .subject-stats .stat-item {
  color: #303133;
}

.subject-stats .stat-item .el-icon {
  color: #667eea;
  font-weight: bold;
}

/* 练习设置表单 */
.setting-form {
  padding: 0 30px 30px;
}

.form-actions {
  display: flex;
  justify-content: flex-start;
  gap: 15px;
  margin-top: 40px;
  padding-top: 30px;
  border-top: 1px solid #f0f0f0;
  flex-wrap: wrap;
}

/* 练习提示卡片 */
.practice-tips-card {
  margin-top: 20px;
}

.tips-content {
  padding: 0 30px 30px;
}

:deep(.el-timeline) {
  margin: 0;
  padding: 10px 0;
}

:deep(.el-timeline-item) {
  padding: 0;
  margin-bottom: 25px;
  transition: all 0.3s ease;
}

:deep(.el-timeline-item:hover) {
  transform: translateX(5px);
}

:deep(.el-timeline-item__timestamp) {
  font-weight: 600;
  color: #303133;
  margin-right: 20px;
  font-size: 14px;
  padding: 4px 12px;
  background: #f5f7fa;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

:deep(.el-timeline-item__content) {
  padding: 15px 20px;
  color: #606266;
  background: #fafafa;
  border-radius: 8px;
  border-left: 3px solid #667eea;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

:deep(.el-timeline-item:hover .el-timeline-item__content) {
  background: #ecf5ff;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
}

/* 按钮样式 */
:deep(.el-button--large) {
  border-radius: 10px;
  padding: 14px 32px;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

:deep(.el-button--large:hover) {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

:deep(.el-button--primary:hover) {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: translateY(-3px);
}

:deep(.el-button--small) {
  border-radius: 6px;
  transition: all 0.3s ease;
  font-weight: 500;
}

:deep(.el-button--small:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 表单元素样式 */
:deep(.el-select--large),
:deep(.el-input-number--large),
:deep(.el-textarea--large) {
  border-radius: 10px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #e4e7ed;
}

:deep(.el-select--large:hover),
:deep(.el-input-number--large:hover),
:deep(.el-textarea--large:hover),
:deep(.el-select--large:focus),
:deep(.el-input-number--large:focus),
:deep(.el-textarea--large:focus) {
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
  border-color: #667eea;
}

:deep(.el-select__wrapper),
:deep(.el-input__wrapper),
:deep(.el-textarea__inner) {
  border-radius: 10px;
}

:deep(.el-form-item__label) {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

:deep(.el-radio-group) {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

:deep(.el-radio) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
  background: #fafafa;
  border: 1px solid #e4e7ed;
}

:deep(.el-radio:hover) {
  background: #ecf5ff;
  border-color: #667eea;
}

:deep(.el-radio.is-checked) {
  background: #ecf5ff;
  border-color: #667eea;
}

:deep(.el-radio.is-checked .el-radio__label) {
  color: #667eea;
  font-weight: 600;
}

:deep(.el-radio .el-radio__inner) {
  border-color: #667eea;
}

:deep(.el-radio.is-checked .el-radio__inner) {
  background-color: #667eea;
  border-color: #667eea;
}

:deep(.el-radio__label) {
  font-weight: 500;
  color: #303133;
  transition: color 0.3s ease;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .subject-item {
    height: auto;
    min-height: 180px;
  }
}

@media (max-width: 768px) {
  .practice {
    padding: 10px 0;
  }
  
  .page-header {
    padding: 25px;
    margin-bottom: 20px;
  }
  
  .page-header h1 {
    font-size: 28px;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
    padding: 20px;
  }
  
  .selected-subject-info {
    width: 100%;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 10px;
  }
  
  .form-actions {
    flex-direction: column;
    gap: 10px;
  }
  
  :deep(.el-radio-group) {
    flex-direction: column;
    gap: 10px;
  }
  
  :deep(.el-radio) {
    width: 100%;
  }
  
  .el-col {
    margin-bottom: 20px;
  }
  
  .setting-form {
    padding: 0 20px 20px;
  }
  
  .tips-content {
    padding: 0 20px 20px;
  }
  
  .subject-content {
    padding: 20px;
  }
}

/* 加载状态样式 */
:deep(.el-loading-spinner) {
  margin-top: -20px;
}

:deep(.el-loading-text) {
  color: #667eea;
  font-weight: 500;
}
</style>



