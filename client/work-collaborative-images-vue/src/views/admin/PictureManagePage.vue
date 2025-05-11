<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  pictureDelete,
  pictureReview,
  pictureSearch
} from '@/api/work-collaborative-images/pictureController.ts'
import { PIC_REVIEW_STATUS_ENUM, PIC_REVIEW_STATUS_MAP } from '@/constants/picture.ts'

// 定义表格的列名和对应字段
const columns = [
  {
    title: '标识',
    dataIndex: 'id',
  },
  {
    title: '图片',
    dataIndex: 'url',
  },
  {
    title: '图片信息',
    dataIndex: 'picInfo',
  },
  {
    title: '分类',
    dataIndex: 'category',
  },
  {
    title: '名称',
    dataIndex: 'name',
  },
  {
    title: '简介',
    dataIndex: 'introduction',
  },
  {
    title: '用户标识',
    dataIndex: 'userId',
  },
  {
    title: '审核信息',
    dataIndex: 'reviewInfo',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '编辑时间',
    dataIndex: 'editTime',
  },
  {
    title: '操作',
    key: 'action',
  },
  {
    title: '标签',
    dataIndex: 'tags',
  },
]

// 获取分页查询的结果
const dataList = ref<WorkCollaborativeImagesAPI.Picture[]>([]) // 存储分页查询后的图片数组
const searchParams = reactive<WorkCollaborativeImagesAPI.PictureSearchRequest>({
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
  const res = await pictureSearch({
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
} // 将数组数据渲染为 tag
const renderTagsForUser = (pictureTags: string | string[]) => {
  let tagList: string[] = []
  if (Array.isArray(pictureTags)) {
    tagList = pictureTags
  } else {
    try {
      const parsed = JSON.parse(pictureTags)
      if (Array.isArray(parsed)) {
        tagList = parsed
      }
    } catch (e) {
      console.error('tags 解析失败: ', e)
    }
  }
  return renderTags(tagList)
} // 解析 json 数组字符进行分割

// 设置删除图片的回调
const doDelete = async (id: string) => {
  if (!id) {
    return
  }
  const deleteParams = reactive<WorkCollaborativeImagesAPI.PictureDeleteRequest>({
    id: Number(id),
  })
  const res = await pictureDelete(deleteParams)
  if (res.data.code === 20000) {
    message.success('删除成功')
    await getTableData() // 刷新数据
  } else {
    message.error(res.data.message)
  }
}

// 设置审核回调
const handleReview = async (record: WorkCollaborativeImagesAPI.PictureVO, reviewStatus: number) => {
  const reviewMessage = reviewStatus === PIC_REVIEW_STATUS_ENUM.PASS ? '管理员操作通过' : '管理员操作拒绝'
  const res = await pictureReview({
    id: record.id,
    reviewStatus,
    reviewMessage,
  })
  if (res.data.code === 20000) {
    message.success('审核操作成功')
    await getTableData() // 重新获取列表
  } else {
    message.error('审核操作失败，' + res.data.message)
  }
}
</script>

<template>
  <div id="pictureManagePage">
    <!-- 管理标题 -->
    <a-flex justify="space-between">
      <h2>图片管理</h2>
      <a-button href="/picture/add" target="_blank" type="primary">+ 创建图片</a-button>
    </a-flex>
    <!-- 搜索表单 -->
    <a-form :model="searchParams" layout="vertical" @finish="doSearch">
      <!-- 快速可用部分 -->
      <a-form-item label="名字">
        <a-input v-model:value="searchParams.name" allow-clear placeholder="输入名字" />
      </a-form-item>
      <!-- 展开可用部分 -->
      <template v-if="showMore">
        <a-form-item label="昵称">
          <a-input v-model:value="searchParams.introduction" allow-clear placeholder="输入简介" />
        </a-form-item>
      </template>
      <template v-if="showMore">
        <a-form-item label="状态">
          <a-select
            v-model:value="searchParams.reviewStatus"
            allow-clear
            placeholder="选择状态"
          >
            <a-select-option :value="0">待审核</a-select-option>
            <a-select-option :value="1">已通过</a-select-option>
            <a-select-option :value="2">已拒绝</a-select-option>
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
    <!-- 图片列表 -->
    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="paginationConfig"
      :scroll="{ x: 'max-content' }"
      rowKey="id"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <!-- 图片展示 -->
        <template v-if="column.dataIndex === 'url'">
          <a-image :src="record.url" :width="120" />
        </template>
        <!-- 图片信息 -->
        <template v-if="column.dataIndex === 'picInfo'">
          <a-tooltip :title="record.url">
            链接:
            <a-typography-text copyable>
              {{ record.url.slice(0, 30) }}{{ record.url.length > 30 ? '...' : '' }}
            </a-typography-text>
          </a-tooltip>
          <div>格式: {{ record.picFormat }}</div>
          <div>宽度: {{ record.picWidth }}</div>
          <div>高度: {{ record.picHeight }}</div>
          <div>宽高比: {{ record.picScale }}</div>
          <div>估算体积: {{ (record.picSize / 1024).toFixed(2) }}KB</div>
        </template>
        <!-- 审核信息 -->
        <template v-if="column.dataIndex === 'reviewInfo'">
          <div>审核状态：{{ PIC_REVIEW_STATUS_MAP[record.reviewStatus as 0 | 1 | 2] }}</div>
          <div>审核信息：{{ record.reviewMessage }}</div>
          <div>审核人员：{{ record.reviewerId }}</div>
        </template>
        <!-- 创建时间 -->
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <!-- 修改时间 -->
        <template v-else-if="column.dataIndex === 'editTime'">
          {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <!-- 具体操作 -->
        <template v-else-if="column.key === 'action'">
          <a-button
            v-if="record.reviewStatus !== PIC_REVIEW_STATUS_ENUM.PASS"
            type="link"
            @click="handleReview(record, PIC_REVIEW_STATUS_ENUM.PASS)"
          >
            通过
          </a-button>
          <a-button
            v-if="record.reviewStatus !== PIC_REVIEW_STATUS_ENUM.REJECT"
            type="link"
            danger
            @click="handleReview(record, PIC_REVIEW_STATUS_ENUM.REJECT)"
          >
            拒绝
          </a-button>
          |
          <a-button :href="`/picture/add?id=${record.id}`" target="_blank" type="link"
            >编辑
          </a-button>
          <a-button danger type="link" @click="doDelete(record.id)">删除</a-button>
        </template>
        <!-- 标签渲染 -->
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
      </template>
    </a-table>
  </div>
</template>

<style scoped></style>
