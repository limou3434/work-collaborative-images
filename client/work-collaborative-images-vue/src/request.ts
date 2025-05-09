import axios from 'axios'
import { message } from 'ant-design-vue'

// 创建 Axios 实例
const myAxios = axios.create({
  baseURL: 'http://127.0.0.1:8000/work_collaborative_images_api',
  timeout: 60000, // 响应时间未 6 s
  withCredentials: true, // 请求时携带 Cookie
})

// 全局请求拦截器
myAxios.interceptors.request.use(
  function (config) {
    console.debug('触发全局请求拦截器 - 此时请求为', config)
    return config
  },
  function (error) {
    console.debug('触发全局请求拦截器 - 此时错误为', error)
    return Promise.reject(error)
  },
)

// 全局响应拦截器
myAxios.interceptors.response.use(
  // 响应成功(2xx 响应触发)
  function (response) {
    const { data } = response
    console.debug('触发全局响应拦截器 - 此时响应为', data)
    // 未登录则跳转到登录页面
    if (data.code === 40100 && !window.location.pathname.includes('/user/login')) {
      void message.warning('请先登录')
      window.location.href = `/user/login?redirect=${window.location.href}`
    }
    return response
  },

  // 响应失败(非 2xx 响应触发)
  function (error) {
    console.debug('触发全局响应拦截器 - 此时错误为', error)
    return Promise.reject(error)
  },
)

export default myAxios
