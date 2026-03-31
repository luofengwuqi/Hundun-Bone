import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user.js'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path:'/',redirect:'/login'},
    { path:'/login', component:()=>import('@/views/login/Login.vue')},
    { path:'/help', component:()=>import('@/views/help/Help.vue')},
    { path:'/admin',component:()=>import('@/views/admin/AdminLayout.vue')},
    { path:'/user',component:()=>import('@/views/user/UserHome.vue')},
  ],
})

router.beforeEach(async (to) => {
  const userStore = useUserStore()

  // 未登录的情况
  if (!userStore.token) {
    // 如果访问的是 login 或 help 页面，直接放行
    if (to.path === '/login' || to.path === '/help') {
      return true
    }
    // 其他页面重定向到登录页
    return '/login'
  }

  // 已登录的情况
  // 如果访问登录页，那就把pinia清空
  if (to.path === '/login') {
    useUserStore().setUserInfo({})
    useUserStore().setToken('')
  }
  // 放行
  return true
})

export default router
