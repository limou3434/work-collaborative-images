<script lang="ts" setup>
import PictureUpload from '@/components/PictureUpload.vue'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  pictureSearchVo,
  pictureUploadVo,
} from '@/api/work-collaborative-images/pictureController.ts'

// 设置请求图片上传的参数
const pictureForm = reactive<WorkCollaborativeImagesAPI.pictureUploadVOParams>({
  pictureId: -1,
  pictureName: '',
  pictureCategory: '',
  pictureIntroduction: '',
  pictureTags: '',
})

// 设置图片变量用于存储上传后
const picture = ref<WorkCollaborativeImagesAPI.PictureVO>()

// 上传成功后更新图片信息
const onSuccess = (newPicture: WorkCollaborativeImagesAPI.PictureVO) => {
  picture.value = newPicture
  pictureForm.pictureId = newPicture.id
  pictureForm.pictureName = newPicture.name
}

// 提交表单回调
const router = useRouter()
const handleSubmit = async () => {
  const pictureId = picture.value?.id
  if (!pictureId) {
    message.error('请先上传图片')
    return
  }
  pictureForm.pictureTags = JSON.stringify(pictureForm.pictureTags)
  const res = await pictureUploadVo(pictureForm)
  if (res.data.code === 20000 && res.data.data) {
    message.success('上传成功')
    // 跳转到图片详情页
    await router.push({
      path: `/picture/${pictureId}`,
    })
  } else {
    message.error(res.data.message)
  }
}

// 这个页面作为创建页面的同时也可以作为修改页面把路径中的 id 获取到后直接显示在上传组件上
const route = useRoute()
const searchParams = reactive<WorkCollaborativeImagesAPI.PictureSearchRequest>({
  id: -1,
}) // 存储初始化的查询参数, 后续用来做搜索请求
const getOldPicture = async () => {
  const id = route.query?.id // 页面传递的图片 id
  if (id) {
    searchParams.id = Number(id)
    const res = await pictureSearchVo(searchParams)
    if (res.data.code === 20000 && res.data.data) {
      const data = res.data.data[0]
      picture.value = data
      pictureForm.pictureId = data.id
      pictureForm.pictureName = data.name
      pictureForm.pictureIntroduction = data.introduction
      pictureForm.pictureCategory = data.category
      pictureForm.pictureTags = JSON.parse(data.tags || "")
    }
  }
} // 获取老数据
onMounted(() => {
  getOldPicture()
})
</script>

<template>
  <div id="addPicturePage">
    <!-- 页面标题 -->
    <h2 style="margin-bottom: 16px">填写信息以创建图片</h2>
    <!-- 上传组件 -->
    <PictureUpload :onSuccess="onSuccess" :picture="picture" />
    <!-- 信息表单 -->
    <a-form :model="pictureForm" layout="vertical" @finish="handleSubmit">
      <!-- 名称 -->
      <a-form-item label="名称" name="name">
        <a-input v-model:value="pictureForm.pictureName" allowClear placeholder="请输入名称" />
      </a-form-item>
      <!-- 分类 -->
      <a-form-item label="分类" name="category">
        <a-auto-complete
          v-model:value="pictureForm.pictureCategory"
          allowClear
          placeholder="请输入分类"
        />
      </a-form-item>
      <!-- 简介 -->
      <a-form-item label="简介" name="introduction">
        <a-textarea
          v-model:value="pictureForm.pictureIntroduction"
          :rows="2"
          allowClear
          autoSize
          placeholder="请输入简介（可换行）"
        />
      </a-form-item>
      <!-- 标签 -->
      <a-form-item label="标签" name="tags">
        <a-select
          v-model:value="pictureForm.pictureTags"
          allowClear
          mode="tags"
          placeholder="请输入标签"
        />
      </a-form-item>
      <!-- 创建 -->
      <a-form-item>
        <a-button html-type="submit" style="width: 100%" type="primary">创建</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
