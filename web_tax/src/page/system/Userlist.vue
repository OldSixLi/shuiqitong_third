<template>
  <div style="padding:10px;">
    <el-popover placement="top" width="170" v-model="isShowPop" ref="delPop">
      <p class="text-center">确定为此用户重置密码吗?</p>
      <div style="text-align: right; margin: 0">
        <el-button size="mini" type="text" @click="cancelReset">取消</el-button>
        <el-button type="primary" size="mini" @click="confirmReset" :loading="resetLoading">确定</el-button>
      </div>
      <span slot="reference"></span>
    </el-popover>
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
    <el-dialog width="30%" title="分配角色" :visible.sync="showRolesModal" :modal-append-to-body='false'>
      <el-row>
        <el-col  :span="24">
          <p>为用户 <b>{{this.currentUserObj.NAME}}</b> 分配角色</p>
          <p class="table-title">角色列表</p>
          <el-table ref="rolesTable" border :data="tableRoleData" style="width: 100%" @selection-change="handleSelectionChange">

            <el-table-column type="selection" width="55" align="center" :selectable="checkTest"></el-table-column>

            <el-table-column label="角色名称" prop="description"></el-table-column>
            
          </el-table>
        </el-col>
      </el-row>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="saveRole" :loading="saveRoleLoading">确定</el-button>
        <el-button @click="showRolesModal=false">取消</el-button>
      </div>
    </el-dialog>
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
    <h2 class="text-center">用户管理</h2>
    <el-row style="margin-top: 15px;">
      <el-col :span="22" :offset="1">
        <p class="table-title">
          用户管理
        </p>
        <el-table ref="tableSet" border :data="tableData" style="min-width: 100%;" :maxHeight="availHeight()"
          :row-class-name="tableRowClassName">
          <el-table-column type="index" width="50" fixed align="center"></el-table-column>

          <el-table-column label="用户名称" prop="NAME"></el-table-column>

          <el-table-column label="头像" class="icon-img-td" width="160" align="center" style="padding:0;">
            <template slot-scope="scope">
              <img :src="scope.row.AVATAR" class="table-td-img" alt="" v-if="scope.row.AVATAR">
              <span v-if="!scope.row.AVATAR">-</span>
            </template>
          </el-table-column>

          <el-table-column prop="GENDER" label="性别" align="center" width="60">
            <template slot-scope="scope">
              {{scope.row.GENDER=='1'?'男':'女'}}
            </template>
          </el-table-column>

          <el-table-column label="手机号" align="center">
            <template slot-scope="scope">
              {{scope.row.MOBILE||'-'}}
            </template>
          </el-table-column>

          <el-table-column label="绑定账号" align="center">
            <template slot-scope="scope">
              {{scope.row.LOGIN_ACCOUNT||'-'}}
            </template>
          </el-table-column>

          <el-table-column prop="joinTime" label="上次登录时间" align="center">
            <template slot-scope="scope">
              {{scope.row.LAST_TIME|toTime}}
            </template>
          </el-table-column>

          <el-table-column label="操作" width="160" align="center" fixed="right">
            <template slot-scope="scope">
              <icon-btn :circle="true" @clicks="resetClick($event,scope.row)" :src="require('@/assets/img/login/resetPass.png')"
                word="重置密码"></icon-btn>
              <icon-btn :circle="true" @clicks="setRoleClick($event,scope.row)" :src="require('@/assets/img/system/role-set.png')"
                word="分配角色" />
            </template>
          </el-table-column>
        </el-table>
        <div style="margin-top:15px;">
          <el-pagination style="float:right;" @current-change="handleCurrentChange" :current-page.sync="currentPage1"
            layout="total, prev, pager, next" background :page-count="totalPage" :total="totalNum" v-show="totalPage>0">
          </el-pagination>
        </div>
      </el-col>
    </el-row>

  </div>
