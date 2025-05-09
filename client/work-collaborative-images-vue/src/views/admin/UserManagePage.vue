<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { userSearch } from '@/api/work-collaborative-images/userController.ts'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'

// 定义表格的列名和对应字段
const columns = [
  { title: '标识', dataIndex: 'id' },
  { title: '账号', dataIndex: 'account' },
  { title: '名字', dataIndex: 'name' },
  { title: '头像', dataIndex: 'avatar' },
  { title: '简介', dataIndex: 'profile' },
  { title: '角色', dataIndex: 'role' },
  { title: '创建时间', dataIndex: 'createTime' },
  { title: '更新时间', dataIndex: 'updateTime' },
  { title: '操作', key: 'action' },
]

// 获取分页查询的结果
const dataList = ref<WorkCollaborativeImagesAPI.UserVO[]>([]) // 存储分页查询后的用户数组
const searchParams = reactive<WorkCollaborativeImagesAPI.UserSearchRequest>({
  account: '',
  name: '',
  pageCurrent: 1,
  pageSize: 10,
}) // 存储初始化的查询参数
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: (total: number) => `共 ${total} 条`,
  showSizeChanger: true,
  showQuickJumper: true,
}) // 存储页面上的分页配置
const paginationConfig = computed(() => pagination.value) // 计算分页配置
const getTableData = async () => {
  const res = await userSearch({
    ...searchParams,
  })
  if (res.data.code === 20000 && res.data?.data) {
    dataList.value = res.data.data.records ?? []
    pagination.value.total = res.data.data.total ?? 0
  } else {
    message.error(res.data.message)
  }
} // 异步获取具体的分页数据
onMounted(getTableData) // 页面加载时就执行一次

// 表格分页/排序/筛选变化
const doTableChange = (page: any) => {
  pagination.value.current = page.current
  pagination.value.pageSize = page.pageSize
  searchParams.pageCurrent = page.current
  searchParams.pageSize = page.pageSize
  getTableData()
}

// 表格搜索操作, 需要搜索重置分页并且重新获取表格数据
const doSearch = () => {
  searchParams.pageCurrent = 1
  pagination.value.current = 1
  getTableData()
}
</script>

<template>
  <div id="userManagePage">
    <!-- 搜索表单 -->
    <a-form :model="searchParams" layout="inline" @finish="doSearch">
      <a-form-item label="账号">
        <a-input v-model:value="searchParams.account" allow-clear placeholder="输入账号" />
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="searchParams.name" allow-clear placeholder="输入用户名" />
      </a-form-item>
      <a-form-item>
        <a-button html-type="submit" type="primary">搜索</a-button>
      </a-form-item>
    </a-form>
    <!-- 留空区域 -->
    <div style="margin: 20px 0" />
    <!-- 用户列表 -->
    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="paginationConfig"
      rowKey="id"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'avatar'">
          <a-image :src="record.avatar" :width="80" lazy />
        </template>
        <template v-else-if="column.dataIndex === 'role'">
          <a-tag :color="record.role === 'admin' ? 'green' : 'blue'">
            {{ record.role === 'admin' ? '管理员' : '普通用户' }}
          </a-tag>
        </template>
        <template
          v-else-if="column.dataIndex === 'createTime' || column.dataIndex === 'updateTime'"
        >
          {{ dayjs(record[column.dataIndex]).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-button danger type="link">删除</a-button>
        </template>
      </template>
    </a-table>
  </div>
</template>

<style scoped></style>
