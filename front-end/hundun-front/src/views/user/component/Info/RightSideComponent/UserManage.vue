<script setup>
import { useUserStore } from '@/stores/user.js'
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import inputpic from '@/assets/img/user/info/inputpic.png'

const router = useRouter()
const userStore = useUserStore()

// 获取用户名
const username = computed(() => userStore.userInfo?.username || '')

// 获取昵称
const nickname = computed(() => userStore.userInfo?.nickname || '')

// 获取邮箱
const email = computed(() => userStore.userInfo?.email || '')

// 获取手机号
const phone = computed(() => userStore.userInfo?.phone || '')

// 当前激活的项
const activeIndex = ref(null)

// 显示提示
const showHint = ref(false)

// 显示图片（用于其他项未掉落完成时）
const showInputPic = ref(false)
const picTimeout = ref(null)
const debugMode = ref(true) // 开启调试模式

// 图片加载成功
const handleImageLoad = () => {
  console.log('图片加载成功:', inputpic)
  debugMode.value = false
}

// 图片加载失败
const handleImageError = (e) => {
  console.error('图片加载失败:', inputpic)
  console.log('当前组件所在路径:', import.meta.url)
  debugMode.value = true
}

// 处理点击
const handleItemClick = (index) => {
  activeIndex.value = index

  // 如果点击的是昵称(1)、邮箱(3)或手机号(4)，显示图片
  showInputPic.value = index === 1 || index === 3 || index === 4;

  // 如果点击的是名字（索引0），在掉落动画后显示提示
  if (index === 0) {
    setTimeout(() => {
      showHint.value = true
    }, 800)
  } else {
    showHint.value = false
  }
}

// 复位
const handleReset = () => {
  activeIndex.value = null
  showHint.value = false
  showInputPic.value = false
  if (picTimeout.value) clearTimeout(picTimeout.value)
}

// 联系管理员
const handleContactAdmin = () => {
  router.replace('/help')
}

// 禁止滚动
const preventScroll = (e) => {
  e.preventDefault()
}

onMounted(() => {
  // 添加滚动禁止监听
  window.addEventListener('wheel', preventScroll, { passive: false })
  window.addEventListener('touchmove', preventScroll, { passive: false })
})

onUnmounted(() => {
  // 移除滚动禁止监听
  window.removeEventListener('wheel', preventScroll)
  window.removeEventListener('touchmove', preventScroll)
})
</script>

