<template>
  <div class="user-list">
    <h1>用户列表</h1>
    
    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="80px" inline>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="searchForm.realName" placeholder="请输入真实姓名"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态">
            <el-option label="全部" value=""></el-option>
            <el-option label="启用" :value="1"></el-option>
            <el-option label="禁用" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">添加用户</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 用户表格 -->
    <el-card class="table-card" style="margin-top: 20px;">
      <el-table :data="users" stripe style="width: 100%">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="realName" label="真实姓名" width="120"></el-table-column>
        <el-table-column prop="email" label="邮箱" width="180"></el-table-column>
        <el-table-column prop="phone" label="手机号" width="150"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              active-value="1"
              inactive-value="0"
              @change="handleStatusChange(scope.row)"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column prop="role" label="角色" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'admin' ? 'primary' : 'success'">
              {{ scope.row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180"></el-table-column>
        <el-table-column label="操作" width="300">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
            <el-button type="info" size="small" @click="handleView(scope.row)">查看详情</el-button>
            <template v-if="scope.row.role === 'user'">
              <el-button 
                type="warning" 
                size="small" 
                @click="handlePromote(scope.row)"
                :disabled="scope.row.username === 'admin'"
              >
                提权
              </el-button>
            </template>
            <template v-else>
              <el-button 
                type="success" 
                size="small" 
                @click="handleDemote(scope.row)"
                :disabled="scope.row.username === 'admin'"
              >
                降权
              </el-button>
            </template>
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
    
    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!form.id">
          <el-input v-model="form.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" type="email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" active-value="1" inactive-value="0"></el-switch>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option label="普通用户" value="user"></el-option>
            <el-option label="管理员" value="admin"></el-option>
          </el-select>
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
import { ElMessage, ElMessageBox } from 'element-plus'

// 模拟用户数据
const users = ref([
  {
    id: 1,
    username: 'user1',
    realName: '张三',
    email: 'zhangsan@example.com',
    phone: '13800138001',
    status: '1',
    role: 'user',
    createTime: '2025-12-01 10:00:00',
    updateTime: '2025-12-02 14:30:00'
  },
  {
    id: 2,
    username: 'user2',
    realName: '李四',
    email: 'lisi@example.com',
    phone: '13800138002',
    status: '1',
    role: 'user',
    createTime: '2025-12-01 11:00:00',
    updateTime: '2025-12-03 09:15:00'
  },
  {
    id: 3,
    username: 'admin1',
    realName: '王五',
    email: 'wangwu@example.com',
    phone: '13800138003',
    status: '0',
    role: 'admin',
    createTime: '2025-12-02 14:00:00',
    updateTime: '2025-12-04 16:45:00'
  }
])

const searchForm = reactive({
  username: '',
  realName: '',
  status: ''
})

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(users.value.length)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('添加用户')
const formRef = ref<FormInstance>()

const form = reactive({
  id: '',
  username: '',
  password: '',
  realName: '',
  email: '',
  phone: '',
  status: '1',
  role: 'user'
})

const rules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
  ]
})

const handleSearch = () => {
  // 搜索逻辑
  console.log('搜索用户', searchForm)
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.realName = ''
  searchForm.status = ''
}

const handleAdd = () => {
  dialogTitle.value = '添加用户'
  // 重置表单
    Object.assign(form, {
      id: '',
      username: '',
      password: '',
      realName: '',
      email: '',
      phone: '',
      status: '1',
      role: 'user' // 默认角色为普通用户
    })
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  dialogTitle.value = '编辑用户'
  // 填充表单
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    // 提交表单逻辑
    console.log('提交用户信息', form)
    dialogVisible.value = false
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

const handleDelete = (row: any) => {
  // 删除逻辑
  console.log('删除用户', row)
}

const handleView = (row: any) => {
  // 查看详情逻辑
  console.log('查看用户详情', row)
}

const handleStatusChange = (row: any) => {
  // 状态变更逻辑
  console.log('变更用户状态', row)
}

const handlePromote = (row: any) => {
  ElMessageBox.confirm('确定要将该用户提权为管理员吗？', '提权确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      // 更新角色
      row.role = 'admin'
      ElMessage.success('提权成功，该用户已成为管理员')
      // 这里可以添加调用后端API的逻辑，将用户添加到管理员列表
      console.log('用户提权成功', row)
    })
    .catch(() => {
      ElMessage.info('已取消提权操作')
    })
}

const handleDemote = (row: any) => {
  ElMessageBox.confirm('确定要将该管理员降为普通用户吗？', '降权确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      // 更新角色
      row.role = 'user'
      ElMessage.success('降权成功，该管理员已成为普通用户')
      // 这里可以添加调用后端API的逻辑，将用户从管理员列表移除
      console.log('管理员降权成功', row)
    })
    .catch(() => {
      ElMessage.info('已取消降权操作')
    })
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
  console.log('用户列表初始化')
})
</script>

<style scoped>
.user-list {
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
</style>