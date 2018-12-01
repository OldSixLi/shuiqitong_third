<!-- 页面描述:获取用户列表 -->
<template>
  <div>
    <!--
    '########::'#######::'########::
    ... ##..::'##.... ##: ##.... ##:
    ::: ##:::: ##:::: ##: ##:::: ##:
    ::: ##:::: ##:::: ##: ########::
    ::: ##:::: ##:::: ##: ##.....:::
    ::: ##:::: ##:::: ##: ##::::::::
    ::: ##::::. #######:: ##::::::::
    :::..::::::.......:::..:::::::::
    -->
    <mt-header fixed title="">
      <!-- 左侧 -->
      <mt-button icon="back" @click="topBack" slot="left">{{"用户列表"}}</mt-button>
      <!-- 右侧 -->
      <span slot="right" style="margin-right: -1rem;" class="span-right-search">
        <label>搜索</label>
        <mt-switch v-model="isSearch"></mt-switch>
      </span>
    </mt-header>
    <div class="page-content">
      <!--
      :'######::'########::::'###::::'########:::'######::'##::::'##:
      '##... ##: ##.....::::'## ##::: ##.... ##:'##... ##: ##:::: ##:
       ##:::..:: ##::::::::'##:. ##:: ##:::: ##: ##:::..:: ##:::: ##:
      . ######:: ######:::'##:::. ##: ########:: ##::::::: #########:
      :..... ##: ##...:::: #########: ##.. ##::: ##::::::: ##.... ##:
      '##::: ##: ##::::::: ##.... ##: ##::. ##:: ##::: ##: ##:::: ##:
      . ######:: ########: ##:::: ##: ##:::. ##:. ######:: ##:::: ##:
      :......:::........::..:::::..::..:::::..:::......:::..:::::..::
      -->
      <transition name="searchBlock">
        <div class="search-block" v-show="isSearch">
          <div>
            <mt-field label="姓　名" placeholder="请输入姓名" v-model.trim="userName"></mt-field>
            <mt-field label="手机号" placeholder="请输入手机号" v-model.trim="phoneNum" type="number"></mt-field>
          </div>
          <p class="text-right search-btns" style="padding:0 1rem;">
            <mt-button size="small" type="primary" @click="searchBtnClick">搜索</mt-button>
            <mt-button size="small" @click="cancelSearch">取消</mt-button>
          </p>
        </div>
      </transition>
      <!--
      '##:::::::'####::'######::'########:
       ##:::::::. ##::'##... ##:... ##..::
       ##:::::::: ##:: ##:::..::::: ##::::
       ##:::::::: ##::. ######::::: ##::::
       ##:::::::: ##:::..... ##:::: ##::::
       ##:::::::: ##::'##::: ##:::: ##::::
       ########:'####:. ######::::: ##::::
      ........::....:::......::::::..:::::
      -->
      <div v-for="(x,$index) in list" class="single-user" v-show="list.length>0">
        <div class="index-num" style="width:1.5rem;">
          {{indexMethod($index)}}
        </div>
        <!-- 用户头像 -->
        <div class="user-icon"><img :src="x.AVATAR|toSmallImg"></div>
          <!-- 用户信息 -->
          <div class="user-info">
            <p class="name">
              <span class="span-name"> {{x.NAME}} </span>
              <span class="gender-icon">
                <img :src="require('@/assets/img/icon/man-icon.png')"  v-if="x.GENDER==1">
                <img :src="require('@/assets/img/icon/woman-icon.png')" v-if="x.GENDER!=1">
            </span>
            </p>
            <p class="time"> <span class="span-time"> 登陆时间：{{x.LAST_TIME|toTime}} </span> </p>
          </div>
          <!-- 详情按钮 -->
          <div class="detail-icon" @click="toUserInfo(x)">
            <img :src="require('@/assets/img/icon/detail-icon.png')" alt="">
        </div>
          </div>

          <div v-show="list.length==0" class="text-center">
            <img :src="require('@/assets/img/no-result.png')">
            <h4 style="color:#696464;margin:0;">未搜索到结果</h4>
          </div>
        </div>

        <!--
      '########:::::'###:::::'######:::'########:
       ##.... ##:::'## ##:::'##... ##:: ##.....::
       ##:::: ##::'##:. ##:: ##:::..::: ##:::::::
       ########::'##:::. ##: ##::'####: ######:::
       ##.....::: #########: ##::: ##:: ##...::::
       ##:::::::: ##.... ##: ##::: ##:: ##:::::::
       ##:::::::: ##:::: ##:. ######::: ########:
      ..:::::::::..:::::..:::......::::........::
      -->
        <div class="bottom-page" v-show="totalNum>0">
          <p class="text-center btns">
            <!-- 首页 -->
            <mt-button size="small" type="primary" @click="pageFirst" :disabled="currentPage==1">首页</mt-button>
            <!-- 上一页 -->
            <mt-button size="small" type="primary" @click="pagePrev" :disabled="currentPage==1">上一页</mt-button>
            <!-- 页码 -->
            <mt-button size="small" @click="pageIndexClick"><span>{{currentPage}} / {{totalPage}}</span></mt-button>
            <!-- 下一页 -->
            <mt-button size="small" type="primary" @click="pageNext" :disabled="currentPage==totalPage">下一页</mt-button>
            <!-- 尾页 -->
            <mt-button size="small" type="primary" @click="pageLast" :disabled="currentPage==totalPage">尾页</mt-button>
          </p>
          <!-- 底部分页区域 -->
          <mt-popup v-model="isShowList" position="bottom" class="mint-popup-4">
            <p class="page-change-area">
              <mt-button type="default" size="small" @click="btnCancelPage">取消</mt-button>
              <mt-button type="primary" size="small" @click="btnChangePage">确定</mt-button>
            </p>
            <!-- 分页 -->
            <mt-picker :slots="numberSlot" @change="onDateChange" :visible-item-count="5" :show-toolbar="false"
              value-key="page"></mt-picker>
          </mt-popup>
        </div>
      </div>
