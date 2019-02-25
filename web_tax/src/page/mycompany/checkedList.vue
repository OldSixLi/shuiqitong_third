<!-- 页面描述:消息企业查询页面 -->
<template>
  <div>

    <!--     
    '########::'####::::'###::::'##::::::::'#######:::'######:::
     ##.... ##:. ##::::'## ##::: ##:::::::'##.... ##:'##... ##::
     ##:::: ##:: ##:::'##:. ##:: ##::::::: ##:::: ##: ##:::..:::
     ##:::: ##:: ##::'##:::. ##: ##::::::: ##:::: ##: ##::'####:
     ##:::: ##:: ##:: #########: ##::::::: ##:::: ##: ##::: ##::
     ##:::: ##:: ##:: ##.... ##: ##::::::: ##:::: ##: ##::: ##::
     ########::'####: ##:::: ##: ########:. #######::. ######:::
    ........:::....::..:::::..::........:::.......::::......::::
    -->
    <el-dialog title="企业详情" :visible.sync="showDetail" width="60%" :modal-append-to-body='false' @close="modalClose">
      <!--NOTE 显示企业的详细信息 -->
      <el-row :gutter="10">
        <el-col :span="22" :offset="1">
          <p class="table-title"> <b>详细信息</b></p>
          <div style="border:1px solid #ddd;border-bottom-left-radius: 5px;border-bottom-right-radius: 5px;padding: 0 15px;">
            <el-row :gutter="10">
              <el-col :span="8">
                <p> <b>企业名称：</b>{{currentRow.entName}}</p>
              </el-col>
              <el-col :span="8">
                <p> <b>管理员：</b>{{currentRow.manager}}</p>
              </el-col>
            </el-row>
            <el-row :gutter="10">
              <el-col :span="8">
                <p> <b>注册时间：</b>{{currentRow.requestTime|toTime}}</p>
              </el-col>

              <el-col :span="8">
                <p> <b>审核通过时间：</b>{{currentRow.passTime|toTime}}</p>
              </el-col>
            </el-row>
          </div>

        </el-col>
      </el-row>
      <!-- NOTE 显示企业的人员构成 -->
      <el-row :gutter="10">
        <el-col :span="22" :offset="1">
          <p class="table-title"> <b>特殊角色</b></p>
          <!-- table -->
          <el-table stripe :data="rolesList">
            <el-table-column prop="roleName" label="身份"></el-table-column>
            <el-table-column prop="name" label="名称"></el-table-column>
            <el-table-column prop="roleName" label="性别">
              <template slot-scope="scope">
                {{scope.row.gender=='1'?'男':'女'}}
              </template>
            </el-table-column>
            <el-table-column prop="mobile" label="手机号"></el-table-column>

          </el-table>
        </el-col>
      </el-row>
      <!--NOTE 显示企业的问答列表 -->
      <el-row :gutter="10">
        <el-col :span="22" :offset="1">
          <p class="table-title"><b>问题列表</b></p>
          <!-- table -->
          <el-table :data="askList">
              <el-table-column type="index" width="50" fixed align="center" :index="indexMethod">
                </el-table-column>
            <el-table-column prop="title" label="问题标题"></el-table-column>
            <el-table-column prop="question" label="问题描述"></el-table-column>
            <el-table-column prop="name" label="提问者" align="right"></el-table-column>
            <el-table-column label="提问时间" align="center">
              <template slot-scope="scope">
                {{scope.row.queTime|toTime}}
              </template>
            </el-table-column>
          </el-table>

          <div style="margin-top:15px;">
            <el-pagination style="float:right;" @current-change="handleCurrentChange2" :current-page.sync="currentPage2"
              layout="total, prev, pager, next" background :page-count="totalPage2" :total="totalNum2" v-show="totalPage2>0">
            </el-pagination>
          </div>
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="showDetail = false">关 闭</el-button>
      </div>
    </el-dialog>
   

    <el-col :span="22" :offset="1">
      <h2>企业列表</h2>
      <el-row style="margin-top: 15px;">
        <p class="table-title">
          <span>企业列表</span>
          <el-button type="primary" size="mini" style="float:right" @click="checkComs">审核企业</el-button>
        </p>
        <!--
        '########::::'###::::'########::'##:::::::'########:
        ... ##..::::'## ##::: ##.... ##: ##::::::: ##.....::
        ::: ##:::::'##:. ##:: ##:::: ##: ##::::::: ##:::::::
        ::: ##::::'##:::. ##: ########:: ##::::::: ######:::
        ::: ##:::: #########: ##.... ##: ##::::::: ##...::::
        ::: ##:::: ##.... ##: ##:::: ##: ##::::::: ##:::::::
        ::: ##:::: ##:::: ##: ########:: ########: ########:
        :::..:::::..:::::..::........:::........::........::
        -->
        <el-table ref="tableSet" border :data="tableData" style="min-width: 100%;" :maxHeight="availHeight()"
          :row-class-name="tableRowClassName">
          <el-table-column type="index" width="50" fixed align="center" :index="indexMethod">
          </el-table-column>
          <el-table-column prop="entName" label="企业名称">
          </el-table-column>
          <el-table-column prop="sh" label="企业税号" align="left">

          </el-table-column>
          <el-table-column label="审核通过时间" align="center">
            <template slot-scope="scope">
              {{scope.row.passTime|toTime}}
            </template>
          </el-table-column>
          <el-table-column prop="managerName" label="管理员">
          </el-table-column>
          <el-table-column prop="phone" label="联系电话" align="center">
          </el-table-column>
          <el-table-column label="操作" minWidth="70" align="center" fixed="right">
            <template slot-scope="scope">
              <icon-btn :circle="true" @clicks="detailClick(scope.row)" :src="require('@/assets/img/detail.png')" word="企业详情"></icon-btn>
            </template>
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
    data() {
      return {
        msgId: "",
        tableData: [],
        currentPage1: 1, //列表的当前页
        totalPage: 0, //列表的总页数,
        totalNum: 0, //总记录条数
        showDetail: false,
        askList: [],
        rolesList: [],
        currentRow: {

        },
        pageSize:10,
        pageSize2:10,
        currentPage2: 1, //列表的当前页
        totalPage2: 0, //列表的总页数,
        totalNum2: 0,
      }
    },
    methods: {
      checkComs(){
        this.$to('/company/checking')
      },
      indexMethod(index){
        return (this.currentPage1 || 1) * (this.pageSize||10) + index - this.pageSize+1;
      },
      indexMethod2(index){
        return (this.currentPage2 || 1) * (this.pageSize2||10) + index -this.pageSize2+1;
      },
      modalClose() {
        this.currentRow={};
      },
      detailClick(row) {
        let enterpriseId = row.enterpriseId;
        this.currentRow = row;
        this.showDetail = true;
        this.$post(
            `/tax/enterprise/enterpriseRole`,
            {
              enterpriseId
            })
          .then(data => {
            if (data.success) {
              this.rolesList = data.bean;
            } else {
              this.$message.error(data.message || `操作失败,请重试！`);
            }
          }).catch(() => {
            this.$message.error("请求服务失败,请重试！");
          }).finally(() => {});

        this.getAskList(enterpriseId)
      },

      getAskList(){
        this.$post(
            `/tax/enterprise/queResList`, {
              enterpriseId,
              currentPage: this.currentPage2 || 1
            })
          .then(data => {
            if (data.success) {
              this.askList = data.bean.data;
              this.totalPage2 = data.bean.pageCount;
              this.currentPage2 = data.bean.pageNum;
              this.totalNum2 = data.bean.rowCount;
            } else {
              this.$message.error(data.message || `操作失败,请重试！`);
            }
          }).catch(() => {
            this.$message.error("请求服务失败,请重试！");
          }).finally(() => {});
      },
      handleCurrentChange(index) {
        this.currentPage1 = index;
        this.getTable();
      },
      handleCurrentChange2(index) {
        this.currentPage2 = index;
        this.currentRow.enterpriseId&&this.getAskList(this.currentRow.enterpriseId);
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
        this.$post(`/tax/enterprise/enterpriseList`, {
          state: 1,
          currentPage: this.currentPage1,
          showCount:this.pageSize
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
        let param = to.params;
        vm.getTable();
      });
    },
  }
</script>
<style scoped>
</style>