<!-- 页面描述:Page 分页组件 -->
<!-- mashaobo 2018年11月20日14:53:15 新增 -->
<template>
  <div>
    <div class="bottom-page" v-show="pageParam.totalNum>0">
      <p class="text-center btns">
        <!-- 首页 -->
        <mt-button size="small" type="primary" @click="pageFirst" :disabled="pageParam.currentPage==1">首页</mt-button>
        <!-- 上一页 -->
        <mt-button size="small" type="primary" @click="pagePrev" :disabled="pageParam.currentPage==1">上一页</mt-button>
        <!-- 页码 -->
        <mt-button size="small" @click="pageIndexClick">
          <span>{{pageParam.currentPage}} / {{pageParam.totalPage}}</span></mt-button>
        <!-- 下一页 -->
        <mt-button size="small" type="primary" @click="pageNext" :disabled="pageParam.currentPage==pageParam.totalPage">下一页</mt-button>
        <!-- 尾页 -->
        <mt-button size="small" type="primary" @click="pageLast" :disabled="pageParam.currentPage==pageParam.totalPage">尾页</mt-button>
      </p>
      <!-- 底部分页区域 -->
      <mt-popup v-model="isShowList" position="bottom" class="mint-popup-4">
        <p class="page-change-area">
          <mt-button type="default" size="small" @click="btnCancelPage">取消</mt-button>
          <mt-button type="primary" size="small" @click="btnChangePage">确定</mt-button>
        </p>
        <!-- 分页 -->
        <mt-picker 
        :slots="numberSlot" 
        @change="onDateChange" 
        :visible-item-count="5" 
        :show-toolbar="false" 
        value-key="page"></mt-picker>
      </mt-popup>
    </div>
  </div>
</template>
<script>
  export default {
    name: "Page",
    props: {
      pageParam: {
        type: Object,
        required: true
      }
    },
    data() {
      return {
        // 分页信息展示
        numberSlot: [{
          flex: 1,
          defaultIndex: 0,
          values: [], 
          className: 'slot1'
        }],

        currentChangePage: 1, //中间变量存储当前的分页信息
        isShowList: false //是否显示分页信息
      }
    },
    methods: {
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
       * 页码点击确定按钮事件
       * @returns 
       */
      btnChangePage() {
        this.isShowList = false;
        if (this.pageParam.currentPage != this.currentChangePage) {
          this.pageParam.currentPage = this.currentChangePage;
          this.emitChange();
        }
      },
      /**
       * 取消选择页码事件 
       * @returns 
       */
      btnCancelPage() {
        this.isShowList = false;
      },
      /**
       * 点击选择页码  出现悬浮modal 
       * @returns 
       */
      pageIndexClick() {
        if (this.pageParam.currentPage == 1 && this.pageParam.totalPage == 1) {
          this.isShowList = false;
          return;
        }
        this.isShowList = true;
      },
      /**
       * 对父组件触发页码变化事件 
       * @returns 
       */
      emitChange() {
        this.$emit('pageChange', this.pageParam.currentPage);
      },
      //页面中用到的方法
      /**
       * 首页 
       * @returns 
       */
      pageFirst() {
        this.pageParam.currentPage = 1;
        this.emitChange();
      }
      /**
       * 上一页 
       * @returns 
       */
      ,
      pagePrev() {
        this.pageParam.currentPage--;
        this.emitChange();
      },
      /**
       * 下一页 
       * @returns 
       */
      pageNext() {
        this.pageParam.currentPage++;
        this.emitChange();
      },
      /**
       * 尾页 
       * @returns 
       */
      pageLast() {
        this.pageParam.currentPage = this.pageParam.totalPage;
        this.emitChange();
      }
    },
    watch: {
      //实时监控数据变化
      'pageParam.currentPage': function (newValue, oldValue) {
        this.numberSlot[0].defaultIndex = newValue - 1;
      },
      'pageParam.totalPage': function (total, oldValue) {
        if (!total) {
          return;
        }
        let arr = [];
        for (let i = 0; i < total; i++) {
          arr.push({value: i + 1,page: `第${i + 1}页`})
        }
        this.numberSlot[0].values = arr;
      }
    }
  }
</script>
<style scoped>
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
</style>
