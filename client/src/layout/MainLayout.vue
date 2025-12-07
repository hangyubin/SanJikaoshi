<template>
  <div class="app-container">
    <el-container class="main-container">
      <!-- 侧边栏 -->
      <el-aside :width="isSidebarCollapsed ? '64px' : '200px'" class="sidebar" :class="{ 'collapsed': isSidebarCollapsed }">
        <div class="logo" :class="{ 'collapsed': isSidebarCollapsed }">
          <h2 v-if="!isSidebarCollapsed">医疗卫生系统三基考试培训</h2>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="el-menu-vertical-demo"
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b"
          router
          :collapse="isSidebarCollapsed"
        >
          <el-menu-item index="/dashboard">
            <el-icon><House /></el-icon>
            <template #title>
              <span>首页</span>
            </template>
          </el-menu-item>
          
          <!-- 学员功能 -->
          <el-sub-menu index="student">
            <template #title>
              <el-icon><User /></el-icon>
              <span>学员功能</span>
            </template>
            <el-menu-item index="/dashboard/practice">
              <el-icon><Star /></el-icon>
              <template #title>
                <span>练习中心</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/dashboard/exam-center">
              <el-icon><Document /></el-icon>
              <template #title>
                <span>考试中心</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/dashboard/error-notebook">
              <el-icon><WarningFilled /></el-icon>
              <template #title>
                <span>错题集</span>
              </template>
            </el-menu-item>
          </el-sub-menu>
          
          <!-- 管理员功能 -->
          <el-sub-menu index="admin">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>管理员功能</span>
            </template>
            <el-menu-item index="/dashboard/user-management">
              <el-icon><User /></el-icon>
              <template #title>
                <span>用户管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/dashboard/paper-management">
              <el-icon><DocumentCopy /></el-icon>
              <template #title>
                <span>三基题库管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/dashboard/task-management">
              <el-icon><Timer /></el-icon>
              <template #title>
                <span>考试任务管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/dashboard/settings">
              <el-icon><Setting /></el-icon>
              <template #title>
                <span>系统设置</span>
              </template>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      
      <el-container>
        <!-- 顶部导航栏 -->
        <el-header class="header">
          <div class="header-left">
            <el-icon class="toggle-sidebar" @click="toggleSidebar"><Menu /></el-icon>
          </div>
          <div class="header-right">
            <el-dropdown>
              <span class="user-info">
                <el-avatar :size="32" :src="userAvatar"></el-avatar>
                <span>{{ userName }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleProfile">个人中心</el-dropdown-item>
                  <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <!-- 主内容区 -->
        <el-main class="main-content">
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { 
  House, Setting, User, Menu, 
  DocumentCopy, Document, Timer, 
  WarningFilled, Star
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 侧边栏状态
const isSidebarCollapsed = ref(false)

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

// 用户信息
const userName = ref('管理员')
const userAvatar = ref('')

// 切换侧边栏
const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
}

// 处理个人中心
const handleProfile = () => {
  console.log('个人中心')
}

// 处理退出登录
const handleLogout = () => {
  router.push('/login')
}
</script>

<style scoped>
.app-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-container {
  height: 100%;
}

.sidebar {
  background-color: #545c64;
  overflow: hidden;
  transition: width 0.3s;
}

.sidebar.collapsed {
  width: 64px !important;
}

.logo {
  text-align: center;
  padding: 20px 0;
  background-color: #409eff;
  color: white;
  transition: all 0.3s;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo.collapsed {
  padding: 0;
}

.logo h2 {
  margin: 0;
  font-size: 18px;
  transition: all 0.3s;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
  height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
}

.toggle-sidebar {
  cursor: pointer;
  font-size: 20px;
  margin-right: 20px;
  color: #606266;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-info span {
  margin-left: 10px;
  color: #606266;
}

.main-content {
  padding: 20px;
  background-color: #f5f7fa;
  overflow: auto;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .main-content {
    padding: 15px;
  }
}

@media (max-width: 768px) {
  /* 移动端样式 */
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    height: 100vh;
    z-index: 100;
    transition: left 0.3s;
  }
  
  .sidebar.collapsed {
    left: -200px;
    width: 200px !important;
  }
  
  .header {
    padding: 0 15px;
  }
  
  .main-content {
    padding: 10px;
    margin-left: 0;
  }
  
  /* 移动端表格处理 */
  .main-content :deep(.el-table) {
    overflow-x: auto;
  }
  
  /* 移动端卡片处理 */
  .main-content :deep(.el-card) {
    margin-bottom: 15px;
  }
  
  /* 移动端按钮组处理 */
  .main-content :deep(.el-button-group) {
    flex-wrap: wrap;
  }
}

@media (max-width: 480px) {
  .header {
    padding: 0 10px;
  }
  
  .user-info span {
    display: none;
  }
  
  .main-content {
    padding: 5px;
  }
}
</style>