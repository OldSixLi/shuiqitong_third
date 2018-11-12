<template>
  <div class="hello">
    <el-row style="margin-top: 10px;">
      <el-col :span="12" :offset="6">
        <h1 class="text-center">对不起,您没有权限访问此页面</h1>
        <el-button @click="back()">
          返回
        </el-button>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import axios from 'axios';
  import { Loading } from 'element-ui';
  import { wxUrl } from '@/_config';

  export default {
    name: 'Auth',
    data() {
      return {
        code: ""
      }
    },
    methods: {
      /**
       * 返回原来的页面 
       * @returns 
       */
      back() {
        this.$to('/');
      },
      /**
       * 分隔获取各个参数
       * 根据URL地址获取其参数
       */
      urlSearch() {
        var name, value;
        var str = location.href; //取得整个地址栏
        var num = str.indexOf("?");
        str = str.substr(num + 1);
        let obj = {};
        var arr = str.split("&"); //各个参数放到数组里
        for (var i = 0; i < arr.length; i++) {
          num = arr[i].indexOf("=");
          if (num > 0) {
            name = arr[i].substring(0, num);
            value = arr[i].substr(num + 1);
            obj[name] = value;
          }
        }
        return obj;
      }
    },
    // beforeRouteLeave(to, from, next) {
    //   next();
    // }
  }
</script>