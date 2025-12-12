<template>
  <div class="role-management">
    <h1>角色与权限管理</h1>
    
    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <el-form :model="searchForm" label-width="80px" inline>
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.name" placeholder="请输入角色名称"></el-input>
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
          <el-button type="success" @click="handleAdd">添加角色</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 角色表格 -->
    <el-card class="table-card" style="margin-top: 20px;">
      <el-table :data="roles" stripe style="width: 100%">
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="角色名称" width="150"></el-table-column>
        <el-table-column prop="code" label="角色代码" width="150"></el-table-column>
        <el-table-column prop="description" label="角色描述"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              active-value="1"
              inactive-value="0"
              @change="handleStatusChange(scope.row)"
              :disabled="scope.row.code === 'ROLE_ADMIN'"
            ></el-switch>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <!-- 系统管理员角色不能被修改或删除 -->
            <el-button 
              type="primary" 
              size="small" 
              @click="handleEdit(scope.row)"
              :disabled="scope.row.code === 'ROLE_ADMIN'"
            >
              编辑
            </el-button>
            <el-button 
              type="warning" 
              size="small" 
              @click="handleAssignPermission(scope.row)"
              :disabled="scope.row.code === 'ROLE_ADMIN'"
            >
              分配权限
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              @click="handleDelete(scope.row)"
              :disabled="scope.row.code === 'ROLE_ADMIN'"
            >
              删除
            </el-button>
            <el-button 
              type="info" 
              size="small" 
              @click="handleViewUsers(scope.row)"
            >
              查看成员
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
    
    <!-- 添加/编辑角色对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入角色名称"></el-input>
        </el-form-item>
        <el-form-item label="角色代码" prop="code">
          <el-input v-model="form.code" placeholder="请输入角色代码" :disabled="form.id"></el-input>
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入角色描述"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="form.status" active-value="1" inactive-value="0"></el-switch>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确认</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 分配权限对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="分配权限"
      width="800px"
    >
      <el-tree
        :data="menuTree"
        :props="treeProps"
        show-checkbox
        node-key="id"
        default-expand-all
        :checked-keys="checkedMenuIds"
        @check="handleMenuCheck"
        ref="menuTreeRef"
      ></el-tree>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handlePermissionSubmit">确认分配</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 角色成员列表对话框 -->
    <el-dialog
      v-model="userDialogVisible"
      :title="`${currentRoleName} - 角色成员`"
      width="800px"
    >
      <el-table :data="roleUsers" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="realName" label="姓名" width="120"></el-table-column>
        <el-table-column prop="phone" label="手机号" width="150"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">
              {{ scope.row.status === '1' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="userDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '@/utils/axios'

// 角色数据
const roles = ref<any[]>([])
const menus = ref<any[]>([])
const menuTree = ref<any[]>([])
const checkedMenuIds = ref<any[]>([])
const roleUsers = ref<any[]>([])

// 搜索表单
const searchForm = reactive({
  name: '',
  status: ''
})

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('添加角色')
const permissionDialogVisible = ref(false)
const userDialogVisible = ref(false)
const formRef = ref<FormInstance>()
const menuTreeRef = ref()
const currentRoleId = ref(0)
const currentRoleName = ref('')

// 表单数据
const form = reactive({
  id: '',
  name: '',
  code: '',
  description: '',
  status: '1'
})

// 表单验证规则
const rules = reactive<FormRules>({
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 20, message: '角色名称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入角色代码', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: '角色代码只能包含大写字母和下划线', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '角色描述不能超过 200 个字符', trigger: 'blur' }
  ]
})

// 树形控件配置
const treeProps = {
  children: 'children',
  label: 'name'
}

// 获取角色列表
const fetchRoles = () => {
  loading.value = true
  axios.get('/roles', {
    params: {
      page: currentPage.value,
      pageSize: pageSize.value,
      name: searchForm.name,
      status: searchForm.status
    }
  })
  .then(res => {
    roles.value = res.data.roles || []
    total.value = res.data.total || 0
  })
  .catch(error => {
    console.error('获取角色列表失败:', error)
    ElMessage.error('获取角色列表失败')
  })
  .finally(() => {
    loading.value = false
  })
}

// 获取菜单列表
const fetchMenus = () => {
  axios.get('/menus')
    .then(res => {
      menus.value = res.data.menus || []
      // 构建树形菜单
      buildMenuTree()
    })
    .catch(error => {
      console.error('获取菜单列表失败:', error)
      ElMessage.error('获取菜单列表失败')
    })
}

