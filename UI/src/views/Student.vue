<template>
  <div class="retro-poster-container">
    <!-- 噪点纹理层 -->
    <div class="noise-overlay"></div>

    <!-- 装饰性背景圆环 -->
    <div class="bg-circle circle-1"></div>
    <div class="bg-circle circle-2"></div>

    <!-- 主面板容器 -->
    <div class="main-panel">

      <!-- 顶部状态栏 -->
      <div class="panel-header">
        <div class="header-decoration">
          <span class="dot red"></span>
          <span class="dot yellow"></span>
          <span class="dot green"></span>
        </div>
        <div class="header-title-box">
          <h1 class="main-title">STUDENT_DATABASE</h1>
          <span class="sub-title">v.2025 // SYS.ADMIN</span>
        </div>
        <div class="header-time-box">
          <span class="time-label">TIME_CYCLE</span>
          <span class="time-value">{{ timeStr }}</span>
        </div>
      </div>

      <!-- 控制台区域 -->
      <div class="control-deck">
        <!-- 搜索栏 -->
        <div class="search-module">
          <div class="module-label">QUERY_INPUT</div>
          <div class="input-group">
            <input
                v-model="queryParams.name"
                class="retro-input"
                placeholder="ENTER ID OR NAME..."
                @keyup.enter="handleQuery"
            />
            <button class="retro-btn primary" @click="handleQuery">SEARCH</button>
            <button class="retro-btn warning" @click="handleAdd">NEW_ENTRY</button>
          </div>
        </div>
      </div>

      <!-- 数据显示区域 -->
      <div class="data-viewport">
        <el-table
            :data="tableData"
            class="retro-table"
            v-loading="loading"
            element-loading-background="rgba(30, 39, 46, 0.8)"
        >
          <el-table-column prop="sno" label="ID_NO" width="100" align="center"/>

          <el-table-column prop="sname" label="IDENTITY" width="120" align="center">
            <template #default="scope">
              <span class="highlight-text">{{ scope.row.sname }}</span>
            </template>
          </el-table-column>

          <el-table-column label="VISUAL" width="90" align="center">
            <template #default="scope">
              <div class="retro-avatar" :class="scope.row.sex === '男' ? 'av-cyan' : 'av-orange'">
                {{ scope.row.sname ? scope.row.sname.charAt(0) : '?' }}
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="sex" label="GENDER" width="90" align="center">
            <template #default="scope">
              <span class="gender-tag" :class="scope.row.sex === '男' ? 'tag-male' : 'tag-female'">
                {{ scope.row.sex === '男' ? 'M' : 'F' }}
              </span>
            </template>
          </el-table-column>

          <el-table-column prop="birth" label="BIRTH_DATE" width="120" />
          <el-table-column prop="classno" label="CLASS" width="80" align="center"/>
          <el-table-column prop="sdept" label="DEPARTMENT" show-overflow-tooltip header-align="center"/>
          <el-table-column prop="postcode" label="ZIP" width="90" align="center"/>
          <el-table-column prop="homeAddr" label="COORDINATES" show-overflow-tooltip align="center"/>

          <el-table-column label="ACTIONS" width="160" fixed="right" align="center">
            <template #default="scope">
              <div class="action-group">
                <button class="icon-btn edit" @click="handleEdit(scope.row)">✎</button>
                <button class="icon-btn del" @click="handleDelete(scope.row.sno)">✖</button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="panel-footer">
          <el-pagination
              v-model:current-page="queryParams.current"
              v-model:page-size="queryParams.size"
              :page-sizes="[5, 10, 20, 50]"
              layout="prev, pager, next"
              :total="total"
              @size-change="getList"
              @current-change="getList"
              class="retro-pagination"
          />
        </div>
      </div>
    </div>

    <!-- 弹窗 -->
    <el-dialog
        :title="dialog.title"
        v-model="dialog.visible"
        width="450px"
        @close="resetForm"
        class="retro-dialog"
        :show-close="false"
    >
      <div class="dialog-stripe"></div>
      <el-form :model="form" ref="formRef" label-width="90px" :rules="rules" class="retro-form">
        <el-form-item label="ID_NO" prop="sno">
          <el-input v-model="form.sno" :disabled="!!form.sno && dialog.title === '修改学生'" class="retro-form-input"/>
        </el-form-item>
        <el-form-item label="NAME" prop="sname">
          <el-input v-model="form.sname" class="retro-form-input"/>
        </el-form-item>
        <el-form-item label="GEN" prop="sex">
          <el-radio-group v-model="form.sex" class="retro-radio-group">
            <el-radio label="男" border>MALE</el-radio>
            <el-radio label="女" border>FEMALE</el-radio>
          </el-radio-group>
        </el-form-item>
        <div class="form-row">
          <el-form-item label="BIRTH" prop="birth" style="width: 50%">
            <el-date-picker v-model="form.birth" type="date" value-format="YYYY-MM-DD" class="retro-date"/>
          </el-form-item>
          <el-form-item label="ENTRY" prop="entranceDate" style="width: 50%">
            <el-date-picker v-model="form.entranceDate" type="date" value-format="YYYY-MM-DD" class="retro-date"/>
          </el-form-item>
        </div>
        <el-form-item label="CLASS" prop="classno">
          <el-input v-model="form.classno" class="retro-form-input"/>
        </el-form-item>
        <el-form-item label="DEPT" prop="sdept">
          <el-input v-model="form.sdept" class="retro-form-input"/>
        </el-form-item>
        <el-form-item label="ZIP" prop="postcode">
          <el-input v-model="form.postcode" class="retro-form-input"/>
        </el-form-item>
        <el-form-item label="ADDR" prop="homeAddr">
          <el-input v-model="form.homeAddr" class="retro-form-input"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-actions">
          <button class="retro-btn ghost" @click="dialog.visible = false">ABORT</button>
          <button class="retro-btn primary" @click="submitForm">EXECUTE</button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// --- Script 部分保持 100% 原样，未做任何修改 ---
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { getStudentPage, addStudent, updateStudent, deleteStudent } from '../api/student.js'
import { ElMessage, ElMessageBox } from 'element-plus'

