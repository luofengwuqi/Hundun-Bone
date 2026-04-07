import { logoutAPI } from '@/api/user.js'
import { useUserStore } from '@/stores/user.js'
import router from '@/router/index.js'
import { ElMessage } from 'element-plus'

export const logout = async ()=> {
  try {
    await logoutAPI()
  } catch (error) {
    console.error('Logout API error:', error)
  }

  const userStore = useUserStore()

  userStore.setToken('')
  userStore.setUserInfo({})

  ElMessage.success('ヾ(￣▽￣)Bye~Bye~')

  // 使用 push 而不是 replace，并添加时间戳确保路由变化
  if (router.currentRoute.value.path === '/login') {
    // 如果当前已经在登录页，使用 window.location 强制刷新
    window.location.href = '/login'
  } else {
    await router.push('/login')
  }
}
