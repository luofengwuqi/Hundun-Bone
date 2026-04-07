<template>
  <div class="feedback-container">
    <el-empty v-if="!loading && feedbackList.length === 0" description="暂无反馈数据" />

    <div v-loading="loading" class="feedback-grid">
      <transition-group name="feedback-list">
        <el-card
          v-for="(item, index) in feedbackList"
          :key="index"
          class="feedback-card"
          shadow="hover"
        >
          <template #header>
            <div class="card-header">
              <span class="feedback-type">{{ item.type }}</span>
              <el-tag :type="getStatusType(item.status)" size="small">
                {{ getStatusText(item.status) }}
              </el-tag>
            </div>
          </template>

          <div class="card-body">
            <div class="info-item">
              <span class="label">用户：</span>
              <span class="value">{{ item.username }}</span>
            </div>

            <div class="info-item">
              <span class="label">邮箱：</span>
              <span class="value">{{ item.userEmail }}</span>
            </div>

            <div class="info-item">
              <span class="label">反馈内容：</span>
              <div class="value text-content">
                <div v-if="isTextLong(item.text)" class="text-collapsed">
                  {{ truncateText(item.text) }}
                  <el-button
                    type="primary"
                    link
                    size="small"
                    @click="toggleText(item)"
                  >
                    {{ item.expanded ? '收起' : '展开' }}
                  </el-button>
                </div>
                <div v-else class="text-full">
                  {{ item.text }}
                </div>
              </div>
            </div>

            <div v-if="item.createdAt" class="info-item">
              <span class="label">创建时间：</span>
              <span class="value">{{ formatDate(item.createdAt) }}</span>
            </div>

            <div v-if="item.finishedAt" class="info-item">
              <span class="label">处理时间：</span>
              <span class="value">{{ formatDate(item.finishedAt) }}</span>
            </div>
          </div>
        </el-card>
      </transition-group>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request.js'

const loading = ref(false)
const feedbackList = ref([])

// 状态映射
const statusMap = {
  '0': { type: 'warning', text: '未处理' },
  '1': { type: 'success', text: '已处理' },
  '-1': { type: 'danger', text: '已驳回' }
}

// 获取状态类型
const getStatusType = (status) => {
  return statusMap[status]?.type || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  return statusMap[status]?.text || '未知'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 判断文本是否过长（超过100字符）
const isTextLong = (text) => {
  return text && text.length > 100
}

// 截断文本
const truncateText = (text) => {
  if (!text) return ''
  return text.length > 100 ? text.substring(0, 100) + '...' : text
}

// 切换文本展开/收起
const toggleText = (item) => {
  item.expanded = !item.expanded
}

// 获取反馈列表
const fetchFeedback = async () => {
  loading.value = true
  try {
    const res = await request.get('/feedback')

    if (res.code === 200 || res.code === 0) {
      feedbackList.value = (res.data || []).map(item => ({
        ...item,
        expanded: false
      }))
    }
  } catch (error) {
    console.error('获取反馈列表失败:', error)
    // ElMessage.error('获取反馈列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchFeedback()
})
</script>

<style scoped>
.feedback-container {
  padding: 20px;
  background: #FFF8E8;
  min-height: 100%;
}

.feedback-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.feedback-card {
  border-radius: 8px;
  transition: all 0.3s ease;
  background-color: #FFF8E8;
}

.feedback-card:hover {
  transform: translateY(-4px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.feedback-type {
  font-size: 16px;
  font-weight: 600;
  color: #409eff;
}

.card-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  line-height: 1.6;
}

.label {
  font-weight: 500;
  color: #606266;
  white-space: nowrap;
  min-width: 80px;
}

.value {
  color: #303133;
  flex: 1;
  word-break: break-all;
}

.text-content {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  min-height: 40px;
}

.text-collapsed,
.text-full {
  line-height: 1.8;
  white-space: pre-wrap;
}

/* 卡片列表过渡动画 */
.feedback-list-enter-active,
.feedback-list-leave-active {
  transition: all 0.4s ease;
}

.feedback-list-enter-from {
  opacity: 0;
  transform: translateY(20px) scale(0.95);
}

.feedback-list-enter-to {
  opacity: 1;
  transform: translateY(0) scale(1);
}

.feedback-list-leave-from {
  opacity: 1;
  transform: translateY(0) scale(1);
}

.feedback-list-leave-to {
  opacity: 0;
  transform: translateY(-20px) scale(0.95);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .feedback-grid {
    grid-template-columns: 1fr;
  }

  .feedback-container {
    padding: 10px;
  }
}
</style>
