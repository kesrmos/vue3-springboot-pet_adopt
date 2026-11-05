<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const conditions = [
  '年满 18 周岁，有独立经济能力与稳定住所',
  '家人 / 室友均同意养宠，居住环境允许饲养',
  '愿意为宠物提供科学喂养、定期疫苗与驱虫',
  '承诺不遗弃、不转卖，接受后续回访',
  '理解养宠是十几年的责任，而非一时兴起'
]

const flow = [
  { title: '浏览宠物', desc: '在宠物列表挑选合适的小伙伴，先了解它的性格与需求。' },
  { title: '提交申请', desc: '登录后填写居住环境、养宠经验等信息，等待管理员审核。' },
  { title: '审核沟通', desc: '管理员会核对信息，必要时电话沟通，请保持联系方式畅通。' },
  { title: '接它回家', desc: '审核通过后约定时间见面交接，并完成基础回访登记。' }
]

const notices = [
  '领养前请准备好猫砂盆、食盆、窝、牵引绳等基础用品。',
  '新环境需要 1 - 2 周适应期，请给它安静的空间。',
  '按时接种疫苗、定期驱虫，适龄绝育更利于健康。',
  '如遇特殊情况无法继续饲养，请第一时间联系我们，切勿随意遗弃。'
]

const faqs = [
  { q: '领养需要付费吗？', a: '本平台不收取任何领养费用，请警惕任何以领养为名的收费行为。' },
  { q: '没有养宠经验可以申请吗？', a: '可以。如实填写经验情况即可，我们更看重责任心与稳定的居住环境。' },
  { q: '提交申请后多久出结果？', a: '一般 1 - 3 个工作日内完成审核，可在「我的申请」查看实时状态。' },
  { q: '同一只宠物可以重复申请吗？', a: '同一只宠物在待审核期间不能重复提交；若被驳回，可以重新提交申请。' }
]

const activeNames = ref(['0'])
</script>

<template>
  <div class="container about-page">
    <div class="page-hero">
      <h1 class="page-title">领养须知</h1>
      <p class="page-sub">领养不是施舍，而是一份需要认真履行的承诺</p>
    </div>

    <div class="about-grid">
      <section class="about-card">
        <h2 class="card-title">🐾 领养条件</h2>
        <ul class="list">
          <li v-for="item in conditions" :key="item">{{ item }}</li>
        </ul>
      </section>

      <section class="about-card">
        <h2 class="card-title">📋 领养流程</h2>
        <el-timeline class="timeline">
          <el-timeline-item
            v-for="(item, index) in flow"
            :key="item.title"
            :timestamp="`第 ${index + 1} 步`"
            placement="top"
            color="#f08a4b"
          >
            <p class="timeline-title">{{ item.title }}</p>
            <p class="timeline-desc">{{ item.desc }}</p>
          </el-timeline-item>
        </el-timeline>
      </section>
    </div>

    <section class="about-card full">
      <h2 class="card-title">💡 注意事项</h2>
      <div class="notice-grid">
        <div v-for="item in notices" :key="item" class="notice-item">{{ item }}</div>
      </div>
    </section>

    <section class="about-card full">
      <h2 class="card-title">❓ 常见问题</h2>
      <el-collapse v-model="activeNames">
        <el-collapse-item v-for="(item, index) in faqs" :key="item.q" :title="item.q" :name="String(index)">
          <p class="faq-answer">{{ item.a }}</p>
        </el-collapse-item>
      </el-collapse>
    </section>

    <section class="cta">
      <div>
        <h3 class="cta-title">准备好给它一个家了吗？</h3>
        <p class="cta-desc">去宠物列表看看，也许它正在等你。</p>
      </div>
      <el-button type="primary" size="large" round @click="router.push('/petList')">浏览宠物</el-button>
    </section>
  </div>
</template>

<style scoped>
.about-page {
  padding-top: 32px;
}

.page-hero {
  padding: 34px 30px;
  border-radius: 20px;
  background: linear-gradient(120deg, #fff3e6, #ffe6d2);
  border: 1px solid #ffdcc2;
}

.page-title {
  font-size: 30px;
  font-weight: 800;
  color: #4a3f36;
}

.page-sub {
  margin-top: 8px;
  color: #8a7361;
  font-size: 14px;
}

.about-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
  margin-top: 24px;
}

.about-card {
  padding: 24px 26px;
  background: #fff;
  border: 1px solid var(--border-soft);
  border-radius: 18px;
  box-shadow: var(--shadow-soft);
}

.about-card.full {
  margin-top: 20px;
}

.card-title {
  margin-bottom: 14px;
  font-size: 18px;
  font-weight: 700;
  color: #41372f;
}

.list {
  margin: 0;
  padding-left: 18px;
  color: #6b5f56;
  font-size: 14px;
  line-height: 2.1;
}

.timeline {
  padding-left: 4px;
}

.timeline :deep(.el-timeline-item__timestamp) {
  color: var(--brand);
  font-weight: 600;
}

.timeline-title {
  font-size: 15px;
  font-weight: 700;
  color: #4a3f36;
}

.timeline-desc {
  margin-top: 4px;
  color: var(--text-sub);
  font-size: 13px;
  line-height: 1.8;
}

.notice-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 12px;
}

.notice-item {
  padding: 14px 16px;
  background: #fffaf5;
  border: 1px solid var(--border-soft);
  border-radius: 12px;
  color: #6b5f56;
  font-size: 13px;
  line-height: 1.8;
}

.faq-answer {
  color: var(--text-sub);
  font-size: 13px;
  line-height: 1.9;
}

.cta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  margin-top: 24px;
  padding: 26px 30px;
  border-radius: 18px;
  background: linear-gradient(120deg, #fff5eb, #ffe9d8);
  border: 1px solid #ffdcc2;
}

.cta-title {
  font-size: 19px;
  font-weight: 700;
  color: var(--brand-dark);
}

.cta-desc {
  margin-top: 6px;
  color: #8a7361;
  font-size: 13px;
}

@media (max-width: 768px) {
  .cta {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>

