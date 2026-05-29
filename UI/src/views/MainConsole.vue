<template>
  <div class="retro-poster-container">
    <!-- 视觉特效层 -->
    <div class="noise-overlay"></div>
    <div class="bg-circle circle-1"></div>
    <div class="bg-circle circle-2"></div>

    <!-- 主控制台容器 -->
    <div class="main-panel">

      <!-- 顶部 Header -->
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

        <!-- 左侧导航栏 -->
        <aside class="side-nav">
          <div class="nav-label">MODULES</div>

          <div class="nav-item" :class="{ active: currentModule === 'student' }" @click="switchModule('student')">
            <span class="indicator"></span><span class="nav-text">STUDENT_DB</span><span class="nav-icon">👤</span>
          </div>
          <div class="nav-item" :class="{ active: currentModule === 'class' }" @click="switchModule('class')">
            <span class="indicator"></span><span class="nav-text">CLASS_DB</span><span class="nav-icon">🏫</span>
          </div>
          <div class="nav-item" :class="{ active: currentModule === 'course' }" @click="switchModule('course')">
            <span class="indicator"></span><span class="nav-text">COURSE_DATA</span><span class="nav-icon">📚</span>
          </div>
          <div class="nav-item" :class="{ active: currentModule === 'teacher' }" @click="switchModule('teacher')">
            <span class="indicator"></span><span class="nav-text">TEACHER_LOG</span><span class="nav-icon">👨‍🏫</span>
          </div>
        </aside>

        <!-- 右侧内容区 -->
        <main class="content-area">

          <!-- 1. 控制工具栏 (已优化) -->
          <div class="control-deck">
            <div class="search-module">
              <div class="module-label">COMMAND_LINE // SEARCH_{{ currentModule.toUpperCase() }}</div>
              <div class="input-group">
                <input
                    v-model="queryParams.keyword"
                    class="retro-input"
                    :placeholder="getPlaceholder()"
                    @keyup.enter="handleQuery"
                />

                <!-- 课程筛选 (按类型) -->
                <select v-if="currentModule === 'course'" v-model="queryParams.type" class="retro-select">
                  <option value="">ALL_TYPES</option>
                  <option value="必修">REQUIRED</option>
                  <option value="选修">ELECTIVE</option>
                </select>

                <!-- 教师筛选 (按职称) - 新增 -->
                <select v-if="currentModule === 'teacher'" v-model="queryParams.title" class="retro-select">
                  <option value="">ALL_TITLES</option>
                  <option value="教授">教授 (Professor)</option>
                  <option value="副教授">副教授 (Assoc. Prof)</option>
                  <option value="讲师">讲师 (Lecturer)</option>
                  <option value="助教">助教 (Assistant)</option>
                </select>

                <button class="retro-btn primary" @click="handleQuery">SCAN</button>
                <button class="retro-btn warning" @click="handleAdd">
                  NEW_ENTRY
                </button>
              </div>
            </div>
          </div>

          <!-- 2. 数据视窗 -->
          <div class="data-viewport">
            <!-- A. 学生表格 -->
            <el-table v-if="currentModule === 'student'" :data="studentData" class="retro-table" v-loading="loading" element-loading-background="rgba(30, 39, 46, 0.8)" height="100%" style="width: 100%">
              <el-table-column prop="sno" label="ID_NO" width="100" align="center" fixed="left"/>
              <el-table-column prop="sname" label="IDENTITY" width="110" align="center" fixed="left">
                <template #default="scope"><span class="highlight-text">{{ scope.row.sname }}</span></template>
              </el-table-column>
              <el-table-column label="VISUAL" width="70" align="center">
                <template #default="scope"><div class="retro-avatar" :class="scope.row.sex === '男' ? 'av-cyan' : 'av-orange'">{{ scope.row.sname ? scope.row.sname.charAt(0) : '?' }}</div></template>
              </el-table-column>
              <el-table-column prop="sex" label="GEN" width="70" align="center">
                <template #default="scope"><span class="gender-tag" :class="scope.row.sex === '男' ? 'tag-male' : 'tag-female'">{{ scope.row.sex === '男' ? 'M' : 'F' }}</span></template>
              </el-table-column>
              <el-table-column prop="birth" label="BIRTH_DATE" width="120" align="center">
                <template #default="scope"><span class="mono-text">{{ scope.row.birth ? scope.row.birth.substring(0,10) : '-' }}</span></template>
              </el-table-column>
              <el-table-column prop="entranceDate" label="ENTRY_DATE" width="120" align="center">
                <template #default="scope"><span class="mono-text">{{ scope.row.entranceDate ? scope.row.entranceDate.substring(0,10) : '-' }}</span></template>
              </el-table-column>
              <el-table-column prop="classno" label="CLASS" width="80" align="center"/>
              <el-table-column prop="sdept" label="DEPT" width="150" show-overflow-tooltip header-align="center"/>
              <el-table-column prop="postcode" label="ZIP" width="90" align="center"/>
              <el-table-column prop="homeAddr" label="ADDR (COORDINATES)" min-width="180" show-overflow-tooltip header-align="center"/>
              <el-table-column label="ACTIONS" width="140" fixed="right" align="center">
                <template #default="scope">
                  <div class="action-group">
                    <button class="icon-btn edit" @click="handleEdit(scope.row)">✎</button>
                    <button class="icon-btn del" @click="handleDelete(scope.row.sno)">✖</button>
                  </div>
                </template>
              </el-table-column>
            </el-table>

            <!-- B. 班级表格 -->
            <el-table v-if="currentModule === 'class'" :data="classData" class="retro-table" v-loading="loading" element-loading-background="rgba(30, 39, 46, 0.8)" height="100%" style="width: 100%">
              <el-table-column prop="classno" label="CLASS_NO" width="150" align="center" fixed="left">
                <template #default="scope"><span class="highlight-text">{{ scope.row.classno }}</span></template>
              </el-table-column>
              <el-table-column prop="major" label="MAJOR_NAME" show-overflow-tooltip>
                <template #default="scope"><span class="mono-text" style="font-size: 16px;">{{ scope.row.major }}</span></template>
              </el-table-column>
              <el-table-column label="ACTIONS" width="140" fixed="right" align="center">
                <template #default="scope">
                  <div class="action-group">
                    <button class="icon-btn edit" @click="handleEdit(scope.row)">✎</button>
                    <button class="icon-btn del" @click="handleDelete(scope.row.classno)">✖</button>
                  </div>
                </template>
              </el-table-column>
            </el-table>

            <!-- C. 课程表格 -->
            <el-table v-if="currentModule === 'course'" :data="courseData" class="retro-table" v-loading="loading" element-loading-background="rgba(30, 39, 46, 0.8)" height="100%" style="width: 100%">
              <el-table-column prop="cno" label="COURSE_ID" width="110" align="center"/>
              <el-table-column prop="cname" label="COURSE_NAME" min-width="180" show-overflow-tooltip>
                <template #default="scope"><span class="highlight-text" style="color: var(--c-accent)">{{ scope.row.cname }}</span></template>
              </el-table-column>
              <el-table-column prop="credit" label="CREDITS" width="90" align="center">
                <template #default="scope"><span class="digital-number">{{ scope.row.credit }}</span></template>
              </el-table-column>
              <el-table-column label="INSTRUCTOR" width="150" align="center">
                <template #default="scope"><span>{{ scope.row.teacher ? scope.row.teacher.tname : 'TBD' }}</span></template>
              </el-table-column>
              <el-table-column prop="type" label="TYPE" width="100" align="center">
                <template #default="scope"><span class="course-tag" :class="scope.row.type === '必修' ? 'required' : 'elective'">{{ scope.row.type === '必修' ? 'REQ' : 'OPT' }}</span></template>
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

            <!-- D. 教师表格 -->
            <el-table v-if="currentModule === 'teacher'" :data="teacherData" class="retro-table" v-loading="loading" element-loading-background="rgba(30, 39, 46, 0.8)" height="100%" style="width: 100%">
              <el-table-column prop="tno" label="TEACHER_ID" width="150" align="center" fixed="left">
                <template #default="scope"><span class="highlight-text">{{ scope.row.tno }}</span></template>
              </el-table-column>
              <el-table-column prop="tname" label="NAME" show-overflow-tooltip>
                <template #default="scope"><span class="mono-text" style="font-size: 16px;">{{ scope.row.tname }}</span></template>
              </el-table-column>
              <el-table-column prop="title" label="TITLE" width="150" align="center">
                <template #default="scope"><span class="course-tag elective">{{ scope.row.title }}</span></template>
              </el-table-column>
              <el-table-column label="ACTIONS" width="140" fixed="right" align="center">
                <template #default="scope">
                  <div class="action-group">
                    <button class="icon-btn edit" @click="handleEdit(scope.row)">✎</button>
                    <button class="icon-btn del" @click="handleDelete(scope.row.tno)">✖</button>
                  </div>
                </template>
              </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="panel-footer">
              <el-pagination v-model:current-page="queryParams.current" v-model:page-size="queryParams.size" :page-sizes="[10, 20, 50]" layout="prev, pager, next" :total="total" @size-change="handleQuery" @current-change="handleQuery" class="retro-pagination"/>
            </div>
          </div>
        </main>
      </div>
    </div>

    <!-- 弹窗 -->
    <el-dialog
        :title="dialogTitleDisplay"
        v-model="dialog.visible"
        width="600px"
        @close="resetForm"
        class="retro-dialog"
        :show-close="false"
    >
      <div class="dialog-stripe"></div>

      <!-- Tab 切换器 -->
      <div class="retro-tabs" v-if="currentModule === 'course' && dialog.isEdit">
        <div class="tab-item" :class="{ active: activeTab === 'info' }" @click="activeTab = 'info'">DATA_LOG</div>
        <div class="tab-item" :class="{ active: activeTab === 'files' }" @click="loadCourseFiles()">RESOURCES</div>
      </div>

      <!-- 内容区域 -->
      <div class="dialog-content">
        <el-form v-show="activeTab === 'info'" :model="form" ref="formRef" label-width="90px" :rules="getRules()" class="retro-form">
          <!-- 学生表单 -->
          <template v-if="currentModule === 'student'">
            <div class="form-row">
              <el-form-item label="ID_NO" prop="sno" style="width: 50%"><el-input v-model="form.sno" :disabled="dialog.isEdit" class="retro-form-input"/></el-form-item>
              <el-form-item label="NAME" prop="sname" style="width: 50%"><el-input v-model="form.sname" class="retro-form-input"/></el-form-item>
            </div>
            <div class="form-row">
              <el-form-item label="GEN" prop="sex" style="width: 50%"><el-radio-group v-model="form.sex" class="retro-radio-group"><el-radio label="男" border>MALE</el-radio><el-radio label="女" border>FEMALE</el-radio></el-radio-group></el-form-item>
              <el-form-item label="CLASS" prop="classno" style="width: 50%"><el-select v-model="form.classno" placeholder="SELECT" class="retro-select-inner" popper-class="retro-select-popper" filterable><el-option v-for="item in classList" :key="item.classno" :label="`[${item.classno}] ${item.major}`" :value="item.classno"/></el-select></el-form-item>
            </div>
            <div class="form-row">
              <el-form-item label="BIRTH" prop="birth" style="width: 50%"><el-date-picker v-model="form.birth" type="date" value-format="YYYY-MM-DD" class="retro-date" style="width: 100%"/></el-form-item>
              <el-form-item label="ENTRY" prop="entranceDate" style="width: 50%"><el-date-picker v-model="form.entranceDate" type="date" value-format="YYYY-MM-DD" class="retro-date" style="width: 100%"/></el-form-item>
            </div>
            <div class="form-row">
              <el-form-item label="DEPT" prop="sdept" style="width: 50%"><el-input v-model="form.sdept" class="retro-form-input"/></el-form-item>
              <el-form-item label="ZIP" prop="postcode" style="width: 50%"><el-input v-model="form.postcode" maxlength="6" class="retro-form-input"/></el-form-item>
            </div>
            <el-form-item label="ADDR" prop="homeAddr"><el-input v-model="form.homeAddr" class="retro-form-input"/></el-form-item>
          </template>

          <!-- 班级表单 -->
          <template v-else-if="currentModule === 'class'">
            <el-form-item label="CLASS_NO" prop="classno"><el-input v-model="form.classno" :disabled="dialog.isEdit" class="retro-form-input"/></el-form-item>
            <el-form-item label="MAJOR" prop="major"><el-input v-model="form.major" class="retro-form-input"/></el-form-item>
          </template>

          <!-- 课程表单 -->
          <template v-else-if="currentModule === 'course'">
            <el-form-item label="CODE" prop="cno"><el-input v-model="form.cno" :disabled="dialog.isEdit" class="retro-form-input"/></el-form-item>
            <el-form-item label="TITLE" prop="cname"><el-input v-model="form.cname" class="retro-form-input"/></el-form-item>
            <div class="form-row">
              <el-form-item label="CREDIT" prop="credit" style="width: 50%"><el-input-number v-model="form.credit" :min="1" :max="10" class="retro-number-input" /></el-form-item>
              <el-form-item label="HOURS" prop="period" style="width: 50%"><el-input v-model="form.period" class="retro-form-input"/></el-form-item>
            </div>
            <el-form-item label="TEACHER" prop="teacherTno">
              <el-select v-model="form.teacherTno" placeholder="SELECT INSTRUCTOR" class="retro-select-inner" popper-class="retro-select-popper" filterable>
                <el-option v-for="t in teacherList" :key="t.tno" :label="`${t.tname} (${t.title || 'Teacher'})`" :value="t.tno"/>
              </el-select>
            </el-form-item>
            <el-form-item label="TYPE" prop="type">
              <el-radio-group v-model="form.type" class="retro-radio-group"><el-radio label="必修" border>REQ</el-radio><el-radio label="选修" border>OPT</el-radio></el-radio-group>
            </el-form-item>
          </template>

          <!-- 教师表单 -->
          <template v-else>
            <el-form-item label="TEACHER_ID" prop="tno"><el-input v-model="form.tno" :disabled="dialog.isEdit" class="retro-form-input"/></el-form-item>
            <el-form-item label="NAME" prop="tname"><el-input v-model="form.tname" class="retro-form-input"/></el-form-item>
            <el-form-item label="TITLE" prop="title">
              <el-select v-model="form.title" class="retro-select-inner" popper-class="retro-select-popper">
                <el-option label="教授 (Professor)" value="教授" /><el-option label="副教授 (Assoc. Prof)" value="副教授" /><el-option label="讲师 (Lecturer)" value="讲师" /><el-option label="助教 (Assistant)" value="助教" />
              </el-select>
            </el-form-item>
          </template>
        </el-form>

        <!-- 资源管理面板 -->
        <div v-show="activeTab === 'files'" class="resource-manager">
          <div class="upload-zone">
            <input type="file" ref="fileInput" style="display: none" @change="handleUploadFile">
            <button class="retro-btn warning full-width" @click="$refs.fileInput.click()" :disabled="fileUploading">
              {{ fileUploading ? 'TRANSMITTING DATA...' : '[+] INSERT NEW DATA DISK' }}
            </button>
          </div>
          <div class="file-list-container">
            <div class="file-header">
              <span>FILENAME</span><span>TYPE</span><span>ACT</span>
            </div>
            <div class="file-body" v-loading="fileLoading" element-loading-background="rgba(0,0,0,0.5)">
              <div v-if="courseFiles.length === 0" class="empty-state">NO_DATA_FOUND_ON_DISK</div>
              <div v-for="file in courseFiles" :key="file.id" class="file-row">
                <div class="file-name" :title="file.fileName"><span class="file-icon">📄</span> {{ file.fileName }}</div>
                <div class="file-type">{{ file.fileType }}</div>
                <div class="file-actions">
                  <button class="mini-btn download" @click="downloadFile(file.fileUrl)" title="DOWNLOAD">⬇</button>
                  <button class="mini-btn delete" @click="removeFile(file.id)" title="DELETE">✖</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-actions">
          <button class="retro-btn ghost" @click="dialog.visible = false">EXIT</button>
          <button v-if="activeTab === 'info'" class="retro-btn primary" @click="submitForm">EXECUTE</button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 引入 API
