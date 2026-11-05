<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/user'
import { useUserStore } from '@/stores/userStore'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为 3 - 20 位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为 6 - 20 位', trigger: 'blur' }
  ]
}

async function handleLogin() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }

  loading.value = true
  try {
    const data = await login({ username: form.username.trim(), password: form.password })
    userStore.setLogin(data)
    ElMessage.success(`欢迎回来，${userStore.displayName}`)

    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : ''
    if (redirect) {
      router.replace(redirect)
    } else {
      router.replace(userStore.isAdmin ? '/admin/petManage' : '/')
    }
  } catch (e) {
    // 错误提示已由请求层统一处理
  } finally {
    loading.value = false
  }
}

function fillDemo(username) {
  form.username = username
  form.password = '123456'
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <aside class="brand-panel">
        <p class="brand-logo">🐾</p>
        <h1 class="brand-title">宠物领养之家</h1>
        <p class="brand-slogan">用领养代替购买<br />让每个小生命都被温柔以待</p>
        <ul class="brand-points">
          <li>✔ 真实待领养信息</li>
          <li>✔ 在线提交申请</li>
          <li>✔ 审核进度可查</li>
        </ul>
      </aside>

      <section class="form-panel">
        <h2 class="form-title">用户登录</h2>
        <p class="form-sub">登录后即可提交领养申请、查看审核进度</p>

        <el-form ref="formRef" :model="form" :rules="rules" size="large" @submit.prevent="handleLogin">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" clearable />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-button class="submit-btn" type="primary" round :loading="loading" @click="handleLogin">
            登录
          </el-button>
        </el-form>

        <div class="demo-box">
          <p class="demo-title">演示账号（密码均为 123456）</p>
          <div class="demo-tags">
            <el-tag class="demo-tag" effect="plain" round @click="fillDemo('zhangsan')">
              zhangsan（普通用户）
            </el-tag>
            <el-tag class="demo-tag" effect="plain" round @click="fillDemo('lisi')">
              lisi（普通用户）
            </el-tag>
            <el-tag class="demo-tag" type="warning" effect="plain" round @click="fillDemo('admin')">
              admin（管理员）
            </el-tag>
          </div>
        </div>

        <div class="form-links">
          <router-link to="/">← 返回首页</router-link>
          <router-link to="/admin/login">管理员登录 →</router-link>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: grid;
  place-items: center;
  min-height: 100vh;
  padding: 32px 20px;
  background:
    radial-gradient(circle at 15% 20%, #ffe6d3 0, transparent 45%),
    radial-gradient(circle at 85% 80%, #ffeede 0, transparent 45%),
    #fff8f2;
}

.login-card {
  display: grid;
  grid-template-columns: 320px 400px;
  overflow: hidden;
  background: #fff;
  border-radius: 22px;
  box-shadow: 0 20px 50px rgba(240, 138, 75, 0.16);
}

.brand-panel {
  padding: 44px 34px;
  color: #fff;
  background: linear-gradient(155deg, #f7a86e 0%, #ef7f3c 60%, #e56f2f 100%);
}

.brand-logo {
  font-size: 38px;
}

.brand-title {
  margin-top: 14px;
  font-size: 24px;
  font-weight: 800;
  letter-spacing: 1px;
}

.brand-slogan {
  margin-top: 14px;
  font-size: 14px;
  line-height: 1.9;
  opacity: 0.94;
}

.brand-points {
  margin: 34px 0 0;
  padding: 0;
  list-style: none;
  font-size: 13px;
  line-height: 2.2;
  opacity: 0.92;
}

.form-panel {
  padding: 44px 40px 32px;
}

.form-title {
  font-size: 22px;
  font-weight: 800;
  color: #41372f;
}

.form-sub {
  margin: 8px 0 24px;
  color: var(--text-sub);
  font-size: 13px;
}

.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  margin-top: 4px;
}

.demo-box {
  margin-top: 22px;
  padding: 14px;
  background: #fffaf5;
  border: 1px dashed #ffd9c0;
  border-radius: 12px;
}

.demo-title {
  margin-bottom: 10px;
  color: #a1745a;
  font-size: 12px;
}

.demo-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.demo-tag {
  cursor: pointer;
}

.form-links {
  display: flex;
  justify-content: space-between;
  margin-top: 22px;
  font-size: 13px;
  color: #a89c92;
}

.form-links a:hover {
  color: var(--brand);
}

@media (max-width: 760px) {
  .login-card {
    grid-template-columns: 1fr;
    width: 100%;
  }

  .brand-panel {
    padding: 28px 24px;
  }

  .form-panel {
    padding: 28px 22px 24px;
  }
}
</style>

