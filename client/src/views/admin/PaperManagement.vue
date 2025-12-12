<template>
  <div class="paper-management">
    <h1>题库管理</h1>
    
    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="80px" inline>
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
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 题库表格 -->
    <el-card class="table-card" style="margin-top: 20px;">
      <el-table :data="papers" stripe style="width: 100%">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="type" label="题型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.type === 1 ? 'primary' : scope.row.type === 2 ? 'success' : scope.row.type === 3 ? 'warning' : 'info'">
              {{ scope.row.type === 1 ? '单选题' : scope.row.type === 2 ? '多选题' : '是非题' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="题干" min-width="300">
          <template #default="scope">
            <div class="question-title">{{ scope.row.title.substring(0, 50) }}...</div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
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
    
    <!-- 创建/编辑题目对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="题型" prop="type">
          <el-select v-model="form.type" placeholder="请选择题型">
            <el-option label="单选题" :value="1"></el-option>
            <el-option label="多选题" :value="2"></el-option>
            <el-option label="是非题" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="题干" prop="title">
          <el-input v-model="form.title" type="textarea" :rows="4" placeholder="请输入题干内容"></el-input>
        </el-form-item>
        
        <!-- 单选题和多选题显示选项 -->
        <template v-if="form.type === 1 || form.type === 2">
          <el-form-item label="选项A" prop="optionA">
            <el-input v-model="form.optionA" placeholder="请输入选项A内容"></el-input>
          </el-form-item>
          <el-form-item label="选项B" prop="optionB">
            <el-input v-model="form.optionB" placeholder="请输入选项B内容"></el-input>
          </el-form-item>
          <el-form-item label="选项C" prop="optionC">
            <el-input v-model="form.optionC" placeholder="请输入选项C内容"></el-input>
          </el-form-item>
          <el-form-item label="选项D" prop="optionD">
            <el-input v-model="form.optionD" placeholder="请输入选项D内容"></el-input>
          </el-form-item>
          <el-form-item label="选项E" prop="optionE">
            <el-input v-model="form.optionE" placeholder="请输入选项E内容（可选）"></el-input>
          </el-form-item>
          <el-form-item label="选项F" prop="optionF">
            <el-input v-model="form.optionF" placeholder="请输入选项F内容（可选）"></el-input>
          </el-form-item>
        </template>
        
        <el-form-item label="正确答案" prop="correctAnswer">
          <template v-if="form.type === 3">
            <!-- 是非题只显示对错选项 -->
            <el-select v-model="form.correctAnswer" placeholder="请选择正确答案">
              <el-option label="对" value="对"></el-option>
              <el-option label="错" value="错"></el-option>
            </el-select>
          </template>
          <template v-else>
            <!-- 单选题和多选题显示ABCD等选项 -->
            <el-select v-model="form.correctAnswer" placeholder="请选择正确答案" multiple v-if="form.type === 2">
              <el-option label="A" value="A"></el-option>
              <el-option label="B" value="B"></el-option>
              <el-option label="C" value="C"></el-option>
              <el-option label="D" value="D"></el-option>
              <el-option label="E" value="E"></el-option>
              <el-option label="F" value="F"></el-option>
            </el-select>
            <el-select v-model="form.correctAnswer" placeholder="请选择正确答案" v-else>
              <el-option label="A" value="A"></el-option>
              <el-option label="B" value="B"></el-option>
              <el-option label="C" value="C"></el-option>
              <el-option label="D" value="D"></el-option>
              <el-option label="E" value="E"></el-option>
              <el-option label="F" value="F"></el-option>
            </el-select>
          </template>
        </el-form-item>
        <el-form-item label="解析" prop="analysis">
          <el-input v-model="form.analysis" type="textarea" :rows="4" placeholder="请输入解析内容"></el-input>
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
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form :model="importForm" label-width="100px">
        <!-- 模板说明 -->
        <el-card class="template-info-card">
          <h4>模板说明</h4>
          <ul class="template-rules">
            <li>1. 支持题型：单选题（1）、多选题（2）、是非题（3）</li>
            <li>2. 单选题/多选题必填：题型、题干、选项A-D、正确答案</li>
            <li>3. 是非题必填：题型、题干、正确答案（对/错）</li>
            <li>4. 多选题正确答案格式：A,B,C（逗号分隔）</li>
            <li>5. 支持选项E-F（可选）</li>
            <li>6. 解析为可选字段</li>
            <li>7. 支持XLSX、XLS格式，最大10MB</li>
          </ul>
        </el-card>
        
        <el-form-item label="导入文件" style="margin-top: 20px;">
          <el-upload
            class="upload-demo"
            action="#"
            :show-file-list="false"
            :before-upload="handleImport"
            accept=".xlsx,.xls"
          >
            <el-button type="primary">选择文件</el-button>
            <div class="el-upload__tip">
              支持 XLSX、XLS 格式，文件大小不超过 10MB
            </div>
          </el-upload>
        </el-form-item>
        
        <el-form-item>
          <el-button type="info" @click="handleDownloadTemplate">下载模板</el-button>
          <span class="template-desc">下载模板，按照模板格式填写题目后导入</span>
        </el-form-item>
        
        <!-- 导入预览 -->
        <el-card v-if="previewData.length > 0" class="preview-card">
          <h4>导入预览（前5条）</h4>
          <el-table :data="previewData" stripe style="width: 100%" size="small">
            <el-table-column prop="type" label="题型" width="80">
              <template #default="scope">
                {{ scope.row.type === 1 ? '单选题' : scope.row.type === 2 ? '多选题' : '是非题' }}
              </template>
            </el-table-column>
            <el-table-column prop="title" label="题干" min-width="200">
              <template #default="scope">
                {{ scope.row.title.substring(0, 30) }}...
              </template>
            </el-table-column>
            <el-table-column prop="correctAnswer" label="正确答案" width="120"></el-table-column>
            <el-table-column prop="status" label="状态" width="80">
              <template #default="scope">
                <el-tag :type="scope.row.status === 'valid' ? 'success' : 'danger'">
                  {{ scope.row.status === 'valid' ? '有效' : '无效' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
        
        <!-- 错误信息展示 -->
        <el-alert
          v-if="importError.length > 0"
          title="导入错误"
          type="error"
          :closable="false"
          style="margin-top: 15px;">
          <div v-for="(error, index) in importError" :key="index" class="error-item">
            <span class="error-index">第{{ error.row }}行：</span>
            <span class="error-message">{{ error.message }}</span>
          </div>
        </el-alert>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="importDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="handleImportSubmit"
            :loading="importLoading"
          >
            <el-icon v-if="importLoading"><Loading /></el-icon>
            开始导入
          </el-button>
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

// 题目数据
const papers = ref<any[]>([])

const searchForm = reactive({
  type: ''
})

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('增加题库')
const formRef = ref<FormInstance>()

// 题目表单数据
const form = reactive({
  id: '',
  type: 1, // 题型：1-单选题，2-多选题，3-是非题
  title: '', // 题干
  optionA: '', // 选项A
  optionB: '', // 选项B
  optionC: '', // 选项C
  optionD: '', // 选项D
  optionE: '', // 选项E
  optionF: '', // 选项F
  correctAnswer: '', // 正确答案
  analysis: '' // 解析
})

// 表单验证规则
const rules = reactive<FormRules>({
  type: [
    { required: true, message: '请选择题型', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入题干', trigger: 'blur' }
  ],
  optionA: [
    { required: true, message: '请输入选项A', trigger: 'blur', validator: (_rule, value, callback) => {
      // 只有单选题和多选题需要验证选项，是非题不需要
      if ((form.type === 1 || form.type === 2) && !value) {
        callback(new Error('请输入选项A'))
      } else {
        callback()
      }
    }}
  ],
  optionB: [
    { required: true, message: '请输入选项B', trigger: 'blur', validator: (_rule, value, callback) => {
      if ((form.type === 1 || form.type === 2) && !value) {
        callback(new Error('请输入选项B'))
      } else {
        callback()
      }
    }}
  ],
  optionC: [
    { required: true, message: '请输入选项C', trigger: 'blur', validator: (_rule, value, callback) => {
      if ((form.type === 1 || form.type === 2) && !value) {
        callback(new Error('请输入选项C'))
      } else {
        callback()
      }
    }}
  ],
  optionD: [
    { required: true, message: '请输入选项D', trigger: 'blur', validator: (_rule, value, callback) => {
      if ((form.type === 1 || form.type === 2) && !value) {
        callback(new Error('请输入选项D'))
      } else {
        callback()
      }
    }}
  ],
  correctAnswer: [
    { required: true, message: '请选择正确答案', trigger: 'change' }
  ]
})

// 导入题库相关
const importDialogVisible = ref(false)
const importForm = reactive({
  file: null as File | null
})

// 导入预览数据
const previewData = ref<any[]>([])
// 导入错误信息
const importError = ref<any[]>([])
// 导入加载状态
const importLoading = ref(false)
// 导入历史记录
const importHistory = ref<any[]>([])

// 获取题目列表
const fetchPapers = async () => {
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      type: searchForm.type
    }
    // 修复API地址，去掉/api前缀
    const response = await axios.get('/questions', { params })
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
    console.error('获取题目列表失败:', error)
    ElMessage.error('获取题目列表失败')
  }
}

// 搜索题目
const handleSearch = () => {
  currentPage.value = 1
  fetchPapers()
}

// 重置搜索
const handleReset = () => {
  searchForm.type = ''
  currentPage.value = 1
  fetchPapers()
}

// 增加题目
const handleAdd = () => {
  dialogTitle.value = '增加题库'
  // 重置表单
  Object.assign(form, {
    id: '',
    type: 1,
    title: '',
    optionA: '',
    optionB: '',
    optionC: '',
    optionD: '',
    correctAnswer: '',
    analysis: ''
  })
  dialogVisible.value = true
}

// 编辑题目
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑题库'
  // 填充表单
  Object.assign(form, {
    id: row.id,
    type: row.type,
    title: row.title,
    optionA: row.optionA,
    optionB: row.optionB,
    optionC: row.optionC,
    optionD: row.optionD,
    correctAnswer: row.correctAnswer,
    analysis: row.analysis
  })
  dialogVisible.value = true
}

// 提交题目
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 构建请求数据
    const requestData = {
      ...form
    }
    
    if (form.id) {
      // 更新题目
      await axios.put(`/questions/${form.id}`, requestData)
    } else {
      // 增加题目
      await axios.post('/questions', requestData)
    }
    
    ElMessage.success(form.id ? '更新题目成功' : '增加题目成功')
    dialogVisible.value = false
    fetchPapers() // 重新加载题目列表
  } catch (error) {
    console.error('表单验证失败:', error)
    ElMessage.error('操作失败，请检查表单')
  }
}

