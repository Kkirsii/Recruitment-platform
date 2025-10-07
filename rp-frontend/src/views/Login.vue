<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="login-card">
        <div class="brand-logo">
          <img src="../assets/logo.png" alt="Logo" v-if="false">
          <h1 class="brand-name">招聘平台</h1>
        </div>
        <h2 class="title">欢迎回来</h2>
        <p class="subtitle">请登录您的账号</p>
        
        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label for="email">账号</label>
            <div class="input-wrapper">
              <i class="fas fa-user"></i>
              <input
                type="text"
                id="email"
                v-model="email"
                placeholder="请输入账号"
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
                v-model="password"
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

          <div class="error-message" v-if="errorMsg">
            {{ errorMsg }}
          </div>

          <button type="submit" class="login-button" :disabled="isLoading">
            <span v-if="!isLoading">登录</span>
            <span v-else class="loading-spinner"></span>
          </button>

          <div class="form-footer">
            <router-link to="/register" class="register-link">
              还没有账号？立即注册
            </router-link>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="login">
import { ref } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();
const email = ref('');
const password = ref('');
const errorMsg = ref('');
const isLoading = ref(false);
const showPassword = ref(false);

const clearError = () => {
  errorMsg.value = '';
};

const togglePassword = () => {
  showPassword.value = !showPassword.value;
};

async function handleLogin() {
  if (isLoading.value) return;
  
  clearError();
  isLoading.value = true;

  try {
    const response = await axios.post('http://localhost:8080/user/login', {
      email: email.value,
      password: password.value
    });

    if(response.data.msg === 'success') {
      localStorage.setItem('Authorization', response.data.data);
      localStorage.setItem('Email', email.value);
      router.push('/profile'); // 登录后跳转到个人页面
    } else {
      errorMsg.value = '账号或密码错误';
    }
  } catch (error) {
    console.error('登录失败:', error);
    errorMsg.value = '登录失败，请稍后重试';
  } finally {
    isLoading.value = false;
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.login-wrapper {
  width: 100%;
  max-width: 420px;
  margin: auto;
}

.login-card {
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

.error-message {
  color: #e53e3e;
  font-size: 0.875rem;
  margin-bottom: 1rem;
  padding: 0.5rem;
  background: #fff5f5;
  border-radius: 8px;
  text-align: center;
}

.login-button {
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

.login-button:hover {
  background: #3182ce;
  transform: translateY(-1px);
}

.login-button:disabled {
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

.register-link {
  color: #4299e1;
  text-decoration: none;
  font-size: 0.95rem;
  transition: color 0.2s;
}

.register-link:hover {
  color: #3182ce;
  text-decoration: underline;
}

@media (max-width: 480px) {
  .login-card {
    padding: 1.5rem;
  }
  
  .title {
    font-size: 1.25rem;
  }
  
  input {
    font-size: 0.95rem;
  }
}
</style>