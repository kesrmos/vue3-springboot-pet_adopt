import { defineStore } from 'pinia'
import { getPetList } from '@/api/pet'

const createDefaultFilters = () => ({ type: '', gender: '', status: '' })

/**
 * 前台宠物列表状态（首页/列表页共用请求逻辑）
 */
export const usePetStore = defineStore('pet', {
  state: () => ({
    list: [],
    total: 0,
    page: 1,
    size: 12,
    filters: createDefaultFilters(),
    loading: false
  }),

  actions: {
    async fetchPets() {
      this.loading = true
      try {
        const params = { page: this.page, size: this.size }
        if (this.filters.type) params.type = this.filters.type
        if (this.filters.gender) params.gender = this.filters.gender
        if (this.filters.status !== '' && this.filters.status !== null && this.filters.status !== undefined) {
          params.status = this.filters.status
        }

        const data = await getPetList(params)
        this.list = data?.records || []
        this.total = Number(data?.total) || 0
        this.page = Number(data?.page) || this.page
        this.size = Number(data?.size) || this.size
      } catch (e) {
        this.list = []
        this.total = 0
      } finally {
        this.loading = false
      }
    },

    /** 条件查询：回到第一页 */
    applyFilters() {
      this.page = 1
      return this.fetchPets()
    },

    resetFilters() {
      this.filters = createDefaultFilters()
      this.page = 1
      return this.fetchPets()
    },

    changePage(page, size) {
      this.page = page
      if (size) this.size = size
      return this.fetchPets()
    }
  }
})
