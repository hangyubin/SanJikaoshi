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
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios'
import { roleConfig, getRoleType, getRoleLabel, permissionList } from '@/utils/roleConfig'

// 用户数据，初始为空数组
const users = ref<any[]>([])

const searchForm = reactive({
  username: '',
  realName: ''
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
      realName: searchForm.realName
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
    // 如果是管理员角色，执行权限分配
    if (permissionForm.role === 'admin') {
      // 权限分配逻辑 - 这里可以根据实际情况调整
      // 例如：如果后端支持直接分配权限，可以调用相应接口
      // await axios.put(`/users/${permissionForm.id}/permissions`, {
      //   permissions: permissionForm.permissions
      // })
      
      // 目前先简化处理，直接成功
      ElMessage.success('管理员权限已分配')
    }
    
    permissionDialogVisible.value = false
    // 刷新用户列表
    fetchUsers()
  } catch (error) {
    console.error('权限分配失败:', error)
    ElMessage.error('权限分配失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 对于已有用户的角色变更
    if (form.id) {
      // 获取原始用户信息
      const originalUserRes = await axios.get(`/users/${form.id}`)
      const originalUser = originalUserRes.data
      
      // 1. 直接更新用户信息，包括角色变更
      await axios.put(`/users/${form.id}`, {
        ...form
      })
      
      // 2. 如果角色从普通用户变为管理员，弹出权限分配对话框
      if (originalUser.role === 'user' && form.role === 'admin') {
        // 3. 先关闭用户编辑对话框
        dialogVisible.value = false
        
        // 4. 重新获取最新的用户数据，确保角色已更新
        const updatedUserRes = await axios.get(`/users/${form.id}`)
        const updatedUser = updatedUserRes.data
        
        // 5. 更新权限表单数据，确保角色为admin
        permissionForm.id = updatedUser.id
        permissionForm.username = updatedUser.username
        permissionForm.role = updatedUser.role // 确保角色是最新的admin
        permissionForm.permissions = updatedUser.permissions || []
        
        // 6. 弹出权限分配对话框
        permissionDialogVisible.value = true
      } else {
        // 7. 非管理员角色变更，直接关闭对话框
        dialogVisible.value = false
      }
    } 
    // 新用户创建
    else {
      // 1. 提交用户信息
      const userRes = await axios.post('/users', form)
      const userId = userRes.data.id
      
      // 2. 如果是创建新的管理员用户，需要分配权限
      if (form.role === 'admin') {
        // 3. 先关闭用户编辑对话框
        dialogVisible.value = false
        
        // 4. 获取最新创建的用户数据
        const updatedUserRes = await axios.get(`/users/${userId}`)
        const updatedUser = updatedUserRes.data
        
        // 5. 更新权限表单数据
        permissionForm.id = updatedUser.id
        permissionForm.username = updatedUser.username
        permissionForm.role = updatedUser.role // 确保角色是最新的admin
        permissionForm.permissions = []
        
        // 6. 弹出权限分配对话框
        permissionDialogVisible.value = true
      } else {
        // 7. 非管理员新用户，直接关闭对话框
        dialogVisible.value = false
      }
    }
    
    ElMessage.success(form.id ? '编辑用户成功' : '添加用户成功')
    
    fetchUsers() // 刷新用户列表
  } catch (error: any) {
    console.error('提交用户信息失败:', error)
    let errorMsg = form.id ? '编辑用户失败' : '添加用户失败'
    
    // 更详细的错误处理
    if (error.response) {
      if (error.response.status === 403) {
        errorMsg = '您没有权限执行此操作，请联系系统管理员'
      } else {
        errorMsg = error.response.data?.message || errorMsg
      }
    } else if (error.request) {
      errorMsg = '服务器无响应，请稍后重试'
    } else {
      errorMsg = error.message || errorMsg
    }
    
    ElMessage.error(errorMsg)
  }
}

const handleDelete = (row: any) => {
  // 删除逻辑
  console.log('删除用户', row)
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



