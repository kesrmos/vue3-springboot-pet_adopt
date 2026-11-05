<script setup>
import FrontNav from '@/components/FrontNav.vue'

const year = new Date().getFullYear()
</script>

<template>
  <div class="front-layout">
    <FrontNav />

    <main class="front-main">
      <router-view />
    </main>

    <footer class="front-footer">
      <div class="container footer-inner">
        <div class="footer-brand-box">
          <p class="footer-brand">🐾 宠物领养之家</p>
          <p class="footer-tip">用领养代替购买，让每一次相遇都有温度。</p>
        </div>
        <div class="footer-links">
          <router-link to="/petList">宠物列表</router-link>
          <router-link to="/about">领养须知</router-link>
          <router-link to="/myApply">我的申请</router-link>
          <router-link to="/admin/login">管理员入口</router-link>
        </div>
      </div>
      <div class="copyright">© {{ year }} 宠物领养系统 · 学习演示项目</div>
    </footer>
  </div>
</template>

<style scoped>
.front-layout {
  display: flex;
  flex-direction: column;
  min-height: 100%;
}

.front-main {
  flex: 1;
  padding-bottom: 40px;
}

.front-footer {
  margin-top: 20px;
  padding-top: 32px;
  background: #fff;
  border-top: 1px solid var(--border-soft);
}

.footer-inner {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  padding-bottom: 24px;
}

.footer-brand {
  font-size: 17px;
  font-weight: 700;
  color: var(--brand-dark);
}

.footer-tip {
  margin-top: 8px;
  color: var(--text-sub);
  font-size: 13px;
}

.footer-links {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  font-size: 13px;
  color: #6b5f56;
}

.footer-links a:hover {
  color: var(--brand);
}

.copyright {
  padding: 14px 20px;
  border-top: 1px dashed var(--border-soft);
  text-align: center;
  color: #a89c92;
  font-size: 12px;
}

@media (max-width: 768px) {
  .footer-inner {
    flex-direction: column;
  }
}
</style>