// 删除题目
const handleDelete = async (row: any) => {
  try {
    // 删除题目
    await axios.delete(`/questions/${row.id}`)
    ElMessage.success('删除题目成功')
    fetchPapers() // 重新加载题目列表
  } catch (error) {
    console.error('删除题目失败:', error)
    ElMessage.error('删除题目失败')
  }
}

// 打开导入题库对话框
const handleImportClick = () => {
  importDialogVisible.value = true
  // 重置表单
  Object.assign(importForm, {
    file: null
  })
}

// 下载题库模板
const handleDownloadTemplate = async () => {
  try {
    const response = await axios.get('/questions/import/template', {
      responseType: 'blob'
    })
    
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', '题目导入模板.xlsx')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('题目模板下载成功')
  } catch (error) {
    console.error('下载题目模板失败:', error)
    ElMessage.error('下载题目模板失败')
  }
}

// 导入XLSX库用于Excel文件解析
import * as XLSX from 'xlsx'
import { Loading } from '@element-plus/icons-vue'

// 处理文件选择
const handleImport = (file: any) => {
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('上传文件大小不能超过 10MB!')
    return false
  }
  
  // 验证文件类型
  const fileType = file.type
  const isXlsx = fileType === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  const isXls = fileType === 'application/vnd.ms-excel'
  
  if (!isXlsx && !isXls) {
    ElMessage.error('请上传 XLSX、XLS 格式的文件!')
    return false
  }
  
  // 保存文件对象
  importForm.file = file
  
  // 解析文件生成预览
  const reader = new FileReader()
  reader.onload = (e) => {
    try {
      // 获取文件数据
      const data = new Uint8Array(e.target?.result as ArrayBuffer)
      const workbook = XLSX.read(data, { type: 'array' })
      if (workbook.SheetNames.length === 0) {
        throw new Error('Excel文件中没有工作表')
      }
      const firstSheetName = workbook.SheetNames[0] as string
      const worksheet = workbook.Sheets[firstSheetName] as XLSX.WorkSheet
      
      // 解析为JSON格式
      const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 })
      
      // 生成预览数据
      generatePreview(jsonData)
      
      ElMessage.success('文件选择成功，已生成预览')
    } catch (error) {
      console.error('文件解析失败:', error)
      ElMessage.error('文件解析失败，请检查文件格式是否正确')
    }
  }
  reader.readAsArrayBuffer(file)
  
  return false // 阻止自动上传
}

