<template>
  <div class="question-management">
    <h1>题目管理</h1>
    
    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="80px" inline>
        <el-form-item label="题目内容">
          <el-input v-model="searchForm.content" placeholder="请输入题目内容"></el-input>
        </el-form-item>
        <el-form-item label="科目">
          <el-select v-model="searchForm.subjectId" placeholder="请选择科目">
            <el-option label="全部" value=""></el-option>
            <el-option v-for="subject in subjects" :key="subject.id" :label="subject.name" :value="subject.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="题目类型">
          <el-select v-model="searchForm.type" placeholder="请选择题目类型">
            <el-option label="全部" value=""></el-option>
            <el-option label="单选题" :value="1"></el-option>
            <el-option label="多选题" :value="2"></el-option>
            <el-option label="是非题" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="searchForm.difficulty" placeholder="请选择难度">
            <el-option label="全部" value=""></el-option>
            <el-option label="简单" :value="1"></el-option>
            <el-option label="中等" :value="2"></el-option>
            <el-option label="困难" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">添加题目</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="warning" @click="handleGenerateTemplate">生成模板</el-button>
          <el-upload
            class="upload-demo"
            action=""
            :show-file-list="false"
            :before-upload="handleBatchImport"
            accept=".xlsx,.xls,.csv"
          >
            <el-button type="info">Excel/CSV批量导入</el-button>
          </el-upload>
          <el-upload
            class="upload-demo"
            action=""
            :show-file-list="false"
            :before-upload="handleTextFileImport"
            accept=".txt,.md"
          >
            <el-button type="success">文本文件导入</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 题目表格 -->
    <el-card class="table-card" style="margin-top: 20px;">
      <el-table :data="questions" stripe style="width: 100%">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="subject" label="科目" width="120"></el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.type === 1 ? 'primary' : scope.row.type === 2 ? 'success' : scope.row.type === 3 ? 'warning' : 'info'">
              {{ getQuestionType(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.difficulty === 1 ? 'success' : scope.row.difficulty === 2 ? 'warning' : 'danger'">
              {{ getDifficultyText(scope.row.difficulty) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="题目内容" min-width="300" show-overflow-tooltip></el-table-column>
        <el-table-column label="选项" min-width="250" show-overflow-tooltip>
          <template #default="scope">
            <div class="option-list">
              <div v-if="scope.row.options" class="option-item" v-for="(option, key) in parseOptions(scope.row.options)" :key="key">
                <span class="option-key">{{ key }}. </span>
                <span class="option-value">{{ option }}</span>
              </div>
              <span v-else class="empty-text">无选项</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="正确答案" width="100">
          <template #default="scope">
            <el-tag type="success" v-if="scope.row.answer">{{ scope.row.answer }}</el-tag>
            <span v-else class="empty-text">未设置</span>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分值" width="80"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
            <el-button type="info" size="small" @click="handleView(scope.row)">查看详情</el-button>
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
    
    <!-- 添加/编辑题目对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="科目" prop="subjectId">
          <el-select v-model="form.subjectId" placeholder="请选择科目">
            <el-option v-for="subject in subjects" :key="subject.id" :label="subject.name" :value="subject.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="题目类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择题目类型">
            <el-option label="单选题" :value="1"></el-option>
            <el-option label="多选题" :value="2"></el-option>
            <el-option label="是非题" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="难度" prop="difficulty">
          <el-select v-model="form.difficulty" placeholder="请选择难度">
            <el-option label="简单" :value="1"></el-option>
            <el-option label="中等" :value="2"></el-option>
            <el-option label="困难" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="题目分值" prop="score">
          <el-input-number v-model="form.score" :min="1" :max="100" :step="1"></el-input-number>
        </el-form-item>
        <el-form-item label="题目内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入题目内容"></el-input>
        </el-form-item>
        
        <!-- 选择题选项 -->
        <!-- 选择题选项，所有题型都显示 -->
        <el-form-item label="选项" prop="options">
          <div 
            v-for="(_, key) in optionsObject" 
            :key="key" 
            class="option-item"
          >
            <el-input 
              v-model="optionsObject[key]"
              placeholder="请输入选项内容"
            ></el-input>
          </div>
          <!-- 添加选项按钮 -->
          <el-button 
            type="text" 
            @click="addOption" 
            v-if="Object.keys(optionsObject).length < 5"
          >
            <el-icon><Plus /></el-icon> 添加选项
          </el-button>
        </el-form-item>
        
        <el-form-item label="正确答案" prop="answer">
          <el-input v-model="form.answer" placeholder="请输入正确答案"></el-input>
          <div v-if="form.type === 1" class="answer-hint">
            提示：选择题答案请输入选项字母，如A、B、C、D
          </div>
        </el-form-item>
        
        <el-form-item label="解析" prop="analysis">
          <el-input v-model="form.analysis" type="textarea" :rows="3" placeholder="请输入题目解析"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import axios from '@/utils/axios'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

// 真实题目数据
const questions = ref<any[]>([])
const subjects = ref<any[]>([])

const searchForm = reactive({
  content: '',
  subjectId: '',
  type: '',
  difficulty: ''
})

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('添加题目')
const formRef = ref<FormInstance>()

const form = reactive({
  id: '',
  subjectId: '',
  type: 1,
  difficulty: 1,
  content: '',
  options: '', // 存储JSON格式的选项
  answer: '',
  analysis: '',
  score: 10
})

// 选项对象，用于表单编辑
const optionsObject = reactive<Record<string, string>>({
  A: '',
  B: '',
  C: '',
  D: '',
  E: ''
})

const rules = reactive<FormRules>({
  subjectId: [
    { required: true, message: '请选择科目', trigger: 'change' }
  ],
  type: [
    { required: true, message: '请选择题目类型', trigger: 'change' }
  ],
  difficulty: [
    { required: true, message: '请选择难度', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入题目内容', trigger: 'blur' }
  ],
  answer: [
    { required: true, message: '请输入正确答案', trigger: 'blur' }
  ],
  score: [
    { required: true, message: '请输入题目分值', trigger: 'blur' }
  ]
})

// 获取题目类型文本
const getQuestionType = (type: number) => {
  switch (type) {
    case 1: return '单选题'
    case 2: return '多选题'
    case 3: return '是非题'
    default: return '未知类型'
  }
}

// 获取难度文本
const getDifficultyText = (difficulty: number) => {
  switch (difficulty) {
    case 1: return '简单'
    case 2: return '中等'
    case 3: return '困难'
    default: return '未知难度'
  }
}

// 解析选项
const parseOptions = (options: string) => {
  if (!options) return {};
  try {
    const parsed = JSON.parse(options);
    if (typeof parsed === 'object' && parsed !== null) {
      return parsed;
    }
  } catch (e) {
    // 解析失败，返回空对象
  }
  return {};
}

// 获取科目列表
const fetchSubjects = async () => {
  try {
    const res = await axios.get('/subjects')
    subjects.value = res.data
  } catch (error) {
    console.error('获取科目列表失败:', error)
    ElMessage.error('获取科目列表失败')
  }
}

// 获取题目列表
const fetchQuestions = async () => {
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      content: searchForm.content,
      subjectId: searchForm.subjectId,
      type: searchForm.type,
      difficulty: searchForm.difficulty
    }
    const res = await axios.get('/questions', { params })
    questions.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取题目列表失败:', error)
    // 清除模拟数据，只使用真实API调用
    questions.value = []
    total.value = 0
    ElMessage.error('获取题目列表失败')
  }
}

// 搜索题目
const handleSearch = () => {
  currentPage.value = 1
  fetchQuestions()
}

// 重置搜索
const handleReset = () => {
  searchForm.content = ''
  searchForm.subjectId = ''
  searchForm.type = ''
  searchForm.difficulty = ''
  currentPage.value = 1
  fetchQuestions()
}

// 生成题目模板
const handleGenerateTemplate = async () => {
  try {
    // 检查后端是否支持该端点
    ElMessage.info('正在生成题目模板...')
    
    // 尝试不同的API端点
    let response
    try {
      // 尝试1：标准端点
      response = await axios.get('/api/questions/import/template', { 
        responseType: 'blob' 
      })
    } catch (e1) {
      try {
        // 尝试2：简化端点
        response = await axios.get('/questions/template', { 
          responseType: 'blob' 
        })
      } catch (e2) {
        // 尝试3：最简化端点
        response = await axios.get('/template', { 
          responseType: 'blob' 
        })
      }
    }
    
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', '题目导入模板.xlsx')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('题目模板生成成功')
  } catch (error) {
    console.error('生成题目模板失败:', error)
    ElMessage.error('生成题目模板失败，请检查后端是否支持该功能')
  }
}



// 文本文件导入
const handleTextFileImport = async (file: any) => {
  try {
    // 检查文件大小（不超过10MB）
    const maxSize = 10 * 1024 * 1024; // 10MB
    if (file.size > maxSize) {
      ElMessage.error('文件大小不能超过 10MB!');
      return false;
    }
    
    const formData = new FormData();
    formData.append('file', file);
    
    // 直接上传文件到后端，由后端处理解析
    let res;
    try {
      // 尝试标准端点
      res = await axios.post('/api/questions/import/txt', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
    } catch (e1) {
      try {
        // 尝试简化端点
        res = await axios.post('/questions/import/txt', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
      } catch (e2) {
        // 尝试兼容原批量导入端点
        res = await axios.post('/questions/import', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
      }
    }
    
    const result = res.data;
    ElMessage.success(`文本文件导入成功：共导入${result.success || result.length || 0}条`);
    fetchQuestions(); // 重新加载题目列表
  } catch (error) {
    console.error('文本文件导入失败:', error);
    ElMessage.error('文本文件导入失败，请检查文件格式或后端接口');
  }
  return false; // 阻止自动上传
};

// Excel批量导入题目
const handleBatchImport = async (file: any) => {
  try {
    // 检查文件类型
    const fileName = file.name;
    const fileExtension = fileName.substring(fileName.lastIndexOf('.')).toLowerCase();
    const allowedExtensions = ['.xlsx', '.xls', '.csv'];
    
    if (!allowedExtensions.includes(fileExtension)) {
      ElMessage.error('请上传 XLSX、XLS 或 CSV 格式的文件!');
      return false;
    }
    
    // 检查文件大小（不超过10MB）
    const maxSize = 10 * 1024 * 1024; // 10MB
    if (file.size > maxSize) {
      ElMessage.error('文件大小不能超过 10MB!');
      return false;
    }
    
    const formData = new FormData()
    formData.append('file', file)
    
    let res;
    try {
      // 尝试标准端点
      res = await axios.post('/api/questions/import/batch', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
    } catch (e1) {
      try {
        // 尝试简化端点
        res = await axios.post('/questions/import', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        });
      } catch (e2) {
        try {
          // 尝试CSV专用端点
          res = await axios.post('/questions/import/csv', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          });
        } catch (e3) {
          // 尝试最简化端点
          res = await axios.post('/import', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          });
        }
      }
    }
    
    const result = res.data;
    ElMessage.success(`${fileExtension === '.csv' ? 'CSV' : 'Excel'}批量导入完成：成功${result.success || result.length || 0}条`);
    fetchQuestions(); // 重新加载题目列表
  } catch (error: any) {
    console.error('批量导入失败:', error);
    // 检查错误信息，显示更友好的提示
    if (error.response?.data?.message?.includes('XLSX、XLS')) {
      ElMessage.error('后端仅支持 XLSX、XLS 格式的文件，请使用 Excel 文件导入!');
    } else {
      ElMessage.error('批量导入失败，请检查文件格式或后端接口');
    }
  }
  return false; // 阻止自动上传
}

// 添加题目
const handleAdd = () => {
  dialogTitle.value = '添加题目'
  // 重置基本表单
  Object.assign(form, {
    id: '',
    subjectId: '',
    type: 1,
    difficulty: 1,
    content: '',
    options: '',
    answer: '',
    analysis: '',
    score: 10
  })
  
  // 重置选项
  Object.keys(optionsObject).forEach(key => {
    optionsObject[key] = ''
  })
  
  dialogVisible.value = true
}

// 编辑题目
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑题目'
  
  // 填充基本表单数据
  Object.assign(form, {
    id: row.id,
    subjectId: row.subject?.id || '',
    type: row.type,
    difficulty: row.difficulty,
    content: row.content,
    options: row.options || '',
    answer: row.answer,
    analysis: row.analysis,
    score: row.score
  })
  
  // 解析并填充选项
  if (row.options) {
    try {
      const parsedOptions = JSON.parse(row.options)
      if (typeof parsedOptions === 'object' && parsedOptions !== null) {
        // 清空当前选项
        Object.keys(optionsObject).forEach(key => {
          optionsObject[key] = ''
        })
        // 填充解析后的选项
        Object.entries(parsedOptions).forEach(([key, value]) => {
          if (typeof value === 'string') {
            // 处理不同大小写的选项键，确保A-E格式
            const normalizedKey = key.toUpperCase()
            // 只处理A-E范围内的选项
            if (/^[A-E]$/.test(normalizedKey)) {
              optionsObject[normalizedKey] = value
            }
          }
        })
        // 兼容旧格式：如果没有解析到选项，尝试其他格式
        if (Object.values(optionsObject).every(val => val === '')) {
          // 尝试直接将字符串作为选项（如"A选项|B选项|C选项|D选项"）
          const optionsStr = row.options
          if (typeof optionsStr === 'string') {
            // 尝试多种分隔符
            const separators = ['|', ';', ',', '，', '\n', '\r\n']
            let bestSeparator = '|'
            let maxParts = 1
            
            // 找到最佳分隔符
            separators.forEach(sep => {
              const parts = optionsStr.split(sep)
              if (parts.length > maxParts) {
                maxParts = parts.length
                bestSeparator = sep
              }
            })
            
            const optionsArray = optionsStr.split(bestSeparator)
            optionsArray.forEach((option, index) => {
              if (option && option.trim()) {
                const optionKey = String.fromCharCode(65 + index) // A, B, C, D, E
                if (optionKey in optionsObject) {
                  optionsObject[optionKey] = option.trim()
                }
              }
            })
          }
        }
      }
    } catch (e) {
      console.error('解析选项失败:', e)
      // 解析失败时，尝试其他方式处理
      const optionsStr = row.options
      if (typeof optionsStr === 'string') {
        // 尝试直接将字符串作为选项（如"A选项|B选项|C选项|D选项"）
        const separators = ['|', ';', ',', '，', '\n', '\r\n']
        let bestSeparator = '|'
        let maxParts = 1
        
        // 找到最佳分隔符
        separators.forEach(sep => {
          const parts = optionsStr.split(sep)
          if (parts.length > maxParts) {
            maxParts = parts.length
            bestSeparator = sep
          }
        })
        
        const optionsArray = optionsStr.split(bestSeparator)
        // 清空当前选项
        Object.keys(optionsObject).forEach(key => {
          optionsObject[key] = ''
        })
        // 填充选项
        optionsArray.forEach((option, index) => {
          if (option && option.trim()) {
            const optionKey = String.fromCharCode(65 + index) // A, B, C, D, E
            if (optionKey in optionsObject) {
              optionsObject[optionKey] = option.trim()
            }
          }
        })
      } else {
        // 其他情况，使用默认空选项
        Object.keys(optionsObject).forEach(key => {
          optionsObject[key] = ''
        })
      }
    }
  } else {
    // 没有选项，使用默认空选项
    Object.keys(optionsObject).forEach(key => {
      optionsObject[key] = ''
    })
  }
  
  dialogVisible.value = true
}

// 添加选项方法
const addOption = () => {
  const keys = Object.keys(optionsObject)
  const currentLength = keys.length
  if (currentLength < 5) {
    const nextKey = String.fromCharCode(65 + currentLength) // A, B, C, D, E
    optionsObject[nextKey] = ''
  }
}

// 监听options变化，更新optionsObject
watch(() => form.options, (newOptions) => {
  if (newOptions) {
    try {
      const parsed = JSON.parse(newOptions)
      if (typeof parsed === 'object' && parsed !== null) {
        // 清空当前选项
        Object.keys(optionsObject).forEach(key => {
          optionsObject[key] = ''
        })
        // 填充解析后的选项，确保键为大写
        Object.entries(parsed).forEach(([key, value]) => {
          if (typeof value === 'string') {
            const normalizedKey = key.toUpperCase()
            if (normalizedKey in optionsObject) {
              optionsObject[normalizedKey] = value
            }
          }
        })
      }
    } catch (e) {
      // 不是JSON格式，忽略
    }
  }
}, { immediate: true })

// 提交题目
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 处理选项
    const validOptions: Record<string, string> = {}
    Object.entries(optionsObject).forEach(([key, value]) => {
      if (value && value.trim()) {
        validOptions[key] = value.trim()
      }
    })
    
    // 构建请求数据
    const requestData = {
      ...form,
      subject: form.subjectId ? { id: form.subjectId } : null,
      options: Object.keys(validOptions).length > 0 ? JSON.stringify(validOptions) : undefined
    }
    
    if (form.id) {
      // 更新题目
      await axios.put(`/questions/${form.id}`, requestData)
    } else {
      // 添加题目
      await axios.post('/questions', requestData)
    }
    
    ElMessage.success(form.id ? '更新题目成功' : '添加题目成功')
    dialogVisible.value = false
    fetchQuestions() // 重新加载题目列表
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败，请检查输入内容')
  }
}

// 删除题目
const handleDelete = async (row: any) => {
  try {
    // 删除题目
    await axios.delete(`/questions/${row.id}`)
    ElMessage.success('删除题目成功')
    fetchQuestions() // 重新加载题目列表
  } catch (error) {
    console.error('删除题目失败:', error)
    ElMessage.error('删除题目失败')
  }
}

// 查看题目详情
const handleView = (row: any) => {
  // 查看详情逻辑
  console.log('查看题目详情', row)
  // 可以在这里打开详情对话框
}

// 分页大小变化
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchQuestions()
}

// 当前页码变化
const handleCurrentChange = (current: number) => {
  currentPage.value = current
  fetchQuestions()
}

// 初始化数据
onMounted(() => {
  fetchSubjects()
  fetchQuestions()
})
</script>

<style scoped>
.question-management {
  padding: 20px 0;
}

.search-card {
  margin-bottom: 20px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.option-item {
  margin-bottom: 10px;
}

.answer-hint {
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
}
</style>