// 构建树形菜单
const buildMenuTree = () => {
  const rootMenus = menus.value.filter(menu => !menu.parentId)
  const buildTree = (parent: any) => {
    const children = menus.value.filter(menu => menu.parentId === parent.id)
    if (children.length > 0) {
      parent.children = children
      children.forEach(child => buildTree(child))
    }
  }
  rootMenus.forEach(root => buildTree(root))
  menuTree.value = rootMenus
}

// 获取角色菜单
const fetchRoleMenus = (roleId: number) => {
  axios.get(`/roles/${roleId}/menus`)
    .then(res => {
      const roleMenus = res.data.menus || []
      checkedMenuIds.value = roleMenus.map((menu: any) => menu.id)
    })
    .catch(error => {
      console.error('获取角色菜单失败:', error)
      ElMessage.error('获取角色菜单失败')
    })
}

// 获取角色成员
const fetchRoleUsers = (roleId: number) => {
  axios.get(`/roles/${roleId}/users`)
    .then(res => {
      roleUsers.value = res.data.users || []
    })
    .catch(error => {
      console.error('获取角色成员失败:', error)
      ElMessage.error('获取角色成员失败')
    })
}

// 页面加载
onMounted(() => {
  fetchRoles()
  fetchMenus()
})

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchRoles()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.status = ''
  currentPage.value = 1
  fetchRoles()
}

// 添加角色
const handleAdd = () => {
  dialogTitle.value = '添加角色'
  // 重置表单
  Object.assign(form, {
    id: '',
    name: '',
    code: '',
    description: '',
    status: '1'
  })
  dialogVisible.value = true
}

// 编辑角色
const handleEdit = (row: any) => {
  dialogTitle.value = '编辑角色'
  // 填充表单
  Object.assign(form, row)
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (form.id) {
      // 编辑角色
      await axios.put(`/roles/${form.id}`, form)
      ElMessage.success('角色编辑成功')
    } else {
      // 添加角色
      await axios.post('/roles', form)
      ElMessage.success('角色添加成功')
    }
    
    dialogVisible.value = false
    fetchRoles()
  } catch (error) {
    console.error('表单提交失败:', error)
    ElMessage.error('表单提交失败')
  }
}

// 删除角色
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除该角色吗？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(() => {
      return axios.delete(`/roles/${row.id}`)
    })
    .then(() => {
      ElMessage.success('角色删除成功')
      fetchRoles()
    })
    .catch(() => {
      ElMessage.info('已取消删除操作')
    })
}

// 状态变更
const handleStatusChange = (row: any) => {
  axios.put(`/roles/${row.id}/status`, { status: row.status })
    .then(() => {
      ElMessage.success('状态更新成功')
    })
    .catch(error => {
      console.error('更新状态失败:', error)
      ElMessage.error('状态更新失败')
      fetchRoles()
    })
}

// 分配权限
const handleAssignPermission = (row: any) => {
  currentRoleId.value = row.id
  currentRoleName.value = row.name
  permissionDialogVisible.value = true
  fetchRoleMenus(row.id)
}

// 菜单勾选
const handleMenuCheck = (data: any, checked: any) => {
  // 处理菜单勾选逻辑
  console.log('菜单勾选:', data, checked)
}

// 提交权限分配
const handlePermissionSubmit = () => {
  const selectedMenus = menuTreeRef.value.getCheckedNodes(true)
  const selectedMenuIds = selectedMenus.map((node: any) => node.id)
  
  axios.put(`/roles/${currentRoleId.value}/menus`, { menuIds: selectedMenuIds })
    .then(() => {
      ElMessage.success('权限分配成功')
      permissionDialogVisible.value = false
    })
    .catch(error => {
      console.error('权限分配失败:', error)
      ElMessage.error('权限分配失败')
    })
}

// 查看角色成员
const handleViewUsers = (row: any) => {
  currentRoleName.value = row.name
  userDialogVisible.value = true
  fetchRoleUsers(row.id)
}

// 分页大小变更
const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
  fetchRoles()
}

// 当前页变更
const handleCurrentChange = (current: number) => {
  currentPage.value = current
  fetchRoles()
}
</script>

<style scoped>
.role-management {
  padding: 20px 0;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

/* 页面标题 */
.role-management h1 {
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

.role-management h1::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 32px;
  background: linear-gradient(180deg, #667eea, #764ba2);
  border-radius: 2px;
}

/* 搜索卡片 */
.search-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  background: white;
  overflow: hidden;
  position: relative;
}

.search-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2);
}

/* 表格卡片 */
.table-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  background: white;
  overflow: hidden;
  position: relative;
}

.table-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2);
}

/* 分页 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
