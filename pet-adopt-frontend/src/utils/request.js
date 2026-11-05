import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, clearAuth } from './auth'

/**
 * axios 统一封装
 * - baseURL 使用 /api，开发环境由 vite.config.js 的 proxy 转发到 http://localhost:8080
 * - 后端 HTTP 状态码始终是 200（401/403 也返回 200），因此按业务 code 判断
 */
const request = axios.create({
  // 需要独立部署到其它域名时，在 .env 里配置 VITE_API_BASE；默认走同源 /api
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  timeout: 15000,
  withCredentials: false
})

request.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

/** 401 时跳登录页（用整页跳转，确保 store 与 localStorage 状态一致） */
function redirectToLogin() {
  const current = window.location.pathname + window.location.search
  const isAdminArea = window.location.pathname.startsWith('/admin')
  const loginPath = isAdminArea ? '/admin/login' : '/login'
  if (window.location.pathname === loginPath) return
  window.location.href = `${loginPath}?redirect=${encodeURIComponent(current)}`
}

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (!res || typeof res.code === 'undefined') {
      ElMessage.error('接口返回格式异常')
      return Promise.reject(new Error('接口返回格式异常'))
    }

    if (res.code === 200) {
      return res.data
    }

    if (res.code === 401) {
      clearAuth()
      ElMessage.error(res.msg || '登录已失效，请重新登录')
      redirectToLogin()
      return Promise.reject(new Error(res.msg || '未登录'))
    }

    if (res.code === 403) {
      ElMessage.error(res.msg || '权限不足')
      return Promise.reject(new Error(res.msg || '权限不足'))
    }

    ElMessage.error(res.msg || '操作失败')
    return Promise.reject(new Error(res.msg || '操作失败'))
  },
  (error) => {
    const msg = error.response
      ? `请求失败（HTTP ${error.response.status}），请稍后重试`
      : '网络异常，请确认后端服务（http://localhost:8080）已启动'
    ElMessage.error(msg)
    return Promise.reject(error)
  }
)

export default request
