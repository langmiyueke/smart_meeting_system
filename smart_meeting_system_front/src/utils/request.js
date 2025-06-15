import axios from 'axios';
import { ElMessage } from 'element-plus';
const baseURL = '/api'//自动拼接http://localhost:5173
const instance = axios.create({ baseURL })
import { useTokenStore } from '../stores/token'
//添加请求拦截器
instance.interceptors.request.use(
    config => {
        const tokenStore = useTokenStore()
        config.headers.Authorization = tokenStore.token || null
        return config
    },
    err => {
        return Promise.reject(err)
    }
)
import router from '../router'
//添加响应拦截器
instance.interceptors.response.use(
    result => {
        const data = result.data
        if (data.code === 0) {
            return data
        }
        ElMessage.error(data.message || '请求失败')
        return Promise.reject(data)
    },
    err => {
        if (err.response.status === 401) {
            ElMessage.error('请先登录')
            router.push('/login')
        } else {
            ElMessage.error(`服务异常：${err.response?.status} - ${err.message}`)
        }
        return Promise.reject(err);
    }
)
export default instance;