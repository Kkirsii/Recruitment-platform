<template>
  <div class="admin-login-container">
    <div class="login-wrapper">
      <div class="login-card">
        <div class="brand-section">
          <h1 class="brand-title">招聘平台</h1>
          <h2 class="admin-title">管理员登录</h2>
          <p class="admin-subtitle">请使用管理员账号登录系统</p>
        </div>

        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label for="email">管理员邮箱</label>
            <div class="input-wrapper">
              <i class="fas fa-user-shield"></i>
              <input
                type="email"
                id="email"
                v-model="formData.email"
                placeholder="请输入管理员邮箱"
                required
                @focus="clearError"
              />
            </div>
          </div>

          <div class="form-group">
            <label for="password">密码</label>
            <div class="input-wrapper">
              <i class="fas fa-lock"></i>
              <input
                :type="showPassword ? 'text' : 'password'"
                id="password"
                v-model="formData.password"
                placeholder="请输入密码"
                required
                @focus="clearError"
              />
              <i 
                :class="['password-toggle', showPassword ? 'fas fa-eye-slash' : 'fas fa-eye']"
                @click="togglePassword"
              ></i>
            </div>
          </div>

          <div v-if="errorMessage" class="error-message">
            <i class="fas fa-exclamation-circle"></i>
            {{ errorMessage }}
          </div>

          <button type="submit" class="login-button" :disabled="isLoading">
            <i v-if="isLoading" class="fas fa-spinner fa-spin"></i>
            <i v-else class="fas fa-sign-in-alt"></i>
            <span>{{ isLoading ? '登录中...' : '登录' }}</span>
          </button>
        </form>

        <div class="login-footer">
          <p>管理员专用登录入口</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAdminStore } from '@/stores/admin'
import axios from 'axios'

const router = useRouter()
const adminStore = useAdminStore()

const formData = reactive({
  email: '',
  password: ''
})

const showPassword = ref(false)
const isLoading = ref(false)
const errorMessage = ref('')

const clearError = () => {
  errorMessage.value = ''
}

const togglePassword = () => {
  showPassword.value = !showPassword.value
}

const handleLogin = async () => {
  if (isLoading.value) return

  clearError()
  isLoading.value = true

  try {
    const response = await axios.post('http://localhost:8080/admin/login', {
      email: formData.email,
      password: formData.password
    })

    if (response.data.msg === 'success') {
      // 登录成功
      adminStore.login(
        { email: formData.email },
        response.data.data
      )
      
      // 跳转到仪表板
      router.push('/dashboard')
    } else {
      errorMessage.value = '登录失败，请检查邮箱和密码'
    }
  } catch (error) {
    console.error('登录错误:', error)
    if (error.response?.status === 401) {
      errorMessage.value = '邮箱或密码错误'
    } else if (error.response?.status === 404) {
      errorMessage.value = '管理员账号不存在'
    } else {
      errorMessage.value = '登录失败，请稍后重试'
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.admin-login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  position: relative;
  overflow: hidden;
}

.admin-login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse"><path d="M 10 0 L 0 0 0 10" fill="none" stroke="rgba(255,255,255,0.1)" stroke-width="0.5"/></pattern></defs><rect width="100" height="100" fill="url(%23grid)"/></svg>');
  opacity: 0.3;
}

.login-wrapper {
  width: 100%;
  max-width: 480px;
  position: relative;
  z-index: 1;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 24px;
  padding: 3.5rem;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
}

.login-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #667eea, #764ba2, #f093fb);
}

.brand-section {
  text-align: center;
  margin-bottom: 3rem;
  position: relative;
}

.brand-title {
  font-size: 2.8rem;
  font-weight: 800;
  color: #2d3748;
  margin: 0 0 0.5rem 0;
  background: linear-gradient(135deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.02em;
}

.admin-title {
  font-size: 1.5rem;
  color: #4a5568;
  margin: 0 0 0.5rem 0;
  font-weight: 600;
}

.admin-subtitle {
  color: #718096;
  margin: 0;
  font-size: 0.95rem;
}

.login-form {
  margin-bottom: 2rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #4a5568;
  font-size: 0.95rem;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-wrapper i:first-child {
  position: absolute;
  left: 1rem;
  color: #a0aec0;
  font-size: 1rem;
  z-index: 1;
}

.password-toggle {
  position: absolute;
  right: 1rem;
  color: #a0aec0;
  cursor: pointer;
  transition: color 0.2s;
  z-index: 1;
}

.password-toggle:hover {
  color: #4a5568;
}

.input-wrapper input {
  width: 100%;
  padding: 0.875rem 1rem 0.875rem 2.5rem;
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

.error-message {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #e53e3e;
  font-size: 0.875rem;
  margin-bottom: 1rem;
  padding: 0.75rem;
  background: #fff5f5;
  border-radius: 8px;
  border-left: 4px solid #e53e3e;
}

.login-button {
  width: 100%;
  padding: 0.875rem;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
}

.login-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.login-footer {
  text-align: center;
  padding-top: 1.5rem;
  border-top: 1px solid #e2e8f0;
}

.login-footer p {
  color: #718096;
  font-size: 0.875rem;
  margin: 0;
}

/* 16:9 屏幕优化 */
@media (min-width: 1920px) {
  .admin-login-container {
    padding: 4rem;
  }
  
  .login-wrapper {
    max-width: 520px;
  }
  
  .login-card {
    padding: 4rem;
  }
  
  .brand-title {
    font-size: 3.2rem;
  }
  
  .admin-title {
    font-size: 1.8rem;
  }
}

@media (min-width: 1366px) and (max-width: 1919px) {
  .admin-login-container {
    padding: 3rem;
  }
  
  .login-wrapper {
    max-width: 500px;
  }
  
  .login-card {
    padding: 3.5rem;
  }
  
  .brand-title {
    font-size: 3rem;
  }
}

@media (max-width: 480px) {
  .admin-login-container {
    padding: 1rem;
  }
  
  .login-card {
    padding: 2rem;
  }
  
  .brand-title {
    font-size: 2rem;
  }
  
  .admin-title {
    font-size: 1.25rem;
  }
}
</style>
