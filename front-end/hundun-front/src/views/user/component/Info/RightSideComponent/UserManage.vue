<script setup>
import { useUserStore } from '@/stores/user.js'
import { computed } from 'vue'

const userStore = useUserStore()

// 获取用户名
const username = computed(() => userStore.userInfo?.username || '')

// 获取昵称
const nickname = computed(() => userStore.userInfo?.nickname || '')

// 获取邮箱
const email = computed(() => userStore.userInfo?.email || '')

// 获取手机号
const phone = computed(() => userStore.userInfo?.phone || '')
</script>

<template>
  <div class="user-manage">
    <div class="info-item">
      <div class="info-label">
        <span class="label-title">您的名字</span>
        <span class="label-value" :class="{ unset: !username }">{{ username }}</span>
      </div>
      <p class="info-desc">名字常常用于锚定存在 不可更改</p>
    </div>

    <div class="info-item">
      <div class="info-label">
        <span class="label-title">您更希望被称呼为</span>
        <span class="label-value" :class="{ unset: !nickname }">{{ nickname || '未设置' }}</span>
      </div>
      <p class="info-desc">名字同样用于记录变革...</p>
    </div>

    <div class="info-item">
      <div class="info-label">
        <span class="label-title">您的密码</span>
        <span class="label-value unset">****</span>
      </div>
      <p class="info-desc">嘘 不能告诉别人</p>
    </div>

    <div class="info-item">
      <div class="info-label">
        <span class="label-title">您的邮箱</span>
        <span class="label-value" :class="{ unset: !email }">{{ email || '未设置' }}</span>
      </div>
      <p class="info-desc">
        <span v-if="email">一种写信的新的方式 但是更多的时候不是用于写信</span>
        <span v-else>或许会错过些什么呢...</span>
      </p>
    </div>

    <div class="info-item">
      <div class="info-label">
        <span class="label-title">您的手机号</span>
        <span class="label-value" :class="{ unset: !phone }">{{ phone || '未设置' }}</span>
      </div>
      <p class="info-desc">
        <span v-if="phone">一串数字 因你的存在而有意义...</span>
        <span v-else>或许会错过些什么呢...</span>
      </p>
    </div>
  </div>
</template>

<style scoped lang="less">
@import url('https://fonts.googleapis.com/css2?family=Ma+Shan+Zheng&family=ZCOOL+KuaiLe&family=Caveat:wght@400;600;700&family=Indie+Flower&display=swap');

.user-manage {
  padding: 40px 30px;
  font-family: 'Caveat', 'Indie Flower', 'Ma Shan Zheng', 'ZCOOL KuaiLe', cursive, sans-serif;
  position: relative;
  min-height: 84vh;

  .info-item {
    position: absolute;
    padding: 15px 20px;
    opacity: 0;
    animation: fadeIn 0.8s ease-out forwards;

    &:nth-child(1) {
      top: 6%;
      left: 12%;
      transform: rotate(-3deg);
      animation-delay: 0.1s;
    }

    &:nth-child(2) {
      top: 12%;
      right: 8%;
      transform: rotate(2.5deg);
      animation-delay: 0.2s;
    }

    &:nth-child(3) {
      top: 35%;
      left: 5%;
      transform: rotate(-1.5deg);
      animation-delay: 0.3s;
    }

    &:nth-child(4) {
      top: 48%;
      right: 15%;
      transform: rotate(3deg);
      animation-delay: 0.4s;
    }

    &:nth-child(5) {
      bottom: 10%;
      left: 15%;
      transform: rotate(-2deg);
      animation-delay: 0.5s;
    }

    &:nth-child(6) {
      bottom: 20%;
      right: 15%;
      transform: rotate(2deg);
    }

    &:nth-child(7) {
      bottom: 5%;
      left: 20%;
      transform: rotate(-1.5deg);
    }

    .info-label {
      display: flex;
      align-items: baseline;
      gap: 15px;
      margin-bottom: 10px;
      flex-wrap: wrap;

      .label-title {
        font-size: 22px;
        color: #2c3e50;
        font-weight: 600;
        letter-spacing: 2px;
        position: relative;

        &::after {
          content: '';
          position: absolute;
          bottom: -4px;
          left: 0;
          width: 100%;
          height: 2px;
          background: #d4a574;
          opacity: 0.4;
          transform: rotate(-1deg);
        }
      }

      .label-value {
        font-size: 38px;
        color: #2c2420;
        font-weight: 700;
        position: relative;
        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.08);
        margin-left: 5px;

        &::before {
          content: '→ ';
          color: #C3272B;
          font-size: 32px;
        }
      }
    }

    .info-desc {
      margin: 0;
      padding-left: 15px;
      font-size: 22px;
      color: #8b7d6f;
      line-height: 1.9;
      font-style: italic;
      letter-spacing: 0.5px;
      position: relative;
      font-weight: 500;

      &::before {
        content: '✎ ';
        color: #c4b5a5;
        font-size: 20px;
      }

      .hint {
        display: block;
        margin-top: 5px;
        color: #C3272B;
        font-size: 20px;
        opacity: 0.8;
      }
    }
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style>
