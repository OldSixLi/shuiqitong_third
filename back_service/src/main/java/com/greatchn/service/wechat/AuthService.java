package com.greatchn.service.wechat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.greatchn.common.dao.BaseDao;
import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.HttpUtils;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.po.Department;
import com.greatchn.po.EnterpriseInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取企业信息
 *
 * @author zy 2018-9-13
 */
@Service
public class AuthService {

    public static final String GET_SUITE_ACCESS_TOKEN_WECHAT_URL = "https://qyapi.weixin.qq.com/cgi-bin/service/get_suite_token";

    public static final String GET_PERMANENT_CODE_WECHAT_URL = "https://qyapi.weixin.qq.com/cgi-bin/service/get_permanent_code?suite_access_token=%s";

    @Resource
    BaseDao baseDao;

    @Resource
    RedisUtils redisUtils;

    /**
     * 控制台日志对象
     */
    protected final Logger log = Logger.getLogger(this.getClass());

    /**
     * 获取第三方应用服务商的access_token
     *
     * @author zy 2018-9-13
     */
    public String getSuiteAccessToken() throws IOException {
        // TODO 从缓存中获取accessToken,判断是否过期

        String suitAccessToken = (String) redisUtils.get("suitAccessToken");
        //若过期，或未过期，但到期时间与当前时间相隔3分钟以内，需要访问微信接口更新accessToken
        boolean refreshFlag = StringUtils.isBlank(suitAccessToken);

        // 判断是否更新accessToken
        if (refreshFlag) {
            // TODO 企业微信后台推送的ticket
            // 从缓存中获取suite_tiket
            String suiteTicket = (String) redisUtils.get("suiteTicket");
            if (StringUtils.isNotEmpty(suiteTicket)) {
                // 访问微信的获取接口
                Map<String, String> param = new HashMap<>();
                // 以ww或wx开头应用id（对应于旧的以tj开头的套件id）
                param.put("suite_id", Constant.PROVIDER_SUITE_ID);
                // 应用secret
                param.put("suite_secret", Constant.PROVIDER_SECRET);

                param.put("suite_ticket", suiteTicket);
                String result = HttpUtils.requestByPost(GET_SUITE_ACCESS_TOKEN_WECHAT_URL, param);
                // 处理微信接口返回的信息，将AccessToken放入缓存，缓存中记录时间
                JSONObject jsonObject = JSONObject.parseObject(result);
                // 若失败进行一次重试
                if (jsonObject == null || jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) != 0) {
                    log.info("访问微信接口获取suitAccessToken" + (jsonObject == null ? "失败" : "异常" + jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) + "," + jsonObject.getString(Constant.WECHAT_ERROR_MSG_KEY)));
                    //获取失败进行重试，在控制台日志中记录错误信息
                    suiteTicket = (String) redisUtils.get("suiteTicket");
                    param.put("suite_ticket", suiteTicket);
                    result = HttpUtils.requestByPost(GET_SUITE_ACCESS_TOKEN_WECHAT_URL, param);
                    // 处理微信接口返回的信息，将AccessToken放入缓存，缓存中记录时间
                    jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) == 0) {
                        suitAccessToken = updateSuitAccessTokenRedis(jsonObject);
                    } else {
                        log.info("访问微信接口获取suitAccessToken重试" + (jsonObject == null ? "失败" : "异常" + jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) + "," + jsonObject.getString(Constant.WECHAT_ERROR_MSG_KEY)));

                    }
                } else {
                    //获取成功，更新缓存中的信息
                    suitAccessToken = updateSuitAccessTokenRedis(jsonObject);
                }
            } else {
                // suitTicket不存在，无法获取suitAccessToken
                log.info("suitTicket不存在，无法获取suitAccessToken");
            }
        }
        // 返回accessToken
        return suitAccessToken;
    }

    /**
     * 更新缓存中suitAccessToken
     *
     * @author zy 2018-9-18
     */
    public String updateSuitAccessTokenRedis(JSONObject jsonObject) {

        // 获取返回的信息
        String suitAccessToken = jsonObject.getString("suite_access_token");
        Long expiresIn = jsonObject.getLong("expires_in");
        // 设置提前一分钟过期
        redisUtils.set("suitAccessToken", suitAccessToken, expiresIn - Constant.ENT_ACCESSTOKEN_ADVANCE_TIME);
        return suitAccessToken;
    }

    /**
     * 通过suiteAccessToken、临时授权码，获取企业永久授权码,以及其他信息
     *
     * @param authCode 临时授权码
     * @author zy 2018-9-13
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> getAuthInfoByAuthCode(String authCode) throws IOException {
        Map<String, Object> resultMap = new HashMap<>();
        // 获取suiteAccessToken
        String suiteAccessToken = getSuiteAccessToken();
        String url = String.format(GET_PERMANENT_CODE_WECHAT_URL, suiteAccessToken);
        Map<String, String> map = new HashMap<>();
        map.put("auth_code", authCode);
        Map<String, String> param = new HashMap<>();
        String result = HttpUtils.requestByPost(url, JSONObject.toJSONString(map));
        // 处理微信接口返回的信息，解析返回的授权信息，保存企业信息，管理员信息
        JSONObject jsonObject = JSONObject.parseObject(result);
        EnterpriseInfo enterpriseInfo = null;
        // 若失败进行一次重试
        if (jsonObject == null || jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) != 0) {
            // 若失败进行一次重试
            result = HttpUtils.requestByPost(url, JSONObject.toJSONString(map));
            jsonObject = JSONObject.parseObject(result);
            if (jsonObject != null && jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) == 0) {
                //获取成功，保存公司信息
                enterpriseInfo = getEnterpriseInfoFromAuthInfo(jsonObject);
                // 获取授权应用的管理员
                getWeChatAuthorziedAdminInfo(jsonObject);
            } else {
                // 获取失败信息
                String msg = jsonObject == null ? "访问微信接口失败" : "访问微信接口错误：" + jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) + "," + jsonObject.getString(Constant.WECHAT_ERROR_MSG_KEY);
                resultMap.put("msg", msg);
            }
        } else {
            //获取成功，保存公司信息
            enterpriseInfo = getEnterpriseInfoFromAuthInfo(jsonObject);
            // 获取授权应用的管理员
            getWeChatAuthorziedAdminInfo(jsonObject);
        }
        if (enterpriseInfo != null && StringUtils.isEmpty((String) resultMap.get("msg"))) {
            enterpriseInfo.setState("Y");
            baseDao.save(enterpriseInfo);
            // 保存企业的部门信息-不保存组织结构
            // saveEntDepartment(enterpriseInfo);
            redisUtils.set(RedisUtils.REDIS_PREFIX_ENT + enterpriseInfo.getCorpId(), enterpriseInfo);
            // 企业acess_token的有效期（企业微信返回的有效期为2小时，提前3分钟过期重新获取）
            Long expiresIn = jsonObject.getLong("expires_in");
            expiresIn = expiresIn - Constant.ENT_ACCESSTOKEN_ADVANCE_TIME;
            // 企业的acess_token,放入缓存
            redisUtils.set(RedisUtils.REDIS_PREFIX_ENT + enterpriseInfo.getCorpId() + "accessToken", jsonObject.getString("access_token"), expiresIn);
        }
        return resultMap;
    }

    /**
     * 解析临时授权码获取到企业基础信息
     * （1、永久授权码 2 、企业微信的公司的id 3、公司名称 4、应用id）
     *
     * @param jsonObject 企业微信授权信息接口，正确返回的JsonObject
     */
    public EnterpriseInfo getEnterpriseInfoFromAuthInfo(JSONObject jsonObject) {
        EnterpriseInfo enterpriseInfo = new EnterpriseInfo();
        // 永久授权码
        String permanentCode = jsonObject.getString("permanent_code");
        enterpriseInfo.setPermanentCode(permanentCode);
        // 企业微信的公司的id
        String corpId = null;
        // 公司名称
        String corpName = null;
        // 获取应用id
        String agentId = null;
        // 授权方公司信息
        JSONObject authCorpInfo = jsonObject.getJSONObject("auth_corp_info");
        if (authCorpInfo != null) {
            corpId = authCorpInfo.getString("corpid");
            corpName = authCorpInfo.getString("corp_name");
        }
        enterpriseInfo.setCorpId(corpId);
        // 数据库中不允许为null,将null替换为空串
        enterpriseInfo.setEntName(corpName == null ? "" : corpName);
        // 企业微信的授权应用的id
        JSONObject authInfo = jsonObject.getJSONObject("auth_info");
        //获取具体的agent信息，应用信息为数组（单应用，获取第一个）
        JSONArray agents = authInfo.getJSONArray("agent");
        if (agents != null && agents.size() > 0) {
            JSONObject agentInfo = agents.getJSONObject(0);
            agentId = agentInfo.getString("agentid");
        }
        enterpriseInfo.setAgentId(agentId);
        return enterpriseInfo;
    }

    /**
     * 获取添加第三方应用的管理员的信息
     * （授权管理员的userid、授权管理员的name）
     *
     * */
    public Map<String,Object> getWeChatAuthorziedAdminInfo(JSONObject jsonObject){

        JSONObject object=jsonObject.getJSONObject("auth_user_info");

        String userId=object.getString("userid");

        String name=object.getString("name");

        Map<String,Object> map=new HashMap<>(2);
        map.put("userId",userId);
        map.put("name",name);
        return map;
    }

    /**
     * 获取企业AccessToken
     *
     * @author zy 2018-9-17
     */
    public Map<String, String> getAccessToken(String corpId, String permanentCode) throws IOException {
        // accessToken在缓存中的key
        String accessTokenKey = RedisUtils.REDIS_PREFIX_ENT + corpId + "accessToken";
        Map<String, String> map = new HashMap<>();
        String accessToken;
        // 获取该企业的AccessToken,校验是否过期(放入缓存时设置时间提前三分钟)
        accessToken = (String) redisUtils.get(accessTokenKey);
        // 若已过期或企业AccessToken信息不存在，直接调用微信接口获取，并放入缓存
        if (StringUtils.isBlank(accessToken)) {
            // 访问微信接口
            String url = "https://qyapi.weixin.qq.com/cgi-bin/service/get_corp_token?suite_access_token=%s";
            url = String.format(url, getSuiteAccessToken());
            Map<String, Object> parameter = new HashMap<>();
            parameter.put("auth_corpid", corpId);
            parameter.put("permanent_code", permanentCode);
            String result = HttpUtils.requestByPost(url, JSON.toJSONString(parameter));
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject == null) {
                map.put("msg", "访问企业微信接口失败！");
            } else if (jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) != 0) {
                // 返回微信的错误信息
                map.put("msg", "访问企业微信错误：" + jsonObject.getString(Constant.WECHAT_ERROR_CODE_KEY) + "," + jsonObject.getString(Constant.WECHAT_ERROR_MSG_KEY));
            } else {
                // 解析企业微信接口返回信息
                accessToken = jsonObject.getString("access_token");
                map.put("accessToken", accessToken);
                Long expiresIn = jsonObject.getLong("expires_in");
                // 设置该accessToken提前3分钟过期
                expiresIn = expiresIn - Constant.ENT_TOKEN_TIME_OUT;
                // 更新缓存
                redisUtils.set(accessTokenKey, accessToken, expiresIn);
            }
        } else {
            map.put("accessToken", accessToken);
        }
        // 若未过期直接返回
        return map;
    }

    /**
     * 获取企业的部门，并进行保存
     *
     * @param enterpriseInfo 企业信息对象
     * @author zy 2018-9-17
     */
    public String saveEntDepartment(EnterpriseInfo enterpriseInfo) throws IOException, URISyntaxException {
        String msgKey = "msg";
        String msg = null;
        if (enterpriseInfo != null) {
            // 获取企业的AccessToken
            Map<String, String> map = getAccessToken(enterpriseInfo.getCorpId(), enterpriseInfo.getPermanentCode());
            if (StringUtils.isNotEmpty(map.get(msgKey))) {
                msg = map.get(msgKey);
            } else {
                String entAccessToken = map.get("accessToken");
                String url = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=%s&id=%s";
                url = String.format(url, entAccessToken, "");
                String result = HttpUtils.requstByGet(url, null);
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) == 0) {
                    updateDepartment(enterpriseInfo.getCorpId(), jsonObject);
                } else if (jsonObject == null) {
                    // 重试一次
                    result = HttpUtils.requstByGet(url, null);
                    jsonObject = JSONObject.parseObject(result);
                    if (jsonObject != null && jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) == 0) {
                        updateDepartment(enterpriseInfo.getCorpId(), jsonObject);
                    } else {
                        // 访问企业微信失败，返回错误信息
                        msg = jsonObject == null ? "访问微信接口失败" : "访问微信接口错误：" + jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) + "," + jsonObject.getString(Constant.WECHAT_ERROR_MSG_KEY);
                    }
                } else {
                    // 访问企业微信失败，返回错误信息
                    msg = "访问微信接口错误：" + jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) + "," + jsonObject.getString(Constant.WECHAT_ERROR_MSG_KEY);
                }
            }
        } else {
            msg = "企业信息不存在";
        }
        return msg;
    }

    /**
     * 将微信接口返回的部门信息更新到数据库
     */
    private void updateDepartment(String corpId, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("department");
        if (jsonArray != null && jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                Department department = new Department();
                department.setEnterpriseId(corpId);
                department.setDepartmentId(object.getString("id"));
                department.setName(object.getString("name"));
                department.setParentId(object.getInteger("parentid"));
                department.setOrder(object.getInteger("order"));
                baseDao.save(department);
            }
        }
    }

    /**
     * 获取的信息
     *         　
     * */
    public void getText(){
        //都连webSocket会不会浪费资源


    }
}
