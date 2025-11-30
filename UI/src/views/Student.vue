<template>
  <div class="app-container" style="padding: 20px;">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="mb-20">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="学生姓名">
          <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button type="success" @click="handleAdd">新增学生</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格区域 -->
    <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%; margin-top: 20px;">
      <el-table-column prop="sno" label="学号" width="120" />
      <el-table-column prop="sname" label="姓名" width="120" />
      <el-table-column prop="sex" label="性别" width="80" />
      <el-table-column prop="birth" label="出生日期" width="120" />
      <el-table-column prop="entranceDate" label="入学时间" width="120" />
      <el-table-column prop="classno" label="班级编号" width="100" />
      <el-table-column prop="sdept" label="院系" />
      <el-table-column prop="homeAddr" label="家庭住址" show-overflow-tooltip />

      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button link type="danger" size="small" @click="handleDelete(scope.row.sno)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div style="margin-top: 20px; text-align: right;">
      <el-pagination
          v-model:current-page="queryParams.current"
          v-model:page-size="queryParams.size"
          :page-sizes="[5, 10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="getList"
          @current-change="getList"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" @close="resetForm">
      <el-form :model="form" ref="formRef" label-width="100px" :rules="rules">
        <el-form-item label="学号" prop="sno">
          <el-input v-model="form.sno" :disabled="!!form.sno && dialog.title === '修改学生'" placeholder="8位字符" maxlength="8"/>
        </el-form-item>
        <el-form-item label="姓名" prop="sname">
          <el-input v-model="form.sname" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="form.sex">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="出生日期" prop="birth">
          <el-date-picker v-model="form.birth" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%"/>
        </el-form-item>
        <el-form-item label="入学时间" prop="entranceDate">
          <el-date-picker v-model="form.entranceDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%"/>
        </el-form-item>
        <el-form-item label="班级编号" prop="classno">
          <el-input v-model="form.classno" placeholder="例如: 101" maxlength="3"/>
        </el-form-item>
        <el-form-item label="院系" prop="sdept">
          <el-input v-model="form.sdept" />
        </el-form-item>
        <el-form-item label="家庭住址" prop="homeAddr">
          <el-input v-model="form.homeAddr" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialog.visible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getStudentPage, addStudent, updateStudent, deleteStudent } from '../api/student.js'
import { ElMessage, ElMessageBox } from 'element-plus'

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

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getStudentPage(queryParams)
    // 后端返回的是 Page 对象
    // res 已经是 request.js 解包后的 data
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
  form.value = { sex: '男' } // 默认值
}

const handleEdit = (row) => {
  dialog.title = '修改学生'
  dialog.visible = true
  // 深拷贝防止直接修改表格数据
  form.value = JSON.parse(JSON.stringify(row))
}

const handleDelete = (sno) => {
  ElMessageBox.confirm('确认删除该学生吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteStudent(sno)
    ElMessage.success('删除成功')
    getList()
  })
}

const submitForm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      if (dialog.title === '新增学生') {
        await addStudent(form.value)
        ElMessage.success('新增成功')
      } else {
        await updateStudent(form.value)
        ElMessage.success('修改成功')
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
})
</script>