<template>
  <div class="admin-chat-container">
    <div class="chat-header">
      <div class="header-content">
        <div class="chat-title">
          <i class="fas fa-comments"></i>
          <h1>管理员在线聊天</h1>
        </div>
        <div class="header-actions">
          <div class="connection-status" :class="{ connected: isConnected, disconnected: !isConnected }">
            <i class="fas fa-circle"></i>
            <span>{{ isConnected ? '已连接' : '未连接' }}</span>
          </div>
          <button @click="goBack" class="back-button">
            <i class="fas fa-arrow-left"></i>
            返回仪表板
          </button>
        </div>
      </div>
    </div>
    
    <div class="chat-layout">
      <div class="chat-main">
        <div class="messages-container" ref="messagesContainer">
          <div v-if="loading" class="loading-indicator">
            <i class="fas fa-spinner fa-spin"></i>
            <span>正在连接...</span>
          </div>
          
          <div v-if="error" class="error-message">
            <i class="fas fa-exclamation-triangle"></i>
            <span>{{ error }}</span>
            <button @click="reconnect" class="retry-button">重试</button>
          </div>
          
          <div v-for="(message, index) in messages" :key="index" class="message-item" :class="{ 'own-message': message.self }">
            <div class="message-content">
              <div class="message-header">
                <span class="message-user">{{ message.user }}</span>
                <span class="message-time">{{ formatTime(message.timestamp) }}</span>
              </div>
              <div class="message-text">{{ message.text }}</div>
            </div>
          </div>
        </div>
        
        <div class="input-container">
          <div class="input-wrapper">
            <textarea
              v-model="input"
              @keydown.enter.prevent="sendMessage"
              @keydown.shift.enter="input += '\n'"
              placeholder="输入消息... (Enter发送，Shift+Enter换行)"
              class="message-input"
              :disabled="!isConnected"
              ref="messageInput"
            ></textarea>
            <button @click="sendMessage" :disabled="!isConnected || !input.trim()" class="send-button">
              <i class="fas fa-paper-plane"></i>
            </button>
          </div>
        </div>
      </div>
      
      <div class="chat-sidebar">
        <div class="online-members">
          <h3>
            <i class="fas fa-users"></i>
            在线成员 ({{ members.length }})
          </h3>
          <div class="members-list">
            <div v-for="member in members" :key="member" class="member-item">
              <i class="fas fa-user-circle"></i>
              <span>{{ member }}</span>
            </div>
            <div v-if="members.length === 0" class="no-members">
              暂无在线成员
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useAdminStore } from '@/stores/admin'
import dayjs from 'dayjs'

const router = useRouter()
const adminStore = useAdminStore()

// 响应式数据
const messages = ref([])
const members = ref([])
const input = ref('')
const isConnected = ref(false)
const loading = ref(true)
const error = ref('')
const socket = ref(null)
const messagesContainer = ref(null)
const messageInput = ref(null)

// 连接WebSocket
const connect = () => {
  loading.value = true
  error.value = ''
  
  const token = adminStore.token
  if (!token) {
    error.value = '未找到认证令牌，请重新登录'
    loading.value = false
    return
  }
  
  const ws = new WebSocket(`ws://localhost:8080/chat?token=${encodeURIComponent(token)}`)
  socket.value = ws

  ws.onmessage = (event) => {
    try {
      const msg = JSON.parse(event.data)
      if (msg.type === "msg") {
        // 不显示自己发送的消息（避免重复）
        let currentUser = adminStore.adminInfo?.email
        if (msg.email === currentUser) return
        
        messages.value.push({
          user: msg.email,
          text: msg.msg,
          self: false,
          timestamp: new Date().toISOString()
        })
        scrollToBottom()
      }
      if (msg.type === 'members') {
        members.value = msg.members
      }
    } catch (e) {
      console.error('接收消息解析失败', e)
    }
  }

  ws.onopen = () => {
    console.log('WebSocket 已连接')
    isConnected.value = true
    loading.value = false
    error.value = ''
  }

  ws.onclose = () => {
    console.log('WebSocket 已关闭')
    isConnected.value = false
    if (!error.value) {
      error.value = '连接已断开'
    }
  }

  ws.onerror = (err) => {
    console.error('WebSocket 错误', err)
    error.value = '连接失败，请检查网络后重试'
    loading.value = false
  }
}

