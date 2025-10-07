<template>
  <div class="register-container">
    <div class="register-wrapper">
      <div class="register-card">
        <div class="brand-logo">
          <img src="../assets/logo.png" alt="Logo" v-if="false">
          <h1 class="brand-name">招聘平台</h1>
        </div>
        <h2 class="title">创建账号</h2>
        <p class="subtitle">填写以下信息完成注册</p>

        <form @submit.prevent="handleRegister" class="register-form">
          <div class="form-group">
            <label for="email">邮箱</label>
            <div class="input-wrapper">
              <i class="fas fa-envelope"></i>
              <input
                type="email"
                id="email"
                v-model="email"
                placeholder="请输入邮箱"
                required
                @focus="clearError"
              >
            </div>
          </div>

          <div class="form-group">
            <label for="password">密码</label>
            <div class="input-wrapper">
              <i class="fas fa-lock"></i>
              <input
                :type="showPassword ? 'text' : 'password'"
                id="password"
                v-model="password"
                placeholder="请输入密码"
                required
                @focus="clearError"
              >
              <i 
                :class="['password-toggle', showPassword ? 'fas fa-eye-slash' : 'fas fa-eye']"
                @click="togglePassword"
              ></i>
            </div>
          </div>

          <div class="form-group">
            <label for="code">验证码</label>
            <div class="code-row">
              <div class="input-wrapper code-input">
                <i class="fas fa-shield-alt"></i>
                <input
                  type="text"
                  id="code"
                  v-model="code"
                  placeholder="验证码"
                  required
                  @focus="clearError"
                >
              </div>
              <button 
                type="button"
                @click="sendCode"
                :disabled="sending"
                class="code-button"
              >
                {{ sending ? `${countdown}秒后重试` : '获取验证码' }}
              </button>
            </div>
          </div>

          <div class="error-message" v-if="errorMsg">
            {{ errorMsg }}
          </div>

          <button type="submit" class="register-button" :disabled="isLoading">
            <span v-if="!isLoading">注册</span>
            <span v-else class="loading-spinner"></span>
          </button>

          <div class="form-footer">
            <router-link to="/login" class="login-link">
              已有账号？立即登录
            </router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const password = ref('')
const email = ref('')
const code = ref('')
const sending = ref(false)
const countdown = ref(60)
const errorMsg = ref('')
const isLoading = ref(false)
const showPassword = ref(false)
let timer = null

const clearError = () => {
  errorMsg.value = '';
}

const togglePassword = () => {
  showPassword.value = !showPassword.value;
}

const sendCode = async () => {
  if (!email.value) {
    errorMsg.value = '请输入邮箱地址'
    return
  }

  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
    errorMsg.value = '请输入有效的邮箱地址'
    return
  }

  try {
    sending.value = true
    await axios.post('http://localhost:8080/user/signup/getcode', {
      email: email.value,
    })
    
    countdown.value = 60
    timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
        sending.value = false
      }
    }, 1000)
  } catch (err) {
    errorMsg.value = '发送验证码失败，请稍后重试'
    console.error(err)
    sending.value = false
  }
}

const handleRegister = async () => {
  if (!email.value || !password.value || !code.value) {
    errorMsg.value = '请填写完整信息'
    return
  }

  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
    errorMsg.value = '请输入有效的邮箱地址'
    return
  }

  if (password.value.length < 6) {
    errorMsg.value = '密码长度至少为6位'
    return
  }

  try {
    isLoading.value = true
    const res = await axios.post('http://localhost:8080/user/signup', {
      email: email.value,
      password: password.value,
      code: code.value,
    })
    
    if(res.data.msg !== 'success') {
      errorMsg.value = '注册失败，请检查验证码'
      return
    }
    
    router.push('/login')
  } catch (err) {
    errorMsg.value = '注册失败，请稍后重试'
    console.error(err)
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.register-wrapper {
  width: 100%;
  max-width: 420px;
  margin: auto;
}

.register-card {
  background: rgba(255, 255, 255, 0.95);
  padding: 2.5rem;
  border-radius: 20px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
  backdrop-filter: blur(10px);
}

.brand-logo {
  text-align: center;
  margin-bottom: 1.5rem;
}

.brand-logo img {
  width: 80px;
  height: 80px;
  object-fit: contain;
}

.brand-name {
  font-size: 1.8rem;
  color: #2d3748;
  margin: 0.5rem 0;
  font-weight: 600;
}

.title {
  text-align: center;
  font-size: 1.5rem;
  color: #2d3748;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.subtitle {
  text-align: center;
  color: #718096;
  margin-bottom: 2rem;
  font-size: 0.95rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
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

.input-wrapper i {
  position: absolute;
  left: 1rem;
  color: #a0aec0;
  font-size: 1rem;
}

.password-toggle {
  position: absolute;
  right: 1rem;
  color: #a0aec0;
  cursor: pointer;
  transition: color 0.2s;
}

.password-toggle:hover {
  color: #4a5568;
}

input {
  width: 100%;
  padding: 0.75rem 1rem 0.75rem 2.5rem;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  background: white;
}

input:focus {
  border-color: #4299e1;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.15);
  outline: none;
}

.code-row {
  display: flex;
  gap: 0.75rem;
}

.code-input {
  flex: 1;
}

.code-button {
  padding: 0 1.5rem;
  background: #4299e1;
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  min-width: 120px;
}

.code-button:hover:not(:disabled) {
  background: #3182ce;
}

.code-button:disabled {
  background: #a0aec0;
  cursor: not-allowed;
}

.error-message {
  color: #e53e3e;
  font-size: 0.875rem;
  margin-bottom: 1rem;
  padding: 0.5rem;
  background: #fff5f5;
  border-radius: 8px;
  text-align: center;
}

.register-button {
  width: 100%;
  padding: 0.875rem;
  background: #4299e1;
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.register-button:hover:not(:disabled) {
  background: #3182ce;
  transform: translateY(-1px);
}

.register-button:disabled {
  background: #a0aec0;
  cursor: not-allowed;
}

.loading-spinner {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.form-footer {
  margin-top: 1.5rem;
  text-align: center;
}

.login-link {
  color: #4299e1;
  text-decoration: none;
  font-size: 0.95rem;
  transition: color 0.2s;
}

.login-link:hover {
  color: #3182ce;
  text-decoration: underline;
}

@media (max-width: 480px) {
  .register-card {
    padding: 1.5rem;
  }
  
  .title {
    font-size: 1.25rem;
  }
  
  input {
    font-size: 0.95rem;
  }
  
  .code-button {
    padding: 0 1rem;
    font-size: 0.8rem;
    min-width: 100px;
  }
}
</style>
  