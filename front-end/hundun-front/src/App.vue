<script setup>
import {RouterView} from 'vue-router'

</script>

<template>
  <div class="app">
    <router-view></router-view>
  </div>
</template>

<style scoped>
  .app {
    width: 100vw;
    height: 100vh;
    background: url("@/assets/img/common/background.jpg") no-repeat center ;
    background-size: cover;
  }
</style>
