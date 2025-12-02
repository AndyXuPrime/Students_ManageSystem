<template>
  <div class="cyber-container">
    <!-- CRT 扫描线滤镜 (纯视觉效果) -->
    <div class="crt-overlay"></div>

    <!-- 顶部状态栏 -->
    <div class="pager-header">
      <div class="header-left">
        <span class="blink">> SYSTEM_READY</span>
        <span class="header-title">STUDENT_DATABASE</span>
      </div>
      <div class="header-right">
        <span style="margin-left: 15px">{{ timeStr }}</span>
      </div>
    </div>

    <!-- 主操作终端区域 -->
    <div class="terminal-body">

      <!-- 搜索栏 -->
      <div class="command-line">
        <span class="prompt">QUERY_COMMAND:</span>
        <input
            v-model="queryParams.name"
            class="cyber-input"
            placeholder="ENTER_NAME..."
            @keyup.enter="handleQuery"
        />
        <button class="cyber-btn" @click="handleQuery">搜索</button>
        <button class="cyber-btn insert" @click="handleAdd">新建</button>
      </div>

      <!-- 表格 -->
      <el-table
          :data="tableData"
          class="cyber-table"
          v-loading="loading"
          element-loading-background="rgba(0, 0, 0, 0.9)"
          element-loading-text="LOADING_DATA..."
      >
        <el-table-column prop="sno" label="ID_NO" width="100" align="center"/>

        <el-table-column prop="sname" label="NAME" width="100" align="center">
          <template #default="scope">
            <span class="neon-text">{{ scope.row.sname }}</span>
          </template>
        </el-table-column>

        <el-table-column label="AVATAR" width="95" align="center">
          <template #default="scope">
            <div class="pixel-avatar">
              {{ scope.row.sname ? scope.row.sname.charAt(0) : '?' }}
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="sex" label="SEX" width="80" align="center">
          <template #default="scope">
            <span :class="scope.row.sex === '男' ? 'color-cyan' : 'color-pink'">
              {{ scope.row.sex === '男' ? 'MALE' : 'FEMALE' }}
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="birth" label="BIRTH_DATE" width="120" />
        <el-table-column prop="entranceDate" label="ENTRY_DATE" width="120" />
        <el-table-column prop="classno" label="CLASS_ID" width="100" align="center"/>
        <el-table-column prop="sdept" label="DEPT" show-overflow-tooltip header-align="center"/>

        <!-- ✅ 表格中包含邮编列 -->
        <el-table-column prop="postcode" label="POST_CODE" width="110" align="center"/>

        <el-table-column prop="homeAddr" label="ADDRESS" show-overflow-tooltip align="center"/>

        <el-table-column label="OPERATION" width="180" fixed="right" align="center">
          <template #default="scope">
            <button class="text-btn" @click="handleEdit(scope.row)">[EDIT]</button>
            <button class="text-btn danger" @click="handleDelete(scope.row.sno)">[DEL]</button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pager-footer">
        <el-pagination
            v-model:current-page="queryParams.current"
            v-model:page-size="queryParams.size"
            :page-sizes="[5, 10, 20, 50]"
            layout="prev, pager, next"
            :total="total"
            @size-change="getList"
            @current-change="getList"
            class="cyber-pagination"
        />
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
        :title="dialog.title"
        v-model="dialog.visible"
        width="500px"
        @close="resetForm"
        custom-class="cyber-dialog"
        :close-on-click-modal="false"
        :modal-class="'cyber-modal-mask'"
    >
      <el-form :model="form" ref="formRef" label-width="100px" :rules="rules" class="cyber-form">
        <el-form-item label="ID_NO" prop="sno">
          <el-input v-model="form.sno" :disabled="!!form.sno && dialog.title === '修改学生'" placeholder="8 CHARS" maxlength="8" class="cyber-input-inner"/>
        </el-form-item>
        <el-form-item label="NAME" prop="sname">
          <el-input v-model="form.sname" placeholder="Max lenth 8" class="cyber-input-inner"/>
        </el-form-item>
        <el-form-item label="GENDER" prop="sex">
          <el-radio-group v-model="form.sex">
            <el-radio label="男" class="cyber-radio">MALE</el-radio>
            <el-radio label="女" class="cyber-radio">FEMALE</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="BIRTH" prop="birth">
          <el-date-picker v-model="form.birth" type="date" value-format="YYYY-MM-DD" placeholder="YYYY-MM-DD" style="width: 100%" class="cyber-date"/>
        </el-form-item>
        <el-form-item label="ENTRY" prop="entranceDate">
          <el-date-picker v-model="form.entranceDate" type="date" value-format="YYYY-MM-DD" placeholder="YYYY-MM-DD" style="width: 100%" class="cyber-date"/>
        </el-form-item>
        <el-form-item label="CLASS_ID" prop="classno">
          <el-input v-model="form.classno" placeholder="e.g. 101" maxlength="3" class="cyber-input-inner"/>
        </el-form-item>
        <el-form-item label="DEPT" prop="sdept">
          <el-input v-model="form.sdept" class="cyber-input-inner"/>
        </el-form-item>

        <!-- ✅ 弹窗中包含邮编输入框 -->
        <el-form-item label="POSTCODE" prop="postcode">
          <el-input v-model="form.postcode" maxlength="6" placeholder="6 DIGITS" class="cyber-input-inner"/>
        </el-form-item>

        <el-form-item label="ADDR" prop="homeAddr">
          <el-input v-model="form.homeAddr" class="cyber-input-inner"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <button class="cyber-btn" @click="dialog.visible = false">CANCEL</button>
          <button class="cyber-btn insert" @click="submitForm">CONFIRM</button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { getStudentPage, addStudent, updateStudent, deleteStudent } from '../api/student.js'
