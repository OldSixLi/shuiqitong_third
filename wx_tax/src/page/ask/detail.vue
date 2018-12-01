<!-- 问题答案页面 -->
<template>
  <div class="router-block">
    <mt-header fixed title="">
      <mt-button icon="back" @click="topBack" slot="left">
        问题详情
      </mt-button>
    </mt-header>
    <div class="page-content">
      <p class="ask-title"> {{detailObj.QUE_TITLE}} </p>
      <div class="ask-detail title-span-block"><span>问题描述</span>
        <p> {{detailObj.QUESTION}} </p>
        <p class="text-left ask-time" style="margin-bottom: 0;" v-if="detailObj.QUE_TIME">
          <icon-word :src="require('@/assets/img/user.png')" :word="detailObj.QUE_USER_ID" />
          <icon-word :src="require('@/assets/img/time.png')" :word="detailObj.QUE_TIME|toTime" />
        </p>
      </div>

      <div class="ask-answer title-span-block">
        <span v-if="detailObj.RESPONSE">答案详情</span>
        <p v-if="detailObj.RESPONSE">{{detailObj.RESPONSE}}</p>

        <p class="text-left ask-time" v-if="detailObj.RESPONSE" style="margin-top: 1.2rem;margin-bottom: 0;">

          <icon-word :src="require('@/assets/img/user.png')" :word="detailObj.resName" />
          <icon-word :src="require('@/assets/img/time.png')" :word="detailObj.RES_TIME|toTime" />

        </p>
        <span v-if="!detailObj.RESPONSE">回复问题</span>
        <!-- <p v-if="!detailObj.RESPONSE" class="nomore-data"> -->
        <div style="height:100%;">
          <textarea v-if="!detailObj.RESPONSE" v-model="response" placeholder="请在此输入问题的回复" style="" class="textarea-ans" row="10" col="10"></textarea>
        </div>
        <!-- </p> -->
      </div>
      <p class="text-right">
        <mt-button size="small" type="default" @click="topBack">返回列表</mt-button>
        <mt-button size="small" type="primary" @click="saveAns" v-if="!detailObj.RESPONSE">回复问题</mt-button>

      </p>
    </div>
  </div>
</template>
<script>
  import IconWord from '@/components/IconWord';
  export default {
    name: "askDetail",
    components: {
      "icon-word": IconWord
    },
    props: {},
    data: function () {
      // 组件内数据部分
      return {
        currentId: "",

        response:"",
        detailObj: {}
      }
    },
    beforeRouteEnter(to, from, next) {
      next(vm => {
        vm.response="";
        vm.currentId = to.params.id;
        vm.getDetail(vm.currentId);
      });
    },
    mounted: function () {
      //组件生成时调用
    },
    methods: {
      saveAns() {
        let qrId = this.detailObj.ID;
        if (!qrId) {
          return;
        }
        let response = this.response;
        if (!response) {
          this.$alert(`请输入问题回复后再进行保存!`);
          return false;
        }
        this.$post(
            `/tax/queres/saveAndSend`, {
              response,
              qrId
            })
          .then(data => {
            if (data.success) {
              this.$tip('回复问题成功!');
              this.getDetail(this.currentId);
            } else {
              this.$tip(data.message || `操作失败,请重试！`);
            }
          }).catch(() => {
            this.$tip("请求服务失败,请重试！");
          });
      },

      getDetail(qurResId) {
        this.$get(`/queres/detail`, {
          qurResId
        }).then(
          data => {
            if (data.success) {
              this.detailObj = data.bean;
            } else {
              this.$alert(data.message).then(x => {
                this.$to("/ask/list");
              })
            }
          }
        )
      },
      topBack() {
        this.$to("/ask/list");
      },
      backList() {
        this.$back();
      }
    },
    watch: {
      currentId(newValue, oldValue) {
        // this.getDetail(this.currentId);
        this.getDetail(newValue);
      }
    },
  }
</script>
<style scoped>
  .textarea-ans {
    width: 100%;
    height: 100%;
    box-sizing: border-box;
    border-radius: 5px;
    border: none;
    padding: 0.5rem;
    min-height: 6rem;
    margin-top: 0.5rem;
  }

  .textarea-ans:focus {
    border: 0.1rem solid #66afe9;
    box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075), 0 0 8px rgba(102, 175, 233, .6);
    outline: none;
  }

  .page-content {
    padding: 1rem;
    padding-top: 4rem;
  }

  .ask-title {
    font-weight: bold;
    border: none;
  }

  .title-span-block>span:first-child {
    content: "问题描述";
    display: block;
    position: absolute;
    top: -1rem;
    left: 50%;
    transform: translateX(-50%);
    background-color: #26a2ff;
    padding: 0.2rem 0.5rem;
    font-size: 1.3rem;
    border: 1px solid #38f;
    border-radius: 2px;
    color: #fff;
  }

  .ask-detail,
  .ask-answer {
    position: relative;
    border-radius: 0.5rem;
    border: 0.1rem solid #ddd;
    min-height: 5rem;
    margin: 2rem 0 1rem 0;
    padding: 1rem 0.5rem;
  }

  .ask-answer>p {
    margin: 0.5rem 0;
  }

  .ask-time span {
    color: #ab7f7f;
  }

</style>
