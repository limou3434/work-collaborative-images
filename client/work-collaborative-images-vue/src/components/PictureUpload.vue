<script lang="ts" setup>
import { message, type UploadProps } from 'ant-design-vue'
import { ref } from 'vue'
import { pictureUpload } from '@/api/work-collaborative-images/pictureController.ts'
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons-vue'

// 接收外部传入的属性
interface Props {
  picture?: WorkCollaborativeImagesAPI.PictureVO // 已上传的图片信息
  onSuccess?: (newPicture: WorkCollaborativeImagesAPI.PictureVO) => void // 成功回调，传递上传后的图片信息
  pictureId?: number // 接收父组件传递的 pictureId, 因为可能需要更新
}

// 前端校验图片类型与大小
// @ts-ignore
const beforeUpload = (file: UploadProps['fileList'][number]) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    message.error('不支持上传该格式的图片, 推荐使用 jpg/png')
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('不能上传超过 2MB 的图片')
  }
  return isJpgOrPng && isLt2M
}

// 处理上传逻辑, 先预先上传一次图片资源
const props = defineProps<Props>()
const loading = ref<boolean>(false)
const handleUpload = async ({ file }: any) => {
  loading.value = true
  try {
    const uploadParams = props.pictureId ? { pictureId: props.pictureId } : {} // 如果有 pictureId，就传递它
    const res = await pictureUpload({ ...uploadParams }, { file }) // 传递文件到后端
    if (res.data.code === 20000 && res.data.data) {
      message.success('图片上传成功')
      props.onSuccess?.(res.data.data) // 上传成功后将数据传递给父组件
    } else {
      message.error(res.data.message)
    }
  } catch (e: unknown) {
    message.error(e as string)
  } finally {
    loading.value = false
  }
}

// 支持使用 URL 上传图片
const fileUrl = ref<string>()
const handleUrlUpload = async () => {
  if (!fileUrl.value) {
    message.error('请输入图片 URL')
    return
  }

  loading.value = true
  try {
    const uploadParams: WorkCollaborativeImagesAPI.pictureUploadParams = { pictureFileUrl: fileUrl.value }
    if (props.pictureId) uploadParams.pictureId = props.pictureId
    const res = await pictureUpload(uploadParams)

    if (res.data.code === 20000 && res.data.data) {
      message.success('图片上传成功')
      // 通过 onSuccess 回调传递新的图片信息
      props.onSuccess?.(res.data.data)
    } else {
      message.error(res.data.message)
    }
  } catch (e: unknown) {
    message.error(e as string)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="picture-upload">
    <a-upload
      :before-upload="beforeUpload"
      :custom-request="handleUpload"
      :show-upload-list="false"
      list-type="picture-card"
    >
      <img v-if="props.picture?.url" :src="props.picture?.url" alt="avatar" />
      <div v-else>
        <loading-outlined v-if="loading" />
        <plus-outlined v-else />
        <div class="ant-upload-text">点击或拖拽上传图片</div>
      </div>
    </a-upload>
    <div class="url-picture-upload">
      <a-input-group compact style="margin-bottom: 16px">
        <a-input
          v-model:value="fileUrl"
          placeholder="请输入图片 URL"
          style="width: calc(100% - 120px)"
        />
        <a-button :loading="loading" style="width: 120px" type="primary" @click="handleUrlUpload">
          提交
        </a-button>
      </a-input-group>
    </div>
  </div>
</template>

<style scoped>
.picture-upload :deep(.ant-upload) {
  width: 100% !important;
  height: 100% !important;
  min-height: 152px;
  min-width: 152px;
}

.picture-upload img {
  max-width: 100%;
  max-height: 480px;
}

.ant-upload-select-picture-card i {
  font-size: 32px;
  color: #999;
}

.ant-upload-select-picture-card .ant-upload-text {
  margin-top: 8px;
  color: #666;
}
</style>
