// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 👑添加空间用户关联网络接口 POST /space_user/admin/add */
export async function adminSpaceUserAdd(
  body: WorkCollaborativeImagesAPI.SpaceUserAddRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseSpaceUser>('/space_user/admin/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 👑删除空间用户关联网络接口 POST /space_user/admin/delete */
export async function adminSpaceUserDelete(
  body: WorkCollaborativeImagesAPI.SpaceUserDeleteRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseBoolean>('/space_user/admin/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 👑查询空间用户关联网络接口 POST /space_user/admin/search */
export async function adminSpaceUserSearch(
  body: WorkCollaborativeImagesAPI.SpaceUserSearchRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponsePageSpaceUser>('/space_user/admin/search', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 👑更新空间用户关联网络接口 POST /space_user/admin/update */
export async function adminSpaceUserUpdate(
  body: WorkCollaborativeImagesAPI.SpaceUserUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseSpaceUser>('/space_user/admin/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 对指定的协作空间编辑成员网络接口 POST /space_user/edit */
export async function spaceUserEdit(
  body: WorkCollaborativeImagesAPI.SpaceUserEditRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseSpaceUserVO>('/space_user/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 对指定的协作空间移进成员网络接口 POST /space_user/move/in */
export async function spaceUserMoveIn(
  body: WorkCollaborativeImagesAPI.SpaceUserMoveInRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseSpaceUser>('/space_user/move/in', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 对指定的协作空间移出成员网络接口 POST /space_user/move/out */
export async function spaceUserMoveOut(
  body: WorkCollaborativeImagesAPI.SpaceUserMoveOutRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseBoolean>('/space_user/move/out', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 获取当前登录用户已经加入的所有协作空间的相关记录 GET /space_user/page/my_collaborative_space */
export async function spaceUserPageMyCollaborativeSpace(options?: { [key: string]: any }) {
  return request<WorkCollaborativeImagesAPI.BaseResponseListSpaceUserVO>(
    '/space_user/page/my_collaborative_space',
    {
      method: 'GET',
      ...(options || {}),
    }
  )
}

/** 获取当前登录用户的协作空间中所有的成员信息网络接口 GET /space_user/page/user */
export async function spaceUserPageUser(options?: { [key: string]: any }) {
  return request<WorkCollaborativeImagesAPI.BaseResponsePageUserVO>('/space_user/page/user', {
    method: 'GET',
    ...(options || {}),
  })
}
