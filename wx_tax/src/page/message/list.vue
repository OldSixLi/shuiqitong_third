<template>
  <div class="router-block">

    <mt-header fixed title="">
      <mt-button icon="back" @click="topBack" slot="left">{{"消息列表"}}</mt-button>
    </mt-header>
    <div class="page-content">
      <!-- 有内容 -->
      <template>
        <!-- 消息 -->
        <div v-show="!loadingData">
          <div style="padding-bottom:6rem;background-color: #fff;">
            <!-- 循环数据 -->
            <div class="msg-child" v-for="(x,$index) in msgList" @click="toMsgDetail(x.ID,x)">
              <p class="title">{{$index+pageParam1.currentPage*pageParam1.showCount-9}}. {{x.TITLE}}</p>
              <icon-word class="time-icons" style="" :src="require('@/assets/img/time.png')" :word="x.CREATE_TIME|toTime" />
              <icon-word class="time-icons" style="margin-left:0.4rem;" :src="require('@/assets/img/user.png')" :word="x.createName" />
              <span v-if="x.STATE!='1'" class="msg-state" style="">
                <mt-badge type="primary">未发送</mt-badge>
              </span>
            </div>
          </div>
        </div>
        <page :page-param="pageParam1" @pageChange="pageChange1"></page>

        <p v-show="loadingData" class="page-infinite-loading">
          <mt-spinner type="fading-circle"></mt-spinner>
          加载中...
        </p>
      </template>
    </div>
  </div>
</template>
<script>
  import Page from '@/components/page.vue';
  import IconWord from '@/components/IconWord';
  export default {
    name: "MessageList",
    props: {},
    components: {
      Page,
      "icon-word": IconWord
    },
    data() {
      //组件内数据部分
      return {
        pageParam1: {
          currentPage: 1, //列表的当前页
          totalPage: 1, //列表的总页数,
          totalNum: 0, //总记录条数
          showCount: 10,
        },
        msgList: [],
        msgLists: [],
        loading: false,
        currentPage: 1,
        totalPage: 1,
        loadingData: false,

      }
    },
    methods: {
      /**
       * 页码变化的事件 
       * @returns 
       */
      pageChange1(currentPage) {
        this.pageParam1.currentPage = currentPage;
        this.getList();
      },
      /**
       * 跳转消息详情页面 
       * @returns 
       */
      toDetail() {
        this.$to("/message/detail");
      },
      /**
       * 获取当前的列表 
       * @returns 
       */
      getList(currentPage = this.pageParam1.currentPage) {
        this.loading = true;
        this.loadingData = true;
        this.$get(
          `/tax/message/messageList`, {
            currentPage,
            showCount: this.pageParam1.showCount
          }).then(data => {
            if (data.success) {
              this.pageParam1.currentPage = data.bean.pageNum;
              this.pageParam1.totalPage = data.bean.pageCount;
              this.pageParam1.totalNum = data.bean.rowCount;
              // if (this.currentPage > this.totalPage) {
              //   this.currentPage = this.totalPage;
              // }
              if (data.bean && data.bean.data && data.bean.data.length > 0) {
                this.loading = (this.totalPage == 0) || (this.currentPage >= this.totalPage);
                this.msgList = data.bean.data || [];
              }
            } else {
              this.$tip("请求服务失败，请重试!");
              this.totalPage = 0;
              this.currentPage = 0;
            }
          },
          err => {
            this.currentPage = 0;
            this.totalPage = 0;
          }).finally(
          data => {
            this.loadingData = false;
          }
        )
      },
      toMsgDetail(id,info){
        //加入全局变量
        this.$store.commit('setCurrentMsg', info);
        this.$to("/message/detail/" + id);
      },
      // toMsgDetail(id, isRead, index) {
      //   console.log("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
      //   console.log(id);
      //   console.log("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
      //   if (id) {
      //     let readState = 0; //代表未读
      //     if (isRead) {
      //       readState = 1; //代表已读
      //     }
      //     this.$to("/message/detail/" + id + "?isRead=" + readState);
      //     this.msgList[index].userId = 1; //进行已读回执操作
      //   }
      // },
      topBack() {
        this.$to("/")
      },

      loadMore() {
        this.loading = true;
        this.currentPage++;
        this.getList(this.currentPage);
      }
    },
    beforeRouteEnter(to, from, next) {
      next(vm => {
        vm.msgList = [];
        vm.getList(1);
      })
    },
    created() {
      this.rightButtons = [{
          content: '标为已读',
          style: {
            background: 'lightgray',
            color: '#fff'
          }
        },
        {
          content: '删除',
          style: {
            background: 'red',
            color: '#fff'
          },
          handler: () => this.$confirm('确认删除吗?')
        }
      ];
    }
  }
</script>

<style>
  .time-icons .time-icon+span {
   
    position: relative;
    font-size: 1.3rem;
    top: -0.1rem;
  }
  .time-icons{
    margin-left: 0.5rem;
  }
</style>
<style scoped>
  .msg-child {
    padding-left: 0.5rem;
    /* background-color: #fff; */
  }

  .msg-child>p.time>img {
    margin: 0 0.3rem 0 0.5rem;
    width: 1.4rem;
    height: 1.4rem;
  }

  .msg-child>p.time {
    font-size: 1.3rem;
    line-height: 1.8rem;
    margin: 0;

  }

  .msg-child>p.time>span {
    line-height: 1.8rem;
    position: relative;
    top: -0.2rem;
    color: #8b8b8b;
  }

  .msg-child>p.title {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    margin: 0.5rem;
    margin-bottom: 0.4rem;

  }

  .msg-child>img.msg-img {
    width: 4rem;
    height: 4rem;
    position: absolute;
    left: 1rem;
    top: 0.5rem;
  }

  .msg-child>img.msg-img.unread {
    width: 3.5rem;
    height: 3rem;
    left: 1rem;
    top: 1rem;
  }

  .no-msg {
    padding-top: 3rem;
    color: #707070;
    font-weight: bold
  }

  .nomore-data {}

  .page-infinite-loading {
    text-align: center;
    height: 50px;
    line-height: 50px;
  }

  .msg-state {
    position: absolute;
    bottom: 0.2rem;
    right: 0;
  }

  .msg-state .mint-badge.is-primary.is-size-normal {
    /* border-radius: 0; */
    border-bottom-right-radius: 0;
    border-top-right-radius: 0;
    font-size: 1.2rem;
  }
</style>