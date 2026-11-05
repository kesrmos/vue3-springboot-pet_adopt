<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { adminLogin } from '@/api/user'
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
  username: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
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
    const data = await adminLogin({ username: form.username.trim(), password: form.password })
    if (Number(data?.role) !== 1) {
      ElMessage.error('该账号不是管理员，无法登录后台')
      return
    }
    userStore.setLogin(data)
    ElMessage.success(`欢迎回来，${userStore.displayName}`)
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : ''
    router.replace(redirect || '/admin/petManage')
  } catch (e) {
    // 403 / 账号密码错误由请求层提示
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="admin-login-page">
    <div class="login-box">
      <div class="head">
        <p class="logo">🐾</p>
        <h1 class="title">宠物领养后台</h1>
        <p class="sub">仅管理员可登录，普通用户请使用前台登录</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" size="large" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="管理员账号" clearable />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-button class="submit-btn" type="primary" round :loading="loading" @click="handleLogin">
          登录后台
        </el-button>
      </el-form>

      <p class="hint">默认管理员账号：admin / 123456</p>
      <div class="links">
        <router-link to="/">← 返回前台</router-link>
        <router-link to="/login">用户登录 →</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-login-page {
  display: grid;
  place-items: center;
  min-height: 100vh;
  padding: 32px 20px;
  background:
    radial-gradient(circle at 20% 18%, rgba(255, 255, 255, 0.28) 0, transparent 42%),
    linear-gradient(140deg, #f6a469 0%, #e97c3a 55%, #d96a2a 100%);
}

.login-box {
  width: 400px;
  max-width: 100%;
  padding: 38px 34px 26px;
  background: #fff;
  border-radius: 22px;
  box-shadow: 0 24px 60px rgba(122, 60, 20, 0.28);
}

.head {
  margin-bottom: 24px;
  text-align: center;
}

.logo {
  font-size: 34px;
}

.title {
  margin-top: 8px;
  font-size: 21px;
  font-weight: 800;
  color: #41372f;
}

.sub {
  margin-top: 8px;
  color: var(--text-sub);
  font-size: 12px;
  line-height: 1.7;
}

.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  margin-top: 4px;
}

.hint {
  margin-top: 18px;
  padding: 10px 12px;
  border-radius: 10px;
  background: #fffaf5;
  border: 1px dashed #ffd9c0;
  color: #a1745a;
  font-size: 12px;
  text-align: center;
}

.links {
  display: flex;
  justify-content: space-between;
  margin-top: 18px;
  font-size: 13px;
  color: #a89c92;
}

.links a:hover {
  color: var(--brand);
}
</style>
