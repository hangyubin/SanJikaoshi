<template>
  <div class="dashboard">
    <!-- 欢迎信息 -->
    <div class="welcome-section">
      <div class="welcome-info">
        <h1>欢迎回来，{{ userName }}</h1>
        <p class="welcome-subtitle">这里是您的智能考试系统工作台</p>
        <el-tag :type="userRole === 'admin' ? 'primary' : 'success'" size="large" class="role-tag">
          {{ userRole === 'admin' ? '管理员' : '学员' }}
        </el-tag>
      </div>
      <div class="welcome-stats">
        <div class="stat-item">
          <span class="stat-label">今日学习时长</span>
          <span class="stat-value">{{ todayStudyTime }}分钟</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">本周完成题目</span>
          <span class="stat-value">{{ thisWeekCompletedQuestions }}题</span>
        </div>
      </div>
    </div>

    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :span="6">
        <el-card class="stat-card" hoverable>
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">总用户数</p>
              <h3 class="stat-value">{{ userCount }}</h3>
            </div>
            <div class="stat-icon user-icon">
              <el-icon><User /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" hoverable>
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">科目数量</p>
              <h3 class="stat-value">{{ subjectCount }}</h3>
            </div>
            <div class="stat-icon subject-icon">
              <el-icon><Document /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" hoverable>
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">题目数量</p>
              <h3 class="stat-value">{{ questionCount }}</h3>
            </div>
            <div class="stat-icon question-icon">
              <el-icon><EditPen /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" hoverable>
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">试卷数量</p>
              <h3 class="stat-value">{{ paperCount }}</h3>
            </div>
            <div class="stat-icon paper-icon">
              <el-icon><DocumentCopy /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 快捷操作区 -->
    <div class="quick-actions" v-if="userRole === 'admin'">
      <h3>快捷操作</h3>
      <el-row :gutter="20">
        <el-col :span="4" v-for="action in adminQuickActions" :key="action.id">
          <el-card class="action-card" hoverable @click="handleQuickAction(action)">
            <div class="action-content">
              <el-icon :size="32" class="action-icon"><component :is="action.icon" /></el-icon>
              <p class="action-text">{{ action.text }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <!-- 学员快捷操作区 -->
    <div class="quick-actions" v-else>
      <h3>开始学习</h3>
      <el-row :gutter="20">
        <el-col :span="4" v-for="action in studentQuickActions" :key="action.id">
          <el-card class="action-card" hoverable @click="handleQuickAction(action)">
            <div class="action-content">
              <el-icon :size="32" class="action-icon"><component :is="action.icon" /></el-icon>
              <p class="action-text">{{ action.text }}</p>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <!-- 最近考试列表 -->
    <el-card class="exam-list-card" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <h3>最近考试安排</h3>
          <el-button type="primary" size="small">查看全部</el-button>
        </div>
      </template>
      
      <el-table :data="recentExams" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="考试名称" min-width="180" show-overflow-tooltip>
          <template #header>
            <span class="table-header">考试名称</span>
          </template>
        </el-table-column>
        <el-table-column prop="subject" label="科目" width="120">
          <template #header>
            <span class="table-header">科目</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalScore" label="总分" width="80">
          <template #header>
            <span class="table-header">总分</span>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="时长(分钟)" width="100">
          <template #header>
            <span class="table-header">时长</span>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #header>
            <span class="table-header">开始时间</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #header>
            <span class="table-header">状态</span>
          </template>
          <template #default="scope">
            <el-tag :type="scope.row.status === '进行中' ? 'success' : scope.row.status === '未开始' ? 'info' : 'danger'" size="large">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #header>
            <span class="table-header">操作</span>
          </template>
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewExamDetail(scope.row)">
              <el-icon><Document /></el-icon>
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 最新学习资源 -->
    <el-card class="resources-card" style="margin-top: 30px;">
      <template #header>
        <div class="card-header">
          <h3>最新学习资源</h3>
          <el-button type="primary" size="small" @click="router.push('/dashboard/learning-resource-management')">查看全部</el-button>
        </div>
      </template>
      
      <div class="resources-list" v-if="latestResources.length > 0" v-loading="resourcesLoading">
        <div v-for="resource in latestResources" :key="resource.id" class="resource-item">
          <div class="resource-info">
            <div class="resource-title">
              <el-icon class="resource-icon"><Link /></el-icon>
              <span class="title-text">{{ resource.title }}</span>
              <el-tag :type="resource.type === 1 ? 'info' : resource.type === 2 ? 'success' : resource.type === 3 ? 'warning' : 'primary'">
                {{ fileTypeMap[resource.fileType as keyof typeof fileTypeMap]?.label || '未知类型' }}
              </el-tag>
            </div>
            <div class="resource-meta">
              <span class="meta-item">{{ resource.subject?.name || '学习资料' }}</span>
              <span class="meta-item">{{ formatFileSize(resource.fileSize) }}</span>
              <span class="meta-item">{{ new Date(resource.createTime).toLocaleString() }}</span>
            </div>
            <div class="resource-desc">{{ resource.description || '暂无描述' }}</div>
          </div>
          <div class="resource-actions">
            <el-button 
              type="primary" 
              size="small" 
              @click="handleResourcePreview(resource)"
              :icon="resource.type === 2 || resource.type === 3 ? VideoPlay : Download"
            >
              {{ resource.type === 2 || resource.type === 3 ? '预览' : '下载' }}
            </el-button>
          </div>
        </div>
      </div>
      <div v-else class="no-resources" v-loading="resourcesLoading">
        <el-empty description="暂无学习资源" />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  User, Document, EditPen, DocumentCopy, Star, 
  WarningFilled, OfficeBuilding, 
  FolderOpened, Link, VideoPlay, Download 
} from '@element-plus/icons-vue'
import axios from '@/utils/axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

