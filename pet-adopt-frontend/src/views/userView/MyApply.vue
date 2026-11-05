<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getMyApplies } from '@/api/apply'
import placeholder from '@/assets/images/placeholder.svg'
import { auditStatusLabel, auditStatusType, formatTime, petSubtitle } from '@/utils/format'

const router = useRouter()

const loading = ref(true)
const list = ref([])

const summary = computed(() => {
  const result = { pending: 0, passed: 0, rejected: 0 }
  list.value.forEach((item) => {
    if (Number(item.auditStatus) === 0) result.pending += 1
    else if (Number(item.auditStatus) === 1) result.passed += 1
    else if (Number(item.auditStatus) === 2) result.rejected += 1
  })
  return result
})

async function fetchList() {
  loading.value = true
  try {
    const data = await getMyApplies()
    list.value = Array.isArray(data) ? data : []
  } catch (e) {
    list.value = []
  } finally {
    loading.value = false
  }
}

onMounted(fetchList)
</script>

<template>
  <div class="container my-apply-page">
    <div class="page-head">
      <div>
        <h1 class="section-title">我的申请</h1>
        <p class="section-desc">共 {{ list.length }} 条申请记录</p>
      </div>
      <el-button :loading="loading" @click="fetchList">刷新</el-button>
    </div>

    <div class="summary">
      <div class="summary-item">
        <span class="dot pending"></span>
        待审核 <strong>{{ summary.pending }}</strong>
      </div>
      <div class="summary-item">
        <span class="dot passed"></span>
        已通过 <strong>{{ summary.passed }}</strong>
      </div>
      <div class="summary-item">
        <span class="dot rejected"></span>
        已驳回 <strong>{{ summary.rejected }}</strong>
      </div>
    </div>

    <div v-loading="loading" class="list-wrap">
      <div v-if="list.length" class="apply-list">
        <el-card v-for="item in list" :key="item.id" shadow="never" class="apply-item">
          <div class="item-main">
            <el-image class="cover" :src="item.petPhoto" fit="cover">
              <template #error>
                <img class="cover" :src="placeholder" alt="暂无图片" />
              </template>
            </el-image>

            <div class="info">
              <div class="info-head">
                <h3 class="pet-name">{{ item.petName || '宠物已删除' }}</h3>
                <el-tag :type="auditStatusType(item.auditStatus)" effect="light" round>
                  {{ auditStatusLabel(item.auditStatus) }}
                </el-tag>
              </div>

              <p class="pet-meta">
                {{ petSubtitle({ type: item.petType, gender: item.petGender, age: item.petAge }) }}
              </p>

              <div class="detail-grid">
                <p><span>申请编号</span>#{{ item.id }}</p>
                <p><span>申请时间</span>{{ formatTime(item.createTime) }}</p>
                <p class="full"><span>居住环境</span>{{ item.liveEnv || '-' }}</p>
                <p class="full"><span>养宠经验</span>{{ item.exp || '未填写' }}</p>
                <p class="full"><span>备注说明</span>{{ item.remark || '未填写' }}</p>
              </div>

              <div v-if="item.auditRemark" class="audit-remark">
                <span>审核意见</span>{{ item.auditRemark }}
              </div>
            </div>
          </div>

          <div class="item-footer">
            <el-button text type="primary" @click="router.push(`/pet/${item.petId}`)">查看宠物</el-button>
            <el-button
              v-if="Number(item.auditStatus) === 2"
              text
              type="warning"
              @click="router.push(`/apply?petId=${item.petId}`)"
            >
              重新申请
            </el-button>
          </div>
        </el-card>
      </div>

      <el-empty v-else-if="!loading" description="还没有提交过领养申请">
        <el-button type="primary" @click="router.push('/petList')">去看看小可爱们</el-button>
      </el-empty>
    </div>
  </div>
</template>

<style scoped>
.my-apply-page {
  padding-top: 32px;
}

.page-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 18px;
}

.summary {
  display: flex;
  gap: 26px;
  padding: 14px 22px;
  margin-bottom: 20px;
  background: #fff;
  border: 1px solid var(--border-soft);
  border-radius: 14px;
  font-size: 14px;
  color: #6b5f56;
}

.summary-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.summary-item strong {
  color: var(--brand-dark);
  font-size: 16px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.dot.pending {
  background: #e6a23c;
}

.dot.passed {
  background: #67c23a;
}

.dot.rejected {
  background: #f56c6c;
}

.list-wrap {
  min-height: 200px;
}

.apply-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.apply-item :deep(.el-card__body) {
  padding: 18px 20px 10px;
}

.item-main {
  display: flex;
  gap: 18px;
}

.cover {
  width: 150px;
  height: 112px;
  border-radius: 12px;
  object-fit: cover;
  flex-shrink: 0;
  background: #fff3e9;
}

.info {
  flex: 1;
  min-width: 0;
}

.info-head {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pet-name {
  font-size: 17px;
  font-weight: 700;
  color: #41372f;
}

.pet-meta {
  margin-top: 4px;
  color: var(--brand-dark);
  font-size: 13px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 6px 24px;
  margin-top: 10px;
  font-size: 13px;
  color: #5b5047;
}

.detail-grid .full {
  grid-column: 1 / -1;
}

.detail-grid span {
  display: inline-block;
  width: 72px;
  color: var(--text-sub);
}

.audit-remark {
  margin-top: 12px;
  padding: 10px 14px;
  background: #fff6ee;
  border-left: 3px solid var(--brand);
  border-radius: 8px;
  color: #7a5a43;
  font-size: 13px;
}

.audit-remark span {
  margin-right: 10px;
  color: var(--brand-dark);
  font-weight: 600;
}

.item-footer {
  display: flex;
  justify-content: flex-end;
  gap: 4px;
  margin-top: 8px;
  border-top: 1px dashed var(--border-soft);
}

@media (max-width: 768px) {
  .item-main {
    flex-direction: column;
  }

  .cover {
    width: 100%;
    height: 180px;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>

