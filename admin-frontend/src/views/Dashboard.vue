<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h1 class="dashboard-title">管理仪表板</h1>
      <div class="user-info">
        <span>欢迎，{{ adminStore.adminInfo?.email }}</span>
        <button @click="handleLogout" class="logout-button">
          <i class="fas fa-sign-out-alt"></i>
          退出登录
        </button>
      </div>
    </div>

    <div class="dashboard-content">
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-briefcase"></i>
          </div>
          <div class="stat-content">
            <h3>总职位数</h3>
            <p class="stat-number">{{ stats.totalJobs }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-users"></i>
          </div>
          <div class="stat-content">
            <h3>总投递数</h3>
            <p class="stat-number">{{ stats.totalApplications }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-user-check"></i>
          </div>
          <div class="stat-content">
            <h3>录用数</h3>
            <p class="stat-number">{{ stats.hiredApplications }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">
            <i class="fas fa-clock"></i>
          </div>
          <div class="stat-content">
            <h3>进行中</h3>
            <p class="stat-number">{{ stats.pendingApplications }}</p>
          </div>
        </div>
      </div>

      <div class="action-cards">
        <div class="action-card" @click="navigateToJobPublish">
          <div class="action-icon">
            <i class="fas fa-plus-circle"></i>
          </div>
          <div class="action-content">
            <h3>发布新职位</h3>
            <p>创建并发布新的招聘信息</p>
          </div>
          <i class="fas fa-chevron-right action-arrow"></i>
        </div>

        <div class="action-card" @click="navigateToApplications">
          <div class="action-icon">
            <i class="fas fa-clipboard-list"></i>
          </div>
          <div class="action-content">
            <h3>管理投递</h3>
            <p>查看和审核职位申请</p>
          </div>
          <i class="fas fa-chevron-right action-arrow"></i>
        </div>

        <div class="action-card" @click="navigateToJobs">
          <div class="action-icon">
            <i class="fas fa-edit"></i>
          </div>
          <div class="action-content">
            <h3>管理职位</h3>
            <p>编辑和删除已发布的职位</p>
          </div>
          <i class="fas fa-chevron-right action-arrow"></i>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAdminStore } from '@/stores/admin'
import axios from 'axios'

const router = useRouter()
const adminStore = useAdminStore()

const stats = ref({
  totalJobs: 0,
  totalApplications: 0,
  hiredApplications: 0,
  pendingApplications: 0
})

const fetchStats = async () => {
  try {
    // 获取职位统计
    const jobsResponse = await axios.get('http://localhost:8080/admin/job/getlist', {
      params: { page: 1, size: 1000 },
      headers: { Authorization: adminStore.token }
    })
    stats.value.totalJobs = jobsResponse.data.data.length

    // 获取投递统计
    const applicationsResponse = await axios.get('http://localhost:8080/admin/job/submitlist', {
      params: { page: 1, size: 1000 },
      headers: { Authorization: adminStore.token }
    })
    
    const applications = applicationsResponse.data.data
    stats.value.totalApplications = applications.length
    stats.value.hiredApplications = applications.filter(app => app.jobState === 6).length
    stats.value.pendingApplications = applications.filter(app => app.jobState === 0 || app.jobState === 1 || app.jobState === 2 || app.jobState === 3).length
  } catch (error) {
    console.error('获取统计数据失败:', error)
    if (error.response?.status === 401) {
      adminStore.logout()
      router.push('/login')
    }
  }
}

const handleLogout = () => {
  adminStore.logout()
  router.push('/login')
}

const navigateToJobPublish = () => {
  router.push('/job-publish')
}

const navigateToApplications = () => {
  router.push('/applications')
}

const navigateToJobs = () => {
  router.push('/jobs')
}

onMounted(() => {
  if (!adminStore.isLoggedIn) {
    router.push('/login')
    return
  }
  fetchStats()
})
</script>

<style scoped>
.dashboard-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 2rem;
  position: relative;
  overflow: hidden;
}

.dashboard-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="dots" width="20" height="20" patternUnits="userSpaceOnUse"><circle cx="10" cy="10" r="1" fill="rgba(102,126,234,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23dots)"/></svg>');
  opacity: 0.4;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 3rem;
  background: rgba(255, 255, 255, 0.95);
  padding: 2rem 2.5rem;
  border-radius: 20px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 1;
}

.dashboard-title {
  font-size: 2rem;
  font-weight: 700;
  color: #2d3748;
  margin: 0;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-info span {
  color: #4a5568;
  font-weight: 500;
}

.logout-button {
  background: #e53e3e;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.logout-button:hover {
  background: #c53030;
  transform: translateY(-1px);
}

.dashboard-content {
  max-width: 1400px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  gap: 1.5rem;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 70px;
  height: 70px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.8rem;
  color: white;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.stat-card:nth-child(1) .stat-icon {
  background: linear-gradient(135deg, #667eea, #764ba2);
}

.stat-card:nth-child(2) .stat-icon {
  background: linear-gradient(135deg, #f093fb, #f5576c);
}

.stat-card:nth-child(3) .stat-icon {
  background: linear-gradient(135deg, #4facfe, #00f2fe);
}

.stat-card:nth-child(4) .stat-icon {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
}

.stat-content h3 {
  margin: 0 0 0.5rem 0;
  color: #4a5568;
  font-size: 0.9rem;
  font-weight: 500;
}

.stat-number {
  margin: 0;
  font-size: 2rem;
  font-weight: 700;
  color: #2d3748;
}

.action-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 2rem;
}

.action-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 2.5rem;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  position: relative;
  overflow: hidden;
}

.action-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.action-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, #667eea, #764ba2);
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.action-card:hover::before {
  transform: scaleX(1);
}

.action-icon {
  width: 70px;
  height: 70px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.8rem;
  color: white;
  background: linear-gradient(135deg, #667eea, #764ba2);
  box-shadow: 0 8px 16px rgba(102, 126, 234, 0.3);
}

.action-content {
  flex: 1;
}

.action-content h3 {
  margin: 0 0 0.5rem 0;
  color: #2d3748;
  font-size: 1.25rem;
  font-weight: 600;
}

.action-content p {
  margin: 0;
  color: #718096;
  font-size: 0.9rem;
}

.action-arrow {
  color: #a0aec0;
  font-size: 1.2rem;
  transition: transform 0.3s ease;
}

.action-card:hover .action-arrow {
  transform: translateX(4px);
}

/* 16:9 屏幕优化 */
@media (min-width: 1920px) {
  .dashboard-container {
    padding: 3rem;
  }
  
  .dashboard-content {
    max-width: 1600px;
  }
  
  .stats-grid {
    grid-template-columns: repeat(4, 1fr);
    gap: 2.5rem;
  }
  
  .action-cards {
    grid-template-columns: repeat(3, 1fr);
    gap: 2.5rem;
  }
  
  .stat-card {
    padding: 2.5rem;
  }
  
  .action-card {
    padding: 3rem;
  }
}

@media (min-width: 1366px) and (max-width: 1919px) {
  .dashboard-container {
    padding: 2.5rem;
  }
  
  .dashboard-content {
    max-width: 1500px;
  }
  
  .stats-grid {
    grid-template-columns: repeat(4, 1fr);
    gap: 2rem;
  }
  
  .action-cards {
    grid-template-columns: repeat(3, 1fr);
    gap: 2rem;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 1rem;
  }
  
  .dashboard-header {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .action-cards {
    grid-template-columns: 1fr;
  }
}
</style>
