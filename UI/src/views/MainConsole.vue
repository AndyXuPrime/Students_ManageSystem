<template>
  <div class="retro-poster-container">
    <!-- 视觉特效层 -->
    <div class="noise-overlay"></div>
    <div class="bg-circle circle-1"></div>
    <div class="bg-circle circle-2"></div>

    <!-- 主控制台容器 -->
    <div class="main-panel">

      <!-- 顶部 Header (保持不变，作为系统标题) -->
      <div class="panel-header">
        <div class="header-decoration">
          <span class="dot red"></span>
          <span class="dot yellow"></span>
          <span class="dot green"></span>
        </div>
        <div class="header-title-box">
          <h1 class="main-title">ACADEMIC_CORE</h1>
          <span class="sub-title">v.2025 // SYS.ADMIN // {{ currentModule.toUpperCase() }}_MODE</span>
        </div>
        <div class="header-time-box">
          <span class="time-label">TIME_CYCLE</span>
          <span class="time-value">{{ timeStr }}</span>
        </div>
      </div>

      <!-- 核心布局：左右分栏 -->
      <div class="console-layout">

        <!-- 左侧导航栏 (模拟物理按键/磁带插槽) -->
        <aside class="side-nav">
          <div class="nav-label">MODULES</div>

          <div
              class="nav-item"
              :class="{ active: currentModule === 'student' }"
              @click="switchModule('student')"
          >
            <span class="indicator"></span>
            <span class="nav-text">STUDENT_DB</span>
            <span class="nav-icon">👤</span>
          </div>

          <div
              class="nav-item"
              :class="{ active: currentModule === 'course' }"
              @click="switchModule('course')"
          >
            <span class="indicator"></span>
            <span class="nav-text">COURSE_DATA</span>
            <span class="nav-icon">📚</span>
          </div>

          <!-- 装饰性空插槽 -->
          <div class="nav-item disabled">
            <span class="indicator off"></span>
            <span class="nav-text">TEACHER_LOG</span>
            <span class="nav-icon">🔒</span>
          </div>
        </aside>

        <!-- 右侧内容区 -->
        <main class="content-area">

          <!-- 1. 控制工具栏 (搜索 & 添加) -->
          <div class="control-deck">
            <div class="search-module">
              <div class="module-label">COMMAND_LINE // {{ currentModule === 'student' ? 'FIND_STUDENT' : 'FIND_COURSE' }}</div>
              <div class="input-group">
                <!-- 动态 placeholder -->
                <input
                    v-model="queryParams.keyword"
                    class="retro-input"
                    :placeholder="currentModule === 'student' ? 'ENTER NAME OR ID...' : 'ENTER COURSE NAME...'"
                    @keyup.enter="handleQuery"
                />

                <!-- 筛选下拉框 (仅课程模式显示) -->
                <select v-if="currentModule === 'course'" v-model="queryParams.type" class="retro-select">
                  <option value="">ALL_TYPES</option>
                  <option value="必修">REQUIRED</option>
                  <option value="选修">ELECTIVE</option>
                </select>

                <button class="retro-btn primary" @click="handleQuery">SCAN</button>
                <button class="retro-btn warning" @click="handleAdd">
                  {{ currentModule === 'student' ? 'NEW_STUDENT' : 'NEW_COURSE' }}
                </button>
              </div>
            </div>
          </div>

          <!-- 2. 数据视窗 (根据模式切换表格) -->
          <div class="data-viewport">

            <!-- A. 学生表格 (完整字段展示) -->
            <el-table
                v-if="currentModule === 'student'"
                :data="studentData"
                class="retro-table"
                v-loading="loading"
                element-loading-background="rgba(30, 39, 46, 0.8)"
                height="100%"
                style="width: 100%"
            >
              <!-- 1. 学号 (固定左侧) -->
              <el-table-column prop="sno" label="ID_NO" width="100" align="center" fixed="left"/>

              <!-- 2. 姓名 (固定左侧) -->
              <el-table-column prop="sname" label="IDENTITY" width="110" align="center" fixed="left">
                <template #default="scope">
                  <span class="highlight-text">{{ scope.row.sname }}</span>
                </template>
              </el-table-column>

              <!-- 3. 头像 (视觉装饰) -->
              <el-table-column label="VISUAL" width="70" align="center">
                <template #default="scope">
                  <div class="retro-avatar" :class="scope.row.sex === '男' ? 'av-cyan' : 'av-orange'">
                    {{ scope.row.sname ? scope.row.sname.charAt(0) : '?' }}
                  </div>
                </template>
              </el-table-column>

              <!-- 4. 性别 -->
              <el-table-column prop="sex" label="GEN" width="70" align="center">
                <template #default="scope">
                  <span class="gender-tag" :class="scope.row.sex === '男' ? 'tag-male' : 'tag-female'">
                    {{ scope.row.sex === '男' ? 'M' : 'F' }}
                  </span>
                </template>
              </el-table-column>

              <!-- 5. 出生日期 (新增) -->
              <el-table-column prop="birth" label="BIRTH_DATE" width="120" align="center">
                <template #default="scope">
                  <!-- 截取日期部分，防止显示时间 -->
                  <span class="mono-text">{{ scope.row.birth ? scope.row.birth.substring(0,10) : '-' }}</span>
                </template>
              </el-table-column>

              <!-- 6. 入学时间 (新增) -->
              <el-table-column prop="entranceDate" label="ENTRY_DATE" width="120" align="center">
                <template #default="scope">
                  <span class="mono-text">{{ scope.row.entranceDate ? scope.row.entranceDate.substring(0,10) : '-' }}</span>
                </template>
              </el-table-column>

              <!-- 7. 班级 -->
              <el-table-column prop="classno" label="CLASS" width="80" align="center"/>

              <!-- 8. 院系 -->
              <el-table-column prop="sdept" label="DEPT" width="150" show-overflow-tooltip header-align="center"/>

              <!-- 9. 邮编 (新增) -->
              <el-table-column prop="postcode" label="POSTCODE" width="100" align="center"/>

              <!-- 10. 家庭住址 (新增) -->
              <el-table-column prop="homeAddr" label="AADR" min-width="180" show-overflow-tooltip header-align="center"/>

              <!-- 操作栏 (固定右侧) -->
              <el-table-column label="ACTIONS" width="140" fixed="right" align="center">
                <template #default="scope">
                  <div class="action-group">
                    <button class="icon-btn edit" @click="handleEdit(scope.row)">✎</button>
                    <button class="icon-btn del" @click="handleDelete(scope.row.sno)">✖</button>
                  </div>
                </template>
              </el-table-column>
            </el-table>

            <!-- B. 课程表格 (新增) -->
            <el-table
                v-if="currentModule === 'course'"
                :data="courseData"
                class="retro-table"
                v-loading="loading"
                element-loading-background="rgba(30, 39, 46, 0.8)"
                height="100%"
            >
              <el-table-column prop="cno" label="COURSE_ID" width="110" align="center"/>
              <el-table-column prop="cname" label="COURSE_NAME" width="180" show-overflow-tooltip>
                <template #default="scope">
                  <span class="highlight-text" style="color: var(--c-accent)">{{ scope.row.cname }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="credit" label="CREDITS" width="90" align="center">
                <template #default="scope">
                  <span class="digital-number">{{ scope.row.credit }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="teacher" label="INSTRUCTOR" width="120" align="center"/>
              <el-table-column prop="type" label="TYPE" width="100" align="center">
                <template #default="scope">
                  <span class="course-tag" :class="scope.row.type === '必修' ? 'required' : 'elective'">
                    {{ scope.row.type === '必修' ? 'REQ' : 'OPT' }}
                  </span>
                </template>
              </el-table-column>
              <el-table-column prop="period" label="HOURS" width="90" align="center"/>
              <el-table-column label="ACTIONS" width="140" fixed="right" align="center">
                <template #default="scope">
                  <div class="action-group">
                    <button class="icon-btn edit" @click="handleEdit(scope.row)">✎</button>
                    <button class="icon-btn del" @click="handleDelete(scope.row.cno)">✖</button>
                  </div>
                </template>
              </el-table-column>
            </el-table>

            <!-- 分页 (共用) -->
            <div class="panel-footer">
              <el-pagination
                  v-model:current-page="queryParams.current"
                  v-model:page-size="queryParams.size"
                  :page-sizes="[10, 20, 50]"
                  layout="prev, pager, next"
                  :total="total"
                  @size-change="handleQuery"
                  @current-change="handleQuery"
                  class="retro-pagination"
              />
            </div>
          </div>
        </main>
      </div>
    </div>

    <!-- 弹窗 (动态渲染表单) -->
    <el-dialog
        :title="dialog.title"
        v-model="dialog.visible"
        width="480px"
        @close="resetForm"
        class="retro-dialog"
        :show-close="false"
    >
      <div class="dialog-stripe"></div>

      <el-form :model="form" ref="formRef" label-width="100px" :rules="currentModule === 'student' ? studentRules : courseRules" class="retro-form">

        <!-- ================= 学生表单 (完整字段) ================= -->
        <template v-if="currentModule === 'student'">
          <!-- 第一行：学号 & 姓名 -->
          <div class="form-row">
            <el-form-item label="ID_NO" prop="sno" style="width: 50%">
              <el-input v-model="form.sno" :disabled="dialog.isEdit" placeholder="8 CHARS" maxlength="8" class="retro-form-input"/>
            </el-form-item>
            <el-form-item label="NAME" prop="sname" style="width: 50%">
              <el-input v-model="form.sname" class="retro-form-input"/>
            </el-form-item>
          </div>

          <!-- 第二行：性别 & 班级 -->
          <div class="form-row">
            <el-form-item label="GEN" prop="sex" style="width: 50%">
              <el-radio-group v-model="form.sex" class="retro-radio-group">
                <el-radio label="男" border>MALE</el-radio>
                <el-radio label="女" border>FEMALE</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="CLASS" prop="classno" style="width: 50%">
              <el-select
                  v-model="form.classno"
                  placeholder="SELECT CLASS"
                  class="retro-select-inner"
                  popper-class="retro-select-popper"
              >
                <el-option
                    v-for="item in classList"
                    :key="item.classno"
                    :label="item.classno + ' (' + item.major + ')'"
                    :value="item.classno"
                />
              </el-select>
            </el-form-item>
          </div>

          <!-- 第三行：出生日期 & 入学日期 -->
          <div class="form-row">
            <el-form-item label="BIRTH" prop="birth" style="width: 50%">
              <el-date-picker v-model="form.birth" type="date" value-format="YYYY-MM-DD" placeholder="YYYY-MM-DD" class="retro-date" style="width: 100%"/>
            </el-form-item>
            <el-form-item label="ENTRY" prop="entranceDate" style="width: 50%">
              <el-date-picker v-model="form.entranceDate" type="date" value-format="YYYY-MM-DD" placeholder="YYYY-MM-DD" class="retro-date" style="width: 100%"/>
            </el-form-item>
          </div>

          <!-- 第四行：院系 & 邮编 -->
          <div class="form-row">
            <el-form-item label="DEPT" prop="sdept" style="width: 50%">
              <el-input v-model="form.sdept" class="retro-form-input"/>
            </el-form-item>
            <el-form-item label="ZIP" prop="postcode" style="width: 50%">
              <el-input v-model="form.postcode" maxlength="6" class="retro-form-input"/>
            </el-form-item>
          </div>

          <!-- 第五行：家庭住址 (全宽) -->
          <el-form-item label="ADDR" prop="homeAddr">
            <el-input v-model="form.homeAddr" placeholder="FULL ADDRESS COORDINATES" class="retro-form-input"/>
          </el-form-item>
        </template>


        <!-- 课程表单 -->
        <template v-else>
          <el-form-item label="CODE" prop="cno">
            <el-input v-model="form.cno" :disabled="dialog.isEdit" placeholder="e.g. CS101" class="retro-form-input"/>
          </el-form-item>
          <el-form-item label="TITLE" prop="cname">
            <el-input v-model="form.cname" class="retro-form-input"/>
          </el-form-item>
          <div class="form-row">
            <el-form-item label="CREDIT" prop="credit" style="width: 50%">
              <el-input-number v-model="form.credit" :min="1" :max="10" class="retro-number-input" />
            </el-form-item>
            <el-form-item label="HOURS" prop="period" style="width: 50%">
              <el-input v-model="form.period" class="retro-form-input"/>
            </el-form-item>
          </div>
          <el-form-item label="TEACHER" prop="teacher">
            <el-input v-model="form.teacher" class="retro-form-input"/>
          </el-form-item>
          <el-form-item label="TYPE" prop="type">
            <el-radio-group v-model="form.type" class="retro-radio-group">
              <el-radio label="必修" border>REQ</el-radio>
              <el-radio label="选修" border>OPT</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>

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
import { ref, reactive, onMounted, onUnmounted } from 'vue'
// 引入真实的学生 API
import { getStudentPage, addStudent, updateStudent, deleteStudent,getAllClasses } from '../api/student.js'
// 如果有课程 API，请在此处引入，例如：
// import { getCoursePage, addCourse, updateCourse, deleteCourse } from '../api/course.js'
import { ElMessage, ElMessageBox } from 'element-plus'

// --- 1. 状态管理 ---
const currentModule = ref('student') // 当前模块: 'student' | 'course'
const loading = ref(false)
const total = ref(0)
const timeStr = ref('')
const classList = ref([])
// 数据源区分
const studentData = ref([])
const courseData = ref([])

// Mock 课程数据 (用于演示，直到你接入真实后端)
const mockCourses = [
  {cno:'CS101', cname:'Intro to Cybernetics', credit: 4, teacher:'Dr. Sbaitso', type:'必修', period: 64},
  {cno:'HIS20', cname:'History of Synthwave', credit: 2, teacher:'Kavinsky', type:'选修', period: 32},
  {cno:'MATH9', cname:'Quantum Calculus', credit: 5, teacher:'Turing', type:'必修', period: 80},
]

// --- 2. 通用查询参数 ---
const queryParams = reactive({
  current: 1,
  size: 10,
  keyword: '', // 统一使用 keyword，调用接口时再映射
  type: ''     // 课程筛选专用
})

// --- 3. 弹窗与表单 ---
const dialog = reactive({
  visible: false,
  title: '',
  isEdit: false // 新增：用于判断是否为编辑模式（控制 ID 是否可改）
})



const form = ref({})
const formRef = ref(null)

// 验证规则分离
const studentRules = {
  sno: [{ required: true, message: 'REQUIRED', trigger: 'blur' }],
  sname: [{ required: true, message: 'REQUIRED', trigger: 'blur' }],
  sex: [{ required: true, message: 'REQUIRED', trigger: 'change' }]
}

const courseRules = {
  cno: [{ required: true, message: 'REQUIRED', trigger: 'blur' }],
  cname: [{ required: true, message: 'REQUIRED', trigger: 'blur' }],
  credit: [{ required: true, message: 'REQUIRED', trigger: 'blur' }]
}

// --- 4. 核心业务逻辑 ---

// 切换模块
const fetchClassList = async () => {
  try {
    const res = await getAllClasses()
    // 假设后端返回的是数组，例如 [{classno: '051', major: '计算机'}, ...]
    classList.value = res
  } catch (error) {
    console.error('获取班级列表失败:', error)
  }
}
onMounted(() => {
  getList()
  fetchClassList() // 👈 页面初始化时加载班级
  updateTime()
  timer = setInterval(updateTime, 1000)
})

const switchModule = (moduleName) => {
  if (currentModule.value === moduleName) return
  currentModule.value = moduleName
  // 重置查询条件
  queryParams.current = 1
  queryParams.keyword = ''
  queryParams.type = ''
  // 重新获取数据
  getList()
}

// 获取数据 (分流逻辑)
const getList = async () => {
  loading.value = true
  try {
    if (currentModule.value === 'student') {
      // --- 学生模块：调用真实 API ---
      // 映射参数：后端需要 name，前端通用 keyword
      const params = {
        current: queryParams.current,
        size: queryParams.size,
        name: queryParams.keyword
      }
      const res = await getStudentPage(params)
      studentData.value = res.content
      total.value = res.totalElements
    } else {
      // --- 课程模块：使用 Mock 数据 (后续替换为真实 API) ---
      // 模拟网络延迟
      await new Promise(resolve => setTimeout(resolve, 300))
      let result = mockCourses
      // 模拟搜索
      if (queryParams.keyword) {
        result = result.filter(c => c.cname.toLowerCase().includes(queryParams.keyword.toLowerCase()))
      }
      // 模拟筛选
      if (queryParams.type) {
        result = result.filter(c => c.type === queryParams.type)
      }
      courseData.value = result
      total.value = result.length
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('DATA RETRIEVAL FAILED')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.current = 1
  getList()
}

// 新增按钮逻辑
const handleAdd = () => {
  dialog.isEdit = false
  dialog.visible = true

  if (currentModule.value === 'student') {
    dialog.title = 'NEW STUDENT ENTRY'
    form.value = { sex: '男' } // 学生默认值
  } else {
    dialog.title = 'NEW COURSE ENTRY'
    form.value = { type: '必修', credit: 2 } // 课程默认值
  }
}

// 编辑按钮逻辑
const handleEdit = (row) => {
  dialog.isEdit = true
  dialog.visible = true
  // 深拷贝数据
  form.value = JSON.parse(JSON.stringify(row))

  if (currentModule.value === 'student') {
    dialog.title = 'MODIFY STUDENT'
  } else {
    dialog.title = 'MODIFY COURSE'
  }
}

// 删除逻辑
const handleDelete = (id) => {
  ElMessageBox.confirm(`DELETE DATA BLOCK [${id}]?`, 'WARNING', {
    confirmButtonText: 'EXECUTE',
    cancelButtonText: 'ABORT',
    type: 'warning',
    customClass: 'retro-message-box'
  }).then(async () => {
    if (currentModule.value === 'student') {
      await deleteStudent(id)
      ElMessage.success('DELETED SUCCESS')
    } else {
      // 课程删除 Mock
      ElMessage.success('COURSE DELETED (MOCK)')
    }
    getList()
  })
}

// 提交表单
const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      if (currentModule.value === 'student') {
        // --- 学生提交逻辑 ---
        if (!dialog.isEdit) {
          await addStudent(form.value)
          ElMessage.success('INSERTED SUCCESS')
        } else {
          await updateStudent(form.value)
          ElMessage.success('UPDATED SUCCESS')
        }
      } else {
        // --- 课程提交逻辑 (Mock) ---
        ElMessage.success('COURSE SAVED (MOCK)')
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

// --- 5. 时钟逻辑 ---
let timer = null
const updateTime = () => {
  const now = new Date()
  timeStr.value = now.toLocaleTimeString('en-US', { hour12: false })
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
:root {
  --c-bg-dark: #2c3e50;
  --c-panel-bg: rgba(30, 39, 46, 0.95);
  --c-danger: #c0392b;
  --c-highlight: #f1c40f;
  --c-accent: #e67e22;
  --c-primary: #1abc9c;
  --c-muted: #7f8c8d;
  --c-text-light: #ecf0f1;
  --font-display: 'Impact', 'Arial Black', sans-serif;
  --font-mono: 'Courier New', monospace;
}

/* ==========================================================================
   LAYOUT STRUCTURE (NEW)
   ========================================================================== */
.retro-poster-container {
  height: 100vh;
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

.main-panel {
  width: 95%;
  max-width: 1400px;
  height: 90vh;
  background: var(--c-panel-bg);
  border: 4px solid var(--c-muted);
  border-radius: 10px;
  box-shadow: 20px 20px 0px rgba(0,0,0,0.4);
  z-index: 10;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 核心分栏布局 */
.console-layout {
  display: flex;
  flex: 1;
  overflow: hidden;
}

/* 左侧导航 */
.side-nav {
  width: 220px;
  background: #202b33;
  border-right: 3px solid var(--c-muted);
  display: flex;
  flex-direction: column;
  padding: 20px 10px;
  gap: 15px;
}

.nav-label {
  font-family: var(--font-display);
  color: var(--c-muted);
  font-size: 14px;
  text-align: center;
  margin-bottom: 10px;
  letter-spacing: 2px;
}

.nav-item {
  height: 60px;
  background: #34495e;
  border: 2px solid #455a64;
  border-radius: 6px;
  display: flex;
  align-items: center;
  padding: 0 15px;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
  overflow: hidden;
}

.nav-item:hover {
  background: #3e5871;
  border-color: var(--c-text-light);
}

.nav-item.active {
  background: #2c3e50;
  border-color: var(--c-primary);
  box-shadow: inset 0 0 15px rgba(26, 188, 156, 0.2);
}

.nav-item.disabled {
  opacity: 0.5;
  cursor: not-allowed;
  border-style: dashed;
}

.nav-text {
  flex: 1;
  font-weight: bold;
  font-size: 16px;
  color: var(--c-text-light);
}
.nav-item.active .nav-text { color: var(--c-primary); text-shadow: 0 0 5px var(--c-primary); }

.nav-icon { font-size: 20px; }

/* 指示灯 */
.indicator {
  width: 8px; height: 8px; background: #555; border-radius: 50%; margin-right: 15px;
  box-shadow: inset 0 1px 2px rgba(0,0,0,1);
}
.nav-item.active .indicator {
  background: var(--c-highlight);
  box-shadow: 0 0 8px var(--c-highlight);
}
.indicator.off { background: #330000; }

/* 右侧内容区 */
.content-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #25303b;
  overflow: hidden;
}

.panel-header {
  height: 70px;
  background: var(--c-accent);
  padding: 0 30px;
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
.main-title { margin: 0; font-family: var(--font-display); font-size: 28px; letter-spacing: 2px; color: var(--c-bg-dark); }
.sub-title { font-size: 12px; font-weight: bold; opacity: 0.8; color: var(--c-bg-dark); }
.header-time-box { background: var(--c-bg-dark); padding: 5px 15px; border-radius: 20px; border: 2px solid var(--c-highlight); color: var(--c-highlight); text-align: center; }
.time-value { font-weight: bold; font-size: 16px; }

/* 控制栏 */
.control-deck {
  padding: 15px 20px;
  border-bottom: 2px dashed var(--c-muted);
  background: rgba(0,0,0,0.1);
}
.module-label { color: var(--c-primary); font-size: 12px; margin-bottom: 5px; letter-spacing: 1px; }
.input-group { display: flex; gap: 10px; }

.retro-input, .retro-select {
  background: var(--c-bg-dark);
  border: 2px solid var(--c-muted);
  color: var(--c-highlight);
  padding: 8px 12px;
  font-family: var(--font-mono);
  font-size: 16px;
  border-radius: 4px;
  outline: none;
}
.retro-input:focus, .retro-select:focus { border-color: var(--c-highlight); }

.mono-text {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  color: var(--c-text-light);
  opacity: 0.9;
}

/* 调整表格单元格内边距，防止内容太挤 */
.retro-table .el-table__cell {
  padding: 8px 0 !important;
}
.form-row {
  display: flex;
  gap: 15px; /* 增加一点间距，防止两个输入框贴太紧 */
  margin-bottom: 0px; /* 消除额外的底部间距，因为 el-form-item 自带间距 */
}
/* 按钮 */
.retro-btn {
  border: none; padding: 0 20px; font-family: var(--font-display); font-size: 16px; cursor: pointer; border-radius: 4px; transition: transform 0.1s;
}
.retro-btn:active { transform: scale(0.95); }
.retro-btn.primary { background: var(--c-primary); color: var(--c-bg-dark); }
.retro-btn.warning { background: var(--c-highlight); color: var(--c-bg-dark); }
.retro-btn.ghost { background: transparent; border: 2px solid var(--c-muted); color: var(--c-muted); }

/* 表格区域 */
.data-viewport { flex: 1; padding: 20px; overflow: hidden; display: flex; flex-direction: column; }

.retro-table {
  --el-table-bg-color: transparent !important;
  --el-table-tr-bg-color: transparent !important;
  --el-table-header-bg-color: rgba(0,0,0,0.2) !important;
  --el-table-row-hover-bg-color: rgba(26, 188, 156, 0.1) !important;
  --el-table-border-color: var(--c-muted) !important;
  --el-table-text-color: var(--c-text-light) !important;
  --el-table-header-text-color: var(--c-accent) !important;
  font-family: var(--font-mono);
  border: 2px solid var(--c-muted) !important;
  flex: 1; /* 撑满剩余空间 */
}

/* 课程特有样式 */
.course-tag { padding: 2px 6px; border-radius: 2px; font-size: 12px; font-weight: bold; }
.course-tag.required { background: var(--c-danger); color: white; }
.course-tag.elective { background: var(--c-primary); color: var(--c-bg-dark); }
.digital-number { font-family: 'Impact'; letter-spacing: 1px; color: var(--c-highlight); font-size: 18px; }

/* 头像与标签 */
.retro-avatar { width: 30px; height: 30px; border-radius: 4px; display: flex; align-items: center; justify-content: center; font-weight: bold; color: #fff; }
.av-cyan { background: var(--c-primary); }
.av-orange { background: var(--c-accent); }
.gender-tag { font-size: 12px; padding: 2px 6px; border-radius: 4px; border: 1px solid; }
.tag-male { color: var(--c-primary); border-color: var(--c-primary); }
.tag-female { color: var(--c-highlight); border-color: var(--c-highlight); }

/* 操作按钮 */
.action-group { display: flex; justify-content: center; gap: 8px; }
.icon-btn { width: 28px; height: 28px; border-radius: 4px; border: none; cursor: pointer; display: flex; align-items: center; justify-content: center; }
.icon-btn.edit { background: var(--c-primary); color: var(--c-bg-dark); }
.icon-btn.del { background: var(--c-danger); color: #fff; }

/* 分页 */
.panel-footer { margin-top: 15px; display: flex; justify-content: flex-end; }
.retro-pagination {
  --el-pagination-bg-color: transparent !important;
  --el-pagination-text-color: var(--c-muted) !important;
  --el-pagination-button-color: var(--c-muted) !important;
  --el-disabled-bg-color: transparent !important;
}
.retro-pagination .el-pager li.is-active { background: var(--c-highlight) !important; color: var(--c-bg-dark) !important; font-weight: bold; }

/* 弹窗样式 */
.retro-dialog { background: var(--c-bg-dark) !important; border: 4px solid var(--c-highlight) !important; border-radius: 10px !important; }
.retro-dialog .el-dialog__header { background: var(--c-highlight); padding: 10px 20px; margin: 0; }
.retro-dialog .el-dialog__title { color: var(--c-bg-dark) !important; font-family: var(--font-display); font-size: 20px; }
.dialog-stripe { height: 10px; background: repeating-linear-gradient(45deg, var(--c-accent), var(--c-accent) 10px, var(--c-bg-dark) 10px, var(--c-bg-dark) 20px); margin-bottom: 20px; }
.retro-form .el-form-item__label { color: var(--c-primary) !important; font-family: var(--font-mono); }
.retro-form-input .el-input__wrapper, .retro-number-input .el-input__wrapper { background-color: rgba(0,0,0,0.3) !important; box-shadow: 0 0 0 1px var(--c-muted) inset !important; }
.retro-form-input input { color: #fff !important; font-family: var(--font-mono); }
.form-row { display: flex; gap: 10px; }
.dialog-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; border-top: 1px solid var(--c-muted); padding-top: 10px; }

/* 背景特效 */
.noise-overlay { position: absolute; top: 0; left: 0; width: 100%; height: 100%; pointer-events: none; z-index: 1; opacity: 0.05; background: url('data:image/svg+xml;utf8,%3Csvg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg"%3E%3Cfilter id="noiseFilter"%3E%3CfeTurbulence type="fractalNoise" baseFrequency="0.65" numOctaves="3" stitchTiles="stitch"/%3E%3C/filter%3E%3Crect width="100%25" height="100%25" filter="url(%23noiseFilter)"/%3E%3C/svg%3E'); }
.bg-circle { position: absolute; border-radius: 50%; z-index: 0; opacity: 0.1; }
.circle-1 { width: 600px; height: 600px; background: var(--c-primary); top: -200px; right: -100px; }
.circle-2 { width: 400px; height: 400px; background: var(--c-accent); bottom: -100px; left: -100px; }
</style>