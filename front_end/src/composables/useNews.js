import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { saveAs } from 'file-saver'
import {
    newsListService,
    newsDeleteService,
    newsEditService,
    newsAddService,
    batchDeleteService,
    exportSelectedService,
    exportSearchService,
    tenantListService
} from '../api/news'

export function useNews(newsSearchModel, visibleDrawer) {
    const tenantModel = ref([])
    const tenantLoaded = ref(false)

    async function loadTenantList() {
        if (tenantLoaded.value) return
        try {
            const result = await tenantListService()
            tenantModel.value = result
            tenantLoaded.value = true
        } catch (e) {
            console.error('租户列表加载失败', e)
        }
    }
    const orderData = [
        { value: 'title', label: '新闻标题' },
        { value: 'author', label: '作者' },
        { value: 'summary', label: '新闻简介' }
    ]
    const newsListModel = ref([])
    const pageNum = ref(1)
    const total = ref(20)
    const pageSize = ref(3)
    const loading = ref(true)
    const newsAddModel = ref(getEmptyNewsModel())
    const editorKey = ref(0)
    const isEdit = ref(false)
    const submitLoading = ref(false)
    const multipleSelection = ref([])
    const formRef = ref(null)
    const originalModel = ref(getEmptyNewsModel())

    // 规则
    const rules = {
        title: [{ required: true, message: '请输入新闻标题', trigger: 'blur' }],
        imageUrl: [{ required: true, message: '请上传新闻图片', trigger: 'blur' }],
        content: [{ required: true, message: '请输入新闻内容', trigger: 'blur' }],
        author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
        summary: [{ required: true, message: '请输入新闻简介', trigger: 'blur' }],
        tenantId: [{ required: true, message: '请选择租户', trigger: 'blur' }]
    }

    async function newsList() {
        loading.value = true
        try {
            const result = await newsListService({
                pageNum: pageNum.value,
                pageSize: pageSize.value,
                title: newsSearchModel.value.title || null,
                imageUrl: newsSearchModel.value.imageUrl || null,
                author: newsSearchModel.value.author || null,
                summary: newsSearchModel.value.summary || null,
                order: newsSearchModel.value.order || null
            })
            total.value = result.total
            newsListModel.value = result.records
        } catch (e) {
            ElMessage.error("数据加载失败")
        } finally {
            loading.value = false
        }
    }

    async function deleteNews(row) {
        try {
            await ElMessageBox.confirm(
                '确定要删除这条新闻吗？',
                '删除确认',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }
            )
            await newsDeleteService(row.id)
            ElMessage.success('删除成功')
            await newsList()
        } catch (error) {
            if (error !== 'cancel') {
                ElMessage.error('删除失败')
            }
        }
    }

    async function onSizeChange(size) {
        pageSize.value = size
        await newsList()
    }

    async function onCurrentChange(num) {
        pageNum.value = num
        await newsList()
    }

    async function resetNews() {
        Object.assign(newsSearchModel.value, {
            title: '',
            imageUrl: '',
            author: '',
            summary: '',
            order: ''
        })
        await newsList()
    }

    function getEmptyNewsModel() {
        return {
            title: '',
            imageUrl: '',
            content: '',
            author: '',
            summary: '',
            tenantId: ''
        }
    }

    function resetForm() {
        newsAddModel.value = getEmptyNewsModel()
        editorKey.value++
        formRef.value?.resetFields()
    }

    function editNews(row) {
    isEdit.value = true
    newsAddModel.value = { ...row }
    originalModel.value = { ...row }    // 缓存编辑前值
    editorKey.value++
    visibleDrawer.value = true
}

    function addNews() {
    isEdit.value = false
    const model = getEmptyNewsModel()
    newsAddModel.value = { ...model }
    originalModel.value = { ...model }  // 缓存初始值
    editorKey.value++
    visibleDrawer.value = true
}

    async function submitNews() {
        submitLoading.value = true
        try {
            await formRef.value.validate()
            if (isEdit.value) {
                await newsEditService(newsAddModel.value)
                ElMessage.success("编辑成功")
            } else {
                pageNum.value = 1
                await newsAddService(newsAddModel.value)
                ElMessage.success("添加成功")
            }
            visibleDrawer.value = false
            await newsList()
        } catch (e) {
            ElMessage.error("请检查输入项")
        } finally {
            submitLoading.value = false
        }
    }

    function UploadSuccess(result) {
        newsAddModel.value.imageUrl = result.data
        formRef.value.validateField('imageUrl')
    }

    function isDirty() {
    const current = newsAddModel.value
    const original = originalModel.value
    for (const key in original) {
        if (current[key] !== original[key]) return true
    }
    return false
}



    async function handleDrawerClose(done) {
        if (!isDirty()) {
            resetForm()
            if (typeof done === 'function') {
                done()
            } else {
                visibleDrawer.value = false
            }
            return
        }
        try {
            await ElMessageBox.confirm(
                '你有未保存的内容，确定要关闭吗？',
                '提示',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }
            )
            resetForm()
            if (typeof done === 'function') {
                done()
            } else {
                visibleDrawer.value = false
            }
        } catch (e) { }
    }

    function handleSelectionChange(rows) {
        multipleSelection.value = rows
    }

    async function batchDelete() {
        try {
            await ElMessageBox.confirm('确定删除选中的数据吗？', '批量删除', { type: 'warning' })
            const ids = multipleSelection.value.map(item => item.id)
            await batchDeleteService(ids)
            ElMessage.success('删除成功')
            await newsList()
        } catch (e) {
            if (e !== 'cancel') ElMessage.error('删除失败')
        }
    }

    async function exportSelected() {
        try {
            const ids = multipleSelection.value.map(item => item.id).join(',')
            const res = await exportSelectedService(ids)
            const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
            saveAs(blob, '选中新闻.xlsx')
        } catch (error) {
            console.error('导出选中失败：', error)
            ElMessage.error('导出选中失败，请稍后再试')
        }
    }

    async function exportSearch() {
        try {
            const params = {
                title: newsSearchModel.value.title || null,
                imageUrl: newsSearchModel.value.imageUrl || null,
                author: newsSearchModel.value.author || null,
                summary: newsSearchModel.value.summary || null,
                order: newsSearchModel.value.order || null
            }
            const res = await exportSearchService(params)
            const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
            saveAs(blob, '搜索结果.xlsx')
        } catch (e) {
            ElMessage.error('导出失败')
        }
    }

    function beforeImageUpload(file) {
        const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
        const isLt2M = file.size / 1024 / 1024 < 2
        if (!isJPG) ElMessage.error('仅支持 JPG/PNG 格式图片')
        if (!isLt2M) ElMessage.error('图片大小不能超过 2MB')
        return isJPG && isLt2M
    }

    return {
        newsListModel,
        pageNum,
        total,
        pageSize,
        loading,
        newsAddModel,
        editorKey,
        isEdit,
        submitLoading,
        multipleSelection,
        formRef,
        rules,
        tenantModel,
        orderData,
        loadTenantList,
        newsList,
        deleteNews,
        onSizeChange,
        onCurrentChange,
        resetNews,
        resetForm,
        editNews,
        addNews,
        submitNews,
        UploadSuccess,
        isDirty,
        handleDrawerClose,
        handleSelectionChange,
        batchDelete,
        exportSelected,
        exportSearch,
        beforeImageUpload
    }

}
