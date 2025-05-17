// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 空间添加网络接口(管理) POST /space/admin/add */
export async function adminSpaceAdd(
  body: WorkCollaborativeImagesAPI.AdminSpaceAddRequest,
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

/** 空间删除网络接口(管理) POST /space/admin/delete */
export async function adminSpaceDelete(
  body: WorkCollaborativeImagesAPI.AdminSpaceDeleteRequest,
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

/** 空间查询网络接口(管理) POST /space/admin/search */
export async function adminSpaceSearch(
  body: WorkCollaborativeImagesAPI.AdminSpaceSearchRequest,
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

/** 空间更新网络接口(管理) POST /space/admin/update */
export async function adminSpaceUpdate(
  body: WorkCollaborativeImagesAPI.AdminSpaceUpdateRequest,
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

/** 创建空间网络接口 POST /space/create */
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

/** 销毁空间网络接口 POST /space/destroy */
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

/** 编辑空间网络接口 POST /space/edit */
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

/** 查找空间网络接口 POST /space/query */
export async function spaceQuery(
  body: WorkCollaborativeImagesAPI.SpaceQueryRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponsePageSpaceVO>('/space/query', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
