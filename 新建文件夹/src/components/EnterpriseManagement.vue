<script setup lang="ts">
import axios from "axios";
import {computed, onMounted, reactive, ref} from "vue";
import {Delete, Message, Search} from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from 'element-plus'
import { ZoomIn } from '@element-plus/icons-vue'
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';


//弹出框
let updialogVisible=ref(false)
let adddialogVisible=ref(false)



//查询数据
let enterprise_mark=ref("")
let contact_person=ref("")
let phone=ref("")
let name=ref("")



//表格数据
let enterpriseList=ref([])

// 分页相关变量
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

//新增租户数据
const enterprise = reactive({
  name: '',
  enterprise_mark: '',
  contact_person: '',
  phone: '',
  manager_username: '',
  enterprise_icon: null,
  comment: ''
})

//修改租户数据
const up_enterprise = reactive({
  name: '',
  enterprise_mark: '',
  contact_person: '',
  phone: '',
  manager_username: '',
  enterprise_icon: null,
  comment: ''
})

//预览图片的URL引用
const previewImageUrl = ref('');
const up_previewImageUrl = ref('');

//图片处理函数
function handleIconChange(file) {
  const isImage = file.raw.type.match(/image\/(jpeg|png|jpg)/);
  const isLt5M = file.raw.size / 1024 / 1024 < 5;

  if (!isImage) {
    ElMessage.error('请上传 JPG/PNG 格式的图片');
    return false;
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB');
    return false;
  }

  //创建预览URL
  previewImageUrl.value = URL.createObjectURL(file.raw);

  const reader = new FileReader();
  reader.readAsDataURL(file.raw);
  reader.onload = () => {
    const base64Data = (reader.result as string).split(',')[1];
    enterprise.enterprise_icon = base64Data;
  };
}

// 删除图片
function removeImage(e: Event) {
  e.stopPropagation()
  previewImageUrl.value = ''
  enterprise.enterprise_icon = null
}


/// 修改时图片处理函数
function up_handleIconChange(file) {
  const isImage = file.raw.type.match(/image\/(jpeg|png|jpg)/);
  const isLt5M = file.raw.size / 1024 / 1024 < 5;

  if (!isImage) {
    ElMessage.error('请上传 JPG/PNG 格式的图片');
    return false;
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB');
    return false;
  }

  // 创建预览URL
  up_previewImageUrl.value = URL.createObjectURL(file.raw);

  const reader = new FileReader();
  reader.readAsDataURL(file.raw);
  reader.onload = () => {
    const base64Data = (reader.result as string).split(',')[1];
    up_enterprise.enterprise_icon = base64Data;
  };
}

// 修改时删除图片
function up_removeImage(e: Event) {
  e.stopPropagation();
  up_previewImageUrl.value = '';
  up_enterprise.enterprise_icon = null;
}

/// 大图预览控制
const previewDialogVisible = ref(false)
const previewDialogImageUrl = ref('')

function openPreview(url: string) {
  previewDialogImageUrl.value = url
  previewDialogVisible.value = true
}




//mounted钩子，页面加载时获取数据
onMounted(()=>{
  refresh()
})

//刷新与开始时加载数据
function refresh() {
  axios.get("http://localhost:8080/getenterprise", {
    params: {
      currentPage: currentPage.value,
      pageSize: pageSize.value
    }
  })
      .then(res => {
        //以名称顺序排序
        enterpriseList.value = res.data.data.sort((a, b) => {
          return a.name.localeCompare(b.name);
        });
        total.value = res.data.total;
      });
}

// 分页处理
function handleCurrentChange(val: number) {
  currentPage.value = val;
  if (name.value || phone.value || enterprise_mark.value || contact_person.value) {
    searchEnterprises();
  } else {
    refresh();
  }
}

function handleSizeChange(val: number) {
  pageSize.value = val;
  currentPage.value = 1;
  if (name.value || phone.value || enterprise_mark.value || contact_person.value) {
    searchEnterprises();
  } else {
    refresh();
  }
}



//查询租户逻辑
function searchEnterprises() {
  let obj = {
      enterprise_mark:enterprise_mark.value,
      contact_person:contact_person.value,
      phone: phone.value,
      name: name.value,
  };

  axios.post("http://localhost:8080/searchenterprise", obj, {
    params: {
      currentPage: currentPage.value,
      pageSize: pageSize.value
    }
  })
      .then(res => {
        //以名称顺序排序
        enterpriseList.value = res.data.data.sort((a, b) => {
          return a.name.localeCompare(b.name);
        });
        total.value = res.data.total;
      });
}

