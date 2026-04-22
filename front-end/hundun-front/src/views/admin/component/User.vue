<template>
  <div v-show="!detailStatus">
    <!-- 搜索栏 -->
    <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent>
      <el-form-item label="用户名">
        <el-input v-model="searchForm.name" placeholder="请输入用户名" clearable @keydown.enter.prevent />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable @keydown.enter.prevent style="min-width: 140px;">
          <el-option label="用户" :value="1" />
          <el-option label="用户禁用" :value="-1" />
          <el-option label="管理员" :value="2" />
          <el-option label="管理员禁用" :value="-2" />
          <el-option label="超级管理员" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" class="search-btn" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格 -->
    <transition-group name="table-list" tag="div">
      <el-table
        :key="tableKey"
        :data="tableData"
        style="width: 100%"
        :row-class-name="tableRowClassName"
        v-loading="loading"
        :header-cell-style="{ background: '#FFEFD5', color: '#606266', fontWeight: '600' }"
        :cell-style="{ padding: '12px 0' }"
        border
      >
        <el-table-column prop="username" label="用户名" min-width="150" show-overflow-tooltip />
        <el-table-column prop="email" label="邮箱" min-width="200" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" min-width="130" />
        <el-table-column prop="status" label="状态" width="110" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small" effect="light">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginAt" label="最后登录时间" min-width="170" />
        <el-table-column label="操作" fixed="right" width="220" align="center">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              plain
              @click="handleViewDetail(scope.row)"
            >
              修改
            </el-button>
            <el-button
              type="success"
              size="small"
              @click="handleChangeStatus(scope.row.username, -1)"
              v-if="scope.row.status < 0"
              plain
            >
              启用
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleChangeStatus(scope.row.username, 1)"
              v-else-if="scope.row.status > 0"
              plain
            >
              禁用
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleDeleteUser(scope.row.id)"
              plain
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </transition-group>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="pagination.page"
      v-model:page-size="pagination.size"
      :page-sizes="[10, 20, 50, 100]"
      :total="pagination.total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
      class="pagination"
      background
      :small="false"
    />
  </div>
  <transition name="detail-fade">
    <div v-show="detailStatus" class="user-detail-container">
      <div class="detail-header">
        <el-button type="primary" class="back-btn" @click="handleBackToList">
          <el-icon><ArrowLeft /></el-icon>
          返回列表
        </el-button>
        <h2 class="detail-title">用户详细信息</h2>
      </div>

      <el-card class="detail-card" shadow="hover">
        <div class="detail-content">
          <!-- 头像和基本信息 -->
          <div class="avatar-section">
            <el-avatar :size="120" :src="currentUser.avatarUrl || ''" class="user-avatar">
              <el-icon :size="60"><User /></el-icon>
            </el-avatar>
            <div class="basic-info">
              <h3 class="username">{{ currentUser.username }}</h3>
              <p class="nickname" v-if="currentUser.nickname">昵称：{{ currentUser.nickname }}</p>
              <el-tag :type="getStatusType(currentUser.status)" size="large">
                {{ getStatusText(currentUser.status) }}
              </el-tag>
            </div>
          </div>

          <el-divider />

          <!-- 详细信息网格 -->
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">用户ID</span>
              <span class="info-value">{{ currentUser.id }}</span>
            </div>

            <div class="info-item">
              <span class="info-label">用户名</span>
              <span class="info-value">{{ currentUser.username }}</span>
            </div>

            <div class="info-item">
              <span class="info-label">邮箱</span>
              <span class="info-value">{{ currentUser.email || '未设置' }}</span>
            </div>

            <div class="info-item">
              <span class="info-label">密码</span>
              <span class="info-value password-masked">******</span>
            </div>

            <div class="info-item">
              <span class="info-label">手机号</span>
              <span class="info-value">{{ currentUser.phone || '未设置' }}</span>
            </div>

            <div class="info-item">
              <span class="info-label">创建时间</span>
              <span class="info-value">{{ formatDate(currentUser.createdAt) }}</span>
            </div>

            <div class="info-item">
              <span class="info-label">更新时间</span>
              <span class="info-value">{{ formatDate(currentUser.updatedAt) }}</span>
            </div>

            <div class="info-item ">
              <span class="info-label">最后登录时间</span>
              <span class="info-value">{{ formatDate(currentUser.lastLoginAt) }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </transition>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { changeStatus, deleteSingleUser, getUserInfoAPI } from '@/api/user.js'
import request from '@/utils/request.js'
import { ElMessage, ElMessageBox } from 'element-plus'
//操作用户信息细节的相关代码
const detailStatus = ref(false)
const currentUser = ref({})

// 加载状态
const loading = ref(false)

// 表格 key，用于触发过渡动画
const tableKey = computed(() => {
  return pagination.page + '-' + pagination.size
})

// 搜索表单
const searchForm = reactive({
  name: '',
  status: null
})

// 分页配置
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表格数据
const tableData = ref([])

// 状态映射
const statusMap = {
  1: { type: 'success', text: '用户' },
  '-1': { type: 'danger', text: '用户禁用' },
  2: { type: 'warning', text: '管理员' },
  '-2': { type: 'danger', text: '管理禁用' },
  3: { type: 'primary', text: '超级管理' }
}

// 获取状态类型
const getStatusType = (status) => {
  return statusMap[status]?.type || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  return statusMap[status]?.text || '未知'
}

// 表格行样式
const tableRowClassName = ({ row }) => {
  if (row.status === 3) {
    return 'super-admin-row'
  }
  return ''
}

// 查询用户列表
const fetchUsers = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.size,
      name: searchForm.name || undefined,
      status: searchForm.status || undefined
    }

    const res = await request.post('/user/page', params)

    if (res.code === 0 || res.code === 200) {
      tableData.value = res.data.list || []
      pagination.total = res.data.total || 0
    }
  } catch (error) {
    console.error('查询用户列表失败:', error)
    // ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchUsers()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.status = null
  pagination.page = 1
  fetchUsers()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  fetchUsers()
}

