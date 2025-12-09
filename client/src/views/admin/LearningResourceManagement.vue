<template>
  <div class="learning-resource-management-container">
    <el-card class="main-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>学习资源管理</h2>
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
        
        <el-form-item label="资源类型" prop="type">
          <el-select
            v-model="form.type"
            placeholder="请选择资源类型"
            style="width: 100%"
          >
            <el-option label="文档" :value="1" />
            <el-option label="视频" :value="2" />
            <el-option label="音频" :value="3" />
            <el-option label="图片" :value="4" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="资源链接" prop="url">
          <el-input v-model="form.url" placeholder="请输入资源链接" />
        </el-form-item>
        
        <el-form-item label="文件类型" prop="fileType">
          <el-input v-model="form.fileType" placeholder="请输入文件类型（如：pdf, mp4, jpg）" />
        </el-form-item>
        
        <el-form-item label="文件大小" prop="fileSize">
          <el-input-number
            v-model="form.fileSize"
            :min="0"
            placeholder="请输入文件大小（字节）"
          />
        </el-form-item>
        
        <el-form-item label="所属科目" prop="subjectId">
          <el-select
            v-model="form.subjectId"
            placeholder="请选择所属科目"
            style="width: 100%"
          >
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
import { Plus, Search } from '@element-plus/icons-vue'
import axios from '@/utils/axios'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'

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
  status: 1
})

// 表单验证规则
const rules = reactive({
  title: [
    { required: true, message: '请输入资源标题', trigger: 'blur' },
    { min: 2, max: 100, message: '资源标题长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择资源类型', trigger: 'blur' }
  ],
  url: [
    { required: true, message: '请输入资源链接', trigger: 'blur' }
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

// 获取科目列表
const getSubjects = () => {
  axios.get('/api/subjects')
  .then(response => {
    const { data } = response
    if (data.code === 200) {
      subjects.value = data.data
    }
  })
  .catch(error => {
    console.error('获取科目列表失败:', error)
  })
}

// 初始化数据
const initData = () => {
  loading.value = true
  axios.get('/api/learning-resources', {
    params: {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
  })
  .then(response => {
    const { data } = response
    if (data.code === 200) {
      learningResources.value = data.data
      pagination.total = data.total || 0
    }
  })
  .catch(error => {
    console.error('获取学习资源列表失败:', error)
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
    subjectId: '',
    status: 1
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
    subjectId: row.subject?.id || '',
    status: row.status
  })
  dialogVisible.value = true
}

// 处理预览
const handlePreview = (row: any) => {
  // 根据资源类型打开预览
  if (row.url) {
    // 如果是图片，直接预览
    if (row.type === 4) {
      ElNotification({
        title: '图片预览',
        message: `<img src="${row.url}" style="max-width: 100%; max-height: 300px;" />`,
        dangerouslyUseHTMLString: true,
        duration: 5000
      })
    } else {
      // 其他类型，打开新窗口
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
    axios.delete(`/api/learning-resources/${row.id}`)
    .then(response => {
      const { data } = response
      if (data.code === 200) {
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

// 处理提交
const handleSubmit = () => {
  formRef.value?.validate((valid: boolean) => {
    if (valid) {
      loading.value = true
      let request
      if (dialogType.value === 'add') {
        request = axios.post('/api/learning-resources', {
          ...form,
          subject: { id: form.subjectId }
        })
      } else {
        request = axios.put(`/api/learning-resources/${form.id}`, {
          ...form,
          subject: { id: form.subjectId }
        })
      }
      
      request.then(response => {
        const { data } = response
        if (data.code === 200) {
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