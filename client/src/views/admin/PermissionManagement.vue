<template>
  <div class="permission-management">
    <h1>权限管理</h1>
    
    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="80px" inline>
        <el-form-item label="用户名称">
          <el-input v-model="searchForm.username" placeholder="请输入用户名称"></el-input>
        </el-form-item>
        <el-form-item label="权限状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态">
            <el-option label="全部" value=""></el-option>
            <el-option label="启用" :value="1"></el-option>
            <el-option label="禁用" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAssignPermission">分配权限</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 用户权限列表 -->
    <el-card class="table-card" style="margin-top: 20px;">
      <el-table :data="users" stripe style="width: 100%">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名" width="150"></el-table-column>
        <el-table-column prop="realName" label="真实姓名" width="150"></el-table-column>
        <el-table-column prop="role" label="角色" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'admin' ? 'primary' : 'success'">
              {{ scope.row.role === 'admin' ? '系统管理员' : '普通管理员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="permissions" label="权限" width="250">
          <template #default="scope">
            <el-tag size="small" v-for="perm in scope.row.permissions" :key="perm" :type="'info'" style="margin-right: 5px; margin-bottom: 5px;">
              {{ perm }}
            </el-tag>
            <span v-if="!scope.row.permissions || scope.row.permissions.length === 0" class="no-permission">
              未分配权限
            </span>
          </template>
        </el-table-column>
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
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <!-- 系统管理员不能被修改权限 -->
            <el-button 
              type="warning" 
              size="small" 
              @click="handleEditPermission(scope.row)"
              :disabled="scope.row.username === 'admin'"
            >
              编辑权限
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              @click="handleDeleteUser(scope.row)"
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
    
    <!-- 权限分配对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form :model="permissionForm" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户" prop="userId">
          <el-select v-model="permissionForm.userId" placeholder="请选择用户" style="width: 100%">
            <el-option
              v-for="user in userList"
              :key="user.id"
              :label="user.username + ' - ' + user.realName"
              :value="user.id"
              :disabled="user.username === 'admin'"
            ></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="分配权限">
          <div class="permission-tree">
            <el-tree
              :data="permissionTree"
              :props="treeProps"
              show-checkbox
              node-key="id"
              default-expand-all
              :checked-keys="checkedPermissionIds"
              @check="handlePermissionCheck"
              ref="permissionTreeRef"
            ></el-tree>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handlePermissionSubmit" :loading="loading">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElTree } from 'element-plus'
import axios from '@/utils/axios'

// 用户数据
const users = ref<any[]>([])
const userList = ref<any[]>([])
const searchForm = reactive({
  username: '',
  status: ''
})
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 权限分配对话框
const dialogVisible = ref(false)
const dialogTitle = ref('分配权限')
const formRef = ref<FormInstance>()
const permissionForm = reactive({
  userId: '',
  permissions: []
})

const rules = reactive<FormRules>({
  userId: [
    { required: true, message: '请选择用户', trigger: 'change' }
  ]
})

// 权限树
const permissionTreeRef = ref<InstanceType<typeof ElTree>>()
const permissionTree = ref([
  {
    id: 'user',
    label: '用户管理',
    children: [
      { id: 'user:list', label: '查看用户列表' },
      { id: 'user:add', label: '添加用户' },
      { id: 'user:edit', label: '编辑用户' },
      { id: 'user:delete', label: '删除用户' },
      { id: 'user:status', label: '修改状态' }
    ]
  },
  {
    id: 'question',
    label: '题库管理',
    children: [
      { id: 'question:list', label: '查看题库' },
      { id: 'question:add', label: '添加题目' },
      { id: 'question:edit', label: '编辑题目' },
      { id: 'question:delete', label: '删除题目' }
    ]
  },
  {
    id: 'task',
    label: '任务管理',
    children: [
      { id: 'task:list', label: '查看任务列表' },
      { id: 'task:create', label: '创建任务' },
      { id: 'task:edit', label: '编辑任务' },
      { id: 'task:delete', label: '删除任务' }
    ]
  },
  {
    id: 'resource',
    label: '资源管理',
    children: [
      { id: 'resource:list', label: '查看资源列表' },
      { id: 'resource:add', label: '添加资源' },
      { id: 'resource:edit', label: '编辑资源' },
      { id: 'resource:delete', label: '删除资源' }
    ]
  },
  {
    id: 'setting',
    label: '系统设置',
    children: [
      { id: 'setting:basic', label: '基本设置' },
      { id: 'setting:permission', label: '权限设置' }
    ]
  }
])

const treeProps = {
  children: 'children',
  label: 'label'
}

// 当前选中的权限ID
const checkedPermissionIds = ref<string[]>([])

// 获取用户权限列表
const fetchUsers = () => {
  loading.value = true
  axios.get('/users', {
    params: {
      page: currentPage.value,
      pageSize: pageSize.value,
      username: searchForm.username,
      status: searchForm.status,
      role: 'admin' // 只获取管理员用户
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

// 获取所有用户列表（用于权限分配）
const fetchUserList = () => {
  axios.get('/users', {
    params: {
      role: 'admin', // 只获取管理员用户
      pageSize: 1000 // 获取足够多的用户
    }
  })
  .then(res => {
    userList.value = res.data.users || []
  })
  .catch(error => {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  })
}

// 页面加载时获取数据
onMounted(() => {
  fetchUsers()
  fetchUserList()
})

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchUsers()
}

// 重置
const handleReset = () => {
  searchForm.username = ''
  searchForm.status = ''
  currentPage.value = 1
  fetchUsers()
}

// 分页
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchUsers()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  fetchUsers()
}

// 分配权限
const handleAssignPermission = () => {
  dialogTitle.value = '分配权限'
  // 重置表单
  permissionForm.userId = ''
  checkedPermissionIds.value = []
  dialogVisible.value = true
}

// 编辑权限
const handleEditPermission = (row: any) => {
  dialogTitle.value = '编辑权限'
  permissionForm.userId = row.id
  // 设置选中的权限
  checkedPermissionIds.value = row.permissions || []
  dialogVisible.value = true
}

// 权限勾选事件
const handlePermissionCheck = () => {
  // 处理权限勾选逻辑
}

// 提交权限分配
const handlePermissionSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 获取选中的权限ID
    const selectedPermissions = permissionTreeRef.value?.getCheckedKeys() || []
    
    // 提交权限分配
    await axios.put(`/users/${permissionForm.userId}/permissions`, {
      permissions: selectedPermissions
    })
    
    ElMessage.success('权限分配成功')
    dialogVisible.value = false
    fetchUsers() // 刷新用户列表
  } catch (error) {
    console.error('权限分配失败:', error)
    let errorMsg = '权限分配失败'
    if (error instanceof Error) {
      errorMsg = error.message
    }
    ElMessage.error(errorMsg)
  }
}

// 状态变更
const handleStatusChange = (row: any) => {
  const originalStatus = row.status
  axios.put(`/users/${row.id}`, { status: row.status })
    .then(() => {
      ElMessage.success('状态更新成功')
    })
    .catch(error => {
      console.error('更新状态失败:', error)
      let errorMsg = '状态更新失败'
      if (error.response) {
        errorMsg = error.response.data?.message || errorMsg
      } else if (error.request) {
        errorMsg = '服务器无响应，请稍后重试'
      } else {
        errorMsg = error.message || errorMsg
      }
      ElMessage.error(errorMsg)
      // 恢复原状态
      row.status = originalStatus
    })
}

// 删除用户
const handleDeleteUser = (row: any) => {
  if (row.username === 'admin') {
    ElMessage.warning('不能删除系统管理员')
    return
  }
  
  // 这里可以添加确认对话框
  axios.delete(`/users/${row.id}`)
    .then(() => {
      ElMessage.success('删除成功')
      fetchUsers() // 刷新用户列表
    })
    .catch(error => {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    })
}
</script>

<style scoped>
.permission-management {
  padding: 20px 0;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

/* 页面标题 */
.permission-management h1 {
  margin: 0 0 30px 0;
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 0 0 20px 0;
  border-bottom: 2px solid #e4e7ed;
}

.permission-management h1::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 32px;
  background: linear-gradient(180deg, #667eea, #764ba2);
  border-radius: 2px;
}

/* 搜索卡片 */
.search-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  background: white;
  margin-bottom: 20px;
}

/* 表格卡片 */
.table-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  background: white;
}

/* 分页 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 权限树 */
.permission-tree {
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 10px;
}

/* 无权限提示 */
.no-permission {
  color: #909399;
  font-size: 12px;
}

/* 对话框 */
.dialog-footer {
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .permission-management {
    padding: 10px 0;
  }
  
  .permission-management h1 {
    font-size: 24px;
    padding: 0 0 15px 0;
  }
  
  .permission-management h1::before {
    height: 24px;
  }
  
  .search-card,
  .table-card {
    margin: 0 10px 20px;
  }
  
  .permission-tree {
    max-height: 300px;
  }
}
</style>
