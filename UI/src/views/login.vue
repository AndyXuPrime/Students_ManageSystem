<template>
  <div class="retro-container">
    <!-- 背景装饰 -->
    <div class="desk-mat"></div>

    <!-- 磁带机主体 -->
    <div class="walkman-body">
      <!-- 品牌/标题 -->
      <div class="brand-area">
        <span class="brand-name">SONY_SIMS</span>
        <span class="model-no">AUTO REVERSE / 2025</span>
      </div>

      <!-- 磁带仓窗口 -->
      <div class="cassette-window">
        <!-- 玻璃反光效果 -->
        <div class="glass-reflection"></div>

        <!-- 内部磁带 -->
        <div class="cassette-tape">
          <!-- 左卷轴 -->
          <div class="spool left" :class="{ 'spinning': isTyping }">
            <div class="spool-teeth"></div>
          </div>
          <!-- 右卷轴 -->
          <div class="spool right" :class="{ 'spinning': isTyping }">
            <div class="spool-teeth"></div>
          </div>

          <!-- 磁带标签（输入区域） -->
          <div class="tape-label">
            <div class="label-header">A SIDE [ STUDENT LOGIN ]</div>

            <!-- 用户名输入 -->
            <div class="input-line">
              <label>USER:</label>
              <input
                  type="text"
                  v-model="loginForm.username"
                  @focus="isTyping = true"
                  @blur="isTyping = false"
                  placeholder="Type username..."
              >
            </div>

            <!-- 密码输入 -->
            <div class="input-line">
              <label>PASS:</label>
              <input
                  type="password"
                  v-model="loginForm.password"
                  @focus="isTyping = true"
                  @blur="isTyping = false"
                  placeholder="******"
              >
            </div>

            <!-- 验证码输入 -->
            <div class="input-line captcha-line">
              <label>CODE:</label>
              <input
                  type="text"
                  v-model="loginForm.code"
                  @focus="isTyping = true"
                  @blur="isTyping = false"
                  class="short-input"
              >
              <!-- 验证码图片显示区 -->
              <div class="captcha-sticker" @click="loadCaptcha" title="Click to refresh">
                <img v-if="captchaUrl" :src="captchaUrl" alt="Tape Code" />
                <span v-else>LOADING</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部控制按键 -->
      <div class="control-deck">
        <!-- 登录按钮 (PLAY) -->
        <div class="btn-group">
          <button class="mech-btn play-btn" @click="handleLogin" :disabled="loading">
            <div class="triangle"></div>
          </button>
          <span class="btn-label">LOGIN</span>
        </div>

        <!-- 重置按钮 (STOP) -->
        <div class="btn-group">
          <button class="mech-btn stop-btn" @click="handleReset">
            <div class="square"></div>
          </button>
          <span class="btn-label">RESET</span>
        </div>

        <!-- 装饰按钮 (FF/RW) -->
        <div class="btn-group">
          <button class="mech-btn other-btn">
            <div class="icon-rw"></div>
          </button>
        </div>
        <div class="btn-group">
          <button class="mech-btn other-btn">
            <div class="icon-ff"></div>
          </button>
        </div>
      </div>

      <!-- 电源指示灯 -->
      <div class="power-led" :class="{ 'on': isTyping || loading }">
        <span class="led-light"></span>
        <span class="led-label">REC/BATT</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { getCaptcha, login } from '../api/auth' // 确保你的API路径正确
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const isTyping = ref(false)
const loading = ref(false)
const captchaUrl = ref('') // 这里实际存的是 Base64 图片

// 表单数据
const loginForm = reactive({
  username: '',
  password: '',
  code: '',
  uuid: ''
})

