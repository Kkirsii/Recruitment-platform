<template>
  <div class="profile-container">    
    <div class="profile-content">
      <form @submit.prevent="updateInfo" class="profile-form">
        <!-- 头像中心展示区域 -->
        <div class="form-section avatar-center-section">
          <div class="avatar-center">
            <div class="avatar-container-large">
              <img 
                v-if="profileImage" 
                :src="profileImage" 
                alt="个人头像"
                class="profile-avatar-large"
                @error="handleImgError"
              />
              <div v-else class="avatar-placeholder-large">
                <i class="fas fa-user"></i>
              </div>
            </div>
            
            <div class="avatar-info">
              <h2 class="avatar-title">个人头像</h2>
              <p class="avatar-subtitle">上传您的个人照片，让更多人了解您</p>
            </div>
            
            <div class="avatar-actions">
              <label for="imageUpload" class="file-button primary">
                <i class="fas fa-camera"></i>
                上传头像
              </label>
              <input 
                id="imageUpload" 
                type="file" 
                accept="image/*" 
                @change="uploadImage" 
                style="display: none;"
              />
              
              <button 
                type="button" 
                @click="refreshImage" 
                class="file-button secondary"
                :disabled="isImageLoading"
              >
                <i v-if="isImageLoading" class="fas fa-spinner fa-spin"></i>
                <i v-else class="fas fa-sync-alt"></i>
                刷新头像
              </button>
            </div>
            
            <div v-if="imageMessage" class="file-message avatar-message">
              {{ imageMessage }}
            </div>
          </div>
        </div>

        <div class="form-section">
          <h3 class="section-title">
            <i class="fas fa-user-circle"></i>
            基本信息
          </h3>
          
          <div class="form-grid">
            <div class="form-group">
              <label for="email">
                <i class="fas fa-envelope"></i>
                邮箱地址
              </label>
              <div class="input-wrapper disabled">
                <input 
                  id="email" 
                  v-model="user.email" 
                  type="email" 
                  disabled 
                  placeholder="邮箱地址"
                />
              </div>
            </div>

            <div class="form-group">
              <label for="id_number">
                <i class="fas fa-id-card"></i>
                姓名
              </label>
              <div class="input-wrapper">
                <input 
                  id="id_number" 
                  v-model="user.name" 
                  type="text" 
                  placeholder="请输入您的姓名"
                  @focus="clearError"
                />
              </div>
            </div>

            <div class="form-group">
              <label for="age">
                <i class="fas fa-calendar-alt"></i>
                年龄
              </label>
              <div class="input-wrapper">
                <input 
                  id="age" 
                  v-model.number="user.age" 
                  type="number" 
                  placeholder="请输入您的年龄"
                  min="18"
                  max="65"
                  @focus="clearError"
                />
              </div>
            </div>

            <div class="form-group">
              <label for="gender">
                <i class="fas fa-venus-mars"></i>
                性别
              </label>
              <div class="select-wrapper">
                <select id="gender" v-model="user.gender">
                  <option value="男">男</option>
                  <option value="女">女</option>
                  <option value="其他">其他</option>
                </select>
              </div>
            </div>

            <div class="form-group">
              <label for="phone">
                <i class="fas fa-phone"></i>
                手机号
              </label>
              <div class="input-wrapper">
                <input 
                  id="phone" 
                  v-model="user.phone" 
                  type="tel" 
                  placeholder="请输入手机号码"
                  @focus="clearError"
                />
              </div>
            </div>
          </div>
        </div>

        <div class="form-section">
          <h3 class="section-title">
            <i class="fas fa-graduation-cap"></i>
            教育背景
          </h3>
          
          <div class="form-grid">
            <div class="form-group">
              <label for="university">
                <i class="fas fa-university"></i>
                大学毕业学院
              </label>
              <div class="input-wrapper">
                <input 
                  id="university" 
                  v-model="user.university" 
                  type="text" 
                 placeholder="请输入毕业大学"
                  @focus="clearError"
                />
              </div>
            </div>

            <div class="form-group">
              <label for="major">
                <i class="fas fa-book"></i>
                专业
              </label>
              <div class="input-wrapper">
                <input 
                  id="major" 
                  v-model="user.major" 
                  type="text" 
                  placeholder="请输入专业"
                  @focus="clearError"
                />
              </div>
            </div>
          </div>
        </div>

        <!-- 简历管理区域 -->
        <div class="form-section resume-section-compact">
          <h3 class="section-title">
            <i class="fas fa-file-pdf"></i>
            简历管理
          </h3>
          
          <div class="resume-compact">
            <div class="resume-status-compact">
              <i class="fas fa-file-pdf" :class="{ 'has-resume': hasResume }"></i>
              <span class="resume-text-compact">
                {{ hasResume ? '简历已上传' : '暂无简历' }}
              </span>
            </div>
            
            <div class="resume-actions-compact">
              <label for="resumeUpload" class="file-button compact resume-upload-compact">
                <i class="fas fa-upload"></i>
                上传简历
              </label>
              <input 
                id="resumeUpload" 
                type="file" 
                accept=".pdf,.doc,.docx" 
                @change="uploadResume" 
                style="display: none;"
              />
              
              <button 
                v-if="hasResume"
                type="button" 
                @click="downloadResume" 
                class="file-button compact resume-download-compact"
                :disabled="isResumeLoading"
              >
                <i v-if="isResumeLoading" class="fas fa-spinner fa-spin"></i>
                <i v-else class="fas fa-download"></i>
                下载
              </button>
            </div>
            
            <div v-if="resumeMessage" class="file-message resume-message-compact">
              {{ resumeMessage }}
            </div>
          </div>
          <div class="resume-hint-text">
            支持 PDF、DOC、DOCX 格式，最大 10MB
          </div>
        </div>

        <div class="error-message" v-if="errorMsg">
          <i class="fas fa-exclamation-circle"></i>
          {{ errorMsg }}
        </div>

        <div class="success-message" v-if="successMsg">
          <i class="fas fa-check-circle"></i>
          {{ successMsg }}
        </div>

        <div class="form-actions">
          <button type="button" @click="resetForm" class="reset-button">
            <i class="fas fa-undo"></i>
            重置
          </button>
          <button type="submit" class="save-button" :disabled="isLoading">
            <i v-if="isLoading" class="fas fa-spinner fa-spin"></i>
            <i v-else class="fas fa-save"></i>
            <span>{{ isLoading ? '保存中...' : '保存信息' }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const user = ref({
  email: '',
  name: '',
  age: '',
  gender: '其他',
  university: '',
  major: '',
  phone: '',
})

const isLoading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

// 文件管理相关状态
const profileImage = ref('')
const hasResume = ref(false)
const isImageLoading = ref(false)
const isResumeLoading = ref(false)
const imageMessage = ref('')
const resumeMessage = ref('')

const clearError = () => {
  errorMsg.value = ''
  successMsg.value = ''
}

const resetForm = () => {
  fetchUserInfo()
  clearError()
}

// 图片错误处理
const handleImgError = () => {
  profileImage.value = ''
  console.log('图片加载失败，显示默认头像')
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const response = await axios.get('http://localhost:8080/user/userinfo', {
      headers: {
        Authorization: localStorage.getItem('Authorization')
      }
  })
    if(response.data.data===null){
      user.value.email = localStorage.getItem('Email')
      user.value.name = '无'
      user.value.age='0'
      user.value.gender='其他'
      user.value.university='无'
      user.value.major='无'
      user.value.phone='无'
      return
    }
    user.value = response.data.data
    console.log('用户信息:', user.value)
    
    // 检查是否有简历
    hasResume.value = !!user.value.resumeName
    
    // 加载用户头像
    await loadUserImage()
  } catch (error) {
    console.error('获取用户信息失败:', error)
    errorMsg.value = '获取用户信息失败'
  }
}

