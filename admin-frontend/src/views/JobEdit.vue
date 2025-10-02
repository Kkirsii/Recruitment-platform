<template>
  <div class="job-edit-container">
    <div class="page-header">
      <h1 class="page-title">
        <i class="fas fa-edit"></i>
        编辑职位信息
      </h1>
      <p class="page-subtitle">修改职位的详细信息和要求</p>
      <div class="header-actions">
        <button @click="goBack" class="back-button">
          <i class="fas fa-arrow-left"></i>
          返回
        </button>
      </div>
    </div>

    <div class="form-container">
      <form @submit.prevent="handleSubmit" class="job-edit-form">
        <div class="form-row">
          <div class="form-group">
            <label for="jobName">职位名称 *</label>
            <input
              type="text"
              id="jobName"
              v-model="formData.name"
              placeholder="请输入职位名称"
              required
              @focus="clearError"
            />
          </div>

          <div class="form-group">
            <label for="salary">薪资待遇 *</label>
            <input
              type="text"
              id="salary"
              v-model="formData.salary"
              placeholder="例如：8K-15K"
              required
              @focus="clearError"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="number">招聘人数 *</label>
            <input
              type="number"
              id="number"
              v-model.number="formData.number"
              placeholder="招聘人数"
              min="1"
              required
              @focus="clearError"
            />
          </div>

          <div class="form-group">
            <label for="location">工作地点</label>
            <input
              type="text"
              id="location"
              v-model="formData.location"
              placeholder="工作地点"
              @focus="clearError"
            />
          </div>
        </div>

        <div class="form-group">
          <label for="demand">岗位要求 *</label>
          <textarea
            id="demand"
            v-model="formData.demand"
            placeholder="请详细描述岗位要求、技能要求、工作经验等..."
            rows="6"
            required
            @focus="clearError"
          ></textarea>
        </div>

        <div class="form-group">
          <label for="description">职位描述</label>
          <textarea
            id="description"
            v-model="formData.description"
            placeholder="请描述工作内容、职责等..."
            rows="4"
            @focus="clearError"
          ></textarea>
        </div>

        <div class="form-group">
          <label for="benefits">福利待遇</label>
          <textarea
            id="benefits"
            v-model="formData.welfare"
            placeholder="五险一金、带薪年假、节日福利等..."
            rows="3"
            @focus="clearError"
          ></textarea>
        </div>

        <div v-if="errorMessage" class="error-message">
          <i class="fas fa-exclamation-circle"></i>
          {{ errorMessage }}
        </div>

        <div v-if="successMessage" class="success-message">
          <i class="fas fa-check-circle"></i>
          {{ successMessage }}
        </div>

        <div class="form-actions">
          <button type="button" @click="resetForm" class="reset-button">
            <i class="fas fa-undo"></i>
            重置
          </button>
          <button type="button" @click="goBack" class="cancel-button">
            <i class="fas fa-times"></i>
            取消
          </button>
          <button type="submit" class="submit-button" :disabled="isSubmitting">
            <i v-if="isSubmitting" class="fas fa-spinner fa-spin"></i>
            <i v-else class="fas fa-save"></i>
            <span>{{ isSubmitting ? '更新中...' : '更新职位' }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAdminStore } from '@/stores/admin'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const adminStore = useAdminStore()

const formData = reactive({
  id: null,
  name: '',
  salary: '',
  number: 1,
  location: '',
  demand: '',
  description: '',
  welfare: ''
})

const isSubmitting = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const clearError = () => {
  errorMessage.value = ''
  successMessage.value = ''
}

const resetForm = () => {
  loadJobData()
  clearError()
}

const loadJobData = () => {
  // 从路由参数或localStorage获取职位数据
  const jobId = route.query.id || route.params.id
  
  if (jobId) {
    // 从jobId获取职位详情
    fetchJobById(jobId)
  } else {
    // 从localStorage获取临时存储的职位数据
    const jobDataString = localStorage.getItem('editingJob')
    if (jobDataString) {
      try {
        const jobData = JSON.parse(jobDataString)
        Object.assign(formData, jobData)
        localStorage.removeItem('editingJob') // 清除临时数据
      } catch (error) {
        console.error('解析职位数据失败:', error)
        errorMessage.value = '加载职位数据失败'
      }
    }
  }
}

const fetchJobById = async (jobId) => {
  try {
    const response = await axios.get(`http://localhost:8080/admin/job/getlist`, {
      params: { page: 1, size: 1000 },
      headers: { Authorization: adminStore.token }
    })
    
    const jobs = response.data.data
    const job = jobs.find(j => j.id === parseInt(jobId))
    
    if (job) {
      Object.assign(formData, {
        id: job.id,
        name: job.name,
        salary: job.salary,
        number: parseInt(job.number) || 1,
        location: job.location || '',
        demand: job.demand || '',
        description: job.description || '',
        welfare: job.welfare || ''
      })
    } else {
      errorMessage.value = '未找到指定的职位信息'
    }
  } catch (error) {
    console.error('获取职位信息失败:', error)
    errorMessage.value = '加载职位信息失败，请稍后重试'
    if (error.response?.status === 401) {
      adminStore.logout()
      router.push('/login')
    }
  }
}

