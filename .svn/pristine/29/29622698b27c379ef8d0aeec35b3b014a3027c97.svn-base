<template>
  <div class="login-container">

    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" auto-complete="on"
      label-position="left">

      <div class="title-container">
        <h3 class="title">税企通管理平台登录</h3>
        <!-- <lang-select class="set-language" /> -->
      </div>
      <el-form-item prop="username" style="line-height:30px;">
        <span class="svg-container svg-container_login">
          <img src="@/assets/img/login/account.png" alt="">
        </span>
          <el-input  class="username-input" v-model="loginForm.username" placeholder="请输入用户名" name="username" type="text" auto-complete="off" />
      </el-form-item>
      <el-form-item prop="password">
        <span class="svg-container">
          <img src="@/assets/img/login/pass.png" alt="">
        </span>
          <el-input :type="passwordType" v-model="loginForm.password" placeholder="请输入密码" name="password" auto-complete="on"
            @keyup.enter.native="handleLogin" />
          <span class="show-pwd" @click="showPwd">
            <!-- <svg-icon icon-class="eye" /> -->
            <img src="@/assets/img/login/pass-close.png" title="隐藏密码" class="showpass-icon" v-show="passwordType">
            <img src="@/assets/img/login/pass-see.png" title="显示密码" class="showpass-icon" v-show="!passwordType">
          </span>
      </el-form-item>
      <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;" @click.native.prevent="handleLogin">登录</el-button>
      <el-button class="thirdparty-button" type="primary" @click="toScan">扫描二维码登录</el-button>
    </el-form>
  </div>
</template>

<script>
  import { wxUrl } from '@/_config'
  export default {
    name: 'Login',
    data() {
      const validateUsername = (rule, value, callback) => {
        if (!value) {
          callback(new Error('请输入账号'))
        } else {
          if (value.length < 6) {
            callback(new Error('请输入正确的账号'))
          } else {
            callback();
          }
        }
      }
      const validatePassword = (rule, value, callback) => {
        if (!value) {
          callback(new Error('请输入密码'))
        } else {
          if (value.length < 6) {
            callback(new Error('请输入正确的密码'))
          } else {
            callback()
          }
        }
      }
      return {
        loginForm: {
          username: '',
          password: ''
        },
        loginRules: {
          username: [{
            required: true,
            trigger: 'focus',
            validator: validateUsername
          }],
          password: [{
            required: true,
            trigger: 'focus',
            validator: validatePassword
          }]
        },
        passwordType: 'password',
        loading: false,
        showDialog: false
      }
    },
    methods: {
      showPwd() {
        if (this.passwordType === 'password') {
          this.passwordType = ''
        } else {
          this.passwordType = 'password'
        }
      },
      /**
       * 错误提示 
       * @returns 
       */
      errTip(err) {
        this.$message.error(err);
      },
      /**
       * 登录请求处理逻辑 
       * @returns 
       */
      handleLogin() {
        this.$refs.loginForm.validate(valid => {
          if (valid) {
            let account = this.loginForm.username;
            let password = this.loginForm.password;
            this.loading = true;
            this.$post(`/tax/login/loginByPassword`, {
                account,
                password
              })
              .then(data => {
                this.loading = false
                if (data.success) {
                  let flag = data.bean.flag;
                  switch (flag) {
                    case "0":
                      this.errTip("登录失败：账号不存在！");
                      break;
                    case "1":
                      this.errTip("登录失败：您的密码输入错误！");
                      break;
                    case "2":
                      this.errTip("登录失败：用户信息不存在！");
                      break;
                    case '3':
                      this.errTip("登录失败：您所属的企业未通过审核,无法登录本应用！");
                      break;
                    case '4':
                      this.errTip("登录失败：您所属的企业信息审核中,暂时无法登录本应用！");
                      break;
                    case '5':
                      let token = data.bean.token;
                      this.$store.dispatch('logIn', data.bean);
                      //TODO 后期添加根据参数跳转的功能
                      this.$to("/");
                      break;
                    case '6':
                      this.errTip("登录失败：您所属的企业信息审核未通过,暂时无法登录本应用！");
                      break;
                    case "7":
                      this.errTip("登录失败：获取用户信息失败！");
                      break;
                    case "8":
                      this.errTip("登录失败：您没有权限登录此应用！");
                      break;
                    default:
                      this.errTip("请求服务失败,请重试！");
                      break;
                  }
                } else {

                }
              }).catch(
                () => {
                  this.loading = false
                }
              );
          } else {
            console.log('error submit!!')
            return false


          }
        })
      },
      afterQRScan() {},
      toScan() {
        window.location.href = wxUrl;
      }
    }
  }
