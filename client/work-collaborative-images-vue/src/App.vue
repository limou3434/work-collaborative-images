<script setup lang="ts">
import BasicLayout from "@/layouts/BasicLayout.vue";
import { useLoginUserStore } from '@/stores/loginUser.ts'

const loginUserStore = useLoginUserStore()
loginUserStore.getLoginUser()
</script>

<template>
  <div id="app">
    <BasicLayout />
  </div>
</template>

<style scoped>
#app {
}
</style>
