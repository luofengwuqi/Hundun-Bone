import request from '@/utils/request.js'

export const loginByUsernameAndPassword = async (username , password)=> {
  return await request.post(
     '/user/login/username-password',
     {
      username: username,
      password: password
    }
  )
}
