<script setup>
import {Check, Document} from "@element-plus/icons-vue";
import {computed, reactive, ref} from "vue";
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import ImageResize from "quill-image-resize-vue";
import {ImageExtend, QuillWatch} from "quill-image-super-solution-module";
import {ElMessage} from "element-plus";
import {accessHeader, get, post} from "@/net/index.js";
import axios from "axios";
import {Delta, Quill, QuillEditor} from "@vueup/vue-quill";
import ColorDot from "@/components/ColorDot.vue";
import {useStore} from "@/store/index.js";

const props = defineProps({
  show: Boolean,
  defaultTitle: {
    default: '',
    type: String
  },
  defaultText: {
    default: '',
    type: String
  },
  defaultType: {
    default: null,
    type: Number
  },
  submitButton: {
    default: '立即发表主题',
    type: String
  },
  submit: {
    default: (editor, success) => {
      post('/api/forum/create-topic', {
        type: editor.type.id,
        title: editor.title,
        content: editor.text
      }, () => {
        ElMessage.success("帖子发表成功！")
        success()
      })
    },
    type: Function
  }
})
const store = useStore()
const emit = defineEmits(['close','success'])
const editor = reactive({
  type: null,
  title: '',
  text: '',
  loading: false,
})

function deltaToText(delta) {
  if (!delta.ops) return ""
  let str = ""
  for (let op of delta.ops) {
    str += op.insert
  }
  return str.replace(/\s/g, "")
}
const contentLength = computed(() => deltaToText(editor.text).length)
function submitTopic(){
  const text = deltaToText(editor.text)
  if (text.length > 20000){
    ElMessage.warning('字数超过限制，无法发布！')
    return
  }
  if (!editor.title){
    ElMessage.warning('请填写标题！')
    return;
  }
  if (!editor.type){
    ElMessage.warning('请选择一个合适的帖子类型！')
    return;
  }
  props.submit(editor, () => emit('success'))
}
const refEditor = ref()

function initEditor() {
  if(props.defaultText)
    editor.text = new Delta(JSON.parse(props.defaultText))
  else
    refEditor.value.setContents('', 'user')
  editor.title = props.defaultTitle
  editor.type = findTypeById(props.defaultType)
}
function findTypeById(id){
  for (let type of store.forum.types) {
    if(type.id === id)
      return type
  }
}

Quill.register('modules/imageResize', ImageResize)
Quill.register('modules/ImageExtend', ImageExtend)

const editorOption = {
  modules: {
    toolbar: {
      container: [
        "bold", "italic", "underline", "strike","clean",
        {color: []}, {'background': []},
        {size: ["small", false, "large", "huge"]},
        { header: [1, 2, 3, 4, 5, 6, false] },
        {list: "ordered"}, {list: "bullet"}, {align: []},
        "blockquote", "code-block", "link", "image",
        { indent: '-1' }, { indent: '+1' }
      ],
      handlers: {
        'image': function () {
          QuillWatch.emit(this.quill.id)
        }
      }
    },imageResize: {
      modules: [ 'Resize', 'DisplaySize' ]
    },
    ImageExtend: {
      action:  axios.defaults.baseURL + '/api/image/cache',
      name: 'file',
      size: 5,
      loading: true,
      accept: 'image/png, image/jpeg',
      response: (resp) => {
        if(resp.data) {
          return axios.defaults.baseURL + '/images' + resp.data
        } else {
          return null
        }
      },
      methods: 'POST',
      headers: xhr => {
        xhr.setRequestHeader('Authorization', accessHeader().Authorization);
      },
      start: () => editor.uploading = true,
      success: () => {
        ElMessage.success('图片上传成功!')
        editor.uploading = false
      },
      error: () => {
        ElMessage.warning('图片上传失败，请联系管理员!')
        editor.uploading = false
      }
    }
  }
}
</script>

<template>
  <div>
    <el-drawer :model-value="show"
                direction="btt"
               :close-on-click-modal="false"
               :size="650"
               @open="initEditor"
    @close="emit('close')">
        <template #header>
          <div>
            <div style="font-weight: bold">发表新帖子</div>
            <div style="font-size: 13px">发表内容之前，请遵守相关法律法规，网络不是法外之地！</div>
          </div>
        </template>
        <div style="display: flex;gap: 10px">
          <div style="width: 150px">
            <el-select placeholder="选择主题类型" value-key="id" v-model="editor.type" :disabled="!store.forum.types.length">
              <el-option v-for="item in store.forum.types.filter(type => type.id > 0)" :value="item" :label="item.name">
                <div>
                  <color-dot :color="item.color"/>
                  <span style="margin-left: 10px">{{item.name}}</span>
                </div>
              </el-option>
            </el-select>
          </div>
          <div style="flex: 1">
            <el-input v-model="editor.title" placeholder="请输入帖子标题..." :prefix-icon="Document"
                      style="height: 100%" maxlength="30"/>
          </div>
        </div>
      <div style="margin-top: 10px;font-size: 13px;color: grey">
        <color-dot :color="editor.type ? editor.type.color : '#dedede'"/>
        <span style="margin-left: 10px">{{editor.type ? editor.type.desc : '请在上方选择一个帖子类型'}}</span>
      </div>
      <div style="margin-top: 10px;height: 440px;overflow: hidden;border-radius: 5px"
           v-loading="editor.uploading" element-loading-text="正在上传图片，请稍后...">
        <quill-editor v-model:content="editor.text"
                      content-type="delta" ref="refEditor"
                      style="height: calc(100% - 45px)"
                      placeholder="今天想分享些什么呢？..." :options="editorOption"/>
      </div>
      <div style="display: flex;justify-content: space-between;margin-top: 5px">
        <div style="color: grey;font-size: 13px">
          当前字数 {{contentLength}} (最大支持20000字)
        </div>
        <div>
          <el-button type="success" plain :icon="Check" @click="submitTopic">{{submitButton}}</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<style scoped>
:deep(.el-drawer){
  width: 800px;
  margin: auto;
  border-radius: 10px 10px 0 0;
}
:deep(.el-drawer__header){
  margin: 0;
}
:deep(.ql-toolbar){
  border-radius: 5px 5px 0 0;
  border-color: var(--el-border-color);
}
:deep(.ql-container){
  border-radius: 0 0 5px 5px;
  border-color: var(--el-border-color);
}
:deep(.ql-editor.ql-blank::before){
  color: var(--el-text-color-placeholder);
  font-style: normal;
}
:deep(.ql-editor){
  font-size: 14px;
}
</style>