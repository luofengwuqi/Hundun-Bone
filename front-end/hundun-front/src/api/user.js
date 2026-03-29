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
