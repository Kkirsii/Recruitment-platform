<template>
    <div class="chat-container">
        <div class="chat-header">
            <h2>聊天室</h2>
            <div class="connection-status" :class="{ connected: isConnected }">
                {{ isConnected ? '已连接' : '未连接' }}
            </div>
        </div>

        <div class="chat-messages" ref="messagesBox">
            <div v-if="loading" class="loading-state">
                <div class="loading-spinner"></div>
                <p>连接中...</p>
            </div>

            <div v-else-if="error" class="error-state">
                <i class="fas fa-exclamation-circle"></i>
                <p>{{ error }}</p>
                <button @click="reconnect" class="retry-button">重新连接</button>
            </div>

            <template v-else>
                <div class="message-group" v-for="(group, date) in messageGroups" :key="date">
                    <div class="date-divider">{{ date }}</div>
                    <div 
                        class="message" 
                        v-for="(msg, index) in group" 
                        :key="index" 
                        :class="{ 
                            self: msg.self,
                            'first-in-group': isFirstInGroup(msg, group, index),
                            'last-in-group': isLastInGroup(msg, group, index)
                        }"
                    >
                        <div class="message-content">
                            <div class="message-header" v-if="isFirstInGroup(msg, group, index)">
                                <span class="user">{{ msg.user }}</span>
                                <span class="time">{{ formatTime(msg.timestamp) }}</span>
                            </div>
                            <div class="text">{{ msg.text }}</div>
                        </div>
                    </div>
                </div>

                <div v-if="messages.length === 0" class="empty-state">
                    <i class="fas fa-comments"></i>
                    <p>暂无消息，开始聊天吧！</p>
                </div>
            </template>
        </div>

        <div class="chat-input">
            <div class="input-wrapper">
                <textarea 
                    v-model="input" 
                    @keydown.enter.prevent="sendMessage"
                    placeholder="输入消息..."
                    rows="1"
                    ref="inputArea"
                    @input="autoResize"
                ></textarea>
                <button 
                    @click="sendMessage" 
                    class="send-button"
                    :disabled="!input.trim() || !isConnected"
                >
                    <i class="fas fa-paper-plane"></i>
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { useMemberStore } from '@/stores/memberStore'
import dayjs from 'dayjs'

const memberStore = useMemberStore()
const messages = ref([])
const input = ref('')
const socket = ref(null)
const messagesBox = ref(null)
const inputArea = ref(null)
const isConnected = ref(false)
const loading = ref(true)
const error = ref('')

const username = localStorage.getItem("Email") // 实际使用中可换成用户昵称
const token = localStorage.getItem("Authorization")

// 按日期分组消息
const messageGroups = computed(() => {
    const groups = {}
    messages.value.forEach(msg => {
        const date = dayjs(msg.timestamp).format('YYYY-MM-DD')
        if (!groups[date]) {
            groups[date] = []
        }
        groups[date].push(msg)
    })
    return groups
})

const formatTime = (timestamp) => {
    return dayjs(timestamp).format('HH:mm')
}

const isFirstInGroup = (msg, group, index) => {
    if (index === 0) return true
    const prevMsg = group[index - 1]
    return prevMsg.user !== msg.user || 
           dayjs(msg.timestamp).diff(dayjs(prevMsg.timestamp), 'minute') > 2
}

const isLastInGroup = (msg, group, index) => {
    if (index === group.length - 1) return true
    const nextMsg = group[index + 1]
    return nextMsg.user !== msg.user || 
           dayjs(nextMsg.timestamp).diff(dayjs(msg.timestamp), 'minute') > 2
}

const scrollToBottom = () => {
    nextTick(() => {
        if (messagesBox.value) {
            messagesBox.value.scrollTop = messagesBox.value.scrollHeight
        }
    })
}

const autoResize = () => {
    if (!inputArea.value) return
    inputArea.value.style.height = 'auto'
    inputArea.value.style.height = inputArea.value.scrollHeight + 'px'
}

const sendMessage = () => {
    if (!input.value.trim() || !isConnected.value) return
    const text = input.value
    socket.value.send(text)
    messages.value.push({ 
        user: username, 
        text, 
        self: true,
        timestamp: new Date().toISOString()
    })
    input.value = ''
    if (inputArea.value) {
        inputArea.value.style.height = 'auto'
    }
    scrollToBottom()
}

