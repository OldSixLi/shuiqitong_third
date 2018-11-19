<template>
  <!-- <p>进入首页中</p> -->
</template>
<script>
  // 引入
  import {
    mapGetters
  } from 'vuex';
  import {
    redirectUrl,
    appId,
    wxAuthUrl
  } from '@/_config/index.js';
  import {
    Indicator
  } from 'mint-ui';
  let storage = window.localStorage;
  /**
   * 函数执行逻辑
   * @returns 
   */
  export default {
    name: "First",
    data() {
      return {
        auth: redirectUrl,
        path: ""
      }
    },
    computed: {
      ...mapGetters([
        'companyId',
        'comUserId'
      ])
    },
    mounted: function () {
      Indicator.open();
    },
    methods: {
      toSetAuth() {
        window.location.href = wxAuthUrl + `&state=${to.query.path}`;
      }
    },
    created() {
      let param = this.$param(this);
    },
    beforeRouteEnter(to, from, next) {
      next(vm => {
        try {
          storage.setItem('isTokenFail', '0')
          if (to.query && to.query.path) {
            vm.path = to.query.path;
            console.log("■■■■■■■■■■■■to.query.path■■■■■■■■■■■■■");
            console.log(to.query.path);
            console.log("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
            window.location.href = wxAuthUrl + `&state=${to.query.path}`;
          } else {
            window.location.href = wxAuthUrl;
          }
        } catch (error) {
          console.log(error);
        }
      });
    }
  }

</script>