// 加载验证码
const loadCaptcha = async () => {
  try {
    const res = await getCaptcha()
    // 假设后端返回结构: { uuid: "...", code: "1234" }
    // 注意：如果后端返回的是验证码图片Base64，则赋值给 captchaUrl
    // 为了实验效果，这里假设后端直接返回了 code 文本，我们做一个假的图片展示效果
    // 实际项目中请让后端生成 Base64 图片流

    // 这里模拟生成一个假的验证码图片（实际请用 res.img）
    loginForm.uuid = res.uuid
    // 暂时把验证码填入 placeholder 或者 console 方便调试
    console.log('验证码:', res.code)

    // 模拟一个验证码图片 (实际开发中后端返回 img 字段: "data:image/png;base64,....")
    // 这里用 canvas 简单画一个，为了让你没后端图片也能看到效果
    captchaUrl.value = generateCanvasCaptcha(res.code)

  } catch (error) {
    console.error(error)
  }
}

// 简单的 Canvas 验证码生成器 (仅用于前端模拟展示，实际应由后端返回)
const generateCanvasCaptcha = (text) => {
  const canvas = document.createElement('canvas')
  canvas.width = 80
  canvas.height = 30
  const ctx = canvas.getContext('2d')
  ctx.fillStyle = '#ddd'
  ctx.fillRect(0, 0, 80, 30)
  ctx.font = '20px Courier New'
  ctx.fillStyle = '#333'
  ctx.fillText(text, 15, 22)
  return canvas.toDataURL()
}

// 登录逻辑
const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password || !loginForm.code) {
    ElMessage.warning('Please fill in all fields on the tape!')
    return
  }

  loading.value = true
  try {
    const res = await login(loginForm)
    // 登录成功
    ElMessage.success('Music Start! Login Success!')
    localStorage.setItem('token', res) // 假设返回的是 token 字符串
    router.push('/student')
  } catch (error) {
    // 失败刷新验证码
    loadCaptcha()
  } finally {
    loading.value = false
  }
}

// 重置
const handleReset = () => {
  loginForm.username = ''
  loginForm.password = ''
  loginForm.code = ''
}

