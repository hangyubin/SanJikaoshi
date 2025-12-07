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
            <el-option label="数学" :value="1"></el-option>
            <el-option label="英语" :value="2"></el-option>
            <el-option label="计算机" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="题目类型">
          <el-select v-model="searchForm.type" placeholder="请选择题目类型">
            <el-option label="全部" value=""></el-option>
            <el-option label="选择题" :value="1"></el-option>
            <el-option label="判断题" :value="2"></el-option>
            <el-option label="填空题" :value="3"></el-option>
            <el-option label="简答题" :value="4"></el-option>
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
            <el-tag :type="scope.row.type === 1 ? 'primary' : scope.row.type === 2 ? 'success' : scope.row.type === 3 ? 'warning' : 'danger'">
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
        <el-table-column prop="content" label="题目内容" min-width="200"></el-table-column>
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
            <el-option label="数学" :value="1"></el-option>
            <el-option label="英语" :value="2"></el-option>
            <el-option label="计算机" :value="3"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="题目类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择题目类型">
            <el-option label="选择题" :value="1"></el-option>
            <el-option label="判断题" :value="2"></el-option>
            <el-option label="填空题" :value="3"></el-option>
            <el-option label="简答题" :value="4"></el-option>
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
        <el-form-item v-if="form.type === 1" label="选项" prop="options">
          <div v-for="(_, index) in form.options" :key="index" class="option-item">
            <el-input 
              v-model="form.options[index]"
              placeholder="请输入选项内容"
            ></el-input>
          </div>
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
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'

// 模拟题目数据
const questions = ref([
  {
    id: 1,
    subject: '数学',
    type: 1,
    difficulty: 2,
    content: '计算定积分∫₀¹x²dx = ',
    options: ['0', '1/3', '1/2', '1'],
    answer: 'B',
    analysis: '根据定积分公式，∫x²dx = x³/3 + C，所以∫₀¹x²dx = 1³/3 - 0³/3 = 1/3',
    score: 5,
    createTime: '2025-12-01 10:00:00'
  },
  {
    id: 2,
    subject: '英语',
    type: 2,
    difficulty: 1,
    content: '英语中现在完成时的构成是"have/has + 过去分词"。',
    answer: '正确',
    analysis: '现在完成时的构成确实是"have/has + 过去分词"，用于表示过去发生的动作对现在造成的影响或结果。',
    score: 3,
    createTime: '2025-12-02 14:30:00'
  },
  {
    id: 3,
    subject: '计算机',
    type: 1,
    difficulty: 2,
    content: '下列哪个不是JavaScript的基本数据类型？',
    options: ['String', 'Number', 'Boolean', 'Object'],
    answer: 'D',
    analysis: 'JavaScript的基本数据类型包括：String、Number、Boolean、Null、Undefined、Symbol和BigInt。Object是引用数据类型。',
    score: 5,
    createTime: '2025-12-03 09:15:00'
  }
])

const searchForm = reactive({
  content: '',
  subjectId: '',
  type: '',
  difficulty: ''
})

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(questions.value.length)

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
  options: ['', '', '', ''],
  answer: '',
  analysis: '',
  score: 5
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

const getQuestionType = (type: number) => {
  switch (type) {
    case 1: return '选择题'
    case 2: return '判断题'
    case 3: return '填空题'
    case 4: return '简答题'
    default: return '未知类型'
  }
}

const getDifficultyText = (difficulty: number) => {
  switch (difficulty) {
    case 1: return '简单'
    case 2: return '中等'
    case 3: return '困难'
    default: return '未知难度'
  }
}

const handleSearch = () => {
  // 搜索逻辑
  console.log('搜索题目', searchForm)
}

const handleReset = () => {
  searchForm.content = ''
  searchForm.subjectId = ''
  searchForm.type = ''
  searchForm.difficulty = ''
}

const handleAdd = () => {
  dialogTitle.value = '添加题目'
  // 重置表单
  Object.assign(form, {
    id: '',
    subjectId: '',
    type: 1,
    difficulty: 1,
    content: '',
    options: ['', '', '', ''],
    answer: '',
    analysis: '',
    score: 5
  })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑题目'
  // 填充表单
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    // 提交表单逻辑
    console.log('提交题目信息', form)
    dialogVisible.value = false
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

const handleDelete = (row: any) => {
  // 删除逻辑
  console.log('删除题目', row)
}

const handleView = (row: any) => {
  // 查看详情逻辑
  console.log('查看题目详情', row)
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentChange = (current: number) => {
  currentPage.value = current
}

onMounted(() => {
  // 初始化数据
  console.log('题目列表初始化')
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