<template>
  <div class="register-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
    
    <!-- 使用与登录页面一致的布局 -->
    <div class="register-box">
      <!-- 左侧医院信息 -->
      <div class="hospital-info">
        <div class="hospital-logo">
          <el-icon class="hospital-icon"><User /></el-icon>
        </div>
        <h2 class="hospital-name">衡南县第四人民医院</h2>
        <p class="hospital-desc">致力于为医务人员提供高质量的三基考试培训</p>
        <div class="hospital-features">
          <div class="feature-item">
            <el-icon><Star /></el-icon>
            <span>专业权威</span>
          </div>
          <div class="feature-item">
            <el-icon><Timer /></el-icon>
            <span>高效便捷</span>
          </div>
          <div class="feature-item">
            <el-icon><Document /></el-icon>
            <span>全面覆盖</span>
          </div>
        </div>
      </div>
      
      <!-- 右侧注册表单 -->
      <div class="register-form-section">
        <div class="register-header">
          <h2 class="register-title">医疗卫生系统三基考试培训</h2>
          <p class="register-subtitle">专业、权威、高效的医务三基考试培训平台</p>
        </div>
        
        <el-form
          :model="registerForm"
          :rules="registerRules"
          ref="registerFormRef"
          label-position="left"
          label-width="80px"
          class="register-form"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large"
              class="custom-input"
            ></el-input>
          </el-form-item>
          
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              show-password
              size="large"
              class="custom-input"
            ></el-input>
          </el-form-item>
          
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请确认密码"
              prefix-icon="Lock"
              show-password
              size="large"
              class="custom-input"
            ></el-input>
          </el-form-item>
          
          <el-form-item label="真实姓名" prop="realName">
            <el-input
              v-model="registerForm.realName"
              placeholder="请输入真实姓名"
              prefix-icon="UserFilled"
              size="large"
              class="custom-input"
            ></el-input>
          </el-form-item>
          
          <el-form-item label="手机号" prop="phone">
            <el-input
              v-model="registerForm.phone"
              placeholder="请输入手机号（可选）"
              prefix-icon="Phone"
              size="large"
              class="custom-input"
            ></el-input>
          </el-form-item>
          
          <el-form-item label="验证码" prop="code">
            <div class="captcha-container">
              <el-input
                v-model="registerForm.code"
                placeholder="输入验证码"
                size="large"
                class="custom-input captcha-input"
                maxlength="4"
              ></el-input>
              <div class="captcha-image" @click="refreshCaptcha">
                <div class="captcha-content">{{ captchaQuestion }}</div>
              </div>
            </div>
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              class="register-btn"
              @click="handleRegister"
              :loading="loading"
              size="large"
            >
              注册
            </el-button>
          </el-form-item>
          
          <div class="login-section">
            <span>已有账号？</span>
            <el-button type="text" @click="handleLoginRedirect" class="login-link">
              立即登录
            </el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios'

const router = useRouter()
const registerFormRef = ref<FormInstance>()
const loading = ref(false)

// 验证码相关
const captchaQuestion = ref<string>('')
const captchaAnswer = ref<string>('')

// 生成4位字符验证码
const generateCaptcha = () => {
  // 验证码字符集 - 只使用大写字母和数字，避免大小写混淆
  const chars = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'
  let captcha = ''
  // 生成4个随机字符
  for (let i = 0; i < 4; i++) {
    captcha += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  // 设置验证码和答案
  captchaQuestion.value = captcha
  captchaAnswer.value = captcha
}

// 刷新验证码
const refreshCaptcha = () => {
  generateCaptcha()
}

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  code: ''
})

const registerRules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (_, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      }, trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { required: false, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 4, max: 4, message: '验证码长度为 4 个字符', trigger: 'blur' }
  ]
})

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    await registerFormRef.value.validate()
    
    // 验证验证码 - 不区分大小写
    if (registerForm.code.toUpperCase() !== captchaAnswer.value) {
      ElMessage.error('验证码错误')
      refreshCaptcha() // 刷新验证码
      return
    }
    
    loading.value = true
    
    // 准备注册数据，移除confirmPassword和code字段
    const { confirmPassword, code, ...registerData } = registerForm
    
    // 发送注册请求
    await axios.post('/auth/register', registerData)
    
    loading.value = false
    ElMessage.success('注册成功')
    
    // 注册成功后跳转到登录页面
    router.push('/login')
  } catch (error: any) {
    loading.value = false
    console.error('注册失败:', error)
    ElMessage.error(error.response?.data?.message || '注册失败，请稍后重试')
    refreshCaptcha() // 刷新验证码
  }
}

const handleLoginRedirect = () => {
  router.push('/login')
}

