<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addFeedbackAPI } from '@/api/feedback.js'
import router from '@/router/index.js'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user.js'

const userStore = useUserStore()

// 控制蒙层显示状态
const showOverlay = ref(true)

// 点击蒙层隐藏
const hideOverlay = () => {
  showOverlay.value = false
}

const goBack = () => {
  const status = Number(userStore.userInfo.status)
  if(isNaN(status) || status === undefined){
    router.replace('/login')
  }
  else if(status >= 2){
    router.replace('/admin')
  }
  else if(status < 2 && status > 0){
    router.replace('/user')
  }else{
    router.replace('/login')
  }
}
const defaultContent = `___________________________________________________
以下是一些提示信息 建议在提交时删除
1.您的反馈将以邮件的形式发送给管理员们 在ta们处理后同样会以邮件的形式通知您 我们将尽可能地处理您反馈的问题 但会驳回不合理的请求
2.强烈建议您在注册用户时提交您的邮箱信息以便关注反馈的处理进度
3.建议您使用以下格式来反馈问题...当然也可以删掉自己写 给您最大的自由度进行反馈哦~
___________________________________________________
1.问题位置:
您在什么模块下遇到了问题?登录页面/记账模块/管理员模块/...
2.问题描述:
您遇到了什么样的问题?例如忘记密码无法登录/程序报了什么错...
3.操作流程:
您进行了什么样的操作?
`

// 反馈表单数据
const feedbackForm = ref({
  type: '',
  content: defaultContent
})

// 反馈类型选项
const feedbackTypes = [
  { label: 'Bug反馈', value: 'Bug反馈' },
  { label: '登录问题', value: '登录问题' },
  // 后续可以继续添加其他类型
]

// 提交反馈
const submitFeedback = async () => {
  if (!feedbackForm.value.type) {
    ElMessage.warning('请选择反馈类型')
    return
  }
  if (!feedbackForm.value.content || feedbackForm.value.content.trim() === '') {
    ElMessage.warning('请填写反馈内容')
    return
  }
  await addFeedbackAPI(feedbackForm.value.type,feedbackForm.value.content)
  ElMessage.success('我已收到!')

  // 重置表单
  feedbackForm.value = {
    type: '',
    content: defaultContent
  }
}
</script>

<template>
  <div class="help-container">
    <!-- 返回按钮 -->
    <el-button
      class="back-button"
      circle
      @click="goBack"
    >
      <el-icon><ArrowLeft /></el-icon>
    </el-button>

    <!-- 上部：反馈表单区域 -->
    <div class="form-section">
      <!-- 蒙层 -->
      <transition name="fade-overlay">
        <div v-if="showOverlay" class="form-overlay" @click="hideOverlay">
          <div class="overlay-content">
            <span class="overlay-text">
              向管理员求助<br>
              <small style="font-size: 15px">常见问题看下面哦</small>
            </span>
          </div>
        </div>
      </transition>

      <!-- 表单内容 -->
      <transition name="fade-form">
        <el-card v-if="!showOverlay" class="feedback-form-card">
        <template #header>
          <div class="form-header">
            <span>向管理员反馈</span>
          </div>
        </template>

        <el-form :model="feedbackForm" label-width="100px" class="feedback-form">
          <el-form-item label="反馈类型" required>
            <el-select
              v-model="feedbackForm.type"
              placeholder="请选择反馈类型"
              style="width: 100%"
            >
              <el-option
                v-for="item in feedbackTypes"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="反馈内容" required>
            <el-input
              v-model="feedbackForm.content"
              type="textarea"
              :rows="20"
              placeholder="请详细描述您遇到的问题或建议..."
              maxlength="2000"
              show-word-limit
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submitFeedback" class="submit-btn">
              提交并肘击管理员!
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
      </transition>
    </div>

    <!-- 下部：信息卡片区域 -->
    <div class="info-section">
      <div class="cards-wrapper">
        <el-card class="info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>💡 常见问题</span>
            </div>
          </template>
          <div class="card-content">
            <p>Ciallo～(∠・ω&lt; )⌒★</p>
            <p>非常抱歉...一五馄饨这个软件是芜柒sama的小作坊开发的 技术/人手不太够 所以可能存在一些bug哦...</p>
            <p>如果您遇到问题，可以先查看以下常见问题：</p>
            <ul>
              <li>如何注册账号？</li>
              <li>忘记密码怎么办？</li>
              <li>如何联系管理员？</li>
            </ul>
          </div>
        </el-card>

        <!-- 这里可以添加更多卡片 -->
        <el-card class="info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <span>📝 使用提示</span>
            </div>
          </template>
          <div class="card-content">
            <p>这里是更多内容展示区域，您可以根据需要添加卡片。</p>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.help-container {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  padding: 20px;
  background-color: transparent;
  overflow-y: auto;
  z-index: 1;
}

