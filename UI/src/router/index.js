import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/',
        redirect: '/login' // 默认跳转到登录页
    },
    {
        path: '/login',
        name: 'Login',
        // 注意：这里的文件名要和你 views 里的实际文件名大小写一致
        // 根据你之前的截图，你的是 login.vue (小写l)
        component: () => import('../views/login.vue')
    },
    {
        path: '/student',
        name: 'Student',
        // 根据你之前的截图，你的是 MainConsole.vue (大写S)
        component: () => import('../views/MainConsole.vue')
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

export default router