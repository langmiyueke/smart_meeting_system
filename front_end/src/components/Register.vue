<template>
  <div class="register-container">
    <div class="register-bg"></div>
    <el-card class="register-card">
      <div class="register-header">
        <h2 class="register-title">企业用户注册</h2>
        <p class="register-subtitle">欢迎注册，请填写企业信息</p>
      </div>

      <el-form :model="registerForm" ref="formRef" class="register-form" :rules="registerRules" @keyup.enter="register">
        <!-- 企业名称 -->
        <el-form-item prop="enterpriseName">
          <el-input v-model="registerForm.enterpriseName" placeholder="请输入企业名称" size="large"
            :prefix-icon="OfficeBuilding">
            <template #prefix>
              <el-icon>
                <OfficeBuilding />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 用户名 -->
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" size="large" :prefix-icon="User">
            <template #prefix>
              <el-icon>
                <User />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 企业联系方式 -->
        <el-form-item prop="enterprisePhone">
          <el-input v-model="registerForm.enterprisePhone" placeholder="请输入企业联系方式" size="large" :prefix-icon="Iphone">
            <template #prefix>
              <el-icon>
                <Iphone />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input type="password" v-model="registerForm.password" placeholder="请输入密码" size="large" show-password
            :prefix-icon="Lock">
            <template #prefix>
              <el-icon>
                <Lock />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 确认密码 -->
        <el-form-item prop="confirmPassword">
          <el-input type="password" v-model="registerForm.confirmPassword" placeholder="请再次输入密码" size="large"
            show-password :prefix-icon="Lock">
            <template #prefix>
              <el-icon>
                <Lock />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>

        <!-- 验证码 -->
        <el-form-item prop="verificationCode">
          <div class="verification-code-container">
            <el-input v-model="registerForm.verificationCode" placeholder="请输入验证码" size="large" :prefix-icon="Key"
              class="verification-code-input">
              <template #prefix>
                <el-icon>
                  <Key />
                </el-icon>
              </template>
            </el-input>
            <el-button
    type="primary"
    size="large"
    @click="sendCode"
    :disabled="codeCountdown > 0"
    class="verification-code-btn"
  >
    {{ codeCountdown > 0 ? `${codeCountdown}秒后重试` : '获取验证码' }}
  </el-button>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" @click="register" :loading="loading" class="register-btn">
            注册
          </el-button>
        </el-form-item>

        <div class="register-footer">
          <span>已有账号？</span>
          <el-link type="primary" @click="toLogin">立即登录</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
export default defineComponent({
  name: 'Register'
})
</script>

<script lang="ts" setup>
import { ref, onMounted } from "vue"
import { User, Lock, OfficeBuilding, Key, Iphone } from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserInfoStore } from '../stores/userInfo';
import { sendVerificationCode } from '../api/index.js'
import { ToRegister, verifyCode } from '../api/index'


const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)
const codeCountdown = ref(0)
let countdownTimer: number | null = null

const userInfo = useUserInfoStore()

// 注册表单数据
const registerForm = ref({
  enterpriseName: "",
  username: "",
  enterprisePhone: "",
  password: "",
  confirmPassword: "",
  verificationCode: ""
})



// 验证规则
const validatePassword = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.value.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const validatePhone = (rule: any, value: string, callback: any) => {
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!value) {
    callback(new Error('请输入联系方式'))
  } else if (!phoneRegex.test(value)) {
    callback(new Error('请输入有效的手机号码'))
  } else {
    callback()
  }
}

const registerRules = {
  enterpriseName: [
    { required: true, message: '请输入企业名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度在4到20个字符', trigger: 'blur' },
    { 
      pattern: /^[a-zA-Z0-9_]+$/,
      message: '用户名只能包含字母、数字和下划线',
      trigger: 'blur'
    }
  ],
  enterprisePhone: [
    { validator: validatePhone, trigger: 'blur' }
  ],
  password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
    {
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[^]{6,20}$/,
      message: '密码必须6~20位包含大小写字母和数字',
      trigger: 'blur'
    }
  ],
  confirmPassword: [
      { required: true, message: '请确认密码', trigger: ['blur', 'change'] },
      { validator: validatePassword, trigger: ['blur', 'change'] }
  ],
  verificationCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度为6位', trigger: 'blur' }
  ]
}

// 发送验证码
const sendCode = async () => {
  if (!registerForm.value.enterprisePhone) {
    ElMessage.warning('请输入手机号码')
    return
  }
  if (!formRef.value) return;

  try {
    // 同时验证【手机号、密码、确认密码】三个字段
    await formRef.value.validateField(['enterprisePhone', 'password', 'confirmPassword']);
    
    await sendVerificationCode(registerForm.value.enterprisePhone)
    ElMessage.success('验证码已发送')
    codeCountdown.value = 60
    countdownTimer = window.setInterval(() => {
      codeCountdown.value--
      if (codeCountdown.value <= 0 && countdownTimer) {
        clearInterval(countdownTimer)
        countdownTimer = null
      }
    }, 1000)
  } catch (error) {
    ElMessage.error(error.message || '验证码发送失败，请检查表单信息')
  }
}
// 注册
const register = async () => {
  if (!formRef.value) return;

  try {
    loading.value = true;
    await formRef.value.validate();
    // 先验证验证码
    const verifyRes = await verifyCode(registerForm.value.enterprisePhone,
      registerForm.value.verificationCode
    );
  
    if (!verifyRes) {
     throw new Error('验证码错误');
    }

    // 验证通过后继续注册流程
    const registerData = {
      username: registerForm.value.username,
      password: registerForm.value.password,
      role: 'enterprise_tenant',
      enterpriseName: registerForm.value.enterpriseName,
      enterprisePhone: registerForm.value.enterprisePhone
    };

    await ToRegister(registerData);

    ElMessage.success('注册成功');
    router.push({ name: "Login" });
  } catch (error: any) {
    ElMessage.error(error.message || '注册失败，请检查填写的信息');
  } finally {
    loading.value = false;
  }
};
// 跳转到登录
const toLogin = () => {
  router.push({ name: "Login" });
}

// 清除定时器
onMounted(() => {
  return () => {
    if (countdownTimer) {
      clearInterval(countdownTimer)
    }
  }
})
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f2f5;
  position: relative;
}

.register-bg {
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

.register-card {
  width: 450px;
  z-index: 1;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
}

.register-header {
  text-align: center;
  margin-bottom: 24px;
}

.register-title {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}

.register-subtitle {
  font-size: 14px;
  color: #909399;
}

.register-form {
  margin-top: 20px;
}

.register-btn {
  width: 100%;
}

.register-footer {
  text-align: center;
  margin-top: 10px;
  color: #909399;
}

.verification-code-container {
  display: flex;
  gap: 10px;
}

.verification-code-input {
  flex: 1;
}

.verification-code-btn {
  width: 150px;
}
</style>