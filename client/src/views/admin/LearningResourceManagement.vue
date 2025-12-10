<template>
  <div class="learning-resource-management-container">
    <el-card class="main-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>资源管理</h2>
          <el-button type="primary" @click="handleAdd" size="default">
            <el-icon><Plus /></el-icon>
            新增资源
          </el-button>
        </div>
      </template>
      
      <!-- 筛选条件 -->
      <div class="filter-container">
        <el-input
          v-model="searchForm.title"
          placeholder="请输入资源标题"
          clearable
          style="width: 200px; margin-right: 10px;"
        >
          <template #prepend>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <el-select
          v-model="searchForm.type"
          placeholder="请选择资源类型"
          clearable
          style="width: 150px; margin-right: 10px;"
        >
          <el-option label="文档" :value="1" />
          <el-option label="视频" :value="2" />
          <el-option label="音频" :value="3" />
          <el-option label="图片" :value="4" />
        </el-select>
        
        <el-select
          v-model="searchForm.status"
          placeholder="请选择状态"
          clearable
          style="width: 150px; margin-right: 10px;"
        >
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>
      
      <!-- 学习资源列表 -->
      <el-table :data="learningResources" style="width: 100%" :row-key="(row: any) => row.id" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="资源标题" min-width="200" />
        <el-table-column prop="description" label="资源描述" min-width="250" />
        <el-table-column prop="type" label="资源类型" width="100">
          <template #default="scope">
            <el-tag :type="getResourceTypeTag(scope.row.type).type">
              {{ getResourceTypeTag(scope.row.type).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="subject.name" label="所属科目" width="150" />
        <el-table-column prop="createBy.realName" label="创建人" width="120" />
        <el-table-column prop="fileType" label="文件类型" width="100" />
        <el-table-column prop="fileSize" label="文件大小" width="120">
          <template #default="scope">
            {{ formatFileSize(scope.row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" :formatter="formatDate" />
        <el-table-column prop="updateTime" label="更新时间" width="180" :formatter="formatDate" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handlePreview(scope.row)">预览</el-button>
            <el-button type="warning" link @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(scope.row)">删除</el-button>
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
    
    <!-- 新增/编辑资源对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增学习资源' : '编辑学习资源'"
      width="600px"
      destroy-on-close
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="资源标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入资源标题" />
        </el-form-item>
        
        <el-form-item label="资源描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入资源描述"
            :rows="3"
          />
        </el-form-item>
        
        <el-form-item label="资源链接" prop="url">
          <el-input v-model="form.url" placeholder="请输入资源链接（可选）" clearable />
          <div class="form-tip">提示：如果上传文件，资源链接将自动生成</div>
        </el-form-item>
        
        <el-form-item label="上传文件">
          <el-upload
            class="upload-demo"
            :auto-upload="false"
            :on-change="handleFileChange"
            :accept="'.' + supportedExtensions.join(',.')"
            :before-upload="beforeUpload"
            drag
            multiple
            :limit="1"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">
              将文件拖到此处，或 <em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持上传的文件格式：{{ supportedExtensions.join(', ') }}
              </div>
            </template>
          </el-upload>
          <div v-if="form.file" class="file-info">
            <el-icon><Document /></el-icon>
            <span>{{ form.file.name }}</span>
            <el-button type="danger" link size="small" @click="form.file = null">删除</el-button>
          </div>
        </el-form-item>
        
        <el-form-item label="所属科目" prop="subjectId">
          <el-select
            v-model="form.subjectId"
            placeholder="请选择所属科目"
            style="width: 100%"
          >
            <el-option label="学习资料" value="learning-materials" />
            <el-option
              v-for="subject in subjects"
              :key="subject.id"
              :label="subject.name"
              :value="subject.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="1">启用</el-radio>
            <el-radio label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确认</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus, Search, UploadFilled, Document } from '@element-plus/icons-vue'
import axios from '@/utils/axios'
import { ElMessage, ElMessageBox } from 'element-plus'

// 表单引用
const formRef = ref()

// 加载状态
const loading = ref(false)

// 对话框状态
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')

// 搜索表单
const searchForm = reactive({
  title: '',
  type: undefined,
  status: undefined
})

// 分页信息
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 学习资源列表
const learningResources = ref<any[]>([])

// 科目列表
const subjects = ref<any[]>([])

// 表单数据
const form = reactive({
  id: '',
  title: '',
  description: '',
  type: 1,
  url: '',
  fileType: '',
  fileSize: 0,
  subjectId: '',
  status: 1,
  file: null as File | null
})

// 支持的文件类型映射
const fileTypeMap = {
  'pdf': { label: 'PDF文档', type: 1 },
  'doc': { label: 'Word文档', type: 1 },
  'docx': { label: 'Word文档', type: 1 },
  'xls': { label: 'Excel表格', type: 1 },
  'xlsx': { label: 'Excel表格', type: 1 },
  'mp3': { label: '音频文件', type: 3 },
  'mp4': { label: '视频文件', type: 2 },
  'rm': { label: '视频文件', type: 2 },
  'rmvb': { label: '视频文件', type: 2 }
}

// 支持的文件扩展名
const supportedExtensions = ['pdf', 'doc', 'docx', 'xls', 'xlsx', 'mp3', 'mp4', 'rm', 'rmvb']

// 表单验证规则
const rules = reactive({
  title: [
    { required: true, message: '请输入资源标题', trigger: 'blur' },
    { min: 2, max: 100, message: '资源标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  subjectId: [
    { required: true, message: '请选择所属科目', trigger: 'blur' }
  ]
})

// 资源类型映射
const resourceTypeMap: { [key: number]: { label: string; type: string } } = {
  1: { label: '文档', type: 'info' },
  2: { label: '视频', type: 'success' },
  3: { label: '音频', type: 'warning' },
  4: { label: '图片', type: 'primary' }
}

// 获取资源类型标签
const getResourceTypeTag = (type: number) => {
  return resourceTypeMap[type] || { label: '未知', type: 'default' }
}

// 文件大小格式化
const formatFileSize = (bytes: number) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 日期格式化
const formatDate = (_row: any, _column: any, cellValue: any) => {
  if (!cellValue) return ''
  const date = new Date(cellValue)
  return date.toLocaleString('zh-CN')
}

// 获取科目列表 - 暂时保留，但将在后续改为固定值
const getSubjects = () => {
  axios.get('/subjects')
  .then(response => { if (response.code === 200) {
      subjects.value = response.data
    }
  })
  .catch(error => {
    console.error('获取科目列表失败:', error)
  })
}

// 初始化数据
const initData = () => {
  loading.value = true
  axios.get('/learning-resources', {
    params: {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
  })
  .then(response => { if (response.code === 200) {
      learningResources.value = response.data.records || response.data
      pagination.total = response.total || response.total || 0
    }
  })
  .catch(error => {
    console.error('获取学习资源列表失败:', error)
    ElMessage.error('获取学习资源列表失败')
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
    title: '',
    type: undefined,
    status: undefined
  })
  pagination.pageNum = 1
  initData()
}

// 处理新增
const handleAdd = () => {
  dialogType.value = 'add'
  Object.assign(form, {
    id: '',
    title: '',
    description: '',
    type: 1,
    url: '',
    fileType: '',
    fileSize: 0,
    subjectId: 'learning-materials',
    status: 1,
    file: null
  })
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  Object.assign(form, {
    id: row.id,
    title: row.title,
    description: row.description,
    type: row.type,
    url: row.url,
    fileType: row.fileType,
    fileSize: row.fileSize,
    subjectId: row.subject?.id || 'learning-materials',
    status: row.status,
    file: null
  })
  dialogVisible.value = true
}

// 处理预览
const handlePreview = (row: any) => {
  // 根据资源类型打开预览
  if (row.url) {
    // 获取文件扩展名
    const ext = row.fileType?.toLowerCase() || ''
    
    // PDF文件在线预览
    if (ext === 'pdf') {
      window.open(row.url, '_blank')
    }
    // 视频文件在线播放
    else if (['mp4', 'rm', 'rmvb'].includes(ext)) {
      ElMessageBox({
        title: '视频预览',
        message: `<video src="${row.url}" controls style="width: 100%; max-height: 500px;"></video>`,
        dangerouslyUseHTMLString: true,
        confirmButtonText: '关闭',
        customClass: 'video-preview-dialog'
      })
    }
    // 音频文件在线播放
    else if (ext === 'mp3') {
      ElMessageBox({
        title: '音频预览',
        message: `<audio src="${row.url}" controls style="width: 100%;"></audio>`,
        dangerouslyUseHTMLString: true,
        confirmButtonText: '关闭',
        customClass: 'audio-preview-dialog'
      })
    }
    // 文档文件下载
    else if (['doc', 'docx', 'xls', 'xlsx'].includes(ext)) {
      // 直接下载
      const a = document.createElement('a')
      a.href = row.url
      a.download = row.title || '学习资源'
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      ElMessage.success('文件下载中...')
    }
    // 其他类型，打开新窗口
    else {
      window.open(row.url, '_blank')
    }
  } else {
    ElMessage.warning('资源链接为空，无法预览')
  }
}

// 处理删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该学习资源吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  .then(() => {
    axios.delete(`/learning-resources/${row.id}`)
    .then(response => { if (response.code === 200) {
        ElMessage.success('删除成功')
        initData()
      } else {
        ElMessage.error(data.message || '删除失败')
      }
    })
    .catch(error => {
      console.error('删除学习资源失败:', error)
      ElMessage.error('删除失败')
    })
  })
  .catch(() => {
    // 用户取消删除
  })
}

// 文件变化处理
const handleFileChange = (file: any) => {
  form.file = file.raw
  
  // 自动识别文件类型和大小
  const fileName = file.name
  const ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase()
  
  // 设置文件类型和资源类型
  const fileTypeInfo = fileTypeMap[ext as keyof typeof fileTypeMap]
  if (fileTypeInfo) {
    form.fileType = ext
    form.type = fileTypeInfo.type
  } else {
    form.fileType = ext
    form.type = 1 // 默认文档类型
  }
  
  // 设置文件大小
  form.fileSize = file.size
}

// 上传前校验
const beforeUpload = (file: any) => {
  const fileName = file.name
  const ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase()
  
  if (!supportedExtensions.includes(ext)) {
    ElMessage.error(`不支持的文件格式，请上传：${supportedExtensions.join(', ')}`)
    return false
  }
  
  // 可以添加文件大小限制，例如50MB
  const maxSize = 50 * 1024 * 1024 // 50MB
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过50MB')
    return false
  }
  
  return true
}

// 处理提交
const handleSubmit = () => {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      loading.value = true
      
      // 如果有文件，先上传文件
      if (form.file) {
        const formData = new FormData()
        formData.append('file', form.file)
        formData.append('title', form.title)
        formData.append('description', form.description)
        formData.append('type', form.type.toString())
        formData.append('status', form.status.toString())
        formData.append('subjectId', form.subjectId)
        
        if (form.url) {
          formData.append('url', form.url)
        }
        
        let request
        if (dialogType.value === 'add') {
          request = axios.post('/learning-resources/upload', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          })
        } else {
          formData.append('id', form.id)
          request = axios.put('/learning-resources/upload', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          })
        }
        
        request.then(response => { if (response.code === 200) {
            ElMessage.success(dialogType.value === 'add' ? '新增成功' : '编辑成功')
            dialogVisible.value = false
            initData()
          } else {
            ElMessage.error(data.message || (dialogType.value === 'add' ? '新增失败' : '编辑失败'))
          }
        })
        .catch(error => {
          console.error(dialogType.value === 'add' ? '新增学习资源失败:' : '编辑学习资源失败:', error)
          ElMessage.error(dialogType.value === 'add' ? '新增失败' : '编辑失败')
        })
        .finally(() => {
          loading.value = false
        })
      } else {
        // 没有文件，直接提交表单
        let request
        if (dialogType.value === 'add') {
          request = axios.post('/learning-resources', {
            ...form,
            subject: { id: form.subjectId }
          })
        } else {
          request = axios.put(`/learning-resources/${form.id}`, {
            ...form,
            subject: { id: form.subjectId }
          })
        }
        
        request.then(response => { if (response.code === 200) {
            ElMessage.success(dialogType.value === 'add' ? '新增成功' : '编辑成功')
            dialogVisible.value = false
            initData()
          } else {
            ElMessage.error(data.message || (dialogType.value === 'add' ? '新增失败' : '编辑失败'))
          }
        })
        .catch(error => {
          console.error(dialogType.value === 'add' ? '新增学习资源失败:' : '编辑学习资源失败:', error)
          ElMessage.error(dialogType.value === 'add' ? '新增失败' : '编辑失败')
        })
        .finally(() => {
          loading.value = false
        })
      }
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
.learning-resource-management-container {
  padding: 0;
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

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.file-info {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 6px;
}

.video-preview-dialog {
  max-width: 800px;
}

.audio-preview-dialog {
  max-width: 600px;
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



