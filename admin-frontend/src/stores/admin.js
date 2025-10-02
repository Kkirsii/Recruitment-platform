import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAdminStore = defineStore('admin', () => {
  const isLoggedIn = ref(false)
  const adminInfo = ref(null)
  const token = ref(null)

  // 登录
  const login = (adminData, authToken) => {
    isLoggedIn.value = true
    adminInfo.value = adminData
    token.value = authToken
    localStorage.setItem('adminToken', authToken)
    localStorage.setItem('adminInfo', JSON.stringify(adminData))
  }

  // 登出
  const logout = () => {
    isLoggedIn.value = false
    adminInfo.value = null
    token.value = null
    localStorage.removeItem('adminToken')
    localStorage.removeItem('adminInfo')
  }

  // 初始化状态
  const initAuth = () => {
    const savedToken = localStorage.getItem('adminToken')
    const savedInfo = localStorage.getItem('adminInfo')
    
    if (savedToken && savedInfo) {
      token.value = savedToken
      adminInfo.value = JSON.parse(savedInfo)
      isLoggedIn.value = true
    }
  }

  return {
    isLoggedIn,
    adminInfo,
    token,
    login,
    logout,
    initAuth
  }
})