import { getStudentPage, addStudent, updateStudent, deleteStudent, getAllClasses } from '../api/student.js'
import { addClass, updateClass, deleteClass } from '../api/class.js'
import { getCoursePage, addCourse, updateCourse, deleteCourse } from '../api/course.js'
import { getAllTeachers, addTeacher, updateTeacher, deleteTeacher } from '../api/teacher.js'
import { uploadFile, searchFiles, deleteFile } from '../api/resource.js'

// --- 1. 状态管理 ---
const currentModule = ref('student')
const loading = ref(false)
const total = ref(0)
const timeStr = ref('')

// 数据源
const studentData = ref([])
const classData = ref([])
const courseData = ref([])
const teacherData = ref([])
const classList = ref([])
const teacherList = ref([])

// 资源管理状态
const activeTab = ref('info')
const courseFiles = ref([])
const fileLoading = ref(false)
const fileUploading = ref(false)
const fileInput = ref(null)

// --- 2. 查询参数 (已优化) ---
const queryParams = reactive({
  current: 1,
  size: 10,
  keyword: '', // 通用关键词
  type: '',    // 课程类型
  title: ''    // 教师职称
})

// --- 3. 弹窗与表单 ---
const dialog = reactive({
  visible: false,
  title: '',
  isEdit: false
})
const form = ref({})
const formRef = ref(null)

