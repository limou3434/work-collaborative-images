<script lang="ts" setup>
import { h, ref } from 'vue'
import { PictureOutlined, UserOutlined, TeamOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/loginUser.ts'

// 菜单配置
const menuItems = [
  {
    key: '/',
    label: '公共图库',
    icon: () => h(PictureOutlined),
  },
  {
    key: '/self',
    label: '私有空间',
    icon: () => h(UserOutlined),
  },
  {
    key: '/collaborative',
    label: '协作空间',
    icon: () => h(TeamOutlined),
  },
]

const router = useRouter()
const loginUserStore = useLoginUserStore()

// 当前选中的菜单项（初始化为当前路由）
const current = ref<string[]>([router.currentRoute.value.path])

// 路由变化时更新选中项
router.afterEach((to) => {
  current.value = [to.path]
})

// 菜单点击跳转
const doMenuClick = ({ key }: { key: string }) => {
  router.push({ path: key })
}
</script>

<template>
  <div id="globalSider">
    <a-layout-sider
      v-if="loginUserStore.loginUser.id"
      breakpoint="lg"
      class="sider"
      collapsed-width="0"
      width="200"
    >
      <a-menu
        v-model:selectedKeys="current"
        :items="menuItems"
        mode="inline"
        @click="doMenuClick"
      />
    </a-layout-sider>
  </div>
</template>

<style></style>
