<!-- ChatRoom.vue -->
<template>
  <div class="chat-page">
    <div class="chat-header">
      <div class="header-content">
        <div class="chat-title">
          <i class="fas fa-comments"></i>
          <h1>在线面试</h1>
        </div>
        <div class="header-stats">
          <div class="stat-item">
            <i class="fas fa-users"></i>
            <span>在线用户</span>
          </div>
          <div class="stat-item">
            <i class="fas fa-clock"></i>
            <span>工作时间</span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="chat-layout">
      <div class="chat-main">
        <ChatBox />
      </div>
      <div class="chat-sidebar">
        <ChatOnlineMember />
      </div>
    </div>
  </div>
</template>

<script setup>
import ChatBox from './ChatBox.vue';
import ChatOnlineMember from './ChatOnlineMember.vue';
</script>

<style scoped>
.chat-page {
  min-height: 100vh;
  height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 0;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  margin-left: 200px;
}

.chat-layout {
  flex: 1;
  max-width: 100%;
  margin: 0 auto;
  width: 100%;
  display: grid;
  grid-template-columns: minmax(0, 3fr) minmax(250px, 1fr);
  height: 100%;
}

.chat-main {
  background: white;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: 100%;
  border-right: 1px solid #e2e8f0;
}

.chat-sidebar {
  background: white;
  overflow: hidden;
  height: 100%;
  min-width: 250px;
}

@media (min-width: 1920px) {
  .chat-layout {
    grid-template-columns: minmax(0, 4fr) minmax(300px, 1fr);
  }
}

@media (max-width: 1366px) {
  .chat-layout {
    grid-template-columns: minmax(0, 2.5fr) minmax(250px, 1fr);
  }
}

@media (max-width: 1024px) {
  .chat-layout {
    grid-template-columns: 1fr;
  }
  
  .chat-sidebar {
    display: none;
  }
}

@media (max-width: 640px) {
  .chat-page {
    padding: 0;
    margin-left: 0;
  }
  
  .chat-layout {
    height: 100vh;
  }

  .chat-main, 
  .chat-sidebar {
    border-radius: 0;
  }
}

/* 新增美化样式 */
.chat-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1rem 2rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1400px;
  margin: 0 auto;
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.chat-title i {
  font-size: 1.5rem;
  color: #fbb03b;
}

.chat-title h1 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
}

.header-stats {
  display: flex;
  gap: 2rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  opacity: 0.9;
}

.stat-item i {
  color: #fbb03b;
}

.chat-main {
  position: relative;
}

.chat-main::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #fbb03b);
}

.chat-sidebar {
  background: #f8fafc;
  position: relative;
}

.chat-sidebar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #fbb03b, #667eea);
}
</style>
  