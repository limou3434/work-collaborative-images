import { useLoginUserStore } from '@/stores/loginUser.ts'
import { message } from 'ant-design-vue'
import router from '@/router'

/**
 * 全局权限校验
 */
let firstFetchLoginUser = true // 是否为首次获取登录用户
router.beforeEach(async (to, from, next) => {
  const loginUserStore = useLoginUserStore()
  let loginUser = loginUserStore.loginUser
  // 确保页面刷新, 首次加载时, 能够等后端返回用户信息后再校验权限
  if (firstFetchLoginUser) {
    await loginUserStore.getLoginUser()
    loginUser = loginUserStore.loginUser
    firstFetchLoginUser = false
  }
  const toUrl = to.fullPath
  if (toUrl.startsWith('/admin')) {
    if (!loginUser || loginUser.role !== 1) {
      message.error('当前用户没有权限, 请更换账户')
      next(`/user/login?redirect=${to.fullPath}`)
      return
    }
  }
  next()
})
