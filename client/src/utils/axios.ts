import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

// 创建axios实例
const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // 设置默认Content-Type
    if (!config.headers['Content-Type']) {
      config.headers['Content-Type'] = 'application/json'
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    const res = response.data
    
    // 根据实际业务调整
    if (res.code !== 200) {
      ElMessage({
        message: res.message || 'Error',
        type: 'error',
        duration: 3 * 1000
      })
      
      // 401: 未登录或token过期
      if (res.code === 401) {
        const router = useRouter()
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  (error) => {
    // 只在有response时显示错误消息，避免开发环境下显示不必要的错误
    if (error.response) {
      ElMessage({
        message: error.response.data?.message || '请求失败',
        type: 'error',
        duration: 3 * 1000
      })
    }
    return Promise.reject(error)
  }
)

export default service