<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  spaceCreate,
  spaceEdit,
  spaceLevel,
  spaceQuery,
} from '@/api/work-collaborative-images/spaceController.ts'
import { formatSize } from '@/utils'

// 路由
const route = useRoute()

// 判断是否为编辑模式
const id = Number(route.query?.id)
const isEdit = ref(!isNaN(id))

// 表单参数
const spaceFormParams = reactive<
  WorkCollaborativeImagesAPI.AdminSpaceAddRequest & {
    id?: number
  }
>({
  spaceName: '',
})

// 提交表单
const handleSubmit = async () => {
  let res
  if (isEdit.value) {
    res = await spaceEdit({
      id,
      spaceName: spaceFormParams.spaceName,
    })
  } else {
    res = await spaceCreate({
      spaceName: spaceFormParams.spaceName,
    })
  }

  if (res.data.code === 20000 && res.data.data) {
    message.success(isEdit.value ? '修改成功' : '创建成功')
  } else {
    message.error(res.data.message || '操作失败')
  }
}

// 获取旧数据
const getOldSpace = async () => {
  const res = await spaceQuery({ id })
  const record = res.data.data?.records?.[0]
  if (res.data.code === 20000 && record) {
    spaceFormParams.spaceName = record.spaceName
  } else {
    message.error('获取空间信息失败')
  }
}

// 初始化逻辑
onMounted(() => {
  if (isEdit.value) {
    getOldSpace()
  }
})

// 空间级别
const spaceLevelList = ref<WorkCollaborativeImagesAPI.SpaceLevelInfo[]>([])
const fetchSpaceLevelList = async () => {
  const res = await spaceLevel()
  if (res.data.code === 20000 && res.data.data) {
    spaceLevelList.value = res.data.data
  } else {
    message.error('加载空间级别失败，' + res.data.message)
  }
} // 获取空间级别
onMounted(() => {
  fetchSpaceLevelList()
})
</script>

<template>
  <div id="addPicturePage">
    <h2 style="margin-bottom: 16px">
      {{ isEdit ? '编辑空间' : '创建空间' }}
    </h2>

    <a-form :model="spaceFormParams" layout="vertical" @finish="handleSubmit">
      <a-form-item
        :rules="[{ required: true, message: '请输入名称' }]"
        label="名称"
        name="spaceName"
      >
        <a-input v-model:value="spaceFormParams.spaceName" allowClear placeholder="请输入名称" />
      </a-form-item>
      <a-form-item>
        <a-button html-type="submit" style="width: 100%" type="primary">
          {{ isEdit ? '修改' : '创建' }}
        </a-button>
      </a-form-item>
    </a-form>
    <a-card hoverable title="空间级别介绍">
      <a-typography-paragraph>
        * 目前仅支持开通普通版, 如需升级空间, 请联系
        <a href="https://github.com/limou3434" target="_blank">limou3434</a>。
      </a-typography-paragraph>
      <a-typography-paragraph v-for="spaceLevel in spaceLevelList" :key="spaceLevel.value">
        {{ spaceLevel.text }}: 大小 {{ formatSize(spaceLevel.maxSize) }}, 数量
        {{ spaceLevel.maxCount }}
      </a-typography-paragraph>
    </a-card>
  </div>
</template>
