import axios from "axios";
import { ElMessage } from 'element-plus';
import pinia from '../stores/index';
import { useUserInfoStore } from '../stores/userInfo';
import NProgress from "nprogress";
import "nprogress/nprogress.css";
// 配置新建一个 axios 实例
const service = axios.create({
  baseURL: "/api",
  timeout: 50000,
});

// 添加请求拦截器
service.interceptors.request.use((config) => {
  NProgress.start()//开启进度条
  // 如果有token, 通过请求头携带给后台
  const userInfoStore = useUserInfoStore(pinia) // 如果不是在组件中调用,必须传入pinia
  const token = userInfoStore.token
  if (token) {
    config.headers.Authorization = token
  }
  return config;
});

// 添加响应拦截器
service.interceptors.response.use(
  (response) => {
    NProgress.done()
    if (response.data.code !== 0) {
      // ❗先提示，再手动构造 Error 对象返回
      ElMessage.error(response.data.message)
      return Promise.reject(new Error(response.data.message))
    }
    return response.data.data
  },
  (error) => {
    NProgress.done()
    ElMessage.error(error.message || '请求失败')
    return Promise.reject(error)
  }
)


export default service;