// 更新用户信息
const updateInfo = async () => {
  isLoading.value = true
  errorMsg.value = ''
  successMsg.value = ''
  
  try {
    await axios.post('http://localhost:8080/user/userinfo', user.value,{
      headers: {
        Authorization: localStorage.getItem('Authorization')
      }
    })
    successMsg.value = '信息保存成功！'
    setTimeout(() => {
      successMsg.value = ''
    }, 3000)
  } catch (error) {
    console.error('保存信息失败:', error)
    errorMsg.value = '保存信息失败，请稍后重试'
  } finally {
    isLoading.value = false
  }
}

// 加载用户头像
const loadUserImage = async () => {
  try {
    const response = await axios.get('http://localhost:8080/user/userinfo/image', {
      headers: {
        Authorization: localStorage.getItem('Authorization')
      },
      responseType: 'blob'
    })
    
    if (response.data && response.data.size > 0) {
      const imageUrl = URL.createObjectURL(response.data)
      profileImage.value = imageUrl
    } else {
      profileImage.value = ''
    }
  } catch (error) {
    console.log('无头像或头像加载失败:', error)
    profileImage.value = ''
  }
}

// 刷新头像
const refreshImage = async () => {
  isImageLoading.value = true
  imageMessage.value = '正在刷新头像...'
  
  try {
    await loadUserImage()
    imageMessage.value = '头像刷新成功！'
    setTimeout(() => {
      imageMessage.value = ''
    }, 3000)
  } catch (error) {
    imageMessage.value = '头像刷新失败'
    setTimeout(() => {
      imageMessage.value = ''
    }, 3000)
  } finally {
    isImageLoading.value = false
  }
}

