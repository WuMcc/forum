<script setup>
import {useRoute} from "vue-router";
import {get, post} from "@/net";
import {reactive, ref} from "vue";
import {
  ArrowLeft, ChatDotSquare,
  ChatSquare,
  Delete,
  EditPen,
  Female,
  Male,
  Message,
  Star
} from "@element-plus/icons-vue";
import { QuillDeltaToHtmlConverter } from 'quill-delta-to-html';
import Card from "@/components/Card.vue";
import router from "@/router";
import InteractButton from "@/components/InteractButton.vue";
import {ElMessage} from "element-plus";
import {useStore} from "@/store";
import LostEditor from "@/components/LostEditor.vue";
import LostCommentEditor from "@/components/LostCommentEditor.vue";

const route = useRoute()
const store = useStore()

const tid = route.params.tid

const lost = reactive({
  data: null,
  comments: null,
  page: 1
})
const edit = ref(false)
const comment = reactive({
  show: false,
  text: '',
  quote: null
})

const init = () => get(`api/forum/lost?tid=${tid}`, data => {
  lost.data = data
  loadComments(1)
})
init()

function convertToHtml(content) {
  const ops = JSON.parse(content).ops
  const converter = new QuillDeltaToHtmlConverter(ops, { inlineStyles: true });
  return converter.convert();
}


function updateLost(editor) {
  post('/api/forum/update-lost', {
    id: tid,
    title: editor.title,
    content: editor.text
  }, () => {
    ElMessage.success('帖子内容更新成功！')
    edit.value = false
    init()
  })
}

function loadComments(page) {
  lost.comments = null
  lost.page = page
  get(`/api/forum/lost-comments?tid=${tid}&page=${page - 1}`, data => lost.comments = data)
}

function onCommentAdd() {
  comment.show = false
  loadComments(Math.floor(++lost.data.comments / 10) + 1)
}

function deleteComment(id) {
  get(`/api/forum/delete-lost-comment?id=${id}`, () => {
    ElMessage.success('删除评论成功！')
    loadComments(lost.page)
  })
}
</script>

