import { createRouter, createWebHistory } from 'vue-router'

const LoginView = () => import('../views/LoginView.vue')
const RegisterView = () => import('../views/RegisterView.vue')

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', name: 'login', component: LoginView },
  { path: '/register', name: 'register', component: RegisterView }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router