<script setup>
import { onMounted, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminApplyList, auditApply } from '@/api/apply'
import Pagination from '@/components/Pagination.vue'
import placeholder from '@/assets/images/placeholder.svg'
import { auditStatusLabel, auditStatusType, formatTime, petSubtitle } from '@/utils/format'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const activeStatus = ref('')

const dialogVisible = ref(false)
const saving = ref(false)
const currentRow = ref(null)

const auditForm = reactive({ auditStatus: 1, auditRemark: '' })

const statusTabs = [
  { label: '全部', value: '' },
  { label: '待审核', value: 0 },
  { label: '已通过', value: 1 },
  { label: '已驳回', value: 2 }
]

const quickRemarks = [
  '条件符合，同意领养',
  '居住环境暂不适合，感谢关注',
  '资料不完整，请补充后重新申请'
]

async function fetchList() {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (activeStatus.value !== '') params.auditStatus = activeStatus.value

    const data = await getAdminApplyList(params)
    list.value = data?.records || []
    total.value = Number(data?.total) || 0
  } catch (e) {
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

watch(activeStatus, () => {
  page.value = 1
  fetchList()
})

function handlePageChange(payload) {
  page.value = payload.page
  size.value = payload.size
  fetchList()
}

function openAudit(row) {
  currentRow.value = row
  auditForm.auditStatus = 1
  auditForm.auditRemark = ''
  dialogVisible.value = true
}

function fillRemark(text) {
  auditForm.auditRemark = text
}

async function handleAudit() {
  if (!currentRow.value) return

  const passed = Number(auditForm.auditStatus) === 1
  if (!passed && !auditForm.auditRemark.trim()) {
    ElMessage.warning('驳回时请填写审核意见')
    return
  }

  saving.value = true
  try {
    await auditApply({
      applyId: currentRow.value.id,
      auditStatus: Number(auditForm.auditStatus),
      auditRemark: auditForm.auditRemark.trim() || (passed ? '条件符合，同意领养' : '')
    })
    dialogVisible.value = false
    if (passed) {
      ElMessage.success('审核通过：宠物已标记为已领养，该宠物其它待审核申请已自动驳回')
    } else {
      ElMessage.success('已驳回该申请')
    }
    fetchList()
  } catch (e) {
    // 提示已由请求层处理
  } finally {
    saving.value = false
  }
}

onMounted(fetchList)
</script>

<template>
  <div class="page">
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-head">
          <span class="card-title">领养申请（共 {{ total }} 条）</span>
          <el-radio-group v-model="activeStatus">
            <el-radio-button
              v-for="tab in statusTabs"
              :key="String(tab.value)"
              :value="tab.value"
            >
              {{ tab.label }}
            </el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="宠物" width="190">
          <template #default="{ row }">
            <div class="pet-cell">
              <el-image class="thumb" :src="row.petPhoto" fit="cover">
                <template #error>
                  <img class="thumb" :src="placeholder" alt="" />
                </template>
              </el-image>
              <div>
                <p class="cell-main">{{ row.petName || '-' }}</p>
                <p class="cell-sub">
                  {{ petSubtitle({ type: row.petType, gender: row.petGender, age: row.petAge }) }}
                </p>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="申请人" width="160">
          <template #default="{ row }">
            <p class="cell-main">{{ row.userName || '-' }}</p>
            <p class="cell-sub">{{ row.userPhone || '-' }}</p>
          </template>
        </el-table-column>
        <el-table-column prop="liveEnv" label="居住环境" min-width="150" show-overflow-tooltip />
        <el-table-column prop="exp" label="养宠经验" min-width="130" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="auditStatusType(row.auditStatus)" effect="light" round>
              {{ auditStatusLabel(row.auditStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditRemark" label="审核意见" min-width="150" show-overflow-tooltip />
        <el-table-column label="申请时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="Number(row.auditStatus) === 0"
              link
              type="primary"
              @click="openAudit(row)"
            >
              审核
            </el-button>
            <span v-else class="done-text">已完成</span>
          </template>
        </el-table-column>
      </el-table>

      <Pagination :total="total" :page="page" :size="size" @change="handlePageChange" />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="审核领养申请"
      width="560px"
      :close-on-click-modal="false"
    >
      <div v-if="currentRow" class="audit-summary">
        <p class="summary-line">
          申请人：{{ currentRow.userName || '-' }}（{{ currentRow.userPhone || '无电话' }}）
        </p>
        <p class="summary-line">
          宠物：{{ currentRow.petName || '-' }} ·
          {{ petSubtitle({ type: currentRow.petType, gender: currentRow.petGender, age: currentRow.petAge }) }}
        </p>
        <p class="summary-line">居住环境：{{ currentRow.liveEnv || '-' }}</p>
        <p class="summary-line">养宠经验：{{ currentRow.exp || '未填写' }}</p>
        <p class="summary-line">备注：{{ currentRow.remark || '未填写' }}</p>
      </div>

      <el-form label-width="88px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.auditStatus">
            <el-radio :value="1">通过</el-radio>
            <el-radio :value="2">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input
            v-model="auditForm.auditRemark"
            type="textarea"
            :rows="3"
            maxlength="500"
            show-word-limit
            placeholder="通过可填「条件符合，同意领养」，驳回请说明原因"
          />
          <div class="quick-remarks">
            <el-tag
              v-for="text in quickRemarks"
              :key="text"
              class="quick-tag"
              effect="plain"
              round
              @click="fillRemark(text)"
            >
              {{ text }}
            </el-tag>
          </div>
        </el-form-item>
      </el-form>

      <el-alert
        type="warning"
        :closable="false"
        show-icon
        title="审核通过后：宠物将标记为已领养，该宠物其它待审核申请会自动驳回。"
      />

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleAudit">提交审核</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.table-card :deep(.el-card__header) {
  padding: 14px 20px;
}

.card-head {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.card-title {
  font-size: 15px;
  font-weight: 700;
  color: #41372f;
}

.pet-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.thumb {
  width: 56px;
  height: 42px;
  border-radius: 8px;
  object-fit: cover;
  flex-shrink: 0;
  background: #fff3e9;
}

.cell-main {
  font-size: 13px;
  font-weight: 600;
  color: #41372f;
}

.cell-sub {
  font-size: 12px;
  color: var(--text-sub);
}

.done-text {
  color: #a89c92;
  font-size: 12px;
}

.audit-summary {
  margin-bottom: 18px;
  padding: 14px 16px;
  background: #fffaf5;
  border: 1px dashed #ffd9c0;
  border-radius: 12px;
}

.summary-line {
  font-size: 13px;
  line-height: 1.9;
  color: #6b5f56;
}

.quick-remarks {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.quick-tag {
  cursor: pointer;
}

.quick-tag:hover {
  color: var(--brand);
  border-color: var(--brand);
}
</style>


