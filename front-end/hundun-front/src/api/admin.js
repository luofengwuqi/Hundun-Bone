import request from "@/utils/request.js";

export const addAdmin = async (admin)=>{
  return await request.post('/admin',admin)
}

export const getInviteCode = async ()=>{
  return await request.get('/admin/invite-code')
}

export const deleteSingleUser = async (id)=> {
  return await request.delete(`/admin/user/${id}`)
}

export const changeStatus = async (username)=>{
  // console.log(useUserStore().token)
  return await request.put(`/admin/user/status?username=${username}`)
}
