<script lang="ts" setup>
/**
 * 图片概览组件
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
import { reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { pictureCategorys } from '@/api/work-collaborative-images/pictureController.ts'
import { message } from 'ant-design-vue'

// NOTE: 变量

const props = defineProps<{
  dataList: WorkCollaborativeImagesAPI.PictureVO[]
  loading: boolean
  pagination: {
    pageCurrent: number
    pageSize: number
    total: number
  }
}>()
const emit = defineEmits<{
  (e: 'search', params: any): void
}>()
const router = useRouter()
const selectedCategory = ref('all')
const searchKeyword = ref('')
const localPagination = reactive({
  pageCurrent: props.pagination.pageCurrent || 1,
  pageSize: props.pagination.pageSize || 10
})

// NOTE: 调用

const doSearch = () => {
  const params = {
    introduction: searchKeyword.value,
    category: selectedCategory.value === 'all' ? undefined : selectedCategory.value,
    pageCurrent: localPagination.pageCurrent,
    pageSize: localPagination.pageSize
  }
  emit('search', params)
}

const onCategoryChange = (key: string) => {
  selectedCategory.value = key
  localPagination.pageCurrent = 1
  doSearch()
}

const onSearchInput = () => {
  localPagination.pageCurrent = 1
  doSearch()
}

const doClickPicture = (picture: WorkCollaborativeImagesAPI.PictureVO) => {
  router.push({ path: `/picture/${picture.id}` })
}

// 获取标签数据的回调
const categoryList = ref<string[]>([])
const getTagCategoryOptions = async () => {
  const res = await pictureCategorys()
  if (res.data.code === 20000 && res.data.data) {
    categoryList.value = res.data.data ?? []
  } else {
    message.error(res.data.message)
  }
}

// NOTE: 监听

watch(
  () => [localPagination.pageCurrent, localPagination.pageSize], // 监听数据
  () => { // 对应回调
    doSearch()
  }
)

watch(
  () => props.pagination.pageCurrent,
  (val) => {
    if (val && val !== localPagination.pageCurrent) localPagination.pageCurrent = val
  }
)

watch(
  () => props.pagination.pageSize,
  (val) => {
    if (val && val !== localPagination.pageSize) localPagination.pageSize = val
  }
)

watch(
  () => props, // 监听数据
  async () => { // 对应回调
    await getTagCategoryOptions()
  },
  { immediate: true } // 立即执行
)
</script>

<template>
  <div id="PictureOverview">
    <div class="search-bar" style="max-width: 480px; margin: 0 auto 16px; text-align: center">
      <a-input-search
        v-model:value="searchKeyword"
        enter-button="搜索"
        placeholder="从海量图片中搜索"
        size="large"
        style="width: 100%; max-width: 480px"
        @search="onSearchInput"
      />
    </div>

    <a-tabs
      v-model:activeKey="selectedCategory"
      style="margin-bottom: 16px"
      @change="onCategoryChange"
    >
      <a-tab-pane key="all" tab="全部" />
      <a-tab-pane v-for="category in categoryList" :key="category" :tab="category" />
    </a-tabs>

    <a-list
      :data-source="props.dataList"
      :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"
      :loading="props.loading"
      :pagination="{
        current: localPagination.pageCurrent,
        pageSize: localPagination.pageSize,
        total: props.pagination.total,
        showTotal: (total: number) => `共 ${total} 条`,
        showSizeChanger: true,
        showQuickJumper: true,
        'onUpdate:current': (page: number) => (localPagination.pageCurrent = page),
        'onUpdate:pageSize': (size: number) => {
          localPagination.pageSize = size
          localPagination.pageCurrent = 1
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
                    <a-tag :color="picture.category ? 'green' : 'gray'">
                      {{ picture.category || '无种类' }}
                    </a-tag>
                    <div class="tags">
                      <template v-if="JSON.parse(picture.tags ?? '[]').length > 3">
                        <a-tag
                          v-for="tag in JSON.parse(picture.tags ?? '[]').slice(0, 3)"
                          :key="tag"
                        >
                          {{ tag }}
                        </a-tag>
                        <a-tooltip :title="JSON.parse(picture.tags ?? '[]').join(', ')">
                          <a-tag>...</a-tag>
                        </a-tooltip>
                      </template>
                      <template v-else-if="JSON.parse(picture.tags ?? '[]').length > 0">
                        <a-tag v-for="tag in JSON.parse(picture.tags ?? '[]')" :key="tag">
                          {{ tag }}
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
.picture-card {
  padding: 0;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 40px;
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
