import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../views/HomePage.vue'

/**
 * 默认使用设置路由而不是约定路由
 */
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: '公共图库',
      component: HomePage,
    },
    {
      path: '/self',
      name: '私有空间',
      component: () => import('../views/SelfPage.vue'), // 懒加载页面
    },
    {
      path: '/collaborative',
      name: '协作空间',
      component: () => import('../views/CollaborativePage.vue'), // 懒加载页面
    },
    {
      path: '/about',
      name: '关于其他',
      component: () => import('../views/AboutPage.vue'), // 懒加载页面
    },
    {
      path: '/user/login',
      name: '用户登录',
      component: () => import('../views/user/UserLoginPage.vue'), // 懒加载页面
    },
    {
      path: '/user/register',
      name: '用户注册',
      component: () => import('../views/user/UserRegisterPage.vue'), // 懒加载页面
    },
    {
      path: '/operate/picture/add',
      name: '添加图片',
      component: () => import('../views/picture/AddPicturePage.vue'), // 懒加载页面
    },
    {
      path: '/operate/space/add',
      name: '添加空间',
      component: () => import('../views/space/AddSpacePage.vue'), // 懒加载页面
    },
    {
      path: '/picture/:id',
      name: '图片详情',
      component: () => import('../views/picture/PictureDetailPage.vue'), // 懒加载页面
      props: true,
    },
    {
      path: '/space/:id',
      name: '空间详情',
      component: () => import('../views/space/SpaceDetailPage.vue'), // 懒加载页面
      props: true,
    },
    {
      path: '/admin/user',
      name: '用户管理',
      component: () => import('../views/admin/UserManagePage.vue'), // 懒加载页面
    },
    {
      path: '/admin/picture',
      name: '图片管理',
      component: () => import('../views/admin/PictureManagePage.vue'), // 懒加载页面
    },
    {
      path: '/admin/space',
      name: '空间管理',
      component: () => import('../views/admin/SpaceManagePage.vue'), // 懒加载页面
    },
  ],
})

export default router
