<template>
  <div class="application-management-container">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="fas fa-clipboard-list"></i>
          所有投递信息
        </h1>
        <p class="page-subtitle">管理和审核求职者的职位申请</p>
      </div>
      <div class="header-actions">
        <button @click="refreshData" class="refresh-button" :disabled="loading">
          <i v-if="loading" class="fas fa-spinner fa-spin"></i>
          <i v-else class="fas fa-sync-alt"></i>
          刷新
        </button>
        <button @click="goBack" class="back-button">
          <i class="fas fa-arrow-left"></i>
          返回
        </button>
      </div>
    </div>

    <div class="stats-overview">
      <div class="stat-card">
        <div class="stat-icon pending">
          <i class="fas fa-clock"></i>
        </div>
        <div class="stat-content">
          <h3>待审核</h3>
          <p class="stat-number">{{ stats.pending }}</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon approved">
          <i class="fas fa-check-circle"></i>
        </div>
        <div class="stat-content">
          <h3>已通过</h3>
          <p class="stat-number">{{ stats.approved }}</p>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon total">
          <i class="fas fa-users"></i>
        </div>
        <div class="stat-content">
          <h3>总申请</h3>
          <p class="stat-number">{{ stats.total }}</p>
        </div>
      </div>
    </div>

    <div class="filters-section">
      <div class="filter-group">
        <label for="stateFilter">状态筛选:</label>
        <select id="stateFilter" v-model="selectedState" @change="filterApplications">
          <option value="all">全部</option>
          <option value="0">待审核</option>
          <option value="1">已通过</option>
        </select>
      </div>
      
      <div class="filter-group">
        <label for="searchInput">搜索:</label>
        <input
          id="searchInput"
          type="text"
          v-model="searchKeyword"
          placeholder="搜索职位名称或邮箱..."
          @input="filterApplications"
        />
      </div>
    </div>

    <div class="table-container">
      <div v-if="loading" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i>
        <span>加载中...</span>
      </div>

      <div v-else-if="filteredApplications.length === 0" class="empty-state">
        <i class="fas fa-inbox"></i>
        <h3>暂无投递信息</h3>
        <p>当前没有符合筛选条件的投递记录</p>
      </div>

      <table v-else class="applications-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>职位名称</th>
            <th>申请人邮箱</th>
            <th>申请状态</th>
            <th>申请时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="application in paginatedApplications" :key="application.id" class="application-row">
            <td>{{ application.id }}</td>
            <td>{{ application.jobName }}</td>
            <td>{{ application.email }}</td>
            <td>
              <span class="status-badge" :class="getStatusClass(application.jobState)">
                {{ getStatusText(application.jobState) }}
              </span>
            </td>
            <td>{{ formatDate(application.submitTime) }}</td>
            <td class="actions">
              <button
                v-if="application.jobState === 0"
                @click="approveApplication(application.id)"
                class="action-button approve"
                title="通过申请"
              >
                <i class="fas fa-check"></i>
              </button>
              <button
                v-if="application.jobState === 0"
                @click="rejectApplication(application.id)"
                class="action-button reject"
                title="拒绝申请"
              >
                <i class="fas fa-times"></i>
              </button>
              <span v-if="application.jobState === 1" class="approved-text">已处理</span>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 分页 -->
      <div v-if="totalPages > 1" class="pagination">
        <button
          @click="goToPage(currentPage - 1)"
          :disabled="currentPage === 1"
          class="page-button"
        >
          <i class="fas fa-chevron-left"></i>
        </button>
        
        <span class="page-info">
          第 {{ currentPage }} 页，共 {{ totalPages }} 页
        </span>
        
        <button
          @click="goToPage(currentPage + 1)"
          :disabled="currentPage === totalPages"
          class="page-button"
        >
          <i class="fas fa-chevron-right"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAdminStore } from '@/stores/admin'
import axios from 'axios'

const router = useRouter()
const adminStore = useAdminStore()

// 响应式数据
const applications = ref([])
const loading = ref(false)
const selectedState = ref('all')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

// 统计数据
const stats = ref({
  total: 0,
  pending: 0,
  approved: 0
})

// 计算属性
const filteredApplications = computed(() => {
  let filtered = applications.value

  // 状态筛选
  if (selectedState.value !== 'all') {
    filtered = filtered.filter(app => app.jobState.toString() === selectedState.value)
  }

  // 搜索筛选
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(app => 
      app.jobName.toLowerCase().includes(keyword) ||
      app.email.toLowerCase().includes(keyword)
    )
  }

  return filtered
})

const paginatedApplications = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredApplications.value.slice(start, end)
})

const totalPages = computed(() => {
  return Math.ceil(filteredApplications.value.length / pageSize.value)
})

// 方法
const fetchApplications = async () => {
  loading.value = true
  try {
    const response = await axios.get('http://localhost:8080/admin/job/submitlist', {
      params: { page: 1, size: 1000 },
      headers: { Authorization: adminStore.token }
    })
    
    applications.value = response.data.data
    updateStats()
  } catch (error) {
    console.error('获取投递信息失败:', error)
    if (error.response?.status === 401) {
      adminStore.logout()
      router.push('/login')
    }
  } finally {
    loading.value = false
  }
}

const updateStats = () => {
  stats.value.total = applications.value.length
  stats.value.pending = applications.value.filter(app => app.jobState === 0).length
  stats.value.approved = applications.value.filter(app => app.jobState === 1).length
}

const filterApplications = () => {
  currentPage.value = 1 // 重置到第一页
}

