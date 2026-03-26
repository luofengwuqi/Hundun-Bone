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

router.beforeEach(async (to)=>{
  const userStore = useUserStore()
  // 允许访问 login 和 help 页面
  if(!userStore.token && to.path !=='/login' && to.path !== '/help')
    return '/login'
})

export default router
