<template>
  <div class="question-management">
    <h1>题目管理</h1>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">总题目数</p>
              <h3 class="stat-value">{{ total }}</h3>
            </div>
            <div class="stat-icon total-icon">
              <el-icon><DocumentCopy /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">单选题</p>
              <h3 class="stat-value">{{ singleChoiceCount }}</h3>
            </div>
            <div class="stat-icon single-icon">
              <el-icon><CircleClose /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">多选题</p>
              <h3 class="stat-value">{{ multipleChoiceCount }}</h3>
            </div>
            <div class="stat-icon multiple-icon">
              <el-icon><Check /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">是非题</p>
              <h3 class="stat-value">{{ trueFalseCount }}</h3>
            </div>
            <div class="stat-icon truefalse-icon">
              <el-icon><RefreshRight /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, DocumentCopy, CircleClose, Check, RefreshRight } from '@element-plus/icons-vue'
import { parseQuestions } from '@/utils/parseQuestions'

// 真实题目数据
const questions = ref<any[]>([])
const subjects = ref<any[]>([])

// 统计数据
const total = ref(0)
const singleChoiceCount = ref(0)
const multipleChoiceCount = ref(0)
const trueFalseCount = ref(0)

const searchForm = reactive({
  content: '',
  subjectId: '',
  type: '',
  difficulty: ''
})

const currentPage = ref(1)
const pageSize = ref(10)

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

