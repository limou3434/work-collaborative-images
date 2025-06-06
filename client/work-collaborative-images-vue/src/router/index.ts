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
      name: '公共页面',
      component: HomePage,
    },
    {
      path: '/self',
      name: '私有页面',
      component: () => import('../views/SelfPage.vue'), // 懒加载页面
    },
    {
      path: '/collaborative',
      name: '协作页面',
      component: () => import('../views/CollaborativePage.vue'), // 懒加载页面
    },
    {
      path: '/about',
      name: '关于页面',
      component: () => import('../views/AboutPage.vue'), // 懒加载页面
    },
    {
      path: '/user',
      name: '用户页面',
      children: [
        {
          path: 'login',
          name: '用户登录',
          component: () => import('../views/user/UserLoginPage.vue'), // 懒加载页面
        },
        {
          path: 'register',
          name: '用户注册',
          component: () => import('../views/user/UserRegisterPage.vue'), // 懒加载页面
        },
      ],
    },
    {
      path: '/picture',
      name: '图片页面',
      children: [
        {
          path: ':id',
          name: '图片详情',
          component: () => import('../views/picture/PictureDetailPage.vue'), // 懒加载页面
          props: true,
        },
      ],
    },
    {
      path: '/operate',
      name: '操作页面',
      children: [
        {
          path: 'picture/add',
          name: '添加图片',
          component: () => import('../views/picture/AddPicturePage.vue'), // 懒加载页面
        },
        {
          path: 'space/add',
          name: '添加空间',
          component: () => import('../views/space/AddSpacePage.vue'), // 懒加载页面
        },
      ],
    },
    {
      path: '/admin',
      name: '管理页面',
      children: [
        {
          path: 'user',
          name: '用户管理',
          component: () => import('../views/admin/UserManagePage.vue'),
        },
        {
          path: 'picture',
          name: '图片管理',
          component: () => import('../views/admin/PictureManagePage.vue'),
        },
        {
          path: 'space',
          name: '空间管理',
          component: () => import('../views/admin/SpaceManagePage.vue'),
        },
      ],
    },
  ],
})

export default router
