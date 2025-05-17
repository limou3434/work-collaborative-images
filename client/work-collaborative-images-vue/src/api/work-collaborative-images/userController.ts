// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 添加用户网络接口(管理) POST /user/admin/add */
export async function userAdd(
  body: WorkCollaborativeImagesAPI.UserAddRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseUser>('/user/admin/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 删除用户网络接口(管理) POST /user/admin/delete */
export async function adminUserDelete(
  body: WorkCollaborativeImagesAPI.UserDeleteRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseBoolean>('/user/admin/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 封禁用户网络接口(管理) POST /user/admin/disable */
export async function adminUserDisable(
  body: WorkCollaborativeImagesAPI.UserDisableRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseBoolean>('/user/admin/disable', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 查询用户网络接口(管理) POST /user/admin/search */
export async function adminUserSearch(
  body: WorkCollaborativeImagesAPI.UserSearchRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponsePageUser>('/user/admin/search', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 修改用户网络接口(管理) POST /user/admin/update */
export async function adminUserUpdate(
  body: WorkCollaborativeImagesAPI.UserUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseUser>('/user/admin/update', {
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

/** 用户登入网络接口 POST /user/login */
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

/** 获取登录状态网络接口 GET /user/status */
export async function userStatus(options?: { [key: string]: any }) {
  return request<WorkCollaborativeImagesAPI.BaseResponseUserStatus>('/user/status', {
    method: 'GET',
    ...(options || {}),
  })
}
