<script lang="ts" setup>
import PictureUpload from '@/components/PictureUpload.vue'
import { nextTick, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  pictureCategorys,
  pictureQuery,
  pictureUpload,
} from '@/api/work-collaborative-images/pictureController.ts'
import { spaceQuery } from '@/api/work-collaborative-images/spaceController.ts'
import { SPACE_TYPE_ENUM } from '@/constants/space.ts' // 获取私有空间 ID 接口

const route = useRoute()
const router = useRouter()

// 上传表单参数
const pictureForm = reactive<WorkCollaborativeImagesAPI.pictureUploadParams>({
  pictureName: '',
  pictureCategory: '',
  pictureIntroduction: '',
  pictureTags: '',
})

// 是否加入私有空间开关
const joinSlefSpace = ref(false)
watch(joinSlefSpace, async (val) => {
  if (val) {
    const res = await spaceQuery({ spaceType: SPACE_TYPE_ENUM.SELF })
    if (res.data.code === 20000 && res.data.data?.id) {
      pictureForm.spaceId = res.data.data.id
    } else {
      message.error(res.data.message)
      joinSlefSpace.value = false
    }
  } else {
    pictureForm.spaceId = undefined
  }
})

// 上传后存储图片信息
const picture = ref<WorkCollaborativeImagesAPI.PictureVO>()

// 获取已有图片数据
const searchParams = reactive<WorkCollaborativeImagesAPI.PictureSearchRequest>({
  id: -1,
})
const getOldPicture = async () => {
  const id = route.query?.id
  if (id) {
    searchParams.id = Number(id)
    const res = await pictureQuery(searchParams)
    if (res.data.code === 20000 && res.data.data?.records) {
      const data = res.data.data.records[0]
      picture.value = data
      pictureForm.pictureId = data.id
      pictureForm.pictureName = data.name
      pictureForm.pictureIntroduction = data.introduction
      pictureForm.pictureCategory = data.category
      pictureForm.pictureTags = JSON.parse(data.tags || '[]')
      if (data.spaceId && data.spaceId !== 0) {
        joinSlefSpace.value = true
        pictureForm.spaceId = data.spaceId
      }
    }
  }
}

// 上传成功回调
const onSuccess = (newPicture: WorkCollaborativeImagesAPI.PictureVO) => {
  picture.value = newPicture
  pictureForm.pictureId = newPicture.id
}

// 提交表单
const handleSubmit = async () => {
  pictureForm.pictureTags = JSON.stringify(pictureForm.pictureTags)
  const res = await pictureUpload(pictureForm)
  if (res.data.code === 20000 && res.data.data) {
    message.success('上传成功')
    if (route.query.from === 'self') {
      // 如果是从我的空间跳转过来的，则跳转到我的空间
      await router.push('/self')
    }
    else {
      // 如果是从图片详情页跳转过来的，则跳转到图片详情页
      await router.push({ path: `/picture/${picture?.value?.id}` })
    }
  } else {
    message.error(res.data.message)
  }
}

// 分类列表
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
  if (route.query.from === 'self') {
    joinSlefSpace.value = true
    nextTick() // 强制触发 watch 逻辑自动设置 spaceId
  }
  getOldPicture()
  getCategoryList()
})
</script>

<template>
  <div id="addPicturePage">
    <h2 style="margin-bottom: 16px">
      {{ pictureForm.pictureId ? '编辑图片' : '填写信息以创建图片' }}
    </h2>
    <PictureUpload :onSuccess="onSuccess" :picture="picture" :pictureId="pictureForm.pictureId" />

    <a-form :model="pictureForm" layout="vertical" @finish="handleSubmit">
      <a-form-item label="加入私有空间">
        <a-switch v-model:checked="joinSlefSpace" />
      </a-form-item>

      <a-form-item label="名称" name="name">
        <a-input v-model:value="pictureForm.pictureName" allowClear placeholder="请输入名称" />
      </a-form-item>

      <a-form-item label="分类" name="category">
        <a-select
          v-model:value="pictureForm.pictureCategory"
          :options="categoryList.map((cat) => ({ label: cat, value: cat }))"
          allowClear
          placeholder="请选择分类"
        />
      </a-form-item>

      <a-form-item label="简介" name="introduction">
        <a-textarea
          v-model:value="pictureForm.pictureIntroduction"
          :rows="2"
          allowClear
          autoSize
          placeholder="请输入简介（可换行）"
        />
      </a-form-item>

      <a-form-item label="标签" name="tags">
        <a-select
          v-model:value="pictureForm.pictureTags"
          allowClear
          mode="tags"
          placeholder="请输入标签"
        />
      </a-form-item>

      <a-form-item>
        <a-button html-type="submit" style="width: 100%" type="primary">
          {{ pictureForm.pictureId ? '更新' : '创建' }}
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
