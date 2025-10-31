import http from './http';

// 登录：返回 { user }
export function login(payload) {
  return http.post('/auth/login', payload);
}

// 注册：返回 { user }
export function register(payload) {
  return http.post('/auth/register', payload);
}

// 获取当前登录用户信息
export function me() {
  return http.get('/auth/me');
}

// 退出登录：清除后端 Cookie
export function logout() {
  return http.post('/auth/logout');
}