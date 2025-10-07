<template>
  <div class="online-members">
    <div class="members-header">
      <h3>在线成员</h3>
      <span class="member-count">{{ members.length }}人在线</span>
    </div>
    
    <div class="members-list">
      <div 
        v-for="member in members" 
        :key="member" 
        class="member-item"
      >
        <div class="member-avatar">
          <span class="avatar-text">{{ getInitials(member) }}</span>
          <span class="online-indicator"></span>
        </div>
        <div class="member-info">
          <span class="member-name">{{ member }}</span>
          <span class="member-status">在线</span>
        </div>
      </div>
    </div>

    <div v-if="members.length === 0" class="empty-state">
      <i class="fas fa-users"></i>
      <p>暂无其他在线成员</p>
    </div>
  </div>
</template>

<script setup>
import { useMemberStore } from '@/stores/memberStore'
import { storeToRefs } from 'pinia'

const { members } = storeToRefs(useMemberStore())

const getInitials = (email) => {
  return email
    .split('@')[0]
    .split(/[._-]/)
    .map(part => part[0])
    .join('')
    .toUpperCase()
    .slice(0, 2)
}
</script>

<style scoped>
.online-members {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: white;
}

.members-header {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
  background: white;
}

.members-header h3 {
  margin: 0;
  font-size: 1.1rem;
  color: #2d3748;
  font-weight: 600;
}

.member-count {
  font-size: 0.875rem;
  color: #718096;
  padding: 0.25rem 0.75rem;
  background: #f7fafc;
  border-radius: 9999px;
}

.members-list {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  background-color: #f7fafc;
}

.member-item {
  display: flex;
  align-items: center;
  padding: 0.875rem 1rem;
  border-radius: 12px;
  transition: all 0.2s ease;
  margin-bottom: 0.5rem;
  background: white;
  cursor: pointer;
}

.member-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.member-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  background: #4299e1;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 0.875rem;
  position: relative;
}

.avatar-text {
  color: white;
  font-weight: 600;
  font-size: 1rem;
}

.online-indicator {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 12px;
  height: 12px;
  background: #48bb78;
  border: 2px solid white;
  border-radius: 50%;
}

.member-info {
  flex: 1;
  min-width: 0;
}

.member-name {
  display: block;
  font-weight: 500;
  color: #2d3748;
  margin-bottom: 0.25rem;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.member-status {
  font-size: 0.75rem;
  color: #48bb78;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.member-status::before {
  content: '';
  display: inline-block;
  width: 6px;
  height: 6px;
  background: #48bb78;
  border-radius: 50%;
}

/* 大屏幕优化 */
@media (min-width: 1920px) {
  .member-item {
    padding: 1rem 1.25rem;
  }

  .member-avatar {
    width: 48px;
    height: 48px;
  }
}

/* 中等屏幕优化 */
@media (max-width: 1366px) {
  .members-list {
    padding: 0.875rem;
  }

  .member-item {
    padding: 0.75rem;
  }
}

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

.empty-state i {
  font-size: 3rem;
  color: #a0aec0;
  margin-bottom: 1rem;
}

.empty-state p {
  margin: 0;
  color: #718096;
}

@media (max-width: 1024px) {
  .online-members {
    display: none;
  }
}
</style>
  