<template>
  <div class="task-list-container">
    <el-card class="main-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>任务列表</h2>
          <el-button type="primary" @click="handleAddTask">
            <el-icon><Plus /></el-icon>
            新增任务
          </el-button>
        </div>
      </template>
      
      <!-- 筛选条件 -->
      <div class="filter-container">
        <el-input
          v-model="searchForm.name"
          placeholder="请输入任务名称"
          clearable
          style="width: 200px; margin-right: 10px;"
        >
          <template #prepend>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <el-select
          v-model="searchForm.type"
          placeholder="请选择任务类型"
          clearable
          style="width: 150px; margin-right: 10px;"
        >
          <el-option label="模拟考试" :value="1" />
          <el-option label="正式考试" :value="2" />
          <el-option label="练习任务" :value="3" />
        </el-select>
        
        <el-select
          v-model="searchForm.status"
          placeholder="请选择状态"
          clearable
          style="width: 150px; margin-right: 10px;"
        >
          <el-option label="未开始" :value="0" />
          <el-option label="进行中" :value="1" />
          <el-option label="已结束" :value="2" />
        </el-select>
        
        <el-select
          v-model="searchForm.subjectId"
          placeholder="请选择科目"
          clearable
          style="width: 150px; margin-right: 10px;"
        >
          <el-option label="全部" value="" />
          <el-option
            v-for="subject in subjects"
            :key="subject.id"
            :label="subject.name"
            :value="subject.id"
          />
        </el-select>
        
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      
      <!-- 任务列表 -->
      <el-table :data="tasks" style="width: 100%" :row-key="(row: any) => row.id" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="任务名称" min-width="180" />
        <el-table-column prop="type" label="任务类型" width="120">
          <template #default="scope">
            <el-tag :type="getTaskTypeTag(scope.row.type).type">
              {{ getTaskTypeTag(scope.row.type).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="subject.name" label="考试科目" width="150" />
        <el-table-column prop="duration" label="考试时长(分钟)" width="120" />
        <el-table-column prop="participantCount" label="参与人数" width="100" />
        <el-table-column prop="startTime" label="开始时间" width="180" :formatter="formatDate" />
        <el-table-column prop="endTime" label="结束时间" width="180" :formatter="formatDate" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'warning' : scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 0 ? '未开始' : scope.row.status === 1 ? '进行中' : '已结束' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleViewTask(scope.row)">查看</el-button>
            <el-button type="warning" link @click="handleEditTask(scope.row)">编辑</el-button>
            <el-button type="danger" link @click="handleDeleteTask(scope.row)">删除</el-button>
            <el-button type="success" link @click="handlePublishTask(scope.row)">
              {{ scope.row.status === 0 ? '发布' : '已发布' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 编辑任务对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增任务' : '编辑任务'"
      width="600px"
      destroy-on-close
    >
      <!-- 编辑任务表单内容 -->
      <el-form :model="editTaskForm" :rules="editTaskRules" ref="editTaskFormRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="任务名称" prop="name">
              <el-input v-model="editTaskForm.name" placeholder="请输入任务名称"></el-input>
            </el-form-item>
            
            <el-form-item label="任务类型" prop="type">
              <el-select v-model="editTaskForm.type" placeholder="请选择任务类型">
                <el-option label="模拟考试" :value="1"></el-option>
                <el-option label="正式考试" :value="2"></el-option>
                <el-option label="练习任务" :value="3"></el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item label="考试科目" prop="subjectId">
              <el-select v-model="editTaskForm.subjectId" placeholder="请选择考试科目">
                <el-option 
                  v-for="subject in subjects" 
                  :key="subject.id" 
                  :label="subject.name" 
                  :value="subject.id"></el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item label="考试时长(分钟)" prop="duration">
              <el-input-number v-model="editTaskForm.duration" :min="5" :max="300" placeholder="请输入考试时长"></el-input-number>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="editTaskForm.startTime"
                type="datetime"
                placeholder="选择开始时间"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
            
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="editTaskForm.endTime"
                type="datetime"
                placeholder="选择结束时间"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
            
            <el-form-item label="参与人数" prop="participantCount">
              <el-input-number v-model="editTaskForm.participantCount" :min="1" placeholder="请输入参与人数"></el-input-number>
            </el-form-item>
            
            <el-form-item label="状态" prop="status">
              <el-select v-model="editTaskForm.status" placeholder="请选择状态">
                <el-option label="未开始" :value="0"></el-option>
                <el-option label="进行中" :value="1"></el-option>
                <el-option label="已结束" :value="2"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="任务描述" prop="description">
          <el-input 
            v-model="editTaskForm.description" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入任务描述"></el-input>
        </el-form-item>
        
        <el-form-item label="考试说明" prop="instructions">
          <el-input 
            v-model="editTaskForm.instructions" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入考试说明"></el-input>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmitEdit">确认</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Search } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '@/utils/axios'

const router = useRouter()
const editTaskFormRef = ref<FormInstance>()

// 加载状态
const loading = ref(false)

// 对话框状态
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')

// 搜索表单
const searchForm = reactive({
  name: '',
  type: undefined,
  status: undefined,
  subjectId: undefined
})

// 分页信息
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 任务列表
const tasks = ref<any[]>([])

// 科目列表
const subjects = ref<any[]>([])

// 编辑任务表单
const editTaskForm = reactive({
  id: '',
  name: '',
  type: 1,
  subjectId: '',
  duration: 60,
  startTime: '',
  endTime: '',
  participantCount: 0,
  status: 0,
  description: '',
  instructions: ''
})

// 编辑任务表单验证规则
const editTaskRules = reactive<FormRules>({
  name: [
    { required: true, message: '请输入任务名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择任务类型', trigger: 'change' }
  ],
  subjectId: [
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

// 任务类型映射
const taskTypeMap: { [key: number]: { label: string; type: string } } = {
  1: { label: '模拟考试', type: 'info' },
  2: { label: '正式考试', type: 'success' },
  3: { label: '练习任务', type: 'warning' }
}

// 获取任务类型标签
const getTaskTypeTag = (type: number) => {
  return taskTypeMap[type] || { label: '未知', type: 'default' }
}

// 日期格式化
const formatDate = (_row: any, _column: any, cellValue: any) => {
  if (!cellValue) return ''
  const date = new Date(cellValue)
  return date.toLocaleString('zh-CN')
}

// 获取科目列表
const getSubjects = () => {
  axios.get('/subjects')
  .then(res => {
      subjects.value = res.data
    })
  .catch(error => {
    console.error('获取科目列表失败:', error)
  })
}

// 初始化数据
const initData = () => {
  loading.value = true
  axios.get('/tasks', {
    params: {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
  })
  .then(res => {
      tasks.value = res.data
      pagination.total = res.data.total || 0
    })
  .catch(error => {
    console.error('获取任务列表失败:', error)
  })
  .finally(() => {
    loading.value = false
  })
}

// 处理搜索
const handleSearch = () => {
  pagination.pageNum = 1
  initData()
}

// 处理重置
const handleReset = () => {
  Object.assign(searchForm, {
    name: '',
    type: undefined,
    status: undefined,
    subjectId: undefined
  })
  pagination.pageNum = 1
  initData()
}

// 新增任务
const handleAddTask = () => {
  router.push('/dashboard/task-create')
}

// 查看任务
const handleViewTask = (row: any) => {
  router.push(`/dashboard/task-detail/${row.id}`)
}

// 编辑任务
const handleEditTask = (row: any) => {
  dialogType.value = 'edit'
  // 填充编辑表单数据
  Object.assign(editTaskForm, row)
  dialogVisible.value = true
}

// 删除任务
const handleDeleteTask = (row: any) => {
  ElMessageBox.confirm('确定要删除该任务吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  .then(() => {
    axios.delete(`/tasks/${row.id}`)
    .then(() => {
        ElMessage.success('删除成功')
        initData()
      })
    .catch(error => {
      console.error('删除任务失败:', error)
      ElMessage.error('删除失败')
    })
  })
  .catch(() => {
    // 用户取消删除
  })
}

// 发布任务
const handlePublishTask = (row: any) => {
  if (row.status !== 0) {
    ElMessage.warning('该任务已发布')
    return
  }
  
  axios.put(`/tasks/${row.id}/publish`)
  .then(() => {
      ElMessage.success('任务发布成功')
      initData()
  })
  .catch(error => {
    console.error('发布任务失败:', error)
    ElMessage.error('发布失败')
  })
}

// 提交编辑任务
const handleSubmitEdit = () => {
  if (!editTaskFormRef.value) return
  
  editTaskFormRef.value.validate((valid: boolean) => {
    if (valid) {
      loading.value = true
      
      axios.put(`/tasks/${editTaskForm.id}`, {
        ...editTaskForm,
        subject: { id: editTaskForm.subjectId }
      })
      .then(() => {
          ElMessage.success('任务编辑成功')
          dialogVisible.value = false
          initData()
      })
      .catch(error => {
        console.error('编辑任务失败:', error)
        ElMessage.error('编辑失败')
      })
      .finally(() => {
        loading.value = false
      })
    }
  })
}

// 处理分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size
  initData()
}

// 处理分页当前页变化
const handleCurrentChange = (current: number) => {
  pagination.pageNum = current
  initData()
}

// 组件挂载时初始化数据
onMounted(() => {
  getSubjects()
  initData()
})
</script>

<style scoped>
.task-list-container {
  padding: 20px 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-container {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .filter-container {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .el-input,
  .el-select {
    width: 100% !important;
    margin-right: 0 !important;
  }
}
</style>