<template>
  <div class="user-manage" :class="{ 'has-active': activeIndex !== null }">
    <div class="reset-btn" v-if="activeIndex !== null" @click.stop="handleReset">
      <span>复位</span>
    </div>
    <div class="info-item" :class="{ 'active': activeIndex === 0, 'fall': activeIndex !== null && activeIndex !== 0 }" @click="handleItemClick(0)">
      <div class="info-label">
        <span class="label-title">您的名字</span>
        <span class="label-value" :class="{ unset: !username }">{{ username }}</span>
      </div>
      <p class="info-desc">名字常常用于锚定存在 不可更改</p>
    </div>

    <div class="info-item" :class="{ 'active': activeIndex === 1, 'fall': activeIndex !== null && activeIndex !== 1 }" @click="handleItemClick(1)">
      <div class="info-label">
        <span class="label-title">您更希望被称呼为</span>
        <span class="label-value" :class="{ unset: !nickname }">{{ nickname || '未设置' }}</span>
      </div>
      <p class="info-desc">名字同样用于记录变革...</p>
    </div>

    <div class="info-item" :class="{ 'active': activeIndex === 2, 'fall': activeIndex !== null && activeIndex !== 2 }" @click="handleItemClick(2)">
      <div class="info-label">
        <span class="label-title">您的密码</span>
        <span class="label-value unset">****</span>
      </div>
      <p class="info-desc">嘘 不能告诉别人</p>
    </div>

    <div class="info-item" :class="{ 'active': activeIndex === 3, 'fall': activeIndex !== null && activeIndex !== 3 }" @click="handleItemClick(3)">
      <div class="info-label">
        <span class="label-title">您的邮箱</span>
        <span class="label-value" :class="{ unset: !email }">{{ email || '未设置' }}</span>
      </div>
      <p class="info-desc">
        <span v-if="email">一种写信的新的方式 但是更多的时候不是用于写信</span>
        <span v-else>或许会错过些什么呢...</span>
      </p>
    </div>

    <div class="info-item" :class="{ 'active': activeIndex === 4, 'fall': activeIndex !== null && activeIndex !== 4 }" @click="handleItemClick(4)">
      <div class="info-label">
        <span class="label-title">您的手机号</span>
        <span class="label-value" :class="{ unset: !phone }">{{ phone || '未设置' }}</span>
      </div>
      <p class="info-desc">
        <span v-if="phone">一串数字 因你的存在而有意义...</span>
        <span v-else>或许会错过些什么呢...</span>
      </p>
    </div>

    <!-- 掉落中的图片提示 -->
    <div class="input-pic-box" v-if="showInputPic">
      <img
        :src="inputpic"
        alt="input hint"
        @error="handleImageError"
        @load="handleImageLoad"
      />
      <!-- 调试信息 -->
      <p class="debug-info" v-if="debugMode">图片路径: {{ inputpic }}</p>
    </div>

    <!-- 名字被选中时的提示 -->
    <div class="hint-box" v-if="showHint">
      <p class="hint-text">或许可以考虑修改称呼哦</p>
      <p class="hint-subtext">也可以联系会魔法但很懒的管理员们帮你改改名字 (・ω< )</p>
      <button class="hint-btn" @click.stop="handleContactAdmin">联系管理员</button>
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

  .reset-btn {
    position: absolute;
    top: 20px;
    right: 30px;
    padding: 8px 20px;
    background: linear-gradient(135deg, #C3272B 0%, #B91F24 100%);
    color: #fff;
    font-size: 16px;
    font-weight: 600;
    border-radius: 20px;
    cursor: pointer;
    box-shadow: 0 4px 12px rgba(195, 39, 43, 0.3);
    transition: all 0.3s ease;
    z-index: 100;
    animation: fadeIn 0.3s ease-out;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(195, 39, 43, 0.4);
      background: linear-gradient(135deg, #D63A3F 0%, #C3272B 100%);
    }

    &:active {
      transform: translateY(0);
    }
  }

  .hint-box {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
    z-index: 20;
    animation: hintFadeIn 0.6s ease-out;

    .hint-text {
      font-size: 28px;
      color: #2c2420;
      font-weight: 700;
      margin-bottom: 10px;
      text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
    }

    .hint-subtext {
      font-size: 18px;
      color: #8b7d6f;
      margin-bottom: 25px;
      font-style: italic;
    }

    .hint-btn {
      padding: 12px 30px;
      background: linear-gradient(135deg, #5a4a42 0%, #6b5b4f 100%);
      color: #fff;
      font-size: 18px;
      font-weight: 600;
      border: none;
      border-radius: 25px;
      cursor: pointer;
      box-shadow: 0 4px 12px rgba(90, 74, 66, 0.3);
      transition: all 0.3s ease;
      font-family: 'Caveat', 'Indie Flower', 'Ma Shan Zheng', 'ZCOOL KuaiLe', cursive, sans-serif;

      &:hover {
        transform: translateY(-2px) scale(1.05);
        box-shadow: 0 6px 16px rgba(90, 74, 66, 0.4);
        background: linear-gradient(135deg, #6b5b4f 0%, #7c6b5f 100%);
      }

      &:active {
        transform: translateY(0) scale(1);
      }
    }
  }

  .input-pic-box {
    opacity: 0.3;
    width: 700px;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 15;
    animation: picFadeIn 0.4s ease-out;

    img {
      width: 100%;
      height: auto;
      filter: drop-shadow(0 4px 12px rgba(0, 0, 0, 0.15));
    }

    .debug-info {
      margin-top: 10px;
      font-size: 14px;
      color: #C3272B;
      background: rgba(255, 255, 255, 0.9);
      padding: 8px 12px;
      border-radius: 8px;
      word-break: break-all;
    }
  }

  .info-item {
    position: absolute;
    padding: 15px 20px;
    opacity: 0;
    animation: fadeIn 0.8s ease-out forwards;
    cursor: pointer;
    transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);

    // 掉落动画
    &.fall {
      animation: fallDown 0.8s ease-in forwards !important;

      .info-label .label-title::after {
        opacity: 0;
      }

      .info-desc {
        opacity: 0;
        transform: translateY(-10px);
        transition: all 0.3s ease;
      }
    }

    // 激活状态
    &.active {
      top: 2% !important;
      left: 3% !important;
      right: auto !important;
      bottom: auto !important;
      transform: rotate(0deg) !important;
      animation: activeZoom 0.6s ease-out forwards !important;
      z-index: 10;

      .info-desc {
        opacity: 0;
        transform: translateY(-10px);
        transition: all 0.3s ease;
      }
    }

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

@keyframes fallDown {
  0% {
    opacity: 1;
    transform: translateY(0);
  }
  100% {
    opacity: 0;
    transform: translateY(120vh) rotate(20deg);
  }
}

@keyframes activeZoom {
  0% {
    opacity: 0;
    transform: scale(0.8);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
}

@keyframes hintFadeIn {
  0% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(0.8);
  }
  100% {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1);
  }
}

@keyframes picFadeIn {
  0% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(0.9);
  }
  100% {
    opacity: 0.3;
    transform: translate(-50%, -50%) scale(1);
  }
}
</style>
