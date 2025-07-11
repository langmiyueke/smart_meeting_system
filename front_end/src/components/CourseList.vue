<template>
  <div>




    <!-- 搜索框区域 -->
    <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent>

      <el-form-item >
        <el-input v-model="searchForm.name" placeholder="请输入课程名称"  @keyup.enter.native="searchCourses" clearable />
        
      </el-form-item>

      <el-form-item>
        <el-radio-group v-model="searchForm.sortBy">
          <el-radio-button label="name">按课程名称</el-radio-button>
          <el-radio-button label="author">按作者名称</el-radio-button>
        </el-radio-group>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="searchCourses">搜索</el-button>
        <el-button @click="resetForm" style="margin-left: 10px">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <div class="toolbar">
      <el-button type="primary" @click="openAddDialog">新增</el-button>
      <el-button type="success" @click="exportCourses">导出</el-button>
    </div>

    <!-- 课程表格 -->
   <el-table :data="courseList" style="width: 100%">
  <el-table-column prop="id" label="编号" width="80" />
  <el-table-column prop="course_name" label="课程名称" />
  <el-table-column prop="description" label="课程简介" />
  <el-table-column prop="author" label="课程作者" />
  <el-table-column label="操作" width="180">
    <template #default="scope">
      <el-button size="mini" @click="openEditDialog(scope.row)">修改</el-button>
      <el-button size="mini" type="danger" @click="confirmDelete(scope.row.id)">删除</el-button>
    </template>
  </el-table-column>
</el-table>


<!-- 分页器 -->
<el-pagination
  v-model:current-page="findCoursesPageInfo.pageNum"
  v-model:page-size="findCoursesPageInfo.pageSize"
  :total="totalItems"
  :page-sizes="[5, 10, 20, 50]"
  layout="total, sizes, prev, pager, next, jumper"
  @size-change="handleSizeChange"
  @current-change="handlePageChange"
  class="pagination"
/>


    <!-- 弹窗组件 -->
    <CourseDialog
      v-model:visible="dialogVisible"
      :is-edit="isEdit"
      :course="selectedCourse"
      @refresh="fetchCourses"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted,watch,reactive, onUnmounted } from 'vue'
import CourseDialog from './CourseDialog.vue'
import { ElMessageBox,ElMessage } from 'element-plus'
import { deleteCourse,getCourses,clearIsDeleted } from '../api/index'
import { useRouter } from 'vue-router'

const router = useRouter()


/**
 * 实现路由跳转的方法
*/
 const goToPage = (path) => {
  router.push(path)
}

// 表单数据
const searchForm = ref({
  name: '',
  sortBy: 'name'
})

// 列表数据
const courseList = ref([])

// 分页功能
const findCoursesPageInfo = reactive({
  keyWords: '',
  pageNum: 1,
  pageSize: 5,
  sortBy: 'name',
})

// 弹窗控制
const dialogVisible = ref(false)
const isEdit = ref(false)
const selectedCourse = ref(null)

const totalItems = ref(0)

watch(
  () => searchForm.value.sortBy,
  (newValue) => {
    findCoursesPageInfo.sortBy = newValue
    findCoursesPageInfo.pageNum = 1 // 切换排序时重置页码
    fetchCourses()
  }
)


// 获取（初始化）课程数据
const fetchCourses = async () => {
  const response = await getCourses(findCoursesPageInfo)
  courseList.value = response.pageInfo.pageData
  findCoursesPageInfo.pageNum = response.pageInfo.pageNum
  findCoursesPageInfo.pageSize = response.pageInfo.pageSize
  totalItems.value = +response.pageInfo.totalSize
}


watch(
  () => [findCoursesPageInfo.pageNum, findCoursesPageInfo.pageSize],
  fetchCourses
)



const handleSizeChange = (size) => {
  findCoursesPageInfo.pageSize = size;
  findCoursesPageInfo.pageNum = 1; // 改页大小时重置到第一页
}

const handlePageChange = (page) => {
  findCoursesPageInfo.pageNum = page;
}


watch(
    () => searchForm.value.name,
    async (newValue, oldValue) => {
    findCoursesPageInfo.keyWords = newValue
    if (!newValue) {
      findCoursesPageInfo.pageNum = 1  // 重置页码
      await fetchCourses()             // 使用分页接口重新拉取数据
    }
  },
)


// 搜索
const searchCourses = () => {
  findCoursesPageInfo.keyWords = searchForm.value.name
  findCoursesPageInfo.pageNum = 1 // 搜索重置到第一页
  fetchCourses()
}


// 重置表单
// 重置表单
const resetForm = async () => {
  try {
    searchForm.value.name = '';        // 1. 清空搜索框
    searchForm.value.sortBy = 'name';  // 2. 重置排序字段
    findCoursesPageInfo.pageNum = 1;   // 3. 重置当前页
    findCoursesPageInfo.pageSize = 5;  // 4. 重置页大小
    findCoursesPageInfo.keyWords = ''; // 5. 清空关键字

    await fetchCourses();              // 6. 重新获取分页数据
  } catch (error) {
    console.error('重置表单失败:', error);
  }
};


// 新增对话框
const openAddDialog = () => {
  isEdit.value = false
  selectedCourse.value = null
  dialogVisible.value = true
}

// 编辑对话框
const openEditDialog = (course: any) => {
  isEdit.value = true
  selectedCourse.value = course
  dialogVisible.value = true
}

// 删除确认
const confirmDelete = async (courseId: number) => {
  try {
    await ElMessageBox.confirm('确认删除该课程?', '警告', {
      type: 'warning'
    })
    await deleteCourse(courseId)
    ElMessage.success("删除成功")
    fetchCourses()
  } catch (err) {
    // 用户取消删除
  }
}

// 导出课程数据
const exportCourses = async () => {
  const response = await fetch('/api/courses/export')
  const blob = await response.blob()
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'courses.xlsx'
  a.click()
  window.URL.revokeObjectURL(url)
}

onMounted(() => {
  fetchCourses()
})

async function clearData(){
  await clearIsDeleted()
} 

onUnmounted(()=>{
  clearData
})

</script>

<style scoped>
.search-form {
  margin-bottom: 20px;
}
.toolbar {
  margin-bottom: 20px;
}
.el-table {
  width: 100%;
}
</style>