// 页码变化
const handlePageChange = (page) => {
  pagination.page = page
  fetchUsers()
}

// 修改状态
const handleChangeStatus = async (username, targetStatus) => {
  try {
    const action = targetStatus === -1 ? '启用' : '禁用'
    await ElMessageBox.confirm(`确定要${action}该用户吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await changeStatus(username)
    console.log(res)

    if(targetStatus===-1){
      ElMessage.success(`解禁成功!`)
    }else{
      ElMessage.success(`替民除害成功!`)
    }
    await fetchUsers() // 刷新列表
  } catch (error) {
    if (error !== 'cancel') {
      console.error('操作失败:', error)
    }
  }
}

//删除用户
const handleDeleteUser = async (id) => {
  // 弹出警告框
  await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  })
  await deleteSingleUser(id)
  await fetchUsers()
  ElMessage.success(`ID${id}永远的消失了...`)
}

// 查看用户详情
const handleViewDetail = async (row) => {
  const res = await getUserInfoAPI(row.id)
  currentUser.value = res.data
  detailStatus.value = true
}

// 返回列表
const handleBackToList = () => {
  detailStatus.value = false
  currentUser.value = {}
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '未记录'
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}
// 初始化加载
onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.search-form {
  margin-bottom: 20px;
  background: #FFF8E8;
  padding: 24px 24px 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

/* 查询按钮样式 */
.search-btn {
  background-color: #C3272B !important;
  border-color: #C3272B !important;
}

.search-btn:hover {
  background-color: #D64A4F !important;
  border-color: #D64A4F !important;
}

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  padding: 16px 0;
  background: #FFF8E8;
  border-radius: 8px;
}

/* 分页当前页样式 */
:deep(.el-pagination.is-background .el-pager li.is-active) {
  background-color: #C3272B !important;
  color: #fff !important;
}

:deep(.el-pagination.is-background .el-pager li:not(.is-active):hover) {
  color: #C3272B !important;
}

.el-table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  background-color: #FFF8E8;
}

.el-table .super-admin-row {
  background-color: #ecf5ff !important;
}

.el-table .super-admin-row .cell {
  color: #409eff;
  font-weight: 500;
}

/* 表格头部样式 */
.el-table th {
  background-color: #FFEFD5 !important;
  color: #606266;
  font-weight: 600;
}

/* 表格行样式 */
:deep(.el-table__row) {
  background-color: #FFF8E8 !important;
}

:deep(.el-table__row td) {
  background-color: #FFF8E8 !important;
}

:deep(.el-table th.el-table__cell) {
  background-color: #FFEFD5 !important;
}

:deep(.el-table td.el-table__cell) {
  background-color: #FFF8E8 !important;
}

:deep(.el-table__body tr) {
  background-color: #FFF8E8 !important;
}

/* 按钮组样式 */
.el-table .el-button {
  margin: 2px;
  min-width: 60px;
}

/* 状态标签样式 */
.el-tag--small {
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
}

/* 表格列表过渡动画 */
.table-list-enter-active,
.table-list-leave-active {
  transition: all 0.4s ease;
}

.table-list-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.table-list-enter-to {
  opacity: 1;
  transform: translateY(0);
}

/* 表格行逐个出现动画 */
:deep(.el-table__body tr) {
  animation: table-row-fade-in 0.3s ease-in-out forwards;
  opacity: 0;
}

:deep(.el-table__body tr:nth-child(1)) {
  animation-delay: 0.05s;
}

:deep(.el-table__body tr:nth-child(2)) {
  animation-delay: 0.1s;
}

:deep(.el-table__body tr:nth-child(3)) {
  animation-delay: 0.15s;
}

:deep(.el-table__body tr:nth-child(4)) {
  animation-delay: 0.2s;
}

:deep(.el-table__body tr:nth-child(5)) {
  animation-delay: 0.25s;
}

:deep(.el-table__body tr:nth-child(6)) {
  animation-delay: 0.3s;
}

:deep(.el-table__body tr:nth-child(7)) {
  animation-delay: 0.35s;
}

:deep(.el-table__body tr:nth-child(8)) {
  animation-delay: 0.4s;
}

:deep(.el-table__body tr:nth-child(9)) {
  animation-delay: 0.45s;
}

:deep(.el-table__body tr:nth-child(10)) {
  animation-delay: 0.5s;
}

@keyframes table-row-fade-in {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 用户详情页面样式 */
.user-detail-container {
  padding: 20px;
  animation: slideInRight 0.4s ease-out;
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
}

.back-btn {
  background-color: #C3272B !important;
  border-color: #C3272B !important;
}

.back-btn:hover {
  background-color: #D64A4F !important;
  border-color: #D64A4F !important;
}

.detail-title {
  font-size: 24px;
  color: #333;
  margin: 0;
  font-weight: 600;
}

.detail-card {
  background-color: #FFF8E8;
  border-radius: 12px;
  overflow: hidden;
}

.detail-content {
  padding: 20px;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 30px;
  margin-bottom: 20px;
}

.user-avatar {
  border: 3px solid #FFEFD5;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  background-color: #FFEFD5;
}

.basic-info {
  flex: 1;
}

.username {
  font-size: 28px;
  color: #333;
  margin: 0 0 8px 0;
  font-weight: 600;
}

.nickname {
  font-size: 16px;
  color: #666;
  margin: 0 0 12px 0;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-top: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 16px;
  background-color: rgba(255, 239, 213, 0.5);
  border-radius: 8px;
  transition: all 0.3s ease;
}

.info-item:hover {
  background-color: rgba(255, 239, 213, 0.8);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-label {
  font-size: 14px;
  color: #999;
  font-weight: 500;
}

.info-value {
  font-size: 16px;
  color: #333;
  font-weight: 500;
  word-break: break-all;
}

.password-masked {
  font-family: 'Courier New', monospace;
  letter-spacing: 2px;
  color: #C3272B;
  font-weight: 600;
}

/* 详情页面过渡动画 */
.detail-fade-enter-active,
.detail-fade-leave-active {
  transition: all 0.4s ease;
}

.detail-fade-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.detail-fade-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}
</style>
