<script lang="ts" setup>
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalContent from '@/components/GlobalContent.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'
</script>

<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <!-- 网址头部 -->
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <!-- 网址内容 -->
      <a-layout-content class="content">
        <GlobalContent />
      </a-layout-content>
      <!-- 网址页脚 -->
      <a-layout-footer class="footer">
        <GlobalFooter />
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<style scoped>
#basicLayout .header {
  padding-inline: 20px;
  margin-bottom: 16px;
  color: unset;
  background: white;
}

#basicLayout .content {
  background: linear-gradient(to right, #fefefe, #fff);
  margin-bottom: 28px;
  padding: 20px;
}

#basicLayout .footer {
  background: #efefef;
  padding: 16px;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  text-align: center;
}
</style>
