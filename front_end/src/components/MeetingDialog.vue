<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="handleVisibleUpdate"
    :title="isEdit ? '修改会议信息' : '新增会议'"
    width="50%"
  >
    <el-form :model="form"  ref="formRef" label-width="100px">

      <el-form-item label="会议名称" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>

      
      <el-form-item label="会议简介" prop="content" class="rich-text-item">
        <RichTextEditor 
          v-model="form.content" 
          :height="'500px'"/>
      </el-form-item>
      <el-form-item label="会议创建者" prop="creator">
        <el-input v-model="form.creator" />
      </el-form-item>

      <!-- 状态开关（仅在编辑模式下显示） -->
      <el-form-item v-if="isEdit" label="会议状态">
        <el-switch
          v-model="form.is_effective"
          active-text="有效"
          inactive-text="过期"
          active-value=1
          inactive-value=0
        />
        <el-tooltip
          content="开启表示会议有效，关闭表示会议过期"
          placement="top"
        >
          <el-icon><QuestionFilled /></el-icon>
        </el-tooltip>
      </el-form-item>
      
      <el-form-item label="开始时间" prop="start_time">
        <el-date-picker
          v-model="form.start_time"
          type="datetime"
          placeholder="选择日期和时间"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      
      <!-- 封面上传带预览 -->
      <el-form-item label="会议封面" prop="cover">
        <el-upload
          drag
          class="upload-drag"
          name="cover"
          :action="uploadUrl"
          :headers="{ Authorization: token }"
          :limit="1"
          :file-list="coverFileList"
          :on-success="handleCoverSuccess"
          :before-upload="beforeImageUpload"
          :on-remove="handleCoverRemove"
          list-type="picture-card"
        >
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">
            拖拽文件到此处，或 <em>点击上传封面</em>
          </div>
        </el-upload>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="emit('update:visible', false)">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, reactive, computed } from "vue";
import { ElMessage } from "element-plus";
import { handleAdd, handleRefactor, removeFile } from "../api/index";
import { useUserInfoStore } from "../stores/userInfo";
import RichTextEditor from '../components/RichTextEditor.vue';
import dayjs from "dayjs";



const props = defineProps<{
  visible: boolean;
  isEdit: boolean;
  meeting: Record<string, any> | null;
}>();

function extractFileName(url: string): string {
  if (!url) return "";
  return decodeURIComponent(url.split("/").pop() || "");
}

const userInfo = useUserInfoStore();
const token = userInfo.token;

const emit = defineEmits(["update:visible", "refresh"]);

// 使用计算属性确保日期格式正确
const computedMeeting = computed(() => {
  if (props.meeting && props.meeting.start_time) {
    return {
      ...props.meeting,
      start_time: dayjs(props.meeting.start_time).isValid() 
        ? dayjs(props.meeting.start_time).format("YYYY-MM-DD HH:mm:ss")
        : null,
        is_effective: props.meeting.isEffective,
    };
  }
  return props.meeting;
});

const form = reactive({
  id: "",
  name: "",
  content: "",
  creator: "",
  cover: "",
  start_time: null,
  is_effective: 1,

});

const formRef = ref();

const rules = {
  name: [{ required: true, message: "请输入会议名称", trigger: "blur" }],
  creator: [{ required: true, message: "请输入会议作者", trigger: "blur" }],
  content: [{ required: true, message: "请输入会议简介", trigger: "blur" }],
  start_time: [
    { 
      required: true, 
      message: "请选择会议开始时间",
      trigger: "change"
    }
  ],
  cover: [
    { required: true, message: "请传入封面", trigger: "change" }
  ],
};

const uploadUrl = "http://localhost:8080/upload/uploadFiles";

const coverFileList = ref<any[]>([]);

// 直接监听 computedMeeting 的变化
watch(computedMeeting, (newMeeting) => {
  if (newMeeting) {
    Object.assign(form, {
      id: newMeeting.id || "",
      name: newMeeting.name || "",
      content: newMeeting.content || "",
      creator: newMeeting.creator || "",
      cover: newMeeting.cover || "",
      start_time: newMeeting.start_time || null,
      is_effective: newMeeting.is_effective,
      
      
    });
    coverFileList.value = form.cover
      ? [{
          name: extractFileName(form.cover),
          url: form.cover,
        }]
      : [];
  } else {
    // 重置表单
    Object.assign(form, {
      id: "",
      name: "",
      content: "",
      creator: "",
      cover: "",
      start_time: null,
      is_effective: 1,
      is_effective_switch: 1
    });
    coverFileList.value = [];
  }
}, { immediate: true, deep: true });

