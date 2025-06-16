import { createRouter, createWebHistory } from 'vue-router';
import EnterpriseTenantRegister from '../components/EnterpriseTenantRegister.vue';
import Login from '../components/Login.vue';
//import Home from '../views/Home.vue'; // 假设首页组件为 Home.vue

const routes = [
  {
    path: '/register',
    component: EnterpriseTenantRegister,
  },
  {
    path: '/login',
    component: Login,
  },
  {
    path: '/',
    redirect: '/login',
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;