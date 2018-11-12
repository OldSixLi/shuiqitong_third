import router from './index';
import store from "@/store";
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';

NProgress.configure({showSpinner: false});

/**
 * 跳转之前逻辑校验 
 * @returns 
 */
router.beforeEach((to, from, next) => {
  // 页面顶部进度条
  NProgress.start();
  // 根据当前的环境确定是否添加权限
  process.env.NODE_ENV === 'production' ? loginCheck(to, next) : next();
  // userAuthCheck(to, next, from)
  process.env.NODE_ENV === 'production' ? userAuthCheck(to, next, from) : next();
});

/**
 * 跳转之后逻辑 
 * @returns 
 */
router.afterEach(() => {
  NProgress.done(); // 关闭
});

// '##::::'##:'########:'########:'##::::'##::'#######::'########:::'######::
//  ###::'###: ##.....::... ##..:: ##:::: ##:'##.... ##: ##.... ##:'##... ##:
//  ####'####: ##:::::::::: ##:::: ##:::: ##: ##:::: ##: ##:::: ##: ##:::..::
//  ## ### ##: ######:::::: ##:::: #########: ##:::: ##: ##:::: ##:. ######::
//  ##. #: ##: ##...::::::: ##:::: ##.... ##: ##:::: ##: ##:::: ##::..... ##:
//  ##:.:: ##: ##:::::::::: ##:::: ##:::: ##: ##:::: ##: ##:::: ##:'##::: ##:
//  ##:::: ##: ########:::: ##:::: ##:::: ##:. #######:: ########::. ######::
// ..:::::..::........:::::..:::::..:::::..:::.......:::........::::......:::
/**
 * 用户权限校验
 *
 * @param {*} to 跳转路由
 * @param {*} next 继续方法
 * @param {*} from 跳转之前路由
 */
function userAuthCheck(to, next, from) {
  let funPoint = to.meta && to.meta.funPoint;
  if (!funPoint) {
    next();
  } else {
    console.log(from);
    let userAuth = new Set(store.getters.userAuths);
    if (!userAuth.has(funPoint)) {
      next('/noauth');
    }
    next();
  }
}

/**
 * 校验当前登录状态
 *
 * @param {*} to
 * @param {*} next
 */
function loginCheck(to, next) {
  if (to.path != '/login' && to.path != '/auth' && !store.state.isLogin) {
    var urlObj = new UrlSearch();
    if (!urlObj.code) {
      Vue.prototype.$message({
        message: '您尚未登录,请登录后再进行操作！',
        type: 'warning'
      });
      next('/login');
    } else {
      next();
    }
  } else {
    next();
  }
}

/**
 * 分隔获取各个参数
 * 根据URL地址获取其参数
 */
function UrlSearch() {
  var name, value;
  var str = location.href; //取得整个地址栏
  var num = str.indexOf("?");
  str = str.substr(num + 1);
  var arr = str.split("&"); //各个参数放到数组里
  for (var i = 0; i < arr.length; i++) {
    num = arr[i].indexOf("=");
    if (num > 0) {
      name = arr[i].substring(0, num);
      value = arr[i].substr(num + 1);
      this[name] = value;
    }
  }
}
