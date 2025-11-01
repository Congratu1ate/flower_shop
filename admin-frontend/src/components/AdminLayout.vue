<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { logout } from '../api/auth'

const router = useRouter()
const route = useRoute()

const menus = [
  { key: 'dashboard', label: '仪表盘', icon: 'Grid', path: '/admin' },
  { key: 'products', label: '商品管理', icon: 'ShoppingBag', path: '/admin/products' },
  { key: 'orders', label: '订单管理', icon: 'List', path: '/admin/orders' },
  { key: 'customers', label: '客户管理', icon: 'User', path: '/admin/customers' },
]

function go(path) {
  router.push(path)
}

const currentUser = computed(() => {
  try {
    const raw = localStorage.getItem('currentUser')
    return raw ? JSON.parse(raw) : null
  } catch (_) {
    return null
  }
})

// 将手机号模糊处理，仅显示后四位（例如：*******2949）
function maskPhone(p) {
  if (typeof p !== 'string') return '—'
  const last4 = p.slice(-4)
  return `*******${last4}`
}

async function onLogout() {
  try {
    await logout()
  } catch (_) {
    // 后端未响应也继续本地退出
  }
  // 清除本地缓存并跳转登录
  localStorage.removeItem('currentUser')
  localStorage.removeItem('userAvatar')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<template>
  <div class="admin-shell">
    <aside class="sidebar">
      <div class="user-block">
        <div class="avatar">{{ (currentUser?.name || '花').slice(0,1) }}</div>
        <div class="user-info">
          <div class="name">{{ currentUser?.name || '未登录' }}</div>
          <div class="email">{{ maskPhone(currentUser?.phone) }}</div>
        </div>
      </div>

      <nav class="menu">
        <button v-for="m in menus" :key="m.key" class="menu-item" :class="{ active: route.path.startsWith(m.path) }" @click="go(m.path)">
          <el-icon class="mi"><component :is="m.icon" /></el-icon>
          <span>{{ m.label }}</span>
        </button>
      </nav>

      <div class="bottom-action">
        <el-button type="primary" class="brand-btn" round>浏览网站</el-button>
        <el-button type="primary" class="brand-btn" style="margin-top:8px;margin-left: 0px;" round @click="onLogout">退出登录</el-button>
      </div>
    </aside>

    <main class="content">
      <router-view />
    </main>
  </div>
  
</template>

<style scoped>
.admin-shell { display: grid; grid-template-columns: clamp(220px, 20vw, 260px) 1fr; height: 100vh; background: #f8f4f6; }
.sidebar { background: #f7eaf0; padding: 16px 12px; display:flex; flex-direction:column; gap: 16px; }
.user-block { display:flex; align-items:center; gap:12px; padding:8px; }
.avatar { width:40px; height:40px; border-radius:50%; background:#f06292; color:#fff; display:flex; align-items:center; justify-content:center; font-weight:700; }
.user-info { line-height:1.2; }
.name { font-weight:700; }
.email { font-size:12px; opacity:.7; }
.menu { display:flex; flex-direction:column; gap:4px; margin-top:8px; }
.menu-item { display:flex; align-items:center; gap:10px; padding:10px 12px; border-radius:12px; background: transparent; border:none; cursor:pointer; text-align:left; }
.menu-item:hover { background:#f3dde6; }
.menu-item.active { background:#f06292; color: #fff; }
.menu-item .mi { font-size:18px; }
.bottom-action { margin-top:auto; }
.brand-btn { width:100%; background:#f06292; border-color:#f06292; }
.brand-btn:hover { filter:brightness(1.05); }
.content { padding: 24px 28px; overflow:auto; min-width: 0; }
</style>