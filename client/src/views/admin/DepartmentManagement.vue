<template>
  <div class="department-management-container">
    <el-card class="main-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2>科室管理</h2>
          <el-button type="primary" @click="handleAdd" size="default">
            <el-icon><Plus /></el-icon>
            新增科室
          </el-button>
        </div>
      </template>
      
      <!-- 筛选条件 -->
      <div class="filter-container">
        <el-input
          v-model="searchForm.name"
          placeholder="请输入科室名称"
          clearable
          style="width: 200px; margin-right: 10px;"
        >
          <template #prepend>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <el-input
          v-model="searchForm.code"
          placeholder="请输入科室编码"
          clearable
          style="width: 200px; margin-right: 10px;"
        >
          <template #prepend>
            <el-icon><Ticket /></el-icon>
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
      
      <!-- 科室列表 -->
      <el-table :data="departments" style="width: 100%" :row-key="(row: any) => row.id" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="科室名称" min-width="150" />
        <el-table-column prop="code" label="科室编码" min-width="120" />
        <el-table-column prop="description" label="科室描述" min-width="200" />
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
    
    <!-- 新增/编辑科室对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增科室' : '编辑科室'"
      width="500px"
      destroy-on-close
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="科室名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入科室名称" />
        </el-form-item>
        
        <el-form-item label="科室编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入科室编码" />
        </el-form-item>
        
        <el-form-item label="科室描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入科室描述"
            :rows="3"
          />
        </el-form-item>
        
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
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
import { ref, reactive, onMounted, watch } from 'vue'
import { Plus, Search, Ticket } from '@element-plus/icons-vue'
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
  code: '',
  status: undefined
})

// 分页信息
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 科室列表
const departments = ref<any[]>([])

// 表单数据
const form = reactive({
  id: null,
  name: '',
  code: '',
  description: '',
  status: 1
})

// 表单验证规则
const rules = reactive({
  name: [
    { required: true, message: '请输入科室名称', trigger: 'blur' },
    { min: 2, max: 50, message: '科室名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入科室编码', trigger: 'blur' },
    { min: 1, max: 20, message: '科室编码长度在 1 到 20 个字符', trigger: 'blur' }
  ]
})

// 日期格式化
const formatDate = (_row: any, _column: any, cellValue: any) => {
  if (!cellValue) return ''
  const date = new Date(cellValue)
  return date.toLocaleString('zh-CN')
}

