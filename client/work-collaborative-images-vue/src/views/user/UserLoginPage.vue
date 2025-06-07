<script lang="ts" setup>
/**
 * 用户登录页面
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
import { reactive } from 'vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { useRouter } from 'vue-router'
import { userLogin } from '@/api/work-collaborative-images/userController.ts'
import { message } from 'ant-design-vue'

// 用于存储用户输入的表单
const formState = reactive<WorkCollaborativeImagesAPI.UserLoginRequest>({
  account: '',
  passwd: '',
})

// 用于响应提交表单的行为
const router = useRouter()
const loginUserStore = useLoginUserStore()
const handleSubmit = async (values: WorkCollaborativeImagesAPI.UserLoginRequest) => {
  const res = await userLogin(values)
  if (res.data.code === 20000 && res.data.data) {
    await loginUserStore.setLoginUser(res.data.data)
    message.success('登入成功')
    await router.push({
      path: '/',
      replace: true,
    })
  } else {
    message.error(res.data.message)
  }
}
</script>

<template>
  <div id="userLoginPage">
    <!-- 标语 -->
    <h2 class="title">工作室协作图库 - 用户登录</h2>
    <div class="desc">欢迎登录</div>
    <!-- 表单 -->
    <a-form :model="formState" autocomplete="off" name="basic" @finish="handleSubmit">
      <!-- 账户输入框 -->
      <a-form-item
        :rules="[
          { required: true, message: '请输入账号' },
          { min: 5, message: '账户不能小于 5 位' },
        ]"
        name="account"
      >
        <a-input
          v-model:value="formState.account"
          autocomplete="username"
          placeholder="请输入账号"
        />
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
      <!-- 提示文本 -->
      <div class="tips">
        没有账号？
        <RouterLink to="/user/register">去注册</RouterLink>
      </div>
      <!-- 跳转登录 -->
      <a-form-item>
        <a-button html-type="submit" style="width: 100%" type="primary">登录</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<style scoped>
#userLoginPage {
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
