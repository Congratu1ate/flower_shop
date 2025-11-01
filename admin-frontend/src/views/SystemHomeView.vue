<script setup>
import { ref, onMounted } from 'vue'

const stats = ref({ users: 0, orders: 0, flowers: 0, revenue: 0 })

onMounted(() => {
  // 这里可接入后端仪表盘统计接口；当前使用占位数据
  stats.value = { users: 128, orders: 256, flowers: 42, revenue: 123456 }
})
</script>

<template>
  <div class="dashboard">
    <div class="title">系统主页</div>
    <div class="cards">
      <div class="card">
        <div class="label">用户数</div>
        <div class="value">{{ stats.users }}</div>
      </div>
      <div class="card">
        <div class="label">订单数</div>
        <div class="value">{{ stats.orders }}</div>
      </div>
      <div class="card">
        <div class="label">商品数</div>
        <div class="value">{{ stats.flowers }}</div>
      </div>
      <div class="card">
        <div class="label">收入(¥)</div>
        <div class="value">{{ stats.revenue.toLocaleString() }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard { background:#fff; border-radius:16px; padding:16px; max-width: 1200px; margin: 0 auto; }
.title { font-size:20px; font-weight:700; margin-bottom:12px; }
.cards { display:grid; grid-template-columns: repeat(4, 1fr); gap:12px; }
.card { background:#f8f4f6; border-radius:12px; padding:16px; }
.label { font-size:12px; color:#888; }
.value { font-size:22px; font-weight:700; margin-top:6px; }
</style>