// 初始化数据
const initData = () => {
  loading.value = true
  axios.get('/departments', {
    params: {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
  })
  .then(res => {
      // 适应后端返回的分页格式
      if (res.data.records) {
        departments.value = res.data.records
        pagination.total = res.data.total || 0
      } else {
        // 直接列表格式
        departments.value = res.data || []
        pagination.total = res.data?.length || 0
      }
    }
  )
  .catch(error => {
    console.error('获取科室列表失败:', error)
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
    code: '',
    status: undefined
  })
  pagination.pageNum = 1
  initData()
}

// 自动生成科室编码
const generateDepartmentCode = (name: string): string => {
  if (!name) return ''
  
  // 移除空格，转换为小写
  const cleanedName = name.trim().toLowerCase()
  
  // 生成拼音首字母缩写
  const pinyinMap: Record<string, string> = {
    '内': 'nei',
    '外': 'wai',
    '儿': 'er',
    '妇产': 'fuchan',
    '眼': 'yan',
    '耳鼻喉': 'ebhk',
    '口腔': 'kouqiang',
    '皮肤': 'pifu',
    '神经': 'shenjing',
    '消化': 'xiaohua',
    '呼吸': 'huxi',
    '心血管': 'xinxueguan',
    '血液': 'xueye',
    '肾内': 'shennei',
    '内分泌': 'neifenmi',
    '风湿': 'fengshi',
    '免疫': 'mianyi',
    '感染': 'ganran',
    '肿瘤': 'zhongliu',
    '放射': 'fangshe',
    '麻醉': 'mazui',
    '急诊': 'jizhen',
    '重症': 'zhongzheng',
    '康复': 'kangfu',
    '中医': 'zhongyi',
    '中西医': 'zhongxiyi',
    '体检': 'tijian',
    '药剂': 'yaoji',
    '检验': 'jianyan',
    '病理': 'bingli',
    '超声': 'chaosheng',
    '心电图': 'xindiantu',
    'CT': 'ct',
    'MRI': 'mri',
    '介入': 'jieru',
    '营养': 'yingyang',
    '护理': 'huli',
    '院感': 'yuangan',
    '预防': 'yufang',
    '公共卫生': 'gonggongweisheng',
    '行政': 'xingzheng',
    '后勤': 'houqin',
    '财务': 'caiwu',
    '人事': 'renshi',
    '信息': 'xinxi'
  }
  
  // 复合科室名称处理，优先匹配最长的关键词
  // 按关键词长度降序排序
  const sortedKeys = Object.keys(pinyinMap).sort((a, b) => b.length - a.length)
  
  // 检查是否有匹配的科室名称
  for (const key of sortedKeys) {
    if (cleanedName.includes(key)) {
      // 提取匹配的关键词
      const matchedValue = pinyinMap[key]
      // 检查是否还有其他关键词
      const remainingName = cleanedName.replace(key, '')
      
      // 查找剩余名称中的其他关键词
      for (const subKey of sortedKeys) {
        if (remainingName.includes(subKey) && subKey !== key) {
          const subValue = pinyinMap[subKey]
          if (matchedValue && subValue) {
            return (matchedValue + subValue).toUpperCase()
          }
        }
      }
      
      if (matchedValue) {
        return matchedValue.toUpperCase()
      }
    }
  }
  
  // 如果没有匹配，使用名称的拼音首字母
  // 这里简化处理，使用前3个字母
  return cleanedName.slice(0, 3).toUpperCase()
}

// 监听科室名称变化，自动生成编码
watch(() => form.name, (newName) => {
  if (dialogType.value === 'add' && !form.code) {
    form.code = generateDepartmentCode(newName)
  }
}, { immediate: true })

// 处理新增
const handleAdd = () => {
  dialogType.value = 'add'
  Object.assign(form, {
    id: null,
    name: '',
    code: '',
    description: '',
    status: 1
  })
  dialogVisible.value = true
}

// 处理编辑
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 处理删除
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该科室吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
  .then(() => {
    axios.delete(`/departments/${row.id}`)
    .then(() => {
        ElMessage.success('删除成功')
        initData()
      })
    .catch(error => {
      console.error('删除科室失败:', error)
      ElMessage.error('删除失败')
    })
  })
  .catch(() => {
    // 用户取消删除
  })
}

// 处理提交
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    loading.value = true
    
    let response
    if (dialogType.value === 'add') {
      response = await axios.post('/departments', form, {
        headers: {
          'Content-Type': 'application/json;charset=UTF-8'
        }
      })
    } else {
      response = await axios.put(`/departments/${form.id}`, form, {
        headers: {
          'Content-Type': 'application/json;charset=UTF-8'
        }
      })
    }
    
    // 检查响应数据
    if (response.data) {
      ElMessage.success(dialogType.value === 'add' ? '新增成功' : '编辑成功')
      dialogVisible.value = false
      initData() // 刷新列表
    } else {
      ElMessage.error(dialogType.value === 'add' ? '新增失败，服务器返回无效数据' : '编辑失败，服务器返回无效数据')
    }
  } catch (error: any) {
    console.error(dialogType.value === 'add' ? '新增科室失败:' : '编辑科室失败:', error)
    
    // 处理不同类型的错误
    if (error.response) {
      // 服务器返回了错误响应
      ElMessage.error(error.response.data?.message || (dialogType.value === 'add' ? '新增失败' : '编辑失败'))
    } else if (error.request) {
      // 请求已发送但没有收到响应
      ElMessage.error('服务器无响应，请稍后重试')
    } else {
      // 请求配置出错
      ElMessage.error(error.message || (dialogType.value === 'add' ? '新增失败' : '编辑失败'))
    }
  } finally {
    loading.value = false
  }
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
  initData()
})
</script>

<style scoped>
.department-management-container {
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



