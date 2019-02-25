

import '@/util/session_share.js';
import Vue from 'vue';
import App from './App.vue';
import router from './router';
import component from 'element-ui';
import store from '@/store/index.js';

import '@/router/config';
import '@/global'; //element组件注册部分
import '@/util/ajax.js';
import '@/util/common.js';

Vue.config.productionTip = false;

if(process.env.NODE_ENV === 'production'){
  console.log=()=>{}
};

Vue.use('element-ui');

new Vue(
  //注册Vue实例
  {
    el: '#app',
    router,
    store,
    components: {
      App
    },
    template: '<App/>'
  }
);

Vue.directive('areafocus', function (el, option) {
  el.querySelector('textarea').focus()
});