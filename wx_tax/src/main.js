import VConsole from 'vconsole/dist/vconsole.min.js';
import '@/util/storage.js';
import App from '@/App.vue';
import router from '@/router';
import store from '@/store/index.js';

import '@/router/config'; //路由跳转模块
import '@/global'; //element组件注册部分
import '@/util/ajax.js'; //全局封装ajax组件
import '@/util/common.js';
import '@/assets/css/index.css'; 
import '@/util/filter/time.js';

Vue.config.productionTip = false;
// let vConsole = process.env.NODE_ENV === 'production' ? null : new VConsole();
let vConsole = new VConsole();
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
)
