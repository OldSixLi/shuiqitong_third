/*
 * @Author:马少博 (ma.shaobo@qq.com)
 * @Date: 前台AJAX请求统一封装
 * @Last Modified by: 马少博
 * @Last Modified time:2018年8月27日13:34:11
 */
/* jshint esversion: 6 */

import {
  Message
} from 'element-ui';
import {
  serviceUrl
} from '@/_config';
import store from '@/store';
import Qs from 'qs';

/*
:'######:::'#######::'##::: ##:'########:'####::'######:::
'##... ##:'##.... ##: ###:: ##: ##.....::. ##::'##... ##::
 ##:::..:: ##:::: ##: ####: ##: ##:::::::: ##:: ##:::..:::
 ##::::::: ##:::: ##: ## ## ##: ######:::: ##:: ##::'####:
 ##::::::: ##:::: ##: ##. ####: ##...::::: ##:: ##::: ##::
 ##::: ##: ##:::: ##: ##:. ###: ##:::::::: ##:: ##::: ##::
. ######::. #######:: ##::. ##: ##:::::::'####:. ######:::
:......::::.......:::..::::..::..::::::::....:::......::::
*/
/**
 * 全局统一ajax配置 
 */
let AUTH_TOKEN = store.state.token || "6666"; //用户请求token
axios.defaults.baseURL = serviceUrl;
axios.defaults.headers.common['token'] = AUTH_TOKEN;

/*
'##::::'##::::'###::::'##::: ##:'########::'##:::::::'########:'########::
 ##:::: ##:::'## ##::: ###:: ##: ##.... ##: ##::::::: ##.....:: ##.... ##:
 ##:::: ##::'##:. ##:: ####: ##: ##:::: ##: ##::::::: ##::::::: ##:::: ##:
 #########:'##:::. ##: ## ## ##: ##:::: ##: ##::::::: ######::: ########::
 ##.... ##: #########: ##. ####: ##:::: ##: ##::::::: ##...:::: ##.. ##:::
 ##:::: ##: ##.... ##: ##:. ###: ##:::: ##: ##::::::: ##::::::: ##::. ##::
 ##:::: ##: ##:::: ##: ##::. ##: ########:: ########: ########: ##:::. ##:
..:::::..::..:::::..::..::::..::........:::........::........::..:::::..::
*/

/**
 * 错误Response的处理
 *
 * @param {*} err 
 */
function errorHandler(err) {
  if(err.response&&err.response.status){
    
  if (err.response.status == 504 || err.response.status == 404) {
    Message.error({
      message: '请求服务失败，请重试！'
    });
  } else if (err.response.status == 403) {
    Message.error({
      message: '权限不足,请联系管理员!'
    });
  } else {
    Message.error({
      message: '未知错误!'
    });
  }
}else{
  Message.error({
    message: '网络请求失败，请检查网络或联系管理员！'
  });
}
}

const successHandler = data => {
  if (data.status && data.status >= 200 && data.status <= 300) {
    let responseData = data.data;
    if (!responseData.success && responseData.message == '-1') {
      Vue.prototype.$message({
        message: '您的登陆信息已失效,请重新登陆！',
        type: 'warning',
        duration: 5000
      });
      Vue.prototype.$to("/login");
      //模拟成功数据,不影响已有的逻辑  否则不添加这个return   直接会进promise的catch中,影响较大
      return {
        success: true,
        bean: {}
      };
    } else {
      return responseData;
    }

  } else {
    return data;
  }

};

const errHandler = err => {
  errorHandler(err);
  return Promise.reject(err);
};

/**
 * 统一处理所有的response 
 * @returns 
 */
axios.interceptors.response.use(successHandler, errHandler);

/*
'##::::'##:'########:'########:'##::::'##::'#######::'########:::'######::
 ###::'###: ##.....::... ##..:: ##:::: ##:'##.... ##: ##.... ##:'##... ##:
 ####'####: ##:::::::::: ##:::: ##:::: ##: ##:::: ##: ##:::: ##: ##:::..::
 ## ### ##: ######:::::: ##:::: #########: ##:::: ##: ##:::: ##:. ######::
 ##. #: ##: ##...::::::: ##:::: ##.... ##: ##:::: ##: ##:::: ##::..... ##:
 ##:.:: ##: ##:::::::::: ##:::: ##:::: ##: ##:::: ##: ##:::: ##:'##::: ##:
 ##:::: ##: ########:::: ##:::: ##:::: ##:. #######:: ########::. ######::
..:::::..::........:::::..:::::..:::::..:::.......:::........::::......:::
*/
/**
 * get请求封装 
 * @returns 
 */
const vueGet = (url, params) => axios.get(url, {
  params
});
/**
 * post请求封装 
 * @returns 
 */
const vuePost = (url, data) => axios({
  method: "POST",
  url,
  data,
  transformRequest: [data => Qs.stringify(data)],
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'
  }
});
// 添加到prototype
Vue.prototype.$get = vueGet;
Vue.prototype.$post = vuePost;
Vue.prototype.$http = axios;
Vue.prototype.$ajax = axios;
