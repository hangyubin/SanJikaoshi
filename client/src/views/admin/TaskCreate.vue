<template>
  <div class="task-create-container">
    <el-card class="main-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>创建考试任务</h2>
        </div>
      </template>
      
      <el-form :model="taskForm" :rules="taskRules" ref="taskFormRef" label-width="120px" class="task-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="任务名称" prop="name">
              <el-input v-model="taskForm.name" placeholder="请输入任务名称"></el-input>
            </el-form-item>
            
            <el-form-item label="任务类型" prop="type">
              <el-select v-model="taskForm.type" placeholder="请选择任务类型">
                <el-option label="模拟考试" :value="1"></el-option>
                <el-option label="正式考试" :value="2"></el-option>
                <el-option label="练习任务" :value="3"></el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item label="考试题库" prop="paperId">
              <el-select v-model="taskForm.paperId" placeholder="请选择考试题库" filterable>
                <el-option 
                  v-for="paper in papers" 
                  :key="paper.id" 
                  :label="paper.name" 
                  :value="paper.id"></el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item label="考试时长(分钟)" prop="duration">
              <el-input-number v-model="taskForm.duration" :min="5" :max="300" placeholder="请输入考试时长"></el-input-number>
            </el-form-item>
            
            <el-form-item label="开考后允许进入时间(分钟)" prop="lateAllowedTime">
              <el-input-number v-model="taskForm.lateAllowedTime" :min="0" :max="120" placeholder="请输入开考后允许进入的时间"></el-input-number>
              <div class="form-tip">设置为0表示不允许迟到进入</div>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="taskForm.startTime"
                type="datetime"
                placeholder="选择开始时间"
                style="width: 100%"
                language="zh-CN"
              ></el-date-picker>
            </el-form-item>
            
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="taskForm.endTime"
                type="datetime"
                placeholder="选择结束时间"
                style="width: 100%"
                language="zh-CN"
              ></el-date-picker>
            </el-form-item>
            
            <el-form-item label="参与科室" prop="departmentIds">
              <el-select v-model="taskForm.departmentIds" placeholder="请选择参与科室" multiple filterable>
                <el-option 
                  v-for="dept in departments" 
                  :key="dept.id" 
                  :label="dept.name" 
                  :value="dept.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 题型分配 -->
        <el-divider>题型分配</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="总题数" prop="totalQuestions">
              <el-input-number 
                v-model="taskForm.totalQuestions" 
                :min="10" 
                :max="200" 
                :step="5" 
                placeholder="请输入总题数"></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 题型比例分配 -->
        <el-form-item label="题型比例分配">
          <div class="question-type-distribution">
            <div class="distribution-item" v-for="type in questionTypes" :key="type.value">
              <span class="distribution-label">{{ type.label }}:</span>
              <el-slider 
                v-model="questionTypeDistribution[type.value]" 
                :min="0" 
                :max="100" 
                :step="5"
                @change="updateQuestionTypeDistribution"></el-slider>
              <span class="distribution-value">{{ questionTypeDistribution[type.value] }}%</span>
            </div>
          </div>
          <div class="distribution-total">总比例: {{ totalQuestionTypeRatio }}%</div>
        </el-form-item>
        
        <el-form-item label="任务描述" prop="description">
          <el-input 
            v-model="taskForm.description" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入任务描述"></el-input>
        </el-form-item>
        
        <el-form-item label="考试说明" prop="instructions">
          <el-input 
            v-model="taskForm.instructions" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入考试说明"></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">创建任务</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios'

const router = useRouter()
const taskFormRef = ref<FormInstance>()

// 任务表单
const taskForm = reactive({
  id: '',
  name: '',
  type: 1,
  paperId: '',
  duration: 60,
  startTime: '',
  endTime: '',
  lateAllowedTime: 0, // 开考后允许进入的时间（分钟）
  departmentIds: [], // 参与科室ID列表
  totalQuestions: 50, // 总题数，默认50题
  description: '',
  instructions: ''
})

// 科室列表
const departments = ref<any[]>([])

// 题库列表
const papers = ref<any[]>([])

// 题型列表
const questionTypes = ref([
  { label: '单选题', value: 1 },
  { label: '多选题', value: 2 },
  { label: '是非题', value: 3 }
])

// 题型分配比例
const questionTypeDistribution = reactive<Record<number, number>>({
  1: 40, // 单选题比例
  2: 40, // 多选题比例
  3: 20 // 是非题比例
})

