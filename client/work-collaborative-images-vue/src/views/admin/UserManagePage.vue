<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { userDelete, userSearch } from '@/api/work-collaborative-images/userController.ts'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'

// 定义表格的列名和对应字段
const columns = [
  { title: '标识', dataIndex: 'id' },
  { title: '账号', dataIndex: 'account' },
  { title: '名字', dataIndex: 'name' },
  { title: '昵称', dataIndex: 'nick' },
  { title: '邮箱', dataIndex: 'email' },
  { title: '电话', dataIndex: 'phone' },
  { title: '头像', dataIndex: 'avatar' },
  { title: '简介', dataIndex: 'profile' },
  { title: '角色', dataIndex: 'role' },
  { title: '创建时间', dataIndex: 'createTime' },
  { title: '更新时间', dataIndex: 'updateTime' },
  { title: '操作', key: 'action' },
  { title: '标签', dataIndex: 'tags' },
]

// 获取分页查询的结果
const dataList = ref<WorkCollaborativeImagesAPI.UserVO[]>([]) // 存储分页查询后的用户数组
const searchParams = reactive<WorkCollaborativeImagesAPI.UserSearchRequest>({
  account: '',
  name: '',
  nick: '',
  email: '',
  phone: '',
  tags: '',
  pageCurrent: 1,
  pageSize: 10,
}) // 存储初始化的查询参数, 后续用来做搜索请求
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: (total: number) => `共 ${total} 条`,
  showSizeChanger: true,
  showQuickJumper: true,
}) // 存储页面上的分页配置, 后续用来控制分页渲染
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

// 设置表格分页/排序/筛选变化时的回调函数
const doTableChange = (
  paginationInfo: { current: number; pageSize: number }, // 分页发生变化
  // filters: any, // 修改每页条数
  // sorter: any, // 点击表头排序
) => {
  // 当分页发生变化时就把分页配置和搜索参数进行动态修改
  pagination.value.current = paginationInfo.current
  pagination.value.pageSize = paginationInfo.pageSize
  searchParams.pageCurrent = paginationInfo.current
  searchParams.pageSize = paginationInfo.pageSize
  getTableData()
}

// 表格搜索操作, 需要搜索重置分页并且重新获取表格数据
const doSearch = () => {
  searchParams.pageCurrent = 1
  pagination.value.current = 1
  getTableData()
}

// 设置搜索控件可折叠
const showMore = ref(false)
const toggleShowMore = () => {
  showMore.value = !showMore.value
}

// 把标签转化为多个 Tag
const renderTags = (tags: string[]) => {
  const displayTags = tags.slice(0, 3).map((item) => ({
    content: item,
  }))
  const hiddenCount = tags.length - displayTags.length
  return {
    displayTags,
    hiddenCount,
    allTags: tags,
  }
} // 将数组数据渲染为不同颜色的 tag
const renderTagsForUser = (userTags: string | string[]) => {
  let tagList: string[] = []
  if (Array.isArray(userTags)) {
    tagList = userTags
  } else {
    try {
      const parsed = JSON.parse(userTags)
      if (Array.isArray(parsed)) {
        tagList = parsed
      }
    } catch (e) {
      console.error('tags 解析失败: ', e)
    }
  }
  return renderTags(tagList)
} // 解析 json 数组字符进行分割

// 设置删除用户的回调
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const deleteParams = reactive<WorkCollaborativeImagesAPI.UserDeleteRequest>({
    id: Number(id),
  })
  const res = await userDelete(deleteParams)
  if (res.data.code === 20000) {
    message.success('删除成功')
    await getTableData() // 刷新数据
  } else {
    message.error(res.data.message)
  }
}
</script>

<template>
  <div id="userManagePage">
    <!-- 搜索表单 -->
    <a-form :model="searchParams" layout="vertical" @finish="doSearch">
      <!-- 快速可用部分 -->
      <a-form-item label="账号">
        <a-input v-model:value="searchParams.account" allow-clear placeholder="输入账号" />
      </a-form-item>
      <a-form-item label="名字">
        <a-input v-model:value="searchParams.name" allow-clear placeholder="输入用户名" />
      </a-form-item>
      <!-- 展开可用部分 -->
      <template v-if="showMore">
        <a-form-item label="昵称">
          <a-input v-model:value="searchParams.nick" allow-clear placeholder="输入昵称" />
        </a-form-item>
        <a-form-item label="邮箱">
          <a-input v-model:value="searchParams.email" allow-clear placeholder="输入邮箱" />
        </a-form-item>
        <a-form-item label="电话">
          <a-input v-model:value="searchParams.phone" allow-clear placeholder="输入电话" />
        </a-form-item>
        <a-form-item label="标签">
          <a-input v-model:value="searchParams.tags" allow-clear placeholder="输入标签" />
        </a-form-item>
      </template>

      <a-form-item>
        <a-button html-type="submit" style="margin-right: 8px" type="primary">搜索</a-button>
        <a-button type="link" @click="toggleShowMore">
          {{ showMore ? '收起' : '展开' }}
        </a-button>
      </a-form-item>
    </a-form>
    <!-- 留空区域 -->
    <div style="margin: 20px 0" />
    <!-- 用户列表 -->
    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="paginationConfig"
      :scroll="{ x: 'max-content' }"
      rowKey="id"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'avatar'">
          <a-image :src="record.avatar" :width="80" lazy />
        </template>
        <template v-else-if="column.dataIndex === 'tags'">
          <template v-if="renderTagsForUser(record.tags || []).displayTags">
            <template
              v-for="tag in renderTagsForUser(record.tags || []).displayTags"
              :key="tag.content"
            >
              <a-tag>{{ tag.content }}</a-tag>
            </template>
            <template v-if="renderTagsForUser(record.tags || []).hiddenCount > 0">
              <a-tooltip :title="renderTagsForUser(record.tags || []).allTags.join(', ')">
                <a-tag>...</a-tag>
              </a-tooltip>
            </template>
          </template>
        </template>
        <template v-else-if="column.dataIndex === 'role'">
          <a-tag
            :color="
              record.role === -1
                ? 'red'
                : record.role === 0
                  ? 'blue'
                  : record.role === 1
                    ? 'gold'
                    : 'gray'
            "
          >
            {{
              record.role === -1
                ? '封号'
                : record.role === 0
                  ? '用户'
                  : record.role === 1
                    ? '管理'
                    : '未知'
            }}
          </a-tag>
        </template>
        <template
          v-else-if="column.dataIndex === 'createTime' || column.dataIndex === 'updateTime'"
        >
          {{ dayjs(record[column.dataIndex]).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-button danger type="link" @click="doDelete(record.id)">删除</a-button>
        </template>
      </template>
    </a-table>
  </div>
</template>

<style scoped></style>
