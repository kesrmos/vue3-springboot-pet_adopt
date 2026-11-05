<script setup>
/**
 * 通用分页组件
 * 用法：<Pagination :total="total" :page="page" :size="size" @change="handleChange" />
 */
const props = defineProps({
  total: { type: Number, default: 0 },
  page: { type: Number, default: 1 },
  size: { type: Number, default: 10 },
  pageSizes: { type: Array, default: () => [10, 20, 50] },
  layout: { type: String, default: 'total, sizes, prev, pager, next, jumper' },
  background: { type: Boolean, default: true }
})

const emit = defineEmits(['update:page', 'update:size', 'change'])

function handleCurrentChange(currentPage) {
  emit('update:page', currentPage)
  emit('change', { page: currentPage, size: props.size })
}

function handleSizeChange(pageSize) {
  emit('update:size', pageSize)
  emit('update:page', 1)
  emit('change', { page: 1, size: pageSize })
}
</script>

<template>
  <div v-if="total > 0" class="pagination-wrap">
    <el-pagination
      :background="background"
      :layout="layout"
      :total="total"
      :current-page="page"
      :page-size="size"
      :page-sizes="pageSizes"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
    />
  </div>
</template>

<style scoped>
.pagination-wrap {
  display: flex;
  justify-content: center;
  padding: 20px 0 4px;
}
</style>