const dialogTitleDisplay = computed(() => {
  if (activeTab.value === 'files') return `RESOURCE MANAGER: ${form.value.cname || 'UNKNOWN'}`
  return dialog.title
})

// 验证规则
const studentRules = { sno: [{ required: true, message: 'REQUIRED', trigger: 'blur' }], sname: [{ required: true, message: 'REQUIRED', trigger: 'blur' }], sex: [{ required: true, message: 'REQUIRED', trigger: 'change' }] }
const classRules = { classno: [{ required: true, message: 'REQUIRED', trigger: 'blur' }], major: [{ required: true, message: 'REQUIRED', trigger: 'blur' }] }
const courseRules = { cno: [{ required: true, message: 'REQUIRED', trigger: 'blur' }], cname: [{ required: true, message: 'REQUIRED', trigger: 'blur' }], teacherTno: [{ required: true, message: 'REQUIRED', trigger: 'change' }] }
const teacherRules = { tno: [{ required: true, message: 'REQUIRED', trigger: 'blur' }], tname: [{ required: true, message: 'REQUIRED', trigger: 'blur' }], title: [{ required: true, message: 'REQUIRED', trigger: 'change' }] }

const getRules = () => {
  if (currentModule.value === 'student') return studentRules
  if (currentModule.value === 'class') return classRules
  if (currentModule.value === 'course') return courseRules
  return teacherRules
}

