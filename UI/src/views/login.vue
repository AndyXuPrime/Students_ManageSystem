<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2 style="text-align: center">学生管理系统登录</h2>
      </template>
      <el-form :model="form" ref="loginFormRef" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="admin"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="form.password" placeholder="任意密码"></el-input>
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <div style="display: flex; gap: 10px;">
            <el-input v-model="form.code" placeholder="输入验证码"></el-input>
            <div @click="refreshCaptcha" class="captcha-box">{{ captchaCode || '1234' }}</div>
          </div>
        </el-form-item>
        <el-button type="primary" style="width: 100%" @click="handleLogin" :loading="loading">登录</el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCaptcha, login } from '../api/auth.js' // 假设 api 文件在 src/api 下
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const captchaCode = ref('')
const form = ref({
  username: 'admin',
  password: '123',
  code: '',
  uuid: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const refreshCaptcha = async () => {
  try {
    const res = await getCaptcha()
    // 根据后端 AuthController，返回的是 map {uuid: "...", code: "..."}
    // 注意：request.js 已经解包了 Result，直接返回 data
    form.value.uuid = res.uuid
    captchaCode.value = res.code // 实际开发中通常返回图片流，这里后端直接返回了 code 字符串用于测试
  } catch (e) {
    console.error(e)
  }
}

const handleLogin = async () => {
  if (!form.value.code) return ElMessage.warning('请输入验证码')

  loading.value = true
  try {
    const token = await login(form.value)
    // 后端返回 Result.success("fake-jwt-token...")
    localStorage.setItem('token', token)
    ElMessage.success('登录成功')
    router.push('/student')
  } catch (e) {
    // request.js 会自动弹窗错误
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}
.login-card {
  width: 400px;
}
.captcha-box {
  background: #eee;
  padding: 0 10px;
  cursor: pointer;
  letter-spacing: 5px;
  font-weight: bold;
  user-select: none;
}
</style>