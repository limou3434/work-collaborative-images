<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { spaceQuerySelf } from '@/api/work-collaborative-images/spaceController.ts'
import {
  pictureCategorys,
  pictureQuery,
} from '@/api/work-collaborative-images/pictureController.ts'
import { useRouter } from 'vue-router'

// 获取从路径上得到的空间 id 参数
const props = defineProps<{
  id: number
}>()

// 根据从页面上获取到的 id 来查询图片详情
const searchParams = reactive<WorkCollaborativeImagesAPI.SpaceSearchRequest>({
  id: props.id,
}) // 存储初始化的查询参数, 后续用来做搜索请求
// 空间信息
const space = ref<WorkCollaborativeImagesAPI.SpaceVO>({
  name: '',
  totalSize: 0,
  maxSize: 0,
})

// 拉取空间详情
const fetchSpaceDetail = async () => {
  try {
    const res = await spaceQuerySelf()
    if (res.data.code === 20000 && res.data.data) {
      space.value = res.data.data
      searchParams.spaceId = space.value.id
      await getTableData()
    } else {
      message.error(res.data.message || '空间加载失败')
    }
  } catch (e: any) {
    message.error(e.message)
  }
}

// 分类标签
const categoryList = ref<string[]>([])
const selectedCategory = ref<string>('all')

const getTagCategoryOptions = async () => {
  const res = await pictureCategorys()
  if (res.data.code === 20000 && res.data.data) {
    categoryList.value = res.data.data ?? []
  } else {
    message.error(res.data.message)
  }
}

// 分页
const pagination = reactive({
  pageCurrent: 1,
  pageSize: 10,
  total: 0,
  showTotal: (total: number) => `共 ${total} 条`,
  showSizeChanger: true,
  showQuickJumper: true,
})

// 图片数据和搜索参数
const searchParams = reactive<WorkCollaborativeImagesAPI.PictureQueryRequest>({
  spaceId: undefined, // 初始为空，等加载完后设置
  sortOrder: 'inverted',
  pageCurrent: 1,
  pageSize: 10,
})

const dataList = ref<WorkCollaborativeImagesAPI.PictureVO[]>([])
const loading = ref(true)

// 拉取图片列表
const getTableData = async () => {
  if (!searchParams.spaceId) return
  loading.value = true
  try {
    const res = await pictureQuery(searchParams)
    if (res.data.data) {
      dataList.value = res.data.data.records ?? []
      pagination.total = res.data.data.total ?? 0
    } else {
      message.error(res.data.message)
    }
  } finally {
    loading.value = false
  }
}

// 搜索回调
const doSearch = () => {
  searchParams.category = selectedCategory.value === 'all' ? undefined : selectedCategory.value
  searchParams.pageCurrent = 1
  pagination.pageCurrent = 1
  getTableData()
}

// 图片详情跳转
const router = useRouter()
const doClickPicture = (picture: WorkCollaborativeImagesAPI.PictureVO) => {
  router.push({
    path: `/picture/${picture.id}`,
  })
}

// 页面初始化
onMounted(() => {
  fetchSpaceDetail()
  getTagCategoryOptions()
})
</script>

<template>
  <div id="spaceDetailPage">
    <!-- 空间信息 -->
    <a-flex justify="space-between">
      <h2>私有空间: {{ space.name }}</h2>
    </a-flex>
  </div>
</template>

<style scoped></style>
