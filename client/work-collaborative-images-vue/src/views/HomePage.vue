<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'
import PictureOverview from '@/components/PictureOverview.vue'
import { pictureCategorys, pictureQuery } from '@/api/work-collaborative-images/pictureController.ts'
import { onMounted } from 'vue'

const categoryList = ref<string[]>([])

const pagination = reactive({
  pageCurrent: 1,
  pageSize: 10,
  total: 0,
})

const dataList = ref<WorkCollaborativeImagesAPI.PictureVO[]>([])
const loading = ref(false)

const getTagCategoryOptions = async () => {
  const res = await pictureCategorys()
  if (res.data.code === 20000 && res.data.data) {
    categoryList.value = res.data.data ?? []
  } else {
    message.error(res.data.message)
  }
}
getTagCategoryOptions()

const getTableData = async (params: any) => {
  loading.value = true
  const res = await pictureQuery(params)
  if (res.data.code === 20000 && res.data.data && res.data.data.records) {
    dataList.value = res.data.data.records ?? []
    pagination.total = res.data.data.total ?? 0
  } else {
    message.error(res.data.message)
  }
  loading.value = false
}

onMounted(async () => {
  await getTagCategoryOptions()
  await getTableData({
    pageCurrent: pagination.pageCurrent,
    pageSize: pagination.pageSize,
    introduction: '',  // 默认搜索词空
    category: undefined, // 默认全部分类
  })
})
</script>

<template>
  <div>
    <PictureOverview
      :category-list="categoryList"
      :pagination="pagination"
      :data-list="dataList"
      :loading="loading"
      @search="getTableData"
    />
  </div>
</template>