</template>
<script>
  // import { mapGetters } from 'vuex';
  export default {
    name: "name",
    data() {
      return {
        userName: "",
        phoneNum: "",
        isSearch: false,
        list: [],
        currentPage: 1, //列表的当前页
        totalPage: 1, //列表的总页数,
        totalNum: 0, //总记录条数
        showCount: 10, //TODO 后期修改为10
        isShowList: false,
        currentChangePage: 1, //当前分页组件所选的分页值(未点击确定按钮时给此变量赋值)
        numberSlot: [{
          flex: 1,
          defaultIndex: 0,
          values: [], //在getTable方法中设置对象的形式
          className: 'slot1'
        }],
        searchData: {
          name: "",
          phone: ""
        }
      }
    },
    // '##::::'##:'########:'########:'##::::'##::'#######::'########:::'######::
    //  ###::'###: ##.....::... ##..:: ##:::: ##:'##.... ##: ##.... ##:'##... ##:
    //  ####'####: ##:::::::::: ##:::: ##:::: ##: ##:::: ##: ##:::: ##: ##:::..::
    //  ## ### ##: ######:::::: ##:::: #########: ##:::: ##: ##:::: ##:. ######::
    //  ##. #: ##: ##...::::::: ##:::: ##.... ##: ##:::: ##: ##:::: ##::..... ##:
    //  ##:.:: ##: ##:::::::::: ##:::: ##:::: ##: ##:::: ##: ##:::: ##:'##::: ##:
    //  ##:::: ##: ########:::: ##:::: ##:::: ##:. #######:: ########::. ######::
    // ..:::::..::........:::::..:::::..:::::..:::.......:::........::::......:::
    methods: {
      /**
       * 电话校验
       * 
       * @param {any} phone 手机号
       * @returns 是否有效 T/F
       */
      checkPhone(phone) {
        if (!(/^1(3|4|5|7|8)\d{9}$/.test(phone))) {
          return false;
        } else {
          return true;
        }
      },
      /**
       * 获取用户详情 
       * @returns 
       */
      toUserInfo(info) {
        let userId = info.ID;
        //加入全局变量
        this.$store.commit('setCurrentUser', info);
        this.$to(`/system/userInfo/${userId}`);
      },

      /**
       * 搜索按钮点击事件 
       * @returns 
       */
      searchBtnClick() {
        let phone = this.phoneNum;
        // 手机号校验
        if (phone) {
          if (!this.checkPhone(phone)) {
            this.$tip("请输入正确的手机号", "error");
            return false;
          }
        }
        let name = this.userName;
        this.currentPage = 1;
        this.searchData = {
          name,
          phone
        }
        this.getTable();
      },
      /**
       * 取消搜索 
       * @returns 
       */
      cancelSearch() {
        this.isSearch = false;
      },
      /**
       * 计算当前的列表index 
       * @returns 
       */
      indexMethod(index) {
        return (this.currentPage - 1) * this.showCount + index + 1;
      },
      /**
       * 点击选择页码  出现悬浮modal 
       * @returns 
       */
      pageIndexClick() {
        //如果总数只有一页的话 不允许点击
        if (this.currentPage == 1 && this.totalPage == 1) {
          this.isShowList = false;
          return;
        }
        this.isShowList = true;
      },
      /**
       * 取消选择页码 
       * @returns 
       */
      btnCancelPage() {
        this.isShowList = false;
      },
      /**
       * 页码点击确定按钮事件
       * @returns 
       */
      btnChangePage() {
        this.isShowList = false;
        if (this.currentPage != this.currentChangePage) {
          this.currentPage = this.currentChangePage;
          this.getTable();
        }
      },

      /**
       * 选择页码发生变化 
       * @returns 
       */
      onDateChange(picker, values) {
        if (!values[0]) {
          return;
        }
        this.currentChangePage = values[0].value;
      },
      /**
       * 顶部返回条 
       * @returns 
       */
      topBack() {
        this.$to("/")
      },
      //页面中用到的方法
      getTable(currentPage = this.currentPage, showCount = this.showCount) {
        this.$post(
          `/tax/userCenter/getTaxUserList`, {
            currentPage,
            showCount,
            userName: this.searchData.name,
            phone: this.searchData.phone
          }
        ).then(data => {
          if (data.success) {
            console.log(data);
            this.list = data.bean.data;
            this.totalPage = data.bean.pageCount;
            this.currentPage = data.bean.pageNum;
            this.totalNum = data.bean.rowCount;
            // 此处将页码塞进数组中用于展示所有页码
            let arr = [];
            for (let i = 0; i < this.totalPage; i++) {
              arr.push({
                value: i + 1,
                page: `第${i + 1}页`
              })
            }
            this.numberSlot[0].values = arr;
          } else {
            // this.$message.error(data.message || `操作失败,请重试！`); 
          }
        }).catch((err) => {
          console.log(err);
        });
      },
      // '########:::::'###:::::'######:::'########:
      //  ##.... ##:::'## ##:::'##... ##:: ##.....::
      //  ##:::: ##::'##:. ##:: ##:::..::: ##:::::::
      //  ########::'##:::. ##: ##::'####: ######:::
      //  ##.....::: #########: ##::: ##:: ##...::::
      //  ##:::::::: ##.... ##: ##::: ##:: ##:::::::
      //  ##:::::::: ##:::: ##:. ######::: ########:
      // ..:::::::::..:::::..:::......::::........::
      /**
       * 首页 
       * @returns 
       */
      pageFirst() {
        this.currentPage = 1;
        this.getTable();
      }
      /**
       * 上一页 
       * @returns 
       */
      ,
      pagePrev() {
        this.currentPage--;
        this.getTable();
      },
      /**
       * 下一页 
       * @returns 
       */
      pageNext() {
        this.currentPage++;
        this.getTable();
      },
      /**
       * 尾页 
       * @returns 
       */
      pageLast() {
        this.currentPage = this.totalPage;
        this.getTable();
      }
    },
    filters: {
      toSmallImg(val) {
        if (val) {
          let index = val.lastIndexOf('/');
          let str = val.substr(0, index + 1) + '100';
          return str;
        } else {
          return "";
        }
      }
    },
    mounted() {
      this.getTable();
    },
    watch: {
      currentPage(newValue, oldValue) {
        this.numberSlot[0].defaultIndex = newValue - 1;
      },
      isSearch(newValue, oldValue) {
        if (!newValue) {
          this.userName = this.searchData.name = '';
          this.phoneNum = this.searchData.phone = '';
          this.getTable(1);
        }
      }
    },
    beforeRouteEnter(to, from, next) {
      next(vm => {
        vm.getTable();
      });
    }
  }

