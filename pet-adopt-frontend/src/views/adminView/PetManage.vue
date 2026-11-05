<script setup>
import { nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminPetList, savePet, deletePet } from '@/api/pet'
import Pagination from '@/components/Pagination.vue'
import placeholder from '@/assets/images/placeholder.svg'
import {
  PET_TYPE_OPTIONS,
  GENDER_OPTIONS,
  petStatusLabel,
  petStatusType,
  formatAge,
  formatTime
} from '@/utils/format'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

const filters = reactive({ name: '', type: '', gender: '', status: '' })

const dialogVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const formRef = ref()

const form = reactive({
  id: null,
  name: '',
  type: '',
  gender: '公',
  age: 6,
  photo: '',
  description: '',
  status: 0
})

const rules = {
  name: [
    { required: true, message: '请输入宠物名称', trigger: 'blur' },
    { max: 50, message: '名称最多 50 个字符', trigger: 'blur' }
  ],
  type: [{ required: true, message: '请选择或输入宠物类型', trigger: 'change' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  age: [{ required: true, message: '请输入年龄（月）', trigger: 'blur' }],
  description: [{ max: 1000, message: '描述最多 1000 个字符', trigger: 'blur' }]
}

async function fetchList() {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (filters.name.trim()) params.name = filters.name.trim()
    if (filters.type) params.type = filters.type
    if (filters.gender) params.gender = filters.gender
    if (filters.status !== '') params.status = filters.status

    const data = await getAdminPetList(params)
    list.value = data?.records || []
    total.value = Number(data?.total) || 0
  } catch (e) {
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  fetchList()
}

function handleReset() {
  filters.name = ''
  filters.type = ''
  filters.gender = ''
  filters.status = ''
  handleSearch()
}

function handlePageChange(payload) {
  page.value = payload.page
  size.value = payload.size
  fetchList()
}

function resetForm() {
  Object.assign(form, {
    id: null,
    name: '',
    type: '',
    gender: '公',
    age: 6,
    photo: '',
    description: '',
    status: 0
  })
}

function openCreate() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

function openEdit(row) {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    name: row.name || '',
    type: row.type || '',
    gender: row.gender || '公',
    age: row.age ?? 6,
    photo: row.photo || '',
    description: row.description || '',
    status: row.status ?? 0
  })
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

async function handleSave() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }

  saving.value = true
  try {
    await savePet({
      id: form.id,
      name: form.name.trim(),
      type: (form.type || '').trim(),
      gender: form.gender,
      age: form.age,
      photo: (form.photo || '').trim(),
      description: (form.description || '').trim(),
      status: form.status
    })
    ElMessage.success(isEdit.value ? '保存成功' : '新增成功')
    dialogVisible.value = false
    fetchList()
  } catch (e) {
    // 提示已由请求层处理
  } finally {
    saving.value = false
  }
}

/** 下架/上架：后端只有 0 待领养 / 1 已领养，因此用状态切换表达 */
async function toggleStatus(row) {
  const next = Number(row.status) === 1 ? 0 : 1
  const actionText = next === 1 ? '标记为已领养（下架）' : '恢复为待领养（上架）'
  try {
    await ElMessageBox.confirm(`确定要将「${row.name}」${actionText}吗？`, '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
  } catch (e) {
    return
  }

  try {
    await savePet({
      id: row.id,
      name: row.name,
      type: row.type,
      gender: row.gender,
      age: row.age,
      photo: row.photo,
      description: row.description,
      status: next
    })
    ElMessage.success('状态已更新')
    fetchList()
  } catch (e) {
    // 提示已由请求层处理
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `删除「${row.name}」会同时删除它的全部领养申请记录，确定删除吗？`,
      '删除确认',
      { type: 'warning', confirmButtonText: '确定删除', cancelButtonText: '取消' }
    )
  } catch (e) {
    return
  }

  try {
    await deletePet(row.id)
    ElMessage.success('删除成功')
    if (list.value.length === 1 && page.value > 1) {
      page.value -= 1
    }
    fetchList()
  } catch (e) {
    // 提示已由请求层处理
  }
}

onMounted(fetchList)
</script>

<template>
  <div class="page">
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" @submit.prevent>
        <el-form-item label="名称">
          <el-input
            v-model="filters.name"
            placeholder="宠物名称"
            clearable
            style="width: 160px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="filters.type" placeholder="全部" clearable style="width: 110px">
            <el-option v-for="item in PET_TYPE_OPTIONS" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="filters.gender" placeholder="全部" clearable style="width: 100px">
            <el-option v-for="item in GENDER_OPTIONS" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待领养" :value="0" />
            <el-option label="已领养" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-head">
          <span class="card-title">宠物列表（共 {{ total }} 条）</span>
          <el-button type="primary" @click="openCreate">+ 新增宠物</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="list" stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="图片" width="96">
          <template #default="{ row }">
            <el-image class="thumb" :src="row.photo" fit="cover">
              <template #error>
                <img class="thumb" :src="placeholder" alt="" />
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="名称" width="110" />
        <el-table-column prop="type" label="类型" width="80" />
        <el-table-column prop="gender" label="性别" width="70" />
        <el-table-column label="年龄" width="100">
          <template #default="{ row }">{{ formatAge(row.age) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="petStatusType(row.status)" effect="light" round>
              {{ petStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
        <el-table-column label="创建时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="230" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="warning" @click="toggleStatus(row)">
              {{ Number(row.status) === 1 ? '恢复待领养' : '标记已领养' }}
            </el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <Pagination :total="total" :page="page" :size="size" @change="handlePageChange" />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑宠物' : '新增宠物'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" maxlength="50" placeholder="例如：豆豆" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select
            v-model="form.type"
            filterable
            allow-create
            default-first-option
            placeholder="选择或输入类型"
            style="width: 100%"
          >
            <el-option v-for="item in PET_TYPE_OPTIONS" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio v-for="item in GENDER_OPTIONS" :key="item" :value="item">{{ item }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄(月)" prop="age">
          <el-input-number v-model="form.age" :min="0" :max="360" />
        </el-form-item>
        <el-form-item label="照片地址" prop="photo">
          <el-input v-model="form.photo" maxlength="500" placeholder="https://example.com/pet.jpg" />
          <el-image v-if="form.photo" class="preview" :src="form.photo" fit="cover">
            <template #error>
              <img class="preview" :src="placeholder" alt="" />
            </template>
          </el-image>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            maxlength="1000"
            show-word-limit
            placeholder="性格、健康情况、生活习惯等"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">待领养</el-radio>
            <el-radio :value="1">已领养</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
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

.filter-card :deep(.el-card__body) {
  padding-bottom: 2px;
}

.table-card :deep(.el-card__header) {
  padding: 14px 20px;
}

.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 15px;
  font-weight: 700;
  color: #41372f;
}

.thumb {
  width: 62px;
  height: 46px;
  border-radius: 8px;
  object-fit: cover;
  background: #fff3e9;
}

.preview {
  width: 140px;
  height: 92px;
  margin-top: 10px;
  border-radius: 10px;
  object-fit: cover;
  background: #fff3e9;
}
</style>