//重置查询参数
function reset(){
  name.value=""
  phone.value=""
  enterprise_mark.value= ""
  contact_person.value=""
}

// 修改租户逻辑
function upEnterprise(enterprise_mark) {
  axios.get("http://localhost:8080/getenterprisebyenterprisemark?enterprise_mark="+enterprise_mark)
      .then(res=> {
        Object.assign(up_enterprise, res.data);
        // 直接使用后端返回的图标数据
        up_previewImageUrl.value = res.data.enterprise_icon || '';
        updialogVisible.value=true;
      })
      .catch(error => {
        console.error("获取租户信息失败:", error);
      });
}

function ensureUpEnterprise(){
  if(up_enterprise.name=="" || up_enterprise.name== null
  || up_enterprise.phone==""|| up_enterprise.phone== null){
    ElMessage("租户名称，电话不能为空")
  }
  else{
    axios.post("http://localhost:8080/updateenterprise",up_enterprise)
        .then(res=>{
          if(res.data>0){
            ElMessage.success("修改成功")
            updialogVisible.value=false
            refresh()
          }
        })
  }
}




// 删除租户逻辑
function  deleteEnterprise(enterprise_mark) {
  ElMessageBox.confirm(
      '是否确认删除租户标识为'+enterprise_mark+'的数据项',
      '系统提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(() => {
        axios.get("http://localhost:8080/delenterprise?enterprise_mark="+enterprise_mark)
            .then(res=>{
              if(res.data>0){
                ElMessage({
                  type: 'success',
                  message: '删除成功',
                })
                refresh();
              }
              else{
                ElMessage.error("删除失败")
              }
            })
      })
      .catch(() => {
        ElMessage({
          type: 'info',
          message: '取消删除',
        })
      })



}

function addEnterprise() {
  // 重置表单
  Object.assign(enterprise, {
    name: "",
    enterprise_mark: "",
    contact_person: "",
    phone: "",
    manager_username: "",
    enterprise_icon: null,
    comment: ""
  })
  adddialogVisible.value = true
}


function ensureAddEnterprise() {
  if(enterprise.name==null || enterprise.name==""||
      enterprise.phone==null || enterprise.phone==""||
      enterprise.manager_username==null || enterprise.manager_username==""
  )
  {
    ElMessage("租户名称，号码,用户名不能为空")
  }
  else{
    axios.post("http://localhost:8080/addenterprise", enterprise)
        .then(res => {
          if (res.data > 0) {
            ElMessage.success('新增租户成功')
            adddialogVisible.value = false
            refresh()
          } else {
            ElMessage.error('新增租户失败')
          }
        })
        .catch(error => {
          ElMessage.error('新增租户失败: ' + error.message)
        })
  }
}


// 导出租户数据为Excel
function exportEnterprises() {
  // 准备要导出的数据
  const exportData = enterpriseList.value.map(ent => ({
    '租户标识': ent.enterprise_mark,
    '租户名称': ent.name,
    '联系人': ent.contact_person,
    '电话': ent.phone,
    '用户名': ent.manager_username,
  }));

  // 创建工作表
  const worksheet = XLSX.utils.json_to_sheet(exportData);

  // 创建工作簿
  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, worksheet, "租户数据");

  // 生成Excel文件
  const excelBuffer = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
  const data = new Blob([excelBuffer], { type: 'application/octet-stream' });

  // 保存文件
  saveAs(data, `租户数据_${new Date().toISOString().slice(0, 10)}.xlsx`);
}





</script>





