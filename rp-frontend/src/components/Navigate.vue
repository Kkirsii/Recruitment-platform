<template>
    <nav class="navbar">
      <div class="logo">MyApp</div>
      <ul class="nav-links">
        <li><RouterLink to="/login">登录</RouterLink></li>
        <li><RouterLink to="/register">注册</RouterLink></li>
        <li><RouterLink to="/profile">个人主页</RouterLink></li>
        <li><RouterLink to="/jobs">招聘信息</RouterLink></li>
        <li><RouterLink to="/application-status">投递状态</RouterLink></li>
        <li><RouterLink to="/chat">面试</RouterLink></li>
      </ul>
    </nav>
  </template>
  
  <script setup>
  import { RouterLink } from 'vue-router';
  </script>
  
  <style scoped>
  .navbar {
    margin: 0;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    background-color: #667eea;
    padding: 2rem 1rem;
    color: white;
    position: fixed;
    left: 0;
    top: 0;
    height: 100vh;
    width: 200px;
    box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  }
  
  .logo {
    font-size: 1.5rem;
    font-weight: bold;
  }
  
  .nav-links {
    display: flex;
    flex-direction: column;
    list-style: none;
    gap: 0;
    flex: 1;
    justify-content: space-around;
    width: 100%;
    padding: 2rem 0;
  }
  
  .nav-links li {
    transition: all 0.3s ease;
    width: 100%;
    text-align: center;
  }
  
  .nav-links li:hover {
    background-color: rgba(255, 255, 255, 0.1);
    border-radius: 8px;
    transform: translateX(5px);
  }
  
  a {
    color: white;
    text-decoration: none;
    font-weight: 500;
    display: block;
    padding: 1rem;
    border-radius: 8px;
    transition: all 0.3s ease;
  }
  
  a:hover {
    background-color: rgba(255, 255, 255, 0.15);
  }
  
  /* 响应式设计 */
  @media (max-width: 768px) {
    .navbar {
      width: 100%;
      height: auto;
      position: relative;
      flex-direction: row;
      justify-content: space-around;
      padding: 1rem;
    }
    
    .nav-links {
      flex-direction: row;
      justify-content: space-around;
      padding: 0;
      flex: none;
    }
    
    .nav-links li {
      width: auto;
    }
    
    a {
      padding: 0.5rem;
      font-size: 0.9rem;
    }
  }
  </style>
  