/*Mock API for styling preview
const getStudentPage = async () => ({ content: [{sno:'1001', sname:'Alex', sex:'男', birth:'2000-01-01', classno:'A1', sdept:'CS', postcode:'1000', homeAddr:'Earth'}], totalElements: 10 })
const addStudent = async () => {}
const updateStudent = async () => {}
const deleteStudent = async () => {}
*/
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  current: 1,
  size: 10,
  name: ''
})

const dialog = reactive({
  visible: false,
  title: ''
})

const form = ref({})
const formRef = ref(null)

const rules = {
  sno: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  sname: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  sex: [{ required: true, message: '请选择性别', trigger: 'change' }]
}

const timeStr = ref('')
let timer = null
const updateTime = () => {
  const now = new Date()
  timeStr.value = now.toLocaleTimeString('en-US', { hour12: false })
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getStudentPage(queryParams)
    tableData.value = res.content
    total.value = res.totalElements
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.current = 1
  getList()
}

const handleAdd = () => {
  dialog.title = '新增学生'
  dialog.visible = true
  form.value = { sex: '男' }
}

const handleEdit = (row) => {
  dialog.title = '修改学生'
  dialog.visible = true
  form.value = JSON.parse(JSON.stringify(row))
}

const handleDelete = (sno) => {
  ElMessageBox.confirm('确认删除该数据块吗 (DELETE BLOCK)?', 'WARNING', {
    confirmButtonText: 'EXECUTE',
    cancelButtonText: 'ABORT',
    type: 'warning',
    customClass: 'retro-message-box'
  }).then(async () => {
    await deleteStudent(sno)
    ElMessage.success('DELETED SUCCESS')
    getList()
  })
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      if (dialog.title === '新增学生') {
        await addStudent(form.value)
        ElMessage.success('INSERTED SUCCESS')
      } else {
        await updateStudent(form.value)
        ElMessage.success('UPDATED SUCCESS')
      }
      dialog.visible = false
      getList()
    }
  })
}

const resetForm = () => {
  form.value = {}
  if(formRef.value) formRef.value.resetFields()
}

onMounted(() => {
  getList()
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if(timer) clearInterval(timer)
})
</script>

<style>
/* ==========================================================================
   COLOR PALETTE (Based on Image)
   ========================================================================== */
:root {
  --c-bg-dark: #2c3e50;    /* 深蓝灰 (背景) */
  --c-danger: #c0392b;     /* 焦糖红 (危险/删除) */
  --c-highlight: #f1c40f;  /* 明黄 (高亮/输入框) */
  --c-accent: #e67e22;     /* 暖橙 (边框/次要) */
  --c-primary: #1abc9c;    /* 复古青 (主色/文字) */
  --c-muted: #7f8c8d;      /* 橄榄褐 (边框/分割线) */
  --c-text-light: #ecf0f1; /* 米白 (正文) */
  --font-display: 'Impact', 'Arial Black', sans-serif;
  --font-mono: 'Courier New', monospace;
}

/* ==========================================================================
   1. GLOBAL CONTAINER & BACKGROUND
   ========================================================================== */
