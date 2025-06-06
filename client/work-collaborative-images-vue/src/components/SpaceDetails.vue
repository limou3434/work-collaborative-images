<script lang="ts" setup>
/**
 * 空间详情组件, 可以通过外部传递的空间类型显示不同的组件, 同时根据外部传递的空间标识来显示不同的空间信息
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
import { computed, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { SPACE_TYPE_MAP } from '@/constants/space.ts'
import PictureOverview from '@/components/PictureOverview.vue'
import SpaceDashboard from '@/components/SpaceDashboard.vue'
import { pictureQuery } from '@/api/work-collaborative-images/pictureController.ts'
import { spaceDestroy } from '@/api/work-collaborative-images/spaceController.ts'

/// 变量 ///

const props = defineProps<{ // 外部属性
  spaceVO?: WorkCollaborativeImagesAPI.SpaceVO
}>()
const router = useRouter() // 路由对象
const pagination = reactive({ // 存储图片分页状态
  pageCurrent: 1,
  pageSize: 16,
  total: 0
})
const dataList = ref<WorkCollaborativeImagesAPI.PictureVO[]>([]) // 存储所有的图片数据的列表
const loading = ref(false) // 存储加载状态

/// 回调 ///

// 跳转到添加图片的页面
const handleAddPicture = () => {
  router.push({ path: '/operate/picture/add/', query: { from: 'self' } })
}

// 动态获取空间类型描述
const titleName = computed(() => {
  if (!props.spaceVO) return ''
  return SPACE_TYPE_MAP[props.spaceVO.type as 0 | 1 | 2] || ''
})

// 调用时获取指定空间中的所有图片
const getPictures = async () => {
  loading.value = true
  const res = await pictureQuery({
    spaceId: props.spaceVO?.id
  })
  if (res.data.code === 20000 && res.data.data && res.data.data.records) {
    dataList.value = res.data.data.records ?? []
    pagination.total = res.data.data.total ?? 0
  } else {
    message.error(res.data.message)
  }
  loading.value = false
}

// 删除空间的调用
const handleDeleteSpace = async () => {
  const res = await spaceDestroy({
    spaceType: props.spaceVO?.type
  })
  if (res.data.code === 20000) {
    message.success('销毁成功')
    await router.push('/')
  } else {
    message.error(res.data.message)
  }
}

/// 监控 ///

watch(
  () => props.spaceVO, // 监听数据
  (newVal) => { // 监听回调
    if (newVal?.id) { // 验证是否收取了有效数据
      getPictures()
    }
  },
  { immediate: true }           // immediate 表示“立即执行”，组件刚挂载时也会立刻执行一次回调（相当于 onMounted + watch 合体）
)
</script>

<template>
  <div id="SpaceDetails">
    <!-- 空间信息 -->
    <a-flex justify="space-between">
      <h2 style="max-width: 100%; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
        {{ titleName }} - {{ spaceVO?.name }}
      </h2>
      <a-space>
        <a-button type="primary" @click="handleAddPicture">添加图片到图库</a-button>
        <a-popconfirm cancel-text="取消" ok-text="确认" title="确认销毁?"
                      @confirm="handleDeleteSpace">
          <a-button danger type="default">销毁本图库内容</a-button>
        </a-popconfirm>
      </a-space>
    </a-flex>
    <!-- 空间仪表 -->
    <SpaceDashboard :space="spaceVO" style="margin-bottom: 24px" />
    <!-- 图片概览 -->
    <PictureOverview
      :data-list="dataList"
      :loading="loading"
      :pagination="pagination"
      @search="getPictures"
    />
  </div>
</template>

<style scoped></style>