const handleSubmit = async () => {
  if (isSubmitting.value) return

  if (!formData.id) {
    errorMessage.value = '职位ID不能为空'
    return
  }

  clearError()
  isSubmitting.value = true

  try {
    const response = await axios.put('http://localhost:8080/admin/job/publish', formData, {
      headers: {
        Authorization: adminStore.token
      }
    })

    if (response.data.msg === 'success') {
      successMessage.value = '职位信息更新成功！'
      setTimeout(() => {
        router.push('/jobs')
      }, 1500)
    } else {
      errorMessage.value = '更新失败，请稍后重试'
    }
  } catch (error) {
    console.error('更新职位错误:', error)
    if (error.response?.status === 401) {
      errorMessage.value = '登录已过期，请重新登录'
      setTimeout(() => {
        adminStore.logout()
        router.push('/login')
      }, 2000)
    } else if (error.response?.status === 400) {
      errorMessage.value = '请检查填写的信息是否完整'
    } else {
      errorMessage.value = '更新失败，请检查网络连接后重试'
    }
  } finally {
    isSubmitting.value = false
  }
}

const goBack = () => {
  router.push('/jobs')
}

onMounted(() => {
  // 检查登录状态
  if (!adminStore.isLoggedIn) {
    router.push('/login')
    return
  }
  loadJobData()
})
</script>

<style scoped>
.job-edit-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  position: relative;
}

.job-edit-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="waves" width="40" height="40" patternUnits="userSpaceOnUse"><path d="M0,20 Q10,0 20,20 T40,20" fill="none" stroke="rgba(102,126,234,0.1)" stroke-width="1"/></pattern></defs><rect width="100" height="100" fill="url(%23waves)"/></svg>');
  opacity: 0.3;
}

.page-header {
  text-align: center;
  margin-bottom: 3rem;
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.95);
  padding: 2rem;
  border-radius: 20px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 1rem;
}

.back-button {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: #e2e8f0;
  color: #4a5568;
}

.back-button:hover {
  background: #cbd5e0;
  transform: translateY(-1px);
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2d3748;
  margin: 0 0 0.5rem 0;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.page-title i {
  color: #667eea;
  -webkit-text-fill-color: #667eea;
}

.page-subtitle {
  color: #718096;
  font-size: 1.1rem;
  margin: 0;
}

.form-container {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 3rem;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 1;
}

.job-edit-form {
  max-width: 900px;
  margin: 0 auto;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  margin-bottom: 2rem;
}

.form-group {
  margin-bottom: 2rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 600;
  color: #4a5568;
  font-size: 0.95rem;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.9);
  font-family: inherit;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 118, 234, 0.15);
  outline: none;
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #e53e3e;
  font-size: 0.875rem;
  margin-bottom: 1.5rem;
  padding: 0.75rem;
  background: #fff5f5;
  border-radius: 8px;
  border-left: 4px solid #e53e3e;
}

.success-message {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #38a169;
  font-size: 0.875rem;
  margin-bottom: 1.5rem;
  padding: 0.75rem;
  background: #f0fff4;
  border-radius: 8px;
  border-left: 4px solid #38a169;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-top: 2rem;
}

.reset-button,
.cancel-button,
.submit-button {
  padding: 0.875rem 2rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  min-width: 140px;
  justify-content: center;
}

.reset-button {
  background: #e2e8f0;
  color: #4a5568;
}

.reset-button:hover {
  background: #cbd5e0;
  transform: translateY(-1px);
}

.cancel-button {
  background: #ff6b6b;
  color: white;
}

.cancel-button:hover {
  background: #ee5a24;
  transform: translateY(-1px);
}

.submit-button {
  background: linear-gradient(135deg, #43e97b, #38f9d7);
  color: white;
}

.submit-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(67, 233, 123, 0.3);
}

.submit-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

/* 16:9 屏幕优化 */
@media (min-width: 1920px) {
  .job-edit-container {
    max-width: 1400px;
    padding: 3rem;
  }
  
  .form-container {
    padding: 4rem;
  }
  
  .job-edit-form {
    max-width: 1000px;
  }
  
  .page-title {
    font-size: 3rem;
  }
  
  .form-row {
    gap: 2.5rem;
  }
}

@media (min-width: 1366px) and (max-width: 1919px) {
  .job-edit-container {
    max-width: 1300px;
    padding: 2.5rem;
  }
  
  .form-container {
    padding: 3.5rem;
  }
  
  .job-edit-form {
    max-width: 950px;
  }
  
  .page-title {
    font-size: 2.8rem;
  }
}

@media (max-width: 768px) {
  .job-edit-container {
    padding: 1rem;
  }
  
  .page-header {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
  
  .form-container {
    padding: 1.5rem;
  }
  
  .form-row {
    grid-template-columns: 1fr;
    gap: 0;
  }
  
  .page-title {
    font-size: 2rem;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .reset-button,
  .cancel-button,
  .submit-button {
    width: 100%;
  }
}
</style>
