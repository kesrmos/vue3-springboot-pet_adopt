<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => route.path)

const menus = [
  { path: '/admin/petManage', label: '宠物管理', icon: '🐾' },
  { path: '/admin/applyManage', label: '领养审核', icon: '📋' },
  { path: '/admin/userManage', label: '用户管理', icon: '👥' }
]
</script>

<template>
  <el-menu class="admin-menu" :default-active="activeMenu" router>
    <el-menu-item v-for="item in menus" :key="item.path" :index="item.path">
      <span class="menu-icon">{{ item.icon }}</span>
      <span>{{ item.label }}</span>
    </el-menu-item>
  </el-menu>
</template>

<style scoped>
.admin-menu {
  border-right: none;
  background: transparent;
  padding: 8px 12px;
}

.admin-menu :deep(.el-menu-item) {
  height: 46px;
  margin-bottom: 6px;
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.82);
  font-size: 14px;
}

.admin-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.14);
  color: #fff;
}

.admin-menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.95);
  color: #d9733a;
  font-weight: 600;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.menu-icon {
  margin-right: 10px;
  font-size: 16px;
}
</style>
