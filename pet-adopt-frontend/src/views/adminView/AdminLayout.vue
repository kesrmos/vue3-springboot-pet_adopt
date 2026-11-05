<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import AdminSidebar from '@/components/AdminSidebar.vue'
import { useUserStore } from '@/stores/userStore'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const pageTitle = computed(() => route.meta?.title || '后台管理')

async function handleCommand(command) {
  if (command === 'front') {
    router.push('/')
    return
  }
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出后台登录吗？', '提示', {
        type: 'warning',
        confirmButtonText: '退出',
        cancelButtonText: '取消'
      })
    } catch (e) {
      return
    }
    userStore.logout()
    ElMessage.success('已退出登录')
    router.replace('/admin/login')
  }
}
</script>

<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="admin-aside">
      <div class="admin-brand">
        <span class="brand-icon">🐾</span>
        <span>宠物领养后台</span>
      </div>
      <AdminSidebar />
      <div class="aside-tip">学习演示系统<br />数据仅供测试使用</div>
    </el-aside>

    <el-container class="admin-body">
      <el-header class="admin-header">
        <div class="header-left">
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <el-tag type="warning" effect="plain" round size="small">管理员</el-tag>
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="admin-user">
              <span class="avatar">{{ userStore.displayName.slice(0, 1) }}</span>
              <span>{{ userStore.displayName }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="front">返回前台</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background: #f7f4f1;
}

.admin-aside {
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #f39b5d 0%, #e97c3a 100%);
  box-shadow: 4px 0 18px rgba(233, 124, 58, 0.18);
}

.admin-brand {
  display: flex;
  align-items: center;
  gap: 8px;
  height: 62px;
  padding: 0 20px;
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 0.5px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.22);
}

.brand-icon {
  font-size: 19px;
}

.aside-tip {
  margin-top: auto;
  padding: 18px 20px;
  color: rgba(255, 255, 255, 0.72);
  font-size: 12px;
  line-height: 1.8;
}

.admin-body {
  min-width: 0;
}

.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 62px;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid var(--border-soft);
}

.page-title {
  font-size: 16px;
  font-weight: 700;
  color: #41372f;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 14px;
}

.admin-user {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 10px 4px 4px;
  border-radius: 999px;
  background: #fff8f2;
  cursor: pointer;
  outline: none;
  font-size: 13px;
  color: #5b5047;
}

.avatar {
  display: grid;
  place-items: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  color: #fff;
  background: linear-gradient(135deg, #f7a86e, #ef7f3c);
}

.admin-main {
  padding: 20px 24px 28px;
  background: #f7f4f1;
}

@media (max-width: 900px) {
  .admin-aside {
    width: 76px !important;
  }

  .admin-brand span:last-child,
  .aside-tip {
    display: none;
  }
}
</style>
