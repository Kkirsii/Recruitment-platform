<template>
  <div class="job-management-container">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="fas fa-briefcase"></i>
          所有职位信息
        </h1>
        <p class="page-subtitle">管理和维护已发布的招聘职位</p>
      </div>
      <div class="header-actions">
        <button @click="refreshData" class="refresh-button" :disabled="loading">
          <i v-if="loading" class="fas fa-spinner fa-spin"></i>
          <i v-else class="fas fa-sync-alt"></i>
          刷新
        </button>
        <button @click="goToCreateJob" class="create-button">
          <i class="fas fa-plus"></i>
          创建职位
        </button>
        <button @click="goBack" class="back-button">
          <i class="fas fa-arrow-left"></i>
          返回
        </button>
      </div>
    </div>

    <div class="stats-overview">
      <div class="stat-card">
        <div class="stat-icon">
          <i class="fas fa-briefcase"></i>
        </div>
        <div class="stat-content">
          <h3>总职位数</h3>
          <p class="stat-number">{{ stats.total }}</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon">
          <i class="fas fa-users"></i>
        </div>
        <div class="stat-content">
          <h3>总招聘人数</h3>
          <p class="stat-number">{{ stats.totalPositions }}</p>
        </div>
      </div>
    </div>

    <div class="filters-section">
      <div class="filter-group">
        <label for="searchInput">搜索职位:</label>
        <input
          id="searchInput"
          type="text"
          v-model="searchKeyword"
          placeholder="搜索职位名称、地点、薪资..."
          @input="filterJobs"
        />
      </div>
    </div>

    <div class="table-container">
      <div v-if="loading" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i>
        <span>加载中...</span>
      </div>

      <div v-else-if="filteredJobs.length === 0" class="empty-state">
        <i class="fas fa-search"></i>
        <h3>暂无职位信息</h3>
        <p>当前没有符合筛选条件的职位记录</p>
      </div>

      <table v-else class="jobs-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>职位名称</th>
            <th>薪资待遇</th>
            <th>招聘人数</th>
            <th>工作地点</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="job in paginatedJobs" :key="job.id" class="job-row">
            <td>{{ job.id }}</td>
            <td>
              <div class="job-name">{{ job.name }}</div>
              <div class="job-demand">{{ truncateText(job.demand, 50) }}</div>
            </td>
            <td class="salary">{{ job.salary }}</td>
            <td class="number">{{ job.number }}</td>
            <td class="location">{{ job.location || '未设置' }}</td>
            <td class="actions">
              <button
                @click="viewJobDetails(job)"
                class="action-button view"
                title="查看详情"
              >
                <i class="fas fa-eye"></i>
              </button>
              <button
                @click="editJob(job)"
                class="action-button edit"
                title="编辑职位"
              >
                <i class="fas fa-edit"></i>
              </button>
              <button
                @click="deleteJob(job.id)"
                class="action-button delete"
                title="删除职位"
              >
                <i class="fas fa-trash"></i>
              </button>
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

    <!-- 职位详情模态框 -->
    <div v-if="showJobDetails" class="modal-overlay" @click="closeJobDetails">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ selectedJob?.name }}</h3>
          <button @click="closeJobDetails" class="close-button">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body">
          <div class="job-detail-section">
            <h4>基本信息</h4>
            <div class="detail-grid">
              <div class="detail-item">
                <label>职位ID:</label>
                <span>{{ selectedJob?.id }}</span>
              </div>
              <div class="detail-item">
                <label>薪资待遇:</label>
                <span>{{ selectedJob?.salary }}</span>
              </div>
              <div class="detail-item">
                <label>招聘人数:</label>
                <span>{{ selectedJob?.number }}</span>
              </div>
              <div class="detail-item">
                <label>工作地点:</label>
                <span>{{ selectedJob?.location || '未设置' }}</span>
              </div>
            </div>
          </div>

          <div class="job-detail-section">
            <h4>岗位要求</h4>
            <div class="detail-content">
              {{ selectedJob?.demand }}
            </div>
          </div>

          <div class="job-detail-section">
            <h4>职位描述</h4>
            <div class="detail-content">
              {{ selectedJob?.jobDescription || '未填写' }}
            </div>
          </div>

          <div class="job-detail-section">
            <h4>福利待遇</h4>
            <div class="detail-content">
              {{ selectedJob?.welfare || '未填写' }}
            </div>
          </div>
        </div>
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
const jobs = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

// 统计数据和模态框
const stats = ref({
  total: 0,
  totalPositions: 0
})

const showJobDetails = ref(false)
const selectedJob = ref(null)

// 计算属性
const filteredJobs = computed(() => {
  let filtered = jobs.value

  // 搜索筛选
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(job => 
      job.name.toLowerCase().includes(keyword) ||
      (job.location && job.location.toLowerCase().includes(keyword)) ||
      (job.salary && job.salary.toLowerCase().includes(keyword)) ||
      (job.demand && job.demand.toLowerCase().includes(keyword))
    )
  }

  return filtered
})

const paginatedJobs = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredJobs.value.slice(start, end)
})

const totalPages = computed(() => {
  return Math.ceil(filteredJobs.value.length / pageSize.value)
})

