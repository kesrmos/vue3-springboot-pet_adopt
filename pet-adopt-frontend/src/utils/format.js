/**
 * 状态 / 类型 等常量与格式化方法（与后端字段严格对应）
 */

export const PET_TYPE_OPTIONS = ['猫', '狗', '兔', '鸟']

export const GENDER_OPTIONS = ['公', '母']

/** 宠物状态：0 待领养，1 已领养 */
export const PET_STATUS_MAP = {
  0: { label: '待领养', type: 'success' },
  1: { label: '已领养', type: 'info' }
}

/** 审核状态：0 待审核，1 通过，2 驳回 */
export const AUDIT_STATUS_MAP = {
  0: { label: '待审核', type: 'warning' },
  1: { label: '已通过', type: 'success' },
  2: { label: '已驳回', type: 'danger' }
}

/** 角色：0 普通用户，1 管理员 */
export const ROLE_MAP = {
  0: { label: '普通用户', type: 'info' },
  1: { label: '管理员', type: 'warning' }
}

export function petStatusLabel(status) {
  return (PET_STATUS_MAP[status] || { label: '未知' }).label
}

export function petStatusType(status) {
  return (PET_STATUS_MAP[status] || { type: 'info' }).type
}

export function auditStatusLabel(status) {
  return (AUDIT_STATUS_MAP[status] || { label: '未知' }).label
}

export function auditStatusType(status) {
  return (AUDIT_STATUS_MAP[status] || { type: 'info' }).type
}

export function roleLabel(role) {
  return (ROLE_MAP[role] || { label: '未知' }).label
}

export function roleType(role) {
  return (ROLE_MAP[role] || { type: 'info' }).type
}

/** 年龄：后端存的是「月」 */
export function formatAge(age) {
  if (age === null || age === undefined || age === '') return '年龄未知'
  return `${age} 个月`
}

/** 时间：后端已格式化为 yyyy-MM-dd HH:mm:ss */
export function formatTime(value) {
  return value || '-'
}

/** 卡片副标题，如「狗 · 公 · 8 个月」 */
export function petSubtitle(pet) {
  if (!pet) return ''
  return [pet.type, pet.gender, formatAge(pet.age)].filter(Boolean).join(' · ')
}
