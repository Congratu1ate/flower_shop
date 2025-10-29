import axios from 'axios'

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  withCredentials: true,
  timeout: 10000
})

client.interceptors.response.use(
  (res) => res,
  (err) => {
    const resp = err.response
    if (resp && resp.data && resp.data.message) {
      // 统一错误信息可在页面进行提示
      console.error('API Error:', resp.status, resp.data.message)
    }
    return Promise.reject(err)
  }
)

export default client