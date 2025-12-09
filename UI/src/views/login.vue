<template>
  <div class="retro-container">
    <!-- 背景装饰层开始 -->
    <div class="atom-background">
      <div class="grid-floor"></div>
      <div class="grid-ceil"></div>
      <div class="atom-core">
        <div class="ring ring-1"></div>
        <div class="ring ring-2"></div>
        <div class="ring ring-3"></div>
        <div class="nucleus"></div>
      </div>
      <div class="radar-scan"></div>
    </div>
    <div class="crt-overlay"></div>
    <!-- 背景装饰层结束 -->

    <div class="beeper-body">
      <div class="brand-strip">
        <div class="brand-logo">MOTOROLA <span class="sub-text">FIX Beeper 2025</span></div>
        <div class="signal-icon">📶</div>
      </div>

      <div class="beeper-screen">
        <div class="screen-header">
          <span class="icon">✉️</span>
          <span class="time-display">{{ currentTime }}</span>
          <span class="battery-icon">🔋</span>
        </div>

        <div class="message-display">
          <div class="line">
            <span class="label">P:</span>
            <input v-model="form.username" type="text" class="screen-input full" placeholder="USER ID" />
          </div>
          <div class="line">
            <span class="label">C:</span>
            <input v-model="form.password" type="password" class="screen-input full" placeholder="PASSCODE" />
          </div>
          <div class="line code-line">
            <span class="label">V:</span>
            <input v-model="form.code" type="text" class="screen-input short" placeholder="CODE" maxlength="4" />
            <span class="code-value" @click="refreshCaptcha">{{ captchaCode || 'loading' }}</span>
          </div>
        </div>
      </div>

      <div class="controls">
        <button class="mech-btn read-btn" @click="handleLogin" :disabled="loading">
          <span class="btn-text">{{ loading ? 'WAIT..' : 'READ/SEND' }}</span>
        </button>
        <button class="mech-btn clear-btn" @click="resetForm">
          <span class="btn-text">CLR</span>
        </button>
        <button class="mech-btn menu-btn">
          <span class="btn-text">MENU</span>
        </button>
      </div>

      <div class="led-light" :class="{ 'on': loading }"></div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

import { getCaptcha, login } from '../api/auth.js'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const captchaCode = ref('')
const form = ref({ username: '', password: '', code: '', uuid: '' })
const currentTime = ref('12:00A')

const refreshCaptcha = async () => {
  try {
    const res = await getCaptcha()
    form.value.uuid = res.uuid
    captchaCode.value = res.code
  } catch (e) {
    ElMessage.error('获取验证码失败')
  }
}

const handleLogin = async () => {
  loading.value = true
  try {
    // 模拟延迟
    await new Promise(r => setTimeout(r, 1000))
    const token = await login(form.value)
    localStorage.setItem('token', token)
    router.push('/student')
    ElMessage.success('ACCESS GRANTED')
  } catch (e) {
    refreshCaptcha()
    ElMessage.error(e.msg || 'LOGIN FAILED')
  }
  finally { loading.value = false }
}

const resetForm = () => {
  form.value = {username:'', password:'', code:'', uuid: form.value.uuid}
}

const updateTime = () => {
  const now = new Date();
  const hours = now.getHours() % 12 || 12;
  const minutes = now.getMinutes().toString().padStart(2, '0');
  const ampm = now.getHours() >= 12 ? 'P' : 'A';
  currentTime.value = `${hours}:${minutes}${ampm}`;
}

onMounted(() => {
  refreshCaptcha();
  updateTime();
  setInterval(updateTime, 60000);
})
</script>

<style scoped>
.code-line {
  /* 关键属性：将整行（包括标签、输入框、验证码）变为斜体 */
  font-style: italic;
}
/* =========================================
   1. 原子朋克背景样式 (Atom Punk Background)
   ========================================= */
.retro-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #0d1117; /* 深空灰蓝 */
  overflow: hidden;
  position: relative;
  perspective: 1000px; /* 为3D网格提供透视 */
}

/* 背景容器 */
.atom-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  background: radial-gradient(circle at center, #1a2a3a 0%, #050505 80%);
}

/* 顶部和底部的复古网格 (Blueprint Grid) */
.grid-floor, .grid-ceil {
  position: absolute;
  width: 200%;
  height: 50%;
  left: -50%;
  background-image:
      linear-gradient(rgba(0, 255, 200, 0.2) 1px, transparent 1px),
      linear-gradient(90deg, rgba(0, 255, 200, 0.2) 1px, transparent 1px);
  background-size: 50px 50px;
  transform-style: preserve-3d;
}

.grid-floor {
  bottom: 0;
  transform: rotateX(60deg); /* 地面透视 */
  box-shadow: inset 0 100px 100px #000; /* 远处的淡出 */
  animation: gridMove 20s linear infinite;
}

.grid-ceil {
  top: 0;
  transform: rotateX(-60deg); /* 天花板透视 */
  opacity: 0.3;
  box-shadow: inset 0 -100px 100px #000;
  animation: gridMove 20s linear infinite reverse;
}

/* 原子核心动画 (The Atom) */
.atom-core {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 600px;
  height: 600px;
  transform: translate(-50%, -50%);
  pointer-events: none;
  opacity: 0.6;
}

.nucleus {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 40px;
  height: 40px;
  background: #00ffcc;
  border-radius: 50%;
  transform: translate(-50%, -50%);
  box-shadow: 0 0 30px #00ffcc, 0 0 60px #00ffcc;
  animation: pulse 3s ease-in-out infinite;
}

.ring {
  position: absolute;
  top: 50%;
  left: 50%;
  border: 2px solid rgba(0, 255, 204, 0.4);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  box-shadow: 0 0 10px rgba(0, 255, 204, 0.1);
}

