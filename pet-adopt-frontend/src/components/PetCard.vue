<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import placeholder from '@/assets/images/placeholder.svg'
import { petStatusLabel, petStatusType, petSubtitle } from '@/utils/format'

const props = defineProps({
  pet: { type: Object, required: true }
})

const router = useRouter()

const subtitle = computed(() => petSubtitle(props.pet))
const adopted = computed(() => Number(props.pet.status) === 1)

function goDetail() {
  router.push(`/pet/${props.pet.id}`)
}
</script>

<template>
  <article class="pet-card" :class="{ adopted }" @click="goDetail">
    <div class="cover">
      <el-image class="cover-img" :src="pet.photo" fit="cover" alt="">
        <template #error>
          <img class="cover-fallback" :src="placeholder" alt="暂无图片" />
        </template>
      </el-image>
      <el-tag
        class="status-tag"
        :type="petStatusType(pet.status)"
        effect="dark"
        round
        size="small"
      >
        {{ petStatusLabel(pet.status) }}
      </el-tag>
    </div>

    <div class="body">
      <h3 class="name">{{ pet.name }}</h3>
      <p class="meta">{{ subtitle }}</p>
      <p class="desc">{{ pet.description || '这个小伙伴还没有留下自我介绍～' }}</p>
      <div class="footer">
        <span class="more">{{ adopted ? '已找到家' : '查看详情 →' }}</span>
      </div>
    </div>
  </article>
</template>

<style scoped>
.pet-card {
  overflow: hidden;
  background: #fff;
  border: 1px solid var(--border-soft);
  border-radius: 16px;
  cursor: pointer;
  transition: transform 0.25s, box-shadow 0.25s;
}

.pet-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 14px 30px rgba(240, 138, 75, 0.18);
}

.pet-card.adopted {
  opacity: 0.88;
}

.cover {
  position: relative;
  height: 190px;
  background: #fff3e9;
}

.cover-img {
  width: 100%;
  height: 100%;
}

.cover-fallback {
  width: 100%;
  height: 190px;
  object-fit: cover;
}

.status-tag {
  position: absolute;
  top: 12px;
  right: 12px;
}

.body {
  padding: 14px 16px 16px;
}

.name {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-main);
}

.meta {
  margin-top: 6px;
  font-size: 13px;
  color: var(--brand-dark);
}

.desc {
  margin-top: 8px;
  height: 44px;
  overflow: hidden;
  color: var(--text-sub);
  font-size: 13px;
  line-height: 22px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px dashed var(--border-soft);
}

.more {
  font-size: 13px;
  font-weight: 600;
  color: var(--brand);
}
</style>
