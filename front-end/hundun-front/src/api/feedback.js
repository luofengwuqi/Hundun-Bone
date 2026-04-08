import request from '@/utils/request.js'

// 获取反馈列表
export const getFeedbackAPI = async () => {
  return await request.get('/feedback')
}

// 处理反馈（完成/驳回）
export const handleFeedbackAPI = async (id, status) => {
  return await request.put('/feedback/status', {
    id,
    status
  })
}
