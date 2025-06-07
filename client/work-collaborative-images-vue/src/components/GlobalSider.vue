<script lang="ts" setup>
/**
 * 全局侧边栏组件, 用来切换多个空间或其他的已参加团队的空间
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
import { computed, h, ref, watch } from 'vue'
import { PictureOutlined, TeamOutlined, UserOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import {
  spaceUserPageMyCollaborativeSpace
} from '@/api/work-collaborative-images/spaceUserController.ts'
import { message } from 'ant-design-vue'

/// 变量 ///

const fixedMenuItems = [
  {
    label: '公共图库',
    key: '/',
    icon: () => h(PictureOutlined)
  },
  {
    label: '我的空间',
    type: 'group',
    children: [
      {
        key: '/self',
        label: '私有空间',
        icon: () => h(UserOutlined)
      },
      {
        key: '/collaborative',
        label: '协作空间',
        icon: () => h(TeamOutlined)
      }
    ]
  }
] // 存储侧边菜单数据的固定配置
const router = useRouter() // 存储路由管理器
const loginUserStore = useLoginUserStore() // 存储用户登录状态
const myCollaborativeSpace = ref<WorkCollaborativeImagesAPI.SpaceUserVO[]>([]) // 存储用户已经加入的所有协作空间

/// 调用 ///

// 菜单点击跳转的调用
const doMenuClick = ({ key }: { key: string }) => {
  router.push({ path: key })
}

// 当前选中的菜单项(初始化为当前路由)以路由变化时更新选中项调用
const current = ref<string[]>([router.currentRoute.value.path])
router.afterEach((to) => {
  current.value = [to.path]
})

// 加载当前登录用户的已加入团队空间记录调用
const fetchTeamSpaceList = async () => {
  const res = await spaceUserPageMyCollaborativeSpace()
  if (res.data.code === 20000 && res.data.data) {
    myCollaborativeSpace.value = res.data.data
  } else {
    message.error(res.data.message)
  }
}

// 计算固定侧边菜单项是否需要动态添加新的子菜单选项调用
const menuItems = computed(() => {
  // 没有团队空间，只展示固定菜单
  if (myCollaborativeSpace.value?.length < 1) {
    return fixedMenuItems
  }
  // 若有团队空间则加入这些空间作为新的侧边选项
  const myCollaborativeSpaceSubMenus = myCollaborativeSpace.value.map((spaceUser) => {
    const spaceVO = spaceUser.spaceVO
    const spaceName = spaceVO?.name ?? "未知空间"
    return {
      label: spaceName,
      key: '/space/' + spaceUser.spaceId
    }
  })
  const myCollaborativeSpaceMenuGroup = {
    label: '我的团队',
    type: 'group',
    children: myCollaborativeSpaceSubMenus
  }
  return [...fixedMenuItems, myCollaborativeSpaceMenuGroup]
})

/// 监听 ///

watch(
  () => loginUserStore.loginUser.id,
  (newId) => {
    if (newId) {
      fetchTeamSpaceList()
    }
  },
  { immediate: true }
)
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
