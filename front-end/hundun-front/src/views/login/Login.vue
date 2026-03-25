<script setup>
import LoginWay from '@/views/login/component/LoginWay.vue'
import { ref } from 'vue'
//登录模式选择器相关的东西(子传父)
const loginType = ref(`phone-password`)
const changeLoginType = (type) => {
  console.log(`登录模式切换:${type}`)
  loginType.value = type
}

</script>
<template>
  <login-way @changeLoginType="changeLoginType"/>
  <div class="loginForm">

  </div>

</template>
<style scoped>

</style>
