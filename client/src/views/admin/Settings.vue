<template>
  <div class="settings-container">
    <h1>系统设置</h1>
    
    <el-card class="settings-card">
      <template #header>
        <div class="card-header">
          <h2>医院信息设置</h2>
        </div>
      </template>
      
      <el-form
        :model="hospitalForm"
        :rules="hospitalRules"
        ref="hospitalFormRef"
        label-width="100px"
        class="settings-form"
      >
        <el-form-item label="医院名称" prop="name">
          <el-input
            v-model="hospitalForm.name"
            placeholder="请输入医院名称"
            size="large"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="医院描述" prop="description">
          <el-input
            v-model="hospitalForm.description"
            placeholder="请输入医院描述"
            type="textarea"
            :rows="3"
            size="large"
          ></el-input>
        </el-form-item>
        
        <el-form-item label="医院图标" prop="icon">
          <el-select
            v-model="hospitalForm.icon"
            placeholder="请选择医院图标"
            size="large"
          >
            <el-option label="用户" value="User"></el-option>
            <el-option label="医院" value="OfficeBuilding"></el-option>
            <el-option label="健康" value="CircleCheck"></el-option>
            <el-option label="星星" value="Star"></el-option>
            <el-option label="文档" value="Document"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            @click="handleSave"
            :loading="loading"
            size="large"
          >
            保存设置
          </el-button>
          <el-button
            type="default"
            @click="handleReset"
            size="large"
            style="margin-left: 10px;"
          >
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'

const hospitalFormRef = ref<FormInstance>()
const loading = ref(false)

// 医院信息表单
const hospitalForm = reactive({
  name: '衡南县第四人民医院',
  description: '致力于为医疗人员提供高质量的三基学习服务',
  icon: 'User'
})

// 表单验证规则
const hospitalRules = reactive<FormRules>({
  name: [
    { required: true, message: '请输入医院名称', trigger: 'blur' },
    { min: 2, max: 50, message: '医院名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入医院描述', trigger: 'blur' },
    { min: 10, max: 200, message: '医院描述长度在 10 到 200 个字符', trigger: 'blur' }
  ],
  icon: [
    { required: true, message: '请选择医院图标', trigger: 'change' }
  ]
})

// 保存设置
const handleSave = async () => {
  if (!hospitalFormRef.value) return
  
  try {
    await hospitalFormRef.value.validate()
    loading.value = true
    
    // 模拟保存到本地存储
    localStorage.setItem('hospitalInfo', JSON.stringify(hospitalForm))
    
    setTimeout(() => {
      loading.value = false
      ElMessage.success('医院信息保存成功')
    }, 1000)
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 重置表单
const handleReset = () => {
  hospitalFormRef.value?.resetFields()
}

// 从本地存储加载医院信息
onMounted(() => {
  const savedHospitalInfo = localStorage.getItem('hospitalInfo')
  if (savedHospitalInfo) {
    const parsedInfo = JSON.parse(savedHospitalInfo)
    Object.assign(hospitalForm, parsedInfo)
  }
})
</script>

<style scoped>
.settings-container {
  padding: 20px 0;
}

.settings-card {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
}

.settings-form {
  margin-top: 20px;
}

.el-form-item {
  margin-bottom: 25px;
}
</style>