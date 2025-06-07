<script lang="ts" setup>
/**
 * 图片概览组件
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
import { onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  pictureCategorys,
  pictureQuery
} from '@/api/work-collaborative-images/pictureController.ts'
import { message } from 'ant-design-vue'

// NOTE: 变量

const props = defineProps<{
  spaceId?: number // 可选参数：空间ID
}>()
const router = useRouter()
const selectedCategory = ref('all') // 当前选中的分类标签
const searchKeyword = ref('') // 搜索关键词

// 本地分页状态（响应式对象）
const localPagination = reactive({
  pageCurrent: 1,
  pageSize: 16 // 初始值设为16（8的2倍）
})

const dataList = ref<WorkCollaborativeImagesAPI.PictureVO[]>([]) // 图片数据列表
const categoryList = ref<string[]>([]) // 分类标签列表
const total = ref(0) // 总记录数
const loading = ref(true) // 加载状态, 初始为true

// NOTE: 调用

// 获取图片数据和分类标签调用
const handlerLoadData = async (params: any = {}) => {
  loading.value = true
  try {
    // 合并参数
    const queryParams = {
      introduction: searchKeyword.value,
      category: selectedCategory.value === 'all' ? undefined : selectedCategory.value,
      pageCurrent: localPagination.pageCurrent,
      pageSize: localPagination.pageSize,
      spaceId: props.spaceId,
      ...params
    }

    // 并行请求图片数据和分类标签
    const [pictureRes, categoryRes] = await Promise.all([
      pictureQuery(queryParams),
      pictureCategorys()
    ])

    // 处理图片数据
    if (pictureRes.data.code === 20000 && pictureRes.data.data) {
      dataList.value = pictureRes.data.data.records || []
      total.value = pictureRes.data.data.total || 0
    } else {
      message.error(pictureRes.data.message || '获取图片数据失败')
    }

    // 处理分类标签
    if (categoryRes.data.code === 20000 && categoryRes.data.data) {
      categoryList.value = categoryRes.data.data || []
    } else {
      message.error(categoryRes.data.message || '获取分类数据失败')
    }
  } catch (error) {
    console.error('加载数据出错:', error)
    message.error('网络错误, 请稍后重试')
  } finally {
    loading.value = false
  }
}

// 分类标签变更时调用
const handlerOnCategoryChange = (key: string) => {
  selectedCategory.value = key
  localPagination.pageCurrent = 1 // 重置页码为1
  handlerLoadData()
}

// 搜索输入框变化时调用
const handlerOnSearchInput = () => {
  localPagination.pageCurrent = 1 // 重置页码为1
  handlerLoadData()
}

// 点击图片时跳转到详情页
const handlerDoClickPicture = (picture: WorkCollaborativeImagesAPI.PictureVO) => {
  router.push({ path: `/picture/${picture.id}` })
}

// NOTE: 监听

// 监听本地分页参数变化
watch(
  () => [localPagination.pageCurrent, localPagination.pageSize],
  () => {
    handlerLoadData()
  }
)

// 监听 spaceId 变化, 仅在存在时加载数据
watch(
  () => props.spaceId,
  () => {
    localPagination.pageCurrent = 1
    handlerLoadData()
  },
  { immediate: true } // 初始加载时检查 spaceId
)

// 初始化数据
onMounted(async () => {
  // 始终加载分类标签
  try {
    const res = await pictureCategorys()
    if (res.data.code === 20000 && res.data.data) {
      categoryList.value = res.data.data || []
    } else {
      message.error(res.data.message || '获取分类数据失败')
    }
  } catch (error) {
    console.error('获取分类标签出错:', error)
  }

  // 仅当 spaceId 存在时加载图片数据
  if (props.spaceId !== undefined) {
    await handlerLoadData()
  }
})
</script>

<template>
  <div v-if="!loading" id="PictureOverview">
    <!-- 搜索组件 -->
    <div class="search-bar" style="max-width: 480px; margin: 0 auto 16px; text-align: center">
      <a-input-search
        v-model:value="searchKeyword"
        enter-button="搜索"
        placeholder="从海量图片中搜索"
        size="large"
        style="width: 100%; max-width: 480px"
        @search="handlerOnSearchInput"
      />
    </div>
    <a-tabs
      v-model:activeKey="selectedCategory"
      style="margin-bottom: 16px"
      @change="handlerOnCategoryChange"
    >
      <a-tab-pane key="all" tab="全部" />
      <a-tab-pane v-for="category in categoryList" :key="category" :tab="category" />
    </a-tabs>

    <!-- 图片列表 -->
    <a-list
      v-if="dataList.length > 0"
      :data-source="dataList"
      :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 4, xxl: 5 }"
      :loading="loading"
      :pagination="{
        current: localPagination.pageCurrent,
        pageSize: localPagination.pageSize,
        total: total,
        showTotal: (total: number) => `共 ${total} 条`,
        showSizeChanger: true,
        showQuickJumper: true,
        pageSizeOptions: ['8', '16', '32', '64', '128'], // 8的倍数选项
        'onUpdate:current': (page: number) => (localPagination.pageCurrent = page),
        'onUpdate:pageSize': (size: number) => {
          localPagination.pageSize = size
          localPagination.pageCurrent = 1
        },
      }"
    >
      <template #renderItem="{ item: picture }">
        <a-list-item>
          <a-card class="picture-card" hoverable @click="handlerDoClickPicture(picture)">
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

    <!-- 数据为空时的提示 -->
    <div v-else class="empty-state" style="text-align: center; padding: 40px 0">
      <a-empty description="暂无图片数据" />
    </div>
  </div>

  <!-- 加载中状态 -->
  <div v-else class="loading-state" style="text-align: center; padding: 40px 0">
    <a-spin size="large" />
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