</script>
<style scoped>
  .span-right-search {
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }

  .single-user {
    box-sizing: border-box;
    border-bottom: 1px dotted #efecec;
    height: 5.5rem;
    padding: 0.5rem;
    display: flex;
    justify-content: space-between;
  }

  .user-icon {
    flex: 0 0 5.5rem;
    text-align: center;
    vertical-align: middle;
  }

  .user-icon img {
    width: 3rem;
    margin-top: 0.75rem;
    border-radius: 50%;
    border: 1px solid #ddd;
  }

  .user-info {
    flex: 1;
    padding-left: .5rem;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  .user-info p {
    margin: 0;
  }

  .gender-icon img {
    top: 0.4rem;
    position: relative;
    width: 2rem;
    height: 2rem;
    margin: 0;
  }

  .span-time {
    font-size: 1.2rem;
    color: #7f7575;
  }

  .span-name {
    font-weight: 700;
    color: #5a5050;
  }

  .index-num {
    vertical-align: middle;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .detail-icon {
    height: 100%;
    align-self: flex-end;
    padding: 0 1rem;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .detail-icon img {
    width: 2.2rem;
    height: 2.2rem;
  }

  /* 底部分页区域 */

  .bottom-page {
    background-color: #fff;
    border-top: 1px solid #ddd;
    box-shadow: -1px 1px 4px 2px rgba(5, 5, 5, 0.1);
    height: 5rem;
    position: fixed;
    bottom: 0;
    width: 100%;
  }

  .mint-popup-4 {
    width: 100%;
    text-align: center;
  }

  .mint-popup-4 .picker-slot-wrapper,
  .mint-popup-4 .picker-item {
    backface-visibility: hidden;
    text-align: center;
  }

  .page-change-area {
    padding: 0 0.2rem;
    margin: 0.5rem;
    display: flex;
    justify-content: space-between;
  }

  .bottom-page>p.btns {
    vertical-align: middle;
    margin: 0;
    line-height: 5rem;
  }

  .searchBlock-enter-active {
    /* 动画过渡设置 */
    transition: all .3s;
    transform-origin: left top 0;

  }

  .searchBlock-leave-active {
    /* 动画过渡设置 */
    transition: all .3s;
    transform-origin: left top 0;
  }

  .searchBlock-leave,
  .searchBlock-enter-to {
    /* 终止,动画已完成 */
    transform: scale(1, 1);
    opacity: 1;
  }

  .searchBlock-leave-to,
  .searchBlock-enter {
    /* 起始,动画刚开始*/
    transform: scale(1, 0);
    opacity: 0.1;
  }

  .search-block {
    border: 1px solid #ddd;
    border-top: none;
    box-shadow: 0 6px 5px rgba(5, 5, 5, 0.1);
    /* margin:1rem; */
    margin-top: 0;
    margin-bottom: 1rem;
    border-bottom-left-radius: 1rem;
    border-bottom-right-radius: 1rem;

  }

  .search-block .search-btns {
    padding: 1rem 0;
  }

</style>
<style>
  .is-right .mint-switch {
    transform: scale(0.7)
  }

  .is-right .mint-switch-input:checked+.mint-switch-core {
    background-color: #653be6;
  }

</style>
