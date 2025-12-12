<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <h2>个人中心</h2>
        </div>
      </template>
      
      <div class="profile-content">
        <!-- 基本信息 -->
        <el-form :model="userForm" :rules="rules" ref="formRef" label-width="120px" class="profile-form">
          <!-- 头像设置 -->
          <el-form-item label="头像">
            <div class="avatar-section">
              <el-avatar :size="120" :src="userForm.avatar" class="user-avatar"></el-avatar>
              <div class="avatar-actions" v-if="!isSystemAdmin">
                <el-button type="primary" @click="showAvatarOptions = true">更换头像</el-button>
              </div>
            </div>
          </el-form-item>
          
          <!-- 基本信息表单 - 只有非系统管理员可以编辑 -->
          <template v-if="!isSystemAdmin">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="用户名">
                  <div class="profile-info-text">{{ userForm.username }}</div>
                </el-form-item>
                
                <el-form-item label="姓名" prop="realName">
                <el-input v-model="userForm.realName" placeholder="请输入姓名"></el-input>
              </el-form-item>
                
                <el-form-item label="性别" prop="gender">
                  <el-select v-model="userForm.gender" placeholder="请选择性别">
                    <el-option label="男" :value="1"></el-option>
                    <el-option label="女" :value="2"></el-option>
                    <el-option label="保密" :value="0"></el-option>
                  </el-select>
                </el-form-item>
                
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="userForm.phone" placeholder="手机号"></el-input>
                </el-form-item>
              </el-col>
              
              <el-col :span="12">
                <el-form-item label="科室" prop="department">
                  <el-input v-model="userForm.department" placeholder="请输入科室"></el-input>
                </el-form-item>
                
                <el-form-item label="职称" prop="jobTitle">
                  <el-input v-model="userForm.jobTitle" placeholder="职称"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            
            <!-- 保存按钮 -->
            <div class="form-actions">
              <el-button type="primary" @click="handleSubmit">保存修改</el-button>
              <el-button @click="handleReset">重置</el-button>
            </div>
          </template>
          
          <!-- 系统管理员只显示头像设置，修改密码功能在顶部导航栏 -->
          <template v-else>
            <!-- 系统管理员保留头像设置功能 -->
            <div class="system-admin-actions">
              <el-button type="primary" @click="openAvatarDialog">更换头像</el-button>
            </div>
          </template>
        </el-form>
      </div>
    </el-card>
    
    <!-- 头像选择弹窗 -->
    <el-dialog v-model="showAvatarOptions" title="更换头像" width="600px">
      <div class="avatar-options">
        <!-- 系统提供的头像选择 -->
        <div class="avatar-category">
          <h3>系统头像</h3>
          <div class="avatar-grid">
            <div 
              v-for="(avatar, index) in systemAvatars" 
              :key="index" 
              class="avatar-item"
              :class="{ 'selected': selectedSystemAvatar === avatar }"
              @click="selectedSystemAvatar = avatar"
            >
              <el-avatar :size="60" :src="avatar"></el-avatar>
            </div>
          </div>
        </div>
        
        <!-- 上传头像 -->
        <div class="avatar-category">
          <h3>上传头像</h3>
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :before-upload="handleAvatarUpload"
          >
            <el-button type="primary">上传图片</el-button>
            <div class="el-upload__tip">
              支持 JPG、PNG 格式，文件大小不超过 2MB
            </div>
          </el-upload>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showAvatarOptions = false">取消</el-button>
          <el-button type="primary" @click="confirmAvatarChange">确认更换</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

// 表单引用
const formRef = ref<FormInstance>()

// 头像选择弹窗
const showAvatarOptions = ref(false)
const selectedSystemAvatar = ref('')

// 系统提供的头像选项
const systemAvatars = ref([
  // 男性头像
  'https://picsum.photos/id/1005/400/400',
  'https://picsum.photos/id/1012/400/400',
  'https://picsum.photos/id/1013/400/400',
  'https://picsum.photos/id/1016/400/400',
  'https://picsum.photos/id/1025/400/400',
  'https://picsum.photos/id/1035/400/400',
  'https://picsum.photos/id/1062/400/400',
  'https://picsum.photos/id/1074/400/400',
  
  // 女性头像
  'https://picsum.photos/id/1027/400/400',
  'https://picsum.photos/id/1042/400/400',
  'https://picsum.photos/id/1060/400/400',
  'https://picsum.photos/id/1066/400/400',
  'https://picsum.photos/id/1070/400/400',
  'https://picsum.photos/id/1077/400/400',
  'https://picsum.photos/id/1082/400/400',
  'https://picsum.photos/id/1083/400/400',
  
  // 中性头像/职业头像
  'https://picsum.photos/id/1024/400/400',
  'https://picsum.photos/id/1059/400/400',
  'https://picsum.photos/id/1071/400/400',
  'https://picsum.photos/id/1084/400/400',
  'https://picsum.photos/id/1092/400/400',
  'https://picsum.photos/id/1094/400/400',
  'https://picsum.photos/id/110/400/400',
  'https://picsum.photos/id/116/400/400'
])

// 用户表单
const userForm = reactive({
  id: 0,
  username: '',
  realName: '',
  phone: '',
  avatar: '',
  gender: 0,
  department: '',
  jobTitle: ''
})

// 用户角色
const userRole = ref<string>('')