<template>
  <div class="topic-page" v-if="lost.data">
    <div class="topic-main" style="position: sticky;top: 0;z-index: 10">
      <card style="display: flex;width: 100%;">
        <el-button :icon="ArrowLeft" type="info" size="small"
                   plain round @click="router.push('/index/lost')">返回列表</el-button>
        <div style="text-align: center;flex: 1">
          <span style="font-weight: bold;margin-left: 5px">{{lost.data.title}}</span>
        </div>
      </card>
    </div>
    <div class="topic-main">
      <div class="topic-main-left">
        <el-avatar :src="store.avatarUserUrl(lost.data.user.avatar, lost.data.user.email)" :size="60"/>
        <div>
          <div style="font-size: 18px;font-weight: bold">
            {{lost.data.user.username}}
            <span style="color: hotpink" v-if="lost.data.user.gender === 1">
                            <el-icon><Female/></el-icon>
                        </span>
            <span style="color: dodgerblue" v-if="lost.data.user.gender === 0">
                            <el-icon><Male/></el-icon>
                        </span>
          </div>
          <div class="desc">{{lost.data.user.email}}</div>
        </div>
        <el-divider style="margin: 10px 0"/>
        <div style="text-align: left;margin: 0 5px">
          <div class="desc">微信号: {{lost.data.user.wx || '已隐藏或未填写'}}</div>
          <div class="desc">QQ号: {{lost.data.user.qq || '已隐藏或未填写'}}</div>
          <div class="desc">手机号: {{lost.data.user.phone || '已隐藏或未填写'}}</div>
        </div>
        <el-divider style="margin: 10px 0"/>
        <div class="desc" style="margin: 0 5px">{{lost.data.user.desc}}</div>
      </div>
      <div class="topic-main-right">
        <div class="topic-content" v-html="convertToHtml(lost.data.content)"></div>
        <el-divider/>
        <div style="font-size: 13px;color: grey;text-align: center">
          <div>发帖时间: {{new Date(lost.data.time).toLocaleString()}}</div>
        </div>
        <div style="text-align: right;margin-top: 30px">
          <interact-button name="编辑帖子" color="dodgerblue" :check="false"
                           @check="edit = true" style="margin-right: 20px"
                           v-if="store.user.id === lost.data.user.id">
            <el-icon><EditPen/></el-icon>
          </interact-button>
        </div>
      </div>
    </div>
    <transition name="el-fade-in-linear" mode="out-in">
      <div v-if="lost.comments">
        <div class="topic-main" style="margin-top: 10px" v-for="item in lost.comments">
          <div class="topic-main-left">
            <el-avatar :src="store.avatarUserUrl(item.user.avatar, item.user.email)" :size="60"/>
            <div>
              <div style="font-size: 18px;font-weight: bold">
                {{item.user.username}}
                <span style="color: hotpink" v-if="item.user.gender === 1">
                            <el-icon><Female/></el-icon>
                        </span>
                <span style="color: dodgerblue" v-if="item.user.gender === 0">
                            <el-icon><Male/></el-icon>
                        </span>
              </div>
              <div class="desc">{{item.user.email}}</div>
            </div>
            <el-divider style="margin: 10px 0"/>
            <div style="text-align: left;margin: 0 5px">
              <div class="desc">微信号: {{item.user.wx || '已隐藏或未填写'}}</div>
              <div class="desc">QQ号: {{item.user.qq || '已隐藏或未填写'}}</div>
              <div class="desc">手机号: {{item.user.phone || '已隐藏或未填写'}}</div>
            </div>
          </div>
          <div class="topic-main-right">
            <div style="font-size: 13px;color: grey">
              <div>评论时间: {{new Date(item.time).toLocaleString()}}</div>
            </div>
            <div v-if="item.quote" class="comment-quote">
              回复: {{item.quote}}
            </div>
            <div class="topic-content" v-html="convertToHtml(item.content)"></div>
            <div style="text-align: right">
              <el-link :icon="ChatSquare" @click="comment.show = true;comment.quote = item"
                       type="info">&nbsp;回复评论</el-link>
              <el-link :icon="Delete" type="danger" v-if="item.user.id === store.user.id"
                       style="margin-left: 20px" @click="deleteComment(item.id)">&nbsp;删除评论</el-link>
            </div>
          </div>
        </div>
        <div style="width: fit-content;margin: 20px auto">
          <el-pagination background layout="prev, pager, next"
                         v-model:current-page="lost.page" @current-change="loadComments"
                         :total="lost.data.comments" :page-size="10"
                         hide-on-single-page/>
        </div>
      </div>
    </transition>
    <lost-editor :show="edit" @close="edit = false" v-if="lost.data && store.forum.types"
                  :default-type="lost.data.type" :default-text="lost.data.content"
                  :default-title="lost.data.title" submit-button="更新帖子内容" :submit="updateLost"/>
    <lost-comment-editor :show="comment.show" @close="comment.show = false" :tid="tid"
                          :quote="comment.quote" @comment="onCommentAdd"/>
    <div class="add-comment" @click="comment.show = true;comment.quote = null">
      <el-icon><ChatDotSquare/></el-icon>
    </div>
  </div>
</template>

<style lang="less" scoped>
.comment-quote {
  font-size: 13px;
  color: grey;
  background-color: rgba(94, 94, 94, 0.1);
  padding: 10px;
  margin-top: 10px;
  border-radius: 5px;
}

.add-comment {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  font-size: 18px;
  color: var(--el-color-primary);
  text-align: center;
  line-height: 45px;
  background: var(--el-bg-color-overlay);
  box-shadow: var(--el-box-shadow-lighter);

  &:hover {
    background: var(--el-border-color-extra-light);
    cursor: pointer;
  }
}

.topic-page {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 10px 0;
}

.topic-main {
  display: flex;
  border-radius: 7px;
  margin: 0 auto;
  background-color: var(--el-bg-color);
  width: 800px;

  .topic-main-left {
    width: 200px;
    padding: 10px;
    text-align: center;
    border-right: solid 1px var(--el-border-color);

    .desc {
      font-size: 12px;
      color: grey;
    }
  }

  .topic-main-right {
    width: 600px;
    padding: 10px 20px;
    display: flex;
    flex-direction: column;

    .topic-content {
      font-size: 14px;
      line-height: 22px;
      opacity: 0.8;
      flex: 1;
    }
  }
}
</style>
