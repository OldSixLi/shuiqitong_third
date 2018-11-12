<!-- 页面描述:绑定账号页面 -->
<!-- 逻辑:检测当前用户是否已经绑定账号,如果未绑定,则可绑定账号
     如果绑定过账号,只提供密码修改功能 -->
<template>
  <el-row>
    <el-col :span="12" :offset="4">
      <el-form :model="passForm" status-icon :rules="passRule" ref="passForm" label-width="100px" class="bind-pass">
        <h2 class="text-center">
          <span v-if="!isHaveAccount">绑定账号</span>
          <span v-if="isHaveAccount">修改密码</span>
        </h2>
        <div class="pass-block">
          <!-- 输入框 -->
          <el-form-item label="绑定账号" prop="username" ref="userName">
            <el-input v-model="passForm.username" placeholder="账号只能包含字母和数字,长度不小于6位" :disabled="isHaveAccount"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="pass" ref="passWord">
            <el-input type="password" v-model="passForm.pass" auto-complete="off" placeholder="请输入您的密码,长度不小于6位"></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="checkPass" ref="checkPass">
            <el-input type="password" v-model="passForm.checkPass" auto-complete="off" placeholder="请确认密码"></el-input>
          </el-form-item>
          <!-- 按钮组 -->
          <el-form-item class="text-right">
            <el-button type="primary" @click="submitForm('passForm')" :loading="formLoading" v-if="!isHaveAccount">提交</el-button>
            <el-button type="primary" @click="changePass('passForm')" :loading="formLoading" v-if="isHaveAccount">修改密码</el-button>
            <el-button @click="resetForm('passForm')">重置</el-button>
          </el-form-item>
        </div>
      </el-form>
    </el-col>
  </el-row>
</template>
<script>
  export default {
    name: "name",
    data() {
      /**
       * 校验当前的用户名是否合法
       * @returns 
       */
      let checkusername = (rule, value, callback) => {
        if (this.isHaveAccount) {
          callback();
          return;
        }
        if (!value) {
          return callback(new Error('请输入账号'));
        } else {
          if (value.toString().length < 6) {
            callback(new Error('账号的长度不能小于6位'));
          } else if (value.toString().length > 20) {
            callback(new Error('账号的长度不能超过20位'));
          } else {
            if (!(/^[A-Za-z0-9]+$/.test(value))) {
              callback(new Error('账号只能包含字母和数字'));
            } else {
              this.$post(`/tax/userCenter/checkAccount`, {
                  account: value
                })
                .then(data => {
                  if (data.success) {
                    callback();
                  } else {
                    callback(new Error('已存在此账号,请重新输入'));
                  }
                })
                .catch(() => {
                  callback();
                });
            }
          }
        }
        // }, 1000);
      };
      /**
       * 检验当前的密码是否和合法 
       * @returns 
       */
      let validatePass = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请输入密码'));
        } else {
          if (value.length < 6) {
            callback(new Error('密码长度不能小于6位'));
          } else if (value.toString().length > 20) {
            callback(new Error('密码的长度不能超过20位'));
          } else {
            callback();
          }
        }
      };
      /**
       * 校验当前的密码是否一致 
       * @returns 
       */
      let validatePass2 = (rule, value, callback) => {
        if (value === '') {
          callback(new Error('请再次输入密码'));
        } else if (value !== this.passForm.pass) {
          callback(new Error('两次输入密码不一致!'));
        } else {
          callback();
        }
      };
      
      return {
        passForm: {
          pass: '',
          checkPass: '',
          username: ''
        },
        formLoading: false,// 保存按钮 loading状态
        isHaveAccount: false,// 是否已有账号
        /**
         * 校验规则 
         * @returns 
         */
        passRule: {
          pass: [{
            validator: validatePass,
            trigger: 'blur'
          }],
          checkPass: [{
            validator: validatePass2,
            trigger: 'blur'
          }],
          username: [{
            validator: checkusername,
            trigger: 'blur'
          }]
        }
      };
    },
    methods: {
      //页面中用到的方法
      /**
       * 表单内容提交 
       * @returns 
       */
      submitForm(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.formLoading = true;
            // alert('submit!');
            let account = this.passForm.username;
            let password = this.passForm.pass;
            this.$post(`/tax/userCenter/setAccountAndPassword`, {
              account,
              password
            }).then(data => {
              if (data.success) {
                this.$message({
                  message: '账号绑定成功',
                  type: 'success'
                });
                this.getUserAccount();
              } else {
                this.$message({
                  message: data.message || "绑定失败,请重试！",
                  type: 'error'
                });
              }
            }).catch(() => {
              this.$message({
                message: "请求服务失败,请重试！",
                type: 'error'
              });
            }).finally(() => {
              this.formLoading = false;
            })

          } else {
            console.log('error submit!!');
            return false;
          }
        });
      },
      /**
       * 修改密码操作 
       * @returns 
       */
      changePass(formName) {
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.formLoading = true;
            let password = this.passForm.pass;
            this.$post(`/tax/userCenter/updatePassword`, {
              password
            }).then(data => {
              if (data.success) {
                this.$message({
                  message: '密码修改成功！  ',
                  type: 'success'
                });
                this.getUserAccount();
              } else {
                this.$message({
                  message: data.message || "修改失败,请重试！",
                  type: 'error'
                });
              }
            }).catch(() => {
              this.$message({
                message: "请求服务失败,请重试！",
                type: 'error'
              });
            }).finally(() => {
              this.formLoading = false;
            })

          } else {
            console.log('error submit!!');
            return false;
          }
        });
      },
      /**
       * 重置表单操作 
       * @returns 
       */
      resetForm(formName) {
        if (!this.isHaveAccount) {
          this.$refs.userName.resetField();
          this.$refs.passWord.resetField();
          this.$refs.checkPass.resetField();
        } else {
          this.$refs.passWord.resetField();
          this.$refs.checkPass.resetField();
          this.$refs.userName.clearValidate();
        }
      },
      /**
       * 获取用户的账号 
       */
      getUserAccount() {
        this.resetForm();
        this.$get(`/tax/userCenter/getUserLoginAccount`).then(data => {
            if (data.success) {
              this.isHaveAccount = data.bean.havePw == 'Y';
              //此处将账号置入输入框中
              this.passForm.username = data.bean.loginAccount; 
            } else {
              this.isHaveAccount = false;
            }
          })
          .catch(() => {
            this.$message({
              message: '请求服务失败,请重试！',
              type: 'error'
            });
          })
      }
    },
    beforeRouteEnter(to, from, next) {
      next(vm => {
        // 每次进入页面之前进行的操作
        vm.getUserAccount();
        vm.resetForm('passForm')
      });
    }
  }
</script>
<style>
  .pass-block .el-form-item {
    font-weight: bold;
  }
</style>