.retro-poster-container {
  min-height: 100vh;
  background-color: var(--c-bg-dark);
  color: var(--c-text-light);
  font-family: var(--font-mono);
  position: relative;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

/* 噪点纹理 - 增加复古纸张感 */
.noise-overlay {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: url('data:image/svg+xml;utf8,%3Csvg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg"%3E%3Cfilter id="noiseFilter"%3E%3CfeTurbulence type="fractalNoise" baseFrequency="0.65" numOctaves="3" stitchTiles="stitch"/%3E%3C/filter%3E%3Crect width="100%25" height="100%25" filter="url(%23noiseFilter)" opacity="0.05"/%3E%3C/svg%3E');
  pointer-events: none;
  z-index: 1;
}

/* 装饰性背景圆 (海报风格) */
.bg-circle {
  position: absolute;
  border-radius: 50%;
  z-index: 0;
}
.circle-1 {
  width: 600px; height: 600px;
  background: var(--c-primary);
  top: -200px; right: -100px;
  opacity: 0.1;
}
.circle-2 {
  width: 400px; height: 400px;
  background: var(--c-accent);
  bottom: -100px; left: -100px;
  opacity: 0.1;
}

/* ==========================================================================
   2. MAIN PANEL (The Interface)
   ========================================================================== */
.main-panel {
  width: 100%;
  max-width: 1200px;
  background: rgba(30, 39, 46, 0.95);
  border: 4px solid var(--c-muted);
  border-radius: 20px;
  box-shadow: 15px 15px 0px rgba(0,0,0,0.3); /* 硬阴影 */
  z-index: 10;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部 Header */
.panel-header {
  background: var(--c-accent);
  padding: 15px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 4px solid var(--c-bg-dark);
}

.header-decoration { display: flex; gap: 8px; }
.dot { width: 12px; height: 12px; border-radius: 50%; border: 2px solid rgba(0,0,0,0.2); }
.dot.red { background: var(--c-danger); }
.dot.yellow { background: var(--c-highlight); }
.dot.green { background: var(--c-primary); }

.header-title-box { text-align: center; color: var(--c-bg-dark); }
.main-title {
  margin: 0; font-family: var(--font-display); font-size: 32px; letter-spacing: 2px; text-transform: uppercase;
}
.sub-title { font-size: 12px; font-weight: bold; opacity: 0.8; }

.header-time-box {
  background: var(--c-bg-dark);
  padding: 5px 15px;
  border-radius: 20px;
  border: 2px solid var(--c-highlight);
  color: var(--c-highlight);
  font-weight: bold;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 100px;
}
.time-label { font-size: 10px; color: var(--c-muted); }
.time-value { font-size: 18px; }

/* ==========================================================================
   3. CONTROLS & INPUTS
   ========================================================================== */
.control-deck {
  padding: 20px 30px;
  background: #25303b;
  border-bottom: 2px dashed var(--c-muted);
}

.search-module { display: flex; flex-direction: column; gap: 5px; }
.module-label { font-size: 12px; color: var(--c-primary); letter-spacing: 1px; font-weight: bold; }

.input-group { display: flex; gap: 15px; }

.retro-input {
  flex: 1;
  background: var(--c-bg-dark);
  border: 2px solid var(--c-muted);
  color: var(--c-highlight);
  padding: 10px 15px;
  font-family: var(--font-mono);
  font-size: 16px;
  border-radius: 8px;
  outline: none;
  transition: all 0.3s;
}
.retro-input:focus {
  border-color: var(--c-highlight);
  box-shadow: 0 0 10px rgba(241, 196, 15, 0.2);
}

/* 按钮样式 */
.retro-btn {
  border: none;
  padding: 0 25px;
  font-family: var(--font-display);
  font-size: 16px;
  cursor: pointer;
  border-radius: 8px;
  transition: transform 0.1s;
  text-transform: uppercase;
  letter-spacing: 1px;
}
.retro-btn:active { transform: scale(0.95); }

.retro-btn.primary {
  background: var(--c-primary);
  color: var(--c-bg-dark);
  box-shadow: 0 4px 0 #16a085;
}
.retro-btn.warning {
  background: var(--c-highlight);
  color: var(--c-bg-dark);
  box-shadow: 0 4px 0 #f39c12;
}
.retro-btn.ghost {
  background: transparent;
  border: 2px solid var(--c-muted);
  color: var(--c-muted);
}
.retro-btn.ghost:hover { border-color: var(--c-text-light); color: var(--c-text-light); }

/* ==========================================================================
   4. DATA TABLE (Deep Customization)
   ========================================================================== */
.data-viewport { padding: 20px 30px; flex: 1; overflow: auto; }

.retro-table {
  --el-table-bg-color: transparent !important;
  --el-table-tr-bg-color: transparent !important;
  --el-table-header-bg-color: transparent !important;
  --el-table-row-hover-bg-color: rgba(26, 188, 156, 0.1) !important;
  --el-table-border-color: var(--c-muted) !important;
  --el-table-text-color: var(--c-text-light) !important;
  --el-table-header-text-color: var(--c-accent) !important;

  font-family: var(--font-mono);
  border: 2px solid var(--c-muted) !important;
  border-radius: 10px;
  overflow: hidden;
}

.retro-table th {
  border-bottom: 2px solid var(--c-muted) !important;
  text-transform: uppercase;
  font-size: 14px;
  letter-spacing: 1px;
}

.retro-table td {
  border-bottom: 1px dashed rgba(127, 140, 141, 0.3) !important;
  font-size: 15px;
}

/* 表格内元素 */
.highlight-text { color: var(--c-highlight); font-weight: bold; }

.retro-avatar {
  width: 30px; height: 30px;
  border-radius: 6px;
  display: flex; align-items: center; justify-content: center;
  font-weight: bold; color: #fff;
}
.av-cyan { background: var(--c-primary); }
.av-orange { background: var(--c-accent); }

.gender-tag {
  font-size: 12px; padding: 2px 6px; border-radius: 4px; border: 1px solid;
}
.tag-male { color: var(--c-primary); border-color: var(--c-primary); }
.tag-female { color: var(--c-highlight); border-color: var(--c-highlight); }

.action-group { display: flex; justify-content: center; gap: 10px; }
.icon-btn {
  width: 30px; height: 30px;
  border-radius: 50%; border: none;
  cursor: pointer; display: flex; align-items: center; justify-content: center;
  font-size: 14px; transition: all 0.2s;
}
.icon-btn.edit { background: var(--c-primary); color: var(--c-bg-dark); }
.icon-btn.del { background: var(--c-danger); color: #fff; }
.icon-btn:hover { transform: translateY(-2px); box-shadow: 0 2px 5px rgba(0,0,0,0.5); }

/* 分页 */
.panel-footer { margin-top: 20px; display: flex; justify-content: flex-end; }
.retro-pagination {
  --el-pagination-bg-color: transparent !important;
  --el-pagination-text-color: var(--c-muted) !important;
  --el-pagination-button-color: var(--c-muted) !important;
  --el-disabled-bg-color: transparent !important;
}
.retro-pagination .el-pager li.is-active {
  color: var(--c-bg-dark) !important;
  background: var(--c-highlight) !important;
  font-weight: bold;
  border-radius: 4px;
}

/* ==========================================================================
   5. DIALOG & FORM
   ========================================================================== */
.retro-dialog {
  background: var(--c-bg-dark) !important;
  border: 4px solid var(--c-highlight) !important;
  border-radius: 15px !important;
  box-shadow: 20px 20px 0 rgba(0,0,0,0.5) !important;
}
.retro-dialog .el-dialog__header {
  background: var(--c-highlight);
  margin-right: 0; padding: 15px;
}
.retro-dialog .el-dialog__title {
  color: var(--c-bg-dark) !important;
  font-family: var(--font-display);
  text-transform: uppercase;
  font-size: 24px;
}

.dialog-stripe {
  height: 10px; background: repeating-linear-gradient(45deg, var(--c-accent), var(--c-accent) 10px, var(--c-bg-dark) 10px, var(--c-bg-dark) 20px);
  margin-bottom: 20px;
}

.retro-form .el-form-item__label {
  color: var(--c-primary) !important;
  font-family: var(--font-mono);
}

/* 输入框覆写 */
.retro-form-input .el-input__wrapper,
.retro-date .el-input__wrapper {
  background-color: rgba(0,0,0,0.2) !important;
  box-shadow: 0 0 0 1px var(--c-muted) inset !important;
  border-radius: 4px;
}
.retro-form-input .el-input__wrapper.is-focus {
  box-shadow: 0 0 0 2px var(--c-highlight) inset !important;
}
.retro-form-input input { color: #fff !important; font-family: var(--font-mono); }

/* 单选框 */
.retro-radio-group .el-radio.is-bordered {
  border-color: var(--c-muted);
  color: var(--c-muted);
}
.retro-radio-group .el-radio.is-bordered.is-checked {
  border-color: var(--c-highlight);
  background: rgba(241, 196, 15, 0.1);
}
.retro-radio-group .el-radio__label { color: var(--c-text-light) !important; }
.retro-radio-group .el-radio__input.is-checked .el-radio__inner {
  border-color: var(--c-highlight); background: var(--c-highlight);
}

.dialog-actions { display: flex; justify-content: flex-end; gap: 10px; padding-top: 10px; border-top: 1px solid var(--c-muted); }

/* ==========================================================================
   6. MESSAGE BOX
   ========================================================================== */
.retro-message-box {
  background: var(--c-bg-dark) !important;
  border: 4px solid var(--c-danger) !important;
  border-radius: 10px;
}
.retro-message-box .el-message-box__title { color: var(--c-danger) !important; font-family: var(--font-display); }
.retro-message-box .el-message-box__message { color: var(--c-text-light) !important; }
.retro-message-box .el-button--primary {
  background: var(--c-danger) !important; border-color: var(--c-danger) !important;
}
</style>