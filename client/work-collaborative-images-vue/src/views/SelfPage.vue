<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  spaceDestroySelf,
  spaceQuerySelf,
} from '@/api/work-collaborative-images/spaceController.ts'
import { message } from 'ant-design-vue'
import PictureOverview from '@/components/PictureOverview.vue'
import { pictureQuery } from '@/api/work-collaborative-images/pictureController.ts'
import SpaceDashboard from '@/components/SpaceDashboard.vue'

// 路由对象
const router = useRouter()

// 空间信息
const space = ref<WorkCollaborativeImagesAPI.SpaceVO>()

// 图片分页状态
const pagination = reactive({
  pageCurrent: 1,
  pageSize: 10,
  total: 0,
})

// 图片数据列表和加载状态
const dataList = ref<WorkCollaborativeImagesAPI.PictureVO[]>([])
const loading = ref(false)

// 获取图片数据（关键：始终注入 spaceId）
const getTableData = async (
  params: Omit<WorkCollaborativeImagesAPI.PictureQueryRequest, 'spaceId'>,
) => {
  if (!space.value?.id) return
  loading.value = true
  const res = await pictureQuery({
    ...params,
    spaceId: space.value.id,
  })
  if (res.data.code === 20000 && res.data.data && res.data.data.records) {
    dataList.value = res.data.data.records ?? []
    pagination.total = res.data.data.total ?? 0
  } else {
    message.error(res.data.message)
  }
  loading.value = false
}

// 页面加载时
onMounted(async () => {
  // 获取用户空间信息
  const res = await spaceQuerySelf()
  if (res.data.code === 20000 && res.data.data) {
    space.value = res.data.data
    message.success('获取私有空间成功')

    // 初次加载数据
    await getTableData({
      pageCurrent: pagination.pageCurrent,
      pageSize: pagination.pageSize,
      introduction: '',
      category: undefined,
    })
  } else {
    message.warn(res.data.message)
    await router.replace('/operate/space/add/')
  }
})

// 跳转到添加私有图片的页面
const handleAddPicture = () => {
  router.push({ path: '/operate/picture/add/', query: { from: 'mySpace' } })
}

// 删除本空间的调用
const handleDelSpace = async () => {
  const res = await spaceDestroySelf()
  if (res.data.code === 20000) {
    message.success('销毁成功')
    await router.push('/self')
  } else {
    message.error(res.data.message)
  }
}
</script>

<template>
  <div id="mySpace">
    <!-- 空间信息 -->
    <a-flex justify="space-between">
      <h2>私有空间: {{ space?.name }}</h2>
      <a-space>
        <a-button type="primary" @click="handleAddPicture">添加图片到图库</a-button>
        <a-popconfirm cancel-text="取消" ok-text="确认" title="确认销毁?" @confirm="handleDelSpace">
          <a-button danger type="default">销毁本图库内容</a-button>
        </a-popconfirm>
      </a-space>
    </a-flex>
    <!-- 空间仪表 -->
    <SpaceDashboard :space="space" style="margin-bottom: 24px" />
    <!-- 图片概览 -->
    <PictureOverview
      :data-list="dataList"
      :loading="loading"
      :pagination="pagination"
      @search="getTableData"
    />
  </div>
</template>

<style scoped></style>
