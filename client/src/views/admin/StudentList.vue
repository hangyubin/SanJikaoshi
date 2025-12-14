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

        <el-table-column prop="createTime" label="创建时间" width="180" :formatter="formatDate"></el-table-column>
        <el-table-column prop="role" label="角色" width="120">
          <template #default="scope">
            <el-tag :type="getRoleType(scope.row.role, scope.row.username)">
              {{ getRoleLabel(scope.row.role, scope.row.username) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="jobTitle" label="职称" width="120"></el-table-column>
        <el-table-column prop="department" label="科室" width="120"></el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180"></el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <!-- 系统管理员admin不能被任何账号操作 -->
            <el-button 
              type="danger" 
              size="small" 
              @click="handleResetPassword(scope.row)"
              :disabled="scope.row.username === 'admin'"
            >
              重置密码
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
    

    
    <!-- 重置密码对话框 -->
    <el-dialog
      v-model="resetPasswordDialogVisible"
      title="重置密码"
      width="500px"
    >
      <el-form :model="resetPasswordForm" label-width="120px">
        <el-form-item label="用户名">
          <el-input v-model="resetPasswordForm.username" disabled></el-input>
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="resetPasswordForm.newPassword" type="password" placeholder="请输入新密码"></el-input>
          <div class="password-hint">
            <el-icon class="hint-icon"><WarningFilled /></el-icon>
            <span>友情提示：密码长度至少6个字符，建议包含字母和数字</span>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="resetPasswordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleResetPasswordSubmit">确认重置</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElIcon } from 'element-plus'
import { WarningFilled } from '@element-plus/icons-vue'
import axios from '@/utils/axios'
import { getRoleType, getRoleLabel } from '@/utils/roleConfig'

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

// 格式化日期
const formatDate = (_row: any, _column: any, cellValue: any) => {
  if (!cellValue) return ''
  const date = new Date(cellValue)
  return date.toLocaleString('zh-CN')
}

// 页面加载时获取用户列表
onMounted(() => {
  fetchUsers()
})

// 重置密码对话框
const resetPasswordDialogVisible = ref(false)
const resetPasswordForm = reactive({
  id: '',
  username: '',
  newPassword: ''
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

// 重置密码
const handleResetPassword = (row: any) => {
  resetPasswordForm.id = row.id
  resetPasswordForm.username = row.username
  resetPasswordForm.newPassword = ''
  resetPasswordDialogVisible.value = true
}

// 提交重置密码
const handleResetPasswordSubmit = async () => {
  try {
    // 验证新密码
    if (!resetPasswordForm.newPassword || resetPasswordForm.newPassword.length < 6) {
      ElMessage.error('密码长度不能少于6个字符')
      return
    }
    
    // 调用后端API重置密码
    await axios.put(`/users/${resetPasswordForm.id}/reset-password`, {
      password: resetPasswordForm.newPassword
    })
    
    ElMessage.success('密码重置成功')
    resetPasswordDialogVisible.value = false
    fetchUsers() // 刷新用户列表
  } catch (error) {
    console.error('密码重置失败:', error)
    ElMessage.error('密码重置失败')
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

/* 密码提示样式 */
.password-hint {
  margin-top: 8px;
  padding: 10px;
  background-color: #fff3cd;
  border: 1px solid #ffeeba;
  border-radius: 4px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #856404;
}

.hint-icon {
  color: #ffc107;
  font-size: 16px;
}
</style>



