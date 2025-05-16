// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 空间添加网络接口(管理) POST /space/add */
export async function spaceAdd(
  body: WorkCollaborativeImagesAPI.SpaceAddRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseSpace>('/space/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 空间删除网络接口(管理) POST /space/delete */
export async function spaceDelete(
  body: WorkCollaborativeImagesAPI.SpaceDeleteRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseBoolean>('/space/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 用户编辑空间网络接口 POST /space/edit */
export async function spaceEdit(options?: { [key: string]: any }) {
  return request<WorkCollaborativeImagesAPI.BaseResponseSpaceVO>('/space/edit', {
    method: 'POST',
    ...(options || {}),
  })
}

/** 空间查询网络接口(管理) POST /space/search */
export async function spaceSearch(
  body: WorkCollaborativeImagesAPI.SpaceSearchRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponsePageSpace>('/space/search', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 空间更新网络接口(管理) POST /space/update */
export async function spaceUpdate(
  body: WorkCollaborativeImagesAPI.SpaceUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseSpace>('/space/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 用户创建空间网络接口 POST /space/user/create */
export async function spaceCreate(options?: { [key: string]: any }) {
  return request<WorkCollaborativeImagesAPI.BaseResponseSpaceVO>('/space/user/create', {
    method: 'POST',
    ...(options || {}),
  })
}

/** 用户销毁空间网络接口 POST /space/user/destroy */
export async function spaceDestroy(options?: { [key: string]: any }) {
  return request<WorkCollaborativeImagesAPI.BaseResponseBoolean>('/space/user/destroy', {
    method: 'POST',
    ...(options || {}),
  })
}

/** 用户查询空间网络接口 POST /space/user/query */
export async function spaceQuery(options?: { [key: string]: any }) {
  return request<WorkCollaborativeImagesAPI.BaseResponsePageSpaceVO>('/space/user/query', {
    method: 'POST',
    ...(options || {}),
  })
}
