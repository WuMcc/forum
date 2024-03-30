<script setup>
import LightCard from "@/components/LightCard.vue";
import {
  Calendar,
  Clock,
  CollectionTag,
  Compass,
  Discount,
  Document,
  Edit,
  EditPen,
  Picture,
  Link, Microphone, CircleCheck, Star, ArrowRightBold, FolderOpened, Umbrella, Cpu, Money, Stamp, Iphone, Van
} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, reactive, ref, watch} from "vue";
import {ElMessage} from "element-plus";
import {get} from "@/net/index.js";
import TopicEditor from "@/components/Topic/TopicEditor.vue";
import axios from "axios";
import {useStore} from "@/store/index.js";
import ColorDot from "@/components/ColorDot.vue";
import router from "@/router/index.js";
import TopicTag from "@/components/Topic/TopicTag.vue";
import TopicCollectList from "@/components/Topic/TopicCollectList.vue";

function goUrl(url){
  window.open(url, "_blank")
}

const today = computed(() => {
  const date = new Date()
  return `${date.getFullYear()} 年 ${date.getMonth() + 1} 月 ${date.getDate()} 日`
})

</script>

<template>
  <div style="display: flex;margin: 20px auto;gap: 20px;max-width: 900px">
    <div style="flex: 1">
      <light-card style="margin-top: 10px;height: 30px;display: grid;grid-template-columns: repeat(5, 1fr);grid-gap: 10px">
        <div class="type-select-card">
          <el-icon style="transform: translateY(3px)"><Iphone /></el-icon>
          <span @click="goUrl('https://image.itbaima.cn/images/597/image-20240330172662232.png')" style="line-height: 30px;margin-left: 7px">考研咨询</span>
        </div>
        <div class="type-select-card">
          <el-icon style="transform: translateY(3px)"><Cpu /></el-icon>
          <span @click="goUrl(`https://www.bilibili.com/video/BV1gA411j7Ta/?spm_id_from=333.337.search-card.all.click&vd_source=613740e7868c610a453e870e266c6de8`)" style="line-height: 30px;margin-left: 7px">考研数学</span>
        </div>
        <div class="type-select-card">
          <el-icon style="transform: translateY(3px)"><Money /></el-icon>
          <span @click="goUrl(`https://www.bilibili.com/video/BV1CE411o7x3/?spm_id_from=333.337.search-card.all.click`)" style="line-height: 30px;margin-left: 7px">考研政治</span>
        </div>
        <div class="type-select-card">
          <el-icon style="transform: translateY(3px)"><Stamp /></el-icon>
          <span @click="goUrl(`https://www.zhihu.com/tardis/zm/art/448353853?source_id=1005`)" style="line-height: 30px;margin-left: 7px">考研英语</span>
        </div>
        <div class="type-select-card">
          <el-icon style="transform: translateY(3px)"><Van /></el-icon>
          <span @click="goUrl(`https://yz.chsi.com.cn/kyzx/zyk/`)" style="line-height: 30px;margin-left: 7px">考研专业课</span>
        </div>
      </light-card>
      <light-card style="margin-top: 10px;display: flex;gap: 10px">
        <div>
          <el-tag type="danger" size="large">热门咨询</el-tag>
          <div style="color: burlywood;font-weight: bold"><a target="_blank" style="text-decoration: none;color: burlywood" href="https://yz.chsi.com.cn/kyzx/kydt/202403/20240312/2293269473.html">【官宣】2024年考研国家线发布_2024考研复试分数线</a></div>
          <div style="font-size: 14px;margin:5px;color: lightskyblue"><a target="_blank" style="text-decoration: none;color: teal" href="https://kaoyan.wendu.com/2023/0505/207161.shtml">【汇总】各地院校2024硕士研究生招生简章</a></div>
          <div style="font-size: 14px;margin:5px;color: lightskyblue"><a target="_blank" style="text-decoration: none;color: peachpuff" href="https://kaoyan.eol.cn/shiti/yingyu/202312/t20231226_2551123.shtml">2024考研英语真题答案及解析汇总</a></div>
          <div style="font-size: 14px;margin: 5px;color: lightskyblue"><a target="_blank" style="text-decoration: none;color: darkblue" href="https://kaoyan.eol.cn/shiti/shuxue/202312/t20231226_2551143.shtml">2024考研数学真题答案及解析汇总</a></div>
          <div style="font-size: 14px;margin: 5px;color: lightskyblue"><a target="_blank" style="text-decoration: none;color: greenyellow" href="https://zhenti.burningvocabulary.cn/cet6">2023年12月大学英语六级考试真题及答案汇总</a></div>
          <div style="margin: 5px;color: burlywood;font-weight: bold"><a target="_blank" style="text-decoration: none;color: greenyellow" href="https://kaoyan.eol.cn/tiao_ji/tiaojizhinan/202402/t20240228_2560544.shtml">2024年全国硕士研究生考试各省研招院校招生调剂信息汇总</a></div>
          <div style="margin: 5px;color: burlywood;font-weight: bold"><a target="_blank" style="text-decoration: none;color: lightskyblue" href="https://kaoyan.eol.cn/nnews/202403/t20240313_2563259.shtml">2024考研34所自主划线院校的复试分数线汇总</a></div>
          <div style="font-size: 20px;font-weight: bold;margin: 5px;color: lightskyblue"><a  target="_blank" style="text-decoration: none;color: deeppink" href="https://image.itbaima.cn/images/597/image-20240330172662232.png">25考研考公公共课/专业课学长学姐一对一</a></div>
        </div>
      </light-card>
      <transition name="el-fade-in" mode="out-in">
        <div>
          <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
            <light-card style="height: 150px" class="topic-card" @click="goUrl(`http://www.moe.gov.cn/srcsite/A15/moe_778/s3113/202309/t20230915_1080603.html`)">
              <div style="margin-top: 5px">
                <span style="font-weight: bold;margin-left: 7px">教育部《2024年全国硕士研究生招生工作管理规定》的通知</span>
              </div>
              <div class="topic-content">各省、自治区、直辖市高等学校招生委员会、教育厅（教委）、教育招生考试机构，新疆生产建设兵团教育局，有关部门（单位）教育司（局），各硕士研究生招生单位：

                　　为做好2024年全国硕士研究生招生工作，现将《2024年全国硕士研究生招生工作管理规定》印发给你们，请遵照执行。
              </div>
              <div class="topic-content" style="text-align: right">
                教育部

                2023年9月15日
              </div>
              <div style="display: grid;grid-template-columns: repeat(3, 1fr);grid-gap: 10px">
                <el-image class="topic-image" src="http://www.moe.gov.cn/srcsite/images/scy_jyb_lgo_03.png" fit="cover"></el-image>
              </div>
            </light-card>
          </div>
          <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
            <light-card style="height: 150px" class="topic-card" @click="goUrl(`https://yz.chsi.com.cn/kyzx/kydt/202403/20240312/2293269479.html`)">
              <div style="margin-top: 5px">
                <span style="font-weight: bold;margin-left: 7px">教育部部署2024年全国硕士研究生招生复试录取工作</span>
              </div>
              <div class="topic-content">近日，教育部部署2024年全国硕士研究生招生复试录取工作，要求各地各招生单位以习近平新时代中国特色社会主义思想为指导，深入贯彻党的二十大精神，全面贯彻党的教育方针，落实立德树人根本任务，坚持综合评价、择优录取，严格执行政策，严守工作纪律，规范录取行为，优化考生服务，确保2024年硕士研究生复试录取工作公平、公正、科学。
              </div>
              <div class="topic-content" style="text-align: right">
                教育部

                2024年3月12日
              </div>
              <div style="display: grid;grid-template-columns: repeat(3, 1fr);grid-gap: 10px">
                <el-image class="topic-image" src="https://t3.chei.com.cn/axvert/img/5303043866.jpg" fit="cover"></el-image>
              </div>
            </light-card>
          </div>
          <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
            <light-card style="height: 150px" class="topic-card" @click="goUrl(`https://yz.chsi.com.cn/kyzx/kyft/zxft/202310/20231026/2293158475.html`)">
              <div style="margin-top: 5px">
                <span style="font-weight: bold;margin-left: 7px">西北农林科技大学：农业科学进入ESI全球前0.1‰</span>
              </div>
              <div class="topic-content">2024年西北农林科技大学风景园林艺术学院新增园林植物与观赏园艺（090706）和设计（135700）招生专业，取消风景园林学（083400）和艺术设计（135108）招生专业，设计学专业代码调整为130500。学校农业科学、植物学与动物学、环境科学与生态学等3个学科进入ESI全球前1‰，其中农业科学进入前0.1‰。
              </div>
              <div class="topic-content" style="text-align: right">
                ——访西北农林科技大学研究生招生处处长布都会
              </div>
              <div style="display: grid;grid-template-columns: repeat(3, 1fr);grid-gap: 10px">
                <el-image class="topic-image" src="https://t3.chei.com.cn/news/img/2293158476.jpg" fit="cover"></el-image>
              </div>
            </light-card>
          </div>
          <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
            <light-card style="height: 150px" class="topic-card" @click="goUrl(`https://gradadmissions.smbu.edu.cn/info/1018/2151.htm`)">
              <div style="margin-top: 5px">
                <span style="font-weight: bold;margin-left: 7px">深圳北理莫斯科大学2024年硕士研究生招生简章</span>
              </div>
              <div class="topic-content">深圳北理莫斯科大学是国家主席习近平、俄罗斯总统普京达成重要共识，由深圳市人民政府、北京理工大学和莫斯科国立罗蒙诺索夫大学（简称“莫斯科大学”）三方合作设立的具有独立法人资格的公办性质的中外合作大学。学校以建设独具特色的世界一流国际化综合性研究型大学为目标，致力于开展精英教育以及高水平科学研究和创新活动。2016年10月，学校获教育部批准正式设立。2017年学校招收首批本科生和硕士研究生，2018年招收首批博士研究生，2020年设立博士后创新实践基地，2021年入选广东省新一轮高水平大学重点学科建设高校，2022年3月入选国家自然科学基金依托单位。
              </div>
              <div class="topic-content" style="text-align: right">
                深圳北理莫斯科大学

                2024-02-04
              </div>
              <div style="display: grid;grid-template-columns: repeat(3, 1fr);grid-gap: 10px">
                <el-image class="topic-image" src="https://gradadmissions.smbu.edu.cn/images/logo.png" fit="cover"></el-image>
              </div>
            </light-card>
          </div>
          <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
            <light-card style="height: 150px" class="topic-card" @click="goUrl(`https://yz.chsi.com.cn/kyzx/jyxd/202305/20230511/2285297985.html`)">
              <div style="margin-top: 5px">
                <span style="font-weight: bold;margin-left: 7px">决定考研之前，关于城市和其他选择</span>
              </div>
              <div class="topic-content">进入五月，新一轮考研人的复习计划正式拉开序幕，你做好吃苦的准备了吗？

                随着大学毕业生人数的增多，社会竞争压力越来越大，为了得到更好的工作机会，越来越多的本科生选择考研。

                经历过高考的洗礼，我们都知道，高考最大的特点是先考试然后填报志愿；而考研的流程是先选定志愿，经过统一网上报名后，参加研考初试全国统考。也就是说，考研生必须尽早确定想要报考的目标招生单位（院校）和专业。

                一些考生在选目标院校和选专业上一直犹豫不决，下面小编就来帮你梳理一下，希望看完这篇文章帮你在考研路上开一个好头。
              </div>
              <div class="topic-content" style="text-align: right">
                中国研究生招生信息网

                2023年05月11日
              </div>
              <div style="display: grid;grid-template-columns: repeat(3, 1fr);grid-gap: 10px">
                <el-image class="topic-image" src="https://t2.chei.com.cn/axvert/img/5267787316.jpg" fit="cover"></el-image>
              </div>
            </light-card>
          </div>
        </div>
      </transition>
    </div>
    <div style="width: 280px">
      <div style="position: sticky;top: 31px">
        <light-card>
          <div class="info-text">
            <div>当前日期</div>
            <div>{{today}}</div>
          </div>
          <el-divider style="margin: 10px 0"/>
          <div >
            <img @click="goUrl(`https://www.wendu.com/`)" style="width: 100%;height: 100%" src="https://ts1.cn.mm.bing.net/th?id=OIP-C.9eyD62h3Pu21ou-1JLhVqQHaJ4&w=146&h=195&c=8&rs=1&qlt=90&o=6&dpr=1.4&pid=3.1&rm=2" alt=""/>
          </div>
        </light-card>
        <light-card style="margin-top: 10px">
          <el-divider style="margin: 10px 0"/>
          <img @click="goUrl(`https://www.wendu.com/`)" style="width: 100%;height: 100%" src="https://tse1-mm.cn.bing.net/th/id/OIP-C.npMlptwEG2X1AWM4QdDm5QAAAA?w=189&h=255&c=7&r=0&o=5&dpr=1.4&pid=1.7" alt=""/>
        </light-card>
        <light-card style="margin-top: 10px">
          <el-divider style="margin: 10px 0"/>
          <img @click="goUrl(`https://www.wendu.com/`)" style="width: 100%;height: 100%" src="https://tse3-mm.cn.bing.net/th/id/OIP-C.oBGm7tMZpfTDn3XB2_hqKAHaE2?w=189&h=124&c=7&r=0&o=5&dpr=1.4&pid=1.7" alt=""/>
        </light-card>
        <light-card style="margin-top: 10px">
          <el-divider style="margin: 10px 0"/>
          <img @click="goUrl(`https://www.wendu.com/`)" style="width: 100%;height: 100%" src="https://tse4-mm.cn.bing.net/th/id/OIP-C.rsn_ocXa455rb9vRY6atUwHaEK?w=189&h=106&c=7&r=0&o=5&dpr=1.4&pid=1.7" alt=""/>
        </light-card>
      </div>
    </div>
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
.type-select-card{
  background-color: #f5f5f5;
  padding: 2px 7px;
  font-size: 14px;
  border-radius: 3px;
  box-sizing: border-box;
  transition: background-color .3s;
  &.active {
    border: solid 1px #ead4c4;
  }
  &:hover {
    cursor: pointer;
    background-color: #dadada;
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
    font-size: 13px;
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