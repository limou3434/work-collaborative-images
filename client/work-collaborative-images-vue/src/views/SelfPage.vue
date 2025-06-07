<script lang="ts" setup>
/**
 * 私有页面
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
import SpaceDetails from '@/components/SpaceOverview.vue'
import { SPACE_TYPE_ENUM } from '@/constants/space.ts'
import { spaceQuery } from '@/api/work-collaborative-images/spaceController.ts'
import { onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import router from '@/router'

// NOTE: 变量

const spaceVO = ref<WorkCollaborativeImagesAPI.SpaceVO>()

// NOTE: 调用

// 获取私有空间的调用
const handGetSpace = async () => {
  const res = await spaceQuery({
    spaceType: SPACE_TYPE_ENUM.SELF
  })
  if (res.data.code === 20000 && res.data.data) {
    spaceVO.value = res.data.data
    message.success('获取空间成功')
  } else {
    message.info(res.data.message)
    await router.replace({
      path: '/operate/space/add/',
      query: {
        type: "self"
      }
    })
  }
}

// NOTE: 监听

onMounted(() => {
  handGetSpace()
})
</script>

<template>
  <div id="SelfSpace">
    <SpaceDetails :spaceVO="spaceVO"></SpaceDetails>
  </div>
</template>

<style scoped></style>