// 生成导入预览
const generatePreview = (jsonData: any[]) => {
  // 清除之前的预览和错误信息
  previewData.value = []
  importError.value = []
  
  // 跳过表头行
  const headerRow = jsonData[0]
  const dataRows = jsonData.slice(1)
  
  // 检查表头是否正确
  const expectedHeaders = ['题型', '题干', '选项A', '选项B', '选项C', '选项D', '选项E', '选项F', '正确答案', '解析']
  const headerMatch = headerRow.every((header: any, index: number) => {
    return header === expectedHeaders[index]
  })
  
  if (!headerMatch) {
    importError.value.push({
      row: 1,
      message: '模板格式错误，请使用正确的导入模板'
    })
    return
  }
  
  // 生成预览数据（最多显示前5条）
  const previewRows = dataRows.slice(0, 5)
  const errors: any[] = []
  
  previewRows.forEach((row: any, index: number) => {
    const actualIndex = index + 2 // 实际行号，从2开始
    const [type, title, optionA, optionB, optionC, optionD, _optionE, _optionF, correctAnswer, _analysis] = row
    
    // 基本验证
    let status = 'valid'
    let errorMessage = ''
    
    if (!type || !title || !correctAnswer) {
      status = 'invalid'
      errorMessage = '缺少必填字段'
      errors.push({
        row: actualIndex,
        message: errorMessage
      })
    } else if ((type === 1 || type === 2) && (!optionA || !optionB || !optionC || !optionD)) {
      status = 'invalid'
      errorMessage = '单选/多选题缺少必填选项'
      errors.push({
        row: actualIndex,
        message: errorMessage
      })
    }
    
    previewData.value.push({
      type,
      title,
      correctAnswer,
      status
    })
  })
  
  // 保存错误信息
  importError.value = errors
}

