import { createRouter, createWebHistory } from "vue-router";
import LayoutVue from "../views/Layout.vue";
import LoginVue from "../views/Login.vue";

const routes = [
    { path: '/login', component: LoginVue }
    ,
    {
        path: '/', component: LayoutVue, redirect: '', children: [
            
        ]
    }

]
const router = createRouter({
    history: createWebHistory(),
    routes: routes
})
import { useTokenStore } from '@/stores/token'
// 全局前置守卫
import { ElMessage } from 'element-plus'
router.beforeEach((to, from, next) => {
    const tokenStore = useTokenStore()
    const token = tokenStore.token
    if (to.meta.requiresAuth && !token) {
        ElMessage.error('请先登录')
        next('/login') // 跳转登录页
    } else {
        next()
    }
})

export default router