// 初始化验证码
onMounted(() => {
  generateCaptcha()
})
</script>

<style scoped>
.register-container {
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  bottom: 15%;
  right: 15%;
  animation-delay: 2s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  top: 60%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 注册框整体布局 - 使用grid实现左右两栏 */
.register-box {
  /* 整体背景色 */
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  width: 100%;
  max-width: 900px;
  height: 600px;
  backdrop-filter: blur(20px);
  position: relative;
  z-index: 1;
  transition: all 0.3s ease;
  overflow: hidden;
  /* 使用grid布局，左右两栏各占50% */
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0;
  padding: 0;
  margin: 0;
}

.register-box:hover {
  box-shadow: 0 15px 50px rgba(0, 0, 0, 0.2);
}

/* 左侧医院信息区域 - 直接使用grid项 */
.hospital-info {
  /* 左侧渐变背景 */
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  box-sizing: border-box;
  overflow: hidden;
  /* 左侧圆角 */
  border-radius: 20px 0 0 20px;
}

/* 右侧注册表单区域 - 直接使用grid项 */
.register-form-section {
  /* 右侧白色背景 */
  background: rgba(255, 255, 255, 0.95);
  padding: 40px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  box-sizing: border-box;
  overflow: hidden;
  /* 右侧圆角 */
  border-radius: 0 20px 20px 0;
}

.hospital-logo {
  width: 100px;
  height: 100px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 30px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.hospital-icon {
  font-size: 50px;
  color: white;
}

.hospital-name {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 15px;
  line-height: 1.2;
}

.hospital-desc {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 40px;
  line-height: 1.5;
}

/* 医院特色 */
.hospital-features {
  display: flex;
  gap: 30px;
  margin-top: 20px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.feature-item .el-icon {
  font-size: 24px;
  opacity: 0.9;
}

.feature-item span {
  font-size: 14px;
  opacity: 0.9;
}

/* 注册头部 */
.register-header {
  text-align: center;
  margin-bottom: 30px;
  margin-top: 20px;
  width: 100%;
  max-width: 350px;
  padding-top: 20px;
}

.register-title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.register-subtitle {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

/* 表单样式 */
.register-form {
  margin-top: 30px;
  width: 100%;
  max-width: 350px;
  overflow-y: auto;
  max-height: 400px;
  padding-right: 10px;
}

/* 自定义滚动条 */
.register-form::-webkit-scrollbar {
  width: 6px;
}

.register-form::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.register-form::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.register-form::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-form-item__label {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
}

.custom-input {
  border-radius: 12px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s ease;
  width: 100%;
}

.custom-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.1);
}

/* 注册按钮 */
.register-btn {
  width: 100%;
  height: 50px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
  max-width: 350px;
}

/* 验证码样式 */
.captcha-container {
  display: flex;
  gap: 10px;
  align-items: center;
}

.captcha-input {
  flex: 1;
}

.captcha-image {
  width: 150px;
  height: 40px;
  cursor: pointer;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #dcdfe6;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  user-select: none;
  font-family: Arial, sans-serif;
}

.captcha-image:hover {
  border-color: #667eea;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.2);
  background: linear-gradient(135deg, #ecf5ff 0%, #d0ecff 100%);
}

.captcha-content {
  padding: 8px 16px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.register-btn:hover {
  background: linear-gradient(135deg, #536dfe 0%, #6200ea 100%);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
  transform: translateY(-2px);
}

.register-btn:active {
  transform: translateY(0);
}

/* 登录区域 */
.login-section {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #606266;
  width: 100%;
  max-width: 350px;
}

.login-link {
  color: #667eea;
  font-weight: 500;
  margin-left: 5px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-box {
    margin: 0 20px;
    max-width: none;
    height: auto;
    min-height: 500px;
    /* 移动端改为上下布局 */
    grid-template-columns: 1fr;
    grid-template-rows: auto auto;
  }
  
  /* 调整移动端各区域的顺序和圆角 */
  .hospital-info {
    padding: 40px 30px;
    /* 移动端顶部圆角 */
    border-radius: 20px 20px 0 0;
    /* 调整顺序到下方 */
    grid-row: 2;
    min-height: 300px;
  }
  
  .register-form-section {
    padding: 40px 30px;
    /* 移动端底部圆角 */
    border-radius: 0 0 20px 20px;
    /* 调整顺序到上方 */
    grid-row: 1;
    min-height: 500px;
  }
  
  .hospital-name {
    font-size: 24px;
  }
  
  .register-title {
    font-size: 24px;
  }
  
  .hospital-logo {
    width: 80px;
    height: 80px;
  }
  
  .hospital-icon {
    font-size: 40px;
  }
  
  .hospital-features {
    gap: 20px;
  }
}
</style>