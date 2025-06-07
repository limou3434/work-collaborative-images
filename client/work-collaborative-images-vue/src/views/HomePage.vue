<script lang="ts" setup>
/**
 * 主页页面
 * 生命周期:
 * onBeforeMount() 组件挂载前执行
 * onMounted() 组件挂载后执行
 * onBeforeUpdate() 组件更新前执行
 * onUpdated() 组件更新后执行
 * onBeforeUnmount() 组件销毁前执行
 * onUnmounted() 组件销毁后执行
 * onActivated() 缓存组件 <KeepAlive> 激活时执行
 * onDeactivated() 缓存组件 <KeepAlive> 失活时执行
 * onErrorCaptured() 组件排除错误时执行
 *
 * 响应监听:
 * watchEffect() 自动收集依赖并立即执行, 适合处理副作用
 * watch() 精细监听指定的 ref/reactive 数据, 对比前后值后就可以使用调用
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import PictureOverview from '@/components/PictureOverview.vue'
import { pictureQuery } from '@/api/work-collaborative-images/pictureController.ts'

// NOTE: 变量

const pagination = reactive({
  pageCurrent: 1,
  pageSize: 10,
  total: 0
}) // 存储获取到的分页数据
const dataList = ref<WorkCollaborativeImagesAPI.PictureVO[]>([]) // 存储图片列表
const loading = ref(false) // 存储加载状态标识

// NOTE: 调用

// 获取图片数据调用
const handlerGetTableData = async (params: WorkCollaborativeImagesAPI.PictureQueryRequest) => {
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

// NOTE: 监听

onMounted(async () => {
  await handlerGetTableData({
    pageCurrent: pagination.pageCurrent,
    pageSize: pagination.pageSize,
    introduction: '', // 默认搜索词空
    category: undefined // 默认全部分类
  })
}) // 每次页面渲染都调用获取图片分页的回调
</script>

<template>
  <div class="HomePage">
    <!-- 网页标题 -->
    <a-flex justify="space-between">
      <h2>公共空间</h2>
    </a-flex>
    <!-- 图片概述 -->
    <PictureOverview />
  </div>
</template>
