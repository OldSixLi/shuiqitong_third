<!-- 页面描述:用户详情页面 -->
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
      <mt-button icon="back" @click="backUserList" slot="left">{{"用户详情"}}</mt-button>
    </mt-header>
    <div class="page-content">
      <!--
      '####:'##::: ##:'########::'#######::
      . ##:: ###:: ##: ##.....::'##.... ##:
      : ##:: ####: ##: ##::::::: ##:::: ##:
      : ##:: ## ## ##: ######::: ##:::: ##:
      : ##:: ##. ####: ##...:::: ##:::: ##:
      : ##:: ##:. ###: ##::::::: ##:::: ##:
      '####: ##::. ##: ##:::::::. #######::
      ....::..::::..::..:::::::::.......:::
      -->
      <!-- 
EMPLOYEE_ID: 22
ENTERPRISE_ID: "wxa059996e5d72516b"
GENDER: "1"
HAVE_PW: "Y"
ID: 1
LAST_TIME: 1542259642000
LOGIN_ACCOUNT: "mashaobo"
MOBILE: "18222603560"
NAME: "马少博"
STATE: "Y"
TAX_ID: 1
USER_ID: "TDevG4_105"
roleNames: "管理员,普通用户" -->
      <div class="user-info">
        <img :src="currentUserInfo.AVATAR" class="user-icon">

        <p><b>　用户名：</b>{{currentUserInfo.NAME}}</p>
        <p><b>　　性别：</b>{{currentUserInfo.GENDER=='1'?"男":"女"}}</p>
        <p><b>　手机号：</b>{{currentUserInfo.MOBILE}}</p>
        <p><b>绑定账号：</b>{{currentUserInfo.LOGIN_ACCOUNT}}</p>
        <p><b>用户身份：</b>
          <span v-for="(x,$index) in roleNames">
            <template v-if="$index==0">
                {{x}}
            </template>
            <template v-else>
              <b></b>{{'　　　　　 '+x}}
            </template>
            <br/>
          </span>
        </p>
        
        
        <p><b>上次登录：</b>{{currentUserInfo.LAST_TIME|toTime}}</p>
        <div class="btns">
          <mt-button type="danger" @click="changePass">重置密码</mt-button>
          <mt-button type="primary" @click="changeAuth">分配角色</mt-button>
        </div>
      </div>
    </div>
    <mt-popup v-model="authModal" position="right" class="mint-popup-3" :modal="false">
      <div style="margin:1rem;">
        <span></span>
        <p v-for="x in tableRoleData">
          {{x.description}}
        </p>
        <p class="small-margin">　为 <b>{{currentUserInfo.NAME}}</b> 分配角色</p>

        <mt-checklist title="" align="right" v-model="checkedAuths" :options="tableRoleData">
        </mt-checklist>
      </div>
      <p style="width:100%;" class="bottom-auth-btns">
        <mt-button @click.native="authModal = false" size="small">取消</mt-button>
        <mt-button @click.native="saveRole" size="small" type="primary">保存</mt-button>
      </p>
    </mt-popup>
  </div>
