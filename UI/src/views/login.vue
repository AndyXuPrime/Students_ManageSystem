<template>
  <div class="retro-container">
    <!-- 磁带机外壳 -->
    <div class="walkman-body">
      <div class="brand-logo">SONY_SIMS <span class="sub-text">AUTO REVERSE / 2025</span></div>

      <!-- 磁带窗口 (表单区域) -->
      <div class="cassette-window">
        <div class="tape-reel left"></div>
        <div class="tape-label">
          <div class="side-mark">A SIDE [ STUDENT LOGIN ]</div>

          <el-form :model="form" class="retro-form">
            <div class="input-group">
              <label>USER:</label>
              <input v-model="form.username" type="text" class="handwritten-input" placeholder="ADMIN" />
            </div>
            <div class="input-group">
              <label>PASS:</label>
              <input v-model="form.password" type="password" class="handwritten-input" placeholder="******" />
            </div>
            <div class="input-group">
              <label>CODE:</label>
              <div class="code-row">
                <input v-model="form.code" type="text" class="handwritten-input short" />
                <div class="lcd-display" @click="refreshCaptcha">{{ captchaCode || '8888' }}</div>
              </div>
            </div>
          </el-form>

        </div>
        <div class="tape-reel right"></div>
      </div>

      <!-- 物理按键区域 -->
      <div class="controls">
        <button class="mech-btn play-btn" @click="handleLogin" :disabled="loading">
          <div class="btn-text">LOGIN</div>
          <div class="btn-icon">▶</div>
        </button>
        <button class="mech-btn stop-btn" @click="resetForm">
          <div class="btn-text">RESET</div>
          <div class="btn-icon">■</div>
        </button>
        <div class="led-light" :class="{ 'on': loading }">REC/BATT</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCaptcha, login } from '../api/auth.js'
import { ElMessage } from 'element-plus'

// ... (逻辑代码保持不变，与之前提供的一致)
// 这里为了节省篇幅省略逻辑部分，直接复制之前的 script setup 即可
// 记得把 el-input 换成了原生的 input 以便更好控制样式，或者深度定制 el-input
const router = useRouter()
const loading = ref(false)
const captchaCode = ref('')
const form = ref({ username: 'admin', password: '123', code: '', uuid: '' })

const refreshCaptcha = async () => {
  try {
    const res = await getCaptcha()
    form.value.uuid = res.uuid
    captchaCode.value = res.code
  } catch (e) {}
}

const handleLogin = async () => {
  loading.value = true
  try {
    const token = await login(form.value)
    localStorage.setItem('token', token)
    router.push('/student')
  } catch (e) { refreshCaptcha() }
  finally { loading.value = false }
}
const resetForm = () => { form.value = {username:'', password:'', code:''} }
onMounted(() => refreshCaptcha())
</script>

<style scoped>
/* 核心复古样式 */
.retro-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: radial-gradient(circle, #2b003b 0%, #000000 100%);
  overflow: hidden;
}

.walkman-body {
  width: 500px;
  background: #d4d4d4;
  border-radius: 20px;
  padding: 30px;
  box-shadow:
      inset 0 0 20px rgba(0,0,0,0.5),
      10px 10px 30px rgba(0,0,0,0.8),
      -2px -2px 5px rgba(255,255,255,0.2);
  border: 4px solid #444;
  position: relative;
}

.brand-logo {
  font-family: 'Arial', sans-serif;
  font-weight: 900;
  font-style: italic;
  font-size: 24px;
  color: #333;
  margin-bottom: 15px;
  text-shadow: 1px 1px 0 #fff;
}
.sub-text { font-size: 12px; font-weight: normal; letter-spacing: 2px; }

/* 磁带窗口 */
.cassette-window {
  background: #111;
  border-radius: 10px;
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 3px solid #666;
  box-shadow: inset 0 0 10px #000;
  position: relative;
}

/* 磁带标签贴纸 (表单背景) */
.tape-label {
  background: #f0e68c; /* 米黄色纸张感 */
  width: 65%;
  height: 180px;
  padding: 10px;
  border-radius: 5px;
  box-shadow: 0 0 5px rgba(0,0,0,0.5);
  font-family: 'VT323', monospace;
  position: relative;
  z-index: 2;
}

.side-mark {
  font-size: 20px;
  font-weight: bold;
  border-bottom: 2px solid #333;
  margin-bottom: 10px;
}

/* 磁带卷轴动画 */
.tape-reel {
  width: 60px;
  height: 60px;
  background: radial-gradient(circle, #fff 10%, #333 11%, #333 30%, #fff 31%);
  border-radius: 50%;
  border: 2px solid #555;
  position: relative;
}
.tape-reel::after {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: repeating-conic-gradient(#333 0 10deg, transparent 10deg 20deg);
  border-radius: 50%;
  animation: spin 5s linear infinite;
}
@keyframes spin { 100% { transform: rotate(360deg); } }

/* 输入框样式 - 模拟手写线 */
.input-group {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 22px;
}
.input-group label { width: 60px; font-weight: bold; color: #333; }
.handwritten-input {
  background: transparent;
  border: none;
  border-bottom: 2px dashed #555;
  font-family: 'VT323', monospace; /* 或者使用手写体 */
  font-size: 24px;
  color: #000080;
  width: 100%;
  outline: none;
}
.handwritten-input.short { width: 80px; }

/* 验证码 LCD 屏 */
.lcd-display {
  background: #9ea792; /* 经典计算器灰绿色 */
  border: 2px inset #666;
  color: #000;
  font-family: 'Courier New', monospace;
  letter-spacing: 3px;
  padding: 0 10px;
  margin-left: 10px;
  cursor: pointer;
  box-shadow: inset 2px 2px 5px rgba(0,0,0,0.2);
}

/* 物理按钮 */
.controls {
  margin-top: 20px;
  display: flex;
  gap: 20px;
  justify-content: center;
  align-items: center;
}
.mech-btn {
  width: 80px;
  height: 50px;
  background: linear-gradient(to bottom, #eee, #ccc);
  border: none;
  border-bottom: 6px solid #999;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.1s;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.mech-btn:active {
  transform: translateY(4px);
  border-bottom: 2px solid #999;
}
.play-btn { background: #ff6b6b; color: #fff; border-bottom-color: #c0392b; }
.stop-btn { background: #4ecdc4; color: #fff; border-bottom-color: #2e86de; }

.led-light {
  width: 10px;
  height: 10px;
  background: #330000;
  border-radius: 50%;
  margin-left: 20px;
  position: relative;
  color: #333;
  font-size: 10px;
  white-space: nowrap;
}
.led-light::after { content: 'REC/BATT'; position: absolute; left: 15px; top: -2px; font-weight: bold; }
.led-light.on { background: #ff0000; box-shadow: 0 0 10px #ff0000; }
</style>