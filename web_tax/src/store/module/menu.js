/*
 * 个人权限设置Vuex模块
 * @Author:马少博 (ma.shaobo@qq.com)
 * @Date: 2018年9月3日16:55:28
 * @Last Modified by: 马少博
 * @Last Modified time:2018年9月3日16:55:28
 */
/* jshint esversion: 6 */
"use strict"
// const vue =require('vue')

import { allMenu } from '@/router/index';
import { baseRights } from '@/_config';

let storage = window.sessionStorage;
let rights = (storage.getItem('auth') && storage.getItem('auth').split(',')) || baseRights;

const menu = {
  state: {
    totalRouter: allMenu,
    auth: rights,
    routers: filterAsyncRouter(JSON.parse(JSON.stringify(allMenu)), rights),
  },
  mutations: {
    UPDATE_ROUTERS: (state, userAuths) => {
      let TOTAL_MENU = JSON.parse(JSON.stringify(allMenu));
      state.auth = userAuths;
      storage.setItem('auth', userAuths.join(','))
      state.routers = filterAsyncRouter(TOTAL_MENU, userAuths);
    }
  },
  actions: {
    getAppMenu({ commit }, roles) {
      return new Promise(resolve => {
        commit('UPDATE_ROUTERS', roles);
        resolve();
      })
    }
  }
};

// '##::::'##:'########:'########:'##::::'##::'#######::'########:::'######::
//  ###::'###: ##.....::... ##..:: ##:::: ##:'##.... ##: ##.... ##:'##... ##:
//  ####'####: ##:::::::::: ##:::: ##:::: ##: ##:::: ##: ##:::: ##: ##:::..::
//  ## ### ##: ######:::::: ##:::: #########: ##:::: ##: ##:::: ##:. ######::
//  ##. #: ##: ##...::::::: ##:::: ##.... ##: ##:::: ##: ##:::: ##::..... ##:
//  ##:.:: ##: ##:::::::::: ##:::: ##:::: ##: ##:::: ##: ##:::: ##:'##::: ##:
//  ##:::: ##: ########:::: ##:::: ##:::: ##:. #######:: ########::. ######::
// ..:::::..::........:::::..:::::..:::::..:::.......:::........::::......:::

/**
 * 递归过滤异步路由表，返回符合用户角色权限的路由表
 * @param routerList 当前应用中所有路由
 * @param authList 当前用户的功能点
 */
function filterAsyncRouter(routerList, authList) {
  if (authList.length == 0) {
    return [];
  }
  let accessedRouters = routerList.filter(route => {
    if (hasPermission(authList, route)) {
      if (route.children && route.children.length) {
        route.children = filterAsyncRouter(route.children, authList);
      }
      return true;
    }
    return false;
  });
  return accessedRouters;
}


/**
 * 通过meta.point判断是否与当前用户权限匹配
 * @param authList 用户权限集合
 * @param route 路由
 */
function hasPermission(authList, route) {
  return authList.some(role => new Set(getRouteAllPoints(route)).has(role));
}

/**
 * 获取当前路由及其子路由所有的功能点
 *
 * @param {*} route
 * @param {*} [arr=[]] 功能点list
 * @returns
 */
function getRouteAllPoints(route, arr = []) {
  if (!route.children) {
    arr.push(route.meta && route.meta.funPoint);
    return arr;
  }
  arr.push(route.meta && route.meta.funPoint);
  route.children.forEach(child => getRouteAllPoints(child, arr));
  return arr;
}

export default menu;