// 上传头像
const uploadImage = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    imageMessage.value = '请选择图片文件'
    setTimeout(() => {
      imageMessage.value = ''
    }, 3000)
    return
  }
  
  // 验证文件大小 (5MB)
  if (file.size > 5 * 1024 * 1024) {
    imageMessage.value = '图片大小不能超过5MB'
    setTimeout(() => {
      imageMessage.value = ''
    }, 3000)
    return
  }
  
  imageMessage.value = '正在上传头像...'
  
  try {
    const formData = new FormData()
    formData.append('file', file)
    
    await axios.post('http://localhost:8080/user/userinfo/imageupload', formData, {
      headers: {
        'Authorization': localStorage.getItem('Authorization'),
        'Content-Type': 'multipart/form-data'
      }
    })
    
    imageMessage.value = '头像上传成功！'
    
    // 刷新头像显示
    setTimeout(async () => {
      await loadUserImage()
      imageMessage.value = ''
    }, 1500)
    
  } catch (error) {
    console.error('上传头像失败:', error)
    imageMessage.value = '头像上传失败，请重试'
    setTimeout(() => {
      imageMessage.value = ''
    }, 3000)
  }
  
  // 清空input
  event.target.value = ''
}

// 上传简历
const uploadResume = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  // 验证文件类型
  const allowedExtensions = ['.pdf', '.doc', '.docx']
  const fileName = file.name.toLowerCase()
  const isValidType = allowedExtensions.some(ext => fileName.endsWith(ext))
  
  if (!isValidType) {
    resumeMessage.value = '只支持PDF、DOC、DOCX格式'
    setTimeout(() => {
      resumeMessage.value = ''
    }, 3000)
    return
  }
  
  // 验证文件大小 (10MB)
  if (file.size > 10 * 1024 * 1024) {
    resumeMessage.value = '简历大小不能超过10MB'
    setTimeout(() => {
      resumeMessage.value = ''
    }, 3000)
    return
  }
  
  resumeMessage.value = '正在上传简历...'
  
  try {
    const formData = new FormData()
    formData.append('file', file)
    
    await axios.post('http://localhost:8080/user/userinfo/resumeupload', formData, {
      headers: {
        'Authorization': localStorage.getItem('Authorization'),
        'Content-Type': 'multipart/form-data'
      }
    })
    
    resumeMessage.value = ''
    successMsg.value = '简历上传成功！'
    hasResume.value = true
    
    setTimeout(() => {
      successMsg.value = ''
    }, 3000)
    
  } catch (error) {
    console.error('上传简历失败:', error)
    resumeMessage.value = '简历上传失败，请重试'
    setTimeout(() => {
      resumeMessage.value = ''
    }, 3000)
  }
  
  // 清空input
  event.target.value = ''
}

