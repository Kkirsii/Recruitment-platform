<template>
  <div class="jobs-container">
    <div class="jobs-header">
      <h1 class="page-title">招聘信息</h1>
      <div class="search-bar">
        <i class="fas fa-search search-icon"></i>
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="搜索职位..."
          @input="filterJobs"
        >
      </div>
    </div>

    <div class="content-wrapper">
      <div class="jobs-list-container">
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>
        
        <div v-else-if="error" class="error-state">
          <i class="fas fa-exclamation-circle"></i>
          <p>{{ error }}</p>
          <button @click="fetchJobs" class="retry-button">重试</button>
        </div>

        <ul v-else class="job-list">
          <li
            v-for="job in filteredJobs"
            :key="job.id"
            class="job-item"
            :class="{ 'selected': selectedJob?.id === job.id }"
            @click="selectJob(job)"
          >
            <div class="job-item-content">
              <div class="job-main-info">
                <h3 class="job-name">{{ job.name }}</h3>
                <span class="job-salary">{{ job.salary }}</span>
              </div>
              <div class="job-sub-info">
                <span class="job-tag">
                  <i class="fas fa-users"></i>
                  招聘人数：{{ job.number }}
                </span>
              </div>
            </div>
            <i class="fas fa-chevron-right job-arrow"></i>
          </li>
        </ul>

        <div v-if="!loading && !error && filteredJobs.length === 0" class="empty-state">
          <i class="fas fa-search"></i>
          <p>没有找到匹配的职位</p>
        </div>
      </div>

      <div class="job-detail-container" v-if="selectedJob">
        <div class="job-detail-card">
          <div class="job-detail-header">
            <h2 class="job-detail-title">{{ selectedJob.name }}</h2>
            <span class="job-detail-salary">{{ selectedJob.salary }}</span>
          </div>

          <div class="job-detail-content">
            <div class="detail-section">
              <h3><i class="fas fa-users"></i> 招聘人数</h3>
              <p>{{ selectedJob.number }} 人</p>
            </div>

            <div class="detail-section">
              <h3><i class="fas fa-clipboard-list"></i> 岗位要求</h3>
              <p>{{ selectedJob.demand }}</p>
            </div>

            <div class="detail-section">
              <h3><i class="fas fa-money-bill-wave"></i> 薪资待遇</h3>
              <p>{{ selectedJob.salary }}</p>
            </div>

            <button 
              class="apply-button" 
              :class="{ 
                'applied': appliedJobId === selectedJob.id,
                'applying': isApplying 
              }"
              @click="applyForJob"
              :disabled="isApplying || appliedJobId === selectedJob.id"
            >
              <i v-if="isApplying" class="fas fa-spinner fa-spin"></i>
              <i v-else-if="appliedJobId === selectedJob.id" class="fas fa-check"></i>
              <i v-else class="fas fa-paper-plane"></i>
              <span v-if="isApplying">申请中...</span>
              <span v-else-if="appliedJobId === selectedJob.id">已申请</span>
              <span v-else>立即申请</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'

// 定义职位类型
interface Job {
  id: number
  name: string
  salary: string
  number: number
  demand: string
}

const page = ref(1)
const size = ref(10)
const jobs = ref<Job[]>([])
const selectedJob = ref<Job | null>(null)
const loading = ref(true)
const error = ref('')
const searchQuery = ref('')
const isApplying = ref(false)
const appliedJobId = ref<number | null>(null) // 记录已申请的职位ID

const filteredJobs = computed(() => {
  if (!searchQuery.value) return jobs.value
  const query = searchQuery.value.toLowerCase()
  return jobs.value.filter(job => 
    job.name.toLowerCase().includes(query) ||
    job.demand.toLowerCase().includes(query)
  )
})

const selectJob = (job: Job) => {
  selectedJob.value = job
}

const filterJobs = () => {
  // 实现搜索过滤逻辑
}