// 方法
const fetchJobs = async () => {
  loading.value = true
  try {
    const response = await axios.get('http://localhost:8080/admin/job/getlist', {
      params: { page: 1, size: 1000 },
      headers: { Authorization: adminStore.token }
    })
    
    jobs.value = response.data.data
    updateStats()
  } catch (error) {
    console.error('获取职位信息失败:', error)
    if (error.response?.status === 401) {
      adminStore.logout()
      router.push('/login')
    }
  } finally {
    loading.value = false
  }
}

const updateStats = () => {
  stats.value.total = jobs.value.length
  stats.value.totalPositions = jobs.value.reduce((sum, job) => {
    const number = parseInt(job.number) || 0
    return sum + number
  }, 0)
}

const filterJobs = () => {
  currentPage.value = 1 // 重置到第一页
}

const viewJobDetails = (job) => {
  selectedJob.value = job
  showJobDetails.value = true
}

const closeJobDetails = () => {
  showJobDetails.value = false
  selectedJob.value = null
}

const editJob = (job) => {
  // 将职位数据存储到localStorage中，用于编辑页面初始化
  localStorage.setItem('editingJob', JSON.stringify(job))
  router.push(`/jobs/edit/${job.id}`)
}

const deleteJob = async (jobId) => {
  if (!confirm('确定要删除这个职位吗？删除后无法恢复。')) {
    return
  }

  try {
    await axios.delete('http://localhost:8080/admin/job/publish', {
      params: { id: jobId },
      headers: { Authorization: adminStore.token }
    })
    
    // 从本地数据中删除
    jobs.value = jobs.value.filter(job => job.id !== jobId)
    updateStats()
    
    alert('职位删除成功')
  } catch (error) {
    console.error('删除职位失败:', error)
    alert('删除失败，请稍后重试')
  }
}

const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

const refreshData = async () => {
  await fetchJobs()
}

const goToCreateJob = () => {
  router.push('/job-publish')
}

const goBack = () => {
  router.push('/dashboard')
}

const truncateText = (text, maxLength) => {
  if (!text) return ''
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

// 生命周期
onMounted(() => {
  if (!adminStore.isLoggedIn) {
    router.push('/login')
    return
  }
  fetchJobs()
})
</script>

<style scoped>
.job-management-container {
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

.create-button, .refresh-button, .back-button {
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

.create-button {
  background: #48dbfb;
  color: white;
}

.create-button:hover {
  background: #0abde3;
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
  background: linear-gradient(135deg, #667eea, #764ba2);
}

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
  flex: 1;
}

.filter-group label {
  font-weight: 600;
  color: #4a5568;
}

.filter-group input {
  padding: 0.75rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.9rem;
}

.filter-group input:focus {
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

.jobs-table {
  width: 100%;
  border-collapse: collapse;
}

.jobs-table th {
  background: #f7fafc;
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  color: #4a5568;
  border-bottom: 2px solid #e2e8f0;
}

.jobs-table td {
  padding: 1rem;
  border-bottom: 1px solid #e2e8f0;
}

.job-row:hover {
  background: #f7fafc;
}

.job-name {
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 0.25rem;
}

.job-demand {
  font-size: 0.8rem;
  color: #718096;
  line-height: 1.4;
}

.salary {
  color: #38a169;
  font-weight: 600;
}

.number {
  text-align: center;
  font-weight: 600;
}

.location {
  color: #4a5568;
}

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

.action-button.view {
  background: #f093fb;
  color: white;
}

.action-button.view:hover {
  background: #f5576c;
}

.action-button.edit {
  background: #43e97b;
  color: white;
}

.action-button.edit:hover {
  background: #38f9d7;
}

.action-button.delete {
  background: #ff6b6b;
  color: white;
}

.action-button.delete:hover {
  background: #ee5a24;
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

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 20px;
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 2rem;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h3 {
  margin: 0;
  color: #2d3748;
  font-size: 1.25rem;
}

.close-button {
  width: 32px;
  height: 32px;
  border: none;
  background: #e2e8f0;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #4a5568;
}

.close-button:hover {
  background: #cbd5e0;
}

.modal-body {
  padding: 2rem;
}

.job-detail-section {
  margin-bottom: 1.5rem;
}

.job-detail-section h4 {
  margin: 0 0 1rem 0;
  color: #2d3748;
  font-size: 1rem;
  font-weight: 600;
  border-bottom: 2px solid #e2e8f0;
  padding-bottom: 0.5rem;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.detail-item label {
  font-weight: 600;
  color: #4a5568;
  font-size: 0.9rem;
}

.detail-item span {
  color: #2d3748;
}

.detail-content {
  color: #4a5568;
  line-height: 1.6;
  padding: 0.75rem;
  background: #f7fafc;
  border-radius: 8px;
}

@media (max-width: 768px) {
  .job-management-container {
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
  
  .jobs-table {
    font-size: 0.8rem;
  }
  
  .jobs-table th,
  .jobs-table td {
    padding: 0.5rem;
  }
  
  .actions {
    flex-direction: column;
    gap: 0.25rem;
  }
  
  .action-button {
    width: 28px;
    height: 28px;
  }
  
  .modal-content {
    margin: 1rem;
    max-width: calc(100vw - 2rem);
  }
}
</style>
