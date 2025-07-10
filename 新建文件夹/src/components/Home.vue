<template>
    <div class="home-container">
        <!-- 顶部导航栏 -->
        <el-header class="header">
            <div class="logo">测盟汇管理系统</div>
            <el-menu mode="horizontal" :default-active="activeNav" @select="handleNavSelect" background-color="#304156"
                text-color="#bfcbd9" active-text-color="#409EFF">
                <el-menu-item index="home">首页</el-menu-item>
                <el-menu-item index="dynamic">行业动态</el-menu-item>
                <el-menu-item index="tech">技术平台</el-menu-item>
                <el-menu-item index="cooperation">合作交流</el-menu-item>
                <el-menu-item index="admin" v-if="userRole === 'admin'">管理后台</el-menu-item>
            </el-menu>
            <div class="user-info">
                <el-dropdown @command="handleCommand">
                    <span class="el-dropdown-link">
                        <el-avatar :size="36" :src="userAvatar" />
                        <span class="username">{{ userName }}</span>
                        <el-icon class="el-icon--right"><arrow-down /></el-icon>
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </el-header>

        <!-- 主内容区 -->
        <div class="main-content">
            <section class="subsystems-section">
                <div class="section-header">
                    <el-divider content-position="left">
                        <h2>系统功能</h2>
                    </el-divider>
                </div>

                <el-row :gutter="20">
                    <el-col :xs="24" :sm="12" :md="8" v-for="(system, index) in subsystems.slice(0, 3)"
                        :key="'first-' + index">
                        <el-card class="system-card" shadow="hover" @click="goToSubsystem(system.route)">
                            <div class="system-icon">
                                <component :is="system.icon" />
                            </div>
                            <div class="system-name">{{ system.name }}</div>
                            <div class="system-desc">{{ system.desc }}</div>
                        </el-card>
                    </el-col>
                </el-row>

                <el-row :gutter="20" style="margin-top: 20px;">
                    <el-col :xs="24" :sm="12" :md="8" v-for="(system, index) in subsystems.slice(3)"
                        :key="'second-' + index">
                        <el-card class="system-card" shadow="hover" @click="goToSubsystem(system.route)">
                            <div class="system-icon">
                                <component :is="system.icon" />
                            </div>
                            <div class="system-name">{{ system.name }}</div>
                            <div class="system-desc">{{ system.desc }}</div>
                        </el-card>
                    </el-col>
                </el-row>
            </section>
        </div>
        <el-footer class="footer">
            <div class="footer-links">
                <el-link :underline="false" @click="goToPage('about')">关于我们</el-link>
                <el-link :underline="false" @click="goToPage('contact')">联系方式</el-link>
                <el-link :underline="false" @click="goToPage('help')">帮助中心</el-link>
            </div>

        </el-footer>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
    User,  Monitor, Notebook, ChatSquare,
    DataBoard, ArrowDown, Connection
} from '@element-plus/icons-vue'

const router = useRouter()

// 用户信息
const userName = ref('访客用户')
const userAvatar = ref('')
const userRole = ref('guest') // admin, enterprise, member

// 导航状态
const activeNav = ref('home')

// 子系统列表
const subsystems = ref([
    {
        name: '用户管理',
        desc: '企业租户与用户管理',
        icon: User,
        route: '/user'
    },
    {
        name: '组织管理',
        desc: '部门架构与层级管理',
        icon: Connection,
        route: '/dept'
    },
    {
        name: '行业动态',
        desc: '资讯发布与管理',
        icon: Notebook,
        route: '/news'
    },
    {
        name: '课程管理',
        desc: '培训课程与技术学习',
        icon: Monitor,
        route: '/courseList'
    },
    {
        name: '会议管理',
        desc: '会议组织与参会管理',
        icon: ChatSquare,
        route: '/meeting'
    },
    {
        name: '数据分析',
        desc: '用户行为数据分析',
        icon: DataBoard,
        route: '/analytics'
    }
])

// 导航选择处理
const handleNavSelect = (index) => {
    activeNav.value = index
    switch (index) {
        case 'home':
            router.push('/')
            break
        case 'dynamic':
            router.push('/dynamic')
            break
        case 'tech':
            router.push('/tech')
            break
        case 'cooperation':
            router.push('/cooperation')
            break
        case 'admin':
            router.push('/admin')
            break
    }
}

// 用户命令处理
const handleCommand = (command) => {
    switch (command) {
        case 'profile':
            router.push('/profile')
            break
        case 'settings':
            router.push('/settings')
            break
        case 'logout':
            handleLogout()
            break
    }
}

// 登出处理
const handleLogout = () => {
    // 实际应用中应调用登出API并清除用户凭证
    console.log('用户登出')
    router.push('/login')
}

// 跳转到子系统
const goToSubsystem = (route) => {
    router.push(route)
}

// 跳转到其他页面
const goToPage = (page) => {
    router.push(`/${page}`)
}

// 组件挂载时加载数据
onMounted(() => {
    // 模拟从API获取用户信息
    setTimeout(() => {
        userName.value = '张经理'
        userAvatar.value = ''
        userRole.value = 'enterprise'
    }, 500)
})
</script>

<style scoped>
.home-container {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    background-color: #f5f7fa;
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background-color: #304156;
    color: #fff;
    padding: 0 20px;
    box-shadow: 0 1px 4px rgba(0, 21, 41, .08);
    z-index: 1000;
}

.logo {
    font-size: 22px;
    font-weight: bold;
    margin-right: 30px;
    color: #fff;
}

.user-info {
    display: flex;
    align-items: center;
    cursor: pointer;
}

.username {
    margin: 0 10px;
    color: #bfcbd9;
}

.main-content {
    flex: 1;
    padding: 20px;
    max-width: 1200px;
    margin: 0 auto;
    width: 100%;
}

.section-header {
    margin-bottom: 20px;
}

.section-header h2 {
    font-size: 22px;
    color: #303133;
    margin: 0;
}

.subsystems-section {
    margin: 40px 0;
    padding: 20px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.system-card {
    height: 200px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s;
    border-radius: 8px;
    margin-bottom: 15px;
    text-align: center;
    border: none;
    background-color: #f8f9fa;
}

.system-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(64, 158, 255, .2);
}

.system-icon {
    font-size: 36px;
    color: #409EFF;
    margin-bottom: 15px;
}

.system-name {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 8px;
}

.system-desc {
    font-size: 13px;
    color: #909399;
    text-align: center;
}

.footer {
    background-color: #304156;
    color: #a9b3c6;
    padding: 20px 0;
    text-align: center;
}

.footer-links {
    margin-bottom: 10px;
}

.footer-links .el-link {
    margin: 0 15px;
    color: #a9b3c6;
    font-size: 14px;
}

.footer-links .el-link:hover {
    color: #fff;
}

/* 响应式调整 */
@media (max-width: 768px) {
    .header {
        flex-direction: column;
        height: auto;
        padding: 10px;
    }

    .logo {
        margin-right: 0;
        margin-bottom: 10px;
    }

    .el-menu {
        margin-bottom: 10px;
    }

    .banner-title {
        font-size: 18px;
    }
}
</style>