const applyForJob = async () => {
  if (!selectedJob.value) {
    alert('请先选择一个职位');
    return;
  }

  // 检查是否已登录
  const token = localStorage.getItem('Authorization');
  if (!token) {
    alert('请先登录');
    return;
  }

  // 检查是否已经申请过
  if (appliedJobId.value) {
    alert('您已经申请过职位了，每人只能申请一个职位');
    return;
  }

  // 确认申请
  if (!confirm(`确定要申请 "${selectedJob.value.name}" 这个职位吗？`)) {
    return;
  }

  isApplying.value = true;

  try {
    const response = await axios.post('http://localhost:8080/user/job/submit', null, {
      params: { id: selectedJob.value.id },
      headers: { 
        Authorization: token 
      }
    });

    if (response.data.msg === 'success') {
      alert('申请成功！请等待审核结果');
      appliedJobId.value = selectedJob.value.id; // 记录已申请的职位
    } else {
      alert('申请失败，请稍后重试');
    }
  } catch (err: any) {
    console.error('申请失败:', err);
    if (err.response?.status === 401) {
      alert('登录已过期，请重新登录');
      // 可以在这里重定向到登录页面
    } else if (err.response?.data?.msg === 'error') {
      alert('您已经申请过职位了，每人只能申请一个职位');
      appliedJobId.value = selectedJob.value!.id; // 标记为已申请
    } else {
      alert('申请失败，请检查网络连接后重试');
    }
  } finally {
    isApplying.value = false;
  }
}

const fetchJobs = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const response = await axios.get('http://localhost:8080/user/job/getlist', {
      params: {
        page: page.value,
        size: size.value,
      },
      headers: {
        Authorization: localStorage.getItem('Authorization'),
      },
    })
    jobs.value = response.data.data
    
    // 检查用户是否已经申请过职位
    await checkApplicationStatus()
  } catch (err: any) {
    console.error('Error fetching jobs:', err)
    error.value = '获取职位列表失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

// 检查用户申请状态
const checkApplicationStatus = async () => {
  const token = localStorage.getItem('Authorization')
  if (!token) return
  
  try {
    // 这里可以调用一个API来获取用户的申请状态
    // 由于后端没有提供这个API，我们暂时跳过
    // 在实际项目中，应该有一个API来获取用户的申请记录
    console.log('检查申请状态...')
  } catch (err: any) {
    console.error('检查申请状态失败:', err)
  }
}

onMounted(() => {
  fetchJobs()
})
</script>

<style scoped>
.jobs-container {
  width: calc(100% - 200px);
  margin: 0;
  margin-left: 200px;
  padding: 2rem;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.jobs-header {
  margin-bottom: 2rem;
  background: rgba(255, 255, 255, 0.95);
  padding: 2rem;
  border-radius: 20px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.page-title {
  font-size: 2.5rem;
  color: #2d3748;
  margin-bottom: 1.5rem;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.page-title::before {
  content: '💼';
  font-size: 2rem;
}

.search-bar {
  position: relative;
  max-width: 600px;
}

.search-icon {
  position: absolute;
  left: 1.2rem;
  top: 50%;
  transform: translateY(-50%);
  color: #667eea;
  font-size: 1.1rem;
}

.search-bar input {
  width: 100%;
  padding: 1rem 1.2rem 1rem 3rem;
  border: 2px solid rgba(102, 126, 234, 0.2);
  border-radius: 25px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.search-bar input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.2);
  outline: none;
  background: rgba(255, 255, 255, 1);
  transform: translateY(-2px);
}

.search-bar input::placeholder {
  color: #a0aec0;
  font-weight: 500;
}

.content-wrapper {
  display: grid;
  grid-template-columns: 1fr 1.5fr;
  gap: 2.5rem;
  align-items: start;
  width: 100%;
}

.jobs-list-container {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  width: 100%;
  height: fit-content;
}

.job-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.job-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.5rem;
  border-bottom: 1px solid rgba(226, 232, 240, 0.5);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.job-item:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05), rgba(116, 75, 162, 0.05));
  transform: translateX(5px);
}

.job-item.selected {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(116, 75, 162, 0.1));
  border-left: 4px solid #667eea;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.job-item:last-child {
  border-bottom: none;
}

.job-item-content {
  flex: 1;
}

.job-main-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.job-name {
  font-size: 1.2rem;
  font-weight: 700;
  color: #2d3748;
  margin: 0;
  margin-bottom: 0.5rem;
}

