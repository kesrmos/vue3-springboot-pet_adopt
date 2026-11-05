import request from '@/utils/request'

/** 宠物分页列表（免登录）：GET /api/pet/list?page=&size=&type=&gender=&status= */
export function getPetList(params) {
  return request.get('/pet/list', { params })
}

/** 宠物详情（免登录）：GET /api/pet/{id} */
export function getPetDetail(id) {
  return request.get(`/pet/${id}`)
}

/** 管理员：宠物分页列表  GET /api/admin/pet/list?page=&size=&name=&type=&gender=&status= */
export function getAdminPetList(params) {
  return request.get('/admin/pet/list', { params })
}

/** 管理员：新增 / 编辑宠物  POST /api/admin/pet/save（不传 id 为新增） */
export function savePet(data) {
  return request.post('/admin/pet/save', data)
}

/** 管理员：删除宠物  DELETE /api/admin/pet/{id} */
export function deletePet(id) {
  return request.delete(`/admin/pet/${id}`)
}
