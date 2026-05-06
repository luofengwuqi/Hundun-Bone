<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useArticleStore } from '@/stores/article.js'
import { marked } from 'marked'
import { ElMessage } from 'element-plus'
import {  CircleClose, ChatDotRound, Clock } from '@element-plus/icons-vue'

const router = useRouter()
const articleStore = useArticleStore()
const loading = ref(true)

// 配置 marked
marked.setOptions({
  breaks: true,
  gfm: true
})

onMounted(async () => {
  try {
    await articleStore.getArticleList()
  } catch (error) {
    ElMessage.error('加载文章失败')
  } finally {
    loading.value = false
  }
})

const handleVote = async (articleId, type) => {
  const currentVote = articleStore.getUserVoteStatus(articleId)

  // 如果已经投过相同的票，取消投票
  if (currentVote === type) {
    ElMessage.info('已取消投票')
    articleStore.setUserVoteStatus(articleId, null)
    return
  }

  try {
    if (type === 'good') {
      await articleStore.updateArticleGood(articleId)
    } else {
      await articleStore.updateArticleBad(articleId)
    }
    articleStore.setUserVoteStatus(articleId, type)
    ElMessage.success('投票成功')
  } catch (error) {
    ElMessage.error('投票失败')
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const getExcerpt = (content, length = 150) => {
  if (!content) return ''
  // 移除 Markdown 语法标记，但保留标点符号
  const plainText = content
    .replace(/^#{1,6}\s+/gm, '') // 移除标题标记 #
    .replace(/\*\*(.+?)\*\*/g, '$1') // 移除粗体 **
    .replace(/\*(.+?)\*/g, '$1') // 移除斜体 *
    .replace(/`(.+?)`/g, '$1') // 移除行内代码 `
    .replace(/\[(.+?)\]\(.+?\)/g, '$1') // 移除链接 [text](url)，保留文字
    .replace(/^[-*+]\s+/gm, '') // 移除列表标记
    .replace(/^\d+\.\s+/gm, '') // 移除有序列表标记
    .trim()
  return plainText.length > length ? plainText.substring(0, length) + '...' : plainText
}

// 跳转到文章详情
const goToDetail = (articleId) => {
  router.push(`/user/article/${articleId}`)
}
</script>

<template>
  <div class="community-page">
    <!-- 装饰性背景元素 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- 页面标题区域 -->
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="page-title">社区</h1>
        <p class="page-subtitle">"菜就多练 不会就学 不懂就问"</p>
        <div class="divider">
          <span class="divider-icon">✦</span>
        </div>
      </div>
    </div>

    <!-- 文章列表区域 -->
    <div class="articles-container">
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>

      <div v-else-if="!articleStore.articleList || articleStore.articleList.length === 0" class="empty-state">
        <div class="empty-icon">📝</div>
        <p>暂无文章，快来发表第一篇吧！</p>
      </div>

      <div v-else class="articles-grid">
        <article
          v-for="article in articleStore.articleList"
          :key="article.id"
          class="article-card"
          @click="goToDetail(article.id)"
        >
          <div class="card-header">
            <h2 class="article-title">{{ article.title }}</h2>
            <div class="article-meta">
              <span class="author">@{{ article.creator || '匿名用户' }}</span>
              <span class="meta-separator">•</span>
              <span class="date">
                <el-icon><Clock /></el-icon>
                {{ formatDate(article.createdAt)|| '未被记录的时间' }}
              </span>
            </div>
          </div>

          <div class="card-body">
            <p class="article-excerpt">{{ getExcerpt(article.text) }}</p>
          </div>

          <div class="card-footer">
            <div class="vote-section">
              <button
                class="vote-btn good-btn"
                :class="{ active: articleStore.getUserVoteStatus(article.id) === 'good' }"
                @click.stop="handleVote(article.id, 'good')"
              >
                <span>{{ article.goodCount || 0 }}</span>
              </button>
              <button
                class="vote-btn bad-btn"
                :class="{ active: articleStore.getUserVoteStatus(article.id) === 'bad' }"
                @click.stop="handleVote(article.id, 'bad')"
              >
                <el-icon><CircleClose /></el-icon>
                <span>{{ article.badCount || 0 }}</span>
              </button>
            </div>
            <div class="comment-indicator">
              <el-icon><ChatDotRound /></el-icon>
              <span>{{ article.commentCount || 0 }} 评论</span>
            </div>
          </div>

          <!-- 装饰角标 -->
          <div class="card-corner"></div>
        </article>
      </div>
    </div>
  </div>
</template>

<style scoped lang="less">
@primary-color: #C3272B;
@warm-bg: #FFF8E8;
@paper-white: #FFFEF9;
@text-dark: #2C1810;
@text-muted: #8B7355;
@border-color: #E8DDD0;

.community-page {
  position: relative;
  min-height: calc(100vh - 80px);
  background: linear-gradient(135deg, @warm-bg 0%, #FFF5E6 50%, @warm-bg 100%);
  overflow-x: hidden;
}

// 背景装饰
.bg-decoration {
  position: fixed;
  top: 80px;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
  z-index: 0;

  .circle {
    position: absolute;
    border-radius: 50%;
    opacity: 0.08;

    &.circle-1 {
      width: 400px;
      height: 400px;
      background: radial-gradient(circle, @primary-color, transparent);
      top: 10%;
      right: -100px;
      animation: float 20s ease-in-out infinite;
    }

    &.circle-2 {
      width: 300px;
      height: 300px;
      background: radial-gradient(circle, #D4A574, transparent);
      bottom: 20%;
      left: -50px;
      animation: float 25s ease-in-out infinite reverse;
    }

    &.circle-3 {
      width: 200px;
      height: 200px;
      background: radial-gradient(circle, #E8C9A0, transparent);
      top: 50%;
      left: 30%;
      animation: float 18s ease-in-out infinite;
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -30px) scale(1.05);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.95);
  }
}

// Hero 区域
.hero-section {
  position: relative;
  z-index: 1;
  padding: 80px 40px 60px;
  text-align: center;

  .hero-content {
    max-width: 800px;
    margin: 0 auto;
    animation: fadeInUp 0.8s ease-out;
  }

  .page-title {
    font-family: 'Georgia', 'Times New Roman', serif;
    font-size: 56px;
    font-weight: 700;
    color: @text-dark;
    letter-spacing: 8px;
    margin-bottom: 16px;
    text-shadow: 2px 2px 4px rgba(195, 39, 43, 0.1);
    position: relative;
    display: inline-block;

    &::after {
      content: '';
      position: absolute;
      bottom: -8px;
      left: 50%;
      transform: translateX(-50%);
      width: 60px;
      height: 3px;
      background: linear-gradient(90deg, transparent, @primary-color, transparent);
    }
  }

  .page-subtitle {
    font-family: 'Georgia', serif;
    font-size: 18px;
    color: @text-muted;
    font-style: italic;
    letter-spacing: 2px;
    margin-top: 24px;
    opacity: 0.9;
  }

  .divider {
    margin-top: 32px;
    position: relative;

    &::before,
    &::after {
      content: '';
      position: absolute;
      top: 50%;
      width: 100px;
      height: 1px;
      background: linear-gradient(90deg, transparent, @border-color, transparent);
    }

    &::before {
      right: calc(50% + 30px);
    }

    &::after {
      left: calc(50% + 30px);
    }

    .divider-icon {
      font-size: 24px;
      color: @primary-color;
      opacity: 0.6;
    }
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 文章容器
.articles-container {
  position: relative;
  z-index: 1;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 40px 60px;
}

// 加载状态
.loading-state {
  text-align: center;
  padding: 100px 20px;
  color: @text-muted;

  .loading-spinner {
    width: 50px;
    height: 50px;
    margin: 0 auto 20px;
    border: 3px solid @border-color;
    border-top-color: @primary-color;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }

  p {
    font-size: 16px;
    letter-spacing: 1px;
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

// 空状态
.empty-state {
  text-align: center;
  padding: 100px 20px;
  color: @text-muted;

  .empty-icon {
    font-size: 80px;
    margin-bottom: 20px;
    opacity: 0.5;
  }

  p {
    font-size: 18px;
    font-style: italic;
  }
}

// 文章网格 - 使用瀑布流布局避免空白
.articles-grid {
  columns: 3; // 3列瀑布流
  column-gap: 32px;
  animation: fadeIn 0.6s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

// 文章卡片
.article-card {
  position: relative;
  background: @paper-white;
  border-radius: 12px;
  padding: 32px;
  box-shadow:
    0 2px 8px rgba(44, 24, 16, 0.06),
    0 8px 24px rgba(44, 24, 16, 0.04);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid @border-color;
  overflow: hidden;
  break-inside: avoid; // 防止卡片被分割到两列
  margin-bottom: 32px; // 卡片之间的垂直间距
  cursor: pointer; // 显示手型光标

  &:hover {
    transform: translateY(-8px);
    box-shadow:
      0 8px 16px rgba(195, 39, 43, 0.12),
      0 16px 48px rgba(44, 24, 16, 0.08);
    border-color: darken(@border-color, 10%);

    .article-title {
      color: @primary-color; // 悬停时标题变色
    }

    .card-corner {
      opacity: 1;
    }
  }

  .card-header {
    margin-bottom: 20px;
    border-bottom: 2px solid lighten(@border-color, 5%);
    padding-bottom: 16px;
  }

  .article-title {
    font-family: 'Georgia', serif;
    font-size: 24px;
    font-weight: 600;
    color: @text-dark;
    line-height: 1.4;
    margin-bottom: 12px;
    letter-spacing: 0.5px;
  }

  .article-meta {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
    color: @text-muted;

    .author {
      font-weight: 500;
      color: @primary-color;
    }

    .meta-separator {
      opacity: 0.4;
    }

    .date {
      display: flex;
      align-items: center;
      gap: 4px;

      .el-icon {
        font-size: 14px;
      }
    }
  }

  .card-body {
    margin-bottom: 24px;
  }

  .article-excerpt {
    font-size: 15px;
    line-height: 1.8;
    color: darken(@text-muted, 10%);
    letter-spacing: 0.3px;
    // 文本溢出省略号
    display: -webkit-box;
    -webkit-line-clamp: 4; // 最多显示4行
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
    word-break: break-word;
  }

  .card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 16px;
    border-top: 1px solid lighten(@border-color, 5%);
  }

  .vote-section {
    display: flex;
    gap: 12px;
  }

  .vote-btn {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 16px;
    border-radius: 20px;
    border: 1.5px solid @border-color;
    background: transparent;
    cursor: pointer;
    transition: all 0.3s ease;
    font-size: 14px;
    color: @text-muted;

    .el-icon {
      font-size: 16px;
    }

    &:hover {
      transform: scale(1.05);
    }

    &.good-btn {
      &:hover,
      &.active {
        border-color: #67C23A;
        color: #67C23A;
        background: rgba(103, 194, 58, 0.08);
      }
    }

    &.bad-btn {
      &:hover,
      &.active {
        border-color: #F56C6C;
        color: #F56C6C;
        background: rgba(245, 108, 108, 0.08);
      }
    }
  }

  .comment-indicator {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    color: @text-muted;
    opacity: 0.7;

    .el-icon {
      font-size: 16px;
    }
  }

  .card-corner {
    position: absolute;
    top: 0;
    right: 0;
    width: 60px;
    height: 60px;
    background: linear-gradient(135deg, transparent 50%, rgba(195, 39, 43, 0.08) 50%);
    opacity: 0;
    transition: opacity 0.3s ease;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .hero-section {
    padding: 60px 20px 40px;

    .page-title {
      font-size: 36px;
      letter-spacing: 4px;
    }

    .page-subtitle {
      font-size: 14px;
    }
  }

  .articles-container {
    padding: 0 20px 40px;
  }

  .articles-grid {
    columns: 1; // 移动端单列
    column-gap: 24px;
  }

  .article-card {
    padding: 24px;

    .article-title {
      font-size: 20px;
    }
  }
}
</style>
