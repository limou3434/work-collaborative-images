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
    <a-collapse>
      <a-collapse-panel key="1" header="空间仪表盘">
        <a-row :gutter="[16, 16]" style="margin-bottom: 24px">
          <a-col :md="6" :sm="12" :xs="24">
            <a-card>
              <a-statistic :value="space?.totalCount ?? 0" title="图片总数(个)" />
            </a-card>
          </a-col>
          <a-col :md="6" :sm="12" :xs="24">
            <a-card>
              <a-statistic :precision="2" :value="(space?.totalSize ?? 0) / 1024 / 1024"
                           title="空间容量(MB)" />
            </a-card>
          </a-col>
          <a-col :md="6" :sm="12" :xs="24">
            <a-card>
              <a-statistic :value="space?.maxCount ?? 0" title="最大图片数(个)" />
            </a-card>
          </a-col>
          <a-col :md="6" :sm="12" :xs="24">
            <a-card>
              <a-statistic :precision="2" :value="(space?.maxSize ?? 0) / 1024 / 1024"
                           title="最大容量(MB)" />
            </a-card>
          </a-col>
        </a-row>

        <a-row :gutter="[24, 24]" justify="center">
          <a-col :md="12" :sm="24" :xs="24" style="text-align: center">
            <a-card title="图片大小使用情况">
              <a-progress
                :format="() =>
          !space?.totalSize || !space?.maxSize
            ? '0%'
            : ((space.totalSize / space.maxSize) * 100).toFixed(1) + '%'"
                :percent="sizePercent"
                :status="sizePercent >= 100 ? 'exception' : 'normal'"
                type="circle"
              />
            </a-card>
          </a-col>
          <a-col :md="12" :sm="24" :xs="24" style="text-align: center">
            <a-card title="图片数量使用情况">
              <a-progress
                :format="() =>
          !space?.totalCount || !space?.maxCount
            ? '0%'
            : ((space.totalCount / space.maxCount) * 100).toFixed(1) + '%'"
                :percent="countPercent"
                :status="countPercent >= 100 ? 'exception' : 'normal'"
                type="circle"
              />
            </a-card>
          </a-col>
        </a-row>
      </a-collapse-panel>
    </a-collapse>
  </div>
</template>

<style scoped>
.SpaceDashboard {
  padding: 24px;
}
</style>
