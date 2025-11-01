import client from './client'

// 列表查询：params = { q?, status?, startDate?, endDate?, page=1, size=10 }
export function listOrders(params) {
  return client.get('orders', { params })
}

// 获取订单详情
export function getOrder(id) {
  return client.get(`orders/${encodeURIComponent(String(id))}`)
}

// 更新订单状态
export function updateOrderStatus(id, status) {
  return client.patch(`orders/${encodeURIComponent(String(id))}/status`, null, { params: { status } })
}

// 更新订单备注
export function updateOrderRemark(id, remark) {
  return client.patch(`orders/${encodeURIComponent(String(id))}/remark`, { remark })
}