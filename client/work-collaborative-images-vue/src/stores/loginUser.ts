import { defineStore } from 'pinia'
import { ref } from 'vue'
import { userGetSession } from '@/api/work-collaborative-images/userController.ts'

/**
 * 定义登录用户状态
 */
export const useLoginUserStore = defineStore('loginUser', () => {
  // 默认值
  const loginUser = ref<WorkCollaborativeImagesAPI.UserVO>({
    account: '尚未登录',
  })

  // 获取登录用户
  async function getLoginUser() {
    const res = await userGetSession()
    if (res.data.code === 20000 && res.data.data) {
      loginUser.value = res.data.data
      console.debug("获取当前登录的用户信息成功, 已存入状态管理器中")
    }
  }

  // 设置登录用户
  async function setLoginUser(newLoginUser: WorkCollaborativeImagesAPI.UserVO) {
    loginUser.value = newLoginUser
  }

  return { loginUser, setLoginUser, getLoginUser }
})
