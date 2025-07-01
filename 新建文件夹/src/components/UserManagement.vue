<script setup lang="ts">
import axios from "axios";
import {computed, onMounted, reactive, ref} from "vue";
import {Message, Search} from "@element-plus/icons-vue";
import dayjs from 'dayjs';
import { ElMessage, ElMessageBox } from 'element-plus'

//弹出框
let updialogVisible=ref(false)
let adddialogVisible=ref(false)



//查询数据
let username=ref("")
let phone=ref("")
let state=ref("")
let create_time = ref([new Date(''), new Date('')])
const startDate = computed(() => dayjs(create_time.value[0]).format('YYYY-MM-DDTHH:mm:ss'))
const endDate = computed(() => dayjs(create_time.value[1]).format('YYYY-MM-DDTHH:mm:ss'))


//表格数据
let userList=ref([])

// 分页相关变量
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);


//修改信息弹出框 数据

let up_user = reactive({
  nickname: "",
  department: "",
  phone: "",
  email: "",
  sex: "",
  state: "",
  job: "",
  role: "",
  comment:""
});

//添加信息弹出框 数据
let user = reactive({
  nickname: "",
  department: "",
  phone: "",
  email: "",
  username:"",
  password:"",
  sex: "",
  state: "正常",
  job: "",
  role: "",
  comment:""
});



//mounted钩子，页面加载时获取数据
onMounted(()=>{
  refresh()
})

//刷新与开始时加载数据
function refresh() {
  axios.get("http://localhost:8080/getusers", {
    params: {
      currentPage: currentPage.value,
      pageSize: pageSize.value
    }
  })
      .then(res => {
        userList.value = res.data.data;
        total.value = res.data.total;
      });
}

// 分页处理
function handleCurrentChange(val: number) {
  currentPage.value = val;
  if (username.value || phone.value || state.value || create_time.value[0]) {
    searchUsers();
  } else {
    refresh();
  }
}

function handleSizeChange(val: number) {
  pageSize.value = val;
  currentPage.value = 1;
  if (username.value || phone.value || state.value || create_time.value[0]) {
    searchUsers();
  } else {
    refresh();
  }
}



//查询用户逻辑
function searchUsers() {
  let obj = {
    users: {
      username: username.value,
      phone: phone.value,
      state: state.value
    },
    start: startDate.value,
    end: endDate.value
  };

  axios.post("http://localhost:8080/searchusers", obj, {
    params: {
      currentPage: currentPage.value,
      pageSize: pageSize.value
    }
  })
      .then(res => {
        userList.value = res.data.data;
        total.value = res.data.total;
      });
}

//重置查询参数
function reset(){
   username.value=""
   phone.value=""
   create_time.value= [new Date(''), new Date('')]
   state.value=""
}


// 修改用户逻辑

function upUser(id) {
  axios.get("http://localhost:8080/getuserbyid?id="+id)
      .then(res => {
        Object.assign(up_user, res.data);
        updialogVisible.value=true
      })
}

function ensureUpUser(){
  if(up_user.nickname=="" || up_user.nickname== null){
    ElMessage("用户昵称不能为空")
  }
  else{
    axios.post("http://localhost:8080/updateusers",up_user)
        .then(res=>{
          if(res.data>0){
            ElMessage.success("修改成功")
            updialogVisible.value=false
            refresh()
          }
        })
  }
  }