.ring-1 { width: 200px; height: 500px; animation: orbit1 8s linear infinite; }
.ring-2 { width: 400px; height: 200px; animation: orbit2 12s linear infinite; }
.ring-3 { width: 350px; height: 350px; animation: orbit3 10s linear infinite; border: 1px dashed rgba(255, 165, 0, 0.3); }

/* 雷达扫描效果 */
.radar-scan {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 100vw;
  height: 100vh;
  background: conic-gradient(from 0deg, transparent 0deg, transparent 300deg, rgba(0, 255, 200, 0.05) 360deg);
  transform: translate(-50%, -50%);
  border-radius: 50%;
  animation: radar 5s linear infinite;
  pointer-events: none;
}

/* CRT 屏幕覆盖层 (扫描线和暗角) */
.crt-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 999; /* 最顶层，覆盖寻呼机 */
  background: linear-gradient(rgba(18, 16, 16, 0) 50%, rgba(0, 0, 0, 0.1) 50%);
  background-size: 100% 4px; /* 扫描线密度 */
  pointer-events: none; /* 允许点击穿透 */
  box-shadow: inset 0 0 100px rgba(0,0,0,0.9); /* 强烈的暗角 */
  opacity: 0.4;
}

/* 动画定义 */
@keyframes gridMove {
  0% { background-position: 0 0; }
  100% { background-position: 0 50px; }
}

@keyframes orbit1 { from { transform: translate(-50%, -50%) rotate(0deg); } to { transform: translate(-50%, -50%) rotate(360deg); } }
@keyframes orbit2 { from { transform: translate(-50%, -50%) rotate(45deg); } to { transform: translate(-50%, -50%) rotate(405deg); } }
@keyframes orbit3 { from { transform: translate(-50%, -50%) rotate(-45deg); } to { transform: translate(-50%, -50%) rotate(-405deg); } }

@keyframes pulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.8; }
  50% { transform: translate(-50%, -50%) scale(1.2); opacity: 1; }
}

@keyframes radar {
  from { transform: translate(-50%, -50%) rotate(0deg); }
  to { transform: translate(-50%, -50%) rotate(360deg); }
}

.beeper-body {
  width: 300px;
  background: #444;
  border-radius: 15px;
  padding: 15px;
  box-shadow:
      inset 0 0 10px rgba(0,0,0,0.8),
      0 20px 50px rgba(0,0,0,0.8); /* 增加投影以在复杂背景中突显 */
  border: 3px solid #666;
  position: relative;
  font-family: 'Courier New', monospace;
  z-index: 10; /* 确保在背景之上，但在CRT层之下 */
  transform: scale(1.05); /* 稍微放大一点 */
}

/* 以下样式完全保持你原有的代码，未做修改 */
.brand-strip {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.brand-logo {
  font-family: 'Arial', sans-serif;
  font-weight: bold;
  font-size: 16px;
  color: #c8c8c8;
}
.sub-text { font-size: 10px; font-weight: normal; margin-left: 5px; color: #888; }
.signal-icon { font-size: 18px; color: #fff; }

.beeper-screen {
  background: #405040;
  border-radius: 5px;
  padding: 10px;
  border: 2px solid #2a332a;
  box-shadow: inset 0 0 5px rgba(0,0,0,0.8);
  margin-bottom: 15px;
  min-height: 150px;
  display: flex;
  flex-direction: column;
}

.screen-header {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #aaccbb;
  margin-bottom: 5px;
}

.message-display {
  flex-grow: 1;
  padding-top: 5px;
  font-family: 'VT323', monospace, 'Courier New';
  font-size: 24px;
  color: #c9ffc9;
}

.line {
  display: flex;
  margin-bottom: 5px;
  align-items: center;
}

.label {
  font-weight: bold;
  width: 30px;
  text-align: right;
  margin-right: 5px;
}

.screen-input {
  background: none;
  border: none;
  border-bottom: 1px dashed #c9ffc9;
  font-family: inherit;
  font-size: inherit;
  color: inherit;
  outline: none;
  padding: 0;
  box-sizing: border-box;
}
.screen-input.full { flex-grow: 1; }
.screen-input.short { width: 80px; text-align: center; }

.screen-input:-webkit-autofill,
.screen-input:-webkit-autofill:hover,
.screen-input:-webkit-autofill:focus,
.screen-input:-webkit-autofill:active {
  -webkit-box-shadow: 0 0 0px 1000px #405040 inset !important;
  -webkit-text-fill-color: #c9ffc9 !important;
  transition: background-color 5000s ease-in-out 0s;
}

.code-value {
  background: #384238;
  padding: 2px 5px;
  margin-left: 10px;
  cursor: pointer;
  border-radius: 2px;
}

.controls {
  margin-top: 15px;
  display: flex;
  gap: 10px;
  justify-content: space-between;
}

.mech-btn {
  flex-grow: 1;
  height: 40px;
  background: #2b2b2b;
  color: #c8c8c8;
  border: none;
  border-bottom: 4px solid #1a1a1a;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.1s;
  font-family: 'Arial', sans-serif;
  font-weight: bold;
  font-size: 14px;
  padding: 0 5px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.5);
}
.mech-btn:active {
  transform: translateY(2px);
  border-bottom: 2px solid #1a1a1a;
}
.read-btn { background: #007bff; border-bottom-color: #0056b3; color: #fff; }
.clear-btn { background: #dc3545; border-bottom-color: #a71d2a; color: #fff; }

.led-light {
  position: absolute;
  top: 15px;
  right: 15px;
  width: 8px;
  height: 8px;
  background: #330000;
  border-radius: 50%;
}
.led-light.on {
  background: #ffaa00;
  box-shadow: 0 0 5px #ffaa00, 0 0 10px #ffaa00;
}
</style>