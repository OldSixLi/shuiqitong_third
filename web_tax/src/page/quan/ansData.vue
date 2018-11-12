<!-- 页面描述:某个问卷答案统计分析 显示图表 -->
<template>
  <div>
    <!--NOTE 响应式设置 :xs="8" :sm="6" :md="4" :lg="3" :xl="1" -->
    <el-row :gutter="10">
      <el-col :span="22" :offset="1">
        <h2>《{{obj.title||""}}》统计分析</h2>
        <p class="table-title text-right" style="padding:3px 10px;"><b style="float:left;line-height: 35px;" :disabled="!detailId">问卷基础信息</b>
        <el-button  type="primary" size="small" @click="toAnsList">查看用户答案列表</el-button>
        </p>
        <div style="border:1px solid #ddd;border-bottom-left-radius: 5px;border-bottom-right-radius: 5px;padding:0 15px;">
          <p>
            <el-row>
              <el-col :span="6"><b>开始时间 :</b> {{obj.startTime}}</el-col>
              <el-col :span="6"><b>结束时间 :</b> {{obj.endTime||"未结束调查"}}</el-col>
              <el-col :span="6"><b>参与调查人数 :</b> {{obj.totalNum||0}} 人</el-col>
            </el-row>
          </p>
        </div>
        <!--
        '##::::'##::'#######::'########:::::'###::::'##:::::::
         ###::'###:'##.... ##: ##.... ##:::'## ##::: ##:::::::
         ####'####: ##:::: ##: ##:::: ##::'##:. ##:: ##:::::::
         ## ### ##: ##:::: ##: ##:::: ##:'##:::. ##: ##:::::::
         ##. #: ##: ##:::: ##: ##:::: ##: #########: ##:::::::
         ##:.:: ##: ##:::: ##: ##:::: ##: ##.... ##: ##:::::::
         ##:::: ##:. #######:: ########:: ##:::: ##: ########:
        ..:::::..:::.......:::........:::..:::::..::........::
        -->
        <el-dialog title="消息详情" :visible.sync="showAnsModal" :modal-append-to-body='false' @close="modalClose">
          <template v-for="(x,$index) in textList">
              <div class="text-item">
            <p class="user-name"> <span>{{$index+1}} . {{x.userName}}</span>
              <span class="" style="float:right;">{{x.time|toTime}}</span>
            </p>
            <p class="asn">{{x.text}}</p>
        </div>
          </template>
          <div style="margin-top:15px;">
            <el-pagination style="float:right;" @current-change="handleCurrentChange" :current-page.sync="currentPage1"
              layout="total, prev, pager, next" background :page-count="totalPage" :total="totalNum" v-show="totalPage>0">
            </el-pagination>
          </div>
          <div slot="footer" class="dialog-footer">
            <el-button  @click="showAnsModal = false">关 闭</el-button>
          </div>
        </el-dialog>

        <!--
            :::'###::::'##::: ##::'######::
            ::'## ##::: ###:: ##:'##... ##:
            :'##:. ##:: ####: ##: ##:::..::
            '##:::. ##: ## ## ##:. ######::
            #########: ##. ####::..... ##:
            ##.... ##: ##:. ###:'##::: ##:
            ##:::: ##: ##::. ##:. ######::
            ..:::::..::..::::..:::......:::
            -->
        <!--NOTE 响应式设置 :xs="8" :sm="6" :md="4" :lg="3" :xl="1" -->
        <el-row :gutter="10" v-for="(x,$index) in quesList" :key="$index" style="padding:0 10px;margin-top:15px;border-bottom: 1px solid #ddd;">
          <el-col :span="10">
            <div class="col-md-5 col-sm-12 qnas">
              <div class="qna-body">
                <p style="font-size:0;font-weight:bold;">
                  <span style="display:inline-block;min-width: 20px;font-size: 14px;">{{$index+1}}.</span>
                  <b style="font-size:14px;">{{x.qustion.stem}}</b>
                </p>
                <div class="form-group chooseContain" style="padding:0 20px;">
                  <ul v-for="(y,yIndex) in x.options" v-show="x.qustion.typeArr&&(x.qustion.typeArr.indexOf('1')>-1||x.qustion.typeArr.indexOf('2')>-1)"
                    style="padding-left:0;">
                    <li style="list-style: none; ">
                      <el-radio :label="y.id.toString()" :key="y.id" v-model="x.optionAnswer" :disabled="true" v-if="x.qustion.typeArr&&(x.qustion.typeArr.indexOf('1')>-1)">{{''}}</el-radio>
                      <el-checkbox :disabled="true" v-if="x.qustion.typeArr&&(x.qustion.typeArr.indexOf('2')>-1)"
                        :checked="x.optionAnswer&&x.optionAnswer.split(',').indexOf(y.id.toString())>-1"></el-checkbox>
                      <b>{{y.content}} （{{y.poll||0}}）人</b>
                    </li>
                  </ul>
                  <span v-if="x.qustion.type=='3'||x.qustion.type=='4'">
                    <b>其他：</b>
                    <el-button type="primary" size="mini" @click="textAnsClick(x.qustion.id)">查看用户答案<span> （{{x.otherNum}}）人</span></el-button>
                  </span>
                  <p v-if="x.qustion.type=='5'">
                    <el-button type="primary" size="mini" @click="textAnsClick(x.qustion.id)">查看用户答案</el-button>
                  </p>
                </div>
              </div>
            </div>

          </el-col>
          <el-col :span="14" v-if="x.qustion.type!='5'">
            <div class="col-md-7 col-sm-12">
              <div class="qna-pic" :id="'pic'+x.qustion.id" _echarts_instance_="ec_1541471962245" style="-webkit-tap-highlight-color: transparent; user-select: none; position: relative;width:100%;height: 300px;">
              </div>
            </div>

          </el-col>
        </el-row>

        <p class="text-right">
          <el-button type="primary" @click="backList">返回问卷列表</el-button>
        </p>
      </el-col>
    </el-row>
  </div>