// 删除用户逻辑
function  deleteUser(id) {
    ElMessageBox.confirm(
        '是否确认删除用户编号为'+id+'的数据项',
        '系统提示',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning',
        }
    )
        .then(() => {
          axios.get("http://localhost:8080/delusers?id="+id)
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

//添加用户逻辑
function addUser(){
  reset_add()
  adddialogVisible.value=true

}


function reset_add(){
  user.nickname="";
  user.department= "";
  user.phone= "";
  user.email="";
  user.username="";
  user.password="";
  user.sex= "";
  user.state= "正常";
  user.job="";
  user.role= "";
  user.comment="";
}

function ensureAddUser(){
  if(user.username=="" || user.username== null || user.password=="" || user.password == null || user.nickname =="" || user.nickname== null){
    ElMessage("用户昵称，用户名称，用户密码不能为空")
  }
  else{
    axios.post("http://localhost:8080/addusers",user)
        .then(res=>{
          if(res.data>0){
            ElMessage.success("添加成功")
            adddialogVisible.value=false
            refresh()
          }
        })
  }
}




</script>





<template>

  <!--修改信息弹出框-->
  <el-dialog v-model="updialogVisible" title="添加用户" width="700px" class="user-up-dialog">
    <el-form :model="up_user" label-width="100px" label-position="left" >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="用户昵称" prop="nickname" :rules="[{ required: true, message: '请输入用户昵称', trigger: 'red' }]">
            <el-input v-model="up_user.nickname" placeholder="请输入用户昵称" clearable />
          </el-form-item>

          <el-form-item label="手机号码" prop="phone">
            <el-input v-model="up_user.phone" placeholder="请输入手机号码" clearable />
          </el-form-item>

          <el-form-item label="用户性别">
            <el-select v-model="up_user.sex" placeholder="请选择性别" style="width: 100%">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>

          <el-form-item label="岗位" prop="job">
            <el-select v-model="up_user.job" placeholder="请选择岗位" style="width:100%">
              <el-option label="前端开发" value="frontend"></el-option>
              <el-option label="后端开发" value="backend"></el-option>
              <el-option label="产品经理" value="pm"></el-option>
            </el-select>
          </el-form-item>

        </el-col>

        <el-col :span="12">
          <el-form-item label="归属部门" prop="department">
            <el-select v-model="up_user.department" placeholder="请选择归属部门" style="width:100%">
              <el-option label="技术部" value="tech"></el-option>
              <el-option label="市场部" value="market"></el-option>
              <el-option label="人事部" value="hr"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input v-model="up_user.email" placeholder="请输入邮箱" clearable />
          </el-form-item>

          <el-form-item label="状态">
            <el-radio-group v-model="up_user.state">
              <el-radio label="正常" />
              <el-radio label="停用" />
            </el-radio-group>
          </el-form-item>

          <el-form-item label="角色" prop="role">
            <el-select v-model="up_user.role" placeholder="请选择角色" style="width:100%">
              <el-option label="管理员" value="管理员" />
              <el-option label="用户管理员" value="用户管理员" />
              <el-option label="租户管理员" value="租户管理员" />
              <el-option label="普通用户" value="普通用户" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="备注" prop="comment">
        <el-input
            v-model="up_user.comment"
            type="textarea"
            :rows="3"
            placeholder="请输入备注内容"
            maxlength="200"
            show-word-limit
        />
      </el-form-item>
    </el-form>

  <template #footer>
    <span class="dialog-footer">
      <el-button @click="updialogVisible = false">取消</el-button>
      <el-button type="primary" @click="ensureUpUser">确定</el-button>
    </span>
  </template>
</el-dialog>

  <!--添加信息弹出框-->
  <el-dialog v-model="adddialogVisible" title="添加用户" width="700px" class="user-add-dialog">
    <el-form :model="user" label-width="100px" label-position="left" >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="用户昵称" prop="nickname" :rules="[{ required: true, message: '请输入用户昵称', trigger: 'red' }]">
            <el-input v-model="user.nickname" placeholder="请输入用户昵称" clearable />
          </el-form-item>

          <el-form-item label="手机号码" prop="phone">
            <el-input v-model="user.phone" placeholder="请输入手机号码" clearable />
          </el-form-item>

          <el-form-item label="用户名称" prop="username" :rules="[{ required: true, message: '请输入用户昵称', trigger: 'red' }]">
            <el-input v-model="user.username" placeholder="请输入用户名称" clearable />
          </el-form-item>

          <el-form-item label="用户性别">
            <el-select v-model="user.sex" placeholder="请选择性别" style="width: 100%">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </el-form-item>

          <el-form-item label="岗位" prop="job">
            <el-select v-model="user.job" placeholder="请选择岗位" style="width:100%">
              <el-option label="前端开发" value="frontend"></el-option>
              <el-option label="后端开发" value="backend"></el-option>
              <el-option label="产品经理" value="pm"></el-option>
            </el-select>
          </el-form-item>

        </el-col>

        <el-col :span="12">
          <el-form-item label="归属部门" prop="department">
            <el-select v-model="user.department" placeholder="请选择归属部门" style="width:100%">
              <el-option label="技术部" value="tech"></el-option>
              <el-option label="市场部" value="market"></el-option>
              <el-option label="人事部" value="hr"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="邮箱" prop="email">
            <el-input v-model="user.email" placeholder="请输入邮箱" clearable />
          </el-form-item>

          <el-form-item label="用户密码" prop="password" :rules="[{ required: true, message: '请输入用户昵称', trigger: 'red' }]">
            <el-input v-model="user.password" placeholder="请输入密码" show-password clearable />
          </el-form-item>

          <el-form-item label="状态">
            <el-radio-group v-model="user.state">
              <el-radio label="正常" />
              <el-radio label="停用" />
            </el-radio-group>
          </el-form-item>

          <el-form-item label="角色" prop="role">
            <el-select v-model="user.role" placeholder="请选择角色" style="width:100%">
              <el-option label="管理员" value="管理员" />
              <el-option label="用户管理员" value="用户管理员" />
              <el-option label="租户管理员" value="租户管理员" />
              <el-option label="普通用户" value="普通用户" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="备注" prop="comment">
        <el-input
            v-model="user.comment"
            type="textarea"
            :rows="3"
            placeholder="请输入备注内容"
            maxlength="200"
            show-word-limit
        />
      </el-form-item>
    </el-form>

    <template #footer>
    <span class="dialog-footer">
      <el-button @click="adddialogVisible = false">取消</el-button>
      <el-button type="primary" @click="ensureAddUser">确定</el-button>
    </span>
    </template>
  </el-dialog>

  <!--布局-->
  <div class="common-layout">

    <el-container>
      <!--边栏-->
      <el-aside width="200px">
        Aside
      </el-aside>


      <el-container>
        <!--头部-->
        <el-header height="150px">
          <span style="margin-right: 16px;">用户名称</span>
          <el-input
            v-model="username"
            style="width: 240px"
            placeholder="请输入用户名">
        </el-input>
          <span style="margin-right: 16px;"></span>
          <span style="margin-right: 16px;">手机号码</span>
          <el-input
            v-model="phone"
            style="width: 240px"
            placeholder="请输入手机号码">
        </el-input>
          <span style="margin-right: 16px;"></span>
          <span style="margin-right: 16px;">状态</span>
          <el-select
            v-model="state"
            style="width: 240px"
            placeholder="请选择状态">
          <el-option label="正常" value="normal"></el-option>
          <el-option label="禁用" value="disabled"></el-option>
        </el-select>
          <span style="margin-right: 16px;"></span>
          <span style="margin-right: 16px;">创建时间</span>
          <el-date-picker
              v-model="create_time"
              style="width: 240px"
              type="daterange"
              start-placeholder="开始时间"
              end-placeholder="结束时间">
          </el-date-picker>
          <br />
          <br />

          <el-button type="primary" :icon="Search" @click="searchUsers">搜索</el-button>
          <el-button @click="reset">重置</el-button>
          <br />
          <br />

          <el-button type="success" @click="addUser">新增</el-button>

        </el-header>


        <!--主要-->
        <el-main>
          <el-table :data="userList" border>
            <el-table-column prop="id" label="用户编号"></el-table-column>
            <el-table-column prop="username" label="用户名称"></el-table-column>
            <el-table-column prop="nickname" label="用户昵称"></el-table-column>
            <el-table-column prop="department" label="部门"></el-table-column>
            <el-table-column prop="phone" label="手机号码"></el-table-column>
            <el-table-column prop="state" label="状态"></el-table-column>
            <el-table-column prop="update_create_at" label="创建时间"></el-table-column>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button link type="primary" size="small" @click="upUser(scope.row.id)">修改</el-button>
                <el-button link type="primary" size="small" @click="deleteUser(scope.row.id)">删除</el-button>
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

</style>