</template>
<script>
  import {
    mapGetters
  } from 'vuex';
  export default {
    name: "name",
    data() {
      return {
        userId: "",
        authModal: false,
        tableRoleData: [],
        checkedAuths: []
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
       * 获取当前的用户角色 
       * @returns 
       */
      getUserRole() {
        let userId = this.userId;
        this.$post(
            `/tax/roleManage/getAllRolesOfUser`, {
              userId
            })
          .then(data => {
            if (data.success) {
              let idArr = [],nameArr=[];
              data.bean.forEach(x => {
                idArr.push(x.roleId);
                nameArr.push(x.roleName);
              });
              if (idArr.length > 0) {
                this.checkedAuths=idArr;
              }
              this.currentUserInfo.roleNames=nameArr.join(',');
            } else {
              this.$tip(data.bean.message || `操作失败,请重试！`);
            }
          }).catch((err) => {
            console.log(err);
            this.$tip("请求服务失败,请重试！");
          });
      },
      /**
       * 
       * 为某个用户保存角色 
       * @returns 
       */
      saveRole() {
        let roles =this.checkedAuths;
        let userId = this.userId;
        this.$post(
            `/tax/roleManage/saveOrUpdateUserRoles`, //TODO请求的地址
            {
              userId,
              roleIds: roles.join(',')
            })
          .then(data => {
            if (data.success) {
              //TODO 成功提示
              this.authModal = false;
              this.getUserRole();
              this.$tip("修改用户角色成功！");
            } else {
              //错误提示
              this.$tip(data.message||"操作失败,请重试！");
            }
          }).catch((err) => {
            //错误提示
            console.log(err);
          });

      },
      /**
       * 修改密码 
       * @returns 
       */
      changePass() {
        this.$confirm("确定要为此用户重置密码吗？", "提示").then(action => {
          if (action == 'confirm') {
            let id = this.userId;
            //重置密码操作
            this.$post(
                `/tax/userCenter/reset`, {
                  id
                })
              .then(data => {
                if (data.success) {
                  this.$tip("重置密码操作成功，请提示该用户及时登陆web系统修改密码！", 3000);
                } else {
                  this.$tip(data.message || `操作失败,请重试！`);
                }
              }).catch(() => {
                this.$tip("请求服务失败,请重试！");
              });
          }
        })
      },
      /**
       * 修改权限 
       * @returns 
       */
      changeAuth() {
        this.authModal = true;
      },
      /**
       * 顶部返回条 
       * @returns 
       */
      backUserList() {
        this.$to("/system/user")
      },
      /**
       * 获取角色列表 
       * @returns 
       */
      getRoleList() {
        this.$post(
            `/tax/roleManage/getRoleList`,
          )
          .then(data => {
            if (data.success) {
              let arr = [];
              data.bean.forEach((role, i) => {
                if (role.name != 'sys') {
                  // index = i;
                  let obj = {
                    label: role.description,
                    value: role.id
                  };
                  if (role.name == 'normal') {
                    obj.disabled = true;
                    this.checkedAuths.push(role.id);
                  }
                  arr.push(obj);
                }
              });
              this.tableRoleData = arr;
            } else {
              this.$message.error(data.message || `操作失败,请重试！`);
            }
          }).catch(() => {
            this.$message.error("请求服务失败,请重试！");
          });
      }
    },
    created() {
      this.getRoleList();
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
      if (!this.currentUserInfo.ID) {
        this.backUserList();
      }
    },
    watch: {
      userId(newValue, oldValue) {}
    },
    beforeRouteEnter(to, from, next) {
      next(vm => {
        let userId = to.params && to.params.userId || "";
        if (!userId) {
          vm.backUserList();
        } else {
          vm.userId = userId;
          vm.getUserRole();
        }
      });
    },
    beforeRouteLeave(to, from, next) {
      next();
    },
    //实时计算
    computed: {
      ...mapGetters([
        'currentUserInfo'
      ]),
      roleNames() {
        /**
         * 此处代码可优化,应该改成统一配置的变量,否则代码不好维护
         * @returns 
         */
        if (this.currentUserInfo) {
          if (!this.currentUserInfo.roleNames || (this.currentUserInfo.roleNames && this.currentUserInfo.roleNames.length ==
              0)) {
            return ["普通用户"];
          }
        }
        let arr = this.currentUserInfo.roleNames.split(',');
        let sets = new Set(arr);
        if(sets.has("管理员")){
          return ["管理员"];
        }
        sets.delete('普通用户');
        return sets.size > 0 ? [...sets] : ["普通用户"];
      }
    },
  }
</script>
<style scoped>
  .user-info {
    width: 100%;
    text-align: center;
  }

  .user-info p {
    /* width: 60%; */
    margin: 1rem auto;
    margin-left: 20%;

    text-align: left;
  }

  .user-info .btns {
    width: 60%;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
  }

  .page-content {
    display: flex;
    justify-content: center;
  }

  .user-icon {
    border: 1px solid #ddd;
    width: 10rem;
    height: 10rem;
    border-radius: 50%;
  }

  .mint-popup-3 {
    width: 100%;
    height: 100%;
    background-color: #fff;
  }

  .bottom-auth-btns {
    box-sizing: border-box;
    width: 100%;
    height: 5rem;
    border-top: 1px solid #ddd;
    box-shadow: -1px 1px 4px 2px rgba(5, 5, 5, 0.1);
    padding: 0 5%;
    position: fixed;
    bottom: 0;
    vertical-align: middle;
    margin: 0;
    display: flex;
    justify-content: space-between;
    align-items: center;

  }

  .bottom-auth-btns .mint-button {
    width: 10rem;
  }
</style>