// 用户信息
const userName = ref('')
const userRole = ref('student') // 'admin' 或 'student'

// 统计数据
const userCount = ref(0)
const subjectCount = ref(0)
const questionCount = ref(0)
const paperCount = ref(0)

// 学习统计数据
const todayStudyTime = ref(0)
const thisWeekCompletedQuestions = ref(0)

// 加载状态
const loading = ref(false)
const resourcesLoading = ref(false)

// 最近考试数据
const recentExams = ref<any[]>([])

// 最新资源列表
const latestResources = ref<any[]>([])

// 支持的文件类型映射
const fileTypeMap = {
  'pdf': { label: 'PDF文档', type: 1 },
  'doc': { label: 'Word文档', type: 1 },
  'docx': { label: 'Word文档', type: 1 },
  'xls': { label: 'Excel表格', type: 1 },
  'xlsx': { label: 'Excel表格', type: 1 },
  'mp3': { label: '音频文件', type: 3 },
  'mp4': { label: '视频文件', type: 2 },
  'rm': { label: '视频文件', type: 2 },
  'rmvb': { label: '视频文件', type: 2 }
}

// 管理员快捷操作
const adminQuickActions = ref([
  {
    id: 1,
    text: '添加用户',
    icon: User,
    path: '/dashboard/user-management'
  },
  {
    id: 2,
    text: '管理题目',
    icon: EditPen,
    path: '/dashboard/paper-management'
  },
  { 
    id: 3, 
    text: '创建考试', 
    icon: DocumentCopy, 
    path: '/dashboard/task-create' 
  },
  {
    id: 4,
    text: '管理科室',
    icon: OfficeBuilding,
    path: '/dashboard/department-management'
  },

  {
    id: 6,
    text: '资源管理',
    icon: FolderOpened,
    path: '/dashboard/learning-resource-management'
  }
])

// 学员快捷操作
const studentQuickActions = ref([
  {
    id: 1,
    text: '开始练习',
    icon: Star,
    path: '/dashboard/practice'
  },
  {
    id: 2,
    text: '考试中心',
    icon: Document,
    path: '/dashboard/exam-center'
  },
  {
    id: 3,
    text: '错题集',
    icon: WarningFilled,
    path: '/dashboard/error-notebook'
  },
  {
    id: 4,
    text: '学习资源',
    icon: FolderOpened,
    path: '/dashboard/learning-resource-management'
  }
])

// 处理快捷操作
const handleQuickAction = (action: any) => {
  router.push(action.path)
}

// 查看考试详情
const viewExamDetail = (exam: any) => {
  router.push(`/dashboard/exam-detail/${exam.id}`)
}

// 从localStorage获取用户信息
const fetchUserInfo = () => {
  const savedUserInfo = localStorage.getItem('userInfo')
  if (savedUserInfo) {
    const userInfo = JSON.parse(savedUserInfo)
    if (userInfo.username) {
      userName.value = userInfo.username
    }
    // 这里可以根据实际角色设置userRole
    // userRole.value = userInfo.role === 'ROLE_ADMIN' ? 'admin' : 'student'
  }
}

