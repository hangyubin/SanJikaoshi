<template>
  <div class="paper-management">
    <h1>题库管理</h1>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">试卷总数</p>
              <h3 class="stat-value">{{ totalPaperCount }}</h3>
            </div>
            <div class="stat-icon paper-icon">
              <el-icon><DocumentCopy /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">题目总数</p>
              <h3 class="stat-value">{{ totalQuestionCount }}</h3>
            </div>
            <div class="stat-icon question-icon">
              <el-icon><EditPen /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">A1型题</p>
              <h3 class="stat-value">{{ a1QuestionCount }}</h3>
            </div>
            <div class="stat-icon a1-icon">
              <el-icon><Document /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">A2型题</p>
              <h3 class="stat-value">{{ a2QuestionCount }}</h3>
            </div>
            <div class="stat-icon a2-icon">
              <el-icon><Document /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="80px" inline>
        <el-form-item label="题库名称">
          <el-input v-model="searchForm.name" placeholder="请输入题库名称"></el-input>
        </el-form-item>
        <el-form-item label="题型">
          <el-select v-model="searchForm.type" placeholder="请选择题型">
            <el-option label="全部" value=""></el-option>
            <el-option label="单选题" :value="1"></el-option>
            <el-option label="多选题" :value="2"></el-option>
            <el-option label="是非题" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">增加题库</el-button>
          <el-button type="info" @click="handleImportClick">导入题库</el-button>
          <el-button type="danger" @click="handleDownloadTemplate">下载模板</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 题库表格 -->
    <el-card class="table-card" style="margin-top: 20px;">
      <el-table :data="papers" stripe style="width: 100%">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="题库名称" min-width="200"></el-table-column>
        <el-table-column prop="type" label="题型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.type === 1 ? 'primary' : scope.row.type === 2 ? 'success' : scope.row.type === 3 ? 'warning' : 'info'">
              {{ scope.row.type === 1 ? '单选题' : scope.row.type === 2 ? '多选题' : '是非题' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="150">
          <template #default="scope">
            <el-tag :type="scope.row.difficulty === 1 ? 'success' : scope.row.difficulty === 2 ? 'warning' : 'danger'">
              {{ scope.row.difficulty === 1 ? '简单' : scope.row.difficulty === 2 ? '中等' : '困难' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
            <el-button type="info" size="small" @click="handleViewQuestions(scope.row)">查看题目</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
    </el-card>
    
    <!-- 创建/编辑题库对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="题库名称" prop="name">
          <el-select v-model="form.name" placeholder="请选择题库名称">
            <el-option label="基本理论" value="基本理论"></el-option>
            <el-option label="基本知识" value="基本知识"></el-option>
            <el-option label="基本技能" value="基本技能"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="题型" prop="type">
          <el-select v-model="form.type" placeholder="请选择题型">
            <el-option label="单选题" :value="1"></el-option>
            <el-option label="多选题" :value="2"></el-option>
            <el-option label="是非题" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="难度" prop="difficulty">
          <el-select v-model="form.difficulty" placeholder="请选择难度">
            <el-option label="简单 (1分)" :value="1"></el-option>
            <el-option label="中等 (3分)" :value="2"></el-option>
            <el-option label="困难 (5分)" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" active-value="1" inactive-value="0"></el-switch>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注信息"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确认</el-button>
        </span>
      </template>
    </el-dialog>
    

    
    <!-- 导入题库对话框 -->
    <el-dialog
      v-model="importDialogVisible"
      title="导入题库"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="importForm" label-width="100px">
        <el-form-item label="题库名称">
          <el-input v-model="importForm.name" placeholder="请输入题库名称"></el-input>
        </el-form-item>
        <el-form-item label="导入文件">
          <el-upload
            class="upload-demo"
            action="#"
            :show-file-list="false"
            :before-upload="handleImport"
            accept=".xlsx,.xls,.json"
          >
            <el-button type="primary">选择文件</el-button>
            <div class="el-upload__tip">
              支持 XLSX、XLS、JSON 格式，文件大小不超过 10MB
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item label="导入说明">
          <el-input v-model="importForm.description" type="textarea" :rows="3" placeholder="请输入导入说明"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="importDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleImportSubmit">开始导入</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import axios from '@/utils/axios'
import { ElMessage } from 'element-plus'
import { DocumentCopy, EditPen, Document } from '@element-plus/icons-vue'

// 题库数据
const papers = ref<any[]>([])

// 统计数据
const totalPaperCount = ref(85)
const totalQuestionCount = ref(2500)
const a1QuestionCount = ref(800)
const a2QuestionCount = ref(700)

const searchForm = reactive({
  name: '',
  type: ''
})

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('增加题库')
const formRef = ref<FormInstance>()

const form = reactive({
  id: '',
  name: '',
  type: 1,
  difficulty: 2,
  status: 1,
  remark: ''
})

const rules = reactive<FormRules>({
  name: [
    { required: true, message: '请输入题库名称', trigger: 'blur' }
  ]
})



// 导入题库相关
const importDialogVisible = ref(false)
const importForm = reactive({
  name: '',
  file: null,
  description: ''
})

// 获取题库列表
const fetchPapers = async () => {
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      name: searchForm.name,
      type: searchForm.type
    }
    // 修复API地址，去掉/api前缀
    const response = await axios.get('/papers', { params })
    // 适应后端返回的格式
    const responseData = response as any
    if (responseData.records) {
      // 如果是分页格式
      papers.value = responseData.records
      total.value = responseData.total
    } else {
      // 如果是直接列表格式
      papers.value = responseData
      total.value = responseData.length
    }
  } catch (error) {
    console.error('获取题库列表失败:', error)
    ElMessage.error('获取题库列表失败')
  }
}

// 搜索题库
const handleSearch = () => {
  currentPage.value = 1
  fetchPapers()
}

// 重置搜索
const handleReset = () => {
  searchForm.name = ''
  searchForm.type = ''
  currentPage.value = 1
  fetchPapers()
}

// 增加题库
const handleAdd = () => {
  dialogTitle.value = '增加题库'
  // 重置表单
  Object.assign(form, {
    id: '',
    name: '',
    type: 1,
    difficulty: 2,
    status: 1,
    remark: ''
  })
  dialogVisible.value = true
}

// 编辑题库
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑题库'
  // 填充表单
  Object.assign(form, {
    id: row.id,
    name: row.name,
    type: row.type,
    difficulty: row.difficulty,
    status: row.status,
    remark: row.remark
  })
  dialogVisible.value = true
}

// 提交题库
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 构建请求数据
    const requestData = {
      ...form
    }
    
    if (form.id) {
      // 更新题库
      await axios.put(`/papers/${form.id}`, requestData)
    } else {
      // 增加题库，去掉/api前缀
      await axios.post('/papers', requestData)
    }
    
    ElMessage.success(form.id ? '更新题库成功' : '增加题库成功')
    dialogVisible.value = false
    fetchPapers() // 重新加载题库列表
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 删除题库
const handleDelete = async (row: any) => {
  try {
    // 删除题库
    await axios.delete(`/papers/${row.id}`)
    ElMessage.success('删除题库成功')
    fetchPapers() // 重新加载题库列表
  } catch (error) {
    console.error('删除题库失败:', error)
    ElMessage.error('删除题库失败')
  }
}

// 查看题库题目
const handleViewQuestions = (row: any) => {
  console.log('查看题库题目:', row)
  // 这里可以跳转到题库题目管理页面
}



// 打开导入题库对话框
const handleImportClick = () => {
  importDialogVisible.value = true
  // 重置表单
  Object.assign(importForm, {
    name: '',
    file: null,
    description: ''
  })
}

// 下载题库模板
const handleDownloadTemplate = async () => {
  try {
    const response = await axios.get('/papers/import/template', {
      responseType: 'blob'
    })
    
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', '题库导入模板.xlsx')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('题库模板下载成功')
  } catch (error) {
    console.error('下载题库模板失败:', error)
    ElMessage.error('下载题库模板失败')
  }
}

// 处理文件选择
const handleImport = (file: any) => {
  // 验证文件大小
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('上传文件大小不能超过 10MB!')
    return false
  }
  
  // 验证文件类型
  const fileType = file.type
  const isXlsx = fileType === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  const isXls = fileType === 'application/vnd.ms-excel'
  const isJson = fileType === 'application/json'
  
  if (!isXlsx && !isXls && !isJson) {
    ElMessage.error('请上传 XLSX、XLS 或 JSON 格式的文件!')
    return false
  }
  
  // 保存文件对象
  importForm.file = file
  ElMessage.success('文件选择成功')
  return false // 阻止自动上传
}

