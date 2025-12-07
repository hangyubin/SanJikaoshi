<template>
  <div class="practice">
    <h1>练习中心</h1>
    
    <!-- 科目选择 -->
    <el-card class="subject-select-card">
      <h3>选择练习科目</h3>
      <el-row :gutter="20">
        <el-col :span="8" v-for="subject in subjects" :key="subject.id">
          <el-card class="subject-item" @click="selectSubject(subject)">
            <div class="subject-content">
              <h4>{{ subject.name }}</h4>
              <p>{{ subject.description }}</p>
              <div class="question-count">
                <span>题目数量: {{ subject.questionCount }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 练习设置 -->
    <el-card class="practice-setting-card" v-if="selectedSubject">
      <h3>练习设置</h3>
      <el-form :model="practiceSetting" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="题目类型">
              <el-select v-model="practiceSetting.questionType" multiple placeholder="请选择题目类型">
                <el-option label="选择题" :value="1"></el-option>
                <el-option label="判断题" :value="2"></el-option>
                <el-option label="填空题" :value="3"></el-option>
                <el-option label="简答题" :value="4"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="难度等级">
              <el-select v-model="practiceSetting.difficulty" multiple placeholder="请选择难度等级">
                <el-option label="简单" :value="1"></el-option>
                <el-option label="中等" :value="2"></el-option>
                <el-option label="困难" :value="3"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="题目数量">
              <el-input-number v-model="practiceSetting.questionCount" :min="5" :max="50" :step="5"></el-input-number>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="练习模式">
              <el-radio-group v-model="practiceSetting.mode">
                <el-radio :value="1">顺序练习</el-radio>
                <el-radio :value="2">随机练习</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button type="primary" @click="startPractice">开始练习</el-button>
          <el-button @click="resetSetting">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'

// 模拟科目数据
const subjects = ref([
  {
    id: 1,
    name: '数学',
    description: '高等数学练习',
    questionCount: 500
  },
  {
    id: 2,
    name: '英语',
    description: '大学英语练习',
    questionCount: 450
  },
  {
    id: 3,
    name: '计算机',
    description: '计算机基础练习',
    questionCount: 600
  }
])

const selectedSubject = ref<any>(null)

const practiceSetting = reactive({
  questionType: [],
  difficulty: [],
  questionCount: 10,
  mode: 1
})

const selectSubject = (subject: any) => {
  selectedSubject.value = subject
}

const startPractice = () => {
  // 开始练习逻辑
  console.log('开始练习', selectedSubject.value, practiceSetting)
}

const resetSetting = () => {
  practiceSetting.questionType = []
  practiceSetting.difficulty = []
  practiceSetting.questionCount = 10
  practiceSetting.mode = 1
}
</script>

<style scoped>
.practice {
  padding: 20px 0;
}

.subject-select-card {
  margin-bottom: 20px;
}

.subject-item {
  cursor: pointer;
  transition: all 0.3s;
}

.subject-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.subject-content {
  text-align: center;
}

.subject-content h4 {
  margin-bottom: 10px;
  color: #303133;
}

.subject-content p {
  color: #606266;
  margin-bottom: 15px;
}

.question-count {
  color: #409eff;
  font-size: 14px;
}

.practice-setting-card {
  margin-top: 20px;
}
</style>