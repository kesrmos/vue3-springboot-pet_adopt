import request from '@/utils/request'

/** 普通用户登录：POST /api/user/login  { username, password } */
export function login(data) {
  return request.post('/user/login', data)
}

/** 管理员登录：POST /api/admin/login  { username, password } */
export function adminLogin(data) {
  return request.post('/admin/login', data)
}

/** 管理员：用户分页  GET /api/admin/user/list?page=&size=&keyword= */
export function getAdminUserList(params) {
  return request.get('/admin/user/list', { params })
}
