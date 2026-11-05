<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPetList } from '@/api/pet'
import { submitApply } from '@/api/apply'
import placeholder from '@/assets/images/placeholder.svg'
import { petSubtitle } from '@/utils/format'

const route = useRoute()
const router = useRouter()

const formRef = ref()
const submitting = ref(false)
const loadingPets = ref(false)
const petOptions = ref([])

const form = reactive({
  petId: null,
  liveEnv: '',
  exp: '',
  remark: ''
})

const rules = {
  petId: [{ required: true, message: '请选择要领养的宠物', trigger: 'change' }],
  liveEnv: [
    { required: true, message: '请填写居住环境', trigger: 'blur' },
    { min: 2, max: 255, message: '居住环境需 2 - 255 个字符', trigger: 'blur' }
  ],
  exp: [{ max: 500, message: '最多 500 个字符', trigger: 'blur' }],
  remark: [{ max: 500, message: '最多 500 个字符', trigger: 'blur' }]
}

const quickEnvs = ['自有住房', '合租公寓', '与家人同住', '独立小院']

const selectedPet = computed(() => petOptions.value.find((item) => item.id === form.petId) || null)

async function loadPetOptions() {
  loadingPets.value = true
  try {
    const data = await getPetList({ page: 1, size: 100, status: 0 })
    petOptions.value = data?.records || []
  } catch (e) {
    petOptions.value = []
  } finally {
    loadingPets.value = false
  }
}

function fillEnv(text) {
  form.liveEnv = text
}

async function handleSubmit() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }

  submitting.value = true
  try {
    await submitApply({
      petId: form.petId,
      liveEnv: form.liveEnv.trim(),
      exp: form.exp.trim(),
      remark: form.remark.trim()
    })
    ElMessage.success('申请提交成功，请等待管理员审核')
    router.push('/myApply')
  } catch (e) {
    // 错误提示已由请求层统一处理
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  await loadPetOptions()
  const queryId = Number(route.query.petId)
  if (queryId && petOptions.value.some((item) => item.id === queryId)) {
    form.petId = queryId
  }
})
</script>

<template>
  <div class="container apply-page">
    <div class="page-head">
      <h1 class="section-title">领养申请</h1>
      <p class="section-desc">认真填写以下信息，能帮助我们更快完成审核</p>
    </div>

    <div class="apply-card">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="96px"
        class="apply-form"
      >
        <el-form-item label="领养宠物" prop="petId">
          <el-select
            v-model="form.petId"
            filterable
            clearable
            :loading="loadingPets"
            placeholder="请选择要领养的宠物"
            style="width: 320px"
          >
            <el-option
              v-for="pet in petOptions"
              :key="pet.id"
              :label="`${pet.name}（${petSubtitle(pet)}）`"
              :value="pet.id"
            />
          </el-select>
        </el-form-item>

        <div v-if="selectedPet" class="selected-pet">
          <el-image class="selected-cover" :src="selectedPet.photo" fit="cover">
            <template #error>
              <img class="selected-cover" :src="placeholder" alt="暂无图片" />
            </template>
          </el-image>
          <div>
            <p class="selected-name">{{ selectedPet.name }}</p>
            <p class="selected-meta">{{ petSubtitle(selectedPet) }}</p>
            <p class="selected-desc">{{ selectedPet.description || '暂无介绍' }}</p>
          </div>
        </div>

        <el-form-item label="居住环境" prop="liveEnv">
          <div class="field-block">
            <el-input
              v-model="form.liveEnv"
              type="textarea"
              :rows="3"
              maxlength="255"
              show-word-limit
              placeholder="例如：自有住房，两室一厅，带阳台，家人均同意养宠"
            />
            <div class="quick-tags">
              <el-tag
                v-for="text in quickEnvs"
                :key="text"
                class="quick-tag"
                effect="plain"
                round
                @click="fillEnv(text)"
              >
                {{ text }}
              </el-tag>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="养宠经验" prop="exp">
          <el-input
            v-model="form.exp"
            type="textarea"
            :rows="3"
            maxlength="500"
            show-word-limit
            placeholder="例如：养过 2 年猫，了解基本喂养与驱虫知识；无经验也可以如实填写"
          />
        </el-form-item>

        <el-form-item label="备注说明" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            maxlength="500"
            show-word-limit
            placeholder="想对管理员说的话，例如领养原因、可提供的陪伴时间等"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" round :loading="submitting" @click="handleSubmit">
            提交申请
          </el-button>
          <el-button size="large" round @click="router.back()">返回</el-button>
        </el-form-item>
      </el-form>

      <el-alert
        class="apply-alert"
        type="warning"
        :closable="false"
        show-icon
        title="温馨提示"
        description="同一只宠物在待审核期间不能重复提交申请；审核通过后该宠物会自动标记为已领养。"
      />
    </div>
  </div>
</template>

<style scoped>
.apply-page {
  padding-top: 32px;
}

.page-head {
  margin-bottom: 20px;
}

.apply-card {
  padding: 28px 30px 20px;
  background: #fff;
  border: 1px solid var(--border-soft);
  border-radius: 18px;
  box-shadow: var(--shadow-soft);
}

.apply-form {
  max-width: 760px;
}

.selected-pet {
  display: flex;
  gap: 16px;
  margin: 0 0 22px 96px;
  padding: 14px;
  background: #fffaf5;
  border: 1px dashed #ffd9c0;
  border-radius: 14px;
}

.selected-cover {
  width: 110px;
  height: 82px;
  border-radius: 10px;
  object-fit: cover;
  flex-shrink: 0;
  background: #fff3e9;
}

.selected-name {
  font-size: 16px;
  font-weight: 700;
  color: #453b33;
}

.selected-meta {
  margin-top: 4px;
  color: var(--brand-dark);
  font-size: 13px;
}

.selected-desc {
  margin-top: 6px;
  color: var(--text-sub);
  font-size: 12px;
  line-height: 1.7;
}

.field-block {
  width: 100%;
}

.quick-tags {
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

.apply-alert {
  max-width: 760px;
  margin-top: 10px;
  border-radius: 12px;
}

@media (max-width: 768px) {
  .apply-card {
    padding: 20px 16px 12px;
  }

  .selected-pet {
    margin-left: 0;
  }
}
</style>