.job-salary {
  color: #4caf50;
  font-weight: 600;
  background: linear-gradient(135deg, #f0fff4, #9ae6b4);
  padding: 0.4rem 1rem;
  border-radius: 20px;
  border: 1px solid rgba(76, 175, 80, 0.2);
  box-shadow: 0 2px 8px rgba(76, 175, 80, 0.1);
}

.job-sub-info {
  display: flex;
  gap: 1rem;
}

.job-tag {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  padding: 0.3rem 0.8rem;
  border-radius: 15px;
  font-weight: 500;
}

.job-arrow {
  color: #667eea;
  transition: all 0.3s ease;
  font-size: 1.1rem;
}

.job-item:hover .job-arrow {
  transform: translateX(4px);
}

.job-detail-container {
  position: sticky;
  top: 2rem;
  width: 100%;
}

.job-detail-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  padding: 2.5rem;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
  width: 100%;
  box-sizing: border-box;
}

.job-detail-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.job-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 2px solid rgba(102, 126, 234, 0.1);
}

.job-detail-title {
  font-size: 1.8rem;
  color: #2d3748;
  margin: 0;
  font-weight: 700;
}

.job-detail-salary {
  font-size: 1.3rem;
  color: #4caf50;
  font-weight: 700;
  background: linear-gradient(135deg, #f0fff4, #9ae6b4);
  padding: 0.5rem 1.2rem;
  border-radius: 25px;
  border: 1px solid rgba(76, 175, 80, 0.2);
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.2);
}

.detail-section {
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: rgba(102, 126, 234, 0.05);
  border-radius: 16px;
  border: 1px solid rgba(102, 126, 234, 0.1);
}

.detail-section h3 {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: #667eea;
  font-size: 1.2rem;
  margin-bottom: 1rem;
  font-weight: 600;
}

.detail-section h3 i {
  color: #667eea;
  font-size: 1.1rem;
}

.detail-section p {
  color: #4a5568;
  line-height: 1.7;
  margin: 0;
  font-size: 1rem;
  font-weight: 500;
}

.apply-button {
  width: 100%;
  padding: 1rem 2rem;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.75rem;
  margin-top: 2rem;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.apply-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #5a67d8, #6b46c1);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.apply-button.applied {
  background: linear-gradient(135deg, #4caf50, #388e3c);
  cursor: not-allowed;
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3);
}

.apply-button.applied:hover {
  background: linear-gradient(135deg, #4caf50, #388e3c);
  transform: none;
  box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3);
}

.apply-button.applying {
  background: linear-gradient(135deg, #ff9800, #f57c00);
  cursor: not-allowed;
  box-shadow: 0 4px 15px rgba(255, 152, 0, 0.3);
}

.apply-button.applying:hover {
  background: linear-gradient(135deg, #ff9800, #f57c00);
  transform: none;
  box-shadow: 0 4px 15px rgba(255, 152, 0, 0.3);
}

.apply-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.loading-state,
.error-state,
.empty-state {
  padding: 4rem 3rem;
  text-align: center;
  color: #718096;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 20px;
  margin: 2rem;
}

.loading-spinner {
  display: inline-block;
  width: 50px;
  height: 50px;
  border: 4px solid rgba(102, 126, 234, 0.2);
  border-radius: 50%;
  border-top-color: #667eea;
  animation: spin 1s linear infinite;
  margin-bottom: 1.5rem;
}

.error-state i,
.empty-state i {
  font-size: 3rem;
  color: #667eea;
  margin-bottom: 1.5rem;
  opacity: 0.7;
}

.retry-button {
  margin-top: 1.5rem;
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.retry-button:hover {
  background: linear-gradient(135deg, #5a67d8, #6b46c1);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 1024px) {
  .jobs-container {
    width: calc(100% - 200px);
  }
  
  .content-wrapper {
    grid-template-columns: 1fr;
  }

  .job-detail-container {
    position: static;
  }
}

@media (max-width: 640px) {
  .jobs-container {
    width: 100%;
    padding: 1rem;
    margin-left: 0;
  }

  .page-title {
    font-size: 1.5rem;
  }

  .job-item {
    padding: 1rem;
  }

  .job-detail-card {
    padding: 1.5rem;
  }
}
</style>
  