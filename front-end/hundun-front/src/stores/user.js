import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', () => {
  //token - 从 localStorage 初始化
  const token = ref(localStorage.getItem('token') || '')
  const setToken = (newToken) => {
    token.value = newToken
    if (newToken) {
      localStorage.setItem('token', newToken)
    } else {
      localStorage.removeItem('token')
    }
  }
  //userInfo - 从 localStorage 初始化
  const userInfoStr = localStorage.getItem('userInfo')
  const userInfo = ref(userInfoStr ? JSON.parse(userInfoStr) : {})
  const setUserInfo = (newUserInfo) => {
    userInfo.value = newUserInfo
    if (Object.keys(newUserInfo).length > 0) {
      localStorage.setItem('userInfo', JSON.stringify(newUserInfo))
    } else {
      localStorage.removeItem('userInfo')
    }
  }


  return {
    token,
    setToken,
    userInfo,
    setUserInfo
  }
})
