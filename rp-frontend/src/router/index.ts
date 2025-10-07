import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import Profile from '@/views/Profile.vue'
import Jobs from '@/views/Jobs.vue'
import JobDetail from '@/views/JobDetail.vue'
import Chat from '@/views/Chat.vue'
import ApplicationStatus from '@/views/ApplicationStatus.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [{
    name: 'Login',
    path: '/login',
    component: Login,
  },
  {
    name: 'Register',
    path: '/register',
    component: Register,

  },
  {
    name:'Profile',
    path: '/profile',
    component: Profile,
  },
  {
    name:'Jobs',
    path:'/jobs',
    component: Jobs, 
    children:[
      {name:'JobDetails',
      path: 'detail',
      component:JobDetail,
    }
    ]
  },
  {
    name:'Chat',
    path:'/chat',
    component: Chat,
  },
  {
    name:'ApplicationStatus',
    path:'/application-status',
    component: ApplicationStatus,
  }
  ],
})

export default router
