import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

import App from './App.vue'
import router from './router'
import pinia from './stores'

import 'element-plus/dist/index.css'
import './assets/styles/global.css'

const app = createApp(App)

// 运行时错误统一输出到控制台，便于快速定位白屏原因（不阻断页面交互）
app.config.errorHandler = (err, instance, info) => {
  console.error('[pet-adopt] 组件运行出错：', info, err)
}

window.addEventListener('unhandledrejection', (event) => {
  console.error('[pet-adopt] 未处理的 Promise 异常：', event.reason)
})

// 先装 pinia，路由守卫里才能拿到 store
app.use(pinia)
app.use(router)
app.use(ElementPlus, { locale: zhCn })

app.mount('#app')

