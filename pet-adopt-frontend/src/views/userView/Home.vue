<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getPetList } from '@/api/pet'
import PetCard from '@/components/PetCard.vue'

const router = useRouter()

const loading = ref(true)
const pets = ref([])
const pendingTotal = ref(0)
const adoptedTotal = ref(0)

const steps = [
  { icon: '🔍', title: '浏览宠物', desc: '在待领养列表里挑选让你心动的小伙伴' },
  { icon: '📝', title: '提交申请', desc: '填写居住环境与养宠经验，让我们更了解你' },
  { icon: '🏡', title: '审核与见面', desc: '管理员审核通过后，就可以接它回家啦' }
]

onMounted(async () => {
  loading.value = true
  try {
    const [pending, adopted] = await Promise.all([
      getPetList({ page: 1, size: 8, status: 0 }),
      getPetList({ page: 1, size: 1, status: 1 })
    ])
    pets.value = pending?.records || []
    pendingTotal.value = Number(pending?.total) || 0
    adoptedTotal.value = Number(adopted?.total) || 0
  } catch (e) {
    pets.value = []
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="home">
    <section class="hero">
      <div class="container hero-inner">
        <div class="hero-text">
          <p class="hero-badge">🐾 用领养代替购买</p>
          <h1 class="hero-title">给它一个<br /><span>温暖的家</span></h1>
          <p class="hero-desc">
            这里有一群等待被爱的小家伙，它们健康、亲人，只差一个愿意负责的你。
          </p>
          <div class="hero-actions">
            <el-button type="primary" size="large" round @click="router.push('/petList')">
              我要领养
            </el-button>
            <el-button size="large" round plain @click="router.push('/about')">
              领养须知
            </el-button>
          </div>
        </div>

        <div class="hero-stats">
          <div class="stat-card">
            <p class="stat-num">{{ pendingTotal }}</p>
            <p class="stat-label">待领养小伙伴</p>
          </div>
          <div class="stat-card">
            <p class="stat-num">{{ adoptedTotal }}</p>
            <p class="stat-label">已找到新家</p>
          </div>
          <div class="stat-card">
            <p class="stat-num">3</p>
            <p class="stat-label">步完成领养</p>
          </div>
        </div>
      </div>
    </section>

    <section class="container section">
      <div class="section-head">
        <div>
          <h2 class="section-title">等待领养的小可爱</h2>
          <p class="section-desc">它们都已完成基础检查，期待与你相遇</p>
        </div>
        <el-button text type="primary" @click="router.push('/petList')">查看全部 →</el-button>
      </div>

      <div v-if="loading" class="pet-grid">
        <div v-for="i in 4" :key="i" class="skeleton-card">
          <el-skeleton animated>
            <template #template>
              <el-skeleton-item variant="image" style="width: 100%; height: 190px" />
              <div style="padding: 16px">
                <el-skeleton-item variant="h3" style="width: 40%" />
                <el-skeleton-item variant="text" style="margin-top: 12px" />
                <el-skeleton-item variant="text" style="width: 80%" />
              </div>
            </template>
          </el-skeleton>
        </div>
      </div>

      <div v-else-if="pets.length" class="pet-grid">
        <PetCard v-for="pet in pets" :key="pet.id" :pet="pet" />
      </div>

      <el-empty v-else description="暂时没有待领养的小可爱，稍后再来看看吧" />
    </section>

    <section class="container section">
      <div class="section-head">
        <div>
          <h2 class="section-title">领养只需三步</h2>
          <p class="section-desc">认真对待每一次领养，是对生命最好的尊重</p>
        </div>
      </div>
      <div class="step-grid">
        <div v-for="(step, index) in steps" :key="step.title" class="step-card">
          <span class="step-index">0{{ index + 1 }}</span>
          <p class="step-icon">{{ step.icon }}</p>
          <h3 class="step-title">{{ step.title }}</h3>
          <p class="step-desc">{{ step.desc }}</p>
        </div>
      </div>
    </section>

    <section class="container">
      <div class="notice-card">
        <div>
          <h3 class="notice-title">领养小提示</h3>
          <p class="notice-desc">
            领养前请确认家人同意、居住环境允许养宠，并准备好未来十几年的陪伴与开销。
            提交申请后，管理员会尽快审核，请留意「我的申请」中的审核进度。
          </p>
        </div>
        <el-button type="primary" plain round @click="router.push('/about')">了解领养要求</el-button>
      </div>
    </section>
  </div>
</template>

<style scoped>
.hero {
  padding: 56px 0 48px;
  background:
    radial-gradient(circle at 88% 12%, #ffe3cd 0, transparent 45%),
    linear-gradient(135deg, #fff6ec 0%, #ffeadb 100%);
  border-bottom: 1px solid var(--border-soft);
}

.hero-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 40px;
}

.hero-badge {
  display: inline-block;
  padding: 6px 14px;
  border-radius: 999px;
  background: #fff;
  color: var(--brand-dark);
  font-size: 13px;
  box-shadow: var(--shadow-soft);
}

.hero-title {
  margin-top: 18px;
  font-size: 46px;
  line-height: 1.25;
  font-weight: 800;
  letter-spacing: 1px;
  color: #4a3f36;
}

.hero-title span {
  color: var(--brand);
}

.hero-desc {
  max-width: 460px;
  margin-top: 16px;
  color: #7d6e62;
  font-size: 15px;
  line-height: 1.9;
}

.hero-actions {
  display: flex;
  gap: 14px;
  margin-top: 28px;
}

.hero-stats {
  display: flex;
  flex-direction: column;
  gap: 14px;
  min-width: 220px;
}

.stat-card {
  padding: 16px 22px;
  background: #fff;
  border-radius: 16px;
  box-shadow: var(--shadow-soft);
}

.stat-num {
  font-size: 28px;
  font-weight: 800;
  color: var(--brand);
  line-height: 1.2;
}

.stat-label {
  margin-top: 4px;
  color: var(--text-sub);
  font-size: 13px;
}

.section {
  margin-top: 52px;
}

.section-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 22px;
}

.pet-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 22px;
}

.skeleton-card {
  overflow: hidden;
  background: #fff;
  border-radius: 16px;
  border: 1px solid var(--border-soft);
}

.step-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
}

.step-card {
  position: relative;
  padding: 26px 22px;
  background: #fff;
  border: 1px solid var(--border-soft);
  border-radius: 16px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.step-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-soft);
}

.step-index {
  position: absolute;
  top: 14px;
  right: 20px;
  font-size: 30px;
  font-weight: 800;
  color: #ffe7d6;
}

.step-icon {
  font-size: 26px;
}

.step-title {
  margin-top: 12px;
  font-size: 17px;
  font-weight: 700;
}

.step-desc {
  margin-top: 8px;
  color: var(--text-sub);
  font-size: 13px;
  line-height: 1.8;
}

.notice-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  margin-top: 48px;
  padding: 26px 30px;
  background: linear-gradient(120deg, #fff5eb, #ffe9d8);
  border: 1px solid #ffdcc2;
  border-radius: 18px;
}

.notice-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--brand-dark);
}

.notice-desc {
  max-width: 680px;
  margin-top: 8px;
  color: #7d6e62;
  font-size: 13px;
  line-height: 1.9;
}

@media (max-width: 900px) {
  .hero-inner {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-title {
    font-size: 34px;
  }

  .hero-stats {
    flex-direction: row;
    flex-wrap: wrap;
    width: 100%;
  }

  .stat-card {
    flex: 1;
    min-width: 120px;
  }

  .notice-card {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>

