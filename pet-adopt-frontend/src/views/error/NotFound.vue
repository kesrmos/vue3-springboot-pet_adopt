<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<template>
  <div class="not-found">
    <p class="code">404</p>
    <h1 class="title">这个页面好像被领养走了</h1>
    <p class="desc">你访问的地址不存在，或者链接已经失效。</p>
    <div class="actions">
      <el-button type="primary" round @click="router.push('/')">返回首页</el-button>
      <el-button round plain @click="router.push('/petList')">看看待领养的宠物</el-button>
    </div>
  </div>
</template>

<style scoped>
.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 40px 20px;
  text-align: center;
  background: var(--brand-bg);
}

.code {
  font-size: 96px;
  font-weight: 800;
  line-height: 1;
  color: var(--brand-light);
  letter-spacing: 4px;
}

.title {
  margin-top: 8px;
  font-size: 22px;
  font-weight: 700;
  color: var(--text-main);
}

.desc {
  margin-top: 10px;
  font-size: 14px;
  color: var(--text-sub);
}

.actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 12px;
  margin-top: 26px;
}
</style>