/* 返回按钮 */
.back-button {
  position: fixed;
  top: 30px;
  left: 30px;
  width: 50px;
  height: 50px;
  background-color: #C3272B;
  border-color: #C3272B;
  z-index: 10;
  box-shadow: 0 2px 8px rgba(195, 39, 43, 0.3);
}

.back-button:hover {
  background-color: #D64A4F;
  border-color: #D64A4F;
  transform: scale(1.05);
}

.back-button :deep(.el-icon) {
  color: white;
  font-size: 20px;
}

/* 下部：信息卡片区域 */
.info-section {
  order: 2;
  width: 100%;
  margin-top: 20px;
}

.cards-wrapper {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.info-card {
  background-color: #FFF8E8;
  border: 1px solid #FFEFD5;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #333333;
}

.card-content {
  color: #454545;
  line-height: 1.8;
}

.card-content p {
  margin: 10px 0;
}

.card-content ul {
  margin: 10px 0;
  padding-left: 20px;
}

.card-content li {
  margin: 5px 0;
  color: #5A4A42;
}

/* 上部：反馈表单区域 */
.form-section {
  order: 1;
  width: 70%;
  max-width: 900px;
  margin: 15px auto;
  display: flex;
  flex-direction: column;
  position: relative;
  min-height: 660px;
}

.feedback-form-card {
  width: 100%;
  min-height: 600px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  border: none;
  display: flex;
  flex-direction: column;
}

/* 蒙层样式 */
.form-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 5;
  border-radius: 4px;
  overflow: hidden;
}

.form-overlay::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: url('@/assets/img/help/envelope.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  opacity: 0.5;
  z-index: -1;
}

.overlay-content {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.overlay-text {
  text-align: center;
  font-size: 32px;
  font-weight: bold;
  color: #C3272B;
  text-shadow: 2px 2px 4px rgba(255, 255, 255, 0.8);
  user-select: none;
  margin-top: 250px;
}

/* 蒙层渐变动画 */
.fade-overlay-enter-active,
.fade-overlay-leave-active {
  transition: opacity 0.5s ease;
}

.fade-overlay-enter-from,
.fade-overlay-leave-to {
  opacity: 0;
}

/* 表单渐变动画 */
.fade-form-enter-active,
.fade-form-leave-active {
  transition: opacity 0.5s ease;
}

.fade-form-enter-from,
.fade-form-leave-to {
  opacity: 0;
}

.form-header {
  font-size: 18px;
  font-weight: bold;
  color: #333333;
}

.feedback-form {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.submit-btn {
  width: 100%;
  background-color: #C3272B;
  border-color: #C3272B;
  font-size: 16px;
  padding: 12px 24px;
}

.submit-btn:hover {
  background-color: #D64A4F;
  border-color: #D64A4F;
}

/* 自定义滚动条样式 */
.help-container::-webkit-scrollbar {
  width: 8px;
}

.help-container::-webkit-scrollbar-track {
  background: #FFEFD5;
  border-radius: 4px;
}

.help-container::-webkit-scrollbar-thumb {
  background: #AAA;
  border-radius: 4px;
}

.help-container::-webkit-scrollbar-thumb:hover {
  background: #666;
}

.info-section::-webkit-scrollbar {
  width: 8px;
}

.info-section::-webkit-scrollbar-track {
  background: #FFEFD5;
  border-radius: 4px;
}

.info-section::-webkit-scrollbar-thumb {
  background: #AAA;
  border-radius: 4px;
}

.info-section::-webkit-scrollbar-thumb:hover {
  background: #666;
}

/* 表单项样式优化 */
:deep(.el-form-item__label) {
  color: #333333;
  font-weight: 500;
}

:deep(.el-textarea__inner) {
  background-color: rgba(255, 248, 232, 0.9);
  border-color: #FFEFD5;
  color: #333333;
  resize: none;
}

:deep(.el-textarea__inner:focus) {
  border-color: #C3272B;
}

:deep(.el-select .el-input__wrapper) {
  background-color: rgba(255, 248, 232, 0.9);
  border-color: #FFEFD5;
}

:deep(.el-select .el-input__wrapper:hover) {
  border-color: #AAA;
}

:deep(.el-select .el-input__wrapper.is-focus) {
  border-color: #C3272B;
}
</style>
