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
          <el-input 
            v-model="form.name" 
            placeholder="请输入科室名称" 
            @keyup.enter="handleNameInput" 
            @blur="handleNameInput"
          />
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
  
  // 添加内儿科的特殊映射
  if (cleanedName === '内儿科') {
    return 'NEK'
  }
  
  // 简化的拼音缩写映射，使用更直观的缩写
  const pinyinMap: Record<string, string> = {
    // 一级科室
    '内科': 'NK',
    '外科': 'WK',
    '儿科': 'EK',
    '内儿科': 'NEK', // 新增内儿科映射
    '妇产科': 'FCK',
    '眼科': 'YK',
    '耳鼻喉科': 'EBHK',
    '口腔科': 'KQK',
    '皮肤科': 'PFK',
    '神经内科': 'SNK',
    '消化内科': 'XHK',
    '呼吸内科': 'HXK',
    '心血管内科': 'XXGK',
    '血液内科': 'XYK',
    '肾内科': 'SNK',
    '内分泌科': 'NMFK',
    '风湿免疫科': 'FSK',
    '感染科': 'GRK',
    '肿瘤科': 'ZLK',
    '放射科': 'FSK',
    '麻醉科': 'MZK',
    '急诊科': 'JZK',
    '重症医学科': 'ZZK',
    '康复科': 'FK',
    '中医科': 'ZYK',
    '中西医结合科': 'ZXK',
    '体检科': 'TJK',
    '药剂科': 'YJK',
    '检验科': 'JK',
    '病理科': 'BLK',
    '超声科': 'CSK',
    '心电图室': 'XDT',
    'CT室': 'CT',
    'MRI室': 'MRI',
    '介入科': 'JRK',
    '营养科': 'YYK',
    '护理部': 'HLB',
    '院感科': 'YGK',
    '预防保健科': 'YFK',
    '公共卫生科': 'GWK',
    '行政科': 'XZK',
    '后勤科': 'HQK',
    '财务科': 'CWK',
    '人事科': 'RSK',
    '信息科': 'XXK',
    // 单个字的映射
    '内': 'NK',
    '外': 'WK',
    '儿': 'EK',
    '眼': 'YK',
    '口': 'KQK',
    '皮': 'PFK',
    '神': 'SNK',
    '消': 'XHK',
    '呼': 'HXK',
    '心': 'XXGK',
    '血': 'XYK',
    '肾': 'SNK',
    '肿': 'ZLK',
    '放': 'FSK',
    '麻': 'MZK',
    '急': 'JZK',
    '重': 'ZZK',
    '康': 'FK',
    '中': 'ZYK',
    '体': 'TJK',
    '药': 'YJK',
    '检': 'JK',
    '病': 'BLK',
    '超': 'CSK'
  }
  
  // 1. 优先匹配完整科室名称
  if (pinyinMap[cleanedName]) {
    return pinyinMap[cleanedName]
  }
  
  // 2. 匹配科室名称包含的关键词，优先匹配最长的关键词
  // 将关键词按长度排序，优先匹配更长的关键词
  const sortedKeywords = Object.keys(pinyinMap).sort((a, b) => b.length - a.length)
  for (const keyword of sortedKeywords) {
    if (cleanedName.includes(keyword)) {
      const code = pinyinMap[keyword]
      if (code) {
        return code
      }
    }
  }
  
  // 3. 如果没有匹配，使用名称的拼音首字母缩写
  // 处理中文名称，生成拼音首字母缩写
  let code = ''
  for (let i = 0; i < cleanedName.length && i < 4; i++) {
    const char = cleanedName[i]
    if (char) {
      if (/[a-zA-Z]/.test(char)) {
        // 英文直接使用
        code += char.toUpperCase()
      } else {
        // 中文取首字母，简化处理
        const charCode = char.charCodeAt(0)
        if (charCode >= 0x4e00 && charCode <= 0x9fa5) {
          // 这里使用简单的拼音首字母映射，实际项目中可以使用pinyin库
          const pinyinFirstLetterMap: Record<string, string> = {
            '一': 'Y', '乙': 'Y', '二': 'E', '三': 'S', '四': 'S', '五': 'W',
            '六': 'L', '七': 'Q', '八': 'B', '九': 'J', '十': 'S',
            '内': 'N', '儿': 'E', '科': 'K' // 新增常用字的拼音首字母
          }
          const pinyinChar = char as string
          code += pinyinFirstLetterMap[pinyinChar] || pinyinChar.charAt(0).toUpperCase()
        } else {
          // 其他字符直接使用
          code += char.toUpperCase()
        }
      }
    }
  }
  
  // 确保编码至少3个字符
  while (code.length < 3) {
    code += '0'
  }
  
  return code
}

// 监听科室名称变化，自动生成编码
watch(() => form.name, (newName) => {
  if (dialogType.value === 'add') {
    // 如果科室名称为空，清除编码
    if (!newName || newName.trim() === '') {
      form.code = ''
    } else if (!form.code || form.code.trim() === '') {
      form.code = generateDepartmentCode(newName)
    }
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

// 处理科室名称输入事件（回车或失去焦点）
const handleNameInput = () => {
  if (dialogType.value === 'add') {
    const name = form.name.trim()
    if (name) {
      form.code = generateDepartmentCode(name)
    } else {
      form.code = ''
    }
  }
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
    
    // 简化请求数据，确保只包含必要字段
    const requestData = {
      name: form.name.trim(),
      code: form.code.trim(),
      description: form.description.trim(),
      status: form.status
    }
    
    let response
    if (dialogType.value === 'add') {
      // 新增科室，使用POST请求，不设置特殊headers
      response = await axios.post('/departments', requestData)
    } else {
      // 编辑科室，使用PUT请求，不设置特殊headers
      response = await axios.put(`/departments/${form.id}`, requestData)
    }
    
    // 检查响应数据，使用更宽松的判断条件
    if (response && response.status >= 200 && response.status < 300) {
      ElMessage.success(dialogType.value === 'add' ? '新增成功' : '编辑成功')
      dialogVisible.value = false
      initData() // 刷新列表
    } else {
      ElMessage.error(dialogType.value === 'add' ? '新增失败' : '编辑失败')
    }
  } catch (error: any) {
    console.error(dialogType.value === 'add' ? '新增科室失败:' : '编辑科室失败:', error)
    
    // 处理不同类型的错误
    if (error.response) {
      // 服务器返回了错误响应
      const errorMsg = error.response.data?.message || error.response.statusText || (dialogType.value === 'add' ? '新增失败' : '编辑失败')
      ElMessage.error(errorMsg)
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



