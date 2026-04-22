import request from '@/utils/request'

// 获取文章列表
export const getArticleListAPI = () => {
  return request({
    url: '/article',
    method: 'GET'
  })
}