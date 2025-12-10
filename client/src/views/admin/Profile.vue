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
              <div class="avatar-actions">
                <el-button type="primary" @click="showAvatarOptions = true">更换头像</el-button>
              </div>
            </div>
          </el-form-item>
          
          <!-- 基本信息表单 -->
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="用户名" prop="username" disabled>
                <el-input v-model="userForm.username" placeholder="用户名"></el-input>
              </el-form-item>
              
              <el-form-item label="真实姓名" prop="realName">
                <el-input v-model="userForm.realName" placeholder="真实姓名"></el-input>
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
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios'

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
  username: '',
  realName: '',
  phone: '',
  avatar: '',
  gender: 0,
  department: '',
  jobTitle: ''
})

// 表单验证规则
const rules = reactive<FormRules>({
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
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
      return
    }
    
    // 从API获取用户信息
      const response = await axios.get('/user/profile')
      const { data } = response
      if (data.code === 200) {
        Object.assign(userForm, data.data)
      } else {
        // 如果API调用失败，使用基本数据，不使用模拟数据
        userForm.username = 'admin'
        userForm.avatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
      }
    
    selectedSystemAvatar.value = userForm.avatar
  } catch (error) {
    console.error('获取用户信息失败:', error)
    // 如果API调用失败，使用基本数据，不使用模拟数据
    userForm.username = 'admin'
    userForm.avatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
    selectedSystemAvatar.value = userForm.avatar
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 保存用户信息到localStorage，确保头像等信息能够持久保存
    localStorage.setItem('userInfo', JSON.stringify(userForm))
    
    // 发送保存用户信息请求
    const response = await axios.put('/user/profile', userForm)
    const { data } = response
    if (data.code === 200) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error('保存失败')
    }
  } catch (error) {
    console.error('保存用户信息失败:', error)
    ElMessage.error('保存失败')
  }
}

// 重置表单
const handleReset = () => {
  fetchUserInfo()
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
const confirmAvatarChange = () => {
  userForm.avatar = selectedSystemAvatar.value
  showAvatarOptions.value = false
  ElMessage.success('头像更换成功')
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

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 10px;
}
</style>