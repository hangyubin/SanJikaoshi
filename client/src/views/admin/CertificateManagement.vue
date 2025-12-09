<template>
  <div class="certificate-management-container">
    <el-card class="main-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>证书管理</h2>
          <el-button type="primary" @click="handleAdd" size="default">
            <el-icon><Plus /></el-icon>
            新增证书
          </el-button>
        </div>
      </template>
      
      <!-- 筛选条件 -->
      <div class="filter-container">
        <el-input
          v-model="searchForm.name"
          placeholder="请输入证书名称"
          clearable
          style="width: 200px; margin-right: 10px;"
        >
          <template #prepend>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
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
      
      <!-- 证书列表 -->
      <el-table :data="certificates" style="width: 100%" :row-key="(row: any) => row.id" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="证书名称" min-width="150" />
        <el-table-column prop="description" label="证书描述" min-width="200" />
        <el-table-column prop="passScore" label="及格分数" width="100" />
        <el-table-column prop="validityPeriod" label="有效期(年)" width="120" />
        <el-table-column prop="subject.name" label="所属科目" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" :formatter="formatDate" />
        <el-table-column prop="updateTime" label="更新时间" width="180" :formatter="formatDate" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button type="primary" link @click="handleEdit(scope.row)">编辑</el-button>
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
    
    <!-- 新增/编辑证书对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增证书' : '编辑证书'"
      width="500px"
      destroy-on-close
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="证书名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入证书名称" />
        </el-form-item>
        
        <el-form-item label="证书描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入证书描述"
            :rows="3"
          />
        </el-form-item>
        
        <el-form-item label="及格分数" prop="passScore">
          <el-input-number
            v-model="form.passScore"
            :min="0"
            :max="100"
            placeholder="请输入及格分数"
          />
        </el-form-item>
        
        <el-form-item label="有效期(年)" prop="validityPeriod">
          <el-input-number
            v-model="form.validityPeriod"
            :min="1"
            :max="10"
            placeholder="请输入有效期(年)"
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
  name: '',
  status: undefined
})

// 分页信息
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 证书列表
const certificates = ref<any[]>([])

// 科目列表
const subjects = ref<any[]>([])

// 表单数据
const form = reactive({
  id: '',
  name: '',
  description: '',
  passScore: 60,
  validityPeriod: 3,
  subjectId: '',
  status: 1
})

// 表单验证规则
const rules = reactive({
  name: [
    { required: true, message: '请输入证书名称', trigger: 'blur' },
    { min: 2, max: 50, message: '证书名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  passScore: [
    { required: true, message: '请输入及格分数', trigger: 'blur' }
  ],
  validityPeriod: [
    { required: true, message: '请输入有效期', trigger: 'blur' }
  ],
  subjectId: [
    { required: true, message: '请选择所属科目', trigger: 'blur' }
  ]
})

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
  axios.get('/api/certificates', {
    params: {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
  })
  .then(response => {
    const { data } = response
    if (data.code === 200) {
      certificates.value = data.data
      pagination.total = data.total || 0
    }
  })
  .catch(error => {
    console.error('获取证书列表失败:', error)
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
    name: '',
    description: '',
    passScore: 60,
    validityPeriod: 3,
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
    name: row.name,
    description: row.description,
    passScore: row.passScore,
    validityPeriod: row.validityPeriod,
    subjectId: row.subject?.id || '',
    status: row.status
  })
  dialogVisible.value = true
}

// 处理删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该证书吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  .then(() => {
    axios.delete(`/api/certificates/${row.id}`)
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
      console.error('删除证书失败:', error)
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
        request = axios.post('/api/certificates', {
          ...form,
          subject: { id: form.subjectId }
        })
      } else {
        request = axios.put(`/api/certificates/${form.id}`, {
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
        console.error(dialogType.value === 'add' ? '新增证书失败:' : '编辑证书失败:', error)
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
.certificate-management-container {
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