<template>

  <!-- 大图预览对话框 -->
  <el-dialog v-model="previewDialogVisible" title="图片预览" width="50%">
    <img :src="previewDialogImageUrl" style="width: 100%;" alt="租户图标预览">
  </el-dialog>

  <!--修改信息弹出框-->
  <el-dialog v-model="updialogVisible" title="修改租户管理" width="700px" class="enterprise-up-dialog">
    <el-form :model="up_enterprise" label-width="100px" label-position="left">
      <el-form-item label="租户名称" prop="up_enterprise.name" :rules="[{ required: true, message: '请输入租户名称', trigger: 'red' }]">
        <el-input v-model="up_enterprise.name" placeholder="请输入租户名称" clearable />
      </el-form-item>

      <el-form-item label="租户图标">
        <el-upload
            action="#"
            :auto-upload="false"
            :on-change="up_handleIconChange"
            :show-file-list="false"
            accept=".png,.jpg,.jpeg"
            class="custom-uploader"
        >
          <template #trigger>
            <!-- 上传前状态 -->
            <div v-if="!up_previewImageUrl" class="upload-area">
              <div class="el-upload__tip">
                请上传 大小不超过 5MB 格式为 <strong>png/jpg/jpeg</strong> 的文件
              </div>
            </div>

            <!-- 上传后状态 -->
            <div v-else class="preview-wrapper">
              <!-- 悬停操作面板 -->
              <div class="preview-actions">
                <div class="action-item" @click.stop="openPreview(up_previewImageUrl)">
                  <el-icon><zoom-in /></el-icon>
                  <span>查看大图</span>
                </div>
                <div class="action-item delete-action" @click.stop="up_removeImage">
                  <el-icon><delete /></el-icon>
                  <span>删除图片</span>
                </div>
              </div>
              <!-- 修复图片绑定和样式 -->
              <img :src="up_previewImageUrl" style="width: 100%; height: 100%; object-fit: cover;" alt="租户图标预览">
            </div>
          </template>
        </el-upload>
      </el-form-item>

      <el-form-item label="联系人" prop="up_enterprise.contact_person" >
        <el-input
            v-model="up_enterprise.contact_person"
            style="width: 240px"
            placeholder="请输入联系人">
        </el-input>
      </el-form-item>

      <el-form-item label="电话" prop="up_enterprise.phone" :rules="[{ required: true, message: '请输入电话', trigger: 'red' }]">
        <el-input
            v-model="up_enterprise.phone"
            style="width: 240px"
            placeholder="请输入电话">
        </el-input>
      </el-form-item>

      <el-form-item label="用户名" prop="up_enterprise.manager_username" :rules="[{ required: true, message: '请输入要创建的用户名', trigger: 'red' }]">
        <el-text class="mx-1">{{up_enterprise.manager_username}} <br> 默认密码为该租户创建后的租户标识</el-text>
      </el-form-item>
      <el-form-item label="备注">
        <el-input
            v-model="up_enterprise.comment"
            type="textarea"
            :rows="3"
            placeholder="请输入内容"
            maxlength="200"
            show-word-limit>
        </el-input>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="updialogVisible = false">取消</el-button>
        <el-button type="primary" @click="ensureUpEnterprise">确定</el-button>
      </span>
    </template>
  </el-dialog>

  <!--添加信息弹出框-->
  <el-dialog v-model="adddialogVisible" title="添加租户管理" width="700px" class="user-add-dialog">
    <el-form :model="enterprise" label-width="100px" label-position="left">
      <el-form-item label="租户名称" prop="enterprise.name" :rules="[{ required: true, message: '请输入租户名称', trigger: 'red' }]">
        <el-input v-model="enterprise.name" placeholder="请输入租户名称" clearable />
      </el-form-item>

      <el-form-item label="租户图标">
        <el-upload
            action="#"
            :auto-upload="false"
            :on-change="handleIconChange"
            :show-file-list="false"
            accept=".png,.jpg,.jpeg"
            class="custom-uploader"
        >
          <template #trigger>
            <!-- 上传前状态 -->
            <div v-if="!previewImageUrl" class="upload-area">
              <div class="el-upload__tip">
                请上传 大小不超过 5MB 格式为 <strong>png/jpg/jpeg</strong> 的文件
              </div>
            </div>

            <!-- 上传后状态 -->
            <div v-else class="preview-wrapper" @click="openPreview(previewImageUrl)">
              <!-- 悬停操作面板 -->
              <div class="preview-actions">
                <div class="action-item" @click.stop="openPreview(previewImageUrl)">
                  <el-icon><zoom-in /></el-icon>
                  <span>查看大图</span>
                </div>
                <div class="action-item delete-action" @click.stop="removeImage">
                  <el-icon><delete /></el-icon>
                  <span>删除图片</span>
                </div>
              </div>
              <img :src="previewImageUrl" class="preview-image" alt="租户图标预览">
            </div>
          </template>
        </el-upload>
      </el-form-item>

      <el-form-item label="联系人" prop="enterprise.contact_person" :rules="[{ required: true, message: '请输入联系人', trigger: 'red' }]">
        <el-input
            v-model="enterprise.contact_person"
            style="width: 240px"
            placeholder="请输入联系人">
        </el-input>
      </el-form-item>

      <el-form-item label="电话" prop="enterprise.phone" :rules="[{ required: true, message: '请输入电话', trigger: 'red' }]">
        <el-input
            v-model="enterprise.phone"
            style="width: 240px"
            placeholder="请输入电话">
        </el-input>
      </el-form-item>

      <el-form-item label="用户名" prop="enterprise.manager_username" :rules="[{ required: true, message: '请输入要创建的用户名', trigger: 'red' }]">
        <el-input
            v-model="enterprise.manager_username"
            style="width: 240px"
            placeholder="请输入要创建的管理员用户名">
        </el-input>

      </el-form-item>

      <el-form-item >
        <div class="form-item-tip">默认密码为该租户创建后的租户标识</div>
      </el-form-item>
      <el-form-item label="备注">
        <el-input
            v-model="enterprise.comment"
            type="textarea"
            :rows="3"
            placeholder="请输入内容"
            maxlength="200"
            show-word-limit>
        </el-input>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="adddialogVisible = false">取消</el-button>
        <el-button type="primary" @click="ensureAddEnterprise">确定</el-button>
      </span>
    </template>
  </el-dialog>

  <!--布局-->
  <div class="common-layout">

    <el-container>


      <el-container>
        <!--头部-->
        <el-header height="150px">
          <span style="margin-right: 16px;">租户标识</span>
          <el-input
              v-model="enterprise_mark"
              style="width: 240px"
              placeholder="请输入租户标识">
          </el-input>
          <span style="margin-right: 16px;"></span>
          <span style="margin-right: 16px;">联系人</span>
          <el-input
              v-model="contact_person"
              style="width: 240px"
              placeholder="请输入联系人">
          </el-input>
          <span style="margin-right: 16px;"></span>
          <span style="margin-right: 16px;">电话</span>
          <el-input
              v-model="phone"
              style="width: 240px"
              placeholder="请输入手机号码">
          </el-input>
          <span style="margin-right: 16px;"></span>
          <span style="margin-right: 16px;">租户名称</span>
          <el-input
              v-model="name"
              style="width: 240px"
              placeholder="请输入租户名称">
          </el-input>
          <br />
          <br />

          <el-button type="primary" :icon="Search" @click="searchEnterprises">搜索</el-button>
          <el-button @click="reset">重置</el-button>
          <br />
          <br />

          <el-button type="success" @click="addEnterprise">新增</el-button>
          <el-button type="warning" @click="exportEnterprises">导出</el-button>

        </el-header>


        <!--主要-->
        <el-main>
          <el-table :data="enterpriseList" border>
            <el-table-column prop="enterprise_mark" label="租户标识"></el-table-column>
            <el-table-column prop="contact_person" label="联系人"></el-table-column>
            <el-table-column prop="phone" label="电话"></el-table-column>
            <el-table-column prop="name" label="租户名称"></el-table-column>
            <el-table-column prop="manager_username" label="用户名"></el-table-column>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button link type="primary" size="small" @click="upEnterprise(scope.row.enterprise_mark)">修改</el-button>
                <el-button link type="primary" size="small" @click="deleteEnterprise(scope.row.enterprise_mark)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-container">
            <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="currentPage"
                :page-sizes="[10, 20, 50, 100]"
                :page-size="pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total">
            </el-pagination>
          </div>

        </el-main>


      </el-container>
    </el-container>
  </div>


</template>



<style scoped>
/* 上传区域样式 */
.custom-uploader {
  width: 100%;
}

.upload-area {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  padding: 20px;
  text-align: center;
  background-color: #fafafa;
  cursor: pointer;
  transition: border-color 0.3s;
}

.upload-area:hover {
  border-color: #409eff;
}




.el-upload__tip {
  color: #999;
  font-size: 12px;
  line-height: 1.5;
  text-align: center;
}

/* 预览区域样式 */
.preview-wrapper {
  position: relative;
  width: 180px;
  height: 180px;
  margin: 0 auto;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  overflow: hidden;
}

.preview-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 悬停操作面板 */
.preview-actions {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.preview-wrapper:hover .preview-actions {
  opacity: 1;
}

.action-item {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 80%;
  padding: 8px;
  margin: 4px 0;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  background-color: white;
}


.delete-action {
  color: #f56c6c;
}


strong {
  font-weight: bold;
}
</style>