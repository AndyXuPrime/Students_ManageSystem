<template>
  <div class="retro-wrapper">
    <!-- CRT 扫描线效果 -->
    <div class="scanlines"></div>

    <div class="retro-terminal">
      <!-- 顶部标题栏 -->
      <div class="terminal-header">
        <span class="blink">> SYSTEM_READY</span>
        <span class="header-title">STUDENT_DATABASE_V1.0</span>
        <span>[REC]</span>
      </div>

      <!-- 1. 顶部搜索栏 -->
      <div class="filter-container">
        <div class="retro-input-group">
          <span class="prefix">QUERY:</span>
          <el-input
              v-model="queryParams.name"
              placeholder="ENTER_NAME..."
              class="retro-input"
              @keyup.enter="handleQuery"
          />
        </div>
        <div class="action-buttons">
          <el-button type="primary" class="retro-btn neon-blue" :icon="Search" @click="handleQuery">SEARCH</el-button>
          <el-button type="success" class="retro-btn neon-pink" :icon="Plus" @click="handleAdd">INSERT</el-button>
        </div>
      </div>

      <!-- 2. 数据表格 -->
      <div class="table-wrapper">
        <el-table
            v-loading="loading"
            :data="studentList"
            border
            class="retro-table"
            element-loading-background="rgba(0, 0, 0, 0.8)"
        >
          <el-table-column prop="sno" label="ID (SNO)" width="140" sortable />

          <!-- 头像列 -->
          <el-table-column label="AVATAR" width="100" align="center">
            <template #default="scope">
              <el-avatar :size="40" :src="scope.row.homeAddr" shape="square" class="retro-avatar">
                {{ scope.row.sname ? scope.row.sname.charAt(0) : '?' }}
              </el-avatar>
            </template>
          </el-table-column>

          <el-table-column prop="sname" label="NAME" width="140" />

          <el-table-column prop="sex" label="GENDER" width="100">
            <template #default="scope">
              <span :class="scope.row.sex === '男' ? 'text-blue' : 'text-pink'">
                {{ scope.row.sex === '男' ? 'MALE' : 'FEMALE' }}
              </span>
            </template>
          </el-table-column>

          <el-table-column prop="birth" label="BIRTH_DATE" width="160" />
          <el-table-column prop="classno" label="CLASS_NO" width="120" />
          <el-table-column prop="sdept" label="DEPT" />

          <el-table-column label="ACTIONS" width="220" align="center">
            <template #default="scope">
              <el-button size="small" class="retro-action-btn edit" :icon="Edit" @click="handleEdit(scope.row)">
                EDIT
              </el-button>
              <el-button size="small" class="retro-action-btn del" :icon="Delete" @click="handleDelete(scope.row)">
                DEL
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 3. 分页组件 -->
      <div class="pagination-container">
        <el-pagination
            v-model:current-page="queryParams.current"
            v-model:page-size="queryParams.size"
            :page-sizes="[5, 10, 20]"
            layout="prev, pager, next"
            :total="total"
            @size-change="getList"
            @current-change="getList"
            class="retro-pagination"
        />
      </div>
    </div>

    <!-- 4. 新增/修改 弹窗 (Retro Style) -->
    <el-dialog
        :title="dialogTitle"
        v-model="dialogVisible"
        width="500px"
        class="retro-dialog"
        :close-on-click-modal="false"
    >
      <el-form :model="form" label-width="100px" class="retro-form">
        <el-form-item label="ID [SNO]" required>
          <el-input v-model="form.sno" :disabled="isEdit" placeholder="MAX 8 CHARS" maxlength="8" />
        </el-form-item>

        <el-form-item label="NAME" required>
          <el-input v-model="form.sname" placeholder="ENTER NAME" maxlength="8" />
        </el-form-item>

        <el-form-item label="GENDER">
          <el-radio-group v-model="form.sex">
            <el-radio label="男" class="retro-radio">MALE</el-radio>
            <el-radio label="女" class="retro-radio">FEMALE</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="CLASS" required>
          <el-select v-model="form.classno" placeholder="SELECT CLASS" popper-class="retro-select-dropdown">
            <el-option
                v-for="item in classOptions"
                :key="item.classno"
                :label="item.classno + ' - ' + item.major"
                :value="item.classno"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="BIRTHDAY">
          <el-date-picker
              v-model="form.birth"
              type="date"
              placeholder="SELECT DATE"
              value-format="YYYY-MM-DD"
              popper-class="retro-date-picker"
          />
        </el-form-item>

        <el-form-item label="DEPT">
          <el-input v-model="form.sdept" placeholder="DEPARTMENT" maxlength="20" />
        </el-form-item>

        <el-form-item label="AVATAR_URL">
          <el-input v-model="form.homeAddr" placeholder="IMAGE URL" maxlength="40" />
        </el-form-item>

      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <button class="retro-btn-simple cancel" @click="dialogVisible = false">CANCEL</button>
          <button class="retro-btn-simple confirm" @click="submitForm">CONFIRM</button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
