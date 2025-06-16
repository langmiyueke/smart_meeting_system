<template>
  <div class="login-container">
    <div class="login-bg"></div>
    <el-card class="login-card">
      <div class="login-header">
        <h2 class="login-title">用户登录</h2>
        <p class="login-subtitle">欢迎回来，请登录您的账户</p>
      </div>
      
      <el-form
        :model="loginForm"
        ref="formRef"
        class="login-form"
        :rules="loginRules"
        @keyup.enter="login"
      >
        <!-- Common username field -->
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- Common password field -->
        <el-form-item prop="userPwd">
          <el-input
            type="password"
            v-model="loginForm.userPwd"
            placeholder="请输入密码"
            size="large"
            show-password
            :prefix-icon="Lock"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

 

        <!-- Admin-specific fields (visible only when admin is selected) -->
        <div v-show="loginForm.role === 'super_admin'">
          <el-form-item prop="adminToken">
            <el-input
              v-model="loginForm.adminToken"
              placeholder="请输入管理员令牌"
              size="large"
              :prefix-icon="Key"
            >
              <template #prefix>
                <el-icon><Key /></el-icon>
              </template>
            </el-input>
          </el-form-item>
        </div>

        <div class="role-selector">
          <el-radio-group v-model="loginForm.role">
            <el-radio-button label="enterprise" value="enterprise_tenant">企业用户</el-radio-button>
            <el-radio-button label="admin" value="super_admin">管理员</el-radio-button>
          </el-radio-group>
        </div>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            @click="login"
            :loading="loading"
            class="login-btn"
          >
            登录
          </el-button>
        </el-form-item>

        <div class="login-footer">
          <el-link type="info" @click="toRegister">注册新账号</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
export default defineComponent({
  name: 'Login'
})
</script>

<script lang="ts" setup>
import { ref, onMounted, watchEffect } from "vue"
import { User, Lock, OfficeBuilding, Key } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserInfoStore } from '../stores/userInfo';

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const userInfo = useUserInfoStore()


// 账号密码参数
const loginForm = ref({
  username: "",
  userPwd: "",
  role: "enterprise_tenant",
  adminCode: ""
})


const login = async () => {
  if (!formRef.value) return;
  
  try {
    loading.value = true;
    await formRef.value.validate();
    
    const loginData = {
      username: loginForm.value.username,
      password: loginForm.value.userPwd,
      role: loginForm.value.role,
      adminCode: loginForm.value.role === 'super_admin' ? loginForm.value.adminCode : null
    };

    // 调用登录接口
    const token = await userInfo.login(loginData);
    
    
    ElMessage.success('登录成功');
    router.push({ name: "Home" });
  } catch (error: any) {
    console.error('登录失败:', error);
    ElMessage.error(error.message || '登录失败，请检查用户名和密码');
  } finally {
    loading.value = false;
  } 
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f2f5;
  position: relative;
}

.login-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('../assets/login-bg.jpg') no-repeat center center;
  background-size: cover;
  filter: blur(5px);
  z-index: 0;
}

.login-card {
  width: 400px;
  z-index: 1;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 24px;
}

.login-title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}

.login-subtitle {
  font-size: 14px;
  color: #909399;
}

.login-form {
  margin-top: 20px;
}

.role-selector {
  margin-bottom: 20px;
  text-align: center;
}

.login-btn {
  width: 100%;
}

.login-footer {
  text-align: center;
  margin-top: 10px;
}
</style>