<script setup>

import ChangeAvatar from '@/views/user/component/Info/RightSideComponent/ChangeAvatar.vue'
import Default from '@/views/user/component/Info/RightSideComponent/Default.vue'

const props = defineProps({
  operationType: {
    type: Number,
    default: 0
  }
})
</script>

<template>
  <div class="right">
    <Transition name="fade">
      <Default v-if="props.operationType === 0" key="default" />
      <ChangeAvatar v-else-if="props.operationType === 1" key="avatar" />
    </Transition>
  </div>
</template>

<style scoped lang="less">
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
