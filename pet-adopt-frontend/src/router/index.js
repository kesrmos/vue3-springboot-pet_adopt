import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/userStore'

const routes = [
  // ---------------- 前台 ----------------
  {
    path: '/',
    component: () => import('@/views/userView/FrontLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/userView/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'petList',
        name: 'PetList',
        component: () => import('@/views/userView/PetList.vue'),
        meta: { title: '宠物列表' }
      },
      {
        path: 'pet/:id',
        name: 'PetDetail',
        component: () => import('@/views/userView/PetDetail.vue'),
        meta: { title: '宠物详情' }
      },
      {
        path: 'apply',
        name: 'Apply',
        component: () => import('@/views/userView/Apply.vue'),
        meta: { title: '领养申请', requiresAuth: true }
      },
      {
        path: 'myApply',
        name: 'MyApply',
        component: () => import('@/views/userView/MyApply.vue'),
        meta: { title: '我的申请', requiresAuth: true }
      },
      {
        path: 'about',
        name: 'About',
        component: () => import('@/views/userView/About.vue'),
        meta: { title: '领养须知' }
      }
    ]
  },

  // ---------------- 登录 ----------------
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/userView/Login.vue'),
    meta: { title: '用户登录' }
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/adminView/AdminLogin.vue'),
    meta: { title: '管理员登录' }
  },

  // ---------------- 后台 ----------------
  {
    path: '/admin',
    component: () => import('@/views/adminView/AdminLayout.vue'),
    redirect: '/admin/petManage',
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'petManage',
        name: 'PetManage',
        component: () => import('@/views/adminView/PetManage.vue'),
        meta: { title: '宠物管理' }
      },
      {
        path: 'applyManage',
        name: 'ApplyManage',
        component: () => import('@/views/adminView/ApplyManage.vue'),
        meta: { title: '领养审核' }
      },
      {
        path: 'userManage',
        name: 'UserManage',
        component: () => import('@/views/adminView/UserManage.vue'),
        meta: { title: '用户管理' }
      }
    ]
  },

  // 兜底：路径写错时给出明确提示，而不是静默跳回首页
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 })
})

/**
 * 全局路由守卫：
 * 1. 需要登录但未登录 → 跳转对应登录页并记录 redirect
 * 2. 需要管理员但角色不是 admin → 提示并回首页
 * 3. 已登录再访问登录页 → 按角色回到对应首页
 */
router.beforeEach((to) => {
  const userStore = useUserStore()

  document.title = to.meta?.title
    ? `${to.meta.title} - 宠物领养之家`
    : '宠物领养之家'

  const isAdminArea = to.path.startsWith('/admin') && to.name !== 'AdminLogin'
  const isLoginPage = to.name === 'Login' || to.name === 'AdminLogin'

  // 已登录访问登录页
  if (userStore.isLogin && isLoginPage) {
    return userStore.isAdmin ? '/admin/petManage' : '/'
  }

  // 未登录访问受限页面
  if (to.meta.requiresAuth && !userStore.isLogin) {
    ElMessage.warning('请先登录')
    return {
      path: isAdminArea ? '/admin/login' : '/login',
      query: { redirect: to.fullPath }
    }
  }

  // 普通用户访问后台页面
  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    ElMessage.warning('权限不足，仅管理员可访问')
    return '/'
  }

  return true
})

export default router
