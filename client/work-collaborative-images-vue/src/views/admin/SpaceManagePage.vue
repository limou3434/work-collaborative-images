<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  adminSpaceDelete,
  adminSpaceSearch,
} from '@/api/work-collaborative-images/spaceController.ts'
import { formatSize } from '@/utils'
import { SPACE_TYPE_MAP, SPACE_LEVEL_MAP } from '@/constants/space.ts'

// 定义表格的列名和对应字段
const columns = [
  {
    title: '标识',
    dataIndex: 'id',
  },
  {
    title: '空间名称',
    dataIndex: 'spaceName',
  },
  {
    title: '空间类型',
    dataIndex: 'spaceType',
  },
  {
    title: '空间级别',
    dataIndex: 'spaceLevel',
  },
  {
    title: '使用情况',
    dataIndex: 'spaceInfo',
  },
  {
    title: '用户标识',
    dataIndex: 'userId',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '编辑时间',
    dataIndex: 'updateTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 获取分页查询的结果
const dataList = ref<WorkCollaborativeImagesAPI.Space[]>([]) // 存储分页查询后的图片数组
const searchParams = reactive<WorkCollaborativeImagesAPI.SpaceSearchRequest>({
  sortOrder: 'inverted',
  pageCurrent: 1,
  pageSize: 10,
}) // 存储初始化的查询参数, 后续用来做搜索请求
const pagination = ref({
  pageCurrent: 1,
  pageSize: 10,
  total: 0,
  showTotal: (total: number) => `共 ${total} 条`,
  showSizeChanger: true,
  showQuickJumper: true,
}) // 存储页面上的分页配置, 后续用来控制分页渲染
const paginationConfig = computed(() => pagination.value) // 计算分页配置
const getTableData = async () => {
  const res = await adminSpaceSearch({
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
  pagination.value.pageCurrent = paginationInfo.current
  pagination.value.pageSize = paginationInfo.pageSize
  searchParams.pageCurrent = paginationInfo.current
  searchParams.pageSize = paginationInfo.pageSize
  getTableData()
}

// 表格搜索操作, 需要搜索重置分页并且重新获取表格数据
const doSearch = () => {
  searchParams.pageCurrent = 1
  pagination.value.pageCurrent = 1
  getTableData()
}

// 设置搜索控件可折叠
const showMore = ref(false)
const toggleShowMore = () => {
  showMore.value = !showMore.value
}

// 设置删除空间的回调
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const deleteParams = reactive<WorkCollaborativeImagesAPI.SpaceDeleteRequest>({
    id: Number(id),
  })
  const res = await adminSpaceDelete(deleteParams)
  if (res.data.code === 20000) {
    message.success('删除成功')
    await getTableData() // 刷新数据
  } else {
    message.error(res.data.message)
  }
}
</script>

<template>
  <div id="pictureManagePage">
    <!-- 管理标题 -->
    <a-flex justify="space-between">
      <h2>空间管理</h2>
      <a-space>
        <a-button href="/space/add" target="_blank" type="primary">+ 创建空间</a-button>
      </a-space>
    </a-flex>
    <!-- 搜索表单 -->
    <a-form :model="searchParams" layout="vertical" @finish="doSearch">
      <!-- 快速可用部分 -->
      <a-form-item label="名称">
        <a-input v-model:value="searchParams.name" allow-clear placeholder="输入名称" />
      </a-form-item>
      <!-- 展开可用部分 -->
      <template v-if="showMore">
        <a-form-item label="所属用户">
          <a-input v-model:value="searchParams.userId" allow-clear placeholder="输入用户标识" />
        </a-form-item>
      </template>
      <template v-if="showMore">
        <a-form-item label="空间等级">
          <a-select v-model:value="searchParams.level" allow-clear placeholder="选择空间等级">
            <a-select-option :value="0">普通版</a-select-option>
            <a-select-option :value="1">专业版</a-select-option>
            <a-select-option :value="2">旗舰版</a-select-option>
          </a-select>
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
    <!-- 空间列表 -->
    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="paginationConfig"
      :scroll="{ x: 'max-content' }"
      rowKey="id"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <!-- 空间名称 -->
        <template v-if="column.dataIndex === 'spaceName'">
          {{record.name}}
        </template>
        <!-- 空间类型 -->
        <template v-if="column.dataIndex === 'spaceType'">
          {{ SPACE_TYPE_MAP[record.type as 0 | 1 | 2] }}
        </template>
        <!-- 空间级别 -->
        <template v-if="column.dataIndex === 'spaceLevel'">
          {{ SPACE_LEVEL_MAP[record.level as 0 | 1 | 2] }}
        </template>
        <!-- 空间存量 -->
        <template v-else-if="column.dataIndex === 'spaceInfo'">
          <div>数量使用率: {{ record.totalCount }} 张 / {{ record.maxCount }} 张</div>
          <div>
            大小使用率: {{ formatSize(record.totalSize) }} / {{ formatSize(record.maxSize) }}
          </div>
        </template>
        <!-- 创建时间 -->
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <!-- 修改时间 -->
        <template v-else-if="column.dataIndex === 'updateTime'">
          {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <!-- 具体操作 -->
        <template v-else-if="column.key === 'action'">
          <a-button :href="`/operate/space/add?id=${record.id}`" target="_blank" type="link">
            编辑
          </a-button>
          <a-button danger type="link" @click="doDelete(record.id)">删除</a-button>
        </template>
      </template>
    </a-table>
  </div>
</template>

<!-- TODO: 添加管理员可以直接跳转到管理空间的页面 -->
<!-- TODO: 删除空间的时候需要弹出提示 -->
<!-- TODO: 管理元无法修改其他用户的空间名称... -->
<!-- TODO: 支持对空间等级的修改... -->
<!-- TODO: 支持对标签的修改... -->

<style scoped></style>
