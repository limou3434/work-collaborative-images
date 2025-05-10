// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 获取当前后端支持的图片类别列表 GET /picture/categorys */
export async function pictureCategorys(options?: { [key: string]: any }) {
  return request<WorkCollaborativeImagesAPI.BaseResponseListString>('/picture/categorys', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 未脱敏的图片查询网络接口(管理) POST /picture/search */
export async function pictureSearch(
  body: WorkCollaborativeImagesAPI.PictureSearchRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseListPicture>('/picture/search', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 已脱敏的图片查询网络接口 POST /picture/search/vo */
export async function pictureSearchVo(
  body: WorkCollaborativeImagesAPI.PictureSearchRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseListPictureVO>('/picture/search/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 未脱敏的图片上传网络接口(管理) POST /picture/upload */
export async function pictureUpload(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: WorkCollaborativeImagesAPI.pictureUploadParams,
  body: {},
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponsePicture>('/picture/upload', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    params: {
      ...params,
    },
    data: body,
    ...(options || {}),
  })
}

/** 已脱敏的图片上传网络接口 POST /picture/upload/vo */
export async function pictureUploadVo(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: WorkCollaborativeImagesAPI.pictureUploadVOParams,
  body?: {file: File},
  options?: { [key: string]: any }
) {
  const formData = new FormData();

  // 将参数添加到 FormData
  if (params != null) {
    for (const key in params) {
      if (params.hasOwnProperty(key)) {
        formData.append(key, (params as { [key: string]: any })[key]); // 类型断言
      }
    }
  }

  // 将文件添加到 FormData
  if (body != null) {
    formData.append('pictureFile', body.file);
  }

  return request<WorkCollaborativeImagesAPI.BaseResponsePictureVO>('/picture/upload/vo', {
    method: 'POST',
    headers: {
      // 不需要手动设置 Content-Type 为 multipart/form-data
      // 浏览器会自动为我们处理 boundary。
    },
    data: formData, // 使用 FormData 作为请求体
    ...(options || {}),
  })
}

