<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listOrders, updateOrderStatus, getOrder, updateOrderRemark } from '../api/orders'

const loading = ref(false)
const rows = ref([])
const pager = reactive({ page: 1, size: 10, total: 0 })

const filters = reactive({
  q: '',
  status: null, // 1待付款 2已付款 3配送中 4已完成 5取消 6退款
  dateRange: [], // [start, end]
})

const statusDefs = [
  { value: null, label: '全部' },
  { value: 1, label: '待付款', type: 'pending' },
  { value: 2, label: '已付款', type: 'paid' },
  { value: 3, label: '配送中', type: 'shipping' },
  { value: 4, label: '已完成', type: 'done' },
  { value: 5, label: '已取消', type: 'cancel' },
  { value: 6, label: '退款中', type: 'refund' },
]

function statusLabel(s) {
  const found = statusDefs.find(x => x.value === s)
  return found?.label || '—'
}

function statusClass(s) {
  const found = statusDefs.find(x => x.value === s)
  switch (found?.type) {
    case 'pending': return 'tag tag-pending'
    case 'paid': return 'tag tag-paid'
    case 'shipping': return 'tag tag-shipping'
    case 'done': return 'tag tag-done'
    case 'cancel': return 'tag tag-cancel'
    case 'refund': return 'tag tag-refund'
    default: return 'tag'
  }
}

