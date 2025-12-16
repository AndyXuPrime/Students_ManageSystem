import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': path.resolve(__dirname, 'src')
        }
    },
    server: {
        port: 5173,
        open: true,
        proxy: {
            '/api': {
                // 👇 关键修改 1：指向网关端口 8080 (而不是业务服务 8082)
                target: 'http://localhost:8080',
                changeOrigin: true,

                // 👇 关键修改 2：注释掉或删除 rewrite
                // 不要在这里去掉 /api，因为网关需要靠 /api 前缀来识别请求
                // rewrite: (path) => path.replace(/^\/api/, '')
            }
        }
    }
})