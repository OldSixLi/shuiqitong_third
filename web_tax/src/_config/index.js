/**
 * 当前请求的接口地址  根据生产和开发环境区分地址 
 * @returns 
 */
const devUrl = `http://192.168.106.12:8080/`;
const prodUrl = `http://dev-ws.htyfw.com.cn:18300/`;
export const serviceUrl = process.env.NODE_ENV === 'production' ? prodUrl : prodUrl;

/**
 * token信息 区分开发环境与生产环境
 */

const devToken=`tax_a35933bf537c41e1a01e663e2f0b0fcf`;
export const token = process.env.NODE_ENV === 'production' ? "" : devToken;

/**
 * 当前UserID
 */
export const wxUserId = "";

/**
 * 当前用户登录企业代码 ComID
 */
export const wxComId = "";

/**
 * 微信应用代码 
 */
export const appId = "wxa059996e5d72516b";

/**
 * 微信应用代码 
 */
export const agentId = 1000009;

/**
 * 回调地址 
 */
export const redirectUrl = "http://dev-ws.htyfw.com.cn:18303/auth";

/**
 * 扫码登录的地址 
 * @returns 
 */
// export const wxUrl = `https://open.work.weixin.qq.com/wwopen/sso/qrConnect?appid=${appId}&redirect_uri=${encodeURIComponent(redirectUrl)}&state=`;


export const wxUrl =`https://open.work.weixin.qq.com/wwopen/sso/3rd_qrConnect?appid=${appId}&redirect_uri=${encodeURIComponent(redirectUrl)}&state=&usertype=member`

/**
 * 只有管理员可启用的权限 
 * @returns 
 */
export const onlySysPoints=["system-userlist", "system-role"];

/**
 * 所有用户都拥有的权限 
 * @returns 
 */
export const baseRights=["system-account"];