// 提交导入题库
const handleImportSubmit = async () => {
  try {
    if (!importForm.file) {
      ElMessage.error('请先选择文件')
      return
    }
    
    if (!importForm.name) {
      ElMessage.error('请输入题库名称')
      return
    }
    
    const formData = new FormData()
    formData.append('file', importForm.file)
    formData.append('name', importForm.name)
    formData.append('description', importForm.description)
    
    // 去掉/api前缀
    await axios.post('/papers/import', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    ElMessage.success('导入题库成功')
    importDialogVisible.value = false
    fetchPapers() // 重新加载题库列表
  } catch (error) {
    console.error('导入题库失败:', error)
    ElMessage.error('导入题库失败')
  }
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchPapers()
}

// 当前页码变化
const handleCurrentChange = (current: number) => {
  currentPage.value = current
  fetchPapers()
}

// 初始化数据
onMounted(() => {
  fetchPapers()
})
</script>

<style scoped>
.paper-management {
  padding: 20px 0;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.paper-management h1 {
  margin: 0 0 30px 0;
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 0 0 20px 0;
  border-bottom: 2px solid #e4e7ed;
}

.paper-management h1::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 32px;
  background: linear-gradient(180deg, #667eea, #764ba2);
  border-radius: 2px;
}

/* 统计卡片 */
.stat-card {
  margin-bottom: 20px;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  background: white;
  overflow: hidden;
  position: relative;
}

.stat-card::before {
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

.stat-card:hover::before {
  transform: scaleX(1);
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 120px;
  padding: 0 30px;
  position: relative;
  z-index: 1;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
  display: block;
  font-weight: 500;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin: 0;
  display: block;
  animation: countUp 1s ease-out;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

@keyframes countUp {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 28px;
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.paper-icon {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.question-icon {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.a1-icon {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
}

.a2-icon {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
}

.search-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  background: white;
  overflow: hidden;
}

.table-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  background: white;
  overflow: hidden;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__header-wrapper) {
  background-color: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-table__row) {
  transition: all 0.2s ease;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
  transform: translateX(5px);
}

:deep(.el-button--small) {
  border-radius: 6px;
  padding: 6px 16px;
  transition: all 0.3s ease;
  font-weight: 500;
}

:deep(.el-button--small:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

:deep(.el-tag) {
  border-radius: 6px;
  font-weight: 500;
}
</style>



