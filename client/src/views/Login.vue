<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>
    
    <!-- 使用单个容器，内部使用grid布局 -->
    <div class="login-box">
      <!-- 左侧医院信息 -->
      <div class="hospital-info">
        <div class="hospital-logo">
          <el-icon class="hospital-icon"><component :is="currentHospitalIcon" /></el-icon>
        </div>
        <h2 class="hospital-name">{{ hospitalInfo.name }}</h2>
        <p class="hospital-desc">{{ hospitalInfo.description }}</p>
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
      
      <!-- 右侧登录表单 -->
      <div class="login-form-section">
        <div class="login-header">
          <h2 class="login-title">卫生系统三基在线学习平台</h2>
          <p class="login-subtitle">专业、权威、高效的医务人员三基学习平台</p>
        </div>
        
        <el-form
          :model="loginForm"
          :rules="loginRules"
          ref="loginFormRef"
          label-position="left"
          label-width="80px"
          class="login-form"
        >
          <el-form-item label="用户名" prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large"
              class="custom-input"
            ></el-input>
          </el-form-item>
          
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              show-password
              size="large"
              class="custom-input"
            ></el-input>
          </el-form-item>
          
          <el-form-item label="验证码" prop="code">
            <div class="captcha-container">
              <el-input
                v-model="loginForm.code"
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
          
          <div class="form-actions">
            <el-checkbox v-model="rememberMe" class="remember-me">记住我</el-checkbox>
            <el-link type="primary" :underline="false" class="forgot-password">忘记密码？</el-link>
          </div>
          
          <el-form-item label="">
            <div class="button-group">
              <div class="button-item">
                <el-button
                  class="login-btn"
                  @click="handleLogin"
                  :loading="loading"
                >
                  登录
                </el-button>
              </div>
              
              <div class="button-item">
                <el-button
                  class="register-btn"
                  @click="handleRegisterRedirect"
                >
                  立即注册
                </el-button>
              </div>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios'

const router = useRouter()
const loginFormRef = ref<FormInstance>()
const loading = ref(false)

// 医院信息
const hospitalInfo = reactive({
  name: '衡南县第四人民医院',
  description: '致力于为医务人员提供高质量的三基学习服务',
  icon: 'User'
})

const loginForm = reactive({
  username: '',
  password: '',
  code: ''
})

const loginRules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 4, max: 4, message: '验证码长度为 4 个字符', trigger: 'blur' }
  ]
})

const rememberMe = ref(false)

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

// 初始化验证码
onMounted(() => {
  const savedHospitalInfo = localStorage.getItem('hospitalInfo')
  if (savedHospitalInfo) {
    const parsedInfo = JSON.parse(savedHospitalInfo)
    Object.assign(hospitalInfo, parsedInfo)
  }
  
  // 读取记住的用户信息
  const savedUser = localStorage.getItem('rememberedUser')
  if (savedUser) {
    const parsedUser = JSON.parse(savedUser)
    loginForm.username = parsedUser.username || ''
    loginForm.password = parsedUser.password || ''
    rememberMe.value = true
  }
  
  // 初始化验证码
  generateCaptcha()
})

// 计算当前医院图标组件
const currentHospitalIcon = computed(() => {
  return ElementPlusIconsVue[hospitalInfo.icon as keyof typeof ElementPlusIconsVue] || ElementPlusIconsVue.User
})

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    
    // 验证验证码 - 不区分大小写
    if (loginForm.code.toUpperCase() !== captchaAnswer.value) {
      ElMessage.error('验证码错误')
      refreshCaptcha() // 刷新验证码
      return
    }
    
    loading.value = true
    
    // 发送登录请求
    const response = await axios.post('/auth/login', {
      username: loginForm.username,
      password: loginForm.password
    })
    
    loading.value = false
    
    // 处理记住我功能
    if (rememberMe.value) {
      // 保存用户信息到本地存储
      localStorage.setItem('rememberedUser', JSON.stringify({
        username: loginForm.username,
        password: loginForm.password
      }))
    } else {
      // 清除记住的用户信息
      localStorage.removeItem('rememberedUser')
    }
    
    // 保存token和用户信息到localStorage
    localStorage.setItem('token', response.token)
    localStorage.setItem('userInfo', JSON.stringify({
      username: response.username,
      realName: response.realName,
      roles: response.roles
    }))
    
    // 登录成功，跳转到dashboard
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (error: any) {
    loading.value = false
    console.error('登录失败:', error)
    ElMessage.error(error.response?.data?.message || '登录失败，请稍后重试')
    // 登录失败时刷新验证码
    refreshCaptcha()
  }
}

