<script lang="ts" setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { userRegister } from '@/api/work-collaborative-images/userController.ts'

// 用于存储用户输入的表单
const formState = reactive<WorkCollaborativeImagesAPI.UserRegisterRequest>({
  account: '',
  passwd: '',
  checkPasswd: '',
})

// 用于响应提交表单的行为
const router = useRouter()
const handleSubmit = async (values: WorkCollaborativeImagesAPI.UserRegisterRequest) => {
  // 判断两次输入的密码是否一致
  if (formState.passwd !== formState.checkPasswd) {
    message.error('两次输入的密码不一致')
    return
  }
  const res = await userRegister(values)
  // 注册成功，跳转到登录页面
  if (res.data.code === 20000 && res.data.data) {
    message.success('注册成功')
    await router.push({
      path: '/user/login',
      replace: true,
    })
  } else {
    message.error(res.data.message)
  }
}
</script>

<template>
  <div id="userRegisterPage">
    <!-- 标语 -->
    <h2 class="title">工作室协作图库 - 用户注册</h2>
    <div class="desc">欢迎注册</div>
    <!-- 表单 -->
    <a-form
      :model="formState"
      autocomplete="off"
      label-align="left"
      name="basic"
      @finish="handleSubmit"
    >
      <!-- 账户输入框 -->
      <a-form-item :rules="[{ required: true, message: '请输入账号' }]" name="account">
        <a-input v-model:value="formState.account" placeholder="请输入账号" />
      </a-form-item>
      <!-- 密码输入框 -->
      <a-form-item
        :rules="[
          { required: true, message: '请输入密码' },
          { min: 6, message: '密码不能小于 6 位' },
        ]"
        name="passwd"
      >
        <a-input-password
          v-model:value="formState.passwd"
          autocomplete="current-password"
          placeholder="请输入密码"
        />
      </a-form-item>
      <!-- 确认输入框 -->
      <a-form-item
        :rules="[
          { required: true, message: '请输入确认密码' },
          { min: 6, message: '确认密码不能小于 6 位' },
        ]"
        name="checkPasswd"
      >
        <a-input-password v-model:value="formState.checkPasswd" placeholder="请输入确认密码" />
      </a-form-item>
      <!-- 提示文本 -->
      <div class="tips">
        已有账号？
        <RouterLink to="/user/login">去登录</RouterLink>
      </div>
      <!-- 跳转注册 -->
      <a-form-item>
        <a-button html-type="submit" style="width: 100%" type="primary">注册</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<style scoped>
#userRegisterPage {
  max-width: 360px;
  margin: 0 auto;
}

.title {
  text-align: center;
  margin-bottom: 16px;
}

.desc {
  text-align: center;
  color: #bbb;
  margin-bottom: 16px;
}

.tips {
  margin-bottom: 16px;
  color: #bbb;
  font-size: 13px;
  text-align: right;
}
</style>
