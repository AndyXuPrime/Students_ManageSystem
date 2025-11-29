import axios from 'axios'
import { ElMessage } from 'element-plus'

// 1. 创建 axios 实例
const service = axios.create({
    // 基础路径：
    // 在开发环境，这里写 '/api'，配合 vite.config.js 的 proxy 转发给网关 (localhost:8080)
    baseURL: '/api',
    // 请求超时时间：10秒
    timeout: 10000,
    headers: { 'Content-Type': 'application/json;charset=utf-8' }
})

// 2. 请求拦截器 (Request Interceptor)
service.interceptors.request.use(
    (config) => {
        // 在发送请求之前做些什么

        // 从 localStorage 获取 Token (假设登录后你把 token 存到了这里)
        const token = localStorage.getItem('token')

        if (token) {
            // 让每个请求携带自定义 token
            // 具体 header 名字要看后端 AuthController 怎么解析，通常是 'Authorization' 或 'token'
            config.headers['Authorization'] = token
        }
        return config
    },
    (error) => {
        // 对请求错误做些什么
        console.error('Request Error:', error)
        return Promise.reject(error)
    }
)

// 3. 响应拦截器 (Response Interceptor)
service.interceptors.response.use(
    (response) => {
        // 2xx 范围内的状态码都会触发该函数

        // 获取后端返回的完整数据 ( Result<T> )
        const res = response.data

        // 校验业务状态码 (根据你的 Result.java，成功是 200)
        if (res.code === 200) {
            // 成功：直接返回 data 部分，组件里就不用再写 .data.data 了
            return res.data
        } else {
            // 失败：弹出错误提示
            ElMessage.error(res.msg || '系统繁忙，请稍后再试')

            // 如果是 401 (未登录/Token过期)，可以在这里强制跳转到登录页
            if (res.code === 401) {
                localStorage.removeItem('token')
                window.location.href = '/login'
            }

            return Promise.reject(new Error(res.msg || 'Error'))
        }
    },
    (error) => {
        // 超出 2xx 范围的状态码都会触发该函数 (如 404, 500)
        console.error('Response Error:', error)

        let message = '系统未知错误'
        if (error.response) {
            const status = error.response.status
            switch (status) {
                case 400: message = '请求参数错误'; break
                case 401: message = '未授权，请登录'; break
                case 403: message = '拒绝访问'; break
                case 404: message = '请求地址出错'; break
                case 500: message = '服务器内部错误'; break
                case 503: message = '服务不可用'; break
                default: message = `连接错误 ${status}`;
            }
        } else if (error.message.includes('timeout')) {
            message = '请求超时'
        } else if (error.message.includes('Network Error')) {
            message = '网络连接失败'
        }

        ElMessage.error(message)
        return Promise.reject(error)
    }
)

export default service
