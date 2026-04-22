import { ref } from 'vue'
import { defineStore } from 'pinia'
import { getArticleListAPI } from '@/api/article'

export const useArticleStore = defineStore('article', () => {

  // 文章列表
  const articleList = ref([])
  
  // 获取文章列表
  const getArticleList = async () => {
    const res = await getArticleListAPI()
    articleList.value = res.data
    return res
  }

  return {
    articleList,
    getArticleList
  }
})