const handleRegisterRedirect = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

/* 注册按钮样式 */
.register-btn {
  width: 100%;
  height: 50px;
  border-radius: 12px;
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  border: none;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(103, 194, 58, 0.3);
  max-width: 350px;
  margin-top: 15px;
}

.register-btn:hover {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
  box-shadow: 0 6px 20px rgba(103, 194, 58, 0.4);
  transform: translateY(-2px);
}

.register-btn:active {
  transform: translateY(0);
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

/* 登录框整体布局 - 使用grid实现左右两栏 */
.login-box {
  /* 整体背景色 */
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  width: 100%;
  max-width: 900px;
  height: 500px;
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

.login-box:hover {
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

/* 右侧登录表单区域 - 直接使用grid项 */
.login-form-section {
  /* 右侧白色背景 */
  background: rgba(255, 255, 255, 0.95);
  padding: 60px 50px;
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

/* 登录头部 */
.login-header {
  text-align: center;
  margin-bottom: 40px;
  margin-top: 20px;
  width: 100%;
  max-width: 350px;
  padding-top: 20px;
}

.login-title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.login-subtitle {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

/* 表单样式 */
.login-form {
  margin-top: 30px;
  width: 100%;
  max-width: 350px;
}

.el-form-item {
  margin-bottom: 25px;
}

.el-form-item__label {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
}

/* 没有label的el-form-item，确保内容占满整个宽度 */
.el-form-item.is-no-label .el-form-item__content {
  margin-left: 0 !important;
  width: 100%;
  padding: 0;
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

/* 表单操作区 */
.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.remember-me {
  font-size: 14px;
  color: #606266;
}

.forgot-password {
  font-size: 14px;
  color: #667eea;
}

.forgot-password:hover {
  color: #536dfe;
}

/* 按钮组 */
.button-group {
  display: flex;
  gap: 15px;
  width: 100%;
  margin-top: 10px;
  align-items: stretch;
  padding: 0;
}

.button-item {
  margin: 0 !important;
  flex: 1;
  padding: 0 !important;
  min-width: 0;
}

/* 统一按钮样式 */
.login-btn,
.register-btn {
  width: 100%;
  height: 50px;
  border-radius: 12px;
  border: 1px solid #dcdfe6;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  outline: none;
  letter-spacing: normal;
  text-indent: 0;
  text-transform: none;
  overflow: visible;
  font-family: inherit;
  text-decoration: none;
  margin: 0;
  padding: 0;
}

/* 登录按钮特有样式 */
.login-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

/* 注册按钮特有样式 */
.register-btn {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(103, 194, 58, 0.3);
}

/* 统一按钮悬停效果 */
.login-btn:hover,
.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
}

.login-btn:hover {
  background: linear-gradient(135deg, #536dfe 0%, #6200ea 100%);
}

.register-btn:hover {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
}

/* 统一按钮点击效果 */
.login-btn:active,
.register-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 确保Element Plus默认样式不影响 */
.el-button.login-btn,
.el-button.register-btn {
  --el-button-bg-color: transparent;
  --el-button-border-color: transparent;
  --el-button-text-color: white;
  --el-button-hover-bg-color: transparent;
  --el-button-hover-border-color: transparent;
  --el-button-hover-text-color: white;
  --el-button-active-bg-color: transparent;
  --el-button-active-border-color: transparent;
  --el-button-active-text-color: white;
  --el-button-disabled-bg-color: rgba(0, 0, 0, 0.1);
  --el-button-disabled-border-color: rgba(0, 0, 0, 0.1);
  --el-button-disabled-text-color: rgba(255, 255, 255, 0.5);
  padding: 0;
  margin: 0;
}

.register-link {
  color: #667eea;
  font-weight: 500;
  margin-left: 5px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-box {
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
  
  .login-form-section {
    padding: 40px 30px;
    /* 移动端底部圆角 */
    border-radius: 0 0 20px 20px;
    /* 调整顺序到上方 */
    grid-row: 1;
    min-height: 400px;
  }
  
  .hospital-name {
    font-size: 24px;
  }
  
  .login-title {
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



