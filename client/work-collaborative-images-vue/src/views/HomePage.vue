<script lang="ts" setup>
import { message } from 'ant-design-vue'
import { nextTick, onMounted, reactive, ref } from 'vue'
import {
  pictureCategorys,
  pictureSearchVo,
} from '@/api/work-collaborative-images/pictureController.ts'
import { useRouter } from 'vue-router'

// 设置分类标签
const categoryList = ref<string[]>([])
const selectedCategory = ref<string>('all')
// 获取标签和分类选项
const getTagCategoryOptions = async () => {
  const res = await pictureCategorys()
  if (res.data.code === 20000 && res.data.data) {
    // 转换成下拉选项组件接受的格式
    categoryList.value = res.data.data ?? []
  } else {
    message.error('加载分类标签失败，' + res.data.message)
  }
}
onMounted(() => {
  getTagCategoryOptions()
})

// 设置分页组件
const pagination = reactive({
  pageCurrent: 1,
  pageSize: 10,
  total: 0,
  showTotal: (total: number) => `共 ${total} 条`,
  showSizeChanger: true,
  showQuickJumper: true,
})

// 获取所有的图片数据
const searchParams = reactive<WorkCollaborativeImagesAPI.PictureSearchRequest>({
  sortOrder: 'inverted',
  pageCurrent: 1,
  pageSize: 10,
}) // 初始化搜索参数
const dataList = ref<WorkCollaborativeImagesAPI.PictureVO[]>([])
const loading = ref(true)
const getTableData = async () => {
  loading.value = true
  const res = await pictureSearchVo(searchParams)
  if (res.data.data) {
    dataList.value = res.data.data.records ?? []
    pagination.total = res.data.data.total ?? 0
  } else {
    message.error(res.data.message)
  }
  loading.value = false
}
onMounted(() => {
  getTableData()
}) // 页面加载时执行

// 设置搜索回调
const doSearch = () => {
  searchParams.category = selectedCategory.value === 'all' ? undefined : selectedCategory.value
  searchParams.pageCurrent = 1
  pagination.pageCurrent = 1
  getTableData()
}

// 跳转至图片详情
const router = useRouter()
const doClickPicture = (picture: WorkCollaborativeImagesAPI.PictureVO) => {
  router.push({
    path: `/picture/${picture.id}`,
  })
}

// 懒加载图片
const setupLazyLoad = () => {
  const lazyImages = document.querySelectorAll('img.lazy')
  const observer = new IntersectionObserver((entries, observer) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        const img = entry.target as HTMLImageElement // TypeScript 现在知道是图片元素
        img.src = img.dataset.src ?? '' // 将 data-src 的值赋给 src 属性
        img.classList.remove('lazy')
        observer.unobserve(img) // 停止观察已加载的图片
      }
    })
  }) // 创建 Intersection Observer
  lazyImages.forEach((image) => observer.observe(image)) // 观察每个图片
}
onMounted(async () => {
  await getTagCategoryOptions()
  await getTableData()
  await nextTick()
  setupLazyLoad()
})
</script>

<template>
  <div class="homePage">
    <!-- 搜索组件 -->
    <div class="search-bar">
      <a-input-search
        v-model:value="searchParams.introduction"
        enter-button="搜索"
        placeholder="从海量图片中搜索"
        size="large"
        @search="doSearch"
      />
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
                <!-- <img :alt="picture.name" :src="picture.url" class="image" /> -->
                <img :alt="picture.name" :data-src="picture.url" class="image lazy" src="" />
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
.homePage {
  margin-bottom: 16px;
}

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