</template>
<script>
  import {
    toNormalTime
  } from '@/util/helper.js';
  import {
    getWindowHeight
  } from '@/util/dom.js';
  import IconBtn from '@/components/IconBtn';
  export default {
    components: {
      'icon-btn': IconBtn
    },
    data() {
      return {
        form: {
          name: '',
          region: '',
          date1: '',
          date2: '',
          delivery: false,
          type: [],
          resource: '',
          desc: ''
        },
        tableData: [],
        // 显示修改密码模态框
        showRolesModal: false,
        currentPage1: 1, //列表的当前页
        totalPage: 0, //列表的总页数,
        totalNum: 0, //总记录条数
        resetLoading: false,
        currentResetId: "",
        isShowPop: false,
        tableRoleData: [],
        currentUserObj: {},
        multipleSelection: [],
        saveRoleLoading: false,
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
      checkTest(row, index) {
        if (row.name == 'normal') {
          return false;
        } else {
          return true;
        }
      },
      /**
       * 获取当前的用户角色 
       * @returns 
       */
      getUserRole(userId) {
        this.$post(
            `/tax/roleManage/getAllRolesOfUser`, {
              userId
            })
          .then(data => {
            if (data.success) {
              let idArr = [];
              data.bean.forEach(x => {
                idArr.push(x.roleId)
              });
              if (idArr.length > 0) {
                this.$refs.rolesTable.clearSelection();
                let rows = this.tableRoleData.filter(x => {
                  return idArr.indexOf(x.id) > -1;
                })
                rows.forEach(row => {
                  this.$refs.rolesTable.toggleRowSelection(row, true);
                });
              }
            } else {
              this.$message.error(data.bean.message || `操作失败,请重试！`);
            }
          }).catch(() => {
            this.$message.error("请求服务失败,请重试！");
          });
      },
      /**
       * 获取角色列表 
       * @returns 
       */
      getAllRoles() {
        this.$get(`/tax/roleManage/getRoleList`)
          .then(data => {
            if (data.success) {
              let arr = data.bean;
              let index = null;
              arr.forEach((role, i) => {
                if (role.name == 'sys') {
                  index = i;
                }
              });
              if (index !== null) {
                arr.splice(index, 1);
              }
              this.tableRoleData = arr;

            } else {
              this.$message.error(data.message || `操作失败,请重试！`);
            }
          }).catch(() => {
            this.$message.error("请求服务失败,请重试！");
          });
      },
      /**
       * 
       * 为某个用户保存角色 
       * @returns 
       */
      saveRole() {
        //TODO 1.获取当前选择的角色
        //TODO 2.保存到当前的用户

        let val = this.multipleSelection;
        let roles = [];
        val.forEach(row => {
          roles.push(row.id)
        });
        
        //遍历获取到值
        let userId = this.currentUserObj.ID;
        this.saveRoleLoading = true;
        this.$post(
            `/tax/roleManage/saveOrUpdateUserRoles`, //TODO请求的地址
            {
              //TODO 请求的data参数
              userId,
              roleIds :roles.join(',')
            })
          .then(data => {
            if (data.success) {
              //TODO 操作成功进行的操作
              this.$message({
                message: '操作成功!',
                type: 'success'
              });
              this.showRolesModal = false;
              //TODO 是否需要重新获取列表
            } else {
              this.$message.error(data.message || `操作失败,请重试！`);
            }
          }).catch(() => {
            this.$message.error("请求服务失败,请重试！");
          }).finally(() => {
            this.saveRoleLoading = false;
          });

      },
      handleSelectionChange(val) {
        this.multipleSelection = val;
      },
      /**
       * 分配角色图标点击事件 
       * @returns 
       */
      setRoleClick(event, row) {
        this.showRolesModal = true;
        this.currentUserObj = row;
        this.$nextTick(() => {
          this.getUserRole(row.ID);
        });
      },
      /**
       * 删除操作 
       * @returns 
       */
      confirmReset() {
        let id = this.currentResetId;
        this.resetLoading = true;
        this.$post(`/tax/userCenter/reset`, {
          id
        }).then(data => {
          if (data.success) {
            this.$message({
              message: '操作成功!',
              type: 'success'
            });
            this.$alert('此账户密码已重置为: 111111 ,请提醒该成员及时登录修改密码', '提示', {
              confirmButtonText: '确定',
              callback: action => {}
            });
          } else {
            this.$message({
              message: data.message || '删除失败,请重试！',
              type: 'error'
            });
          }
        }).catch(() => {
          this.$message({
            message: "请求服务失败,请重试！",
            type: 'error'
          });
        }).finally(() => {
          this.isShowPop = false;
          this.resetLoading = false;
        })
      },
      /**
       * 点击取消按钮 
       * @returns 
       */
      cancelReset() {
        this.isShowPop = false;
      },
      /**
       * 点击重置密码图标
       * @returns 
       */
      resetClick({
        target
      }, row) {
        this.currentResetId = row.ID;
        this.resetLoading = false;
        this.isShowPop = true;
        this.$nextTick(() => {
          let pop = this.$refs.delPop;
          if (pop.popperJS) {
            pop.popperJS._reference = target;
            pop.popperJS.state.position = pop.popperJS._getPosition(pop.popperJS._popper, pop.popperJS._reference);
            pop.popperJS.update();
          }
        })
      },
      onSubmit() {},
      getTable() {
        this.$http.get('/tax/userCenter/getTaxUserList')
          .then(
            data => {
              if (data.success) {
                this.tableData = data.bean.data;
                this.totalPage = data.bean.pageCount;
                this.currentPage1 = data.bean.pageNum;
                this.totalNum = data.bean.rowCount;
              } else {
                this.$message({
                  message: '获取用户列表失败,请重试！',
                  type: 'error'
                });
              }
            }).catch(() => {
            this.$message({
              message: '请求服务失败,请重试！',
              type: 'error'
            });
          });
      },
      /**
       * 当前页码改变时 重新请求列表 
       * @returns 
       */
      handleCurrentChange(x) {
        this.getTable(this.currentPage1)
      },

      /**
       * 获取屏幕可用高度 
       * @returns 
       */
      availHeight() {
        return getWindowHeight() - 100;
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
    },
    beforeRouteEnter(to, from, next) {
      next(vm => {
        // 每次进入页面之前进行的操作
        vm.getTable();
        vm.getAllRoles();
      });
    },
    filters: {
      /**
       * 当前问题状态 由数字转换为文字
       */
      toStateName(val) {
        let str = "";
        // 1-未开启 2-已开启 3-已结束 4-已作废 5-已发送 
        switch (val) {
          case "1":
            str = "未开启";
            break;
          case "2":
            str = "已开启";
            break;
          case "3":
            str = "已结束";
            break;
          case "4":
            str = "已作废";
            break;
          case "5":
            str = "已发送";
            break;
          default:
            str = "";
            break;
        }
        return str;
      }
    },
    watch: {
      showRolesModal(newValue, oldValue) {
        if (!newValue) {
          this.currentUserObj = {};
        }

      }
    },
  }
</script>

<style>
  .table-td-img {
    width: 40px;
    height: 40px;
    margin: 0 auto;
    position: absolute;
    left: 0;
    right: 0;
    top: 3px;
  }
</style>
<style scoped>
  .slideout .fix-slide.active {
    height: 0;
  }

  .fix-slide {
    position: relative;
    left: 0;
    top: 0;
    width: 100%;
    height: 0;
    box-sizing: border-box;
    transition: height .3s;
  }

  .fix-slide.active {
    height: 100px;
  }

  h1,
  h2 {
    font-weight: normal;
    font-size: 25px;
    text-align: center;
  }
</style>
