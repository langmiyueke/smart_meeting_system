<template>
  <div class="dept-management">
    <!-- 搜索区域 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="部门名称">
          <el-input v-model="searchForm.deptName" placeholder="请输入部门名称" clearable @clear="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部">
            <el-option label="正常" value="正常" />
            <el-option label="停用" value="停用" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon>
              <Search />
            </el-icon>搜索
          </el-button>
        </el-form-item>
      </el-form>
      <el-alert title="部门状态说明：正常状态可正常使用，停用状态将无法使用" type="info" show-icon :closable="false" />
    </el-card>

    <!-- 操作按钮区域 -->
    <div class="action-bar">
      <el-button type="primary" @click="openDialog('add')">
        <el-icon>
          <Plus />
        </el-icon>新增部门
      </el-button>

      <div class="expand-toggle">
        <el-button @click="toggleExpandAll">
          <el-icon v-if="isExpanded">
            <Minus />
          </el-icon>
          <el-icon v-else>
            <Plus />
          </el-icon>
          {{ isExpanded ? '全部折叠' : '全部展开' }}
        </el-button>
      </div>

      <div class="info-text">
        添加时间：{{ currentDate }} | 共 {{ departments.length }} 个部门
      </div>
    </div>

    <!-- 部门树形表格 -->
    <el-table ref="treeTableRef" :data="filteredDepartments" row-key="deptId"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }" style="width: 100%" border>
      <el-table-column prop="deptName" label="部门名称" width="350">
        <template #default="{ row }">
          <span :style="{ 'padding-left': (row.level * 20) + 'px' }">
            <el-icon v-if="row.children?.length" :size="16">
              <Folder />
            </el-icon>
            <el-icon v-else :size="16">
              <Document />
            </el-icon>
            {{ row.deptName }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="orderNum" label="排序" width="200" align="center">
        <template #default="{ row }">
          <el-tag>{{ row.orderNum }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="200" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === '正常' ? 'success' : 'danger'">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="300" align="center">
      </el-table-column>
      <el-table-column label="操作" width="auto" align="center">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog('add-child', row)">
            添加子部门
          </el-button>
          <el-button size="small" type="primary" @click="openDialog('edit', row)">
            编辑
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑部门对话框 -->
    <el-dialog v-model="dialogVisible"
      :title="dialogType === 'add' ? '添加部门' : dialogType === 'add-child' ? '添加子部门' : '编辑部门'" width="600px">
      <el-form ref="deptFormRef" :model="deptForm" :rules="rules" label-width="100px">
        <el-form-item label="上级部门" prop="parentId">
          <el-cascader v-model="deptForm.parentId" :options="parentOptions" :props="{
            value: 'deptId',
            label: 'deptName',
            children: 'children',
            checkStrictly: true,
            emitPath: false
          }" placeholder="请选择上级部门" clearable />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="deptForm.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="显示排序" prop="orderNum">
          <el-input-number v-model="deptForm.orderNum" :min="1" :max="100" />
          <span class="tip-text">数字越小，排序越前</span>
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="deptForm.leader" placeholder="请输入负责人姓名" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="deptForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="deptForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="部门状态" prop="status">
          <el-radio-group v-model="deptForm.status">
            <el-radio value="正常">正常</el-radio>
            <el-radio value="停用">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import {
  Search, Plus, Minus, Folder, Document
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { useUserInfoStore } from "../stores/userInfo";
import {getDeptTree, getDeptPage, addDept, updateDept, deleteDept} from "../api/dept"
const userInfo = useUserInfoStore()
const token = userInfo.token

// 基础URL配置
const BASE_URL = 'http://localhost:8080'
const api = axios.create({
  baseURL: BASE_URL,
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `${token}`
  }
})

api.interceptors.request.use((config) => {
  return config
}, (error) => {
  return Promise.reject(error)
})
// 展开状态和表格引用
const isExpanded = ref(true)
const treeTableRef = ref(null)
const deptFormRef = ref(null)

// 部门数据
const departments = ref([])

// 搜索表单
const searchForm = reactive({
  deptName: '',
  status: ''
})

// 部门表单
const deptForm = reactive({
  deptId: null,
  deptName: '',
  parentId: null,
  orderNum: 1,
  status: '正常',
  leader: '',
  phone: '',
  email: ''
})

// 表单规则
const rules = {
  deptName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' }
  ],
  orderNum: [
    { required: true, message: '请设置显示排序', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择部门状态', trigger: 'change' }
  ]
}

// 对话框控制
const dialogVisible = ref(false)
const dialogType = ref('add')

// 当前日期
const currentDate = computed(() => {
  const now = new Date()
  return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:${String(now.getSeconds()).padStart(2, '0')}`
})

// 上级部门选项
const parentOptions = computed(() => {
  const generateOptions = (node, level = 0) => {
    const options = [{
      deptId: node.deptId,
      deptName: node.deptName,
      level: level
    }]

    if (node.children && node.children.length) {
      node.children.forEach(child => {
        options.push(...generateOptions(child, level + 1))
      })
    }

    return options
  }

  return departments.value.flatMap(dept => generateOptions(dept))
})

// 过滤后的部门数据

const filteredDepartments = computed(() => {
  const filter = (nodes) => {
    return nodes.filter(node => {
      const matchesName = !searchForm.deptName ||
        node.deptName.toLowerCase().includes(searchForm.deptName.toLowerCase());
      const matchesStatus = !searchForm.status || node.status === searchForm.status;
      return matchesName && matchesStatus;
    });
  }
  return filter(departments.value);
});

// 获取部门树形数据
const fetchTreeData = async () => {
  try {
    const response = await api.get('/dept/tree')
    if (response.data.code === 0) {
      departments.value = transformToTree(response.data.data || [])
      addLevelInfo(departments.value)

      nextTick(() => {
        expandAll()
      })
    } else {
      ElMessage.error('获取部门数据失败: ' + response.data.message)
    }
  } catch (error) {
    ElMessage.error('网络请求失败: ' + error.message)
  }
}

// 转换为树形数据
const transformToTree = (data) => {
  const nodeMap = new Map();
  const idSet = new Set();
  const roots = [];

  // 构建节点映射和ID集合
  data.forEach(item => {
    idSet.add(item.deptId);
    nodeMap.set(item.deptId, { ...item, children: [] });
  });

  // 构建树形结构
  data.forEach(item => {
    const { deptId, parentId } = item;
    // 根节点条件：无父节点或父节点不在当前数据集
    if (!parentId || !idSet.has(parentId)) {
      roots.push(nodeMap.get(deptId));
    }
    // 子节点条件：父节点存在
    else if (nodeMap.has(parentId)) {
      const parent = nodeMap.get(parentId);
      parent.children.push(nodeMap.get(deptId));
    }
  });

  return roots;
};

// 添加层级信息
const addLevelInfo = (treeData) => {
  const addLevel = (nodes, level = 0) => {
    nodes.forEach(node => {
      node.level = level
      if (!node.children) node.children = []
      if (node.children.length) {
        addLevel(node.children, level + 1)
      }
    })
  }

  addLevel(treeData)
}

// 展开或收起所有节点
const expandAll = () => {
  if (!treeTableRef.value) return;
  const expandNode = (row) => {
    treeTableRef.value.toggleRowExpansion(row, isExpanded.value);
    // 如果该行有子节点，则递归展开
    if (row.children && row.children.length > 0) {
      row.children.forEach(child => expandNode(child));
    }
  };

  // 执行递归展开所有行
  filteredDepartments.value.forEach(row => expandNode(row));
};

// 切换展开/折叠
const toggleExpandAll = () => {
  isExpanded.value = !isExpanded.value
  expandAll()
}

// 搜索
const handleSearch = async () => {
  try {
    const requestData = {
      pageNum: 1,
      pageSize: 100, // 返回足够的数据
      keyWords: searchForm.deptName || "",  // 确保为空时传递空字符串
      status: searchForm.status || ""       // 确保为空时传递空字符串
    };

    const response = await api.post('/dept/page', requestData);

    if (response.data.code === 0) {
      const flatData = response.data.data.data || [];

      // 调用 transformToTree 将数据转换为树形结构
      const transformedData = transformToTree(flatData);

      // 使用 Vue 的响应式更新方法
      departments.value.splice(0, departments.value.length, ...transformedData);

      // 添加层级信息
      addLevelInfo(departments.value);

      nextTick(() => {
        expandAll();
      });
    } else {
      ElMessage.error('搜索失败: ' + response.data.message);
    }
  } catch (error) {
    ElMessage.error('搜索请求失败: ' + error.message);
  }
};

// 打开对话框
const openDialog = (type, row = null) => {
  dialogType.value = type;
  resetForm();
  if (type === 'edit') {
    Object.assign(deptForm, row);
  } else if (type === 'add-child') {
    deptForm.parentId = row.deptId
  }
  console.log('父节点ID:', deptForm.parentId);

  dialogVisible.value = true
}

const resetForm = () => {
  Object.assign(deptForm, {
    deptId: null,
    deptName: '',
    parentId: null,
    orderNum: 1,
    status: '正常',
    leader: '',
    phone: '',
    email: ''
  });
};
// 提交表单
const submitForm = async () => {
  if (!deptFormRef.value) return;

  // 表单验证
  const valid = await deptFormRef.value.validate();
  if (!valid) return;

  try {
    // 确保 parentId 存在，如果为空则设置为 0（或其他默认值）
    const payload = {
      ...deptForm,
      parentId: deptForm.parentId ?? 0 // 如果 parentId 为空则默认设置为 0
    };

    console.log("提交的数据", payload); // 输出提交的数据，确保 parentId 正确

    // 根据 deptId 判断是新增还是更新
    if (deptForm.deptId) {
      // 更新部门
      await api.put('/dept', payload);
      ElMessage.success('部门更新成功');
    } else {
      // 新增部门
      await api.post('/dept', payload);
      ElMessage.success('部门添加成功');
    }

    dialogVisible.value = false;
    await fetchTreeData(); // 刷新数据
  } catch (error) {
    ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message));
  }
};


// 删除部门
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定删除部门 "${row.deptName}" 及其所有子部门？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await api.delete(`/dept/${row.deptId}`)
    ElMessage.success('部门删除成功')
    await fetchTreeData();  // Reload data
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + (error.response?.data?.message || error.message))
    }
  }
}

onMounted(() => {
  fetchTreeData()
})
</script>

<style scoped>
.dept-management {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.action-bar {
  display: flex;
  /* justify-content: space-between; */
  align-items: center;
  margin-bottom: 20px;
}

.expand-toggle {
  margin-left: 20px;
}

.tip-text {
  font-size: 12px;
  color: #888;
}

.info-text {
  font-size: 14px;
  color: #444;
  margin-left: auto;
}

.el-select {
  width: 100px;
}
</style>