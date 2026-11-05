<script setup>
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPetDetail } from '@/api/pet'
import { useUserStore } from '@/stores/userStore'
import placeholder from '@/assets/images/placeholder.svg'
import { petStatusLabel, petStatusType, formatAge, formatTime } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const pet = ref(null)
const loading = ref(true)
const notFound = ref(false)

async function loadPet() {
  loading.value = true
  notFound.value = false
  try {
    pet.value = await getPetDetail(route.params.id)
  } catch (e) {
    pet.value = null
    notFound.value = true
  } finally {
    loading.value = false
  }
}

onMounted(loadPet)
watch(() => route.params.id, loadPet)

function handleApply() {
  if (!pet.value || Number(pet.value.status) === 1) return
  const target = `/apply?petId=${pet.value.id}`
  if (!userStore.isLogin) {
    ElMessage.warning('登录后才能提交领养申请')
    router.push({ path: '/login', query: { redirect: target } })
    return
  }
  router.push(target)
}
</script>

<template>
  <div class="container detail-page">
    <el-button text class="back" @click="router.back()">← 返回</el-button>

    <div v-if="loading" class="detail-card">
      <el-skeleton :rows="6" animated />
    </div>

    <el-result
      v-else-if="notFound"
      icon="warning"
      title="宠物不存在"
      sub-title="它可能已经被领养，或者已经被移除"
    >
      <template #extra>
        <el-button type="primary" @click="router.push('/petList')">看看其它宠物</el-button>
      </template>
    </el-result>

    <div v-else-if="pet" class="detail-card">
      <div class="gallery">
        <el-image class="main-image" :src="pet.photo" fit="cover">
          <template #error>
            <img class="main-image" :src="placeholder" alt="暂无图片" />
          </template>
        </el-image>
        <el-tag class="status" :type="petStatusType(pet.status)" effect="dark" round>
          {{ petStatusLabel(pet.status) }}
        </el-tag>
      </div>

      <div class="info">
        <h1 class="name">{{ pet.name }}</h1>
        <div class="tags">
          <el-tag type="warning" effect="plain" round>{{ pet.type }}</el-tag>
          <el-tag v-if="pet.gender" type="info" effect="plain" round>{{ pet.gender }}</el-tag>
          <el-tag type="info" effect="plain" round>{{ formatAge(pet.age) }}</el-tag>
        </div>

        <p class="desc">{{ pet.description || '这个小伙伴还没有留下自我介绍～' }}</p>

        <ul class="meta-list">
          <li><span>类型</span>{{ pet.type || '-' }}</li>
          <li><span>性别</span>{{ pet.gender || '-' }}</li>
          <li><span>年龄</span>{{ formatAge(pet.age) }}</li>
          <li><span>发布时间</span>{{ formatTime(pet.createTime) }}</li>
        </ul>

        <div class="actions">
          <el-button
            type="primary"
            size="large"
            round
            :disabled="Number(pet.status) === 1"
            @click="handleApply"
          >
            {{ Number(pet.status) === 1 ? '它已经找到家了' : '我要领养' }}
          </el-button>
          <el-button size="large" round plain @click="router.push('/petList')">再看看</el-button>
        </div>

        <p class="tip">
          提交申请后可在「我的申请」查看审核进度；审核通过前请勿重复提交同一只宠物。
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.detail-page {
  padding-top: 24px;
}

.back {
  margin-bottom: 12px;
  color: var(--text-sub);
}

.detail-card {
  display: grid;
  grid-template-columns: 460px 1fr;
  gap: 36px;
  padding: 26px;
  background: #fff;
  border: 1px solid var(--border-soft);
  border-radius: 20px;
  box-shadow: var(--shadow-soft);
}

.gallery {
  position: relative;
}

.main-image {
  width: 100%;
  height: 360px;
  border-radius: 16px;
  object-fit: cover;
  background: #fff3e9;
}

.status {
  position: absolute;
  top: 14px;
  left: 14px;
}

.name {
  font-size: 30px;
  font-weight: 800;
  color: #41372f;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.desc {
  margin-top: 18px;
  color: #6f6357;
  font-size: 14px;
  line-height: 1.9;
}

.meta-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px 24px;
  margin: 22px 0 0;
  padding: 18px 20px;
  list-style: none;
  background: #fffaf5;
  border: 1px dashed var(--border-soft);
  border-radius: 14px;
  font-size: 14px;
  color: #5b5047;
}

.meta-list span {
  display: inline-block;
  width: 72px;
  color: var(--text-sub);
}

.actions {
  display: flex;
  gap: 12px;
  margin-top: 26px;
}

.tip {
  margin-top: 16px;
  color: #a89c92;
  font-size: 12px;
}

@media (max-width: 900px) {
  .detail-card {
    grid-template-columns: 1fr;
    gap: 22px;
  }

  .main-image {
    height: 260px;
  }
}
</style>
