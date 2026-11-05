import { createPinia } from 'pinia'

/**
 * Pinia 单例
 * 单独抽出，便于 main.js 安装、路由守卫取 store、以及后续单元测试复用同一实例
 */
const pinia = createPinia()

export default pinia
