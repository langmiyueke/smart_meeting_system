<script setup>
import { ref } from 'vue'
import { useUserInfoStore } from '../stores/userInfo'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'

import { useNews } from '../composables/useNews'
import { onMounted } from 'vue'

const tokenStore = useUserInfoStore()

// 搜索模型
const newsSearchModel = ref({
    title: '',
    imageUrl: '',
    author: '',
    summary: '',
    order: ''
})

const visibleDrawer = ref(false)

const {
    newsListModel, pageNum, total, pageSize, loading, newsAddModel, editorKey,
    isEdit, submitLoading, multipleSelection, formRef, rules, orderData, tenantModel,
    loadTenantList, newsList, deleteNews, onSizeChange, onCurrentChange, resetNews,
    resetForm, editNews, addNews, submitNews, UploadSuccess,
    handleDrawerClose, handleSelectionChange, batchDelete, exportSelected,
    exportSearch, beforeImageUpload
} = useNews(newsSearchModel, visibleDrawer)

onMounted(() => {
    newsList()
})
</script>

<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span>资讯管理</span>
                <div class="extra">
                    <el-button type="primary" @click="addNews">添加资讯</el-button>
                </div>
            </div>
        </template>
        <!-- 搜索表单 -->
        <el-form inline>
            <el-form-item label="新闻标题：">
                <el-input v-model="newsSearchModel.title" style="width: 240px" placeholder="请输入新闻标题" clearable />
            </el-form-item>
            <el-form-item label="新闻图片路径：">
                <el-input v-model="newsSearchModel.imageUrl" style="width: 240px" placeholder="请输入新闻图片路径" clearable />
            </el-form-item>
            <el-form-item label="作者：">
                <el-input v-model="newsSearchModel.author" style="width: 240px" placeholder="请输入作者" clearable />
            </el-form-item>
            <el-form-item label="新闻简介：">
                <el-input v-model="newsSearchModel.summary" style="width: 240px" placeholder="请输入新闻简介" clearable />
            </el-form-item>
            <el-form-item label="选择排序方式：">
                <el-select placeholder="请选择" v-model="newsSearchModel.order" style="width: 150px">
                    <el-option v-for="order in orderData" :label="order.label" :value="order.value" :key="order.value">
                    </el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="newsList()">搜索</el-button>
                <el-button @click="resetNews">重置</el-button>
                <el-button type="success" @click="exportSearch">
                    导出当前搜索结果
                </el-button>

                <el-button type="danger" :disabled="!multipleSelection.length" @click="batchDelete">
                    批量删除
                </el-button>

                <el-button type="success" :disabled="!multipleSelection.length" @click="exportSelected">
                    导出选中
                </el-button>

                <!-- 在模板中绑定 ref 的响应式变量不需要加 .value -->
            </el-form-item>
        </el-form>

        <!-- 新闻列表 -->
        <el-table :data="newsListModel" style="width: 100%" v-loading="loading"
            @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" />
            <el-table-column label="新闻标题" width="400" prop="title"></el-table-column>
            <el-table-column label="作者" prop="author"></el-table-column>
            <el-table-column label="新闻简介" prop="summary"> </el-table-column>

            <el-table-column label="操作" width="100">
                <template #default="{ row }">
                    <el-button :icon="Edit" circle plain type="primary" @click="editNews(row)"></el-button>
                    <el-button :icon="Delete" circle plain type="danger" @click="deleteNews(row)"></el-button>
                </template>
            </el-table-column>
            <template #empty>
                <el-empty description="没有数据" />
            </template>
        </el-table>
        <!-- 分页条 -->
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[3, 5, 10, 15]"
            layout="jumper, total, sizes, prev, pager, next" background :total="total" @size-change="onSizeChange"
            @current-change="onCurrentChange" style="margin-top: 20px; justify-content: flex-end" />
        <!-- 抽屉 -->
        <el-drawer v-model="visibleDrawer" :title="isEdit ? '编辑资讯管理' : '添加资讯管理'" direction="rtl" size="50%"
            :before-close="handleDrawerClose" @close="resetForm">
            <!-- 添加新闻表单 -->
            <el-form ref="formRef" :model="newsAddModel" :rules="rules" label-width="100px">
                <el-form-item label="新闻标题" prop="title">
                    <el-input v-model="newsAddModel.title" placeholder="请输入标题"></el-input>
                </el-form-item>
                <el-form-item label="新闻图片" prop="imageUrl">
                    <!-- 自动发送请求 -->
                    <el-upload class="avatar-uploader" :auto-upload="true" :show-file-list="false" action="/api/upload"
                        name="file" :headers="{ 'Authorization': tokenStore.token }" :before-upload="beforeImageUpload"
                        :on-success="UploadSuccess">
                        <img v-if="newsAddModel.imageUrl" :src="newsAddModel.imageUrl" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="新闻内容" prop="content">
                    <div class="editor"><quill-editor :key="editorKey" theme="snow"
                            v-model:content="newsAddModel.content" contentType="html">
                        </quill-editor></div>
                </el-form-item>
                <el-form-item label="作者" prop="author">
                    <el-input v-model="newsAddModel.author" placeholder="请输入作者"></el-input>
                </el-form-item>
                <el-form-item label="新闻简介" prop="summary">
                    <el-input v-model="newsAddModel.summary" placeholder="请输入新闻简介"></el-input>
                </el-form-item>
                <el-form-item label="选择租户" prop="tenantId">
                    <el-select placeholder="请选择" v-model="newsAddModel.tenantId" @visible-change="loadTenantList">
                        <el-option v-for="c in tenantModel" :key="c.enterpriseMark" :label="c.name" :value="c.enterpriseMark">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" :loading="submitLoading" @click="submitNews()">发布</el-button>
                </el-form-item>
            </el-form>
        </el-drawer>
    </el-card>
</template>
<style lang="scss" scoped>
.page-container {
    min-height: 100%;
    box-sizing: border-box;
    .header {
        display: flex;
        align-items: center;
        justify-content: space-between;
    }
    /* 抽屉样式 */
    .avatar-uploader {
        :deep() {
            .avatar {
                width: 178px;
                height: 178px;
                display: block;
            }
            .el-upload {
                border: 1px dashed var(--el-border-color);
                border-radius: 6px;
                cursor: pointer;
                position: relative;
                overflow: hidden;
                transition: var(--el-transition-duration-fast);
            }
            .el-upload:hover {
                border-color: var(--el-color-primary);
            }
            .el-icon.avatar-uploader-icon {
                font-size: 28px;
                color: #8c939d;
                width: 178px;
                height: 178px;
                text-align: center;
            }
        }
    }
    .editor {
        width: 100%;

        :deep(.ql-editor) {
            min-height: 110px;
        }
    }
}
</style>