// 获取最新资源
const fetchLatestResources = () => {
  resourcesLoading.value = true
  axios.get('/learning-resources', {
    params: { pageNum: 1, pageSize: 5 }
  })
  .then(response => {
    if (response.code === 200) {
      latestResources.value = response.data || []
    }
  })
  .catch(error => {
    console.error('获取最新资源失败:', error)
    // 清除模拟数据，只保留真实API调用
    latestResources.value = []
  })
  .finally(() => {
    resourcesLoading.value = false
  })
}

// 文件大小格式化
const formatFileSize = (bytes: number) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 处理资源预览
const handleResourcePreview = (resource: any) => {
  // 根据资源类型打开预览
  if (resource.url) {
    // 获取文件扩展名
    const ext = resource.fileType?.toLowerCase() || ''
    
    // PDF文件在线预览
    if (ext === 'pdf') {
      window.open(resource.url, '_blank')
    }
    // 视频文件在线播放
    else if (['mp4', 'rm', 'rmvb'].includes(ext)) {
      ElMessageBox({
        title: '视频预览',
        message: `<video src="${resource.url}" controls style="width: 100%; max-height: 500px;"></video>`,
        dangerouslyUseHTMLString: true,
        confirmButtonText: '关闭',
        customClass: 'video-preview-dialog'
      })
    }
    // 音频文件在线播放
    else if (ext === 'mp3') {
      ElMessageBox({
        title: '音频预览',
        message: `<audio src="${resource.url}" controls style="width: 100%;"></audio>`,
        dangerouslyUseHTMLString: true,
        confirmButtonText: '关闭',
        customClass: 'audio-preview-dialog'
      })
    }
    // 文档文件下载
    else if (['doc', 'docx', 'xls', 'xlsx'].includes(ext)) {
      // 直接下载
      const a = document.createElement('a')
      a.href = resource.url
      a.download = resource.title || '学习资源'
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      ElMessage.success('文件下载中...')
    }
    // 其他类型，打开新窗口
    else {
      window.open(resource.url, '_blank')
    }
  } else {
    ElMessage.warning('资源链接为空，无法预览')
  }
}

onMounted(() => {
  fetchUserInfo()
  fetchLatestResources()
  console.log('Dashboard mounted')
})
</script>

<style scoped>
.dashboard {
  padding: 20px 0;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

/* 欢迎信息区域 */
.welcome-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 40px;
  color: white;
  margin-bottom: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.welcome-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #ffffff, transparent);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.welcome-section:hover {
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.4);
  transform: translateY(-2px);
}

.welcome-info {
  flex: 1;
  z-index: 1;
}

.welcome-info h1 {
  margin: 0 0 15px 0;
  font-size: 36px;
  font-weight: bold;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  animation: fadeInUp 0.6s ease;
}

.welcome-subtitle {
  font-size: 18px;
  opacity: 0.95;
  margin-bottom: 20px;
  animation: fadeInUp 0.6s ease 0.2s both;
}

.role-tag {
  margin-top: 10px;
  font-size: 16px;
  animation: fadeInUp 0.6s ease 0.4s both;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.welcome-stats {
  display: flex;
  gap: 30px;
  background: rgba(255, 255, 255, 0.15);
  padding: 25px;
  border-radius: 12px;
  backdrop-filter: blur(15px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  animation: fadeInUp 0.6s ease 0.6s both;
}

.stat-item {
  text-align: center;
  transition: all 0.3s ease;
  padding: 10px;
  border-radius: 8px;
}

.stat-item:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

.stat-item .stat-label {
  font-size: 14px;
  opacity: 0.85;
  margin-bottom: 8px;
  display: block;
  font-weight: 500;
}

.stat-item .stat-value {
  font-size: 28px;
  font-weight: bold;
  display: block;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  animation: countUp 1s ease-out;
}

@keyframes countUp {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 统计卡片 */
.stat-card {
  height: 140px;
  margin-bottom: 20px;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  background: white;
  overflow: hidden;
  position: relative;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.stat-card:hover::before {
  transform: scaleX(1);
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

/* 适配小屏幕设备 */
@media (max-width: 768px) {
  .dashboard {
    padding: 10px 0;
  }
  
  .stat-card {
    margin-bottom: 20px;
    height: 120px;
  }
  
  .welcome-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
    padding: 25px;
  }
  
  .welcome-stats {
    width: 100%;
    justify-content: space-around;
    gap: 15px;
    padding: 15px;
  }
  
  .welcome-info h1 {
    font-size: 28px;
  }
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
  padding: 0 30px;
  position: relative;
  z-index: 1;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
  display: block;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin: 0;
  display: block;
  animation: countUp 1s ease-out;
}

.stat-icon {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 28px;
  color: white;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  overflow: hidden;
}

.stat-icon::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s ease;
}

.stat-card:hover .stat-icon::before {
  left: 100%;
}

.stat-card:hover .stat-icon {
  transform: scale(1.15) rotate(10deg);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.25);
}

.user-icon {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
}

.subject-icon {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.question-icon {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
}

.paper-icon {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
}

/* 快捷操作区域 */
.quick-actions {
  margin: 40px 0 30px;
}

.quick-actions h3 {
  margin: 0 0 25px 0;
  font-size: 22px;
  font-weight: bold;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
}

.quick-actions h3::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 24px;
  background: linear-gradient(180deg, #667eea, #764ba2);
  border-radius: 2px;
}

.action-card {
  height: 130px;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  border: none;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  background: white;
  position: relative;
  overflow: hidden;
}

.action-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(118, 75, 162, 0.05));
  opacity: 0;
  transition: opacity 0.3s ease;
}

.action-card:hover::before {
  opacity: 1;
}

.action-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.2);
}

