<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="handleVisibleUpdate"
    :title="isEdit ? '修改课程' : '新增课程'"
    width="50%"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="课程名称" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>

      <el-form-item label="课程简介" prop="description">
        <el-input v-model="form.description" type="textarea" />
      </el-form-item>

      <el-form-item label="课程作者" prop="author">
        <el-input v-model="form.author" />
      </el-form-item>

      <!--  封面上传带预览 -->
      <el-form-item label="课程封面" prop="coverUrl">
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

   <el-form-item label="课程视频" prop="videoUrl">
  <el-upload
    drag
    class="video-upload"
    ref="videoUploadRef"
    name="video"
    accept="video/mp4"
    :action="uploadUrl"
    :file-list="videoFileList"
    :limit="1"
    :headers="{ Authorization: token }"
    :on-success="handleVideoSuccess"
    :before-upload="beforeVideoUpload"
    :show-file-list="false"
  >
    <template #default>
      <div class="upload-inner">
        <template v-if="form.videoUrl">
          <span class="uploaded-text">已上传视频：{{ extractFileName(form.videoUrl) }}</span>
          <el-button type="text" @click.stop="handleVideoRemove" class="remove-btn">删除</el-button>
        </template>
        <template v-else>
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">
            拖拽文件到此处，或 <em>点击上传视频</em>
          </div>
        </template>
      </div>
    </template>
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
import { ref, watch, reactive } from "vue";
import { ElMessage } from "element-plus";
import { handleAdd, handleRefactor,removeFile } from "../api/index";
import { useUserInfoStore } from "../stores/userInfo";



// props
const props = defineProps<{
  visible: boolean;
  isEdit: boolean;
  course: Record<string, any> | null;
}>();

function extractFileName(url: string): string {
  if (!url) return "";
  return decodeURIComponent(url.split("/").pop() || "");
}

const userInfo = useUserInfoStore();
const token = userInfo.token;

const emit = defineEmits(["update:visible", "refresh"]);

const form = reactive({
  id: null,
  name: "",
  description: "",
  author: "",
  coverUrl: "",
  videoUrl: "",
});

const formRef = ref();

const rules = {
  name: [{ required: true, message: "请输入课程名称", trigger: "blur" }],
  author: [{ required: true, message: "请输入课程作者", trigger: "blur" }],
  description: [{ required: true, message: "请输入课程简介", trigger: "blur" }],
  coverUrl: [
    { required: true, message: "请传入封面", trigger: "change" },
    {
      validator: (rule, value, callback) => {
        if (form.coverUrl === "") {
          callback(new Error("请上传"));
        } else {
          callback();
        }
      },
    },
  ],
  videoUrl: [
    { required: true, message: "请传入视频", trigger: "change" },
    {
      validator: (rule, value, callback) => {
        if (form.videoUrl === "") {
          callback(new Error("请上传"));
        } else {
          callback();
        }
      },
    },
  ],
};

const uploadUrl = "http://localhost:8080/upload/uploadFiles";

const coverFileList = ref<any[]>([]);
const videoFileList = ref<any[]>([]);

watch(
  () => props.course,
  (newCourse) => {
    if (newCourse) {
      Object.assign(form, {
        id: newCourse.id,
        name: newCourse.course_name, // 显式映射字段
        description: newCourse.description,
        author: newCourse.author,
        coverUrl: newCourse.cover_url, // 注意字段命名一致性
        videoUrl: newCourse.video_url,
      });

      coverFileList.value = form.coverUrl
        ? [
            {
              name: extractFileName(form.coverUrl),
              url: form.coverUrl,
            },
          ]
        : [];

      videoFileList.value = form.videoUrl
        ? [
            {
              name: extractFileName(form.videoUrl),
              url: form.videoUrl,
            },
          ]
        : [];
    } else {
      Object.assign(form, {
        id: null,
        name: "",
        description: "",
        author: "",
        coverUrl: "",
        videoUrl: "",
      });
      coverFileList.value = [];
      videoFileList.value = [];
    }
  },
  { immediate: true }
);

function extractUrl(response: any): string {
  return response?.data?.url || "";
}

function handleVisibleUpdate(val: boolean) {
  emit("update:visible", val);
}

function handleCoverSuccess(response: any) {
  form.coverUrl = extractUrl(response);
  formRef.value.validateField("coverUrl"); // 手动触发验证
}

async function handleCoverRemove() {
  const fileName = form.coverUrl.replace("/uploads/","")
  await removeFile(fileName)
  form.coverUrl = "";
  formRef.value.validateField("coverUrl"); // 手动触发验证
}
const videoUploadRef = ref();
function handleVideoSuccess(response: any) {
  form.videoUrl = extractUrl(response);
  formRef.value.validateField("videoUrl");
  if (videoUploadRef.value) {
    videoUploadRef.value.clearFiles();
  }
   videoFileList.value = [
    {
      name: form.name,
      url: form.videoUrl,
    },
  ];
}

async function handleVideoRemove() {
  const fileName = form.videoUrl.replace("/uploads/","")
  await removeFile(fileName)
  form.videoUrl = "";
  videoFileList.value = [];
  formRef.value.validateField("videoUrl");
}



function beforeVideoUpload(file: File) {
/*   const isMP4 = file.type === "video/mp4";
  const isLt100M = file.size / 1024 / 1024 < 100;

  if (!isMP4) {
    ElMessage.error("只能上传 MP4 视频文件！");
  }

  if (!isLt100M) {
    ElMessage.error("视频大小不能超过 100MB！");
  }

  return isMP4 && isLt100M; */
  return true
}

function beforeImageUpload(file: File) {
  const isImage = file.type.startsWith("image/");
  const isLt5M = file.size / 1024 / 1024 < 5;

  if (!isImage) {
    ElMessage.error("只能上传图片文件!");
  }

  if (!isLt5M) {
    ElMessage.error("图片大小不能超过 5MB!");
  }

  return isImage && isLt5M;
}

async function handleSubmit() {
  const valid = await formRef.value?.validate();
  if (!valid) return;

  try {
    if (props.isEdit) {
      await handleRefactor('course', form);
      ElMessage.success("编辑成功");
    } else {
      await handleAdd('course', form);
      ElMessage.success("添加成功");
    }

    emit("refresh");
    emit("update:visible", false);
  } catch (error) {
    console.error("提交失败：", error);
    ElMessage.error((error as Error).message || "操作失败");
  }
}

/* async function handleSubmit() {
  const valid = await formRef.value?.validate()
  if (!valid) return

  if(props.isEdit){
    const result = await handleRefactor(form)
    ElMessage.success("编辑成功")
  }else{
    const result = await handleAdd(form)
    ElMessage.success("添加成功")
  }


  emit('refresh')
  emit('update:visible', false)
} */
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
