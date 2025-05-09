import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * 定义管理分页状态
 */
export const usePaginationStore = defineStore('pagination', () => {
  const current = ref(1)
  const pageSize = ref(10)
  const total = ref(0)

  const setCurrent = (val: number) => { current.value = val }
  const setPageSize = (val: number) => { pageSize.value = val }
  const setTotal = (val: number) => { total.value = val }

  const reset = () => {
    current.value = 1
    pageSize.value = 10
    total.value = 0
  }

  return {
    current,
    pageSize,
    total,
    setCurrent,
    setPageSize,
    setTotal,
    reset,
  }
})
