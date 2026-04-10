<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addFeedbackAPI } from '@/api/feedback.js'
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
  console.log('提交反馈:', feedbackForm.value)
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
    <!-- 左侧：可滚动的卡片区域 -->
    <div class="left-panel">
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

    <!-- 右侧：固定不滚动的表单区域 -->
    <div class="right-panel">
      <el-card class="feedback-form-card">
        <template #header>
          <div class="form-header">
            <span>📨 向管理员反馈</span>
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
    </div>
  </div>
</template>

<style scoped>
.help-container {
  display: flex;
  height: calc(100vh - 60px);
  gap: 20px;
  padding: 20px;
  background-color: #FFF8E8;
}

/* 左侧面板 - 可滚动 */
.left-panel {
  flex: 1;
  overflow-y: auto;
  padding-right: 10px;
}

.cards-wrapper {
  display: flex;
  flex-direction: column;
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

/* 右侧面板 - 不可滚动 */
.right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.feedback-form-card {
  height: 100%;
  background-color: #FFF8E8;
  border: 1px solid #FFEFD5;
  display: flex;
  flex-direction: column;
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
.left-panel::-webkit-scrollbar {
  width: 8px;
}

.left-panel::-webkit-scrollbar-track {
  background: #FFEFD5;
  border-radius: 4px;
}

.left-panel::-webkit-scrollbar-thumb {
  background: #AAA;
  border-radius: 4px;
}

.left-panel::-webkit-scrollbar-thumb:hover {
  background: #666;
}

/* 表单项样式优化 */
:deep(.el-form-item__label) {
  color: #333333;
  font-weight: 500;
}

:deep(.el-textarea__inner) {
  background-color: #FFF8E8;
  border-color: #FFEFD5;
  color: #333333;
  resize: none;
}

:deep(.el-textarea__inner:focus) {
  border-color: #C3272B;
}

:deep(.el-select .el-input__wrapper) {
  background-color: #FFF8E8;
  border-color: #FFEFD5;
}

:deep(.el-select .el-input__wrapper:hover) {
  border-color: #AAA;
}

:deep(.el-select .el-input__wrapper.is-focus) {
  border-color: #C3272B;
}
</style>