// 检查完整数据
const validateImportData = (jsonData: any[]) => {
  const errors: any[] = []
  const dataRows = jsonData.slice(1) // 跳过表头
  
  dataRows.forEach((row: any, index: number) => {
    const actualIndex = index + 2 // 实际行号，从2开始
    const [type, title, optionA, optionB, optionC, optionD, _optionE, _optionF, correctAnswer, _analysis] = row
    
    // 验证必填字段
    if (!type || !title || !correctAnswer) {
      errors.push({
        row: actualIndex,
        message: '缺少必填字段（题型、题干、正确答案）'
      })
      return
    }
    
    // 验证题型
    if (![1, 2, 3].includes(type)) {
      errors.push({
        row: actualIndex,
        message: '题型错误，应为1（单选题）、2（多选题）或3（是非题）'
      })
      return
    }
    
    // 验证单选/多选题的选项
    if ((type === 1 || type === 2) && (!optionA || !optionB || !optionC || !optionD)) {
      errors.push({
        row: actualIndex,
        message: '单选/多选题缺少必填选项A-D'
      })
      return
    }
    
    // 验证是非题的正确答案
    if (type === 3 && !['对', '错'].includes(correctAnswer)) {
      errors.push({
        row: actualIndex,
        message: '是非题正确答案应为"对"或"错"'
      })
      return
    }
    
    // 验证多选题正确答案格式
    if (type === 2) {
      const answers = correctAnswer.split(',').map((a: string) => a.trim())
      const validAnswers = ['A', 'B', 'C', 'D', 'E', 'F']
      const invalidAnswer = answers.find((a: string) => !validAnswers.includes(a))
      if (invalidAnswer) {
        errors.push({
          row: actualIndex,
          message: `多选题正确答案格式错误，包含无效选项"${invalidAnswer}"，应为A,B,C格式`
        })
      }
    }
  })
  
  return errors
}

