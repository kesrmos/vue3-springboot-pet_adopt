<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/userStore'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const menus = computed(() => {
  const list = [
    { path: '/', label: '首页' },
    { path: '/petList', label: '宠物列表' },
    { path: '/about', label: '领养须知' }
  ]
  if (userStore.isLogin) {
    list.push({ path: '/myApply', label: '我的申请' })
  }
  return list
})

function isActive(path) {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      type: 'warning',
      confirmButtonText: '退出',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}
</script>

<template>
  <header class="front-nav">
    <div class="nav-inner">
      <router-link to="/" class="brand">
        <span class="brand-icon">🐾</span>
        <span class="brand-text">宠物领养之家</span>
      </router-link>

      <nav class="nav-links">
        <router-link
          v-for="item in menus"
          :key="item.path"
          :to="item.path"
          class="nav-link"
          :class="{ active: isActive(item.path) }"
        >
          {{ item.label }}
        </router-link>
      </nav>

      <div class="nav-right">
        <el-dropdown v-if="userStore.isLogin" trigger="click">
          <span class="user-entry">
            <span class="avatar">{{ userStore.displayName.slice(0, 1) }}</span>
            <span class="user-name">{{ userStore.displayName }}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/myApply')">我的申请</el-dropdown-item>
              <el-dropdown-item v-if="userStore.isAdmin" @click="router.push('/admin/petManage')">
                进入后台管理
              </el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <el-button v-else type="primary" round @click="router.push('/login')">登录</el-button>
      </div>
    </div>
  </header>
</template>

<style scoped>
.front-nav {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--border-soft);
}

.nav-inner {
  display: flex;
  align-items: center;
  gap: 28px;
  width: 100%;
  max-width: 1200px;
  height: 68px;
  margin: 0 auto;
  padding: 0 20px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 19px;
  font-weight: 700;
  color: var(--brand-dark);
  white-space: nowrap;
}

.brand-icon {
  font-size: 22px;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
}

.nav-link {
  position: relative;
  padding: 8px 14px;
  border-radius: 999px;
  color: #6b5f56;
  font-size: 15px;
  transition: all 0.2s;
}

.nav-link:hover {
  color: var(--brand-dark);
  background: #fff3e9;
}

.nav-link.active {
  color: #fff;
  background: linear-gradient(135deg, #f7a86e, #ef7f3c);
  box-shadow: 0 4px 12px rgba(240, 138, 75, 0.32);
}

.nav-right {
  display: flex;
  align-items: center;
}

.user-entry {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 5px 12px 5px 5px;
  border-radius: 999px;
  cursor: pointer;
  background: #fff6ee;
  border: 1px solid var(--border-soft);
  outline: none;
}

.avatar {
  display: grid;
  place-items: center;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  color: #fff;
  font-size: 14px;
  background: linear-gradient(135deg, #f7a86e, #ef7f3c);
}

.user-name {
  font-size: 14px;
  color: #5b5047;
  max-width: 110px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

@media (max-width: 768px) {
  .nav-inner {
    gap: 12px;
    height: auto;
    padding: 10px 14px;
    flex-wrap: wrap;
  }

  .nav-links {
    order: 3;
    width: 100%;
    overflow-x: auto;
  }

  .brand-text {
    font-size: 16px;
  }
}
</style>
