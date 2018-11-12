/*
 * 路由注册模块
 * @Author:马少博 (ma.shaobo@qq.com)
 * @Date: 2018年8月31日13:49:38
 * @Last Modified by: 马少博
 * @Last Modified time:2018年8月31日13:49:40
 */
/* jshint esversion: 6 */
import Router from 'vue-router';
//基础组件
import Index from '@/page/Index.vue';
import Login from '@/page/Login.vue';
import Hello from '@/components/HelloWorld';
import Lost from '@/page/404.vue';
//权限路由
import agency from './module/agency.js';
import company from './module/company.js';
import mycompany from './module/mycompany.js';
import question from './module/question.js';
import message from './module/message.js';
import quan from './module/quan.js';
import system from './module/system.js';

Vue.use(Router);

let indexNum = 0;
let TOTAL_FLAT_ROUTES = []; //扁平化数据结构
let FUN_POINT = [];

/**
 * 此处注册所有菜单文件相关路由
 */
export const allMenu = [
  // NOTE:路由的注册顺序会影响到菜单的展示顺序,所以要顺延添加路由注册文件
  ...system,
  // ...company,
  ...mycompany,
  ...question,
  ...message,
  ...quan,
  // ...agency,
];

/**
 * 默认路由对象 
 * @returns 
 */
export default new Router({
  mode: "history",
  routes: [{
      path: '/',
      name: 'Index',
      component: Index,
      children: [
        {
          path: '/',
          name: '',
          component: Hello
        },
        ...allMenu
      ]
    },
    {
      path: '/login',
      name: 'Login',
      component: Login,
    },
     {
      path: '/auth',
      name: 'Auth',
      component: () => import('@/page/auth.vue'),
    }, {
      path: '/noauth',
      name: 'NoAuth',
      component: () => import('@/page/NoAuth.vue'),
    },
    {
      path: '*',
      name: '404',
      component: Lost,
    }
  ]
});

/**
 * 路由树状结构 
 */
export const routeTree = getMenuTree();


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
 * 功能点重复判断 
 * @returns 
 */
if (FUN_POINT.length != [...new Set([...FUN_POINT])].length) {
  const filterNonUniques = arr => arr.filter(i => arr.indexOf(i) != arr.lastIndexOf(i));
  let multipleValue = filterNonUniques(FUN_POINT);
  if (multipleValue && multipleValue.length != 0) {
    // 错误提示 
    errTip([...new Set(multipleValue)]);
  }
}
/**
 * 错误提示
 *
 * @param {*} multipleValue 重复功能点
 */
function errTip(multipleValue) {
  let errStr = `当前路由模块中功能点 ${multipleValue.join(' , ')} 被多个路由使用，请修改为唯一值！`;
  console.error("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
  console.error(errStr);
  console.error("■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
  Vue.prototype.$message({
    message: errStr,
    type: 'error',
    duration: 0
  });
}

/**
 * 路由转化为树状结构 --添加总路由入口
 *
 * @returns
 */
function getMenuTree() {
  TOTAL_FLAT_ROUTES = [];
  return {
    id: "totalRoutes",
    name: "用户权限",
    children: menu2Tree(allMenu)
  };
}

/**
 * 当前的路由children转化为树状结构
 *
 * @param {*} allMenu 系统的所有路由
 * @returns 转化为Tree返回
 */
function menu2Tree(allMenu) {
  let treeArr = [];
  let menuArr = allMenu;
  menuArr.forEach(route => {
    let id = (route.meta && route.meta.funPoint) || `fun-${indexNum++}`,
      name = route.name || (route.meta && route.meta.title);
    let obj = {
      id,
      name
    };
    TOTAL_FLAT_ROUTES.push({
      id,
      name
    });
    FUN_POINT.push(id);
    if (route.children) {
      obj.children = menu2Tree(route.children)
    }
    treeArr.push(obj)
  });
  return treeArr;
}

