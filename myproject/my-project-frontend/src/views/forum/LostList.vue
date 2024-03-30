<script setup>
import LightCard from "@/components/LightCard.vue";
import {
  Calendar,
  Clock,
  CollectionTag,
  Compass,
  Document,
  Edit,
  EditPen,
  Picture,
  Link, Microphone, Star, ArrowRightBold, FolderOpened
} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, reactive, ref, watch} from "vue";
import {ElMessage} from "element-plus";
import {get} from "@/net/index.js";
import LostEditor from "@/components/Lost/LostEditor.vue";
import axios from "axios";
import {useStore} from "@/store/index.js";
import router from "@/router/index.js";
import TopicCollectList from "@/components/Topic/TopicCollectList.vue";
import TopicTag from "@/components/Topic/TopicTag.vue";

let ipAddress = ref(null)
const sentence = reactive({
  content: {},
  origin: {},
})
const store = useStore()

axios.get(`https://api.ipify.org?format=json`)
    .then(response => {
      ipAddress.value = response.data.ip;
      console.log(ipAddress);
    })
    .catch(error => {
      console.log(error);
    });

axios.get('https://api.xygeng.cn/one')
    .then(response => {
      sentence.content = response.data.data.content;
      sentence.origin = response.data.data.origin
      console.log(sentence);
    })
    .catch(error => {
      console.log(error);
    });
const collects = ref(false)

const weather = reactive({
  location: {},
  now: {},
  hourly: [],
  success: false
})
const editor = ref(false)
const losts = reactive({
  list: [],
  page: 0,
  end: false,
})
watch(() => losts.type, () => resetList(), {immediate: true})
const today = computed(() => {
  const date = new Date()
  return `${date.getFullYear()} 年 ${date.getMonth() + 1} 月 ${date.getDate()} 日`
})

function updateList(){
  if(losts.end) return
  get(`/api/forum/list-lost?page=${losts.page}&type=${losts.type}`, data => {
    if(data) {
      data.forEach(d => losts.list.push(d))
      losts.page++
    }
    if (data.length < 10 || !data)
      losts.end = true
  })
}
function  onLostCreate(){
  editor.value = false
  resetList()
}
function resetList(){
  losts.page = 0
  losts.end = false
  losts.list = []
  updateList()
}
navigator.geolocation.getCurrentPosition(position => {
  const longitude = position.coords.longitude
  const latitude = position.coords.latitude
  get(`/api/forum/weather?longitude=${longitude}&latitude=${latitude}`, data => {
    Object.assign(weather,data)
    weather.success = true
  })
}, error => {
  console.info(error)
  ElMessage.warning('位置信息获取超时，请检查网络设置')
  get(`/api/forum/weather?longitude=33.068856&latitude=107.053003`,data => {
    Object.assign(weather,data)
    weather.success = true
  })
},{
  timeout: 3000,
  enableHighAccuracy: true
})
</script>