// 解析选项，支持多种格式
const parseOptions = (options: string) => {
  if (!options) return {};
  
  // 1. 尝试JSON解析（主要格式）
  try {
    const parsed = JSON.parse(options);
    if (typeof parsed === 'object' && parsed !== null) {
      return parsed;
    }
  } catch (e) {
    // JSON解析失败，尝试其他格式
  }
  
  // 2. 移除不安全的eval()解析，跳过这种格式
  // 这种格式不常见，跳过以避免安全风险
  
  
  // 3. 尝试按分隔符分割（如"选项A|选项B|选项C|选项D"）
  const separators = ['|', ';', ',', '，', '\n', '\r\n', '\t'];
  let bestSeparator = '|';
  let maxParts = 1;
  
  // 找到最佳分隔符
  separators.forEach(sep => {
    const parts = options.split(sep);
    if (parts.length > maxParts) {
      maxParts = parts.length;
      bestSeparator = sep;
    }
  });
  
  // 使用最佳分隔符分割
  const optionArray = options.split(bestSeparator);
  if (optionArray.length > 1) {
    const result: Record<string, string> = {};
    optionArray.forEach((option, index) => {
      const trimmedOption = option.trim();
      if (trimmedOption) {
        const key = String.fromCharCode(65 + index); // A, B, C, D...
        result[key] = trimmedOption;
      }
    });
    return result;
  }
  
  // 4. 尝试正则匹配（如"A. 选项A B. 选项B"）
  const regexOptions: Record<string, string> = {};
  const regex = /([A-E])\s*[.:]\s*([^A-E]*)/g;
  let match;
  while ((match = regex.exec(options)) !== null) {
    // 确保match[1]和match[2]都存在
    if (match[1] && match[2]) {
      regexOptions[match[1]] = match[2].trim();
    }
  }
  
  if (Object.keys(regexOptions).length > 0) {
    return regexOptions;
  }
  
  // 5. 最后尝试直接作为单个选项
  return { A: options.trim() };
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

// 获取题目列表，增强处理多种返回格式
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
    
    // 尝试多种API端点
    const apiEndpoints = [
      '/questions',
      '/api/questions',
      '/questions/all',
      '/api/questions/all'
    ]
    
    let questionsData: any[] = []
    let totalCount: number = 0
    let success = false
    
    // 尝试所有端点，直到成功获取数据
    for (const endpoint of apiEndpoints) {
      try {
        const res = await axios.get(endpoint, { params })
        const responseData = res.data
        
        // 处理不同的返回格式
        if (responseData.records) {
          // 分页格式，如 { records: [...], total: 100 }
          questionsData = responseData.records
          totalCount = responseData.total || questionsData.length
          success = true
          break
        } else if (Array.isArray(responseData)) {
          // 直接返回数组格式
          questionsData = responseData
          totalCount = questionsData.length
          success = true
          break
        } else if (responseData.data && Array.isArray(responseData.data)) {
          // 嵌套数据格式，如 { data: [...] }
          questionsData = responseData.data
          totalCount = questionsData.length
          success = true
          break
        } else if (responseData.list && Array.isArray(responseData.list)) {
          // list字段格式，如 { list: [...] }
          questionsData = responseData.list
          totalCount = responseData.total || questionsData.length
          success = true
          break
        }
      } catch (e: any) {
        console.log(`API端点 ${endpoint} 失败:`, e.message)
        continue
      }
    }
    
    if (success) {
      questions.value = questionsData
      total.value = totalCount
      
      // 计算不同类型题目的数量
      singleChoiceCount.value = questionsData.filter(q => q.type === 1).length
      multipleChoiceCount.value = questionsData.filter(q => q.type === 2).length
      trueFalseCount.value = questionsData.filter(q => q.type === 3).length
      
      console.log(`成功获取 ${questionsData.length} 道题目，共 ${totalCount} 道`)
      console.log(`单选题：${singleChoiceCount.value} 道，多选题：${multipleChoiceCount.value} 道，是非题：${trueFalseCount.value} 道`)
    } else {
      throw new Error('所有API端点都返回了不支持的数据格式')
    }
  } catch (error: any) {
    console.error('获取题目列表失败:', error)
    questions.value = []
    total.value = 0
    singleChoiceCount.value = 0
    multipleChoiceCount.value = 0
    trueFalseCount.value = 0
    ElMessage.error(`获取题目列表失败：${error.message || '请检查网络连接或联系管理员'}`)
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
    // 检查文件类型
    const fileExtension = file.name.substring(file.name.lastIndexOf('.')).toLowerCase();
    if (!['.txt', '.md'].includes(fileExtension)) {
      ElMessage.error('请上传 .txt 或 .md 格式的文本文件');
      return false;
    }
    
    // 检查文件大小（不超过10MB）
    const maxSize = 10 * 1024 * 1024; // 10MB
    if (file.size > maxSize) {
      ElMessage.error('文件大小不能超过 10MB!');
      return false;
    }
    
    // 读取文件内容
    ElMessage.info('正在读取文件内容...');
    const text = await file.text();
    
    // 使用前端解析器解析题目
    ElMessage.info('正在解析题目...');
    const parsedQuestions = parseQuestions(text);
    
    if (parsedQuestions.length === 0) {
      ElMessage.error('未解析到题目，请检查文件格式是否正确。\n提示：请确保题目格式为 "1. 题目内容：(A)" 且选项为 "A. 选项内容" 格式');
      return false;
    }
    
    // 检查解析质量
    const validQuestions = parsedQuestions.filter(q => q.content && Object.keys(q.options).length > 0);
    if (validQuestions.length === 0) {
      ElMessage.error('解析到的题目缺少内容或选项，请检查文件格式');
      return false;
    }
    
    // 显示解析结果，让用户确认
    ElMessage.success(`成功解析 ${validQuestions.length} 道题目，准备导入`);
    
    // 将题目转换为系统API要求的格式
    const questionsToImport = validQuestions.map(q => ({
      subjectId: form.subjectId || '1', // 使用表单选择的科目或默认值
      type: q.type,
      difficulty: q.difficulty,
      content: q.content,
      options: JSON.stringify(q.options), // 转换为JSON字符串
      answer: q.answer,
      analysis: q.analysis || '',
      score: q.score
    }));
    
    // 批量导入题目
    ElMessage.info('正在导入题目，请稍候...');
    let successCount = 0;
    
    try {
      // 尝试批量导入
      let res;
      try {
        // 尝试标准端点
        res = await axios.post('/questions/batch', questionsToImport);
        successCount = res.data.success || res.data.length || validQuestions.length;
      } catch (e1) {
        try {
          // 尝试简化端点
          res = await axios.post('/api/questions/batch', questionsToImport);
          successCount = res.data.success || res.data.length || validQuestions.length;
        } catch (e2) {
          // 尝试逐个导入
          ElMessage.info('批量导入失败，尝试逐个导入...');
          successCount = 0;
          for (const question of questionsToImport) {
            try {
              await axios.post('/questions', question);
              successCount++;
            } catch (e) {
              console.error('单个题目导入失败:', e);
            }
          }
        }
      }
      
      ElMessage.success(`文本文件导入成功：共导入${successCount}条，失败${validQuestions.length - successCount}条`);
      fetchQuestions(); // 重新加载题目列表
    } catch (error: any) {
      console.error('导入题目失败:', error);
      ElMessage.error(`导入题目失败：${error.message || '请检查网络连接或后端服务'}`);
      return false;
    }
  } catch (error: any) {
    console.error('文本文件导入失败:', error);
    if (error.message.includes('Not Found')) {
      ElMessage.error('文本文件导入功能未启用，请联系管理员');
    } else {
      ElMessage.error(`文本文件导入失败：${error.message || '请检查文件格式是否正确'}`);
    }
  }
  return false; // 阻止自动上传
};



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
  
  // 清空当前选项
  Object.keys(optionsObject).forEach(key => {
    optionsObject[key] = ''
  })
  
  // 使用现有的parseOptions函数解析选项，确保与表格显示一致
  if (row.options) {
    const parsedOptions = parseOptions(row.options)
    
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
    // 显示确认对话框
    await ElMessageBox.confirm(
      `确定要删除题目 "${row.content || row.title || '未知'}" 吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 删除题目
    await axios.delete(`/questions/${row.id}`)
    ElMessage.success('删除题目成功')
    fetchQuestions() // 重新加载题目列表
  } catch (error: any) {
    if (error.name !== 'Cancel') {
      console.error('删除题目失败:', error)
      ElMessage.error('删除题目失败')
    }
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
/* 统计卡片样式 */
.stat-card {
  margin-bottom: 20px;
  transition: all 0.3s ease;
  border-radius: 8px;
  border: none;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin: 0 0 8px 0;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin: 0;
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
}

.total-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.single-icon {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.multiple-icon {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.truefalse-icon {
  background: linear-gradient(135deg, #e6a23c 0%, #f3d19e 100%);
}

/* 选项列表样式 */
.option-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.option-key {
  font-weight: bold;
  color: #409eff;
  min-width: 20px;
}

.option-value {
  color: #606266;
}

.empty-text {
  color: #909399;
  font-style: italic;
}
</style>



