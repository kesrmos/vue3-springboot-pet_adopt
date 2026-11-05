import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: 'localhost',
    port: 5173,
    // 端口被占用时直接报错，避免静默切换到 5174 导致浏览器仍指向旧服务
    strictPort: true,
    open: false,
    proxy: {
      // 后端 context-path 就是 /api，所以这里只做转发，不做 rewrite
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  preview: {
    port: 4173,
    strictPort: true
  }
})
