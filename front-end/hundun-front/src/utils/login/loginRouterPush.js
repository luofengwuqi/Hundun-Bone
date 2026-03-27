import router from '@/router/index.js'
import {useUserStore} from '@/stores/user.js'

export const loginRouterPush = async () => {
  const userStore = useUserStore()

  if(userStore.userInfo.status === 3 || userStore.userInfo.status === 2){
    await router.push('/admin')
  }
  else{
    await router.push('/user')
  }
}