</script>

 
<style rel="stylesheet/scss" lang="scss">
  /* 修复input 背景不协调 和光标变色 */
  /* Detail see https://github.com/PanJiaChen/vue-element-admin/pull/927 */

  $bg:#283443;
  $light_gray:#eee;
  $cursor: #fff;

  @supports (-webkit-mask: none) and (not (cater-color: $cursor)) {
    .login-container .el-input input {
      color: $cursor;

      &::first-line {
        color: $light_gray;
      }
    }
  }

  /* reset element-ui css */

  .login-container {
    .el-input {
      display: inline-block;
      height: 47px;
      width: 85%;

      input {
        background: transparent;
        border: 0;
        -webkit-appearance: none;
        border-radius: 0;
        padding: 12px 5px 12px 15px;
        color: $light_gray;
        height: 47px;
        caret-color: $cursor;

        &:-webkit-autofill {
          -webkit-box-shadow: 0 0 0 1000px $bg inset !important;
          -webkit-text-fill-color: $cursor !important;
        }
      }
    }

    .el-form-item {
      border: 1px solid rgba(255, 255, 255, 0.1);
      background: rgba(0, 0, 0, 0.1);
      border-radius: 5px;
      color: #454545;
    }
  }
</style>

<style rel="stylesheet/scss" lang="scss" scoped>
  /* $bg:#2d3a4b; */
  $bg:#2d3a4b;
  $dark_gray:#889aa4;
  $light_gray:#eee;

  .login-container {
    position: fixed;
    height: 100%;
    width: 100%;
    background-color: $bg;

    .login-form {
      position: absolute;
      left: 0;
      right: 0;
      width: 520px;
      padding: 35px 35px 15px 35px;
      margin: 120px auto;
    }

    .tips {
      font-size: 14px;
      color: #fff;
      margin-bottom: 10px;

      span {
        &:first-of-type {
          margin-right: 16px;
        }
      }
    }

    .svg-container {
      padding: 6px 5px 6px 15px;
      color: $dark_gray;
      vertical-align: middle;
      width: 30px;
      display: inline-block;

      &_login {
        font-size: 20px;
      }

      img {
        width: 25px;
        height: 25px;
        margin-top: 3px;
      }
    }

    .title-container {
      position: relative;

      .title {
        font-size: 26px;
        color: $light_gray;
        margin: 0 auto 40px auto;
        text-align: center;
        font-weight: bold;
      }

      .set-language {
        color: #fff;
        position: absolute;
        top: 5px;
        right: 0;
      }
    }

    .show-pwd {
      position: absolute;
      right: 10px;
      top: 7px;
      font-size: 16px;
      color: $dark_gray;
      cursor: pointer;
      user-select: none;
    }

    .thirdparty-button {
      position: absolute;
      right: 35px;
      bottom: -15px;
    }
  }
</style>

<style>
  .el-form-item__content {
    line-height: 25px;
  }
  .showpass-icon{
    width: 20px;
    height: 20px;
    margin-top: 7px;
    margin-right: 10px;
  }

  /* .username-input.el-input .el-input__inner::-webkit-input-placeholder{
    color:red;
    font-weight: bold;
  } */

  /* .username-input.el-input .el-input__inner::-ms-input-placeholder {
    color: rgba(88, 61, 61, 0.527);font-weight: bold;
}
.username-input.el-input .el-input__inner::placeholder {
  color: rgba(250, 0, 0, 0.527);font-weight: bold;} */

/* .username-input.el-input .el-input__inner{
  height: 150px;
} */

</style>