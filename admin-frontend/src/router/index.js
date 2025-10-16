import { createRouter, createWebHistory } from 'vue-router'
import AdminLogin from '@/views/AdminLogin.vue'
import JobPublish from '@/views/JobPublish.vue'
import Dashboard from '@/views/Dashboard.vue'
import ApplicationManagement from '@/views/ApplicationManagement.vue'
import JobManagement from '@/views/JobManagement.vue'
import JobEdit from '@/views/JobEdit.vue'
import AdminChat from '@/views/AdminChat.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'AdminLogin',
      component: AdminLogin,
      meta: { requiresAuth: false }
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: Dashboard,
      meta: { requiresAuth: true }
    },
    {
      path: '/job-publish',
      name: 'JobPublish',
      component: JobPublish,
      meta: { requiresAuth: true }
    },
    {
      path: '/applications',
      name: 'ApplicationManagement',
      component: ApplicationManagement,
      meta: { requiresAuth: true }
    },
    {
      path: '/jobs',
      name: 'JobManagement',
      component: JobManagement,
      meta: { requiresAuth: true }
    },
    {
      path: '/jobs/edit/:id',
      name: 'JobEdit',
      component: JobEdit,
      meta: { requiresAuth: true }
    },
    {
      path: '/chat',
      name: 'AdminChat',
      component: AdminChat,
      meta: { requiresAuth: true }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('adminToken')
  
  if (to.meta.requiresAuth) {
    if (!token) {
      next('/login')
      return
    }
  }
  
  // 如果已登录用户访问登录页面，重定向到仪表板
  if (to.name === 'AdminLogin' && token) {
    next('/dashboard')
    return
  }
  
  next()
})

export default router




