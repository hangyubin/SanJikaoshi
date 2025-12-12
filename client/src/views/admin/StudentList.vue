<template>
  <div class="user-list">
    <h1>用户列表</h1>
    
    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="80px" inline>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.realName" placeholder="请输入姓名"></el-input>
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
        <el-table-column prop="realName" label="姓名" width="120"></el-table-column>
        <el-table-column prop="phone" label="手机号" width="150"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              active-value="1"
              inactive-value="0"
              @change="handleStatusChange(scope.row)"
              :disabled="scope.row.username === 'admin'"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column prop="role" label="角色" width="120">
          <template #default="scope">
            <el-tag :type="getRoleType(scope.row.role, scope.row.username)">
              {{ getRoleLabel(scope.row.role, scope.row.username) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <!-- 系统管理员admin不能被任何账号操作 -->
            <el-button 
              type="primary" 
              size="small" 
              @click="handleEdit(scope.row)"
              :disabled="scope.row.username === 'admin'"
            >
              编辑
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              @click="handleDelete(scope.row)"
              :disabled="scope.row.username === 'admin'"
            >
              删除
            </el-button>
            <el-button 
              type="info" 
              size="small" 
              @click="handleView(scope.row)"
              :disabled="scope.row.username === 'admin'"
            >
              查看详情
            </el-button>
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
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" active-value="1" inactive-value="0"></el-switch>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option 
              v-for="role in roleConfig" 
              :key="role.value" 
              :label="role.label" 
              :value="role.value"
              :disabled="role.value === 'sysadmin'"
            ></el-option>
          </el-select>
        </el-form-item>
        
        <!-- 权限分配对话框 -->
        <el-dialog
          v-model="permissionDialogVisible"
          title="权限分配"
          width="600px"
        >
          <el-form :model="permissionForm" label-width="100px">
            <el-form-item label="用户">
              {{ permissionForm.username }}
            </el-form-item>
            <el-form-item label="角色">
              <el-tag :type="getRoleType(permissionForm.role)">
                {{ getRoleLabel(permissionForm.role) }}
              </el-tag>
            </el-form-item>
            <el-form-item label="权限">
              <el-checkbox-group v-model="permissionForm.permissions">
                <el-checkbox 
                  v-for="permission in permissionList" 
                  :key="permission.id" 
                  :label="permission.id"
                >
                  {{ permission.label }}
                </el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-form>
          <template #footer>
            <span class="dialog-footer">
              <el-button @click="permissionDialogVisible = false">取消</el-button>
              <el-button type="primary" @click="handlePermissionSubmit">确认</el-button>
            </span>
          </template>
        </el-dialog>
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
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios'
import { roleConfig, getRoleType, getRoleLabel, permissionList } from '@/utils/roleConfig'

// 用户数据，初始为空数组
const users = ref<any[]>([])

const searchForm = reactive({
  username: '',
  realName: '',
  status: ''
})

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 获取用户列表
const fetchUsers = () => {
  loading.value = true
  axios.get('/users', {
    params: {
      page: currentPage.value,
      pageSize: pageSize.value,
      username: searchForm.username,
      realName: searchForm.realName,
      status: searchForm.status
    }
  })
  .then(res => {
    users.value = res.data.users || []
    total.value = res.data.total || 0
  })
  .catch(error => {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  })
  .finally(() => {
    loading.value = false
  })
}

// 页面加载时获取用户列表
onMounted(() => {
  fetchUsers()
})

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('添加用户')
const formRef = ref<FormInstance>()

const form = reactive({
  id: '',
  username: '',
  password: '',
  realName: '',
  phone: '',
  status: '1',
  role: 'user'
})

// 权限分配对话框
const permissionDialogVisible = ref(false)
const permissionForm = reactive({
  id: '',
  username: '',
  role: '',
  permissions: [] as string[]
})

// 监听角色变化，当角色变为admin时，自动弹出权限分配对话框
watch(
  () => form.role,
  (newRole, oldRole) => {
    if (form.id && newRole === 'admin' && oldRole !== 'admin') {
      // 当编辑用户并将角色改为管理员时，自动弹出权限分配对话框
      showPermissionDialog()
    }
  }
)

// 显示权限分配对话框
const showPermissionDialog = async () => {
  if (!form.id) return
  
  try {
    // 获取当前用户的详细信息
    const res = await axios.get(`/users/${form.id}`)
    permissionForm.role = res.data.role || 'user'
  } catch (error) {
    console.error('获取用户信息失败:', error)
    permissionForm.role = form.role
  }
  
  // 填充表单数据
  permissionForm.id = form.id
  permissionForm.username = form.username
  permissionForm.permissions = [] // 重置权限
  
  permissionDialogVisible.value = true
}

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
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { pattern: /^[\u4e00-\u9fa5]+$/, message: '请输入有效的中文姓名，不能包含空格或特殊字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
  ]
})

