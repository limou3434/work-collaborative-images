<script lang="ts" setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { spaceQuerySelf } from '@/api/work-collaborative-images/spaceController.ts'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'

const router = useRouter()
const loginUserStore = useLoginUserStore()

// 检查用户是否有个人空间
const checkUserSpace = async () => {
  const loginUser = loginUserStore.loginUser
  if (!loginUser?.id) {
    await router.replace('/user/login')
    return
  }
  // 获取用户空间信息
  const res = await spaceQuerySelf({
    pageCurrent: 1,
    pageSize: 1,
  })
  if (res.data.code === 20000 && res.data.data != null && res.data.data.records != null) {
    if (res.data.data?.records?.length > 0) {
      const space = res.data.data.records[0]
      await router.replace(`/space/${space.id}`)
    } else {
      await router.replace('/operate/space/add/')
      message.warn('请先创建空间')
    }
  } else {
    message.error('加载我的空间失败，' + res.data.message)
  }
}

// 在页面加载时检查用户空间
onMounted(() => {
  checkUserSpace()
})
</script>

<template>
  <div id="mySpace">
    <p>正在跳转, 请您稍候...</p>
  </div>
</template>

<style scoped></style>
