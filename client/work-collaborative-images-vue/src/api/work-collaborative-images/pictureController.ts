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

/** 销毁图片网络接口 POST /picture/destroy */
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

/** 创建智能绘画任务 POST /picture/out_painting/create_task */
export async function pictureOutPaintingCreateTask(
  body: WorkCollaborativeImagesAPI.PictureCreateOutPaintingTaskRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseCreateOutPaintingTaskResponse>(
    '/picture/out_painting/create_task',
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      data: body,
      ...(options || {}),
    }
  )
}

/** 查询智能绘画任务 GET /picture/out_painting/get_task */
export async function pictureOutPaintingGetTask(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: WorkCollaborativeImagesAPI.pictureOutPaintingGetTaskParams,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseGetOutPaintingTaskResponse>(
    '/picture/out_painting/get_task',
    {
      method: 'GET',
      params: {
        ...params,
      },
      ...(options || {}),
    }
  )
}

/** 查找图片网络接口 POST /picture/query */
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

/** 利用某个图片的唯一标识来搜索同色的图片 POST /picture/search/color */
export async function pictureSearchColor(
  body: WorkCollaborativeImagesAPI.PictureSearchColorRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseListPictureVO>('/picture/search/color', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 利用某个图片的唯一标识来搜索相似的图片 POST /picture/search/picture */
export async function pictureSearchPicture(
  body: WorkCollaborativeImagesAPI.PictureSearchPictureRequest,
  options?: { [key: string]: any }
) {
  return request<WorkCollaborativeImagesAPI.BaseResponseListImageSearchResult>(
    '/picture/search/picture',
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      data: body,
      ...(options || {}),
    }
  )
}

/** 上传图片网络接口 POST /picture/upload */
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
