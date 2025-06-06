<script lang="ts" setup>
/**
 * 添加空间组件, 可以根据路由传递的参数来决定创建哪一种类型的专属空间
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  spaceCreate,
  spaceEdit,
  spaceLevel,
  spaceQuery
} from '@/api/work-collaborative-images/spaceController.ts'
import { formatSize } from '@/utils'
import { SPACE_TYPE_ENUM } from '@/constants/space.ts'

/// 变量 ///
const route = useRoute() // 存储路由读取器
const id = Number(route.query?.id) // 存储传递过来的路由参数, 用于判断是要创建空间还是修改空间
const type = route.query?.type // 存储传递过来的路由参数, 用于判断要求跳转到当前页面的组件是要求创建私有空间还是协作空间的布尔值
const router = useRouter() // 存储路由管理器
const spaceFormParams = reactive<WorkCollaborativeImagesAPI.SpaceAddRequest>({}) // 存储创建空间表单或编空间表单参数
const isCreate = ref(isNaN(id)) // 存储是否为创建空间的标志位
const isSelf = ref(type === 'self') // 存储是否为私有空间的标志位
const spaceLevelList = ref<WorkCollaborativeImagesAPI.SpaceLevelInfo[]>([]) // 存储告知用户空间级别区别的数据

// 获取空间级别调用
const handleGetSpaceLevelList = async () => {
  const res = await spaceLevel()
  if (res.data.code === 20000 && res.data.data) {
    spaceLevelList.value = res.data.data
  } else {
    message.error(res.data.message)
  }
}

// 根据传递的 type 来判断需要操作哪一种空间调用
const handleSetType = () => {
  if (isSelf.value) {
    return SPACE_TYPE_ENUM.SELF
  } else {
    return SPACE_TYPE_ENUM.COLLABORATIVE
  }
}

// 用于提交创建空间/编辑空间表单调用
const handleSubmit = async () => {
  const spaceType = handleSetType()
  // 根据表单内容和路由参数来填充请求进行发送
  let res
  if (isCreate.value) { // 创建空间
    res = await spaceCreate({
      spaceName: spaceFormParams.name,
      spaceType: spaceType
    })
  } else { // 编辑空间
    res = await spaceEdit({
      spaceName: spaceFormParams.name,
      spaceType: spaceType
    })
  }

  if (res.data.code === 20000 && res.data.data) {
    message.success(isCreate.value ? '创建成功' : '修改成功')
    const path = '/' + (isSelf.value ? 'self' : 'collaborative')
    await router.push(path)
  } else {
    message.error(res.data.message)
  }
}

// 用于获取专属空间的旧数据调用
const handleGetOldSpace = async () => {
  const spaceType = handleSetType()
  const res = await spaceQuery({
    spaceType: spaceType
  })
  if (res.data.code === 20000 && res.data.data) {
    spaceFormParams.name = res.data.data.name
  } else {
    message.error(res.data.message)
  }
}

/// 监控 ///
onMounted(() => {
  handleGetSpaceLevelList()
  if (!isCreate.value) {
    handleGetOldSpace()
  }
})
</script>

<template>
  <div id="addPicturePage">
    <!-- 页面标题 -->
    <h2 style="margin-bottom: 16px">
      {{ isCreate ? '创建' : '编辑' }}{{ isSelf ? '私有空间' : '协作空间' }}
    </h2>
    <!-- 操作表单 -->
    <a-form :model="spaceFormParams" layout="vertical" @finish="handleSubmit">
      <a-form-item
        :rules="[{ required: true, message: '请输入名称' }]"
        label="名称"
        name="name"
      >
        <a-input v-model:value="spaceFormParams.name" allowClear placeholder="请输入名称" />
      </a-form-item>
      <a-form-item>
        <a-button html-type="submit" style="width: 100%" type="primary">
          {{ isCreate ? '创建' : '修改' }}
        </a-button>
      </a-form-item>
    </a-form>
    <!-- 级别介绍 -->
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
