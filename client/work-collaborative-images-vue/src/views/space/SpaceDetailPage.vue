<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { spaceQuerySelf } from '@/api/work-collaborative-images/spaceController.ts'
import { formatSize } from '@/utils'
import {
  pictureCategorys,
  pictureQuery,
} from '@/api/work-collaborative-images/pictureController.ts'
import { useRouter } from 'vue-router'

// 获取 props
const props = defineProps<{
  id: string | number
}>()

// 空间信息
const space = ref<WorkCollaborativeImagesAPI.SpaceVO>({
  name: '',
  totalSize: 0,
  maxSize: 0,
})

// 拉取空间详情
const fetchSpaceDetail = async () => {
  try {
    const res = await spaceQuerySelf({ id: Number(props.id) })
    if (
      res.data.code === 20000 &&
      res.data.data &&
      res.data.data.records &&
      res.data.data.records.length > 0
    ) {
      space.value = res.data.data.records[0]
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
    message.error('加载分类标签失败，' + res.data.message)
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
    <!-- 搜索组件 -->
    <div class="search-bar">
      <a-space size="middle">
        <a-input-search
          v-model:value="searchParams.introduction"
          enter-button="搜索"
          placeholder="从私有空间中搜索"
          size="large"
          @search="doSearch"
        />
        <a-button :href="`/operate/picture/add?spaceId=${id}`" target="_blank" type="primary">
          + 创建图片
        </a-button>
        <a-tooltip
          :title="`占用空间 ${formatSize(space.totalSize)} / ${formatSize(space.maxSize)}`"
        >
          <a-progress
            :percent="
              space.maxSize
                ? // @ts-expect-error TS18048
                  ((space.totalSize * 100) / space.maxSize).toFixed(1)
                : '0'
            "
            :size="42"
            type="circle"
          />
        </a-tooltip>
      </a-space>
    </div>
    <!-- 分类标签 -->
    <a-tabs v-model:activeKey="selectedCategory" @change="doSearch">
      <a-tab-pane key="all" tab="全部" />
      <a-tab-pane v-for="category in categoryList" :key="category" :tab="category" />
    </a-tabs>
    <!-- 图片列表 -->
    <a-list
      :data-source="dataList"
      :grid="{ gutter: 0, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"
      :loading="loading"
      :pagination="{
        ...pagination,
        current: pagination.pageCurrent,
        pageSize: pagination.pageSize,
        total: pagination.total,
        onChange: (page: number, pageSize: number) => {
          pagination.pageCurrent = page
          pagination.pageSize = pageSize
          searchParams.pageCurrent = page
          searchParams.pageSize = pageSize
          getTableData()
        },
      }"
    >
      <template #renderItem="{ item: picture }">
        <a-list-item>
          <a-card class="picture-card" hoverable @click="doClickPicture(picture)">
            <template #cover>
              <div class="image-wrapper">
                <img :alt="picture.name" :src="picture.url" class="image" />
                <div class="overlay">
                  <div class="info">
                    <div class="title">{{ picture.name }}</div>
                    <div class="introduction">{{ picture.introduction || '无简介' }}</div>
                    <a-tag :color="picture.category ? 'green' : 'gray'"
                      >{{ picture.category || '无种类' }}
                    </a-tag>
                    <div class="tags">
                      <template v-if="JSON.parse(picture.tags ?? '[]').length > 3">
                        <a-tag
                          v-for="tag in JSON.parse(picture.tags ?? '[]').slice(0, 3)"
                          :key="tag"
                          >{{ tag }}
                        </a-tag>
                        <a-tooltip :title="JSON.parse(picture.tags ?? '[]').join(', ')">
                          <a-tag>...</a-tag>
                        </a-tooltip>
                      </template>
                      <template v-else-if="JSON.parse(picture.tags ?? '[]').length > 0">
                        <a-tag v-for="tag in JSON.parse(picture.tags ?? '[]')" :key="tag"
                          >{{ tag }}
                        </a-tag>
                      </template>
                      <template v-else>
                        <a-tag>无标签</a-tag>
                      </template>
                    </div>
                  </div>
                </div>
              </div>
            </template>
          </a-card>
        </a-list-item>
      </template>
    </a-list>
  </div>
</template>

<style scoped>
.search-bar {
  max-width: 480px;
  margin: 0 auto 16px;
}

.introduction-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 8px; /* 添加底部间距 */
}

a-flex + a-flex {
  margin-top: 4px; /* 第二个 a-flex 和第一个之间的间距 */
}

.picture-card {
  padding: 0;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 40px; /* 加在卡片上也行 */
}

.image-wrapper {
  position: relative;
  height: 240px;
}

.image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(93, 54, 128, 0.56);
  color: #ffffff;
  padding: 8px;
  opacity: 0;
  transition: opacity 0.3s;
  font-size: 12px;
}

.picture-card:hover .overlay {
  opacity: 1;
}

.overlay .title {
  font-weight: bold;
  font-size: 14px;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.overlay .introduction {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 4px;
}

.overlay .tags {
  margin-top: 4px;
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}
</style>
