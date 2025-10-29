import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  withCredentials: true,
  timeout: 10000,
})

http.interceptors.response.use(
  (res) => res,
  (err) => {
    // 统一错误提示
    const msg = err?.response?.data?.message || err.message || '请求失败'
    console.error('HTTP Error:', msg)
    return Promise.reject(err)
  }
)

export default http