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
            
            <el-form-item label="考试科目" prop="subjectIds">
              <el-select v-model="taskForm.subjectIds" placeholder="请选择考试科目" multiple>
                <el-option 
                  v-for="subject in subjects" 
                  :key="subject.id" 
                  :label="subject.name" 
                  :value="subject.id"></el-option>
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
              <el-select v-model="taskForm.departmentIds" placeholder="请选择参与科室" multiple filterable @change="handleDepartmentChange">
                <el-option 
                  v-for="dept in departments" 
                  :key="dept.id" 
                  :label="dept.name" 
                  :value="dept.id"></el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item label="参与人员" prop="participantIds">
              <el-select v-model="taskForm.participantIds" placeholder="请选择参与人员" multiple filterable>
                <el-option 
                  v-for="participant in participants" 
                  :key="participant.id" 
                  :label="participant.department" 
                  :value="participant.id"></el-option>
              </el-select>
              <div class="form-tip">显示格式：科室，不显示真实姓名和人员ID</div>
            </el-form-item>
            
            <el-form-item label="状态" prop="status">
              <el-select v-model="taskForm.status" placeholder="请选择状态">
                <el-option label="未开始" :value="0"></el-option>
                <el-option label="进行中" :value="1"></el-option>
                <el-option label="已结束" :value="2"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 题库选择 -->
        <el-divider>题库设置</el-divider>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选择题库">
              <el-select v-model="selectedPaper" placeholder="请选择题库" filterable>
                <el-option 
                  v-for="paper in papers" 
                  :key="paper.id" 
                  :label="paper.name" 
                  :value="paper"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="选择题型">
              <el-select v-model="selectedTypes" placeholder="请选择题型" multiple>
                <el-option 
                  v-for="type in questionTypes" 
                  :key="type.value" 
                  :label="type.label" 
                  :value="type.value"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <!-- 难度分配比例 -->
        <el-form-item label="难度分配比例">
          <div class="difficulty-distribution">
            <div class="distribution-item">
              <span class="distribution-label">简单题:</span>
              <el-slider 
                v-model="difficultyDistribution.easy" 
                :min="0" 
                :max="100" 
                :step="5"
                @change="updateDifficultyDistribution"></el-slider>
              <span class="distribution-value">{{ difficultyDistribution.easy }}%</span>
            </div>
            <div class="distribution-item">
              <span class="distribution-label">中等题:</span>
              <el-slider 
                v-model="difficultyDistribution.medium" 
                :min="0" 
                :max="100" 
                :step="5"
                @change="updateDifficultyDistribution"></el-slider>
              <span class="distribution-value">{{ difficultyDistribution.medium }}%</span>
            </div>
            <div class="distribution-item">
              <span class="distribution-label">困难题:</span>
              <el-slider 
                v-model="difficultyDistribution.hard" 
                :min="0" 
                :max="100" 
                :step="5"
                @change="updateDifficultyDistribution"></el-slider>
              <span class="distribution-value">{{ difficultyDistribution.hard }}%</span>
            </div>
          </div>
          <div class="distribution-total">总比例: {{ difficultyDistribution.easy + difficultyDistribution.medium + difficultyDistribution.hard }}%</div>
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
import { ref, reactive, onMounted } from 'vue'
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
  subjectIds: [],
  duration: 60,
  startTime: '',
  endTime: '',
  lateAllowedTime: 0, // 开考后允许进入的时间（分钟）
  departmentIds: [], // 参与科室ID列表
  participantIds: [], // 参与人员ID列表
  status: 0,
  description: '',
  instructions: ''
})

// 科室列表
const departments = ref<any[]>([])

// 人员列表
const participants = ref<any[]>([])



// 题库列表
const papers = ref<any[]>([])

// 选择的题库
const selectedPaper = ref<any>(null)

// 题型列表
const questionTypes = ref([
  { label: '单选题', value: 1 },
  { label: '多选题', value: 2 },
  { label: '是非题', value: 3 }
])

// 选择的题型
const selectedTypes = ref<any[]>([])

// 难度分配比例
const difficultyDistribution = reactive({
  easy: 30, // 简单题比例
  medium: 50, // 中等题比例
  hard: 20 // 困难题比例
})

// 表单验证规则
const taskRules = reactive<FormRules>({
  name: [
    { required: true, message: '请输入任务名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择任务类型', trigger: 'change' }
  ],
  subjectIds: [
    { required: true, message: '请选择考试科目', trigger: 'change' }
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
  ],
  participantIds: [
    { required: true, message: '请选择参与人员', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
})

// 更新难度分配比例，确保总和为100%
const updateDifficultyDistribution = () => {
  // 这里可以添加逻辑，确保三个难度比例总和为100%
  // 目前简化处理，允许总和不等于100%
}

// 科目列表，从API获取
const subjects = ref<any[]>([])

// 获取科目列表
const fetchSubjects = async () => {
  try {
    const res = await axios.get('/subjects')
    subjects.value = res.data || []
  } catch (error) {
    console.error('获取科目列表失败:', error)
    ElMessage.error('获取科目列表失败')
  }
}

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
    const res = await axios.get('/departments')
    departments.value = res.data || []
  } catch (error) {
    console.error('获取科室列表失败:', error)
    ElMessage.error('获取科室列表失败')
  }
}

// 获取参与人员列表
const fetchParticipants = async () => {
  try {
    const params = {
      departmentIds: taskForm.departmentIds.length > 0 ? taskForm.departmentIds : undefined
    }
    const res = await axios.get('/users/participants', { params })
    participants.value = res.data || []
  } catch (error) {
    console.error('获取参与人员列表失败:', error)
    ElMessage.error('获取参与人员列表失败')
  }
}

// 科室选择变化时，重新获取人员列表
const handleDepartmentChange = () => {
  fetchParticipants()
  // 清空已选择的人员，避免无效选择
  taskForm.participantIds = []
}

// 提交表单
const handleSubmit = async () => {
  if (!taskFormRef.value) return
  
  try {
    await taskFormRef.value.validate()
    
    // 构建请求数据
    const requestData = {
      ...taskForm,
      subject: { id: taskForm.subjectIds[0] },
      paperId: selectedPaper.value?.id,
      questionTypes: selectedTypes.value,
      difficultyDistribution: {
        easy: difficultyDistribution.easy,
        medium: difficultyDistribution.medium,
        hard: difficultyDistribution.hard
      }
    }
    
    // 发送创建任务请求
    await axios.post('/tasks', requestData)
    
    ElMessage.success('任务创建成功')
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
  fetchSubjects()
  fetchPapers()
  fetchDepartments()
  fetchParticipants()
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

/* 难度分配比例样式 */
.difficulty-distribution {
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



