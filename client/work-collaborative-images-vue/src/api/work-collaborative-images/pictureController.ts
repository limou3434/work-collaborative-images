// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 👑添加图片网络接口 POST /picture/admin/add */
export async function adminPictureAdd(
  body: WorkCollaborativeImagesAPI.PictureAddRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponsePicture>('/picture/admin/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 👑删除图片网络接口 POST /picture/admin/delete */
export async function adminPictureDelete(
  body: WorkCollaborativeImagesAPI.PictureDeleteRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseBoolean>('/picture/admin/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 👑查询图片网络接口 POST /picture/admin/search */
export async function adminPictureSearch(
  body: WorkCollaborativeImagesAPI.PictureSearchRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponsePagePicture>('/picture/admin/search', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 👑更新图片网络接口 POST /picture/admin/update */
export async function adminPictureUpdate(
  body: WorkCollaborativeImagesAPI.PictureUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponsePicture>('/picture/admin/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 👑图片批量爬取网络接口 POST /picture/batch */
export async function adminPictureBatch(
  body: WorkCollaborativeImagesAPI.PictureBatchRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseInteger>('/picture/batch', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 获取当前后端支持图片类别网络接口 GET /picture/categorys */
export async function pictureCategorys(options?: { [key: string]: any }) {
  return request<WorkCollaborativeImagesAPI.BaseResponseListString>('/picture/categorys', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 根据指定标识销毁图片网络接口 POST /picture/destroy */
export async function pictureDestroy(
  body: WorkCollaborativeImagesAPI.PictureDestroyRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseBoolean>('/picture/destroy', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 查找公有图库或私有空间中图片的网络接口 POST /picture/query */
export async function pictureQuery(
  body: WorkCollaborativeImagesAPI.PictureQueryRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponsePagePictureVO>('/picture/query', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 👑审核图片网络接口 POST /picture/review */
export async function adminPictureReview(
  body: WorkCollaborativeImagesAPI.PictureReviewRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseBoolean>('/picture/review', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 已脱敏的图片上传网络接口 POST /space/upload/ */
export async function pictureUpload(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: WorkCollaborativeImagesAPI.pictureUploadParams,
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

  return request<WorkCollaborativeImagesAPI.BaseResponsePictureVO>('/picture/upload', {
    method: 'POST',
    headers: {
      // 不需要手动设置 Content-Type 为 multipart/form-data
      // 浏览器会自动为我们处理 boundary。
    },
    data: formData, // 使用 FormData 作为请求体
    ...(options || {}),
  })
}