const getPlaceholder = () => {
  if (currentModule.value === 'student') return 'SEARCH NAME / CLASS / DEPT'
  if (currentModule.value === 'class') return 'ENTER NO OR MAJOR'
  if (currentModule.value === 'course') return 'NAME / CODE / TEACHER'
  return 'ENTER NAME OR ID'
}

const fetchBaseData = async () => {
  try {
    const [classRes, teacherRes] = await Promise.all([getAllClasses(), getAllTeachers()])
    classList.value = classRes
    teacherList.value = teacherRes
  } catch (error) { console.error('LOAD BASE DATA FAILED:', error) }
}

const switchModule = (moduleName) => {
  if (currentModule.value === moduleName) return
  currentModule.value = moduleName
  // 重置所有筛选条件
  queryParams.current = 1
  queryParams.keyword = ''
  queryParams.type = ''
  queryParams.title = ''
  getList()
}

const getList = async () => {
  loading.value = true
  try {
    // --- 学生模块 ---
    if (currentModule.value === 'student') {
      // 对应后端 StudentController 的参数: current, size, keyword
      const params = {
        current: queryParams.current,
        size: queryParams.size,
        keyword: queryParams.keyword // 名字改为 keyword
      }
      const res = await getStudentPage(params)
      studentData.value = res.content
      total.value = res.totalElements

      // --- 班级模块 ---
    } else if (currentModule.value === 'class') {
      const res = await getAllClasses()
      let result = res
      if (queryParams.keyword) {
        // 前端过滤：班级号 或 专业名
        const k = queryParams.keyword.toLowerCase()
        result = result.filter(c =>
            c.classno.toLowerCase().includes(k) ||
            c.major.toLowerCase().includes(k)
        )
      }
      classData.value = result
      total.value = result.length

      // --- 课程模块 (关键修改) ---
    } else if (currentModule.value === 'course') {
      // 假设 api/course.js 的 getCoursePage 已经更新为接受参数，例如: return request.get('/course/list', { params })
      // 如果没有更新 API文件，这里我们先获取所有，然后在前端做强大的多字段过滤
      const res = await getCoursePage({ keyword: queryParams.keyword })

      let result = res
      // 前端多字段过滤 (兼容后端未生效的情况)
      if (queryParams.keyword) {
        const k = queryParams.keyword.toLowerCase()
        result = result.filter(c =>
            c.cname.toLowerCase().includes(k) ||       // 搜课程名
            c.cno.toLowerCase().includes(k) ||         // 搜课号
            (c.teacher && c.teacher.tname.toLowerCase().includes(k)) // 搜老师名
        )
      }

      if (queryParams.type) {
        result = result.filter(c => c.type === queryParams.type)
      }
      courseData.value = result
      total.value = result.length

      // --- 教师模块 ---
    } else {
      const res = await getAllTeachers()
      let result = res
      if (queryParams.keyword) {
        const k = queryParams.keyword.toLowerCase()
        result = result.filter(t =>
            t.tname.toLowerCase().includes(k) ||
            t.tno.toLowerCase().includes(k)
        )
      }
      if (queryParams.title) {
        result = result.filter(t => t.title === queryParams.title)
      }
      teacherData.value = result
      total.value = result.length
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('DATA RETRIEVAL FAILED')
  } finally {
    loading.value = false
  }
}


const handleQuery = () => { queryParams.current = 1; getList() }

const handleAdd = () => {
  dialog.isEdit = false
  dialog.visible = true
  activeTab.value = 'info'
  if (currentModule.value === 'student') { dialog.title = 'NEW STUDENT ENTRY'; form.value = { sex: '男' } }
  else if (currentModule.value === 'class') { dialog.title = 'NEW CLASS ENTRY'; form.value = {} }
  else if (currentModule.value === 'course') { dialog.title = 'NEW COURSE ENTRY'; form.value = { type: '必修', credit: 2 } }
  else { dialog.title = 'NEW TEACHER ENTRY'; form.value = { title: '讲师' } }
}

const handleEdit = (row) => {
  dialog.isEdit = true
  dialog.visible = true
  activeTab.value = 'info'
  form.value = JSON.parse(JSON.stringify(row))

  if (currentModule.value === 'student') dialog.title = 'MODIFY STUDENT'
  else if (currentModule.value === 'class') dialog.title = 'MODIFY CLASS'
  else if (currentModule.value === 'course') {
    dialog.title = 'MODIFY COURSE'
    if (row.teacher) form.value.teacherTno = row.teacher.tno
  }
  else dialog.title = 'MODIFY TEACHER'
}

const handleDelete = (id) => {
  ElMessageBox.confirm(`DELETE DATA BLOCK [${id}]?`, 'WARNING', { confirmButtonText: 'EXECUTE', cancelButtonText: 'ABORT', type: 'warning', customClass: 'retro-message-box' }).then(async () => {
    if (currentModule.value === 'student') await deleteStudent(id)
    else if (currentModule.value === 'class') await deleteClass(id)
    else if (currentModule.value === 'course') await deleteCourse(id)
    else await deleteTeacher(id)
    ElMessage.success('DELETED SUCCESS')
    getList()
  })
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      if (currentModule.value === 'student') {
        !dialog.isEdit ? await addStudent(form.value) : await updateStudent(form.value)
      } else if (currentModule.value === 'class') {
        !dialog.isEdit ? await addClass(form.value) : await updateClass(form.value)
        fetchBaseData()
      } else if (currentModule.value === 'course') {
        const submitData = { ...form.value, teacher: { tno: form.value.teacherTno } }
        !dialog.isEdit ? await addCourse(submitData) : await updateCourse(submitData)
      } else {
        !dialog.isEdit ? await addTeacher(form.value) : await updateTeacher(form.value)
        fetchBaseData()
      }
      ElMessage.success('OPERATION SUCCESS')
      dialog.visible = false
      getList()
    }
  })
}

