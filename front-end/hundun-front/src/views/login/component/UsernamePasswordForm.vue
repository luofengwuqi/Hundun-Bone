<script setup>
import { reactive, ref } from 'vue'
import { loginByUsernameAndPassword } from '@/api/user.js'
import { useUserStore } from '@/stores/user.js'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

//表单相关
const form = reactive({
  username: '',
  password: '',
})
//表单验证相关
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 1, max: 10, message: '长度在 1 到 10 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6,max:20, message: '长度在 6 到 20 个字符', trigger: 'blur' },
  ]
}
//按钮相关
const userStore = useUserStore()
const router = useRouter()
const formRef = ref(null)
const handleLogin = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      console.log('验证通过，提交登录', form)
      const res = await loginByUsernameAndPassword(form.username, form.password)
      // res.data:token/user
      //操作pinia
      userStore.setToken(res.data.token)
      userStore.setUserInfo(res.data.user)
      ElMessage.success(`Ciallo～(∠・ω< )⌒★${userStore.userInfo?.nickname || userStore.userInfo?.username || '不知名的朋友'}`)
      await new Promise(resolve => setTimeout(resolve, 1500))
      //路由跳转
      if(userStore.userInfo.status === 3 || userStore.userInfo.status === 2){
        await router.push('/admin')
      }
      else{
        await router.push('/user')
      }



    } else {
      console.log('表单验证失败')
      return false
    }
  })

}
  //TODO: 测试方法 最后要禁用
  const adminLogin = async ()=>{
    const res = await loginByUsernameAndPassword('admin', '15hundun')
    // res.data:token/user
    //操作 pinia
    userStore.setToken(res.data.token)
    userStore.setUserInfo(res.data.user)
    ElMessage.success(`Ciallo～(∠・ω< )⌒★${userStore.userInfo?.nickname || userStore.userInfo?.username || '不知名的朋友'}`)
    // 等待状态更新和消息显示
    await new Promise(resolve => setTimeout(resolve, 1500))
    //路由跳转
      await router.push('/admin')

  }




</script>
<template>
  <div class="form">
    <div class="logo-box">
      <img src="../../../assets/img/common/logotext.png" alt="这是logo 按理来说不会显示不出来 哈哈">
    </div>
    <el-form
      :model="form"
      :rules="rules"
      ref="formRef"
      label-width="auto"
      style="max-width: 600px">
    <el-form-item prop="username">
      <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="user" >
      </el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input v-model="form.password" placeholder="请输入密码" prefix-icon="lock"></el-input>
    </el-form-item>
    <div class="buttons">
      <el-form-item class="button-group">
        <el-button type="primary" @click="handleLogin">登录</el-button>
        <el-button type="primary" @click="handleRegister">注册</el-button>
<!--        TODO : 这里后续要删除-->
        <el-button type="primary" @click="adminLogin">测试按钮:管理员登录</el-button>
        <el-button type="primary">测试按钮:用户登录</el-button>
      </el-form-item>
    </div>
      <router-link to="/help">忘记密码</router-link>
    </el-form>

  </div>
</template>

<style scoped lang="less">
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
      object-fit: contain;
    }

  }
  a{
    display: block;
    width: 20%;
    margin-left: 80%;
    color: #AAA;
  }
  a:hover{
    color: #666;
  }
  /* 专门放大表单标签字体 */
  :deep(.el-form-item) {
    margin-bottom: 24px;
  }
  :deep(.el-input__inner) {
    font-size: 15px;
    height: 45px;
  }

  .button-group{
    display: flex;
    flex-direction: row;
    align-items: center;
    width: 100%;
    justify-content: space-between;
    .el-button{
      width: 48%;
      background-color: #C3272B;
      border: none;
    }
    .el-button:hover{
      background-color: #D64A4F;
    }



  }
}
</style>
