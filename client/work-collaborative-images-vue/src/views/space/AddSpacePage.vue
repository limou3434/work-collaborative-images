<script lang="ts" setup>
import PictureUpload from '@/components/PictureUpload.vue'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  pictureCategorys,
  pictureQuery,
  pictureUpload,
} from '@/api/work-collaborative-images/pictureController.ts'

// 这个页面作为创建页面的同时也可以作为修改页面把路径中的 id 获取到后直接显示在上传组件上
const route = useRoute()
const searchParams = reactive<WorkCollaborativeImagesAPI.SpaceQueryRequest>({
  id: -1,
}) // 存储初始化的查询参数, 后续用来做搜索请求
const getOldPicture = async () => {
  const id = route.query?.id // 页面传递的图片 id
  if (id) {
    searchParams.id = Number(id)
    const res = await pictureQuery(searchParams)
    if (res.data.code === 20000 && res.data.data && res.data.data.records) {
      const data = res.data.data.records[0]
      space.value = data
      spaceForm.pictureId = data.id
      spaceForm.pictureName = data.name
      spaceForm.pictureIntroduction = data.introduction
      spaceForm.pictureCategory = data.category
    }
  }
} // 获取老数据
onMounted(() => {
  getOldPicture()
})

// 设置请求图片上传的参数
const spaceForm = reactive<WorkCollaborativeImagesAPI.SpaceCreateRequest>({
  pictureId: Number(route.query?.id), // 如果有id则进行更新操作，否则是添加
  pictureName: '',
  pictureCategory: '',
  pictureIntroduction: '',
  pictureTags: '',
})

// 设置图片变量用于存储上传后
const space = ref<WorkCollaborativeImagesAPI.SpaceVO>()

// 提交表单回调
const router = useRouter()
const handleSubmit = async () => {
  const pictureId = space.value?.id
  if (!pictureId) {
    message.error('请先上传图片')
    return
  }
  spaceForm.pictureTags = JSON.stringify(spaceForm.pictureTags) // 序列化
  const res = await pictureUpload(spaceForm) // 根据 spaceForm 进行上传或者更新
  if (res.data.code === 20000 && res.data.data) {
    message.success('上传成功')
    // 跳转到图片详情页
    await router.push({
      path: `/space/${pictureId}`,
    })
  } else {
    message.error(res.data.message)
  }
}

// 不再允许用户手动提交图片种类, 而是使用选择器
const categoryList = ref<string[]>([])
const getCategoryList = async () => {
  const res = await pictureCategorys()
  if (res.data.code === 20000 && res.data.data) {
    categoryList.value = res.data.data
  } else {
    message.error('获取图片分类失败')
  }
}
onMounted(() => {
  getCategoryList()
})
</script>

<template>
  <div id="addPicturePage">
    <!-- 页面标题 -->
    <h2 style="margin-bottom: 16px">{{ spaceForm.pictureId ? '编辑空间' : '填写信息以创建图片' }}</h2> <!-- 修改标题，根据是否有图片ID判断是编辑还是创建 -->
    <!-- 信息表单 -->
    <a-form :model="spaceForm" layout="vertical" @finish="handleSubmit">
      <!-- 名称 -->
      <a-form-item label="名称" name="name">
        <a-input v-model:value="spaceForm.pictureName" allowClear placeholder="请输入名称" />
      </a-form-item>
      <!-- 分类 -->
      <a-form-item label="分类" name="category">
        <a-select
          v-model:value="spaceForm.pictureCategory"
          allowClear
          placeholder="请选择分类"
          :options="categoryList.map(cat => ({ label: cat, value: cat }))"
        />
      </a-form-item>
      <!-- 简介 -->
      <a-form-item label="简介" name="introduction">
        <a-textarea
          v-model:value="spaceForm.pictureIntroduction"
          :rows="2"
          allowClear
          autoSize
          placeholder="请输入简介（可换行）"
        />
      </a-form-item>
      <!-- 标签 -->
      <a-form-item label="标签" name="tags">
        <a-select
          v-model:value="spaceForm.pictureTags"
          allowClear
          mode="tags"
          placeholder="请输入标签"
        />
      </a-form-item>
      <!-- 创建 -->
      <a-form-item>
        <a-button html-type="submit" style="width: 100%" type="primary">{{ spaceForm.pictureId ? '更新' : '创建' }}</a-button> <!-- 修改按钮文字，取决于是否有图片ID -->
      </a-form-item>
    </a-form>
  </div>
</template>
