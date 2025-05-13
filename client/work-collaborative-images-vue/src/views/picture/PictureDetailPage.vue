<script lang="ts" setup>
import { message } from 'ant-design-vue'
import { computed, onMounted, reactive, ref } from 'vue'
import {
  pictureDelete,
  pictureSearchVo,
} from '@/api/work-collaborative-images/pictureController.ts'
import ShareModal from '@/components/ShareModal.vue'
import { formatSize } from '@/utils'
import {
  DeleteOutlined,
  DownloadOutlined,
  EditOutlined,
  ShareAltOutlined,
} from '@ant-design/icons-vue'
import router from '@/router'
import { useLoginUserStore } from '@/stores/loginUser.ts'

// 获取从路径上得到的图片 id 参数
const props = defineProps<{
  id: number
}>()

// 根据从页面上获取到的 id 来查询图片详情
const searchParams = reactive<WorkCollaborativeImagesAPI.PictureSearchRequest>({
  id: props.id,
}) // 存储初始化的查询参数, 后续用来做搜索请求
const picture = ref<WorkCollaborativeImagesAPI.PictureVO>({})
const fetchPictureDetail = async () => {
  try {
    const res = await pictureSearchVo(searchParams)
    if (res.data.code === 20000 && res.data.data && res.data.data.records) {
      if(res.data.data.records.length === 0) {
        message.error('图片不存在')
        return
      }
      picture.value = res.data.data.records[0]
    } else {
      message.error(res.data.message)
    }
  } catch (e: any) {
    message.error('获取图片详情失败：' + e.message)
  }
}
onMounted(() => {
  fetchPictureDetail()
}) // 在页面加载时加载图片

// 编辑权限
const loginUserStore = useLoginUserStore()
const canEdit = computed(() => {
  const loginUser = loginUserStore.loginUser;
  // 未登录不可编辑
  if (!loginUser.id) {
    return false
  }
  // 仅本人或管理员可编辑
  const userVO = picture.value.userVO || {}
  return loginUser.id === userVO.id || loginUser.role === 1
})

// 下载图片
const doDownload = () => {
  message.info('下载功能待实现')
}

// 分享图片
const shareModalRef = ref()
const shareLink = ref<string>('')
const doShare = () => {
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture/${picture.value.id}`
  if (shareModalRef.value) {
    shareModalRef.value.openModal()
  }

  try {
    navigator.clipboard.writeText(shareLink.value)
    message.success('链接已复制到剪切板, 快去粘贴分享吧~')
  } catch (e: any) {
    message.warning('当前环境无法复制链接, 请手动复制', e)
  }
}

// 编辑图片
const doEdit = () => {
  router.push('/picture/add?id=' + picture.value.id)
}

// 删除图片
const doDelete = async () => {
  const id = picture.value.id
  if (!id) {
    return
  }
  const deletePicture: WorkCollaborativeImagesAPI.PictureDeleteRequest = {
    id: -1,
  }
  deletePicture.id = id
  const res = await pictureDelete(deletePicture)
  if (res.data.code === 20000) {
    message.success('删除成功')
  } else {
    message.error(res.data.message)
  }
}
</script>

<template>
  <a-row :gutter="[16, 16]">
    <!-- 图片展示区 -->
    <a-col :md="16" :sm="24" :xl="18">
      <a-card
        style="box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15); border: 1px solid #f0f0f0; height: 700px"
        title="图片预览"
      >
        <template #default>
          <div style="height: 600px; display: flex; justify-content: center; align-items: center">
            <a-image
              :src="picture.url"
              style="max-height: 600px; max-width: 100%; object-fit: contain"
            />
          </div>
        </template>
      </a-card>
    </a-col>
    <!-- 图片信息区 -->
    <a-col :md="8" :sm="24" :xl="6">
      <a-card
        style="box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15); border: 1px solid #f0f0f0"
        title="图片信息"
      >
        <a-descriptions :column="1">
          <a-descriptions-item label="作者">
            <a-space>
              <a-avatar :size="24" :src="picture.userVO?.avatar" />
              <div>{{ picture.userVO?.name }}</div>
            </a-space>
          </a-descriptions-item>
          <a-descriptions-item label="名称">
            {{ picture.name ?? '无名称' }}
          </a-descriptions-item>
          <a-descriptions-item label="简介">
            {{ picture.introduction ?? '无简介' }}
          </a-descriptions-item>
          <a-descriptions-item label="分类">
            {{ picture.category ?? '无分类' }}
          </a-descriptions-item>
          <a-descriptions-item label="格式">
            {{ picture.picFormat ?? '未知' }}
          </a-descriptions-item>
          <a-descriptions-item label="宽度">
            {{ picture.picWidth ?? '未知' }}
          </a-descriptions-item>
          <a-descriptions-item label="高度">
            {{ picture.picHeight ?? '未知' }}
          </a-descriptions-item>
          <a-descriptions-item label="宽高比">
            {{ picture.picScale ?? '未知' }}
          </a-descriptions-item>
          <a-descriptions-item label="标签">
            <template v-if="JSON.parse(picture.tags ?? '[]').length > 3">
              <!-- 显示前三个标签 -->
              <a-tag v-for="tag in JSON.parse(picture.tags ?? '[]').slice(0, 3)" :key="tag"
                >{{ tag }}
              </a-tag>
              <!-- 如果超过三个标签，显示 "..." -->
              <a-tooltip :title="JSON.parse(picture.tags ?? '[]').join(', ')">
                <a-tag>...</a-tag>
              </a-tooltip>
            </template>
            <template v-else>
              <!-- 标签数量少于或等于 3 个, 直接显示全部标签 -->
              <a-tag v-for="tag in JSON.parse(picture.tags ?? '[]')" :key="tag">{{ tag }}</a-tag>
            </template>
          </a-descriptions-item>
          <a-descriptions-item label="大小">
            {{ formatSize(picture.picSize) }}
          </a-descriptions-item>
        </a-descriptions>
        <!-- 图片操作 -->
        <a-space direction="vertical">
          <a-button ghost type="primary" @click="doShare">
            分享
            <template #icon>
              <ShareAltOutlined />
            </template>
          </a-button>
          <a-button v-if="canEdit" type="default" @click="doEdit">
            编辑
            <template #icon>
              <EditOutlined />
            </template>
          </a-button>
          <a-button v-if="canEdit" danger @click="doDelete">
            删除
            <template #icon>
              <DeleteOutlined />
            </template>
          </a-button>
          <a-button type="primary" @click="doDownload">
            下载
            <template #icon>
              <DownloadOutlined />
            </template>
          </a-button>
        </a-space>
      </a-card>
    </a-col>
  </a-row>
  <ShareModal ref="shareModalRef" :link="shareLink" title="分享图片" />
</template>

<style scoped></style>
