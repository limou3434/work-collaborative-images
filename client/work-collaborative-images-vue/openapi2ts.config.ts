export default [
  // 服务列表
  {
    requestLibPath: "import request from '@/request'", // 自定义请求方法路径
    serversPath: './src/api', // 生成的文件夹的路径
    schemaPath: 'http://127.0.0.1:8000/work_collaborative_images_api/v3/api-docs', // 请求的接口配置地址
    projectName: 'work-collaborative-images', // 项目名称
    isCamelCase: true, // 使用驼峰命名
    namespace: 'WorkCollaborativeImagesAPI', // 命名空间名称
  },
]
