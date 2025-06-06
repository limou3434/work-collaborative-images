// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 👑添加空间网络接口 POST /space/admin/add */
export async function adminSpaceAdd(
  body: WorkCollaborativeImagesAPI.SpaceAddRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseSpace>('/space/admin/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 👑删除空间网络接口 POST /space/admin/delete */
export async function adminSpaceDelete(
  body: WorkCollaborativeImagesAPI.SpaceDeleteRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseBoolean>('/space/admin/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 👑查询空间网络接口 POST /space/admin/search */
export async function adminSpaceSearch(
  body: WorkCollaborativeImagesAPI.SpaceSearchRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponsePageSpace>('/space/admin/search', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 👑更新空间网络接口 POST /space/admin/update */
export async function adminSpaceUpdate(
  body: WorkCollaborativeImagesAPI.SpaceUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseSpace>('/space/admin/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 创建用户专属空间(私有空间/协作空间)网络接口 POST /space/create */
export async function spaceCreate(
  body: WorkCollaborativeImagesAPI.SpaceCreateRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseSpaceVO>('/space/create', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 销毁用户专属空间(私有空间/协作空间)网络接口 POST /space/destroy */
export async function spaceDestroy(
  body: WorkCollaborativeImagesAPI.SpaceDestroyRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseBoolean>('/space/destroy', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 编辑用户专属空间(私有空间/协作空间)网络接口 POST /space/edit */
export async function spaceEdit(
  body: WorkCollaborativeImagesAPI.SpaceEditRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseSpaceVO>('/space/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 获取空间等级描述网络接口 POST /space/level */
export async function spaceLevel(options?: { [key: string]: any }) {
  return request<WorkCollaborativeImagesAPI.BaseResponseListSpaceLevelInfo>('/space/level', {
    method: 'POST',
    ...(options || {}),
  })
}

/** 查找用户专属空间(私有空间/协作空间)网络接口 POST /space/query */
export async function spaceQuery(
  body: WorkCollaborativeImagesAPI.SpaceQueryRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseSpaceVO>('/space/query', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
