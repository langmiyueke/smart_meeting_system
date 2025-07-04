<template>
  <div>

      <!-- 新增跳转栏 -->

    <el-row>
    <el-button type="primary" @click="goToPage('/courseList')">课程管理</el-button>
    <el-button type="primary" @click="goToPage('/meetingManage')">会议管理</el-button>
    <el-button type="primary">租户管理</el-button>
    <el-button type="primary">用户管理</el-button>
    </el-row>
  


    <!-- 搜索框区域 -->
    <el-form :inline="true" :model="searchForm" class="search-form" @submit.prevent>

      <el-form-item >
        <el-input v-model="searchForm.name" placeholder="请输入会议名称"  @keyup.enter.native="searchMeetings" clearable />
        
      </el-form-item>
      <el-form-item>分类方式</el-form-item>
      <el-form-item>
        <el-radio-group v-model="searchForm.sortBy">
          <el-radio-button label="">默认排序</el-radio-button>
          <el-radio-button label="name">会议名称</el-radio-button>
          <el-radio-button label="creator">创建者名称</el-radio-button>
        </el-radio-group>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="searchMeetings">搜索</el-button>
        <el-button @click="resetForm" style="margin-left: 10px">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <div class="toolbar">
      <el-button type="primary" @click="openAddDialog">新增会议</el-button>
      <el-button type="success" @click="exportCourses">导出数据</el-button>
    </div>

    <!-- 会议表格 -->
        <el-table :data="meetingList" style="width: 100%">
        <el-table-column prop="id" label="编号" width="200" />
             <!-- 修改会议名称列，添加点击跳转 -->
      <el-table-column label="会议名称" width="300">
        <template #default="scope">
          <el-link 
            type="primary" 
            @click="viewMeetingDetail(scope.row.id)"
            class="meeting-name-link"
          >
            {{ scope.row.name }}
          </el-link>
        </template>
      </el-table-column>
        <el-table-column prop="content" label="会议简介" width="300"/>
        <el-table-column prop="creator" label="会议作者" width="300"/>
        <el-table-column prop="is_effective" label="会议状态" width="300">
        <template #default="scope">
          <!-- 当值为1时显示有效状态 -->
          <el-tag 
            v-if="scope.row.is_effective === true" 
            type="success" 
            effect="dark" 
            round
            class="status-tag"
          >
            <el-icon class="status-icon-large"><CircleCheck /></el-icon>
            有效
          </el-tag>
          
          <!-- 当值为0时显示无效状态 -->
          <el-tag 
            v-else-if="scope.row.is_effective === false" 
            type="danger" 
            effect="dark" 
            round
            class="status-tag"
          >
            <el-icon class="status-icon-large"><CircleClose /></el-icon>
            无效
          </el-tag>
        </template>
      </el-table-column> 

<el-table-column label="会议封面" width="200">

<template #default="scope">
<div class="cover-image-container">
  <template v-if="scope.row.cover && scope.row.cover.trim() !== ''">
    <img 
      :src="scope.row.cover" 
      class="cover-image"
      :alt="'会议封面：' + scope.row.name"
      @click="previewImage(scope.row.cover, scope.row.name)"
      
    >
  </template>
  <div v-else class="no-cover">
    无封面
  </div>
</div>
</template>
</el-table-column>

  <!-- 开始时间列 -->
<el-table-column prop="start_time" label="开始时间" width="220">
  <template #default="scope">
    <div v-if="scope.row.start_time" class="time-display">
      <el-icon class="time-icon"><Clock /></el-icon>
      <span class="time-text">{{ formatDate(scope.row.start_time, true) }}</span>
    </div>
    <span v-else class="no-time">
      <el-icon class="time-icon"><Warning /></el-icon>
      <span>未设置</span>
    </span>
  </template>
</el-table-column>

  <el-table-column label="操作" >
    <template #default="scope">
      <el-button size="mini" @click="openEditDialog(scope.row)">修改</el-button>
      <el-button size="mini" type="danger" @click="confirmDelete(scope.row.id)">删除</el-button>
    </template>
  </el-table-column>
</el-table>


<!-- 分页器 -->
<el-pagination
  v-model:current-page="findPageInfo.pageNum"
  v-model:page-size="findPageInfo.pageSize"
  :total="totalItems"
  :page-sizes="[5, 10, 20, 50]"
  layout="total, sizes, prev, pager, next, jumper"
  @size-change="handleSizeChange"
  @current-change="handlePageChange"
  class="pagination"
/>


    <!-- 弹窗组件 -->
    <MeetingDialog
      v-model:visible="dialogVisible"
      :is-edit="isEdit"
      :meeting="selectedMeeting"
      @refresh="fetchMeetings"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted,watch,reactive, onUnmounted } from 'vue'