onMounted(() => {
  loadCaptcha()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Courier+Prime:wght@400;700&display=swap');

/* 全局容器 */
.retro-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #2c3e50;
  background-image: radial-gradient(#34495e 15%, transparent 16%),
  radial-gradient(#34495e 15%, transparent 16%);
  background-size: 20px 20px;
  background-position: 0 0, 10px 10px;
  font-family: 'Courier Prime', monospace; /* 复古打字机字体 */
}

/* 随身听机身 */
.walkman-body {
  position: relative;
  width: 400px;
  height: 550px;
  background: #dcdde1;
  border-radius: 20px;
  box-shadow:
      inset 2px 2px 5px rgba(255,255,255,0.7),
      inset -5px -5px 10px rgba(0,0,0,0.2),
      15px 15px 30px rgba(0,0,0,0.5);
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  border: 4px solid #95a5a6;
}

/* 品牌区域 */
.brand-area {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 15px;
  padding: 0 10px;
  color: #2f3640;
}
.brand-name {
  font-size: 24px;
  font-weight: bold;
  font-style: italic;
  letter-spacing: 2px;
}
.model-no {
  font-size: 10px;
  color: #7f8fa6;
}

/* 磁带窗口 */
.cassette-window {
  position: relative;
  width: 340px;
  height: 220px;
  background: #2f3640;
  border-radius: 10px;
  border: 3px solid #576574;
  box-shadow: inset 0 0 20px rgba(0,0,0,0.8);
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 玻璃反光 */
.glass-reflection {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0) 40%, rgba(255,255,255,0.1) 100%);
  pointer-events: none;
  z-index: 10;
}

/* 磁带本体 */
.cassette-tape {
  width: 300px;
  height: 180px;
  background: #f5f6fa;
  border-radius: 10px;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 卷轴 */
.spool {
  position: absolute;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 4px solid #fff;
  background: #2f3640;
  top: 50%;
  transform: translateY(-50%);
  z-index: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}
.spool.left { left: 30px; }
.spool.right { right: 30px; }

.spool-teeth {
  width: 100%;
  height: 4px;
  background: #fff;
  position: absolute;
}
.spool-teeth::after {
  content: '';
  position: absolute;
  width: 100%;
  height: 100%;
  background: #fff;
  transform: rotate(90deg);
}

/* 卷轴转动动画 */
.spinning {
  animation: spin 2s linear infinite;
}
@keyframes spin {
  from { transform: translateY(-50%) rotate(0deg); }
  to { transform: translateY(-50%) rotate(360deg); }
}

/* 磁带贴纸（表单区域） */
.tape-label {
  width: 180px;
  height: 140px;
  background: #fbc531; /* 复古黄 */
  border-radius: 5px;
  z-index: 2;
  padding: 10px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.2);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.label-header {
  font-size: 10px;
  text-align: center;
  font-weight: bold;
  border-bottom: 2px solid #2f3640;
  margin-bottom: 5px;
  color: #2f3640;
}

/* 输入行样式 */
.input-line {
  display: flex;
  align-items: center;
  border-bottom: 1px dashed #2f3640;
  padding-bottom: 2px;
}

.input-line label {
  font-size: 12px;
  font-weight: bold;
  margin-right: 5px;
  color: #2f3640;
}

.input-line input {
  background: transparent;
  border: none;
  font-family: 'Courier Prime', monospace;
  font-size: 14px;
  color: #192a56; /* 像墨水一样的颜色 */
  width: 100%;
  outline: none;
}
.input-line input::placeholder {
  color: rgba(47, 54, 64, 0.4);
}

.captcha-line {
  position: relative;
}
.short-input {
  width: 60px !important;
}

/* 验证码贴纸 */
.captcha-sticker {
  position: absolute;
  right: 0;
  top: -5px;
  background: #fff;
  padding: 2px;
  border: 1px solid #ccc;
  transform: rotate(-5deg);
  cursor: pointer;
  box-shadow: 1px 1px 3px rgba(0,0,0,0.2);
}
.captcha-sticker img {
  height: 25px;
  display: block;
}

/* 底部控制台 */
.control-deck {
  margin-top: 40px;
  width: 100%;
  display: flex;
  justify-content: space-around;
  padding: 10px 20px;
  background: #d1d8e0;
  border-radius: 5px;
  box-shadow: inset 2px 2px 5px rgba(255,255,255,0.5), inset -2px -2px 5px rgba(0,0,0,0.1);
}

.btn-group {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.mech-btn {
  width: 50px;
  height: 50px;
  background: #ecf0f1;
  border: none;
  border-radius: 5px;
  box-shadow:
      0 5px 0 #bdc3c7,
      0 8px 5px rgba(0,0,0,0.2);
  cursor: pointer;
  position: relative;
  transition: all 0.1s;
  display: flex;
  justify-content: center;
  align-items: center;
}

.mech-btn:active {
  transform: translateY(5px);
  box-shadow: 0 0 0 #bdc3c7, inset 0 2px 5px rgba(0,0,0,0.2);
}

.play-btn { background: #e74c3c; box-shadow: 0 5px 0 #c0392b, 0 8px 5px rgba(0,0,0,0.2); }
.play-btn:active { box-shadow: 0 0 0 #c0392b, inset 0 2px 5px rgba(0,0,0,0.2); }

.triangle {
  width: 0;
  height: 0;
  border-top: 10px solid transparent;
  border-bottom: 10px solid transparent;
  border-left: 15px solid #fff;
}

.square {
  width: 15px;
  height: 15px;
  background: #7f8fa6;
}

.btn-label {
  font-size: 10px;
  font-weight: bold;
  color: #576574;
}

/* LED 灯 */
.power-led {
  margin-top: 30px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.led-light {
  width: 10px;
  height: 10px;
  background: #330000;
  border-radius: 50%;
  box-shadow: inset 1px 1px 2px rgba(0,0,0,0.5);
  transition: all 0.3s;
}
.power-led.on .led-light {
  background: #ff0000;
  box-shadow: 0 0 10px #ff0000, 0 0 20px #ff0000;
}
.led-label {
  font-size: 10px;
  color: #7f8fa6;
}

/* 禁用状态 */
button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
</style>