// 提交导入题库
const handleImportSubmit = async () => {
  try {
    if (!importForm.file) {
      ElMessage.error('请先选择文件')
      return
    }
    
    // 显示加载状态
    importLoading.value = true
    
    // 解析文件进行完整验证
    const reader = new FileReader()
    reader.readAsArrayBuffer(importForm.file)
    
    reader.onload = async (e) => {
      try {
        // 解析文件
        const data = new Uint8Array(e.target?.result as ArrayBuffer)
        const workbook = XLSX.read(data, { type: 'array' })
        if (workbook.SheetNames.length === 0) {
          throw new Error('Excel文件中没有工作表')
        }
        const firstSheetName = workbook.SheetNames[0] as string
        const worksheet = workbook.Sheets[firstSheetName] as XLSX.WorkSheet
        const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 })
        
        // 完整数据验证
        const validationErrors = validateImportData(jsonData)
        if (validationErrors.length > 0) {
          importError.value = validationErrors
          ElMessage.error(`数据验证失败，共${validationErrors.length}条错误`)
          importLoading.value = false
          return
        }
        
        // 提交数据
        const formData = new FormData()
        if (importForm.file) {
          formData.append('file', importForm.file)
        }
        
                await axios.post('/questions/import/batch', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        
        // 保存导入历史
        const importRecord = {
          id: Date.now(),
          fileName: importForm.file?.name || '未知文件名',
          importTime: new Date().toLocaleString(),
          success: true,
          count: jsonData.length - 1 // 减去表头行
        }
        importHistory.value.unshift(importRecord)
        
        ElMessage.success(`导入成功，共导入${jsonData.length - 1}条题目`)
        importDialogVisible.value = false
        fetchPapers() // 重新加载题目列表
        importLoading.value = false
      } catch (error: any) {
        console.error('导入题目失败:', error)
        ElMessage.error(`导入失败：${error.response?.data?.message || '服务器错误'}`)
        importLoading.value = false
      }
    }
    
    reader.onerror = (error) => {
      console.error('文件读取失败:', error)
      ElMessage.error('文件读取失败')
      importLoading.value = false
    }
  } catch (error: any) {
    console.error('导入题目失败:', error)
    ElMessage.error(`导入失败：${error.message}`)
    importLoading.value = false
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

/* 题干简要显示样式 */
.question-title {
  font-size: 14px;
  line-height: 1.4;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  word-break: break-word;
}

/* 模板下载说明样式 */
.template-desc {
  margin-left: 10px;
  font-size: 14px;
  color: #606266;
  vertical-align: middle;
}

/* 模板信息卡片样式 */
.template-info-card {
  margin-bottom: 20px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  background-color: #fafafa;
}

.template-info-card h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.template-rules {
  margin: 0;
  padding-left: 20px;
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
}

.template-rules li {
  margin-bottom: 5px;
}

/* 预览卡片样式 */
.preview-card {
  margin-top: 20px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.preview-card h4 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

/* 错误信息样式 */
.error-item {
  margin-bottom: 8px;
  font-size: 14px;
  line-height: 1.5;
}

.error-index {
  font-weight: bold;
  color: #f56c6c;
}

.error-message {
  color: #606266;
}
</style>



