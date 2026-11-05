import { defineStore } from 'pinia'
import {
  getToken,
  getStoredUser,
  setToken,
  setStoredUser,
  clearAuth
} from '@/utils/auth'

/**
 * 用户登录态
 * role 统一存 'user' / 'admin'，方便路由守卫与页面判断
 * 持久化：setLogin / logout 时同步写入 localStorage
 */
export const useUserStore = defineStore('user', {
  state: () => {
    const user = getStoredUser() || {}
    return {
      token: getToken(),
      userId: user.userId || null,
      username: user.username || '',
      name: user.name || '',
      role: user.role || 'user'
    }
  },

  getters: {
    isLogin: (state) => !!state.token,
    isAdmin: (state) => state.role === 'admin',
    displayName: (state) => state.name || state.username || '用户'
  },

  actions: {
    /** 登录成功后调用，参数为后端 LoginVO：{ token, userInfo, role } */
    setLogin(loginVO) {
      const info = loginVO?.userInfo || {}
      this.token = loginVO?.token || ''
      this.userId = info.id ?? null
      this.username = info.username || ''
      this.name = info.name || ''
      this.role = Number(loginVO?.role ?? info.role) === 1 ? 'admin' : 'user'

      setToken(this.token)
      setStoredUser({
        userId: this.userId,
        username: this.username,
        name: this.name,
        role: this.role
      })
    },

    /** 退出登录：清空内存与本地存储 */
    logout() {
      this.token = ''
      this.userId = null
      this.username = ''
      this.name = ''
      this.role = 'user'
      clearAuth()
    }
  }
})