</template>
<script>
  import {
    mapGetters
  } from 'vuex';
  let echarts = require('echarts/lib/echarts');
  // 引入饼状图组件
  require('echarts/lib/chart/pie');
  require('echarts/lib/component/tooltip');
  require('echarts/lib/component/title');
  require('echarts/lib/component/legend');
  export default {
    name: "name",
    //对外获取的数据
    data() {
      return {
        isAdd: true,
        isEdit: false,
        currentId: "",
        isDetail: true,
        obj: {
          createTime: '',
          createUserId: "admin",
          endTime: '',
          giveFlag: "",
          introduction: "",
          modifyTime: '',
          modifyUserId: '',
          requiredLogin: "N",
          score: '',
          startTime: '',
          state: '',
          surveyChannel: [],
          surveyUrl: '',
          title: '',
        },
        quesList: [],
        detailId: "",
        textList: [],
        showAnsModal: false,
        currentQusId: "", //当前查看文字答案列表的问题ID
        currentPage1: 1, //列表的当前页
        totalPage: 0, //列表的总页数,
        pageSize: 10,
        totalNum: 0,
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
      toAnsList(){
        this.detailId&&this.$to(`/quan/statistics/ansList/${this.detailId}`);
      },
      indexMethod(index) {
        return (this.currentPage1 || 1) * this.pageSize + index - 9;
      },
      handleCurrentChange(val) {
        this.currentPage1 = val;
        this.getDetail(this.currentQusId);
      },
      textAnsClick(queId) {
        this.showAnsModal = true;
        this.currentQusId = queId;
        this.$post(
            `/tax/questionStatics/inputList`, {
              queId,
              currentPage: this.currentPage1,
              pageSize: 2
            })
          .then(data => {
            if (data.success) {
              this.textList = data.bean.data;
              this.totalPage = data.bean.pageCount;
              this.pageSize = data.bean.pageSize;
              this.currentPage1 = data.bean.pageNum;
              this.totalNum = data.bean.rowCount;
            } else {
              this.$message.error(data.message || `请求服务失败,请重试！`);
            }
          }).catch(() => {
            this.$message.error("请求服务失败,请重试！");
          }).finally(() => {
            //TODO 此处需要重置某些ajax loading状态

          });
      },
      modalClose() {

      },
      backList() {
        this.$to('/quan/statistics');
      },
      /**
       * 画图 
       * @returns 
       */
      drawPie(domId, pieData, pieNames) {
        var optionPie = {
          title: {
            text: "用户答案分布情况",
            x: "center"
          },
          tooltip: {
            trigger: "item",
            formatter: "{a} <br/>{b} : {c} 人 ({d}%)"
          },
          legend: {
            orient: "vertical",
            left: "left",
            data: pieNames,
            formatter: function (name) {
              return name.length > 10 ? name.substr(0, 11) + "..." : name;
            }
          },
          series: [{
            name: "选择人数",
            type: "pie",
            radius: "55%",
            center: ["50%", "60%"],
            data: pieData,
            itemStyle: {
              emphasis: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)"
              }
            }
          }]
        };
        var pieChart = echarts.init(document.getElementById("pic" + domId));
        pieChart.setOption(optionPie);
      },
      /**
       * 获取详情并画图 
       * @returns 
       */
      getDetail(qnaId) {
        this.$post(
          `/tax/questionStatics/statistics`, {
            qnaId
          }).then(data => {
          if (data.success) {
            this.obj = data.bean;
            this.obj.title = data.bean.title || "";
            let arr = data.bean.questions;
            // 转换数组形式
            arr.forEach(element => {
              switch (element.qustion.type) {
                case '1':
                  element.qustion.typeArr = ['1'];
                  break;
                case '2':
                  element.qustion.typeArr = ['2'];
                  break;
                case '3':
                  element.qustion.typeArr = ['1', '3'];
                  break;
                case '4':
                  element.qustion.typeArr = ['2', '3'];
                  break;
                case '5':
                  element.qustion.typeArr = ['3'];
                  element.options = [];
                  break;
                default:
                  element.qustion.typeArr = [];
                  element.options = [];
              }
            });
            // 问题选项赋值
            this.quesList = arr;
            // 下一个进程
            this.$nextTick(() => {
              this.quesList.forEach(x => {
                let quesID = x.qustion.id,
                  valArr = [],
                  nameArr = [];
                x.options.forEach(b => {
                  valArr.push({
                    name: b.content,
                    value: b.poll
                  });
                  nameArr.push(b.content);
                });
                x.qustion.type != '5' && this.drawPie(quesID, valArr, nameArr);
              });
            })
          } else {
            this.$message.error("未获取到数据");
          }
        }).catch((error) => {
          console.log(error);
        });
      }
    },
    beforeRouteEnter(to, from, next) {
      next(vm => {
        // 每次进入页面之前进行的操作
        let param = to.params;
        let qnaId = param.qnaId;
        if (qnaId) {
          vm.detailId=qnaId;
          vm.getDetail(qnaId);
        } else {
          vm.detailId="";
          vm.$to("/quan/list")
        }
      });
    },
    watch: {
      showAnsModal(newValue, oldValue) {
        if (!newValue) {
          this.currentQusId = "";
          this.currentPage1 = 1;
        }
      }
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
    .text-item{
        border-bottom: 1px solid #ddd;
        margin: 5px;
    }
    .text-item:last-child{
        border-bottom: none;    
    }
.text-item .user-name{
    font-weight: bold;
    color: #000;
}
    .text-item .asn{
        color:#384a4a;
    }
</style>