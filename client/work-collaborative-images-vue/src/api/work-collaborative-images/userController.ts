// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 添加用户网络接口(管理) POST /user/add */
export async function userAdd(
  body: WorkCollaborativeImagesAPI.UserAddRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseUser>('/user/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 删除用户网络接口(管理) POST /user/delete */
export async function userDelete(
  body: WorkCollaborativeImagesAPI.UserDeleteRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseBoolean>('/user/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 封禁用户网络接口(管理) POST /user/disable */
export async function userDisable(
  body: WorkCollaborativeImagesAPI.UserDisableRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseBoolean>('/user/disable', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 获取登录信息网络接口 GET /user/info */
export async function userInfo(options?: { [key: string]: any }) {
  return request<WorkCollaborativeImagesAPI.BaseResponseUserVO>('/user/info', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 用户登入网络接口, 已经脱敏 POST /user/login */
export async function userLogin(
  body: WorkCollaborativeImagesAPI.UserLoginRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseUserVO>('/user/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 用户登出网络接口 POST /user/logout */
export async function userLogout(options?: { [key: string]: any }) {
  return request<WorkCollaborativeImagesAPI.BaseResponseBoolean>('/user/logout', {
    method: 'POST',
    ...(options || {}),
  })
}

/** 用户注册网络接口 POST /user/register */
export async function userRegister(
  body: WorkCollaborativeImagesAPI.UserRegisterRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseBoolean>('/user/register', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 查询用户网络接口(管理) POST /user/search */
export async function userSearch(
  body: WorkCollaborativeImagesAPI.UserSearchRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponsePageUser>('/user/search', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 获取登录状态网络接口 GET /user/status */
export async function userStatus(options?: { [key: string]: any }) {
  return request<WorkCollaborativeImagesAPI.BaseResponseUserStatus>('/user/status', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 修改用户网络接口(管理) POST /user/update */
export async function userUpdate(
  body: WorkCollaborativeImagesAPI.UserUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseUser>('/user/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
