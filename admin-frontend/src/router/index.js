import { createRouter, createWebHistory } from 'vue-router'
import { me } from '../api/auth'

const LoginView = () => import('../views/LoginView.vue')
const RegisterView = () => import('../views/RegisterView.vue')
const AdminLayout = () => import('../components/AdminLayout.vue')
const ProductManageView = () => import('../views/ProductManageView.vue')

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', component: LoginView },
    { path: '/register', component: RegisterView },
    {
      path: '/admin',
      component: AdminLayout,
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        { path: '', redirect: '/admin/products' },
        { path: 'products', component: ProductManageView, meta: { requiresAuth: true, requiresAdmin: true } },
      ]
    },
  ],
})

// 获取或刷新当前用户
async function ensureCurrentUser() {
  const cached = localStorage.getItem('currentUser')
  if (cached) {
    try { return JSON.parse(cached) } catch (_) { /* fallthrough */ }
  }
  try {
    const { data } = await me()
    if (data) {
      localStorage.setItem('currentUser', JSON.stringify(data))
      return data
    }
  } catch (_) {
    // 未登录或接口失败，返回 null
  }
  return null
}

router.beforeEach(async (to, from, next) => {
  const needAuth = to.matched.some(r => r.meta && r.meta.requiresAuth)
  const needAdmin = to.matched.some(r => r.meta && r.meta.requiresAdmin)

  if (!needAuth && !needAdmin) return next()

  const user = await ensureCurrentUser()
  if (!user) {
    return next({ path: '/login', query: { redirect: to.fullPath } })
  }

  if (needAdmin && Number(user.type) !== 1) {
    return next({ path: '/login', query: { reason: 'forbidden' } })
  }

  next()
})

export default router