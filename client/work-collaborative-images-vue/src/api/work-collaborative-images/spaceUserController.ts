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

/** 在当前登录用户的协作空间中编辑权限网络接口 POST /space_user/edit */
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

/** 在当前登录用户的协作空间中获取单个用户网络接口 GET /space_user/get/user */
export async function spaceUserGetUser(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: WorkCollaborativeImagesAPI.spaceUserGetUserParams,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseUserVO>('/space_user/get/user', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 在当前登录用户的协作空间中移进用户网络接口 POST /space_user/move/in */
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

/** 在当前登录用户的协作空间中移出用户网络接口 POST /space_user/move/out */
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

/** 查询当前登录用户已经加入的协作空间列表网络接口 GET /space_user/page/my_collaborative_space */
export async function spaceUserPageMyCollaborativeSpace(options?: { [key: string]: any }) {
  return request<WorkCollaborativeImagesAPI.BaseResponsePageSpaceVO>(
    '/space_user/page/my_collaborative_space',
    {
      method: 'GET',
      ...(options || {}),
    }
  )
}

/** 在当前登录用户的协作空间中获取用户页面网络接口 GET /space_user/page/user */
export async function spaceUserPageUser(options?: { [key: string]: any }) {
  return request<WorkCollaborativeImagesAPI.BaseResponsePageUserVO>('/space_user/page/user', {
    method: 'GET',
    ...(options || {}),
  })
}
