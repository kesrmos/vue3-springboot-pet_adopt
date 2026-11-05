<script setup>
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { usePetStore } from '@/stores/petStore'
import PetCard from '@/components/PetCard.vue'
import Pagination from '@/components/Pagination.vue'
import { PET_TYPE_OPTIONS, GENDER_OPTIONS } from '@/utils/format'

const petStore = usePetStore()
const { list, total, page, size, filters, loading } = storeToRefs(petStore)

onMounted(() => {
  petStore.fetchPets()
})

function handleSearch() {
  petStore.applyFilters()
}

function handleReset() {
  petStore.resetFilters()
}

function handlePageChange(payload) {
  petStore.changePage(payload.page, payload.size)
}
</script>

<template>
  <div class="container pet-list-page">
    <div class="page-head">
      <div>
        <h1 class="section-title">宠物列表</h1>
        <p class="section-desc">共 {{ total }} 个小伙伴在等待一个家</p>
      </div>
    </div>

    <!-- 筛选 -->
    <div class="filter-card">
      <el-form :inline="true" class="filter-form" @submit.prevent>
        <el-form-item label="类型">
          <el-select v-model="filters.type" placeholder="全部类型" clearable style="width: 140px">
            <el-option v-for="type in PET_TYPE_OPTIONS" :key="type" :label="type" :value="type" />
          </el-select>
        </el-form-item>

        <el-form-item label="性别">
          <el-select v-model="filters.gender" placeholder="全部性别" clearable style="width: 120px">
            <el-option v-for="gender in GENDER_OPTIONS" :key="gender" :label="gender" :value="gender" />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部状态" clearable style="width: 140px">
            <el-option label="待领养" :value="0" />
            <el-option label="已领养" :value="1" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 列表 -->
    <div v-loading="loading" class="list-wrap">
      <div v-if="list.length" class="pet-grid">
        <PetCard v-for="pet in list" :key="pet.id" :pet="pet" />
      </div>
      <el-empty v-else-if="!loading" description="没有找到符合条件的宠物，换个筛选条件试试" />
    </div>

    <Pagination
      :total="total"
      :page="page"
      :size="size"
      :page-sizes="[8, 12, 24]"
      @change="handlePageChange"
    />
  </div>
</template>

<style scoped>
.pet-list-page {
  padding-top: 32px;
}

.page-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 20px;
}

.filter-card {
  padding: 18px 20px 4px;
  background: #fff;
  border: 1px solid var(--border-soft);
  border-radius: 16px;
  box-shadow: var(--shadow-soft);
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.list-wrap {
  min-height: 240px;
  margin-top: 24px;
}

.pet-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 22px;
}
</style>
