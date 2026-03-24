import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user.js'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path:'/',
      redirect:'/login',
      children:[
        {path:'login',component:()=>import('@/views/login/Login.vue')},
        {path:'admin',component:()=>import('@/views/admin/AdminLayout.vue')},
        {path:'user',component:()=>import('@/views/user/UserHome.vue')},
      ]
    }

  ],
})

router.beforeEach(async (to)=>{
  const userStore = useUserStore()
  if(!userStore.token && to.path !=='/login')
    return '/login'
})

export default router
