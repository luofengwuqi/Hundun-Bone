<template>
  <div>
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
        <el-button type="primary" @click="handleSearch">查询</el-button>
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
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { changeStatus, deleteSingleUser } from '@/api/user.js'
import request from '@/utils/request.js'
import { ElMessage, ElMessageBox } from 'element-plus'

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
  await deleteSingleUser(id)
  await fetchUsers()
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

.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  padding: 16px 0;
  background: #FFF8E8;
  border-radius: 8px;
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
</style>
