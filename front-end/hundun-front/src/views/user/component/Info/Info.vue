<script setup>

import LeftSide from '@/views/user/component/Info/LeftSide.vue'
</script>

<template>
  <div class="info">
    <el-row :gutter="20">
      <!-- 左侧区域 - 占8列 -->
      <el-col :span="5">
        <div class="left-panel">
          <!-- 左侧内容，如用户头像、基本信息等 -->
          <LeftSide></LeftSide>
        </div>
      </el-col>

      <!-- 右侧区域 - 占16列 -->
      <el-col :span="19">
        <div class="right-panel">
          <!-- 右侧内容，如详细信息、设置等 -->
        </div>
      </el-col>
    </el-row>
  </div>

</template>

<style scoped lang="less">
.info {
  padding: 20px;
}

.left-panel, .right-panel {
  // 可以添加背景色、内边距等样式
  min-height:  calc(100vh - 120px);
  background-color: #FFF8E8;
  border-radius: 12px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>
