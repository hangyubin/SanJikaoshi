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
              
              <el-form-item label="年龄" prop="age">
                <el-input-number v-model="userForm.age" :min="1" :max="120" placeholder="年龄"></el-input-number>
              </el-form-item>
              
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="userForm.phone" placeholder="手机号"></el-input>
              </el-form-item>
            </el-col>
            
            <el-col :span="12">
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="userForm.email" placeholder="邮箱" type="email"></el-input>
              </el-form-item>
              
              <el-form-item label="科室" prop="department">
                <el-select v-model="userForm.department" placeholder="请选择科室">
                  <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id"></el-option>
                </el-select>
              </el-form-item>
              
              <el-form-item label="职称" prop="jobTitle">
                <el-input v-model="userForm.jobTitle" placeholder="职称"></el-input>
              </el-form-item>
              
              <el-form-item label="工号" prop="employeeId">
                <el-input v-model="userForm.employeeId" placeholder="工号"></el-input>
              </el-form-item>
              
              <el-form-item label="工龄" prop="yearsOfService">
                <el-input-number v-model="userForm.yearsOfService" :min="0" placeholder="工龄"></el-input-number>
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

// 表单引用
const formRef = ref<FormInstance>()

// 头像选择弹窗
const showAvatarOptions = ref(false)
const selectedSystemAvatar = ref('')

// 系统提供的头像选项
const systemAvatars = ref([
  'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
  'https://cube.elemecdn.com/1/88/03b0d39583f48206768a7534e55bcpng.png',
  'https://cube.elemecdn.com/2/88/03b0d39583f48206768a7534e55bcpng.png',
  'https://cube.elemecdn.com/3/88/03b0d39583f48206768a7534e55bcpng.png',
  'https://cube.elemecdn.com/4/88/03b0d39583f48206768a7534e55bcpng.png',
  'https://cube.elemecdn.com/5/88/03b0d39583f48206768a7534e55bcpng.png',
  'https://cube.elemecdn.com/6/88/03b0d39583f48206768a7534e55bcpng.png',
  'https://cube.elemecdn.com/7/88/03b0d39583f48206768a7534e55bcpng.png',
])

// 科室列表
const departments = ref<any[]>([
  { id: 1, name: '内科' },
  { id: 2, name: '外科' },
  { id: 3, name: '儿科' },
  { id: 4, name: '妇产科' },
  { id: 5, name: '急诊科' },
])

// 用户表单
const userForm = reactive({
  username: '',
  realName: '',
  email: '',
  phone: '',
  avatar: '',
  gender: 0,
  age: null,
  department: null,
  jobTitle: '',
  employeeId: '',
  yearsOfService: null
})

// 表单验证规则
const rules = reactive<FormRules>({
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
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  age: [
    { required: true, message: '请输入年龄', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请选择科室', trigger: 'change' }
  ]
})

// 初始化用户数据
const fetchUserInfo = async () => {
  try {
    // 模拟从后端获取用户信息
    // 实际使用时应替换为真实API调用
    // const response = await axios.get('/api/user/profile')
    // if (response.data.code === 200) {
    //   Object.assign(userForm, response.data.data)
    // }
    
    // 模拟数据
    Object.assign(userForm, {
      username: 'admin',
      realName: '系统管理员',
      email: 'admin@example.com',
      phone: '13800138000',
      avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      gender: 1,
      age: 30,
      department: 1,
      jobTitle: '主任医师',
      employeeId: 'EMP001',
      yearsOfService: 10
    })
    
    selectedSystemAvatar.value = userForm.avatar
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 模拟保存用户信息
    // 实际使用时应替换为真实API调用
    // const response = await axios.put('/api/user/profile', userForm)
    // if (response.data.code === 200) {
    //   ElMessage.success('保存成功')
    // } else {
    //   ElMessage.error('保存失败')
    // }
    
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('表单验证失败:', error)
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