const resetForm = () => {
  form.value = {}
  courseFiles.value = []
  if(formRef.value) formRef.value.resetFields()
}

// --- 资源管理逻辑 ---

const loadCourseFiles = async () => {
  activeTab.value = 'files'
  courseFiles.value = [] // 先清空

  // 1. 获取当前弹窗中的课程名
  const currentCourseName = form.value.cname
  if (!currentCourseName) {
    console.warn('没有课程名，跳过搜索')
    return
  }

  fileLoading.value = true
  try {
    // 2. 发起请求
    const res = await searchFiles(currentCourseName)

    // 3. 处理响应 (关键点)
    console.log('文件列表响应:', res) // 打开浏览器控制台(F12)看这个输出

    // 情况A: 你的 request.js 拦截器直接返回了 res.data (常见情况)
    if (res.code === 200 && Array.isArray(res.data)) {
      courseFiles.value = res.data
    }
    // 情况B: 你的 request.js 返回的是 axios 原始 response
    else if (res.data && res.data.code === 200 && Array.isArray(res.data.data)) {
      courseFiles.value = res.data.data
    }
    // 情况C: 某些拦截器直接把 data 里的内容返回了
    else if (Array.isArray(res)) {
      courseFiles.value = res
    }
    // 兜底：如果上面都没匹配，打印错误方便调试
    else {
      console.error('无法解析文件数据，数据结构:', res)
    }

  } catch (e) {
    console.error('获取文件列表失败:', e)
    ElMessage.error('无法加载资源列表')
  } finally {
    fileLoading.value = false
  }
}

