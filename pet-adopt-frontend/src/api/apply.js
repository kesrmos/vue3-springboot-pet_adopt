import request from '@/utils/request'

/** 提交领养申请（需登录）：POST /api/apply/submit  { petId, liveEnv, exp, remark } */
export function submitApply(data) {
  return request.post('/apply/submit', data)
}

/** 我的领养申请（需登录，返回数组）：GET /api/apply/my */
export function getMyApplies() {
  return request.get('/apply/my')
}

/** 管理员：全部申请分页  GET /api/admin/apply/list?page=&size=&auditStatus= */
export function getAdminApplyList(params) {
  return request.get('/admin/apply/list', { params })
}

/** 管理员：审核申请  PUT /api/admin/apply/audit  { applyId, auditStatus, auditRemark } */
export function auditApply(data) {
  return request.put('/admin/apply/audit', data)
}