import { ElMessage, ElMessageBox } from 'element-plus'

// --- 业务逻辑保持原样 ---
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

// --- 增加时钟逻辑 ---
const timeStr = ref('')
let timer = null
const updateTime = () => {
  const now = new Date()
  timeStr.value = now.toLocaleTimeString('en-US', { hour12: false })
}

// --- 业务方法 ---
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
    customClass: 'cyber-message-box'
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
/* 1. 容器与背景 */
.cyber-container {
  min-height: 100vh;
  background-color: #000;
  color: #0f0;
  font-family: 'VT323', monospace;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* 2. CRT 扫描线效果 */
.crt-overlay {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  background: linear-gradient(rgba(18, 16, 16, 0) 50%, rgba(0, 0, 0, 0.25) 50%), linear-gradient(90deg, rgba(255, 0, 0, 0.06), rgba(0, 255, 0, 0.02), rgba(0, 0, 255, 0.06));
  background-size: 100% 2px, 3px 100%;
  pointer-events: none;
  z-index: 999;
}

/* 3. 顶部状态栏 */
.pager-header {
  border-bottom: 2px solid #0f0;
  display: flex;
  justify-content: space-between;
  padding: 10px;
  font-size: 24px;
  text-shadow: 0 0 5px #0f0;
  margin-bottom: 20px;
  background: #001100;
}
.blink { animation: blinker 1s linear infinite; }
@keyframes blinker { 50% { opacity: 0; } }

/* 4. 命令行搜索栏 */
.command-line {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  background: #001100;
  padding: 15px;
  border: 1px dashed #0f0;
}
.prompt { color: #0f0; font-size: 20px; font-weight: bold; }

.cyber-input {
  background: transparent;
  border: none;
  border-bottom: 2px solid #0f0;
  color: #fff;
  font-family: 'VT323', monospace;
  font-size: 20px;
  flex: 1;
  outline: none;
  padding: 5px;
}
.cyber-input:focus { border-bottom-color: #f0f; box-shadow: 0 5px 5px -5px #f0f; }

/* 5. 赛博按钮 */
.cyber-btn {
  background: #000;
  border: 2px solid #0f0;
  color: #0f0;
  padding: 5px 20px;
  font-family: 'VT323', monospace;
  font-size: 18px;
  cursor: pointer;
  text-transform: uppercase;
  box-shadow: 0 0 5px #0f0;
  transition: all 0.2s;
}
.cyber-btn:hover { background: #0f0; color: #000; }
.cyber-btn:active { transform: translateY(2px); }
.cyber-btn.insert { border-color: #f0f; color: #f0f; box-shadow: 0 0 5px #f0f; }
.cyber-btn.insert:hover { background: #f0f; color: #fff; }

/* 6. 表格深度改造 */
.cyber-table {
  --el-table-bg-color: transparent !important;
  --el-table-tr-bg-color: transparent !important;
  --el-table-header-bg-color: #002200 !important;
  --el-table-row-hover-bg-color: #003300 !important;
  --el-table-border-color: #0f0 !important;
  --el-table-text-color: #0f0 !important;
  --el-table-header-text-color: #0f0 !important;
  font-family: 'VT323', monospace !important;
  font-size: 18px !important;
  border: 1px solid #0f0 !important;
  box-shadow: 0 0 10px rgba(0, 255, 0, 0.2);
}
.cyber-table::before { display: none; }
.cyber-table th, .cyber-table td { border-bottom: 1px solid #004400 !important; }
.neon-text { color: #0ff; text-shadow: 0 0 5px #0ff; }
.color-cyan { color: #0ff; }
.color-pink { color: #f0f; }
.pixel-avatar {
  width: 30px; height: 30px;
  background: #f0f;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: 2px 2px 0 #fff;
  font-weight: bold;
}
.text-btn { background: none; border: none; color: #0ff; cursor: pointer; font-family: 'VT323'; font-size: 18px; margin-right: 10px; }
.text-btn:hover { text-decoration: underline; text-shadow: 0 0 5px #0ff; }
.text-btn.danger { color: #f00; }
.text-btn.danger:hover { text-shadow: 0 0 5px #f00; }

/* 7. 分页样式 */
.pager-footer { margin-top: 20px; display: flex; justify-content: flex-end; }
.cyber-pagination {
  --el-pagination-bg-color: transparent !important;
  --el-pagination-text-color: #0f0 !important;
  --el-pagination-button-color: #0f0 !important;
  --el-pagination-button-disabled-bg-color: transparent !important;
  --el-disabled-bg-color: transparent !important;
}
.cyber-pagination .el-pager li { background: transparent !important; border: 1px solid #004400; margin: 0 2px; }
.cyber-pagination .el-pager li.is-active { color: #000 !important; background: #0f0 !important; border-color: #0f0; }

/* =================================================================
   8. 弹窗改造 (重点优化对比度)
   ================================================================= */

/* 遮罩层：加深，突出弹窗 */
.cyber-modal-mask {
  background-color: rgba(0, 0, 0, 0.85) !important;
  backdrop-filter: blur(2px);
}

.cyber-dialog {
  background: #0d0d0d !important; /* 稍微提亮背景，区分于纯黑底色 */
  border: 2px solid #0f0 !important; /* 强荧光绿边框 */
  box-shadow: 0 0 30px rgba(0, 255, 0, 0.3) !important;
}

.cyber-dialog .el-dialog__title {
  color: #0f0 !important;
  font-family: 'VT323', monospace;
  font-size: 28px;
  text-shadow: 0 0 5px #0f0; /* 标题增加发光 */
}
.cyber-dialog .el-dialog__headerbtn .el-dialog__close { color: #0f0 !important; font-size: 24px; }

/* 表单 Label：增加亮度 */
.cyber-form .el-form-item__label {
  color: #0f0 !important;
  font-family: 'VT323', monospace;
  font-size: 22px;
  font-weight: bold;
  text-shadow: 1px 1px 0 #000;
}

/* 输入框核心优化 */
.cyber-input-inner .el-input__wrapper {
  /* 背景改为深墨绿，不再是纯黑，提升层次感 */
  background-color: #001a00 !important;
  /* 边框改为亮绿色，不再是暗绿 */
  box-shadow: 0 0 0 1px #0f0 inset !important;
  border-radius: 0 !important;
}

.cyber-input-inner .el-input__wrapper.is-focus {
  /* 聚焦时边框变粉色，增强交互反馈 */
  box-shadow: 0 0 0 2px #f0f inset !important;
  background-color: #000 !important;
}

/* 输入文字：加粗纯白 */
.cyber-input-inner input {
  color: #fff !important;
  font-family: 'VT323', monospace;
  font-size: 20px;
  font-weight: bold;
  letter-spacing: 1px;
}

/* 关键：优化 Placeholder (提示文字) 颜色，防止太暗看不清 */
.cyber-input-inner input::placeholder {
  color: #008800 !important; /* 半亮绿色 */
  opacity: 0.8;
}

/* 单选框优化 */
.cyber-radio .el-radio__label {
  color: #fff !important; /* 选项文字改为白色 */
  font-family: 'VT323', monospace;
  font-size: 18px;
}
.cyber-radio .el-radio__inner {
  border-color: #0f0;
  background: transparent;
  width: 18px; height: 18px;
}
.cyber-radio .el-radio__input.is-checked .el-radio__inner {
  background: #0f0;
  border-color: #0f0;
}
.cyber-radio .el-radio__input.is-checked + .el-radio__label {
  color: #000000 !important; /* 选中后文字变绿 */
  text-shadow: 1px 1px 0 #ff00ff;
}

/* 日期选择器优化 */
.cyber-date { --el-date-editor-width: 100%; }
.cyber-date .el-input__wrapper {
  background-color: #001a00 !important;
  box-shadow: 0 0 0 1px #0f0 inset !important;
  border-radius: 0;
}
.cyber-date input { color: #fff !important; font-family: 'VT323', monospace; font-size: 20px; }

/* 9. 确认框 (MessageBox) */
.cyber-message-box {
  background: #0d0d0d !important;
  border: 2px solid #f00 !important;
  box-shadow: 0 0 20px rgba(255, 0, 0, 0.4) !important;
}
.cyber-message-box .el-message-box__title, .cyber-message-box .el-message-box__content {
  color: #f00 !important; font-family: 'VT323', monospace; font-size: 20px;
}
.cyber-message-box .el-button--primary {
  background: #f00 !important; border-color: #f00 !important; color: #000 !important; font-family: 'VT323'; font-weight: bold;
}
</style>