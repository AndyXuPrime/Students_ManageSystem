import request from '../utils/request'

// 获取验证码
export function getCaptcha() {
    return request({
        url: '/auth/captcha', // 这里会自动拼接到 /api/auth/captcha
        method: 'get'
    })
}

// 登录
export function login(data) {
    return request({
        url: '/auth/login',
        method: 'post',
        data
    })
}