<template>
  <div style="display: flex;margin: 20px auto;gap: 20px;max-width: 900px">
    <div style="flex: 1">
      <light-card>
        <div class="creat-topic" @click="editor = true">
          <el-icon><EditPen/></el-icon>
          点击发布寻物寻主帖子...
        </div>
        <div style="margin-top: 10px;display: flex;gap: 13px;font-size: 18px;color: grey">
          <el-icon><Edit/></el-icon>
          <el-icon><Document/></el-icon>
          <el-icon><Compass/></el-icon>
          <el-icon><Picture/></el-icon>
          <el-icon><Microphone/></el-icon>
        </div>
      </light-card>
      <transition name="el-fade-in" mode="out-in">
        <div v-if="losts.list.length">
          <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px"
               v-infinite-scroll="updateList">
            <light-card style="height: 150px" v-for="item in losts.list" class="topic-card" @click="router.push('/index/lost-detail/'+item.id)">
              <div style="display: flex">
                <div>
                  <el-avatar :size="30" :src="store.avatarUserUrl(item.avatar, item.email)"/>
                </div>
                <div style="margin-left: 7px;transform: translateY(-2px)">
                  <div style="font-size: 13px;font-weight: bold">{{item.username}}</div>
                  <div style="font-size: 12px;color: grey">
                    <el-icon><Clock/></el-icon>
                    <div style="margin-left: 2px;display: inline-block;transform: translateY(-2px)">
                      {{new Date(item.time).toLocaleString()}}
                    </div>
                  </div>
                </div>
              </div>
              <div style="margin-top: 5px">
                <span style="font-weight: bold;margin-left: 7px">{{item.title}}</span>
              </div>
              <div class="topic-content">{{item.text}}</div>
              <div style="display: grid;grid-template-columns: repeat(3, 1fr);grid-gap: 10px">
                <el-image class="topic-image" v-for="img in item.images" :src="img" fit="cover"></el-image>
              </div>
            </light-card>
          </div>
        </div>
      </transition>
    </div>
    <div style="width: 280px">
      <div style="position: sticky;top: 20px">
        <light-card>
          <div class="collect-list-button" @click="collects = true">
            <span><el-icon><FolderOpened /></el-icon> 查看我的收藏</span>
            <el-icon style="transform: translateY(3px)"><ArrowRightBold/></el-icon>
          </div>
        </light-card>
        <light-card style="margin-top: 10px">
          <div style="font-weight: bold">
            <el-icon><CollectionTag/></el-icon>
            论坛公告
          </div>
          <el-divider style="margin: 10px 0"/>
          <div style="font-size: 14px;margin: 10px;color: grey">
            新华社北京3月5日电 第十四届全国人民代表大会第二次会议5日上午在北京人民大会堂开幕。近3000名全国人大代表肩负人民重托出席大会，履行宪法和法律赋予的神圣职责。
          </div>
        </light-card>
        <light-card style="margin-top: 10px">
          <div style="font-weight: bold">
            <el-icon><Calendar/></el-icon>
            天气信息
          </div>
          <el-divider style="margin: 10px 0"/>
          <Weather :data="weather"/>
        </light-card>
        <light-card>
          <div class="info-text">
            <div>当前日期</div>
            <div>{{today}}</div>
          </div>
          <div class="info-text">
            <div>当前IP地址</div>
            <div>{{ipAddress}}</div>
          </div>
        </light-card>
        <light-card style="margin-top: 10px">
          <div style="font-size: 14px;margin-top: 2px;font-weight: bold">
            <el-icon><Link/></el-icon>
            一则短语💕
          </div>
          <el-divider style="margin: 5px 0"/>
          <div style="display: flex;flex-direction: column;margin-top: 10px" class="sentence">
            <div>{{sentence.content}}</div>
            <div>from: {{sentence.origin}}</div>
          </div>
        </light-card>
      </div>
    </div>
    <lost-editor :show="editor" @success="onLostCreate" @close="editor = false"/>
    <topic-collect-list :show="collects" @close="collects = false"/>
  </div>
</template>

<style lang="less" scoped>
.collect-list-button {
  font-size: 14px;
  display: flex;
  justify-content: space-between;
  transition: .3s;

  &:hover {
    cursor: pointer;
    opacity: 0.6;
  }
}
.top-topic{
  display: flex;

  div:first-of-type {
    font-size: 14px;
    margin-left: 10px;
    font-weight: bold;
    opacity: 0.8;
    transition: color .3s;

    &:hover{
      color: grey;
    }
  }

  div:nth-of-type(2){
    flex: 1;
    color: grey;
    font-size: 13px;
    text-align: right;
  }

  &:hover{
    cursor: pointer;
  }
}
.topic-card{
  padding: 15px;
  transition: scale .3s;

  &:hover{
    scale: 1.015;
    cursor: pointer;
  }
  .topic-content{
    font-size: 15px;
    color: grey;
    margin: 5px 0;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 3;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .topic-type{
    display: inline-block;
    border: solid 0.5px grey;
    border-radius: 3px;
    font-size: 12px;
    padding: 0 5px;
    height: 18px;
  }
  .topic-image{
    width: 100%;
    height: 100%;
    max-height: 35px;
    border-radius: 5px;
  }
}
.info-text{
  display: flex;
  justify-content: space-between;
  color: grey;
  font-size: 14px;
}
.sentence{

  div:first-of-type {
    font-size: 13px;
    opacity: 0.8;
    transition: color .3s;
    overflow : hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 5;
    -webkit-box-orient: vertical;
  }

  div:nth-of-type(2){
    flex: 1;
    font-size: 12px;
    color: grey;
    text-align: right;
  }
}
.creat-topic{
  background-color: #efefef;
  border-radius: 5px;
  height: 40px;
  color: grey;
  font-size: 14px;
  line-height: 40px;
  padding: 0 10px;

  &:hover{
    cursor: pointer;
  }
}

.dark{
  .creat-topic{
    background-color: #232323;
  }
  .type-select-card{
    background-color: #282828;
    &.active{
      border: solid 1px #64594b;
    }
    &:hover{
      background-color: #5e5e5e;
    }
  }
}

</style>