// 下载简历
const downloadResume = async () => {
  isResumeLoading.value = true
  resumeMessage.value = '正在下载简历...'
  
  try {
    const response = await axios.get('http://localhost:8080/user/userinfo/resume', {
      headers: {
        Authorization: localStorage.getItem('Authorization')
      },
      responseType: 'blob'
    })
    
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    
    // 从响应头获取文件名，或使用默认文件名
    const contentDisposition = response.headers['content-disposition']
    let filename = `${localStorage.getItem('Email')}_resume.pdf`
    
    if (contentDisposition) {
      const matches = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/)
      if (matches && matches[1]) {
        filename = matches[1].replace(/['"]/g, '')
      }
    }
    
    link.download = filename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    resumeMessage.value = '简历下载成功！'
    
  } catch (error) {
    console.error('下载简历失败:', error)
    resumeMessage.value = '简历下载失败，请重试'
  } finally {
    isResumeLoading.value = false
    setTimeout(() => {
      resumeMessage.value = ''
    }, 3000)
  }
}

onMounted(fetchUserInfo)
</script>

<style scoped>
.profile-container {
  max-width: 1000px;
  margin: 0 auto;
  margin-left: 200px;
  padding: 0;
  min-height: 100%;
}

/* 移除顶部header样式，现在直接显示头像区域 */

.profile-content {
  padding: 2rem 2rem 2rem;
}

.profile-form {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.form-section {
  background: white;
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  border: 1px solid #f1f5f9;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 1.3rem;
  color: #2d3748;
  margin-bottom: 1.5rem;
  padding-bottom: 0.75rem;
  border-bottom: 2px solid #f1f5f9;
}

.section-title i {
  color: #667eea;
  font-size: 1.1rem;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
  color: #4a5568;
  font-size: 0.95rem;
}

.form-group label i {
  color: #667eea;
  width: 16px;
}

.input-wrapper {
  position: relative;
}

.input-wrapper input {
  width: 100%;
  padding: 1rem 1rem 1rem 2.5rem;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: white;
}

.input-wrapper input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
  outline: none;
}

.input-wrapper.disabled input {
  background: #f7fafc;
  color: #718096;
  cursor: not-allowed;
}

.input-wrapper::before {
  content: '';
  position: absolute;
  left: 1rem;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;
  color: #a0aec0;
}

.select-wrapper {
  position: relative;
}

.select-wrapper::after {
  content: '\f107';
  font-family: "Font Awesome 5 Free";
  font-weight: 900;
  position: absolute;
  right: 1rem;
  top: 50%;
  transform: translateY(-50%);
  pointer-events: none;
  color: #a0aec0;
}

.select-wrapper select {
  width: 100%;
  padding: 1rem 2.5rem 1rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: white;
  appearance: none;
  cursor: pointer;
}

.select-wrapper select:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
  outline: none;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #e53e3e;
  font-size: 0.875rem;
  padding: 0.75rem 1rem;
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
  padding: 0.75rem 1rem;
  background: #f0fff4;
  border-radius: 8px;
  border-left: 4px solid #38a169;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  padding: 1.5rem 2rem;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.reset-button,
.save-button {
  padding: 1rem 2rem;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1rem;
}

.reset-button {
  background: #e2e8f0;
  color: #4a5568;
}

.reset-button:hover {
  background: #cbd5e0;
  transform: translateY(-2px);
}

.save-button {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  box-shadow: 0 4px 15px rgba(102, 118, 234, 0.3);
}

.save-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 118, 234, 0.4);
}

.save-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 头像中心区域样式 */
.avatar-center-section {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border: 1px solid #cbd5e0;
}

.avatar-center {
  text-align: center;
  padding: 2rem 0;
}

.avatar-container-large {
  position: relative;
  width: 200px;
  height: 200px;
  margin: 0 auto 2rem;
  border-radius: 50%;
  overflow: hidden;
  border: 6px solid #667eea;
  box-shadow: 0 20px 40px rgba(102, 126, 234, 0.3);
  background: white;
}