// 发送消息
const sendMessage = () => {
  if (!input.value.trim() || !isConnected.value) return
  
  const text = input.value.trim()
  socket.value.send(text)
  
  // 本地显示消息
  messages.value.push({
    user: adminStore.adminInfo?.email || '管理员',
    text: text,
    self: true,
    timestamp: new Date().toISOString()
  })
  
  input.value = ''
  scrollToBottom()
}

// 重连
const reconnect = () => {
  if (socket.value) {
    socket.value.close()
  }
  connect()
}

// 返回仪表板
const goBack = () => {
  router.push('/dashboard')
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

// 格式化时间
const formatTime = (timestamp) => {
  return dayjs(timestamp).format('HH:mm')
}

// 组件挂载时连接
onMounted(() => {
  connect()
})

// 组件卸载时关闭连接
onUnmounted(() => {
  if (socket.value) {
    socket.value.close()
  }
})
</script>

<style scoped>
.admin-chat-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  flex-direction: column;
}

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

.header-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.connection-status {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.1);
}

.connection-status.connected {
  background: rgba(72, 187, 120, 0.2);
}

.connection-status.disconnected {
  background: rgba(245, 101, 101, 0.2);
}

.connection-status i {
  font-size: 0.8rem;
}

.back-button {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 0.5rem 1rem;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.back-button:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-1px);
}

.chat-layout {
  flex: 1;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  display: grid;
  grid-template-columns: minmax(0, 3fr) minmax(250px, 1fr);
  height: calc(100vh - 80px);
  padding: 1rem;
  gap: 1rem;
}

.chat-main {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.loading-indicator, .error-message {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 2rem;
  color: #666;
}

.error-message {
  color: #e53e3e;
  flex-direction: column;
  gap: 1rem;
}

.retry-button {
  background: #e53e3e;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
}

.retry-button:hover {
  background: #c53030;
}

.message-item {
  display: flex;
  margin-bottom: 1rem;
}

.message-item.own-message {
  justify-content: flex-end;
}

.message-content {
  max-width: 70%;
  background: #f7fafc;
  border-radius: 12px;
  padding: 0.75rem 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.own-message .message-content {
  background: #667eea;
  color: white;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.25rem;
  font-size: 0.8rem;
  opacity: 0.8;
}

.message-user {
  font-weight: 600;
}

.message-time {
  font-size: 0.75rem;
}

.message-text {
  line-height: 1.4;
  word-wrap: break-word;
}

.input-container {
  padding: 1rem;
  border-top: 1px solid #e2e8f0;
  background: #f8fafc;
}

.input-wrapper {
  display: flex;
  gap: 0.5rem;
  align-items: flex-end;
}

.message-input {
  flex: 1;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 0.75rem;
  font-size: 0.9rem;
  resize: none;
  min-height: 40px;
  max-height: 120px;
  font-family: inherit;
}

.message-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.message-input:disabled {
  background: #f3f4f6;
  color: #9ca3af;
}

.send-button {
  background: #667eea;
  color: white;
  border: none;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  min-width: 48px;
}

.send-button:hover:not(:disabled) {
  background: #5a67d8;
  transform: translateY(-1px);
}

.send-button:disabled {
  background: #cbd5e0;
  cursor: not-allowed;
  transform: none;
}

.chat-sidebar {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  height: fit-content;
}

.online-members h3 {
  margin: 0 0 1rem 0;
  color: #2d3748;
  font-size: 1.1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.online-members h3 i {
  color: #667eea;
}

.members-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem;
  border-radius: 6px;
  background: #f7fafc;
  font-size: 0.9rem;
}

.member-item i {
  color: #48bb78;
  font-size: 1.1rem;
}

.no-members {
  text-align: center;
  color: #a0aec0;
  font-style: italic;
  padding: 1rem;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .chat-layout {
    grid-template-columns: 1fr;
  }
  
  .chat-sidebar {
    display: none;
  }
}

@media (max-width: 768px) {
  .chat-header {
    padding: 1rem;
  }
  
  .header-content {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
  
  .chat-layout {
    padding: 0.5rem;
    height: calc(100vh - 120px);
  }
  
  .message-content {
    max-width: 85%;
  }
}
</style>
