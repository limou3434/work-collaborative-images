<script lang="ts" setup>
import { computed } from 'vue'

interface Props {
  space?: WorkCollaborativeImagesAPI.SpaceVO
}

const props = defineProps<Props>()

const sizePercent = computed(() => {
  if (!props.space?.totalSize || !props.space?.maxSize) return 0
  return Math.min(100, (props.space.totalSize / props.space.maxSize) * 100)
})
const countPercent = computed(() => {
  if (!props.space?.totalCount || !props.space?.maxCount) return 0
  return Math.min(100, (props.space.totalCount / props.space.maxCount) * 100)
})
</script>

<template>
  <div class="SpaceDashboard">
    <a-row :gutter="[16, 16]" style="margin-bottom: 24px">
      <a-col :xs="24" :sm="12" :md="6">
        <a-card>
          <a-statistic title="图片总数(个)" :value="space?.totalCount ?? 0" />
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :md="6">
        <a-card>
          <a-statistic title="空间容量(MB)" :value="(space?.totalSize ?? 0) / 1024 / 1024" :precision="2" />
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :md="6">
        <a-card>
          <a-statistic title="最大图片数(个)" :value="space?.maxCount ?? 0" />
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :md="6">
        <a-card>
          <a-statistic title="最大容量(MB)" :value="(space?.maxSize ?? 0) / 1024 / 1024" :precision="2" />
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="[24, 24]" justify="center">
      <a-col :xs="24" :sm="24" :md="12" style="text-align: center">
        <a-card title="图片大小使用情况">
          <a-progress
            type="circle"
            :percent="sizePercent"
            :status="sizePercent >= 100 ? 'exception' : 'normal'"
            :format="() =>
          !space?.totalSize || !space?.maxSize
            ? '0%'
            : ((space.totalSize / space.maxSize) * 100).toFixed(1) + '%'"
          />
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="24" :md="12" style="text-align: center">
        <a-card title="图片数量使用情况">
          <a-progress
            type="circle"
            :percent="countPercent"
            :status="countPercent >= 100 ? 'exception' : 'normal'"
            :format="() =>
          !space?.totalCount || !space?.maxCount
            ? '0%'
            : ((space.totalCount / space.maxCount) * 100).toFixed(1) + '%'"
          />
        </a-card>
      </a-col>
    </a-row>

  </div>
</template>

<style scoped>
.SpaceDashboard {
  padding: 24px;
}
</style>
