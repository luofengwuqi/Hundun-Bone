<script setup>
import LoginWay from '@/views/login/component/LoginWay.vue'
import { ref } from 'vue'
import EmailCodeForm from '@/views/login/component/EmailCodeForm.vue'
import EmailPasswordForm from '@/views/login/component/EmailPasswordForm.vue'
import UsernamePasswordForm from '@/views/login/component/UsernamePasswordForm.vue'
import PhonePasswordForm from '@/views/login/component/PhonePasswordForm.vue'
import Register from '@/views/login/component/Register.vue'
//登录模式选择器相关的东西(子传父)
const loginType = ref(`username-password`)
const changeLoginType = (type) => {
  console.log(`登录模式切换:${type}`)
  loginType.value = type
}


</script>
<template>
  <div class="login">
    <login-way @changeLoginType="changeLoginType"/>
    <div class="loginForm" v-if="loginType==='username-password'">
      <UsernamePasswordForm></UsernamePasswordForm>
    </div>
    <div class="loginForm" v-else-if="loginType==='phone-password'">
      <PhonePasswordForm></PhonePasswordForm>
    </div>
    <div class="loginForm" v-else-if="loginType==='email-code'">
      <EmailCodeForm></EmailCodeForm>
    </div>
    <div class="loginForm" v-else-if="loginType==='email-password'">
      <EmailPasswordForm></EmailPasswordForm>
    </div>
    <div class="loginForm" v-else-if="loginType==='register'">
      <Register></Register>
    </div>
    <div class="loginForm" v-else>
      纳尼
    </div>
  </div>



</template>
<style scoped lang="less">
.login{
  width: 100%;
  height: 100vh;
  position: relative;
  .loginForm{
    position:absolute;
    //居中
    top:50%;
    left:50%;
    transform:translate(-50%,-50%);
  }
}
</style>
