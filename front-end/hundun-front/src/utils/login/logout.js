import {  logoutAPI } from '@/api/user.js'
import { useUserStore } from '@/stores/user.js'
import router from '@/router/index.js'
import { ElMessage } from 'element-plus'

export const logout = async ()=> {
  await logoutAPI()
  const userStore = useUserStore()
  userStore.setToken('')
  userStore.setUserInfo({})
  ElMessage.success('ヾ(￣▽￣)Bye~Bye~')
  await router.replace('/login')
}