// 计算属性：是否为系统管理员
const isSystemAdmin = computed(() => {
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    const parsedInfo = JSON.parse(userInfo)
    // admin账号直接是系统管理员
    if (parsedInfo.username === 'admin') {
      return true
    }
    
    // 检查角色信息
    const roleValue = parsedInfo.role || parsedInfo.roles || ''
    
    // 支持字符串格式
    if (typeof roleValue === 'string') {
      return roleValue === 'sysadmin' || roleValue === 'ROLE_SYS_ADMIN' || roleValue === '系统管理员'
    }
    
    // 支持数组格式
    if (Array.isArray(roleValue)) {
      return roleValue.some((role: any) => {
        if (typeof role === 'string') {
          return role === 'sysadmin' || role === 'ROLE_SYS_ADMIN' || role === '系统管理员'
        } else {
          return role.value === 'sysadmin' || role.code === 'ROLE_SYS_ADMIN' || role.name === '系统管理员'
        }
      })
    }
  }
  return false
})

// 表单验证规则
const rules = reactive<FormRules>({
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { pattern: /^[\u4e00-\u9fa5]+$/, message: '请输入有效的中文姓名，不能包含空格或特殊字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  department: [
    { required: true, message: '请输入科室', trigger: 'blur' }
  ],
  jobTitle: [
    { required: false, message: '请输入职称', trigger: 'blur' }
  ]
})

// 初始化用户数据
const fetchUserInfo = async () => {
  try {
    // 从localStorage获取用户信息
    const savedUserInfo = localStorage.getItem('userInfo')
    if (savedUserInfo) {
      const userInfo = JSON.parse(savedUserInfo)
      Object.assign(userForm, userInfo)
      selectedSystemAvatar.value = userForm.avatar
      // 获取用户角色
      if (userInfo.roles && userInfo.roles.length > 0) {
        userRole.value = userInfo.roles.map((role: any) => role.code).join(',')
      }
      return
    }
    
    // 如果localStorage中没有用户信息，显示默认信息
    userForm.username = 'admin'
    userForm.avatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
    userRole.value = 'ROLE_ADMIN'
    selectedSystemAvatar.value = userForm.avatar
  } catch (error) {
    console.error('获取用户信息失败:', error)
    // 如果API调用失败，使用基本数据，不使用模拟数据
    userForm.username = 'admin'
    userForm.avatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
    userRole.value = 'ROLE_ADMIN'
    selectedSystemAvatar.value = userForm.avatar
  }
}

// 引入axios
import axios from '@/utils/axios'

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 调用后端API更新用户信息
    const response = await axios.put(`/users/${userForm.id}`, userForm)
    
    // 保存用户信息到localStorage
    const userData = response.data || userForm
    localStorage.setItem('userInfo', JSON.stringify(userData))
    // 手动触发storage事件，确保同一标签页内的组件也能收到更新
    window.dispatchEvent(new Event('storage'))
    ElMessage.success('保存成功')
  } catch (error: any) {
    console.error('保存用户信息失败:', error)
    // 处理不同类型的错误
    if (error.response) {
      // 服务器返回了错误响应
      ElMessage.error(error.response.data?.message || '保存失败')
    } else {
      // 网络错误或其他错误
      ElMessage.error('网络错误，请稍后重试')
    }
  }
}

// 重置表单
const handleReset = () => {
  fetchUserInfo()
}

// 打开头像选择弹窗
const openAvatarDialog = () => {
  showAvatarOptions.value = true
}

// 处理头像上传
const handleAvatarUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isImage) {
    ElMessage.error('请上传图片文件')
    return false
  }
  
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  
  // 模拟上传处理
  // 实际使用时应替换为真实的上传逻辑
  const reader = new FileReader()
  reader.onload = (e) => {
    const result = e.target?.result as string
    selectedSystemAvatar.value = result
  }
  reader.readAsDataURL(file)
  
  return false
}

// 确认更换头像
const confirmAvatarChange = async () => {
  try {
    userForm.avatar = selectedSystemAvatar.value
    
    // 调用后端API更新头像
    const response = await axios.put(`/users/${userForm.id}`, {
      avatar: userForm.avatar
    }) as any
    
    if (response.code === 200) {
      // 将更新后的用户信息保存到localStorage
      localStorage.setItem('userInfo', JSON.stringify(response.data))
      // 手动触发storage事件，确保同一标签页内的MainLayout.vue也能收到更新
      window.dispatchEvent(new Event('storage'))
      showAvatarOptions.value = false
      ElMessage.success('头像更换成功')
    } else {
      ElMessage.error('头像更换失败')
    }
  } catch (error) {
    console.error('更换头像失败:', error)
    ElMessage.error('头像更换失败')
  }
}

// 初始化
onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.profile-container {
  padding: 20px 0;
}

.profile-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 20px;
}

.profile-content {
  padding: 20px 0;
}

.profile-form {
  margin-top: 20px;
}

.avatar-section {
  display: flex;
  align-items: center;
}

.user-avatar {
  margin-right: 20px;
  cursor: pointer;
}

.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.form-actions {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  gap: 10px;
}

/* 系统管理员操作区域 */
.system-admin-actions {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  gap: 10px;
}

/* 头像选择弹窗样式 */
.avatar-options {
  padding: 20px 0;
}

.avatar-category {
  margin-bottom: 30px;
}

.avatar-category h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: bold;
}

.avatar-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.avatar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  padding: 5px;
  border-radius: 8px;
  transition: all 0.3s;
}

.avatar-item:hover {
  background-color: #f0f0f0;
}

.avatar-item.selected {
  background-color: #ecf5ff;
  border: 2px solid #409eff;
}

.avatar-uploader {
  margin-top: 10px;
}

.profile-info-text {
  padding: 8px 12px;
  background-color: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  font-size: 14px;
  color: #303133;
  line-height: 1.5;
  box-sizing: border-box;
  width: 100%;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 10px;
}
</style>



