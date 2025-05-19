<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { adminSpaceAdd, adminSpaceUpdate, adminSpaceSearch } from '@/api/work-collaborative-images/spaceController.ts'

// 路由
const route = useRoute()

// 判断是否为编辑模式
const id = Number(route.query?.id)
const isEdit = ref(!isNaN(id))

// 表单参数
const spaceFormParams = reactive<WorkCollaborativeImagesAPI.AdminSpaceAddRequest & { id?: number }>({
  spaceName: '',
})

// 提交表单
const handleSubmit = async () => {
  let res
  if (isEdit.value) {
    res = await adminSpaceUpdate({
      id,
      spaceName: spaceFormParams.spaceName,
    })
  } else {
    res = await adminSpaceAdd({
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
  const res = await adminSpaceSearch({ id })
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
</script>

<template>
  <div id="addPicturePage">
    <h2 style="margin-bottom: 16px">
      {{ isEdit ? '编辑空间' : '创建空间' }}
    </h2>

    <a-form :model="spaceFormParams" layout="vertical" @finish="handleSubmit">
      <a-form-item
        label="名称"
        name="spaceName"
        :rules="[{ required: true, message: '请输入名称' }]"
      >
        <a-input v-model:value="spaceFormParams.spaceName" allowClear placeholder="请输入名称" />
      </a-form-item>

      <a-form-item>
        <a-button html-type="submit" style="width: 100%" type="primary">
          {{ isEdit ? '修改' : '创建' }}
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
