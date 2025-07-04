<template>
  <div class="meeting-detail-container">
    <el-page-header @back="goBack" content="会议详情" class="page-header" />
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>
    
    <!-- 无数据状态 -->
    <div v-else-if="!meetingDetails" class="no-data">
      <el-empty description="会议信息未找到" />
      <el-button type="primary" @click="goBack" class="back-button">返回列表</el-button>
    </div>
    
    <!-- 数据展示 -->
    <el-card v-else class="detail-card">
      <div class="header-section">
        <h1 class="meeting-title">{{ meetingDetails.name }}</h1>
        <el-tag 
          :type="meetingDetails.isEffective ? 'success' : 'danger'" 
          effect="dark" 
          class="status-tag"
        >
          <el-icon class="status-icon">
            <CircleCheck v-if="meetingDetails.isEffective" />
            <CircleClose v-else />
          </el-icon>
          {{ meetingDetails.isEffective ? '有效' : '无效' }}
        </el-tag>
      </div>
      
      <div class="info-section">
        <el-descriptions title="基本信息" border>
          <el-descriptions-item label="会议编号">{{ meetingDetails.id }}</el-descriptions-item>
          <el-descriptions-item label="创建人">{{ meetingDetails.creator }}</el-descriptions-item>

          <el-descriptions-item label="开始时间">
            {{ meetingDetails.startTime ? formatDate(meetingDetails.startTime, true) : '未设置' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      
      <div class="cover-section" v-if="meetingDetails.cover">
        <h3>会议封面</h3>
        <el-image 
          :src="meetingDetails.cover" 
          :preview-src-list="[meetingDetails.cover]"
          fit="cover"
          class="cover-image"
        />
      </div>
      
      <div class="content-section">
        <h3>会议内容</h3>
        <div class="content-box">
          {{ meetingDetails.content || '暂无内容描述' }}
        </div>
      </div>
      
      <div class="action-buttons">
        <el-button type="primary" @click="goBack">返回列表</el-button>
        <el-button type="primary" @click="goToEdit">编辑会议</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { CircleCheck, CircleClose } from '@element-plus/icons-vue'
import { getMeetingById } from '../api/index'  
import { ElMessage } from 'element-plus'

export default {
  setup() {
    const route = useRoute()
    const router = useRouter()
    const meetingId = ref(route.params.id)
    console.log('完整路由参数:', route.params)
    console.log('会议ID参数:', route.params.id)
    console.log(meetingId)
    const loading = ref(true)
    const meetingDetails = ref(null)

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

    const goBack = () => {
      router.push('/meetingManage')
    }
    
    const goToEdit = () => {
      router.push(`/editMeeting/${meetingId.value}`)
    }

    // 加载会议数据 - 使用getMeetingDetail
    const loadMeetingData = async () => {
         loading.value = true
      try {
        if (!meetingId.value) {
          throw new Error('会议ID不存在')
        }

        // 重要修改：将参数包装为对象 { id: ... }
        console.log('调用前ID:', meetingId.value) // 2
        const response = await getMeetingById(meetingId.value)
        console.log('调用后ID:', meetingId.value)
        console.log('响应数据:', response)
        meetingDetails.value = response
        console.log('接收到的会议信息', meetingDetails.value)
      } catch (error) {
        ElMessage.error('获取会议详情失败: ' + (error.message || error))
        console.error('获取会议详情失败:', error)
        meetingDetails.value = null
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      console.log('挂载时ID:', meetingId.value)
      loadMeetingData()
    })

    // 监听路由参数变化
    watch(
      () => route.params.id,
      (newId) => {
        if (newId) {
          meetingId.value = newId
          loadMeetingData()
        }
      }
    )

    return {
      meetingDetails,
      formatDate,
      goBack,
      goToEdit,
      loading
    }
  },
  components: {
    CircleCheck,
    CircleClose
  }
}
</script>

<style scoped>
/* 样式保持不变 */
.meeting-detail-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
}

.detail-card {
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
}

.header-section {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.meeting-title {
  margin: 0;
  font-size: 28px;
  color: #1a73e8;
  flex: 1;
}

.status-tag {
  margin-left: 16px;
  font-size: 16px;
  height: 36px;
  line-height: 36px;
  border-radius: 18px;
  padding: 0 20px;
}

.status-icon {
  font-size: 18px;
  margin-right: 8px;
  vertical-align: middle;
}

.info-section {
  margin-bottom: 28px;
}

.cover-section {
  margin-bottom: 28px;
}

.cover-section h3,
.content-section h3 {
  font-size: 18px;
  color: #2c3e50;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f2f5;
}

.cover-image {
  width: 300px;
  height: 180px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.content-section {
  margin-bottom: 32px;
}

.content-box {
  padding: 20px;
  background-color: #f9fafc;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  line-height: 1.8;
  min-height: 150px;
  font-size: 15px;
  color: #5a5e66;
}

.action-buttons {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.no-data {
  text-align: center;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
}

.back-button {
  margin-top: 20px;
}

.loading-container {
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
}
</style>