const approveApplication = async (id) => {
  try {
    await axios.put('http://localhost:8080/admin/job/submitlist', null, {
      params: { id, state: 1 },
      headers: { Authorization: adminStore.token }
    })
    
    // 更新本地数据
    const application = applications.value.find(app => app.id === id)
    if (application) {
      application.jobState = 1
      updateStats()
    }
    
    alert('申请已通过')
  } catch (error) {
    console.error('通过申请失败:', error)
    alert('操作失败，请稍后重试')
  }
}

const rejectApplication = async (id) => {
  try {
    await axios.put('http://localhost:8080/admin/job/submitlist', null, {
      params: { id, state: -1 },
      headers: { Authorization: adminStore.token }
    })
    
    // 更新本地数据
    const application = applications.value.find(app => app.id === id)
    if (application) {
      application.jobState = -1
      updateStats()
    }
    
    alert('申请已拒绝')
  } catch (error) {
    console.error('拒绝申请失败:', error)
    alert('操作失败，请稍后重试')
  }
}

const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

const refreshData = async () => {
  await fetchApplications()
}

const goBack = () => {
  router.push('/dashboard')
}

const getStatusClass = (state) => {
  switch (state) {
    case 0: return 'status-pending'
    case 1: return 'status-approved'
    case -1: return 'status-rejected'
    default: return 'status-unknown'
  }
}

const getStatusText = (state) => {
  switch (state) {
    case 0: return '待审核'
    case 1: return '已通过'
    case -1: return '已拒绝'
    default: return '未知状态'
  }
}

const formatDate = (timestamp) => {
  if (!timestamp) return '未知'
  return new Date(parseInt(timestamp)).toLocaleDateString('zh-CN')
}

// 生命周期
onMounted(() => {
  if (!adminStore.isLoggedIn) {
    router.push('/login')
    return
  }
  fetchApplications()
})
</script>

<style scoped>
.application-management-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 2rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  background: rgba(255, 255, 255, 0.95);
  padding: 2rem;
  border-radius: 20px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.header-content .page-title {
  font-size: 2rem;
  font-weight: 700;
  color: #2d3748;
  margin: 0 0 0.5rem 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.header-content .page-title i {
  color: #667eea;
}

.page-subtitle {
  color: #718096;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 1rem;
}

.refresh-button, .back-button {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.refresh-button {
  background: #667eea;
  color: white;
}

.refresh-button:hover:not(:disabled) {
  background: #5a67d8;
}

.back-button {
  background: #e2e8f0;
  color: #4a5568;
}

.back-button:hover {
  background: #cbd5e0;
}

.refresh-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 1rem;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  color: white;
}

.stat-icon.pending { background: linear-gradient(135deg, #ff6b6b, #ee5a24); }
.stat-icon.approved { background: linear-gradient(135deg, #48dbfb, #0abde3); }
.stat-icon.total { background: linear-gradient(135deg, #667eea, #764ba2); }

.stat-content h3 {
  margin: 0 0 0.25rem 0;
  color: #4a5568;
  font-size: 0.9rem;
}

.stat-number {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: #2d3748;
}

.filters-section {
  display: flex;
  gap: 2rem;
  margin-bottom: 2rem;
  background: rgba(255, 255, 255, 0.95);
  padding: 1.5rem;
  border-radius: 16px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.filter-group label {
  font-weight: 600;
  color: #4a5568;
}

.filter-group select, .filter-group input {
  padding: 0.75rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.9rem;
}

.filter-group select:focus, .filter-group input:focus {
 border-color: #667eea;
  outline: none;
}

.table-container {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.loading-state, .empty-state {
  padding: 4rem;
  text-align: center;
  color: #718096;
}

.loading-state i, .empty-state i {
  font-size: 3rem;
  margin-bottom: 1rem;
  color: #cbd5e0;
}

.empty-state h3 {
  margin: 0 0 0.5rem 0;
  color: #4a5568;
}

.applications-table {
  width: 100%;
  border-collapse: collapse;
}

.applications-table th {
  background: #f7fafc;
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  color: #4a5568;
  border-bottom: 2px solid #e2e8f0;
}

.applications-table td {
  padding: 1rem;
  border-bottom: 1px solid #e2e8f0;
}

.application-row:hover {
  background: #f7fafc;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
}

.status-pending { background: #fff5f5; color: #c53030; }
.status-approved { background: #f0fff4; color: #38a169; }
.status-rejected { background: #fed7d7; color: #e53e3e; }

.actions {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.action-button {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.action-button.approve {
  background: #48dbfb;
  color: white;
}

.action-button.approve:hover {
  background: #0abde3;
}

.action-button.reject {
  background: #ff6b6b;
  color: white;
}

.action-button.reject:hover {
  background: #e53e3e;
}

.approved-text {
  color: #38a169;
  font-weight: 600;
  font-size: 0.9rem;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  padding: 2rem;
  background: #f7fafc;
}

.page-button {
  width: 40px;
  height: 40px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #667eea;
  color: white;
  transition: all 0.3s ease;
}

.page-button:hover:not(:disabled) {
  background: #5a67d8;
}

.page-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #4a5568;
  font-weight: 600;
}

@media (max-width: 768px) {
  .application-management-container {
    padding: 1rem;
  }
  
  .page-header {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
  
  .header-actions {
    width: 100%;
    justify-content: center;
  }
  
  .stats-overview {
    grid-template-columns: 1fr;
  }
  
  .filters-section {
    flex-direction: column;
    gap: 1rem;
  }
  
  .applications-table {
    font-size: 0.8rem;
  }
  
  .applications-table th,
  .applications-table td {
    padding: 0.5rem;
  }
}
</style>
