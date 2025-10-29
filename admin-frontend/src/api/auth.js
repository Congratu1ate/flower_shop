import http from './http'

export const register = (data) => {
  return http.post('/auth/register', data)
}

export const login = (data) => {
  return http.post('/auth/login', data)
}

export const me = () => {
  return http.get('/auth/me')
}