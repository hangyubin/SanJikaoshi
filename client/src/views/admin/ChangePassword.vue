<template>
  <div class="change-password-container">
    <el-card class="change-password-card">
      <template #header>
        <div class="card-header">
          <h2>修改密码</h2>
        </div>
      </template>
      
      <div class="change-password-content">
        <el-form :model="passwordForm" :rules="rules" ref="formRef" label-width="120px" class="change-password-form">
          <el-form-item label="原密码" prop="oldPassword">
            <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" show-password></el-input>
          </el-form-item>
          
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password></el-input>
            <div class="password-strength" v-if="passwordForm.newPassword">
              <span class="strength-label">密码强度：</span>
              <el-progress 
                :percentage="passwordStrength" 
                :stroke-color="passwordStrengthColor"
                :show-text="false"
                :height="8"
                class="strength-progress"
              ></el-progress>
              <span class="strength-text">{{ passwordStrengthText }}</span>
            </div>
          </el-form-item>
          
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password></el-input>
          </el-form-item>
          
          <div class="form-actions">
            <el-button type="primary" @click="handleSubmit">确认修改</el-button>
            <el-button @click="handleReset">重置</el-button>
          </div>
        </el-form>
        
        <!-- 密码规则说明 -->
        <div class="password-rules">
          <h3>密码规则</h3>
          <ul>
            <li>密码长度至少为8个字符</li>
            <li>包含至少一个大写字母</li>
            <li>包含至少一个小写字母</li>
            <li>包含至少一个数字</li>
            <li>包含至少一个特殊字符（如 !@#$%^&*）</li>
          </ul>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

// 表单引用
const formRef = ref<FormInstance>()

// 密码表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码强度
const passwordStrength = ref(0)
const passwordStrengthText = ref('')
const passwordStrengthColor = ref('#67c23a')

// 验证确认密码
const validateConfirmPassword = (_rule: any, value: string, callback: any) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const rules = reactive<FormRules>({
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 8, message: '密码长度不能少于8个字符', trigger: 'blur' },
    { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/, 
      message: '密码必须包含大小写字母、数字和特殊字符', 
      trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
})

// 计算密码强度
const calculatePasswordStrength = (password: string) => {
  let strength = 0
  let text = '弱'
  let color = '#f56c6c'
  
  // 长度检查
  if (password.length >= 8) strength += 25
  if (password.length >= 12) strength += 25
  
  // 包含小写字母
  if (/[a-z]/.test(password)) strength += 15
  
  // 包含大写字母
  if (/[A-Z]/.test(password)) strength += 15
  
  // 包含数字
  if (/\d/.test(password)) strength += 10
  
  // 包含特殊字符
  if (/[@$!%*?&]/.test(password)) strength += 10
  
  // 更新强度文本和颜色
  if (strength < 40) {
    text = '弱'
    color = '#f56c6c'
  } else if (strength < 70) {
    text = '中'
    color = '#e6a23c'
  } else {
    text = '强'
    color = '#67c23a'
  }
  
  passwordStrength.value = strength
  passwordStrengthText.value = text
  passwordStrengthColor.value = color
}

// 监听密码变化，计算强度
watch(
  () => passwordForm.newPassword,
  (newValue) => {
    calculatePasswordStrength(newValue)
  }
)

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 模拟修改密码请求
    // 实际使用时应替换为真实API调用
    // const response = await axios.post('/user/change-password', passwordForm)
    // if (response.code === 200) {
    //   ElMessage.success('密码修改成功')
    //   passwordForm.oldPassword = ''
    //   passwordForm.newPassword = ''
    //   passwordForm.confirmPassword = ''
    // } else {
    //   ElMessage.error('密码修改失败：' + response.data.message)
    // }
    
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error: any) {
    console.error('表单验证失败:', error)
    if (error.message) {
      ElMessage.error(error.message)
    }
  }
}

// 重置表单
const handleReset = () => {
  passwordForm.oldPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordStrength.value = 0
  passwordStrengthText.value = ''
  
  if (formRef.value) {
    formRef.value.resetFields()
  }
}
</script>

<style scoped>
.change-password-container {
  padding: 20px 0;
}

.change-password-card {
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

.change-password-content {
  padding: 20px 0;
}

.change-password-form {
  margin-top: 20px;
  max-width: 500px;
  margin: 0 auto;
}

.password-strength {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.strength-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

.strength-progress {
  flex: 1;
}

.strength-text {
  font-size: 14px;
  font-weight: bold;
  white-space: nowrap;
}

.form-actions {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  gap: 10px;
}

/* 密码规则说明 */
.password-rules {
  margin-top: 30px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
  max-width: 500px;
  margin: 30px auto 0;
}

.password-rules h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.password-rules ul {
  margin: 0;
  padding-left: 20px;
  list-style-type: disc;
  color: #606266;
  font-size: 14px;
}

.password-rules li {
  margin-bottom: 8px;
  line-height: 1.5;
}

.password-rules li:last-child {
  margin-bottom: 0;
}
</style>