// 确保这里的引用路径使用了 @ 或者正确的相对路径
import { getStudentPage, addStudent, updateStudent, deleteStudent, getAllClasses } from '../api/student'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'

const loading = ref(false)
const studentList = ref([])
const total = ref(0)
const classOptions = ref([])

const queryParams = reactive({
  current: 1,
  size: 10,
  name: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)

const form = reactive({
  sno: '',
  sname: '',
  sex: '男',
  birth: '',
  classno: '',
  sdept: '',
  homeAddr: ''
})

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getStudentPage(queryParams)
    studentList.value = res.records
    total.value = res.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 获取班级字典
const getClassList = async () => {
  const res = await getAllClasses()
  classOptions.value = res
}

// 搜索
const handleQuery = () => {
  queryParams.current = 1
  getList()
}

// 打开新增
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '>>> INSERT NEW DATA'
  Object.keys(form).forEach(key => form[key] = '')
  form.sex = '男'
  dialogVisible.value = true
}

// 打开编辑
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '>>> MODIFY DATA'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 提交表单 (包含空值处理修复)
const submitForm = async () => {
  try {
    // 1. 数据清洗：防止传空字符串导致后端500
    const submitData = { ...form }
    if (!submitData.birth) submitData.birth = null
    if (!submitData.classno) submitData.classno = null
    if (!submitData.sdept) submitData.sdept = null

    if (isEdit.value) {
      await updateStudent(submitData)
      ElMessage.success('UPDATE SUCCESS')
    } else {
      await addStudent(submitData)
      ElMessage.success('INSERT SUCCESS')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    // request.js handled error
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(`DELETE STUDENT [${row.sname}] ?`, 'WARNING', {
    confirmButtonText: 'YES',
    cancelButtonText: 'NO',
    type: 'warning',
    customClass: 'retro-message-box'
  }).then(async () => {
    await deleteStudent(row.sno)
    ElMessage.success('DATA DELETED')
    getList()
  })
}

onMounted(() => {
  getList()
  getClassList()
})
</script>

<style>
/* 全局引入复古字体 (如果没有网络，会自动回退) */
@import url('https://fonts.googleapis.com/css2?family=VT323&display=swap');

/* 定义变量 */
:root {
  --retro-bg: #1a0b2e;
  --retro-grid: #2b1d42;
  --neon-pink: #ff00ff;
  --neon-blue: #00ffff;
  --neon-yellow: #f8e71c;
  --text-main: #e0e0e0;
}

/* 覆盖 Element Plus 默认样式 (必须是非 scoped 或者用 deep) */
.retro-wrapper .el-input__wrapper,
.retro-dialog .el-input__wrapper,
.retro-dialog .el-textarea__inner {
  background-color: #000 !important;
  box-shadow: 0 0 0 1px var(--neon-blue) inset !important;
  border-radius: 0 !important;
}

.retro-wrapper .el-input__inner {
  color: var(--neon-blue) !important;
  font-family: 'VT323', monospace;
  font-size: 18px;
}

/* 弹窗样式覆盖 */
.retro-dialog .el-dialog {
  background: var(--retro-bg);
  border: 2px solid var(--neon-pink);
  box-shadow: 0 0 15px var(--neon-pink);
  border-radius: 0;
}
.retro-dialog .el-dialog__title {
  color: var(--neon-pink);
  font-family: 'VT323', monospace;
  font-size: 24px;
}
.retro-dialog .el-dialog__headerbtn .el-dialog__close {
  color: var(--neon-blue);
}
.retro-form .el-form-item__label {
  color: var(--neon-yellow);
  font-family: 'VT323', monospace;
  font-size: 18px;
}

/* 下拉框下拉菜单复古风 */
.retro-select-dropdown {
  background-color: #000 !important;
  border: 1px solid var(--neon-blue) !important;
}
.retro-select-dropdown .el-select-dropdown__item {
  color: var(--neon-blue);
  font-family: 'VT323', monospace;
}
.retro-select-dropdown .el-select-dropdown__item.hover,
.retro-select-dropdown .el-select-dropdown__item:hover {
  background-color: var(--neon-pink) !important;
  color: #fff !important;
}

/* 消息确认框 */
.retro-message-box {
  background: #000 !important;
  border: 2px solid var(--neon-yellow) !important;
  font-family: 'VT323', monospace !important;
}
.retro-message-box .el-message-box__title {
  color: var(--neon-yellow) !important;
}
.retro-message-box .el-message-box__message {
  color: #fff !important;
}
</style>

<style scoped>
/* 容器与背景 */
.retro-wrapper {
  min-height: 100vh;
  background-color: var(--retro-bg);
  background-image:
      linear-gradient(var(--retro-grid) 1px, transparent 1px),
      linear-gradient(90deg, var(--retro-grid) 1px, transparent 1px);
  background-size: 40px 40px;
  font-family: 'VT323', monospace;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

/* CRT 扫描线 */
.scanlines {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
      to bottom,
      rgba(255,255,255,0),
      rgba(255,255,255,0) 50%,
      rgba(0,0,0,0.2) 50%,
      rgba(0,0,0,0.2)
  );
  background-size: 100% 4px;
  pointer-events: none;
  z-index: 999;
}

/* 终端主体 */
.retro-terminal {
  max-width: 1200px;
  margin: 0 auto;
  border: 2px solid var(--neon-blue);
  box-shadow: 0 0 20px var(--neon-blue), inset 0 0 20px rgba(0, 255, 255, 0.2);
  padding: 20px;
  background: rgba(0, 0, 0, 0.6);
  position: relative;
  z-index: 10;
}

/* 头部 */
.terminal-header {
  display: flex;
  justify-content: space-between;
  color: var(--neon-blue);
  border-bottom: 1px dashed var(--neon-blue);
  padding-bottom: 10px;
  margin-bottom: 20px;
  font-size: 20px;
}
.blink {
  animation: blink 1s infinite;
}
@keyframes blink { 50% { opacity: 0; } }

/* 搜索栏 */
.filter-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.retro-input-group {
  display: flex;
  align-items: center;
  gap: 10px;
}
.prefix {
  color: var(--neon-yellow);
  font-size: 20px;
}

/* 按钮样式 */
.retro-btn {
  font-family: 'VT323', monospace;
  border-radius: 0;
  border: none;
  font-size: 18px;
  padding: 10px 20px;
  color: #000;
  font-weight: bold;
  box-shadow: 4px 4px 0px #000;
  transition: transform 0.1s;
}
.retro-btn:active {
  transform: translate(2px, 2px);
  box-shadow: 2px 2px 0px #000;
}
.neon-blue {
  background-color: var(--neon-blue);
  border: 1px solid var(--neon-blue);
}
.neon-pink {
  background-color: var(--neon-pink);
  border: 1px solid var(--neon-pink);
}

/* 表格样式 */
.table-wrapper {
  border: 1px solid var(--neon-blue);
  box-shadow: 0 0 10px var(--neon-blue);
}
:deep(.retro-table) {
  --el-table-border-color: var(--neon-blue);
  --el-table-header-bg-color: rgba(0, 255, 255, 0.2);
  --el-table-row-hover-bg-color: rgba(255, 0, 255, 0.2);
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  background-color: transparent;
  color: var(--text-main);
  font-family: 'VT323', monospace;
  font-size: 18px;
}
:deep(.retro-table th.el-table__cell) {
  background-color: rgba(0, 255, 255, 0.1) !important;
  color: var(--neon-blue);
  font-size: 20px;
}
:deep(.el-table--enable-row-hover .el-table__body tr:hover > td.el-table__cell) {
  background-color: rgba(255, 0, 255, 0.2) !important;
}

/* 头像 */
.retro-avatar {
  background: var(--neon-pink);
  color: #000;
  font-weight: bold;
  border: 2px solid #fff;
}

/* 性别文字 */
.text-blue { color: var(--neon-blue); }
.text-pink { color: var(--neon-pink); }

/* 操作按钮 */
.retro-action-btn {
  background: transparent;
  border: 1px solid currentColor;
  font-family: 'VT323', monospace;
  border-radius: 0;
}
.retro-action-btn.edit { color: var(--neon-yellow); border-color: var(--neon-yellow); }
.retro-action-btn.del { color: #ff4757; border-color: #ff4757; }
.retro-action-btn:hover { background: rgba(255,255,255,0.1); }

/* 分页 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
:deep(.retro-pagination .el-pager li) {
  background: transparent;
  color: var(--neon-blue);
  font-family: 'VT323', monospace;
  font-size: 18px;
}
:deep(.retro-pagination .el-pager li.is-active) {
  color: var(--neon-pink);
  font-weight: bold;
  text-shadow: 0 0 5px var(--neon-pink);
}
:deep(.retro-pagination button) {
  background: transparent;
  color: var(--neon-blue);
}

/* 弹窗底部按钮 */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
}
.retro-btn-simple {
  background: transparent;
  border: 2px solid;
  padding: 5px 20px;
  font-family: 'VT323', monospace;
  font-size: 20px;
  cursor: pointer;
  text-transform: uppercase;
}
.retro-btn-simple.cancel {
  color: #fff;
  border-color: #fff;
}
.retro-btn-simple.confirm {
  color: #000;
  background: var(--neon-blue);
  border-color: var(--neon-blue);
  box-shadow: 0 0 10px var(--neon-blue);
}
.retro-btn-simple:hover {
  opacity: 0.8;
}
</style>