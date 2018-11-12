<!-- 页面描述:消息企业查询页面 -->
<template>

  <div>
    <el-col :span="22" :offset="1">
      <h2>企业列表</h2>
      <el-row style="margin-top: 15px;">
        <p class="table-title">
          <span>已通过企业列表</span>
        </p>

        <el-table ref="tableSet" border :data="tableData" style="min-width: 100%;" :maxHeight="availHeight()"
          :row-class-name="tableRowClassName">
          <el-table-column type="index" width="50" fixed align="center">
          </el-table-column>
          <el-table-column prop="ENT_NAME" label="公司名称">
          </el-table-column>
          <el-table-column label="提交审核时间" align="center">
            <template slot-scope="scope">
              {{scope.row.REQUEST_TIME|toTime}}
            </template>
          </el-table-column>
          <el-table-column label="	审核通过时间" align="center">
            <template slot-scope="scope">
              {{scope.row.PASS_TIME|toTime}}
            </template>
          </el-table-column>
          <el-table-column prop="PHONE" label="联系电话" align="center">
          </el-table-column>
          <el-table-column prop="taxName" label="税务分局名称">
          </el-table-column>

        </el-table>
        <div style="margin-top:15px;">
          <el-pagination style="float:right;" @current-change="handleCurrentChange" :current-page.sync="currentPage1"
            layout="total, prev, pager, next" background :page-count="totalPage" :total="totalNum" v-show="totalPage>0">
          </el-pagination>
        </div>
      </el-row>
    </el-col>
  </div>


</template>
<script>
  //引入方法
  import {
    toNormalTime,
    toTimestamp
  } from '@/util/helper.js';
  import {
    getWindowHeight
  } from '@/util/dom.js';
  // import { mapGetters } from 'vuex';
  export default {
    name: "ComList",
    //对外获取的数据
    props: {},
    data() {
      return {
        msgId: "",
        tableData: [],
        currentPage1: 1, //列表的当前页
        totalPage: 0, //列表的总页数,
        totalNum: 0, //总记录条数
      }
    },
    methods: {
      handleCurrentChange(index){
        this.currentPage1=index;
        this.getTable();
      },
            /**
           * 列表初始化将index赋值 
           * @returns 
           */
          tableRowClassName({
            row,
            rowIndex
          }) {
            row.index = rowIndex;
          },
      //页面中用到的方法
      backList() {
        this.$to('/message/list')
      },
      /**
       * 获取屏幕可用高度 
       * @returns 
       */
      availHeight() {
        return getWindowHeight() - 100;
      },
      getTable() {
        this.$post(`/greatchn/audit/entList`, {
          state: 1,
          currentPage:this.currentPage1
        }).then(data => {
          if (data.success) {
            this.tableData = data.bean.data;
            this.totalPage = data.bean.pageCount;
            this.currentPage1 = data.bean.pageNum;
            this.totalNum = data.bean.rowCount;
          }
        })
      }
    },
    beforeRouteEnter(to, from, next) {
      next(vm => {
        // 每次进入页面之前进行的操作
        let param = to.params;
        vm.getTable();
      });
    },
   
  }
</script>
<style scoped>
</style>