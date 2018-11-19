/*
 * 开发环境中使用到的固定配置(变量)
 * 可脱离微信环境,方便在PC端直接测试
 * @Author:马少博 (ma.shaobo@qq.com)
 * @Date: 2018年9月26日09:13:32
 * @Last Modified by: 马少博
 * @Last Modified time:2018年9月26日09:13:32
 */
/* jshint esversion: 6 */
"use strict"
/*
'########::'########:'##::::'##:
 ##.... ##: ##.....:: ##:::: ##:
 ##:::: ##: ##::::::: ##:::: ##:
 ##:::: ##: ######::: ##:::: ##:
 ##:::: ##: ##...::::. ##:: ##::
 ##:::: ##: ##::::::::. ## ##:::
 ########:: ########:::. ###::::
........:::........:::::...:::::
开发环境
*/
// token信息
export const token = "tax_125274d601ac46e3b8245b915bbfdab3";
//企业微信中UserID
export const wxUserId = "";
//企业微信中公司ID
export const wxComId = "wxa059996e5d72516b";

/*
'########::'########:::'#######::'########::
 ##.... ##: ##.... ##:'##.... ##: ##.... ##:
 ##:::: ##: ##:::: ##: ##:::: ##: ##:::: ##:
 ########:: ########:: ##:::: ##: ##:::: ##:
 ##.....::: ##.. ##::: ##:::: ##: ##:::: ##:
 ##:::::::: ##::. ##:: ##:::: ##: ##:::: ##:
 ##:::::::: ##:::. ##:. #######:: ########::
..:::::::::..:::::..:::.......:::........:::
正式环境
*/
// token信息
// export const token = "";
// //企业微信中UserID
// export const wxUserId = "";
// //企业微信中公司ID
// export const wxComId = "";

/**
 * 接口地址 
 * @returns 
 */
const devUrl = `http://192.168.106.12:8080/`; //开发环境
const prodUrl = `http://dev-ws.htyfw.com.cn:18300/`; //正式环境
//根据生产环境区分服务器地址
export const BASE_URL = process.env.NODE_ENV !== 'production' ? devUrl : prodUrl;

/**
 * 微信授权后回调地址
 * @returns 
 */
export const redirectUrl = "http://dev-ws.htyfw.com.cn:18302/auth";

/**
 * 当前企业wxId 
 * @returns 
 */
export const coprId = "wxa059996e5d72516b";

/**
 * 当前应用appId 
 * @returns 
 */
export const appId = "wxd8ed9a1dc046aaf8";

/**
 * 微信端获取详情地址
 * @returns 
 */
export const wxAuthUrl = `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${appId}&redirect_uri=${encodeURIComponent(redirectUrl)}&response_type=code&scope=snsapi_privateinfo`;

//系统中所有用户均可访问的功能
export const baseRights = [
  "message",
  "message-list",
  "message-detail",
  "question",
  "question-detail",
  "question-info"
]