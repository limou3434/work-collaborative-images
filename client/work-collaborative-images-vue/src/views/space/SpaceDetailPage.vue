<script lang="ts" setup>
/**
 * 空间详情页面
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
import SpaceDetails from '@/components/SpaceOverview.vue'
import { spaceQueryById } from '@/api/work-collaborative-images/spaceController.ts'
import { ref, watch } from 'vue'
import { message } from 'ant-design-vue'

// NOTE: 变量

const props = defineProps<{
  id?: number
}>() // 存储外部属性
const spaceVO = ref<WorkCollaborativeImagesAPI.SpaceVO>()

// NOTE: 调用

// 获取私有空间的调用
const handGetSpace = async () => {
  // 获取用户空间信息
  const res = await spaceQueryById({
    spaceId: Number(props.id)
  })
  if (res.data.code === 20000 && res.data.data) {
    spaceVO.value = res.data.data
    message.success('获取空间成功')
  } else {
    message.error(res.data.message)
  }
}

// NOTE: 监控

watch(
  () => props.id, // 监听变量
  () => { // 监听回调
    handGetSpace()
  },
  { immediate: true } // 立即执行
)
</script>

<template>
  <div id="SpaceDetailPage">
    <SpaceDetails :spaceVO="spaceVO"></SpaceDetails>
  </div>
</template>

<style scoped></style>
