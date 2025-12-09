<template>
  <div class="retro-container">
    <!-- 背景装饰层 -->
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
          <!-- 添加 code-line 类实现斜体 -->
          <div class="line code-line">
            <span class="label">V:</span>
            <!-- 去掉了 short 类，改为 flex 布局自动填充 -->
            <input v-model="form.code" type="text" class="screen-input code-input" placeholder="ROBOT OK" maxlength="4" />
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
// 请确保路径正确，如果没有 api 文件，请注释掉下面这行
import { getCaptcha, login } from '../api/auth.js'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const captchaCode = ref('')
const form = ref({ username: '', password: '', code: '', uuid: '' })
const currentTime = ref('12:00A')

// 模拟 API (如果没有后端，可以使用这个测试)
// const getCaptcha = async () => ({ uuid: '1', code: Math.floor(1000 + Math.random() * 9000) })
// const login = async () => 'mock-token'

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
    await new Promise(r => setTimeout(r, 1000)) // 模拟网络延迟效果
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
/* =========================================
   1. 布局核心调整 (Alignment Fixes)
   ========================================= */

.line {
  display: flex;
  margin-bottom: 8px; /* 稍微增加行间距 */
  align-items: center;
}

.label {
  font-weight: bold;
  width: 30px; /* 固定宽度，确保 P, C, V 冒号垂直对齐 */
  text-align: right;
  margin-right: 8px;
  flex-shrink: 0; /* 防止标签被压缩 */
}

.screen-input {
  background: none;
  border: none;
  border-bottom: 1px dashed #c9ffc9; /* 统一的下划线风格 */
  font-family: inherit;
  font-size: inherit;
  color: inherit;
  outline: none;
  padding: 0 5px;
  box-sizing: border-box;
  height: 30px; /* 固定高度，防止抖动 */
}

/* 全宽输入框 (P和C) */
.screen-input.full {
  /* flex-grow: 1;  <-- 删掉这行，不要让它自动撑满 */
  width: 130px;     /* <-- 设置固定宽度，让虚线变短 */
}

/* 验证码输入框 (V) */
.screen-input.code-input {
  /* flex-grow: 1;  <-- 删掉这行 */
  width: 130px;     /* <-- 保持和上面一样的宽度，确保对齐 */
  text-align: left; /* 文字左对齐 */
  margin-right: 10px; /* 给右边的验证码数字留点空隙 */
}

/* 验证码数字块 */
.code-value {
  background: #384238;
  padding: 2px 8px;
  margin-left: 5px; /* 稍微减小一点间距 */
  cursor: pointer;
  border-radius: 2px;

  /* ✅ 斜体样式 */
  font-style: italic;

  font-weight: bold;
  letter-spacing: 1px;
  color: #fff;

  /* 防止验证码被挤压 */
  flex-shrink: 0;
}

.code-line {
  display: flex;
  margin-bottom: 5px;
  align-items: center;
}


/* =========================================
   2. 样式复位 (Reset & Chrome Fixes)
   ========================================= */
/* 解决 Chrome 自动填充背景色变白的问题 */
.screen-input:-webkit-autofill,
.screen-input:-webkit-autofill:hover,
.screen-input:-webkit-autofill:focus,
.screen-input:-webkit-autofill:active {
  -webkit-box-shadow: 0 0 0px 1000px #405040 inset !important;
  -webkit-text-fill-color: #c9ffc9 !important;
  transition: background-color 5000s ease-in-out 0s;
}

/* =========================================
   3. 寻呼机外壳与屏幕 (Beeper Shell)
   ========================================= */
.beeper-body {
  width: 300px;
  background: #444;
  border-radius: 15px;
  padding: 15px;
  box-shadow:
      inset 0 0 10px rgba(0,0,0,0.8),
      0 20px 50px rgba(0,0,0,0.8);
  border: 3px solid #666;
  position: relative;
  font-family: 'Courier New', monospace;
  z-index: 10;
  transform: scale(1.05);
}

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

