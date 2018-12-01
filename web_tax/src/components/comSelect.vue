<!-- 页面描述:消息企业查询页面 -->
<template>
  <div>
    <el-button type="text" @click="showModal">{{btnText}}</el-button>
    <el-dialog title="选择企业" :visible.sync="selectModal" :modal="showBack" :append-to-body="true" :close-on-click-modal="false"
      :modal-append-to-body='false' width="70%">
      <el-row>
        <el-col :span="24">

            <el-form>
                <el-row :gutter="20">
                  <el-col :span="10">
                    <el-form-item label="公司名称" label-width="100px" style="margin-bottom: 0;">
                      <el-input v-model.trim="keyWord" clearable placeholder="请输入关键字"></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="11">
                    <el-form-item label="公司税号" label-width="100px" style="margin-bottom: 0;">
                      <!-- <el-date-picker v-model="dateValue" type="daterange" style="width:100%" align="right" unlink-panels value-format="yyyy-MM-dd" 
                        range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" :picker-options="pickerOptions2">
                      </el-date-picker> -->

                      <el-input v-model.trim="sh" clearable placeholder="请输入公司税号"></el-input>
                    </el-form-item>
                  </el-col>
                  <el-col :span="3"><el-button style="width:100%;" type="primary" icon="el-icon-search" @click="searchList">查询</el-button></el-col>
                </el-row>
                
              </el-form>
              <!-- <p class="text-right" style="margin-bottom:0;">
                  
                </p> -->
        

          <p class="table-title">
            <span>企业列表</span>
            <span style="float:right;">已选择{{selectedComIds.length}}家公司</span>
          </p>
          <el-table ref="tableSet" border :data="tableData" style="min-width: 100%;" :maxHeight="availHeight()"
            :row-class-name="tableRowClassName" @selection-change="handleSelectionChange">
            <el-table-column type="selection" :selectable='checkboxT' width="50" align="center" :disabled="readOnly">
            </el-table-column>
            <el-table-column type="index" width="50" fixed align="center">
            </el-table-column>
            <el-table-column prop="ENT_NAME" label="公司名称">
            </el-table-column>

            <el-table-column prop="SH" label="税号" align="center">
            </el-table-column>
            <el-table-column label="	审核通过时间" align="center">
              <template slot-scope="scope">
                {{scope.row.REQUEST_TIME|toTime}}
              </template>
            </el-table-column>
            <el-table-column label="管理员" align="center">
              <template slot-scope="scope">
                {{scope.row.managerName||"-"}}
              </template>
            </el-table-column>
            <el-table-column prop="PHONE" label="联系电话" align="center">
            </el-table-column>
          </el-table>
          <div style="margin-top:15px;">
            <el-pagination style="float:right;" @current-change="handleCurrentChange" :current-page.sync="currentPage1"
              layout="total, prev, pager, next" background :page-count="totalPage" :total="totalNum" v-show="totalPage>0">
            </el-pagination>
          </div>
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer" style="margin-top:15px;">
        <el-button type="primary" @click="saveComs" v-show="!readOnly">确 定</el-button>
        <el-button @click="selectModal = false">关 闭</el-button>
      </div>
    </el-dialog>
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
    name: "ComSelect",
    //对外获取的数据
    props: {
      dateValue: '',
      keyWord:"",
      sh:'',
      comIds: {
        type: Array,
        required: false
      },
      btnText: {
        type: String,
        default: '请选择企业'
      },
      //是否显示背景模态框
      showBack: {
        type: Boolean,
        default: false
      },
      // 只展示详情 不允许修改
      readOnly: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        selectModal: false,
        msgId: "",
        tableData: [],
        currentPage1: 1, //列表的当前页
        totalPage: 0, //列表的总页数,
        totalNum: 0, //总记录条数
        // 字符串数组转数字数组
        selectedComIds: this.comIds && this.comIds.length > 0 ? this.comIds.reduce((a,b)=>a.concat(b-0),[]) : [],
        currentComs: [], //当前列表中企业列表,
        checkLock: false //锁定当前checkboxs
      }
    },
    methods: {
      checkboxT() {
        return !this.readOnly;
      },
      /**
       * 处理选择 
       * @returns 
       */
      handleSelectionChange(val) {
        if (!this.checkLock) {
          // 先将十个统一删除
          let totalSet = new Set(this.selectedComIds);
          this.currentComs.forEach(x => {
            if (totalSet.has(x)) {
              totalSet.delete(x);
            }
          });
          if (val.length) {
            let idArr = val.reduce((a, b) => a.concat(b.ID), []);
            this.selectedComIds = [...new Set([...totalSet, ...idArr])];
          } else {
            this.selectedComIds = [...totalSet];
          }
        }
      },
      saveComs() {
        this.selectModal = false;
        this.$emit('result', this.selectedComIds);
      },
      showModal() {
        this.selectModal = true;
      },
      handleCurrentChange(index) {
        this.currentPage1 = index;
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
        this.$post(`/greatchn/audit/enterpriseList`, {
          state: 1,
          currentPage: this.currentPage1
        }).then(data => {
          if (data.success) {
            //当前列表中的企业
            let checkedRows = [];
            let arr = data.bean.data;
            let currentIds = [];
            //收集当前的几个公司ID
            arr.forEach(com => currentIds.push(com.ID));
            this.currentComs = currentIds;
            //table数据赋值
            this.tableData = arr;
            this.totalPage = data.bean.pageCount;
            this.currentPage1 = data.bean.pageNum;
            this.totalNum = data.bean.rowCount;
            this.checkLock = true;
            this.$nextTick(() => {
              let totalSet = new Set(this.selectedComIds);
              console.log(this.selectedComIds);
              this.tableData.forEach(row => {
                if (totalSet.has(row.ID)) {
                  checkedRows.push(row);
                  this.$refs.tableSet && this.$refs.tableSet.toggleRowSelection(row, true);
                }
              })
              this.checkLock = false;
            })
          }
        })
      }
    },
    created() {
      console.log(this.comIds.reduce((a,b)=>a.concat(b-0),[]));
      this.getTable();
    },
  
    watch: {
      selectModal(newValue, oldValue) {
        if (newValue) {
          this.selectedComIds =this.comIds && this.comIds.length > 0 ? this.comIds.reduce((a,b)=>a.concat(b-0),[]) : [];
          this.currentPage1 = 1;
          this.getTable();
        }
      }
    },
  }

</script>
<style scoped>
  .table-title>span {
    font-weight: bold;
  }

</style>