.profile-avatar-large {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder-large {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 4rem;
}

.avatar-info {
  margin-bottom: 2rem;
}

.avatar-title {
  font-size: 1.8rem;
  font-weight: 700;
  color: #2d3748;
  margin: 0 0 0.5rem 0;
  background: linear-gradient(45deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.avatar-subtitle {
  font-size: 1rem;
  color: #718096;
  margin: 0;
}

.avatar-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.avatar-message {
  margin-top: 1rem;
}

/* 文件按钮通用样式 */
.file-button {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  text-decoration: none;
  font-size: 0.9rem;
  min-width: 120px;
  justify-content: center;
}

.file-button.primary {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  box-shadow: 0 4px 15px rgba(102, 118, 234, 0.3);
}

.file-button.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 118, 234, 0.4);
}

.file-button.secondary {
  background: #e2e8f0;
  color: #4a5568;
  border: 2px solid transparent;
}

.file-button.secondary:hover:not(:disabled) {
  background: #cbd5e0;
  transform: translateY(-2px);
}

.file-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.file-button:disabled:hover {
  transform: none;
  box-shadow: none;
}

.file-message {
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-size: 0.875rem;
  font-weight: 500;
  text-align: center;
  background: #fff7ed;
  color: #ea580c;
  border-left: 4px solid #f97316;
}

.file-message.success {
  background: #f0fff4;
  color: #38a169;
  border-left-color: #48bb78;
}

.file-message.error {
  background: #fff5f5;
  color: #e53e3e;
  border-left-color: #fc8181;
}

/* 紧凑简历管理区域样式 */
.resume-section-compact {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  padding: 1.5rem;
}

.resume-compact {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  margin-bottom: 1rem;
}

.resume-status-compact {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex: 1;
}

.resume-status-compact i {
  font-size: 1.5rem;
  color: #e53e3e;
}

.resume-status-compact i.has-resume {
  color: #38a169;
}

.resume-text-compact {
  font-weight: 600;
  color: #4a5568;
}

.resume-actions-compact {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.file-button.compact {
  padding: 0.5rem 1rem;
  font-size: 0.85rem;
  min-width: auto;
}

.resume-upload-compact {
  background: linear-gradient(135deg, #667eea, #764ba2);
  box-shadow: 0 2px 8px rgba(102, 118, 234, 0.2);
}

.resume-download-compact {
  background: linear-gradient(135deg, #38a169, #2f855a);
  box-shadow: 0 2px 8px rgba(56, 161, 105, 0.2);
}

.resume-message-compact {
  text-align: center;
  margin-top: 0.75rem;
  font-size: 0.8rem;
  padding: 0.4rem 0.8rem;
}

.resume-hint-text {
  text-align: center;
  font-size: 0.75rem;
  color: #718096;
  margin-top: 0.5rem;
  font-style: italic;
}

@media (max-width: 768px) {
  .profile-container {
    margin-left: 0;
  }
  
  .profile-content {
    padding: 0 1rem 1rem;
  }
  
  .form-section {
    padding: 1.5rem;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .form-actions {
    flex-direction: column;
    padding: 1rem;
  }
  
  .reset-button,
  .save-button {
    width: 100%;
    justify-content: center;
  }
  
  .avatar-container-large {
    width: 150px;
    height: 150px;
  }
  
  .avatar-title {
    font-size: 1.5rem;
  }
  
  .avatar-subtitle {
    font-size: 0.9rem;
  }
  
  .avatar-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .avatar-actions .file-button {
    min-width: 200px;
  }
  
  /* 紧凑简历的移动端样式 */
  .resume-compact {
    flex-direction: column;
    align-items: stretch;
    gap: 1rem;
    text-align: center;
  }
  
  .resume-status-compact {
    justify-content: center;
  }
  
  .resume-actions-compact {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .resume-upload-compact,
  .resume-download-compact {
    width: 100%;
    justify-content: center;
  }
  
  .file-button {
    min-width: auto;
  }
}
</style>