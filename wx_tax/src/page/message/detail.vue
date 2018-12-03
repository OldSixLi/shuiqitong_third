<template>
  <div class="router-block" v-cloak>
    <mt-header fixed title="">
      <mt-button icon="back" @click="topBack" slot="left">{{"消息详情"}}</mt-button>
    </mt-header>
    <div class="page-content">

<!-- 
        ONTENT
        :
        "测试第三方应用与自建应用使用不同的数据库"
        CREATE_TIME
        :
        1543546819000
        CREATE_USER_ID
        :
        16
        ID
        :
        125
        MSGTYPE
        :
        "textcard"
        STATE
        :
        "1"
        TITLE
        :
        "测试更换数据库消息"
        TYPE
        :
        "1"
        createName
        :
        "张瑶" -->
      <p class="text-left question-title">{{obj.TITLE||""}}</p>
      <p class="text-left ask-time">
        <!-- <img src="@/assets/img/time.png" class="time-icon">
        <span v-if="obj.CREATE_TIME"> 发布时间 : {{ obj.CREATE_TIME | toTime}}</span> -->
        <icon-word class="time-icons" style="margin-left:0.4rem;" :src="require('@/assets/img/time.png')" >发布时间：{{obj.CREATE_TIME | toTime}}</icon-word>
        <icon-word class="time-icons" style="margin-left:1.4rem;" :src="require('@/assets/img/user.png')" >发布人：{{obj.createName}}</icon-word>
      </p>
      <div class="answer-area">{{obj.CONTENT}}</div>
      <mt-button size="small" type="primary" style="float:right;" @click="topBack">返回列表</mt-button>
    </div>
  </div>
</template>
<script>
   import {
    mapGetters
  } from 'vuex'; import IconWord from '@/components/IconWord';
  export default {
    name: "MessageDetail",
    data() {
      return {
        detailId: "",
        obj: {},
        isRead: false
      }
    },
    components: {
      IconWord
    },
    created() {
      this.detailId = this.$param(this).id;
    },
     //实时计算
     computed: {
      ...mapGetters([
        'currentMsgInfo'
      ])},
    beforeRouteEnter(to, from, next) {
      next(vm => {
        vm.detailId = to.params.id;
        vm.obj=vm.currentMsgInfo;
        if(!vm.currentMsgInfo.TITLE){
          Vue.prototype.$back();
        }
        console.log("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
        console.log(vm.obj);
        console.log(vm.currentMsgInfo);
        console.log("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
      
      });
    },
    methods: {
      topBack() {
        this.$to("/message/list");
        // this.$router.back();
      },
      /**
       * 发送回执 
       * @returns 
       */
      sendBackInfo(messageId = "", userId = "") {
        this.$post(`/message/receipt`, {
          messageId,
          userId
        }).then(
          data => {
            console.log(data);
          }
        )
      },
      /**
       * 获取当前的返回信息 
       * @returns 
       */
      getDetail(id) {
        //TODO 获取答案详情
        this.$get(`/message/detail`, {
          messageId: id
        }).then(
          data => {
            if(data.success){
              
            this.obj = data.bean;
            //发送回执信息
            if (!this.isRead) {
              this.sendBackInfo(id, this.userId);
            }
            }else{
              this.$alert(data.message).then(action => {
                this.$to("/message/list")
              });
            }
          }
        )
      }
    },
    watch: {
      // detailId(newValue, oldValue) {
      //   this.getDetail(newValue);
      // }
    },
    beforeRouteUpdate (to, from, next) {
      this.obj = {};
      next();
    }
  }

</script>
<style scoped>
  .content {
    padding: 1rem;
  }

  .question-title {
    margin: 1rem 0;
    font-size: 2rem;
  }

  .answer-area {
    border-radius: 0.5rem;
    border: 0.1rem solid #ddd;
    min-height: 10rem;
    margin: 0 0 1rem 0;
    padding: 0.5rem;
  }

  .page-content {
    padding: 4rem 1rem 4rem;
  }

</style>
