import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', () => {
  //token
  const token = ref('')
  const setToken = (newToken) => {
    token.value = newToken
  }
  //userInfo
  const userInfo = ref({})
  const setUserInfo = (newUserInfo) => {
    userInfo.value = newUserInfo
  }


  return {
    token,
    setToken,
    userInfo,
    setUserInfo
  }
})
