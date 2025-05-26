<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import PictureOverview from '@/components/PictureOverview.vue'
import { pictureQuery } from '@/api/work-collaborative-images/pictureController.ts'

// 设置从后端请求渲染到页面上的图片
const pagination = reactive({
  pageCurrent: 1,
  pageSize: 10,
  total: 0,
}) // 存储获取到的分页数据
const dataList = ref<WorkCollaborativeImagesAPI.PictureVO[]>([]) // 存储图片列表
const loading = ref(false) // 存储加载状态标识
const getTableData = async (params: WorkCollaborativeImagesAPI.PictureQueryRequest) => {
  loading.value = true
  const res = await pictureQuery(params)
  if (res.data.code === 20000 && res.data.data && res.data.data.records) {
    dataList.value = res.data.data.records ?? []
    pagination.total = res.data.data.total ?? 0
  } else {
    message.error(res.data.message)
  }
  loading.value = false
} // 请求回调
onMounted(async () => {
  await getTableData({
    pageCurrent: pagination.pageCurrent,
    pageSize: pagination.pageSize,
    introduction: '', // 默认搜索词空
    category: undefined, // 默认全部分类
  })
}) // 每次页面渲染都调用获取图片分页的回调
</script>

<template>
  <div>
    <PictureOverview
      :data-list="dataList"
      :loading="loading"
      :pagination="pagination"
      @search="getTableData"
    />
  </div>
</template>
