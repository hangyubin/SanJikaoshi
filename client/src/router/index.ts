import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../layout/MainLayout.vue'),
    children: [
      {
        path: '',
        component: () => import('../views/Dashboard.vue')
      },
      // 考生功能
      {
        path: 'practice',
        name: 'Practice',
        component: () => import('../views/student/Practice.vue')
      },
      {
        path: 'exam-center',
        name: 'ExamCenter',
        component: () => import('../views/student/ExamCenter.vue')
      },
      {
        path: 'exam-detail/:id',
        name: 'ExamDetail',
        component: () => import('../views/student/ExamDetail.vue')
      },
      {
        path: 'error-notebook',
        name: 'ErrorNotebook',
        component: () => import('../views/student/ErrorNotebook.vue')
      },
      // 管理员功能
      {
        path: 'user-management',
        name: 'UserManagement',
        component: () => import('../views/admin/UserManagement.vue')
      },
      {        path: 'student-list',        name: 'StudentList',        component: () => import('../views/admin/StudentList.vue')      },      {        path: 'admin-list',        name: 'AdminList',        component: () => import('../views/admin/AdminList.vue')      },
      {        path: 'department-management',        name: 'DepartmentManagement',        component: () => import('../views/admin/DepartmentManagement.vue')      },
      {        path: 'paper-management',        name: 'PaperManagement',        component: () => import('../views/admin/PaperManagement.vue')      },

      {        path: 'learning-resource-management',        name: 'LearningResourceManagement',        component: () => import('../views/admin/LearningResourceManagement.vue')      },
      {        path: 'question-management',        name: 'QuestionManagement',        component: () => import('../views/admin/QuestionManagement.vue')      },
      {        path: 'task-management',        name: 'TaskManagement',        component: () => import('../views/admin/TaskManagement.vue')      },      {        path: 'settings',        name: 'Settings',        component: () => import('../views/admin/Settings.vue')      },
      // 任务管理相关
      {        path: 'task-create',        name: 'TaskCreate',        component: () => import('../views/admin/TaskCreate.vue')      },
      {        path: 'task-list',        name: 'TaskList',        component: () => import('../views/admin/TaskList.vue')      },
      // 个人中心和修改密码
      {        path: 'profile',        name: 'Profile',        component: () => import('../views/admin/Profile.vue')      },
      {        path: 'change-password',        name: 'ChangePassword',        component: () => import('../views/admin/ChangePassword.vue')      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router