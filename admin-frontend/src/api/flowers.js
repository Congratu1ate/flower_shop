import client from './client'

export function listFlowers(params) {
  // params: { q?, categoryId?, status?, page=1, size=10 }
  return client.get('/flowers', { params })
}

export function getFlower(id) {
  return client.get(`/flowers/${encodeURIComponent(String(id))}`)
}

export function createFlower(payload) {
  // payload: { name, categoryId, price, image?, description?, status?, inventory? }
  return client.post('/flowers', payload)
}

export function updateFlower(id, payload) {
  return client.put(`/flowers/${encodeURIComponent(String(id))}`, payload)
}

export function updateFlowerStatus(id, status) {
  return client.patch(`/flowers/${encodeURIComponent(String(id))}/status`, null, { params: { status } })
}

export function deleteFlower(id) {
  return client.delete(`/flowers/${encodeURIComponent(String(id))}`)
}