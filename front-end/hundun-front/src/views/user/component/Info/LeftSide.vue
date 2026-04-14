<script setup>
import { useUserStore } from '@/stores/user.js'
import { computed } from 'vue'
import defaultAvatar from '@/assets/img/common/user.jpg'

const userStore = useUserStore()

// OSS 域名配置
const OSS_DOMAIN = 'https://fifteen-hundun.oss-cn-beijing.aliyuncs.com'

// 获取用户头像
const avatarSrc = computed(() => {
  const avatarUrl = userStore.userInfo?.avatarUrl
  if (!avatarUrl) return defaultAvatar
  // 如果已经是完整 URL，直接返回；否则拼接 OSS 域名
  return avatarUrl.startsWith('http') ? avatarUrl : `${OSS_DOMAIN}/${avatarUrl}`
})

//定义 emit 事件
const emit = defineEmits(['change-operation-type'])

//用户信息相关
const handleChangePic = ()=>{
  // 触发事件,将 operationType 设置为 1
  emit('change-operation-type', 1)
}
</script>

<template>
  <div class="left">
    <div class="user-msg">
      <div class="img-box" @click="handleChangePic">
        <img :src="avatarSrc" alt="" />
        <div class="overlay">
          <span>更改头像</span>
        </div>
      </div>
      <div class="username">
        {{ userStore.userInfo?.username || '' }}
        <span v-if="userStore.userInfo?.nickname">| {{ userStore.userInfo.nickname }}</span>
      </div>
      <div class="email">
        <span v-if="userStore.userInfo?.email">{{ userStore.userInfo.email }}</span>
      </div>
    </div>
    <el-divider />
    <div class="user-operation">

    </div>
  </div>
</template>
<style scoped lang="less">
.left {
  .user-msg {
    margin-bottom: 15px;
    padding: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-direction: column;
    .img-box {
      position: relative;
      margin-bottom: 15px;
      width: 160px;
      height: 160px;
      border-radius: 50%;
      overflow: hidden;
      cursor: pointer;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .overlay {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        opacity: 0;
        transition: opacity 0.3s ease;

        span {
          color: #fff;
          font-size: 20px;
        }
      }

      &:hover {
        .overlay {
          opacity: 1;
        }
      }
    }
    .username {
      font-size: 25px;
      font-weight: 600;
    }
  }
}
</style>
