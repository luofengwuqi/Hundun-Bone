import request from '@/utils/request.js'
import { useUserStore } from '@/stores/user.js'

export const loginByUsernameAndPassword = async (username , password)=> {
  return await request.post(
     '/user/login/username-password',
     {
      username: username,
      password: password
    }
  )
}

export const loginByPhoneAndPassword = async (phone , password)=>{
  return await  request.post('/user/login/phone-password',
    {
    phone,
    password,
  })
}

export const changeStatus = async (username)=>{
  console.log(useUserStore().token)
  return await request.put(`/user/status?username=${username}`)
}

export const logoutAPI = async ()=>{
  console.log('登出逻辑待完善')
  //TODO:前后交互逻辑待完善
}