import MeetingDialog from './MeetingDialog.vue'
import { ElMessageBox,ElMessage } from 'element-plus'
import { deleteMeeting,getMeetings,clearIsDeleted } from '../api/index'
import { useRouter } from 'vue-router'
import { useMeetingStore } from '../stores/meetingStore'


const router = useRouter()
const meetingStore = useMeetingStore()

const viewMeetingDetail = (meetingId) => {
  router.push(`/meetingDetail/${meetingId}`)
}


// 时间格式化函数
const formatDate = (dateString, includeTime = false) => {
  if (!dateString) return ''
  
  try {
    const date = new Date(dateString)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    
    if (!includeTime) return `${year}-${month}-${day}`
    
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}`
  } catch (error) {
    console.error('日期格式错误:', error)
    return '无效日期'
  }
}


/**
 * 实现路由跳转的方法
*/
 const goToPage = (path) => {
  router.push(path)
}

// 表单数据
const searchForm = ref({
  name: '',
  sortBy: ''
})

// 列表数据
const meetingList = ref([])

// 分页功能
const findPageInfo = reactive({
  keyWords: '',
  pageNum: 1,
  pageSize: 5,
  sortBy: '',
})

// 弹窗控制
const dialogVisible = ref(false)
const isEdit = ref(false)
const selectedMeeting = ref(null)

const totalItems = ref(0)

watch(
  () => searchForm.value.sortBy,
  (newValue) => {
    findPageInfo.sortBy = newValue
    findPageInfo.pageNum = 1 // 切换排序时重置页码
    fetchMeetings()
  }
)


// 获取（初始化）会议数据
const fetchMeetings = async () => {
  const response = await getMeetings(findPageInfo)
  meetingList.value = response.pageInfo.pageData
  findPageInfo.pageNum = response.pageInfo.pageNum
  findPageInfo.pageSize = response.pageInfo.pageSize
  totalItems.value = response.pageInfo.totalSize
}


watch(
  () => [findPageInfo.pageNum, findPageInfo.pageSize],
  fetchMeetings
)



const handleSizeChange = (size) => {
  findPageInfo.pageSize = size;
  findPageInfo.pageNum = 1; // 改页大小时重置到第一页
}

const handlePageChange = (page) => {
  findPageInfo.pageNum = page;
}


watch(
    () => searchForm.value.name,
    async (newValue, oldValue) => {
    findPageInfo.keyWords = newValue
    if (!newValue) {
      findPageInfo.pageNum = 1  // 重置页码
      await fetchMeetings()             // 使用分页接口重新拉取数据
    }
  },
)


// 搜索
const searchMeetings = () => {
  findPageInfo.keyWords = searchForm.value.name
  findPageInfo.pageNum = 1 // 搜索重置到第一页
  fetchMeetings()
}



// 重置表单
const resetForm = async () => {
  try {
    searchForm.value.name = '';        // 1. 清空搜索框
    searchForm.value.sortBy = 'name';  // 2. 重置排序字段
    findPageInfo.pageNum = 1;   // 3. 重置当前页
    findPageInfo.pageSize = 5;  // 4. 重置页大小
    findPageInfo.keyWords = ''; // 5. 清空关键字

    await fetchMeetings();              // 6. 重新获取分页数据
  } catch (error) {
    console.error('重置表单失败:', error);
  }
};


// 新增对话框
const openAddDialog = () => {
  isEdit.value = false
  selectedMeeting.value = null
  dialogVisible.value = true
}

// 编辑对话框
const openEditDialog = (meeting: any) => {
  isEdit.value = true
  selectedMeeting.value = meeting
  dialogVisible.value = true
}

// 删除确认
const confirmDelete = async (meetingId: number) => {
  try {
    await ElMessageBox.confirm('确认删除会议编号为' + meetingId +'的会议?', '警告', {
      type: 'warning'
    })
    await deleteMeeting(meetingId)
    ElMessage.success("删除成功")
    fetchMeetings()
  } catch (err) {
    // 用户取消删除
  }
}

// 导出会议数据
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
  fetchMeetings()
})

async function clearData(){
  await clearIsDeleted()
} 

onUnmounted(()=>{
  clearData
})

// 图片预览方法
const previewImage = (url: string, name: string) => {
  // 在新窗口中打开图片预览
  const imageWindow = window.open('', '_blank');
  if(imageWindow != null){
    imageWindow.document.write(`
    <html>
    <head>
      <title>${name} - 会议封面</title>
      <style>
        body { 
          margin: 0; 
          padding: 20px; 
          background: #f0f2f5; 
          display: flex; 
          justify-content: center; 
          align-items: center; 
          height: 100vh; 
          text-align: center; 
        }
        img { 
          max-width: 90%; 
          max-height: 90vh; 
          box-shadow: 0 10px 30px rgba(0,0,0,0.2); 
          border-radius: 8px;
        }
        .title {
          color: #1a73e8; 
          margin-bottom: 20px; 
          font-size: 24px; 
          font-weight: 600;
        }
      </style>
    </head>
    <body>
      <div>
        <div class="title">${name} - 会议封面</div>
        <img src="${url}" alt="会议封面图">
      </div>
    </body>
    </html>
  `);
  }
  
}

</script>

<style scoped>
.search-form {
  margin: 0 0 20px;
  padding: 16px 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.el-form-item {
  margin-bottom: 16px;
  margin-right: 24px;
}

.toolbar {
  display: flex;
  margin: 20px 0;
  gap: 10px;
}

.el-table {
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.el-table ::v-deep .el-table__header-wrapper {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.04);
}

.el-table ::v-deep th.el-table__cell {
  background-color: #f5f7fa;
  color: #1a73e8;
  font-weight: 600;
}

.el-table ::v-deep .el-table__row:nth-child(2n) {
  background-color: #f9fafc;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 按钮组美化 */
.el-row {
  display: flex;
  margin-bottom: 20px;
  gap: 12px;
}

.el-button {
  border-radius: 6px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.12);
}

/* 图片容器样式优化 */
.cover-image-container {
  display: flex;
  justify-content: center;
  padding: 8px 0;
}

/* 封面图片样式 */
.cover-image {
  width: 70px;
  height: 70px;
  border-radius: 8px;
  object-fit: cover;
  transition: all 0.3s ease;
  background-color: #f5f7fa;
  border: 1px solid rgba(0, 0, 0, 0.06);
  cursor: pointer;
}

/* 图片悬停效果 */
.cover-image:hover {
  transform: scale(1.05);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
}

/* 无封面时的状态样式 */
.no-cover {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #e6f4ff 0%, #d0e4ff 100%);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1a73e8;
  font-size: 14px;
  font-weight: 500;
}

/* 时间单元格样式 */
.time-cell {
  font-weight: 500;
  color: #2c3e50;
}

/* 状态标签美化 */
.el-tag {
  font-weight: 600;
  border-radius: 12px;
  padding: 0 12px;
  border: none;
}

/* 操作按钮样式 */
.el-button--mini {
  border-radius: 4px;
  padding: 6px 10px;
  font-size: 12px;
}

/* 搜索区域标签样式 */
.el-form-item:has(.el-radio-group) {
  background: #f5f7fa;
  padding: 8px 16px;
  border-radius: 6px;
}

.status-container {
  display: flex;
  justify-content: center;
}

.status-tag {
  padding: 6px 12px;
  font-weight: 600;
  letter-spacing: 0.5px;
  transition: all 0.3s ease;
}

.status-icon {
  margin-right: 5px;
  font-weight: bold;
  vertical-align: middle;
}

.status-text {
  vertical-align: middle;
}

/* 封面图片美化 */
.cover-image-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.cover-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  cursor: pointer;
  border: 1px solid #eee;
  transition: all 0.3s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.cover-image:hover {
  transform: scale(1.05);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
}

.no-cover {
  width: 80px;
  height: 80px;
  background: #f9f9f9;
  border: 1px dashed #dcdfe6;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #909399;
  font-size: 12px;
  padding: 10px;
}

.no-cover-icon {
  font-size: 24px;
  margin-bottom: 5px;
  color: #c0c4cc;
}

/* 时间显示美化 */
.time-display {
  display: flex;
  align-items: center;
  color: #5a5e66;
}

.time-icon {
  margin-right: 8px;
  color: #409eff;
}

.time-text {
  font-weight: 500;
}

.no-time {
  display: flex;
  align-items: center;
  color: #f56c6c;
}

.no-time .time-icon {
  color: #f56c6c;
}

.status-tag {
  padding: 8px 15px;
  font-size: 14px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
}

/* 增大的状态图标 */
.status-icon-large {
  font-size: 20px; /* 增大图标尺寸 */
  margin-right: 8px; /* 增加与文字间距 */
  width: 1.2em; /* 保持比例 */
  height: 1.2em; /* 保持比例 */
  vertical-align: middle;
}

/* 有效状态的特殊样式 */
.el-tag--success {
  background-color: #63e31d;
  border-color: #fbfbfb;
}

/* 无效状态的特殊样式 */
.el-tag--danger {
  background-color: #f00000;
  border-color: #fcd3d3;
}

/* 封面图片样式 */
.cover-image {
  width: 85px; /* 增大图片尺寸以匹配更大的状态图标 */
  height: 85px; /* 增大图片尺寸以匹配更大的状态图标 */
  /* ...其他图片样式保持不变... */
}

/* 为了保持表格行高的一致性 */
.el-table__row .el-tag {
  line-height: 32px; /* 增加行高以容纳更大的图标 */
}

</style>