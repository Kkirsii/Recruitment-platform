<template>
  <div class="application-status-container">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="fas fa-clipboard-check"></i>
          我的投递状态
        </h1>
        <p class="page-subtitle">查看您的职位申请进度</p>
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

    <div v-if="loading" class="loading-state">
      <i class="fas fa-spinner fa-spin"></i>
      <span>加载中...</span>
    </div>

    <div v-else-if="applications.length === 0" class="empty-state">
      <i class="fas fa-inbox"></i>
      <h3>暂无投递记录</h3>
      <p>您还没有投递任何职位</p>
      <button @click="goToJobs" class="go-jobs-button">
        <i class="fas fa-search"></i>
        浏览职位
      </button>
    </div>

    <div v-else class="applications-list">
      <div 
        v-for="application in applications" 
        :key="application.id" 
        class="application-card"
      >
        <div class="card-header">
          <div class="job-info">
            <h3 class="job-title">{{ application.jobName || '职位名称' }}</h3>
          </div>
        </div>

        <div class="status-section">
          <div class="status-header">
            <h4>申请状态</h4>
            <span class="status-badge" :class="getStatusClass(application.jobState)">
              {{ getStatusText(application.jobState) }}
            </span>
          </div>

          <!-- 进度条 -->
          <div class="progress-container">
            <div class="progress-bar">
              <div 
                class="progress-fill" 
                :style="{ 
                  width: getProgressWidth(application.jobState) + '%',
                  background: getProgressGradient(application.jobState)
                }"
              ></div>
            </div>
            <div class="progress-steps">
              <div 
                v-for="(step, index) in progressSteps" 
                :key="index"
                class="progress-step"
                :class="{ 
                  'active': index <= getCurrentStep(application.jobState),
                  'completed': index < getCurrentStep(application.jobState)
                }"
              >
                <div 
                  class="step-icon"
                  :style="{ 
                    backgroundColor: index <= getCurrentStep(application.jobState) ? (index < getCurrentStep(application.jobState) ? '#4caf50' : step.color) : '#e2e8f0',
                    color: index <= getCurrentStep(application.jobState) ? 'white' : '#a0aec0'
                  }"
                >
                  <i :class="step.icon"></i>
                </div>
                <div 
                  class="step-label"
                  :style="{ 
                    color: index <= getCurrentStep(application.jobState) ? (index < getCurrentStep(application.jobState) ? '#4caf50' : step.color) : '#718096'
                  }"
                >
                  {{ step.label }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="card-actions">
          <button 
            v-if="application.jobState === 0 || application.jobState === 1 || application.jobState === 2"
            @click="cancelApplication(application.jobId)" 
            class="cancel-button"
          >
            <i class="fas fa-times"></i>
            取消投递
          </button>
        </div>
      </div>
    </div>

    <!-- 简历预览弹窗 -->
    <div v-if="showPdfModal" class="pdf-modal-overlay" @click="closePdfModal">
      <div class="pdf-modal" @click.stop>
        <div class="pdf-modal-header">
          <div class="pdf-modal-title">
            <i class="fas fa-file-pdf"></i>
            <span>我的简历</span>
          </div>
          <div class="pdf-modal-actions">
            <button @click="downloadResume" class="download-btn">
              <i class="fas fa-download"></i>
              下载
            </button>
            <button @click="closePdfModal" class="close-btn">
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>
        
        <div class="pdf-modal-content">
          <div v-if="pdfLoading" class="pdf-loading">
            <i class="fas fa-spinner fa-spin"></i>
            <span>正在加载简历...</span>
          </div>
          
          <div v-else-if="pdfUrl" class="pdf-viewer">
            <iframe 
              :src="pdfUrl" 
              class="pdf-iframe"
              title="简历预览"
              @load="pdfLoading = false"
            ></iframe>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

// 响应式数据
const applications = ref([])
const loading = ref(false)

// PDF预览弹窗相关状态
const showPdfModal = ref(false)
const pdfUrl = ref('')
const pdfLoading = ref(false)

// 进度步骤定义
const progressSteps = [
  { label: '简历筛选中', icon: 'fas fa-search', color: '#ff6b6b', bgColor: '#fff5f5' },
  { label: '待面试', icon: 'fas fa-calendar-alt', color: '#ffa726', bgColor: '#fff8e1' },
  { label: '面试通过待录用', icon: 'fas fa-thumbs-up', color: '#4caf50', bgColor: '#f1f8e9' },
  { label: '已录用', icon: 'fas fa-trophy', color: '#4caf50', bgColor: '#e3f2fd' }
]

// 方法
const fetchApplications = async () => {
  loading.value = true
  try {
    const token = localStorage.getItem('Authorization')
    if (!token) {
      router.push('/login')
      return
    }

    // 使用新的用户投递状态API接口
    const response = await axios.get('http://localhost:8080/user/job/get_submitState', {
      headers: { Authorization: token }
    })
    
    if (response.data.data && Array.isArray(response.data.data)) {
      // 直接使用返回的投递记录数组
      applications.value = response.data.data
    } else {
      applications.value = []
    }
  } catch (error) {
    console.error('获取投递记录失败:', error)
    if (error.response?.status === 401) {
      localStorage.removeItem('Authorization')
      localStorage.removeItem('Email')
      router.push('/login')
    }
  } finally {
    loading.value = false
  }
}

const refreshData = async () => {
  await fetchApplications()
}

const goBack = () => {
  router.push('/profile')
}

const goToJobs = () => {
  router.push('/jobs')
}

const viewJobDetail = (jobId) => {
  router.push(`/jobs/detail?id=${jobId}`)
}

// 取消投递
const cancelApplication = async (jobId) => {
  if (!confirm('确定要取消这个职位的投递吗？')) {
    return
  }

  try {
    const token = localStorage.getItem('Authorization')
    if (!token) {
      router.push('/login')
      return
    }

    await axios.put('http://localhost:8080/user/job/sumit_cancel', null, {
      params: { id: jobId },
      headers: { Authorization: token }
    })
    
    alert('投递已取消')
    // 刷新数据
    await fetchApplications()
  } catch (error) {
    console.error('取消投递失败:', error)
    alert('取消投递失败，请稍后重试')
  }
}

const getStatusClass = (state) => {
  switch (state) {
    case 0: return 'status-screening'
    case 1: return 'status-pending-interview'
    case 2: return 'status-interview-sent'
    case 3: return 'status-interview-passed'
    case 4: return 'status-rejected'
    case 5: return 'status-abandoned'
    case 6: return 'status-hired'
    default: return 'status-unknown'
  }
}

const getStatusText = (state) => {
  switch (state) {
    case 0: return '简历筛选中'
    case 1: return '待面试'
    case 2: return '已发送面试邀请'
    case 3: return '面试通过待录用'
    case 4: return '不匹配'
    case 5: return '放弃申请'
    case 6: return '已录用'
    default: return '未知状态'
  }
}

const getCurrentStep = (state) => {
  switch (state) {
    case 0: return 0 // 简历筛选中
    case 1: return 1 // 待面试
    case 2: return 1 // 已发送面试邀请
    case 3: return 2 // 面试通过待录用
    case 4: return -1 // 不匹配
    case 5: return -1 // 放弃申请
    case 6: return 3 // 已录用
    default: return 0
  }
}

const getProgressWidth = (state) => {
  const currentStep = getCurrentStep(state)
  if (currentStep === -1) return 0 // 失败状态
  return ((currentStep + 1) / progressSteps.length) * 100
}

const getProgressGradient = (state) => {
  const currentStep = getCurrentStep(state)
  if (currentStep === -1) return 'linear-gradient(135deg, #ff6b6b, #e53e3e)' // 失败状态
  
  // 根据当前步骤和已完成的步骤数量来确定颜色
  if (currentStep >= 2) {
    // 面试通过或已录用状态，使用绿色渐变
    return 'linear-gradient(135deg, #4caf50, #388e3c)'
  } else if (currentStep === 1) {
    // 待面试状态，使用橙色渐变
    return 'linear-gradient(135deg, #ffa726, #ff8f00)'
  } else if (currentStep === 0) {
    // 简历筛选中，使用红色渐变
    return 'linear-gradient(135deg, #ff6b6b, #e53e3e)'
  }
  
  return 'linear-gradient(135deg, #667eea, #764ba2)'
}

const formatDate = (timestamp) => {
  if (!timestamp) return '未知'
  return new Date(parseInt(timestamp)).toLocaleDateString('zh-CN')
}

// 查看简历
const viewResume = async () => {
  pdfLoading.value = true
  
  try {
    const token = localStorage.getItem('Authorization')
    if (!token) {
      router.push('/login')
      return
    }

    const response = await axios.get('http://localhost:8080/user/userinfo/resume', {
      headers: { Authorization: token },
      responseType: 'blob'
    })
    
    if (response.data && response.data.size > 0) {
      // 创建PDF预览URL
      const blob = new Blob([response.data], { type: 'application/pdf' })
      pdfUrl.value = URL.createObjectURL(blob)
      showPdfModal.value = true
    } else {
      alert('您还没有上传简历')
    }
  } catch (error) {
    console.error('查看简历失败:', error)
    if (error.response?.status === 404) {
      alert('您还没有上传简历')
    } else {
      alert('查看简历失败，请稍后重试')
    }
  } finally {
    pdfLoading.value = false
  }
}

// 下载简历
const downloadResume = async () => {
  try {
    const token = localStorage.getItem('Authorization')
    if (!token) {
      router.push('/login')
      return
    }

    const response = await axios.get('http://localhost:8080/user/userinfo/resume', {
      headers: { Authorization: token },
      responseType: 'blob'
    })
    
    if (response.data && response.data.size > 0) {
      // 创建下载链接
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement('a')
      
      // 设置文件名
      link.href = url
      link.download = `我的简历.pdf`
      
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      
      window.URL.revokeObjectURL(url)
    } else {
      alert('您还没有上传简历')
    }
  } catch (error) {
    console.error('下载简历失败:', error)
    if (error.response?.status === 404) {
      alert('您还没有上传简历')
    } else {
      alert('下载简历失败，请稍后重试')
    }
  }
}

// 关闭PDF预览弹窗
const closePdfModal = () => {
  // 清理URL对象避免内存泄漏
  if (pdfUrl.value) {
    URL.revokeObjectURL(pdfUrl.value)
  }
  
  showPdfModal.value = false
  pdfUrl.value = ''
}

// 键盘事件处理
const handleKeydown = (event) => {
  if (event.key === 'Escape' && showPdfModal.value) {
    closePdfModal()
  }
}

// 生命周期
onMounted(() => {
  const token = localStorage.getItem('Authorization')
  if (!token) {
    router.push('/login')
    return
  }
  fetchApplications()
  
  // 添加键盘事件监听
  window.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  // 清理键盘事件监听
  window.removeEventListener('keydown', handleKeydown)
  
  // 清理PDF URL对象
  if (pdfUrl.value) {
    URL.revokeObjectURL(pdfUrl.value)
  }
})
</script>

<style scoped>
.application-status-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 2rem;
  margin-left: 200px;
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

.loading-state, .empty-state {
  padding: 4rem;
  text-align: center;
  color: #718096;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
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

.go-jobs-button {
  margin-top: 1rem;
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
}

.go-jobs-button:hover {
  background: linear-gradient(135deg, #5a67d8, #6b46c1);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
}

.applications-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.application-card {
  background: rgba(255, 255, 255, 0.98);
  border-radius: 24px;
  padding: 2.5rem;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.application-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(102, 126, 234, 0.2);
}

.card-header {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
}

.job-info .job-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2d3748;
  margin: 0 0 0.5rem 0;
}

.job-info .job-company {
  color: #718096;
  margin: 0;
}

.application-time {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #718096;
  font-size: 0.9rem;
}

.status-section {
  margin-bottom: 1.5rem;
}

.status-section::before,
.status-section::after {
  display: none;
}

.status-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.status-header::before,
.status-header::after {
  display: none;
}

.status-header h4 {
  margin: 0;
  color: #4a5568;
  font-size: 1.1rem;
}

.status-badge {
  padding: 0.6rem 1.2rem;
  border-radius: 25px;
  font-size: 0.9rem;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.status-screening { background: linear-gradient(135deg, #fff5f5, #fed7d7); color: #c53030; }
.status-pending-interview { background: linear-gradient(135deg, #fffaf0, #ffcc80); color: #dd6b20; }
.status-interview-sent { background: linear-gradient(135deg, #ebf8ff, #90cdf4); color: #3182ce; }
.status-interview-passed { background: linear-gradient(135deg, #f0fff4, #9ae6b4); color: #2f855a; }
.status-rejected { background: linear-gradient(135deg, #fed7d7, #feb2b2); color: #c53030; }
.status-abandoned { background: linear-gradient(135deg, #f7fafc, #e2e8f0); color: #718096; }
.status-hired { background: linear-gradient(135deg, #f0fff4, #68d391); color: #276749; }

.progress-container {
  position: relative;
}

.progress-container::before,
.progress-container::after {
  display: none;
}

.progress-bar {
  width: 100%;
  height: 14px;
  background: linear-gradient(135deg, #e2e8f0, #cbd5e0);
  border-radius: 7px;
  overflow: hidden;
  margin-bottom: 2rem;
  box-shadow: inset 0 2px 6px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.progress-fill {
  height: 100%;
  border-radius: 7px;
  transition: all 0.5s ease;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.progress-fill::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.progress-steps {
  display: flex;
  justify-content: space-between;
  position: relative;
}

.progress-steps::before,
.progress-steps::after {
  display: none;
}

.progress-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  flex: 1;
  position: relative;
}

.progress-step::before,
.progress-step::after {
  display: none;
}

.step-icon {
  width: 55px;
  height: 55px;
  border-radius: 50%;
  background: #e2e8f0;
  color: #a0aec0;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  font-size: 1.3rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 3px solid rgba(255, 255, 255, 0.5);
  position: relative;
}

.progress-step.active .step-icon {
  transform: scale(1.15);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.25);
  border: 3px solid rgba(255, 255, 255, 0.8);
}

.progress-step.completed .step-icon {
  transform: scale(1.1);
  box-shadow: 0 5px 16px rgba(76, 175, 80, 0.3);
  border: 3px solid rgba(76, 175, 80, 0.3);
}

.step-label {
  font-size: 0.95rem;
  color: #718096;
  text-align: center;
  font-weight: 500;
  margin-top: 0.8rem;
  transition: all 0.3s ease;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.progress-step.active .step-label {
  font-weight: 700;
  transform: scale(1.08);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.15);
}

.progress-step.completed .step-label {
  font-weight: 600;
  transform: scale(1.05);
  text-shadow: 0 1px 3px rgba(76, 175, 80, 0.2);
}

.card-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
  padding-top: 1rem;
}

.view-job-button, .view-resume-button {
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

.view-job-button {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}

.view-job-button:hover {
  background: linear-gradient(135deg, #5a67d8, #6b46c1);
  transform: translateY(-1px);
}

.view-resume-button {
  background: linear-gradient(135deg, #f97316, #ea580c);
  color: white;
}

.view-resume-button:hover {
  background: linear-gradient(135deg, #ea580c, #dc2626);
  transform: translateY(-1px);
}

.cancel-button {
  padding: 0.75rem 2rem;
  border: none;
  border-radius: 25px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
  font-size: 0.9rem;
}

.cancel-button:hover {
  background: linear-gradient(135deg, #dc2626, #b91c1c);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(239, 68, 68, 0.4);
}

/* PDF预览弹窗样式 */
.pdf-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.pdf-modal {
  width: 90%;
  height: 90%;
  max-width: 1200px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.pdf-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}

.pdf-modal-title {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-weight: 600;
  color: #4a5568;
  font-size: 1.1rem;
}

.pdf-modal-title i {
  color: #dc3545;
  font-size: 1.2rem;
}

.pdf-modal-actions {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.download-btn {
  padding: 0.5rem 1rem;
  background: linear-gradient(135deg, #38a169, #2f855a);
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.download-btn:hover {
  background: linear-gradient(135deg, #2f855a, #276749);
  transform: translateY(-1px);
}

.close-btn {
  width: 40px;
  height: 40px;
  background: #e2e8f0;
  color: #4a5568;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: #cbd5e0;
  transform: translateY(-1px);
}

.pdf-modal-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.pdf-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  color: #718096;
  font-size: 1rem;
}

.pdf-loading i {
  font-size: 2rem;
  color: #667eea;
}

.pdf-viewer {
  width: 100%;
  height: 100%;
  position: relative;
}

.pdf-iframe {
  width: 100%;
  height: 100%;
  border: none;
  border-radius: 0 0 12px 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .application-status-container {
    padding: 1rem;
    margin-left: 0;
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
  
  .card-header {
    flex-direction: column;
    gap: 1rem;
    align-items: flex-start;
  }
  
  .status-header {
    flex-direction: column;
    gap: 0.5rem;
    align-items: flex-start;
  }
  
  .progress-steps {
    flex-direction: column;
    gap: 1rem;
  }
  
  .progress-step {
    flex-direction: row;
    justify-content: flex-start;
  }
  
  .card-actions {
    flex-direction: column;
  }
  
  .pdf-modal {
    width: 95%;
    height: 95%;
  }
  
  .pdf-modal-header {
    padding: 0.75rem 1rem;
    flex-direction: column;
    gap: 0.75rem;
    align-items: stretch;
  }
  
  .pdf-modal-actions {
    justify-content: space-between;
  }
}
</style>
