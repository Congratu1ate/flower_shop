import client from './client'

// 列表查询：params = { q?, page=1, size=10 }
export function listUsers(params) {
  return client.get('users', { params })
}

// 获取用户详情
export function getUser(id) {
  return client.get(`users/${encodeURIComponent(String(id))}`)
}

// 创建用户
export function createUser(payload) {
  return client.post('users', payload)
}

// 更新用户
export function updateUser(id, payload) {
  return client.put(`users/${encodeURIComponent(String(id))}`, payload)
}

// 删除用户
export function deleteUser(id) {
  return client.delete(`users/${encodeURIComponent(String(id))}`)
}