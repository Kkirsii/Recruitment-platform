<template>
  <div class="app-container">
    <Navigate />
    <div class="main-content">
      <div class="content-wrapper">
        <RouterView />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
import Navigate from './Navigate.vue'
</script>

<style scoped>
.app-container {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  position: relative;
}

.main-content {
  flex: 1;
  margin-left: 280px;
  min-height: 100vh;
  padding: 0;
  overflow: hidden;
}

.content-wrapper {
  margin: 2rem;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  min-height: calc(100vh - 4rem);
  overflow: hidden;
}

.content-wrapper::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="dots" width="30" height="30" patternUnits="userSpaceOnUse"><circle cx="15" cy="15" r="1" fill="rgba(102,126,234,0.05)"/></pattern></defs><rect width="100" height="100" fill="url(%23dots)"/></svg>');
  pointer-events: none;
}

/* 16:9 屏幕优化 */
@media (min-width: 1920px) {
  .main-content {
    margin-left: 300px;
  }
  
  .content-wrapper {
    margin: 2.5rem;
    min-height: calc(100vh - 5rem);
  }
}

@media (min-width: 1366px) and (max-width: 1919px) {
  .main-content {
    margin-left: 280px;
  }
  
  .content-wrapper {
    margin: 2rem;
  }
}

@media (max-width: 1024px) {
  .main-content {
    margin-left: 260px;
  }
  
  .content-wrapper {
    margin: 1.5rem;
    min-height: calc(100vh - 3rem);
  }
}

@media (max-width: 768px) {
  .main-content {
    margin-left: 240px;
  }
  
  .content-wrapper {
    margin: 1rem;
    min-height: calc(100vh - 2rem);
  }
}

@media (max-width: 640px) {
  .main-content {
    margin-left: 0;
  }
  
  .content-wrapper {
    margin: 0.5rem;
    min-height: calc(100vh - 1rem);
    border-radius: 16px;
  }
}
</style>
  