<script setup>
import {get, logout} from '@/net'
import router from "@/router";
import {useStore} from "@/store/index.js";
import {reactive, ref} from "vue";
import {
  ArrowDown,
  Back,
  Bell,
  ChatDotSquare, Check, Collection, DataLine,
  Document, Files,
  Location, Lock, Message, Monitor,
  Notification, Operation,
  Position, Reading,
  School, Search, Service, Tickets,
  Umbrella, User
} from "@element-plus/icons-vue";
import LightCard from "@/components/LightCard.vue";

const store = useStore()
const loading = ref(true)

const searchInput = reactive({
  type: '1',
  text: ''
})
const notification = ref([])

get("/api/user/info", (data) => {
  store.user = data
  loading.value = false
})
const loadNotification =
    () => get('/api/notification/list', data => notification.value = data)
loadNotification()
function userLogout() {
  logout(() => router.push("/"))
}
function confirmNotification(id, url) {
  get(`/api/notification/delete?id=${id}`, () => {
    loadNotification()
    window.open(url)
  })
}

function search(url) {
  window.open(url, "_blank")
}
function deleteAllNotification() {
  get(`/api/notification/delete-all`, loadNotification)
}
</script>

<template>
  <div class="main-content" v-loading="loading" element-loading-text="正在进入，请稍后...">
    <el-container style="height: 100%">
      <el-header class="main-content-header">
        <el-image class="logo" src="https://image.itbaima.cn/images/597/image-20240330175124276.png"/>
        <div style="flex: 1;padding: 0 20px;text-align: center">
          <el-input v-model="searchInput.text" style="width: 100%;max-width: 500px" placeholder="百度一下...🔍" @keyup.enter.native="search(`https://www.baidu.com/s?wd=${searchInput.text}`)">
            <template #prefix>
              <el-icon><Search/></el-icon>
            </template>
          </el-input>
        </div>
        <div class="user-info">
          <el-popover placement="bottom" :width="350" trigger="click">
            <template #reference>
              <el-badge style="margin-right: 15px" is-dot :hidden="!notification.length">
                <div class="notification">
                  <el-icon><Bell/></el-icon>
                  <div style="font-size: 10px">消息</div>
                </div>
              </el-badge>
            </template>
            <el-empty :image-size="80" description="暂时没有未读消息哦~" v-if="!notification.length"/>
            <el-scrollbar :max-height="500" v-else>
              <light-card v-for="item in notification" class="notification-item"
                          @click="confirmNotification(item.id, item.url)">
                <div>
                  <el-tag size="small" :type="item.type">消息</el-tag>&nbsp;
                  <span style="font-weight: bold">{{item.title}}</span>
                </div>
                <el-divider style="margin: 7px 0 3px 0"/>
                <div style="font-size: 13px;color: grey">
                  {{item.content}}
                </div>
              </light-card>
            </el-scrollbar>
            <div style="margin-top: 10px">
              <el-button size="small" type="info" :icon="Check" @click="deleteAllNotification"
                         style="width: 100%" plain>清除全部未读消息</el-button>
            </div>
          </el-popover>
            <div class="profile">
            <div>{{store.user.username}}</div>
            <div>{{store.user.email}}</div>
          </div>
          <el-dropdown>
            <el-avatar :src="store.avatarUrl"/>
            <template #dropdown>
              <el-dropdown-item @click="router.push('/index/user-setting')">
                <el-icon><Operation/></el-icon>
                个人信息
              </el-dropdown-item>
              <el-dropdown-item @click="router.push('/index/privacy-setting')">
                <el-icon><Message/></el-icon>
                隐私设置
              </el-dropdown-item>
              <el-dropdown-item @click="userLogout" divided>
                <el-icon><Back/></el-icon>
                退出登录
              </el-dropdown-item>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-container>
        <el-aside width="230px">
          <el-scrollbar style="height: calc(100vh - 55px)">
            <el-menu
                router
                :default-active="$router.path"
                :default-openeds="['1','2','3']"
                style="min-height: calc(100vh - 55px)">
              <el-sub-menu index="1">
                <template #title>
                  <el-icon><Location/></el-icon>
                  <span><b>校园论坛</b></span>
                </template>
                <el-menu-item index="/index">
                  <template #title>
                    <el-icon><ChatDotSquare/></el-icon>
                    帖子广场
                  </template>
                </el-menu-item>
                <el-menu-item index="/index/lost">
                  <template #title>
                    <el-icon><Bell/></el-icon>
                    失物招领
                  </template>
                </el-menu-item>
                <el-menu-item index="/index/love">
                  <template #title>
                    <el-icon><Umbrella/></el-icon>
                    表白墙
                  </template>
                </el-menu-item>
                <el-menu-item index="/index/ad">
                  <template #title>
                    <el-icon><School/></el-icon>
                    梦格考研
                    <el-tag style="margin-left: 10px">合作机构</el-tag>
                  </template>
                </el-menu-item>
              </el-sub-menu>
              <el-sub-menu index="2">
                <template #title>
                  <el-icon><Position/></el-icon>
                  <span><b>实用小工具</b></span>
                </template>
                <el-menu-item @click="search(`https://www.vkna.cn/`)">
                  <template #title>
                    <el-icon><Document/></el-icon>
                    <div>放松一下🎮</div>
                  </template>
                </el-menu-item>
                <el-menu-item @click="search(`https://chat18.aichatos.xyz/#/chat`)">
                  <template #title>
                    <el-icon><Files/></el-icon>
                    <div>AI聊天💬</div>
                  </template>
                </el-menu-item>
                <el-menu-item @click="search(`https://www.processon.com/`)">
                  <template #title>
                    <el-icon><Monitor/></el-icon>
                    <div>在线绘图✏️</div>
                  </template>
                </el-menu-item>
                <el-menu-item @click="search(`https://www.nlc.cn/web/index.shtml`)">
                  <template #title>
                    <el-icon><Collection/></el-icon>
                    <div>在线图书馆📖</div>
                  </template>
                </el-menu-item>
                <el-menu-item @click="search(`https://fanyi.youdao.com/`)">
                  <template #title>
                    <el-icon><DataLine/></el-icon>
                    <div>有道翻译🀄</div>
                  </template>
                </el-menu-item>
                <el-menu-item @click="search(`https://www.mujicv.com/`)">
                  <template #title>
                    <el-icon><Tickets /></el-icon>
                    <div>简历模板📄</div>
                  </template>
                </el-menu-item>
                <el-menu-item @click="search(`https://api.btstu.cn/sjbz/api.php?lx=dongman&format=images`)">
                  <template #title>
                    <el-icon><DataLine/></el-icon>
                    <div>随机壁纸🌆</div>
                  </template>
                </el-menu-item>
                <el-menu-item @click="search(`https://www.tingroom.com/`)">
                  <template #title>
                    <el-icon><Reading/></el-icon>
                    <div>在线听力室🎧</div>
                  </template>
                </el-menu-item>
                <el-menu-item @click="search(`https://www.duolingo.cn/`)">
                  <template #title>
                    <el-icon><Reading/></el-icon>
                    <div>外语学习🥸</div>
                  </template>
                </el-menu-item>
              </el-sub-menu>
              <el-sub-menu index="3">
                <template #title>
                  <el-icon><Operation/></el-icon>
                  <span><b>个人设置</b></span>
                </template>
                <el-menu-item index="/index/user-setting">
                  <template #title>
                    <el-icon><User/></el-icon>
                    个人信息
                  </template>
                </el-menu-item>
                <el-menu-item index="/index/privacy-setting">
                  <template #title>
                    <el-icon><Lock/></el-icon>
                    账号安全
                  </template>
                </el-menu-item>
              </el-sub-menu>
            </el-menu>
          </el-scrollbar>
        </el-aside>
        <el-main class="main-content-page">
          <el-scrollbar style="height: calc(100vh - 55px)">
            <router-view v-slot="{ Component }">
              <transition name="el-fade-in-linear" mode="out-in">
                <component :is="Component" style="height: 100%"/>
              </transition>
            </router-view>
          </el-scrollbar>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>



<style lang="less" scoped>
.search-card {

}
.notification-item {
  transition: .3s;
  &:hover {
    cursor: pointer;
    opacity: 0.7;
  }
}

.notification {
  font-size: 22px;
  line-height: 14px;
  text-align: center;
  transition: color .3s;

  &:hover {
    color: grey;
    cursor: pointer;
  }
}

.main-content-page{
  padding: 0;
  background-color: #f7f8fa;
}

.dark .main-content-page{
  background-color: #212225;
}

.main-content{
  height: 100vh;
  width: 100vw;
}
.main-content-header{
  border-bottom: solid 1px var(--el-border-color);
  height: 55px;
  display: flex;
  align-items: center;
  box-sizing: border-box;
}
.logo{
  height: 130px;
}
.user-info{
  display: flex;
  justify-content: flex-end;
  align-items: center;

  .el-avatar:hover {
    cursor: pointer;
  }

  .profile{
    text-align: right;
    margin-right: 20px;

    :first-child{
      font-size: 18px;
      font-weight: bold;
      line-height: 20px;
    }

    :last-child{
      font-size: 10px;
      color: grey;
    }
  }
}
</style>
