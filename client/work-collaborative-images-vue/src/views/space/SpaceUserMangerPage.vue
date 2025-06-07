<script lang="ts" setup>
/**
 * 空间成员管理页面
 *
 * @author <a href="https://github.com/limou3434">limou3434</a>
 */
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  spaceUserEdit,
  spaceUserMoveIn,
  spaceUserMoveOut,
  spaceUserPageUser
} from '@/api/work-collaborative-images/spaceUserController.ts'
import { SPACE_ROLE_OPTIONS } from '@/constants/space.ts'
import dayjs from 'dayjs'

// NOTE: 变量

interface Props {
  id: string
}
const props = defineProps<Props>()

const columns = [
  {
    title: '用户',
    dataIndex: 'userInfo'
  },
  {
    title: '角色',
    dataIndex: 'spaceRole'
  },
  {
    title: '时间',
    dataIndex: 'createTime'
  },
  {
    title: '操作',
    key: 'action'
  }
]



// 数据
const dataList = ref<WorkCollaborativeImagesAPI.UserVO[]>()

// 获取数据
const fetchData = async () => {
  const spaceId = props.id
  if (!spaceId) {
    return
  }
  const res = await spaceUserPageUser({
    spaceId: Number(spaceId)
  })
  if (res.data.data) {
    dataList.value = res.data.data.records
  } else {
    message.error(res.data.message)
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})

const editSpaceRole = async (value, record) => {
  const res = await spaceUserEdit({
    spaceId: Number(props.id),
    userId: record.id,
    spaceRole: value
  })
  if (res.data.code === 20000) {
    message.success('修改成功')
  } else {
    message.error(res.data.message)
  }
}

// 添加用户
const formData = reactive<WorkCollaborativeImagesAPI.SpaceUserAddRequest>({})

const handleSubmit = async () => {
  const spaceId = props.id
  if (!spaceId) {
    return
  }
  const res = await spaceUserMoveIn({
    spaceId: Number(spaceId),
    ...formData
  })
  if (res.data.code === 0) {
    message.success('添加成功')
    // 刷新数据
    fetchData()
  } else {
    message.error(res.data.message)
  }
}

const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const res = await spaceUserMoveOut({ spaceId: Number(props.id), userId: id })
  if (res.data.code === 0) {
    message.success('删除成功')
    // 刷新数据
    fetchData()
  } else {
    message.error('删除失败')
  }
}

</script>

<template>
  <a-form :model="formData" layout="inline" @finish="handleSubmit">
    <a-form-item label="用户 id" name="userId">
      <a-input v-model:value="formData.userId" allow-clear placeholder="请输入用户 id" />
    </a-form-item>
    <a-form-item>
      <a-button html-type="submit" type="primary">添加用户</a-button>
    </a-form-item>
  </a-form>


  <a-table :columns="columns" :data-source="dataList">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'userInfo'">
        <a-space>
          <a-avatar :src="record?.avatar" />
          {{ record.account }}
        </a-space>
      </template>
      <template v-if="column.dataIndex === 'spaceRole'">
        <a-select
          v-model:value="record.spaceRole"
          :options="SPACE_ROLE_OPTIONS"
          @change="(value) => editSpaceRole(value, record)"
        />
      </template>
      <template v-else-if="column.dataIndex === 'createTime'">
        {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
      </template>
      <template v-else-if="column.key === 'action'">
        <a-space wrap>
          <a-button danger type="link" @click="doDelete(record.id)">删除</a-button>
        </a-space>
      </template>
    </template>
  </a-table>

</template>

<style scoped>

</style>
