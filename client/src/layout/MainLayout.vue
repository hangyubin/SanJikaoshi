<template>
  <div class="app-container">
    <el-container class="main-container">
      <!-- 侧边栏 -->
      <el-aside :width="isSidebarCollapsed ? '64px' : '200px'" class="sidebar" :class="{ 'collapsed': isSidebarCollapsed }">
        <div class="logo" :class="{ 'collapsed': isSidebarCollapsed }">
          <img src="/logo.svg" alt="Logo" class="logo-img" />
          <h2 v-if="!isSidebarCollapsed">智能考试系统</h2>
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
          <el-sub-menu v-if="isAdmin" index="admin">
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
                <span>题库管理</span>
              </template>
            </el-menu-item>

            <el-menu-item index="/dashboard/learning-resource-management">
              <el-icon><FolderOpened /></el-icon>
              <template #title>
                <span>资源管理</span>
              </template>
            </el-menu-item>
            <el-menu-item index="/dashboard/task-management">
              <el-icon><Timer /></el-icon>
              <template #title>
                <span>任务管理</span>
              </template>
            </el-menu-item>
            <!-- 只有系统管理员可以访问系统设置 -->
            <el-menu-item index="/dashboard/settings" v-if="isSystemAdmin">
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
                  <el-dropdown-item @click="handleChangePassword">修改密码</el-dropdown-item>
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
  WarningFilled, Star, FolderOpened
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
const userRoles = ref<string[]>([])

// 计算属性：是否为管理员
const isAdmin = computed(() => {
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    const parsedInfo = JSON.parse(userInfo)
    // admin账号直接是管理员
    if (parsedInfo.username === 'admin') {
      return true
    }
    // 检查角色数组
    const roles = parsedInfo.roles || []
    // 支持字符串和数组两种格式
    if (typeof roles === 'string') {
      return roles.includes('ROLE_ADMIN') || roles.includes('admin') || roles.includes('管理员')
    } else if (Array.isArray(roles)) {
      return roles.some((role: any) => {
        if (typeof role === 'string') {
          return role === 'ROLE_ADMIN' || role === 'admin' || role === '管理员'
        } else {
          return role.code === 'ROLE_ADMIN' || role.name === '管理员'
        }
      })
    }
  }
  return false
})

// 计算属性：是否为系统管理员
const isSystemAdmin = computed(() => {
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    const parsedInfo = JSON.parse(userInfo)
    // admin账号直接是系统管理员
    return parsedInfo.username === 'admin'
  }
  return false
})

// 从localStorage获取用户信息
const fetchUserInfo = () => {
  const savedUserInfo = localStorage.getItem('userInfo')
  if (savedUserInfo) {
    const userInfo = JSON.parse(savedUserInfo)
    // 优先显示真实姓名，否则显示用户名
    if (userInfo.realName) {
      userName.value = userInfo.realName
    } else if (userInfo.username) {
      userName.value = userInfo.username
    }
    if (userInfo.avatar) {
      userAvatar.value = userInfo.avatar
    }
    // 更新角色数组，支持字符串和数组格式
    const roles = userInfo.roles || []
    if (typeof roles === 'string') {
      userRoles.value = [roles]
    } else if (Array.isArray(roles)) {
      userRoles.value = roles.map((role: any) => {
        if (typeof role === 'string') {
          return role
        } else if (role.code) {
          return role.code
        } else if (role.name) {
          return role.name
        }
        return ''
      })
    } else {
      userRoles.value = []
    }
  }
}

// 切换侧边栏
const toggleSidebar = () => {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
}

// 处理个人中心
const handleProfile = () => {
  router.push('/dashboard/profile')
}

// 处理修改密码
const handleChangePassword = () => {
  router.push('/dashboard/change-password')
}

// 处理退出登录
const handleLogout = () => {
  localStorage.removeItem('userInfo')
  router.push('/login')
}

// 初始化用户信息
fetchUserInfo()

// 监听localStorage变化，更新用户信息
window.addEventListener('storage', (e) => {
  // 如果是手动触发的事件或者userInfo发生变化，都更新用户信息
  if (!e.key || e.key === 'userInfo') {
    fetchUserInfo()
  }
})
</script>

<style scoped>
.app-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.main-container {
  height: 100%;
}

/* 侧边栏 */
.sidebar {
  background: linear-gradient(180deg, #667eea 0%, #545c64 100%);
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 2px 0 12px rgba(0, 0, 0, 0.1);
}

.sidebar.collapsed {
  width: 64px !important;
}

/* Logo */
.logo {
  text-align: center;
  padding: 0 20px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  color: white;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo.collapsed {
  padding: 0;
}

.logo-img {
  height: 40px;
  width: auto;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.2));
}

.logo.collapsed .logo-img {
  height: 32px;
}

.logo h2 {
  margin: 0;
  font-size: 18px;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  white-space: nowrap;
  font-weight: bold;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.logo.collapsed h2 {
  display: none;
}

/* 菜单栏 */
:deep(.el-menu-vertical-demo) {
  background: transparent;
  border-right: none;
  transition: all 0.3s ease;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  padding: 0 20px !important;
  height: 50px;
  line-height: 50px;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  border-radius: 0 25px 25px 0;
  margin: 5px 0;
  position: relative;
  overflow: hidden;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.15) !important;
  transform: translateX(5px);
}

:deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.2) !important;
  color: #ffd04b !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

:deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 4px;
  background: #ffd04b;
  border-radius: 0 2px 2px 0;
}

:deep(.el-menu-item .el-icon),
:deep(.el-sub-menu__title .el-icon) {
  font-size: 18px;
  margin-right: 10px;
  transition: all 0.3s ease;
}

:deep(.el-sub-menu .el-menu-item) {
  padding-left: 50px !important;
  height: 45px;
  line-height: 45px;
  font-size: 14px;
}

:deep(.el-sub-menu .el-menu-item:hover) {
  transform: translateX(8px);
}

:deep(.el-menu--collapse .el-menu-item),
:deep(.el-menu--collapse .el-sub-menu__title) {
  border-radius: 50%;
  margin: 10px 12px;
  padding: 0 !important;
  width: 40px;
  height: 40px;
  line-height: 40px;
  text-align: center;
}

:deep(.el-menu--collapse .el-menu-item:hover),
:deep(.el-menu--collapse .el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.2) !important;
  transform: scale(1.1);
}

:deep(.el-menu--collapse .el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.3) !important;
}

:deep(.el-menu--collapse .el-menu-item.is-active::before) {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: transparent;
  border: 2px solid #ffd04b;
  opacity: 0.6;
}

:deep(.el-menu--collapse .el-menu-item .el-icon),
:deep(.el-menu--collapse .el-sub-menu__title .el-icon) {
  margin-right: 0;
  font-size: 18px;
}

/* 顶部导航栏 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  padding: 0 30px;
  height: 60px;
  transition: all 0.3s ease;
  position: relative;
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.toggle-sidebar {
  cursor: pointer;
  font-size: 24px;
  color: #606266;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  padding: 8px;
  border-radius: 8px;
  background: #f5f7fa;
}

.toggle-sidebar:hover {
  color: #667eea;
  background: #ecf5ff;
  transform: rotate(180deg);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 25px;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
}

.user-info:hover {
  background: #ecf5ff;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
  transform: translateY(-2px);
}

.user-info span {
  margin-left: 12px;
  color: #303133;
  font-weight: 500;
  transition: color 0.3s ease;
  white-space: nowrap;
}

.user-info:hover span {
  color: #667eea;
}

:deep(.el-avatar) {
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  border: 2px solid transparent;
}

.user-info:hover :deep(.el-avatar) {
  transform: scale(1.1);
  border-color: #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

/* 下拉菜单 */
:deep(.el-dropdown-menu) {
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  border: none;
  overflow: hidden;
  padding: 10px 0;
}

:deep(.el-dropdown-item) {
  transition: all 0.3s ease;
  padding: 12px 24px;
  font-weight: 500;
  color: #303133;
}

:deep(.el-dropdown-item:hover) {
  background: #ecf5ff;
  color: #667eea;
  transform: translateX(5px);
}

:deep(.el-dropdown-item:not(:last-child)) {
  border-bottom: 1px solid #f5f7fa;
}

/* 主内容区 */
.main-content {
  padding: 20px;
  background: #f5f7fa;
  overflow: auto;
  transition: all 0.3s ease;
}

/* 滚动条样式 */
.main-content::-webkit-scrollbar,
:deep(.el-table__body-wrapper::-webkit-scrollbar) {
  width: 8px;
  height: 8px;
}

.main-content::-webkit-scrollbar-track,
:deep(.el-table__body-wrapper::-webkit-scrollbar-track) {
  background: #f1f1f1;
  border-radius: 4px;
}

.main-content::-webkit-scrollbar-thumb,
:deep(.el-table__body-wrapper::-webkit-scrollbar-thumb) {
  background: #c1c1c1;
  border-radius: 4px;
  transition: background 0.3s ease;
}

.main-content::-webkit-scrollbar-thumb:hover,
:deep(.el-table__body-wrapper::-webkit-scrollbar-thumb:hover) {
  background: #a1a1a1;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .main-content {
    padding: 15px;
  }
  
  .header {
    padding: 0 20px;
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
    transition: left 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  }
  
  .sidebar.collapsed {
    left: -200px;
    width: 200px !important;
  }
  
  .header {
    padding: 0 15px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  .header-left {
    gap: 15px;
  }
  
  .toggle-sidebar {
    font-size: 20px;
    padding: 6px;
  }
  
  .main-content {
    padding: 10px;
    margin-left: 0;
  }
  
  /* 移动端表格处理 */
  .main-content :deep(.el-table) {
    overflow-x: auto;
    border-radius: 8px;
  }
  
  /* 移动端卡片处理 */
  .main-content :deep(.el-card) {
    margin-bottom: 15px;
    border-radius: 8px;
  }
  
  /* 移动端按钮组处理 */
  .main-content :deep(.el-button-group) {
    flex-wrap: wrap;
  }
  
  .user-info span {
    display: none;
  }
  
  .header-right {
    gap: 10px;
  }
}

@media (max-width: 480px) {
  .header {
    padding: 0 10px;
  }
  
  .header-left {
    gap: 10px;
  }
  
  .main-content {
    padding: 5px;
  }
  
  :deep(.el-avatar) {
    size: 28px;
  }
}
</style>



