<!-- 页面描述:消息回执查询页面 -->
<template>

  <div>
    <!-- <h2>回执列表</h2>  -->
    <!-- <table></table> -->
    <el-col :span="22" :offset="1">
      <h2>回执列表</h2>
      <el-row style="margin-top: 15px;">
        <p class="table-title text-right">
          <span style="float:left;line-height: 35px;">回执列表</span>
          <el-button type="primary" size="small" @click="backList">返回消息列表</el-button>
        </p>
        <!-- <el-table ref="tableSet" border :data="tableData" style="min-width: 100%;" :maxHeight="availHeight()"
          :row-class-name="tableRowClassName">
          <el-table-column type="index" width="50" fixed align="center" :index="indexMethod">
          </el-table-column>
          <el-table-column prop="name" label="名称">
          </el-table-column>
          <el-table-column prop="entName" label="所属公司">
          </el-table-column>
          <el-table-column prop="roleName" label="身份">
          </el-table-column>
          <el-table-column label="查看时间" align="center">
            <template slot-scope="scope">
              {{scope.row.time|toTime}}
            </template>
          </el-table-column>
        </el-table> -->



        <el-table ref="tableSet" border :data="tableData" style="min-width: 100%;" :maxHeight="availHeight()"
          :row-class-name="tableRowClassName">

          <el-table-column type="expand">
            <template slot-scope="props">
              <el-table :data="props.row.receipt">
                <el-table-column prop="roleName" label="身份"></el-table-column>
                <el-table-column prop="name" label="姓名"></el-table-column>
                <el-table-column  label="查看时间" align="center">
                    <template slot-scope="scope">
                        {{scope.row.time|toTime}}
                      </template>
                </el-table-column>
              </el-table>
            </template>
          </el-table-column>
       <el-table-column type="index" width="50"  align="center" :index="indexMethod">
          </el-table-column> 
          <el-table-column prop="entName" label="公司名称">
          </el-table-column>
          <el-table-column prop="sh" label="税号">
          </el-table-column>
          <el-table-column label="注册时间" align="center">
            <template slot-scope="scope">
              {{scope.row.passTime|toTime}}
            </template>
          </el-table-column>
          <el-table-column label="已读/全部" align="center">
            <template slot-scope="scope">
           <span>{{getReadedUsers(scope.row.receipt)}} / {{scope.row.receipt.length}}</span>   
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top:15px;">
          <el-pagination style="float:right;" @current-change="handleCurrentChange" :current-page.sync="currentPage"
            layout="total, prev, pager, next" background :page-count="totalPage" :total="totalNum" v-show="totalPage>0">
          </el-pagination>
        </div>
      </el-row>
    </el-col>
  </div>


</template>
<script>
  import {
    getWindowHeight
  } from '@/util/dom.js';
  import {
    toNormalTime,
    toTimestamp
  } from '@/util/helper.js';
  // import { mapGetters } from 'vuex';
  export default {
    name: "ReadBack",
    //对外获取的数据
    props: {},
    data() {
      return {
        msgId: "",
        tableData: [],
        currentPage:1,
        totalPage:0,
        totalNum:0,
        pageSize:10,
      }
    },
    methods: {
      getReadedUsers(
        arr
      ){
      return arr.reduce((a,b)=>a+(b.time?1:0),0)
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
      handleCurrentChange(){
        this.getTable();
      },
      indexMethod(index) {
        return (this.currentPage || 1) * this.pageSize + index - 9;
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
      getTable(messageId=this.msgId,currentPage = this.currentPage) {
        this.$post(`/tax/message/receiptList`, {
          messageId,
          currentPage
        }).then(data => {
          if (data.success) {
            this.tableData = data.bean.data;
            this.totalPage = data.bean.pageCount;
            this.currentPage = data.bean.pageNum;
            this.totalNum = data.bean.rowCount;
          } else {
            this.tableData = [];
          }
        })
      }
    },
    beforeRouteEnter(to, from, next) {
      next(vm => {
        // 每次进入页面之前进行的操作
        let param = to.params;
        if (param.id) {
          vm.msgId = param.id;
          vm.getTable();
        } else {
          this.$message("缺少必要参数");
          this.$to("/message/list");
        }
      });
    },
    //实时计算
    computed: {
      // ...mapGetters([
      //     'departmentId'
      // ])
    },
  }
</script>
<style scoped>
</style>