// 计算总题型比例
const totalQuestionTypeRatio = computed(() => {
  return Object.values(questionTypeDistribution).reduce((sum, ratio) => sum + ratio, 0)
})

// 更新题型分配比例
const updateQuestionTypeDistribution = () => {
  // 这里可以添加逻辑，确保题型比例总和为100%
  // 目前简化处理，允许总和不等于100%
}

// 表单验证规则
const taskRules = reactive<FormRules>({
  name: [
    { required: true, message: '请输入任务名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择任务类型', trigger: 'change' }
  ],
  paperId: [
    { required: true, message: '请选择考试题库', trigger: 'change' }
  ],
  totalQuestions: [
    { required: true, message: '请输入总题数', trigger: 'blur' }
  ],
  duration: [
    { required: true, message: '请输入考试时长', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  lateAllowedTime: [
    { required: true, message: '请输入开考后允许进入的时间', trigger: 'blur' }
  ],
  departmentIds: [
    { required: true, message: '请选择参与科室', trigger: 'change' }
  ]
})



// 获取题库列表
const fetchPapers = async () => {
  try {
    const res = await axios.get('/papers')
    papers.value = res.data.records || res.data || []
  } catch (error) {
    console.error('获取题库列表失败:', error)
    ElMessage.error('获取题库列表失败')
  }
}

// 获取科室列表
const fetchDepartments = async () => {
  try {
    const res = await axios.get('/departments', {
      params: {
        pageSize: 1000 // 获取足够多的科室，确保能显示所有科室
      }
    })
    const responseData = res.data
    // 处理不同的返回格式
    if (responseData && typeof responseData === 'object') {
      // 如果是分页格式，取records字段
      if (responseData.records) {
        departments.value = responseData.records
      } else if (Array.isArray(responseData)) {
        // 如果直接是数组格式，直接使用
        departments.value = responseData
      } else {
        // 其他情况，使用空数组
        departments.value = []
      }
    } else if (Array.isArray(responseData)) {
      // 直接数组格式
      departments.value = responseData
    } else {
      // 无效数据，使用空数组
      departments.value = []
    }
  } catch (error) {
    console.error('获取科室列表失败:', error)
    ElMessage.error('获取科室列表失败')
    departments.value = []
  }
}



// 提交表单
const handleSubmit = async () => {
  if (!taskFormRef.value) return
  
  try {
    await taskFormRef.value.validate()
    
    // 构建请求数据
    const requestData = {
      ...taskForm,
      questionTypeDistribution: {
        ...questionTypeDistribution
      }
    }
    
    // 发送创建任务请求
    await axios.post('/tasks', requestData)
    
    ElMessage.success('任务创建成功，试卷已生成')
    router.push('/dashboard/task-management')
  } catch (error: any) {
    console.error('创建任务失败:', error)
    ElMessage.error(error.response?.data?.message || '创建任务失败')
  }
}

// 重置表单
const handleReset = () => {
  if (taskFormRef.value) {
    taskFormRef.value.resetFields()
  }
}

// 取消操作
const handleCancel = () => {
  router.push('/dashboard/task-management')
}

// 组件挂载时获取数据
onMounted(() => {
  fetchPapers()
  fetchDepartments()
})
</script>

<style scoped>
.task-create-container {
  padding: 20px 0;
}

.main-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-form {
  margin-top: 20px;
}

/* 表单提示 */
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

/* 题型分配比例样式 */
.question-type-distribution {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin: 10px 0;
}

.distribution-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.distribution-label {
  width: 80px;
  font-weight: 500;
  color: #303133;
  text-align: right;
}

.distribution-value {
  width: 50px;
  text-align: left;
  font-weight: 600;
  color: #667eea;
}

.distribution-total {
  text-align: right;
  margin-top: 10px;
  font-weight: 600;
  color: #303133;
}

/* 滑块样式 */
:deep(.el-slider) {
  flex: 1;
}

:deep(.el-slider__runway) {
  background-color: #e4e7ed;
}

:deep(.el-slider__bar) {
  background-color: #667eea;
}

:deep(.el-slider__button) {
  border-color: #667eea;
  background-color: #fff;
}

:deep(.el-slider__button:hover),
:deep(.el-slider__button:focus) {
  border-color: #536dfe;
  box-shadow: 0 0 0 5px rgba(102, 126, 234, 0.1);
}
</style>



