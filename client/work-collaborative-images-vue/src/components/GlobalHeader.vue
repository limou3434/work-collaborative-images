<script lang="ts" setup>
import { computed, h, ref } from 'vue'
import {
  CrownOutlined,
  HomeOutlined,
  LogoutOutlined,
  PictureOutlined,
  UserOutlined,
  QuestionCircleOutlined,
  SnippetsOutlined,
} from '@ant-design/icons-vue'
import { type MenuProps, message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { userLogout } from '@/api/work-collaborative-images/userController.ts'

// 设置页面菜单并且做页面权限校验
const originItems = [
  {
    key: '/',
    title: '主页',
    label: '主页',
    icon: () => h(HomeOutlined),
  },
  {
    key: '/operate',
    title: '添加',
    label: '添加',
    icon: () => h(PictureOutlined),
    children: [
      {
        key: '/operate/picture/add',
        title: '添加图片',
        label: '添加图片',
      },
      {
        key: '/operate/space/add',
        title: '添加空间',
        label: '添加空间',
      },
    ],
  },
  {
    key: '/admin',
    title: '管理',
    label: '管理',
    icon: () => h(CrownOutlined),
    children: [
      {
        key: '/admin/user',
        title: '用户管理',
        label: '用户管理',
      },
      {
        key: '/admin/picture',
        title: '图片管理',
        label: '图片管理',
      },
      {
        key: '/admin/space',
        title: '空间管理',
        label: '空间管理',
      },
    ],
  },
  {
    key: '/about',
    title: '关于',
    label: '关于',
    icon: () => h(QuestionCircleOutlined),
  },
  {
    key: 'others',
    title: '博客',
    label: h(
      'a',
      {
        href: 'https://limou3434.github.io/work-blog-website/',
        target: '_blank',
      },
      '博客',
    ),
    icon: () => h(SnippetsOutlined),
  },
] // 在不考虑权限的情况下设置跳转菜单选项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    if (typeof menu?.key === 'string' && menu?.key.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.role !== 1) {
        return false
      }
    }
    return true
  })
} // 根据权限来过滤菜单项
const items = computed<MenuProps['items']>(() => filterMenus(originItems)) // 展示在菜单的路由数组

// 点击菜单后对应跳转事件
const doMenuClick = ({ key }: { key: string }) => {
  router.push({
    path: key,
  })
}

// 监听路由避免刷新后菜单没有高亮状态
const router = useRouter()
const current = ref<string[]>(['home'])
router.afterEach((to) => {
  current.value = [to.path] // 每次改变路由或刷新页面时自动更新 current 的值从而实现高亮
})

// 得到用户状态管理器用于后续维护登录状态
const loginUserStore = useLoginUserStore()

// 用户注销
const doLogout = async () => {
  const res = await userLogout()
  console.log(res)
  if (res.data.code === 20000) {
    await loginUserStore.setLoginUser({
      account: '尚未登录',
    })
    message.success('登出成功')
    await router.push('/user/login')
  } else {
    message.error(res.data.message)
  }
}
</script>

<template>
  <div id="globalHeader">
    <a-row :wrap="false">
      <!-- 网站图标 -->
      <a-col flex="200px">
        <RouterLink to="/">
          <div class="title-bar">
            <img alt="logo" class="logo" src="../assets/logo.svg" />
            <div class="title">工作室协作图库</div>
          </div>
        </RouterLink>
      </a-col>
      <!-- 菜单列表 -->
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="current"
          :items="items"
          mode="horizontal"
          @click="doMenuClick"
        />
      </a-col>
      <!-- 登录按钮 -->
      <a-col flex="120px">
        <div v-if="loginUserStore.loginUser.id">
          <a-dropdown>
            <ASpace>
              <a-avatar :src="loginUserStore.loginUser.avatar" />
              {{ loginUserStore.loginUser.account ?? '未知用户' }}
            </ASpace>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="doLogout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
                <a-menu-item>
                  <router-link to="/self">
                    <UserOutlined />
                    我的空间
                  </router-link>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
        <div v-else>
          <a-button href="/user/login" type="primary">登录</a-button>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<style scoped>
.title-bar {
  display: flex;
  align-items: center;
}

.title {
  color: black;
  font-size: 18px;
  margin-left: 16px;
}

.logo {
  height: 48px;
}
</style>
