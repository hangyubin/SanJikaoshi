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
            
            <el-form-item label="参与人数" prop="participantCount">
              <el-input-number v-model="taskForm.participantCount" :min="1" placeholder="请输入参与人数"></el-input-number>
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

// 科目列表
const subjects = ref<any[]>([])

// 任务表单
const taskForm = reactive({
  id: '',
  name: '',
  type: 1,
  subjectIds: [],
  duration: 60,
  startTime: '',
  endTime: '',
  participantCount: 0,
  status: 0,
  description: '',
  instructions: ''
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
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
})

// 获取科目列表
const getSubjects = async () => {
  try {
    const response = await axios.get('/api/subjects')
    const { data } = response
    if (data.code === 200) {
      subjects.value = data.data
    }
  } catch (error) {
    console.error('获取科目列表失败:', error)
    ElMessage.error('获取科目列表失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!taskFormRef.value) return
  
  try {
    await taskFormRef.value.validate()
    
    // 发送创建任务请求
    // 注意：这里暂时只使用第一个选择的科目，因为Task实体目前只支持单个科目
    await axios.post('/api/tasks', {
      ...taskForm,
      subject: { id: taskForm.subjectIds[0] }
    })
    
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

// 组件挂载时获取科目列表
onMounted(() => {
  getSubjects()
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
</style>