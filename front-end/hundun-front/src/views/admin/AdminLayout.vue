<script setup>

import Aside from '@/views/admin/Aside.vue'
import User from '@/views/admin/component/User.vue'
</script>

<template>
  <div class="common-layout">
    <el-container>
      <el-aside :span="6">
        <Aside />
      </el-aside>
      <el-container>
        <el-header>Header</el-header>
        <el-main :span="18">
          <User></User>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style lang="less">
//  左侧边栏
.el-aside {
  /* 你要求的外边距 + 背景色 */
  margin: 20px;
  background-color: #FFF8E8;
  height: calc(100vh - 40px); /* 铺满屏幕高度（减去上下margin） */
  border-radius: 20px; /* 圆角更柔和，替代直角生硬感 */
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); /* 轻阴影提升层次感 */
  transition: all 0.3s ease; /* 平滑过渡动画 */
  overflow: hidden; /* 隐藏圆角溢出内容 */
}
// 右边主体
.el-header {
  margin: 20px;
  background-color: #FFF8E8;
  border-radius: 20px; /* 圆角更柔和，替代直角生硬感 */
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); /* 轻阴影提升层次感 */
  transition: all 0.3s ease; /* 平滑过渡动画 */
  overflow: hidden; /* 隐藏圆角溢出内容 */
}
.el-aside {
  margin: 20px;
  background-color: #FFF8E8;
}
</style>