const handleSearch = () => {
  currentPage.value = 1
  fetchUsers()
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.realName = ''
  searchForm.status = ''
  currentPage.value = 1
  fetchUsers()
}

const handleAdd = () => {
  dialogTitle.value = '添加用户'
  // 重置表单
    Object.assign(form, {
      id: '',
      username: '',
      password: '',
      realName: '',
      phone: '',
      status: '1', // 默认启用状态
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

// 提交权限分配
const handlePermissionSubmit = async () => {
  try {
    // 根据角色使用不同的API接口
    if (permissionForm.role === 'admin') {
      // 提升为管理员
      await axios.put(`/users/promote/${permissionForm.id}`)
    } else {
      // 降低为普通用户
      await axios.put(`/users/demote/${permissionForm.id}`)
    }
    
    ElMessage.success('权限分配成功')
    permissionDialogVisible.value = false
  } catch (error) {
    console.error('权限分配失败:', error)
    ElMessage.error('权限分配失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    // 提交表单逻辑
    const method = form.id ? 'put' : 'post'
    const url = form.id ? `/users/${form.id}` : '/users'
    
    // 提交用户信息
    const userRes = await axios[method as 'post' | 'put'](url, form)
    const userId = form.id || userRes.data.id
    
    // 如果是创建新的管理员用户，需要后续分配权限
    if (form.role === 'admin' && !form.id) {
      // 先关闭用户编辑对话框
      dialogVisible.value = false
      
      // 更新权限表单数据
      permissionForm.id = userId
      permissionForm.username = form.username
      permissionForm.role = form.role
      permissionForm.permissions = []
      
      // 弹出权限分配对话框
      permissionDialogVisible.value = true
    }
    
    ElMessage.success(form.id ? '编辑用户成功' : '添加用户成功')
    
    // 如果不是新创建的管理员用户，直接关闭对话框
    if (!(form.role === 'admin' && !form.id)) {
      dialogVisible.value = false
    }
    
    fetchUsers() // 刷新用户列表
  } catch (error) {
    console.error('提交用户信息失败:', error)
    let errorMsg = form.id ? '编辑用户失败' : '添加用户失败'
    if (error instanceof Error) {
      errorMsg = error.message || errorMsg
    }
    ElMessage.error(errorMsg)
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

const handleStatusChange = async (row: any) => {
  // 保存原状态，用于失败时恢复
  const originalStatus = row.status
  try {
    // 调用后端API更新状态，与handleSubmit函数保持一致的调用方式
    await axios.put(`/users/${row.id}`, {
      status: row.status,
      // 确保提交所有必要字段，避免API验证失败
      username: row.username,
      realName: row.realName,
      phone: row.phone,
      role: row.role
    })
    ElMessage.success('状态更新成功')
  } catch (error: any) {
    console.error('更新状态失败:', error)
    // 更详细的错误处理
    let errorMsg = '状态更新失败'
    if (error.response) {
      // 服务器返回了错误响应
      if (error.response.status === 403) {
        errorMsg = '您没有权限执行此操作'
      } else {
        errorMsg = error.response.data?.message || errorMsg
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应
      errorMsg = '服务器无响应，请稍后重试'
    } else {
      // 请求配置错误
      errorMsg = error.message || errorMsg
    }
    ElMessage.error(errorMsg)
    // 恢复原状态
    row.status = originalStatus
  }
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchUsers()
}

const handleCurrentChange = (current: number) => {
  currentPage.value = current
  fetchUsers()
}
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