// 监听对话框显示/隐藏状态
watch(() => props.visible, (visible) => {
  if (!visible) {
    // 关闭时重置表单
    formRef.value?.resetFields();
  }
});

function extractUrl(response: any): string {
  return response?.data?.url || "";
}

function handleVisibleUpdate(val: boolean) {
  emit("update:visible", val);
}

function handleCoverSuccess(response: any) {
  form.cover = extractUrl(response);
  formRef.value.validateField("cover");
}

async function handleCoverRemove() {
  if (form.cover) {
    const fileName = form.cover.replace("/uploads/","");
    await removeFile(fileName);
  }
  form.cover = "";
  coverFileList.value = [];
}

function beforeImageUpload(file: File) {
  const isImage = file.type.startsWith("image/");
  const isLt5M = file.size / 1024 / 1024 < 5;

  if (!isImage) {
    ElMessage.error("只能上传图片文件!");
    return false;
  }

  if (!isLt5M) {
    ElMessage.error("图片大小不能超过 5MB!");
    return false;
  }

  return true;
}

async function handleSubmit() {
  try {
    await formRef.value.validate();
    
    const payload = {
      ...form,
      is_effective: form.is_effective
    };

    if (props.isEdit) {
      await handleRefactor('meeting', payload);
      ElMessage.success("编辑成功");
    } else {
      await handleAdd('meeting', payload);
      ElMessage.success("添加成功");
    }

    emit("refresh");
    emit("update:visible", false);
  } catch (error) {
    console.error("提交失败：", error);
    if (error !== 'validate') {
      ElMessage.error((error as Error).message || "操作失败");
    }
  }
}
function onReceiveContent(html: string) {
  // 使用 form 响应式对象
  this.form.content = html;
}
</script>

<style scoped>
::v-deep(.upload-drag) {
  width: 100%;
  height: 160px;
  border: 2px dashed #d9d9d9;
  border-radius: 6px;
  box-sizing: border-box;
  transition: border-color 0.3s;
  background-color: #fafafa;
  position: relative;
}

::v-deep(.upload-drag:hover) {
  border-color: #409eff;
}

::v-deep(.upload-drag .el-upload-dragger) {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  background-color: transparent;
  border: none;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  pointer-events: all;
}

::v-deep(.upload-drag .el-upload__text em) {
  color: #409eff;
  font-style: normal;
}

::v-deep(.el-upload-list__item-thumbnail) {
  width: 100% !important;
  height: 160px !important;
  object-fit: cover !important;
  border-radius: 6px;
}

::v-deep(.el-upload-list__item-name) {
  display: none;
}

::v-deep(.el-upload--picture-card) {
  width: 100%;
  height: 160px;
  position: relative;
  border: none;
}

::v-deep(.el-upload-list--picture-card) {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 0;
}

::v-deep(.el-upload-list__item) {
  width: 100% !important;
  height: 160px !important;
  margin: 0 !important;
  border-radius: 6px;
}

::v-deep(.el-upload-list__item-thumbnail) {
  width: 100% !important;
  height: 100% !important;
  object-fit: cover;
  border-radius: 6px;
}

::v-deep(.el-upload-list__item-name) {
  display: none;
}
/* ::v-deep(.video-upload .el-upload-list__item-thumbnail) {
  height: 100px !important;
  object-fit: contain;
} */
 ::v-deep(.video-upload .el-upload-dragger) {
  width: 100%;
  height: 160px;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  font-size: 14px;
  color: #666;
  background-color: transparent;
}

::v-deep(.uploaded-video-info) {
  display: flex;
  flex-direction: column;
  align-items: center;
}

::v-deep(.uploaded-video-info .video-name) {
  color: #67c23a;
  font-size: 14px;
  margin-bottom: 5px;
}

::v-deep(.uploaded-video-info .delete-button) {
  color: red;
  padding: 0;
  font-size: 13px;
}
::v-deep(.video-upload) {
  width: 100%;
  height: 160px;
  border: 2px dashed #d9d9d9;
  border-radius: 6px;
  box-sizing: border-box;
  transition: border-color 0.3s;
  background-color: #fafafa;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

.video-upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #67c23a;
  font-size: 14px;
}

.remove-btn {
  color: red;
  margin-top: 10px;
}
::v-deep(.video-upload) {
  width: 100%;
  height: 160px;
  border: 2px dashed #d9d9d9;
  border-radius: 6px;
  background-color: #fafafa;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.upload-inner {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.uploaded-text {
  color: #67c23a;
  font-size: 14px;
  margin-bottom: 10px;
}

.remove-btn {
  color: red;
}

</style>