/* =========================================
   4. 按钮与灯光 (Controls)
   ========================================= */
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

/* =========================================
   5. 背景特效 (Background FX)
   ========================================= */
.retro-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #0d1117;
  overflow: hidden;
  position: relative;
  perspective: 1000px;
}

.atom-background {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 0;
  background: radial-gradient(circle at center, #1a2a3a 0%, #050505 80%);
}

.grid-floor, .grid-ceil {
  position: absolute; width: 200%; height: 50%; left: -50%;
  background-image: linear-gradient(rgba(0, 255, 200, 0.2) 1px, transparent 1px), linear-gradient(90deg, rgba(0, 255, 200, 0.2) 1px, transparent 1px);
  background-size: 50px 50px;
  transform-style: preserve-3d;
}
.grid-floor { bottom: 0; transform: rotateX(60deg); box-shadow: inset 0 100px 100px #000; animation: gridMove 20s linear infinite; }
.grid-ceil { top: 0; transform: rotateX(-60deg); opacity: 0.3; box-shadow: inset 0 -100px 100px #000; animation: gridMove 20s linear infinite reverse; }

.atom-core { position: absolute; top: 50%; left: 50%; width: 600px; height: 600px; transform: translate(-50%, -50%); pointer-events: none; opacity: 0.6; }
.nucleus { position: absolute; top: 50%; left: 50%; width: 40px; height: 40px; background: #00ffcc; border-radius: 50%; transform: translate(-50%, -50%); box-shadow: 0 0 30px #00ffcc, 0 0 60px #00ffcc; animation: pulse 3s ease-in-out infinite; }
.ring { position: absolute; top: 50%; left: 50%; border: 2px solid rgba(0, 255, 204, 0.4); border-radius: 50%; transform: translate(-50%, -50%); box-shadow: 0 0 10px rgba(0, 255, 204, 0.1); }
.ring-1 { width: 200px; height: 500px; animation: orbit1 8s linear infinite; }
.ring-2 { width: 400px; height: 200px; animation: orbit2 12s linear infinite; }
.ring-3 { width: 350px; height: 350px; animation: orbit3 10s linear infinite; border: 1px dashed rgba(255, 165, 0, 0.3); }

.radar-scan {
  position: absolute; top: 50%; left: 50%; width: 100vw; height: 100vh;
  background: conic-gradient(from 0deg, transparent 0deg, transparent 300deg, rgba(0, 255, 200, 0.05) 360deg);
  transform: translate(-50%, -50%); border-radius: 50%; animation: radar 5s linear infinite; pointer-events: none;
}

.crt-overlay {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 999;
  background: linear-gradient(rgba(18, 16, 16, 0) 50%, rgba(0, 0, 0, 0.1) 50%);
  background-size: 100% 4px; pointer-events: none; box-shadow: inset 0 0 100px rgba(0,0,0,0.9); opacity: 0.4;
}

@keyframes gridMove { 0% { background-position: 0 0; } 100% { background-position: 0 50px; } }
@keyframes orbit1 { from { transform: translate(-50%, -50%) rotate(0deg); } to { transform: translate(-50%, -50%) rotate(360deg); } }
@keyframes orbit2 { from { transform: translate(-50%, -50%) rotate(45deg); } to { transform: translate(-50%, -50%) rotate(405deg); } }
@keyframes orbit3 { from { transform: translate(-50%, -50%) rotate(-45deg); } to { transform: translate(-50%, -50%) rotate(-405deg); } }
@keyframes pulse { 0%, 100% { transform: translate(-50%, -50%) scale(1); opacity: 0.8; } 50% { transform: translate(-50%, -50%) scale(1.2); opacity: 1; } }
@keyframes radar { from { transform: translate(-50%, -50%) rotate(0deg); } to { transform: translate(-50%, -50%) rotate(360deg); } }
</style>