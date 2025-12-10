import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8080',
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
    
    // 添加请求日志
    console.log('请求配置:', {
      url: config.url,
      method: config.method,
      baseURL: config.baseURL,
      headers: config.headers,
      params: config.params,
      data: config.data
    })
    
    return config
  },
  (error) => {
    console.error('请求拦截器错误:', error)
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
        // 使用window.location.href而不是useRouter，因为拦截器中无法访问Vue实例
        window.location.href = '/login'
      }
      
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  (error) => {
    // 只在有response时显示错误信息，避免在开发环境下显示不必要的错误
    if (error.response) {
      console.error('API请求失败:', {
        url: error.config.url,
        status: error.response.status,
        statusText: error.response.statusText,
        data: error.response.data
      })
      
      // 只显示有明确错误消息的错误
      const errorMessage = error.response.data?.message || `请求失败，状态码: ${error.response.status}`
      ElMessage({
        message: errorMessage,
        type: 'error',
        duration: 3 * 1000
      })
    } else {
      // 忽略网络错误或其他无响应的错误，避免影响系统主要功能
      console.warn('API请求警告:', {
        message: error.message,
        url: error.config?.url
      })
      // 不显示错误消息，只在控制台记录
    }
    return Promise.reject(error)
  }
)

export default service