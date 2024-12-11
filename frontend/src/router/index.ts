import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores'
import { ElMessage } from 'element-plus'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      redirect: '/admin',
      component: () => import('../views/layouts/CenterLayout.vue'),
      children: [
        {
          path: '/admin',
          name: 'admin',
          component: () => import('../views/admin/UserAdmin.vue'),
          meta: { title: '用户管理', icon: 'Menu', role: 'admin' }
        },
        {
          path: '/demo',
          name: 'demo',
          component: () => import('../views/admin/DemoPage.vue'),
          meta: { title: '示例页面', icon: 'Menu' }
        }
      ]
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/login/LoginPage.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/login/RegisterPage.vue')
    }
  ]
})

router.beforeEach(async (to) => {
  const userStore = useUserStore()
  // 如果用户未登录且访问的不是登录和注册页面，则重定向到登录页面
  if (!userStore.userInfo && to.path !== '/login' && to.path !== '/register') {
    ElMessage.error('请先登录')
    return '/login'
  }
  // 普通用户访问管理员页面时，重定向到demo页面
  if (to.meta.role === 'admin' && userStore.userInfo?.userRole !== 1) {
    return '/demo'
  }
})

export default router