async function fetchList() {
  loading.value = true
  try {
    const params = {
      q: filters.q || undefined,
      status: filters.status ?? undefined,
      startDate: filters.dateRange?.[0] ? formatDate(filters.dateRange[0]) : undefined,
      endDate: filters.dateRange?.[1] ? formatDate(filters.dateRange[1]) : undefined,
      page: pager.page,
      size: pager.size,
    }
    const { data } = await listOrders(params)
    rows.value = data?.records || []
    pager.total = Number(data?.total || 0)
    pager.size = Number(data?.size || pager.size)
    pager.page = Number(data?.current || pager.page)
  } catch (err) {
    ElMessage.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
}

function formatDate(d) {
  // 将日期对象或字符串转换为后端期望的 yyyy-MM-dd
  const dt = typeof d === 'string' ? new Date(d) : d
  const y = dt.getFullYear()
  const m = String(dt.getMonth() + 1).padStart(2, '0')
  const day = String(dt.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

function onSearch() {
  pager.page = 1
  fetchList()
}

function onReset() {
  filters.q = ''
  filters.status = null
  filters.dateRange = []
  pager.page = 1
  fetchList()
}

function onPageChange(p) {
  pager.page = p
  fetchList()
}

function onPageSizeChange(s) {
  pager.size = s
  pager.page = 1
  fetchList()
}

function maskPhone(p) {
  if (!p) return '—'
  const s = String(p)
  return `${s.slice(0,3)}****${s.slice(-4)}`
}

async function changeStatus(row, nextStatus, confirmText) {
  try {
    await ElMessageBox.confirm(confirmText || '确定要更新该订单状态吗？', '提示', { type: 'warning' })
  } catch (_) { return }
  try {
    await updateOrderStatus(row.id, nextStatus)
    ElMessage.success('状态已更新')
    fetchList()
  } catch (err) {
    ElMessage.error('状态更新失败：不合法的状态流转或接口异常')
  }
}

// 详情抽屉
const detailVisible = ref(false)
const detail = ref({ order: null, items: [] })

async function openDetail(row) {
  try {
    const { data } = await getOrder(row.id)
    detail.value = data || { order: null, items: [] }
    detailVisible.value = true
  } catch (err) {
    ElMessage.error('获取订单详情失败')
  }
}

async function saveRemark() {
  const id = detail.value?.order?.id
  const remark = detail.value?.order?.remark || ''
  if (!id) return
  try {
    await updateOrderRemark(id, remark)
    ElMessage.success('备注已保存')
    detailVisible.value = false
    fetchList()
  } catch (err) {
    ElMessage.error('保存备注失败')
  }
}

onMounted(fetchList)
</script>

<template>
  <div class="orders-page">
    <div class="page-title">订单管理</div>

    <div class="filters">
      <div class="status-group">
        <el-button
          v-for="s in statusDefs"
          :key="String(s.value)"
          size="small"
          :type="filters.status === s.value ? 'primary' : 'default'"
          round
          @click="filters.status = s.value; onSearch()"
        >{{ s.label }}</el-button>
      </div>

      <div class="search-group">
        <el-input v-model="filters.q" placeholder="订单号/客户姓名/手机号" clearable style="width: 240px" />
        <el-date-picker
          v-model="filters.dateRange"
          type="daterange"
          unlink-panels
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="margin-left: 8px;"
        />
        <el-button type="primary" style="margin-left: 8px" @click="onSearch">搜索</el-button>
        <el-button style="margin-left: 4px" @click="onReset">重置</el-button>
      </div>
    </div>

    <el-table :data="rows" v-loading="loading" class="orders-table" stripe>
      <el-table-column label="订单信息" min-width="260">
        <template #default="{ row }">
          <div class="order-info">
            <div class="number">#{{ row.number }}</div>
            <div class="time">{{ new Date(row.orderTime).toLocaleString() }}</div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="客户" min-width="180">
        <template #default="{ row }">
          <div class="customer">
            <div class="name">{{ row.customerName || '—' }}</div>
            <div class="phone">{{ maskPhone(row.customerPhone) }}</div>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="金额" width="120" align="right">
        <template #default="{ row }">
          <div class="amount">¥ {{ Number(row.amount || 0).toFixed(2) }}</div>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="120" align="center">
        <template #default="{ row }">
          <span :class="statusClass(row.status)">{{ statusLabel(row.status) }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" width="320" align="center">
        <template #default="{ row }">
          <el-button size="small" type="primary" plain @click="openDetail(row)">查看详情</el-button>
          <el-button v-if="row.status === 1" size="small" type="danger" plain @click="changeStatus(row, 5, '确定取消该订单？')">取消订单</el-button>
          <el-button v-if="row.status === 2" size="small" type="primary" plain @click="changeStatus(row, 3, '确定开始配送？')">开始配送</el-button>
          <el-button v-if="row.status === 3" size="small" type="success" plain @click="changeStatus(row, 4, '确认已送达并完成订单？')">确认送达</el-button>
          <el-button v-if="row.status === 2" size="small" type="warning" plain @click="changeStatus(row, 6, '确定申请退款？')">退款</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="pager.total"
        :page-size="pager.size"
        :current-page="pager.page"
        :page-sizes="[10, 20, 50]"
        @size-change="onPageSizeChange"
        @current-change="onPageChange"
      />
    </div>

    <el-drawer v-model="detailVisible" title="订单详情" size="40%">
      <template v-if="detail.order">
        <div class="detail-block">
          <div class="line"><b>订单号：</b><span>#{{ detail.order.number }}</span></div>
          <div class="line"><b>下单时间：</b><span>{{ new Date(detail.order.orderTime).toLocaleString() }}</span></div>
          <div class="line"><b>客户：</b><span>{{ detail.order.userName || '—' }}</span></div>
          <div class="line"><b>联系电话：</b><span>{{ maskPhone(detail.order.userPhone || '') }}</span></div>
          <div class="line"><b>金额：</b><span>¥ {{ Number(detail.order.amount || 0).toFixed(2) }}</span></div>
          <div class="line"><b>状态：</b><span :class="statusClass(detail.order.status)">{{ statusLabel(detail.order.status) }}</span></div>
          <div class="line remark">
            <b>备注：</b>
            <el-input v-model="detail.order.remark" type="textarea" :rows="3" placeholder="输入订单备注..." />
          </div>
        </div>

        <div class="detail-items">
          <div class="items-title">商品明细</div>
          <el-table :data="detail.items" size="small">
            <el-table-column label="商品" prop="name" min-width="180" />
            <el-table-column label="数量" prop="number" width="100" align="center" />
            <el-table-column label="单价" width="120" align="right">
              <template #default="{ row }">¥ {{ Number(row.price || 0).toFixed(2) }}</template>
            </el-table-column>
          </el-table>
        </div>

        <div style="text-align:right;margin-top:12px;">
          <el-button @click="detailVisible = false">关闭</el-button>
          <el-button type="primary" @click="saveRemark">保存备注</el-button>
        </div>
      </template>
      <template v-else>
        <div>未找到订单信息</div>
      </template>
    </el-drawer>
  </div>
  
</template>

<style scoped>
.orders-page { background:#fff; border-radius:16px; padding:16px; max-width: 1200px; margin: 0 auto; }
.page-title { font-size:20px; font-weight:700; margin-bottom:12px; }
.filters { display:flex; align-items:center; justify-content:flex-start; flex-wrap:wrap; gap:8px 12px; padding:10px; background:#f8f4f6; border-radius:12px; }
.status-group { display:flex; gap:6px; flex-wrap:wrap; }
.search-group { display:flex; align-items:center; flex-wrap:wrap; }
.orders-table { margin-top:12px; }
.order-info { display:flex; align-items:center; gap:10px; }
.order-info .number { font-weight:700; }
.order-info .time { font-size:12px; color:#888; }
.customer .name { font-weight:600; }
.customer .phone { font-size:12px; color:#888; }
.amount { font-weight:700; color:#333; }
.tag { display:inline-block; padding:4px 10px; border-radius:999px; font-size:12px; }
.tag-pending { background:#eee; color:#666; }
.tag-paid { background:#e9f0ff; color:#2463eb; }
.tag-shipping { background:#fff7e6; color:#d97b00; }
.tag-done { background:#e8f7ee; color:#1a7f37; }
.tag-cancel { background:#fde8e8; color:#b91c1c; }
.tag-refund { background:#f3e8ff; color:#6b21a8; }
.pagination { display:flex; justify-content:flex-end; margin-top:12px; }
.detail-block { background:#f8f4f6; padding:12px; border-radius:12px; }
.detail-block .line { margin-bottom:8px; }
.detail-block .remark { margin-top:10px; }
.items-title { font-weight:700; margin:12px 0 8px; }
</style>