const connect = () => {
    loading.value = true
    error.value = ''
    
    const ws = new WebSocket(`ws://localhost:8080/chat?token=${encodeURIComponent(token)}`)
    socket.value = ws

    ws.onmessage = (event) => {
        try {
            const msg = JSON.parse(event.data)
            if (msg.type === "msg") {
                let email = localStorage.getItem("Email")
                if (msg.email === email) return
                messages.value.push({ 
                    user: msg.email, 
                    text: msg.msg, 
                    self: false,
                    timestamp: new Date().toISOString()
                })
                scrollToBottom()
            }
            if (msg.type === 'members') {
                memberStore.setMembers(msg.members)
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

const reconnect = () => {
    if (socket.value) {
        socket.value.close()
    }
    connect()
}

onMounted(() => {
    connect()
})
</script>

<style scoped>
.chat-container {
    height: 100%;
    display: flex;
    flex-direction: column;
    background: white;
}

.chat-header {
    padding: 1rem 1.5rem;
    border-bottom: 1px solid #e2e8f0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-shrink: 0;
    background: white;
}

.chat-header h2 {
    margin: 0;
    font-size: 1.25rem;
    color: #2d3748;
}

.connection-status {
    font-size: 0.875rem;
    padding: 0.25rem 0.75rem;
    border-radius: 9999px;
    background: #fed7d7;
    color: #c53030;
}

.connection-status.connected {
    background: #c6f6d5;
    color: #2f855a;
}

.chat-messages {
    flex: 1;
    overflow-y: auto;
    padding: 1.5rem 2rem;
    background-color: #f7fafc;
    display: flex;
    flex-direction: column;
}

.message-group {
    margin-bottom: 1.5rem;
    max-width: 1200px;
    margin-left: auto;
    margin-right: auto;
    width: 100%;
}

.date-divider {
    text-align: center;
    margin: 1.5rem 0;
    position: relative;
    color: #718096;
    font-size: 0.875rem;
}

.date-divider::before,
.date-divider::after {
    content: '';
    position: absolute;
    top: 50%;
    width: 45%;
    height: 1px;
    background: #e2e8f0;
}

.date-divider::before {
    left: 0;
}

.date-divider::after {
    right: 0;
}

.message {
    display: flex;
    margin-bottom: 0.25rem;
    padding: 0 1rem;
    width: 100%;
}

.message.self {
    justify-content: flex-end;
}

.message-content {
    max-width: 85%;
    min-width: 200px;
}

.message-header {
    margin-bottom: 0.25rem;
    font-size: 0.875rem;
}

.message.self .message-header {
    text-align: right;
}

.user {
    font-weight: 500;
    color: #4a5568;
}

.time {
    margin-left: 0.5rem;
    color: #a0aec0;
}

.text {
    padding: 0.875rem 1.25rem;
    border-radius: 12px;
    background: white;
    color: #2d3748;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
    line-height: 1.6;
    font-size: 1rem;
    word-break: break-word;
}

.message.self .text {
    background: #4299e1;
    color: white;
}

.message.first-in-group {
    margin-top: 1rem;
}

.message.last-in-group {
    margin-bottom: 1rem;
}

.chat-input {
    padding: 1rem 1.5rem;
    background: white;
    border-top: 1px solid #e2e8f0;
    flex-shrink: 0;
}

.input-wrapper {
    display: flex;
    gap: 1rem;
    align-items: flex-end;
    max-width: 1200px;
    margin: 0 auto;
    width: 100%;
    padding: 0 1rem;
}

textarea {
    flex: 1;
    padding: 0.875rem 1rem;
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    font-size: 1rem;
    resize: none;
    max-height: 120px;
    min-height: 42px;
    line-height: 1.5;
    transition: all 0.3s ease;
    box-sizing: border-box;
}

textarea:focus {
    outline: none;
    border-color: #4299e1;
    box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.15);
}

.send-button {
    padding: 0.875rem 1.25rem;
    height: 42px;
    width: 42px;
    display: flex;
    align-items: center;
    justify-content: center;
}

.send-button:disabled {
    background: #a0aec0;
    cursor: not-allowed;
}

.loading-state,
.error-state,
.empty-state {
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #718096;
    text-align: center;
    padding: 2rem;
}

.loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid rgba(66, 153, 225, 0.3);
    border-radius: 50%;
    border-top-color: #4299e1;
    animation: spin 1s linear infinite;
    margin-bottom: 1rem;
}

.error-state i,
.empty-state i {
    font-size: 3rem;
    color: #a0aec0;
    margin-bottom: 1rem;
}

.retry-button {
    margin-top: 1rem;
    padding: 0.5rem 1rem;
    background: #4299e1;
    color: white;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s ease;
}

.retry-button:hover {
    background: #3182ce;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

/* 大屏幕优化 */
@media (min-width: 1920px) {
    .message-group,
    .input-wrapper {
        max-width: 1400px;
    }

    .message-content {
        max-width: 90%;
    }
}

/* 中等屏幕优化 */
@media (max-width: 1366px) {
    .message-group,
    .input-wrapper {
        max-width: 1000px;
    }

    .message-content {
        max-width: 80%;
    }
}

/* 保持原有的移动端适配 */
@media (max-width: 640px) {
    .chat-header {
        padding: 0.75rem 1rem;
    }

    .chat-messages {
        padding: 1rem;
    }

    .message {
        padding: 0;
    }

    .chat-input {
        padding: 0.75rem 1rem;
    }

    .message-content {
        max-width: 90%;
    }

    .input-wrapper {
        gap: 0.75rem;
    }

    textarea {
        padding: 0.75rem;
        font-size: 0.95rem;
    }

    .send-button {
        padding: 0.75rem;
    }
}
</style>