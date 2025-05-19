export default [
  // 服务列表
  {
    requestLibPath: "import request from '@/request'", // 自定义请求方法路径
    serversPath: './src/api', // 生成的文件夹的路径
    schemaPath: 'http://127.0.0.1:8000/v3/api-docs', // 请求的接口配置地址
    projectName: 'work-collaborative-images', // 项目名称
    isCamelCase: true, // 使用驼峰命名
    namespace: 'WorkCollaborativeImagesAPI', // 命名空间名称
  },
]

// 下面是一些需要自己自定义的网路接口...
// /** 已脱敏的图片上传网络接口 POST /space/upload/ */
// export async function pictureUpload(
//   // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
//   params: WorkCollaborativeImagesAPI.pictureUploadParams,
//   body?: {file: File},
//   options?: { [key: string]: any }
// ) {
//   const formData = new FormData();
//
//   // 将参数添加到 FormData
//   if (params != null) {
//     for (const key in params) {
//       if (params.hasOwnProperty(key)) {
//         formData.append(key, (params as { [key: string]: any })[key]); // 类型断言
//       }
//     }
//   }
//
//   // 将文件添加到 FormData
//   if (body != null) {
//     formData.append('pictureFile', body.file);
//   }
//
//   return request<WorkCollaborativeImagesAPI.BaseResponsePictureVO>('/space/upload', {
//     method: 'POST',
//     headers: {
//       // 不需要手动设置 Content-Type 为 multipart/form-data
//       // 浏览器会自动为我们处理 boundary。
//     },
//     data: formData, // 使用 FormData 作为请求体
//     ...(options || {}),
//   })
// }
