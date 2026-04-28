import { ref } from 'vue'
import { defineStore } from 'pinia'
import { getArticleListAPI } from '@/api/article'

export const useArticleStore = defineStore('article', () => {

  // 文章列表
  const articleList = ref([])
  
  // 用户投票状态记录 { articleId: 'good' | 'bad' | null }
  const userVoteStatus = ref({})
  
  // 获取文章列表
  const getArticleList = async () => {
    const res = await getArticleListAPI()
    articleList.value = res.data
    return res
  }
  
  // 设置用户投票状态
  const setUserVoteStatus = (articleId, status) => {
    userVoteStatus.value[articleId] = status
  }
  
  // 获取用户投票状态
  const getUserVoteStatus = (articleId) => {
    return userVoteStatus.value[articleId] || null
  }

  return {
    articleList,
    userVoteStatus,
    getArticleList,
    setUserVoteStatus,
    getUserVoteStatus
  }
})