.action-content {
  text-align: center;
  position: relative;
  z-index: 1;
  transition: transform 0.3s ease;
}

.action-card:hover .action-content {
  transform: translateY(-5px);
}

.action-icon {
  margin-bottom: 12px;
  color: #667eea;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  filter: drop-shadow(0 4px 8px rgba(102, 126, 234, 0.2));
}

.action-card:hover .action-icon {
  transform: scale(1.2) rotate(15deg);
  color: #764ba2;
  filter: drop-shadow(0 6px 12px rgba(102, 126, 234, 0.3));
}

.action-text {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin: 0;
  transition: color 0.3s ease;
}

.action-card:hover .action-text {
  color: #667eea;
}

/* 考试列表 */
.exam-list-card {
  margin-top: 20px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  background: white;
  overflow: hidden;
  transition: all 0.3s ease;
}

.exam-list-card:hover {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 0;
  background: #fafafa;
}

.card-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 10px;
}

.card-header h3::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 20px;
  background: linear-gradient(180deg, #667eea, #764ba2);
  border-radius: 2px;
}

.table-header {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

/* 表格样式优化 */
:deep(.el-table) {
  border-radius: 0 0 12px 12px;
  overflow: hidden;
}

:deep(.el-table__header-wrapper) {
  background-color: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-table__body-wrapper) {
  overflow: auto;
}

:deep(.el-table__row) {
  transition: all 0.3s ease;
  cursor: pointer;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
  transform: translateX(5px);
}

:deep(.el-table__row td) {
  border-bottom: 1px solid #f5f7fa;
}

:deep(.el-table__row:last-child td) {
  border-bottom: none;
}

:deep(.el-button--small) {
  border-radius: 6px;
  padding: 6px 16px;
  transition: all 0.3s ease;
  font-weight: 500;
}

:deep(.el-button--small:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

:deep(.el-tag) {
  border-radius: 6px;
  font-weight: 600;
  padding: 4px 12px;
  font-size: 12px;
  transition: all 0.3s ease;
}

:deep(.el-tag:hover) {
  transform: scale(1.05);
}

/* 滚动条样式 */
:deep(.el-table__body-wrapper::-webkit-scrollbar) {
  width: 8px;
  height: 8px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-track) {
  background: #f1f1f1;
  border-radius: 4px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-thumb) {
  background: #c1c1c1;
  border-radius: 4px;
  transition: background 0.3s ease;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-thumb:hover) {
  background: #a1a1a1;
}

/* 资源列表样式 */
.resources-card {
  margin-top: 30px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  background: white;
  overflow: hidden;
  transition: all 0.3s ease;
}

.resources-card:hover {
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.resources-list {
  padding: 20px 0;
}

.resource-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px 30px;
  border-bottom: 1px solid #f5f7fa;
  transition: all 0.3s ease;
}

.resource-item:last-child {
  border-bottom: none;
}

.resource-item:hover {
  background-color: #fafafa;
  transform: translateX(5px);
}

.resource-info {
  flex: 1;
}

.resource-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.resource-icon {
  font-size: 18px;
  color: #667eea;
}

.title-text {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.resource-meta {
  display: flex;
  gap: 20px;
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.resource-desc {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.resource-actions {
  display: flex;
  gap: 10px;
  align-items: center;
  margin-left: 20px;
}

.no-resources {
  padding: 40px 0;
  text-align: center;
}

.video-preview-dialog {
  max-width: 800px;
}

.audio-preview-dialog {
  max-width: 600px;
}
</style>



