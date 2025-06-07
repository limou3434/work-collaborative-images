<script lang="ts" setup>
/**
 * 图片分享弹窗组件
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
import { ref } from 'vue'

// 外部传递的属性
interface Props {
  title: string
  link: string
}

// 属性的默认值
const { title = '分享链接', link = 'https://limou3434.github.io/work-blog-website/' } =
  defineProps<Props>()

// 弹窗开闭逻辑
const visible = ref(false)
const openModal = () => {
  visible.value = true
}
const closeModal = () => {
  visible.value = false
}

// 暴露弹窗回调给父组件
defineExpose({
  openModal,
})
</script>

<template>
  <div>
    <a-modal v-model:visible="visible" :footer="false" :title="title" @cancel="closeModal">
      <h4>复制分享链接</h4>
      <a-typography-link copyable>
        {{ link }}
      </a-typography-link>
      <div style="margin-bottom: 16px" />
      <h4>手机扫码查看</h4>
      <a-qrcode :value="link" />
    </a-modal>
  </div>
</template>
