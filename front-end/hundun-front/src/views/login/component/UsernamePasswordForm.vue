<script setup>
import { reactive } from 'vue'

const form = reactive({
  username: '',
  password: '',
})
</script>
<template>
  <div class="form">
    <div class="logo-box">
      <img src="../../../assets/img/common/logotext.png" alt="这是logo 按理来说不会显示不出来 哈哈">
    </div>
    <el-form :model="form" label-width="auto" style="max-width: 600px">
    <el-form-item label="用户名">
      <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
    </el-form-item>
    <el-form-item label="密码">
      <el-input v-model="form.password" placeholder="请输入密码"></el-input>
    </el-form-item>
    <el-form-item>
      <el-button type="primary">登录</el-button>
    </el-form-item>
    <el-form-item>
      <el-button type="primary">注册</el-button>
    </el-form-item>
    <el-form-item>
      <el-button type="primary">忘记密码</el-button>
    </el-form-item>
    </el-form>

  </div>
</template>
<style scoped>
.form{
  padding: 50px;
  border-radius: 20px;
  background-color: #fff;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  width: 460px;
  .logo-box{
    width: 100%;
    height: 100px;
    margin-bottom: 25px;
    img{
      width: 100%;
      height: 100%;
      /* 关键：让图片完整显示在容器内，保持宽高比 */
      object-fit: contain;
    }

  }
  /* 专门放大表单标签字体 */
  :deep(.el-form-item__label) {
    font-size: 18px;
  }
  :deep(.el-input__inner) {
    height: 45px;       /* 输入框高度变大 */
    padding: 0 15px;    /* 左右内边距变大 */

  }
}

</style>
