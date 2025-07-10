<template>
  <div class="rich-text-editor">
    <Toolbar
      class="toolbar"
      :editor="editorRef"
      :defaultConfig="toolbarConfig"
      :mode="mode"
    />
    <Editor
      class="editor"
      :defaultConfig="editorConfig"
      :mode="mode"
      v-model="valueHtml"
      @onCreated="handleCreated"
    />
  </div>
</template>

<script setup lang="ts">
import '@wangeditor/editor/dist/css/style.css';
import { onBeforeUnmount, shallowRef, watch } from 'vue';
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';
import type { IDomEditor, IToolbarConfig, IEditorConfig } from '@wangeditor/editor';
import { useUserInfoStore } from '../stores/userInfo';

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  height: {
    type: String,
    default: '300px'
  }
});

const emit = defineEmits(['update:modelValue']);

const mode = 'default'; // 或 'simple'，简单模式
const editorRef = shallowRef<IDomEditor | null>(null);
// 修改此行
const valueHtml = shallowRef(props.modelValue);

// 工具栏配置
const toolbarConfig: Partial<IToolbarConfig> = { 
  excludeKeys: [
    'group-video', 
    'insertTable', 
    'codeBlock', 
    'divider'
  ]
};

// 编辑器配置
const editorConfig: Partial<IEditorConfig> = {
  placeholder: '请输入会议简介...',
  MENU_CONF: {
    uploadImage: {
      server: 'http://localhost:8080/upload/uploadFiles',
      fieldName: 'file',
      meta: { token: useUserInfoStore().token },
      allowedFileTypes: ['image/jpeg', 'image/png', 'image/gif'],
      maxFileSize: 2 * 1024 * 1024, // 2M
      withCredentials: true,
      timeout: 10 * 1000, // 10 秒
      onSuccess(file: File, res: any) {
        return res.data.url;
      }
    }
  }
};

// 监听父组件传递的 modelValue 变化
watch(() => props.modelValue, (newValue) => {
  if (newValue !== valueHtml.value) {
    valueHtml.value = newValue;
  }
});

// 监听编辑器内容变化，向上传递更新
watch(valueHtml, (newHtml) => {
  emit('update:modelValue', newHtml);
});

// 组件创建时的回调
const handleCreated = (editor: IDomEditor) => {
  editorRef.value = editor;
  // 设置编辑器高度
  const editorDom = editorRef.value?.getEditableContainer();
  if (editorDom) {
    editorDom.style.height = props.height;
  }
};

// 组件销毁前销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor == null) return;
  editor.destroy();
});

</script>

<style scoped>
.rich-text-editor {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.toolbar {
  border-bottom: 1px solid #e8e8e8;
  background-color: #f8f9fa;
}

.editor {
  overflow-y: hidden;
}

/* 自定义滚动条 */
.editor::-webkit-scrollbar {
  width: 8px;
}

.editor::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.editor::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 4px;
}

.editor::-webkit-scrollbar-thumb:hover {
  background: #a0a4ac;
}
</style>