const handleUploadFile = async (e) => {
  const file = e.target.files[0]
  if (!file) return

  if (!form.value.cname) {
    ElMessage.warning('请先填写课程名称并保存，或确认课程名不为空')
    return
  }

  fileUploading.value = true
  const formData = new FormData()
  formData.append('file', file)
  formData.append('courseName', form.value.cname)

  try {
    const res = await uploadFile(formData)
    if (res) {
      ElMessage.success('上传成功')
      loadCourseFiles() // 刷新列表
    } else {
      ElMessage.error('上传异常')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('上传失败')
  } finally {
    fileUploading.value = false
    e.target.value = ''
  }
}

const downloadFile = (url) => { window.open(url, '_blank') }

const removeFile = (id) => {
  ElMessageBox.confirm('ERASE FILE PERMANENTLY?', 'WARNING', { confirmButtonText: 'ERASE', cancelButtonText: 'CANCEL', type: 'warning', customClass: 'retro-message-box' }).then(async () => {
    try {
      await deleteFile(id)
      ElMessage.success('FILE DELETED')
      loadCourseFiles()
    } catch (error) {
      console.error(error)
      ElMessage.error('DELETE FAILED')
    }
  })
}

// --- 时钟 ---
let timer = null
const updateTime = () => {
  const now = new Date()
  timeStr.value = now.toLocaleTimeString('en-US', { hour12: false })
}

onMounted(() => {
  getList()
  fetchBaseData()
  updateTime()
  timer = setInterval(updateTime, 1000)
})
onUnmounted(() => { if(timer) clearInterval(timer) })
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

.console-layout { display: flex; flex: 1; overflow: hidden; }

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
.nav-label { font-family: var(--font-display); color: var(--c-muted); font-size: 14px; text-align: center; margin-bottom: 10px; letter-spacing: 2px; }
.nav-item {
  height: 60px; background: #34495e; border: 2px solid #455a64; border-radius: 6px; display: flex; align-items: center; padding: 0 15px; cursor: pointer; transition: all 0.2s; position: relative; overflow: hidden;
}
.nav-item:hover { background: #3e5871; border-color: var(--c-text-light); }
.nav-item.active { background: #2c3e50; border-color: var(--c-primary); box-shadow: inset 0 0 15px rgba(26, 188, 156, 0.2); }
.nav-item.disabled { opacity: 0.5; cursor: not-allowed; border-style: dashed; }
.nav-text { flex: 1; font-weight: bold; font-size: 16px; color: var(--c-text-light); }
.nav-item.active .nav-text { color: var(--c-primary); text-shadow: 0 0 5px var(--c-primary); }
.nav-icon { font-size: 20px; }
.indicator { width: 8px; height: 8px; background: #555; border-radius: 50%; margin-right: 15px; box-shadow: inset 0 1px 2px rgba(0,0,0,1); }
.nav-item.active .indicator { background: var(--c-highlight); box-shadow: 0 0 8px var(--c-highlight); }
.indicator.off { background: #330000; }

/* 右侧内容区 */
.content-area { flex: 1; display: flex; flex-direction: column; background: #25303b; overflow: hidden; }

/* Header */
.panel-header {
  height: 70px; background: var(--c-accent); padding: 0 30px; display: flex; justify-content: space-between; align-items: center; border-bottom: 4px solid var(--c-bg-dark);
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
.control-deck { padding: 15px 20px; border-bottom: 2px dashed var(--c-muted); background: rgba(0,0,0,0.1); }
.module-label { color: var(--c-primary); font-size: 12px; margin-bottom: 5px; letter-spacing: 1px; }
.input-group { display: flex; gap: 10px; }
.retro-input, .retro-select {
  background: var(--c-bg-dark); border: 2px solid var(--c-muted); color: var(--c-highlight); padding: 8px 12px; font-family: var(--font-mono); font-size: 16px; border-radius: 4px; outline: none;
}
.retro-input:focus, .retro-select:focus { border-color: var(--c-highlight); }

/* 表格与文字 */
.mono-text { font-family: 'Courier New', monospace; font-size: 14px; color: var(--c-text-light); opacity: 0.9; }
.retro-table .el-table__cell { padding: 8px 0 !important; }
.form-row { display: flex; gap: 15px; margin-bottom: 0px; }

/* 按钮 */
.retro-btn { border: none; padding: 0 20px; font-family: var(--font-display); font-size: 16px; cursor: pointer; border-radius: 4px; transition: transform 0.1s; }
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
  font-family: var(--font-mono); border: 2px solid var(--c-muted) !important; flex: 1;
}

.course-tag { padding: 2px 6px; border-radius: 2px; font-size: 12px; font-weight: bold; }
.course-tag.required { background: var(--c-danger); color: white; }
.course-tag.elective { background: var(--c-primary); color: var(--c-bg-dark); }
.digital-number { font-family: 'Impact'; letter-spacing: 1px; color: var(--c-highlight); font-size: 18px; }
.retro-avatar { width: 30px; height: 30px; border-radius: 4px; display: flex; align-items: center; justify-content: center; font-weight: bold; color: #fff; }
.av-cyan { background: var(--c-primary); }
.av-orange { background: var(--c-accent); }
.gender-tag { font-size: 12px; padding: 2px 6px; border-radius: 4px; border: 1px solid; }
.tag-male { color: var(--c-primary); border-color: var(--c-primary); }
.tag-female { color: var(--c-highlight); border-color: var(--c-highlight); }
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

/* 弹窗 */
.retro-dialog { background: var(--c-bg-dark) !important; border: 4px solid var(--c-highlight) !important; border-radius: 10px !important; min-height: 400px; }
.retro-dialog .el-dialog__header { background: var(--c-highlight); padding: 10px 20px; margin: 0; }
.retro-dialog .el-dialog__title { color: var(--c-bg-dark) !important; font-family: var(--font-display); font-size: 20px; }
.dialog-stripe { height: 10px; background: repeating-linear-gradient(45deg, var(--c-accent), var(--c-accent) 10px, var(--c-bg-dark) 10px, var(--c-bg-dark) 20px); margin-bottom: 20px; }
.retro-form .el-form-item__label { color: var(--c-primary) !important; font-family: var(--font-mono); }
.retro-form-input .el-input__wrapper, .retro-number-input .el-input__wrapper { background-color: rgba(0,0,0,0.3) !important; box-shadow: 0 0 0 1px var(--c-muted) inset !important; }
.retro-form-input input { color: #fff !important; font-family: var(--font-mono); }
.dialog-actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; border-top: 1px solid var(--c-muted); padding-top: 10px; }

/* 背景特效 */
.noise-overlay { position: absolute; top: 0; left: 0; width: 100%; height: 100%; pointer-events: none; z-index: 1; opacity: 0.05; background: url('data:image/svg+xml;utf8,%3Csvg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg"%3E%3Cfilter id="noiseFilter"%3E%3CfeTurbulence type="fractalNoise" baseFrequency="0.65" numOctaves="3" stitchTiles="stitch"/%3E%3C/filter%3E%3Crect width="100%25" height="100%25" filter="url(%23noiseFilter)"/%3E%3C/svg%3E'); }
.bg-circle { position: absolute; border-radius: 50%; z-index: 0; opacity: 0.1; }
.circle-1 { width: 600px; height: 600px; background: var(--c-primary); top: -200px; right: -100px; }
.circle-2 { width: 400px; height: 400px; background: var(--c-accent); bottom: -100px; left: -100px; }


.retro-select-inner { width: 100%; }
:deep(.retro-select-inner .el-input__wrapper) {
  background-color: rgba(0, 0, 0, 0.3) !important; box-shadow: 0 0 0 1px var(--c-muted) inset !important; border-radius: 4px !important;
}
:deep(.retro-select-inner .el-input__wrapper.is-focus) { box-shadow: 0 0 0 2px var(--c-highlight) inset !important; }
:deep(.retro-select-inner .el-input__inner) { color: var(--c-highlight) !important; font-family: var(--font-mono) !important; font-weight: bold; }

.retro-select-popper {
  background-color: var(--c-bg-dark) !important; border: 2px solid var(--c-highlight) !important; border-radius: 0px !important; box-shadow: 10px 10px 0 rgba(0,0,0,0.5) !important;
}
.retro-select-popper .el-select-dropdown__item { color: var(--c-text-light) !important; font-family: var(--font-mono) !important; padding: 0 15px !important; }
.retro-select-popper .el-select-dropdown__item.hover, .retro-select-popper .el-select-dropdown__item:hover {
  background-color: var(--c-primary) !important; color: var(--c-bg-dark) !important;
}
.retro-select-popper .el-select-dropdown__item.selected { color: var(--c-highlight) !important; font-weight: bold; }
.retro-select-popper .el-select-dropdown__item.selected::after { content: '<'; position: absolute; right: 10px; font-weight: bold; }
.retro-select-popper .el-popper__arrow { display: none; }


.retro-tabs {
  display: flex; border-bottom: 2px solid var(--c-muted); margin-bottom: 20px; background: rgba(0,0,0,0.2);
}
.tab-item {
  flex: 1; text-align: center; padding: 10px 0; font-family: var(--font-display); font-size: 18px; cursor: pointer; color: var(--c-muted); transition: all 0.2s; border-bottom: 4px solid transparent;
}
.tab-item:hover { color: var(--c-text-light); background: rgba(255,255,255,0.05); }
.tab-item.active { color: var(--c-highlight); border-bottom-color: var(--c-highlight); background: rgba(241, 196, 15, 0.1); text-shadow: 0 0 5px var(--c-highlight); }

.resource-manager { min-height: 300px; display: flex; flex-direction: column; gap: 15px; }
.upload-zone { margin-bottom: 10px; }
.full-width { width: 100%; }

.file-list-container { border: 1px solid var(--c-muted); background: rgba(0,0,0,0.3); flex: 1; display: flex; flex-direction: column; }
.file-header { display: flex; background: var(--c-muted); color: var(--c-bg-dark); font-weight: bold; padding: 5px 10px; font-size: 14px; }
.file-header span:nth-child(1) { flex: 2; }
.file-header span:nth-child(2) { flex: 1; }
.file-header span:nth-child(3) { width: 80px; text-align: center; }

.file-body { flex: 1; overflow-y: auto; max-height: 250px; }
.file-row { display: flex; align-items: center; padding: 8px 10px; border-bottom: 1px dashed rgba(127, 140, 141, 0.3); transition: background 0.2s; }
.file-row:hover { background: rgba(26, 188, 156, 0.1); }
.file-name { flex: 2; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; color: var(--c-text-light); }
.file-type { flex: 1; color: var(--c-muted); font-size: 12px; text-transform: uppercase; }
.file-actions { width: 80px; display: flex; justify-content: center; gap: 5px; }
.mini-btn { width: 24px; height: 24px; border: none; border-radius: 4px; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 12px; }
.mini-btn.download { background: var(--c-primary); color: var(--c-bg-dark); }
.mini-btn.delete { background: var(--c-danger); color: #fff; }
.empty-state { padding: 20px; text-align: center; color: var(--c-muted); font-style: italic; }
</style>
