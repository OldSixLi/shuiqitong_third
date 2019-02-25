package com.greatchn.service.wechat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.greatchn.common.dao.BaseDao;
import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.HttpUtils;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.po.EnterpriseInfo;
import com.greatchn.po.TaxInfo;
import com.greatchn.service.ent.EnterpriseService;
import com.greatchn.service.tax.TaxService;
import com.sun.tools.javac.comp.Enter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取企业信息
 *
 * @author zy 2018-9-13
 */
@Service
public class AuthService {

    @Resource
    BaseDao baseDao;

    @Resource
    RedisUtils redisUtils;

    @Resource
    AccessTokenService accessTokenService;

    @Resource
    TaxService taxService;

    @Resource
    EnterpriseService enterpriseService;

    /**
     * 控制台日志对象
     */
    protected final Logger logger = Logger.getLogger(this.getClass());

    /**
     * 获取永久授权码链接
     */
    public static final String GET_PERMANENT_CODE_WECHAT_URL = "https://qyapi.weixin.qq.com/cgi-bin/service/get_permanent_code?suite_access_token=%s";


    /**
     * 通过suiteAccessToken、临时授权码，获取企业永久授权码,以及其他信息
     *
     * @param authCode 临时授权码
     * @author zy 2018-9-13
     */
    @Transactional(rollbackFor = Exception.class)
    public String getAuthInfoByAuthCode(String authCode, String type) throws IOException {
        type = StringUtils.equals(Constant.GET_INFO_TYPE_TAX, type) ? Constant.GET_INFO_TYPE_TAX : Constant.GET_INFO_TYPE_ENT;
        String msg = null;
        // 获取suiteAccessToken
        String suiteAccessToken = StringUtils.equals(Constant.GET_INFO_TYPE_TAX, type) ? accessTokenService.getSuiteAccessToken(Constant.TAX_SUITE_ID, Constant.TAX_SUITE_SECRET) : accessTokenService.getSuiteAccessToken(Constant.ENTERPRISE_SUITE_ID, Constant.ENTERPRISE_SUITE_SECRET);
        String url = String.format(GET_PERMANENT_CODE_WECHAT_URL, suiteAccessToken);
        Map<String, String> map = new HashMap<>();
        map.put("auth_code", authCode);
        Map<String, String> param = new HashMap<>();
        String result = HttpUtils.requestByPost(url, JSONObject.toJSONString(map));
        // 处理微信接口返回的信息，解析返回的授权信息，保存企业信息，管理员信息
        JSONObject jsonObject = JSONObject.parseObject(result);
        EnterpriseInfo enterpriseInfo = null;
        TaxInfo taxInfo = null;
        // 判断是否获取成功
        if (jsonObject == null || jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) != 0) {
            // 若失败进行一次重试
            result = HttpUtils.requestByPost(url, JSONObject.toJSONString(map));
            jsonObject = JSONObject.parseObject(result);
            if (jsonObject != null && jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) == 0) {
                if (!StringUtils.equals(Constant.GET_INFO_TYPE_TAX, type)) {
                    //获取成功，保存公司信息
                    enterpriseInfo = getEnterpriseInfoFromAuthInfo(jsonObject);
                } else {
                    // 获取税务分局信息
                    taxInfo = getTaxInfoFromAuthInfo(jsonObject);
                }
            } else {
                // 获取失败信息
                msg = jsonObject == null ? "访问微信接口获取企业基础信息失败" : "访问微信接口获取企业基础信息错误：" + jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) + "," + jsonObject.getString(Constant.WECHAT_ERROR_MSG_KEY);
            }
        } else {
            if (!StringUtils.equals(Constant.GET_INFO_TYPE_TAX, type)) {
                //获取成功，保存公司信息
                enterpriseInfo = getEnterpriseInfoFromAuthInfo(jsonObject);
            } else {
                // 获取税务分局信息
                taxInfo = getTaxInfoFromAuthInfo(jsonObject);
            }
        }
        if (!StringUtils.equals(Constant.GET_INFO_TYPE_TAX, type)) {
            //获取成功，保存公司信息
            if (enterpriseInfo != null && StringUtils.isEmpty(msg)) {
                // 保存或更新企业信息
                enterpriseInfo.setState("Y");
                saveOrUpdateEntOrTax(enterpriseInfo.getCorpId(), Constant.GET_INFO_TYPE_ENT, enterpriseInfo, null);
                // 将企业信息放入缓存
                redisUtils.set(RedisUtils.REDIS_PREFIX_ENT + enterpriseInfo.getCorpId(), enterpriseInfo);
                // 企业accessToken的有效期（企业微信返回的有效期为2小时，提前3分钟过期重新获取）
                Long expiresIn = jsonObject.getLong("expires_in");
                expiresIn = expiresIn - Constant.ACCESS_TOKEN_ADVANCE_TIME;
                // 企业的accessToken,放入缓存
                redisUtils.set(RedisUtils.REDIS_PREFIX_ENT + enterpriseInfo.getCorpId() + "accessToken", jsonObject.getString("access_token"), expiresIn);
            }
        } else {
            // 获取成功保存税务分局信息
            if (taxInfo != null && StringUtils.isEmpty(msg)) {
                // 保存或更新税务分局信息
                taxInfo.setState("Y");
                saveOrUpdateEntOrTax(taxInfo.getCorpId(), Constant.GET_INFO_TYPE_TAX, null, taxInfo);
                // 将税务分局信息放入缓存
                redisUtils.set(RedisUtils.REDIS_PREFIX_TAX + taxInfo.getCorpId(), taxInfo);
                // 税务分局acess_token的有效期（企业微信返回的有效期为2小时，提前3分钟过期重新获取）
                Long expiresIn = jsonObject.getLong("expires_in");
                expiresIn = expiresIn - Constant.ACCESS_TOKEN_ADVANCE_TIME;
                // 企业的acess_token,放入缓存
                redisUtils.set(RedisUtils.REDIS_PREFIX_TAX + taxInfo.getCorpId() + "accessToken", jsonObject.getString("access_token"), expiresIn);
            }
        }

        return msg;
    }

    /**
     * 根据corpId获取所有有效的企业/税务局信息，并作废
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelAuthUpdateEntOrTax(String corpId, String type) {
        String sql;
        // 判断需要作废信息的类型
        if (StringUtils.equals(Constant.GET_INFO_TYPE_ENT, type)) {
            // 查询所有该税务局的有效信息，并作废
            sql = "update tax_info set STATE='N' where CORP_ID=? and STATE='Y'";
        } else {
            // 查询所有该企业的有效信息，并作废
            sql = "update enterprise_info set STATE='N' where CORP_ID=? and STATE='Y'";
        }
        baseDao.executeSQL(sql, corpId);
    }

    /**
     * 保存或更新用户的授权信息（agentId,permanentCode）
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateEntOrTax(String corpId, String type, EnterpriseInfo enterpriseInfo, TaxInfo taxInfo) {
        // 判断需要作废信息的类型
        if (StringUtils.equals(Constant.GET_INFO_TYPE_TAX, type)) {
            // 查询所有该税务局的有效信息
            TaxInfo oldTaxInfo = taxService.findTaxInfoByCorpId(corpId, null);
            if (oldTaxInfo != null) {
                // 存在且有效的已授权的信息
                oldTaxInfo.setPermanentCode(taxInfo.getPermanentCode());
                oldTaxInfo.setAgentId(taxInfo.getAgentId());
                baseDao.update(oldTaxInfo);
                logger.info("更新税务分局信息");
            } else {
                baseDao.save(taxInfo);
                logger.info("保存税务分局信息");
            }
        } else {
            // 查询所有该企业的有效信息
            EnterpriseInfo oldEnterpriseInfo = enterpriseService.findEnterpriseByCorpId(corpId, null);
            if (oldEnterpriseInfo != null) {
                oldEnterpriseInfo.setAgentId(enterpriseInfo.getAgentId());
                oldEnterpriseInfo.setPermanentCode(enterpriseInfo.getPermanentCode());
                baseDao.update(oldEnterpriseInfo);
            } else {
                baseDao.save(enterpriseInfo);
            }
        }
    }

    /**
     * 解析临时授权码获取到税务分局基础信息
     * （1、永久授权码 2 、企业微信的公司的id 3、公司名称 4、应用id）
     *
     * @param jsonObject 企业微信授权信息接口，正确返回的JsonObject
     */
    public TaxInfo getTaxInfoFromAuthInfo(JSONObject jsonObject) {
        TaxInfo taxInfo = new TaxInfo();
        // 永久授权码
        String permanentCode = jsonObject.getString("permanent_code");
        taxInfo.setPermanentCode(permanentCode);
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
        taxInfo.setCorpId(corpId);
        // 数据库中不允许为null,将null替换为空串
        taxInfo.setName(corpName == null ? "" : corpName);
        // 企业微信的授权应用的id
        JSONObject authInfo = jsonObject.getJSONObject("auth_info");
        //获取具体的agent信息，应用信息为数组（单应用，获取第一个）
        JSONArray agents = authInfo.getJSONArray("agent");
        if (agents != null && agents.size() > 0) {
            JSONObject agentInfo = agents.getJSONObject(0);
            agentId = agentInfo.getString("agentid");
        }
        taxInfo.setAgentId(agentId);
        // TODO 获取授权的管理员信息，将管理员的手机号默认为企业的联系人
        return taxInfo;
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
        // TODO 获取授权的管理员信息，将管理员的手机号默认为企业的联系人
        return enterpriseInfo;
    }

    /**
     * 获取添加第三方应用的管理员的信息
     * （授权管理员的userid、授权管理员的name）
     */
    public Map<String, Object> getWeChatAuthorziedAdminInfo(JSONObject jsonObject) {
        JSONObject object = jsonObject.getJSONObject("auth_user_info");
        String userId = object.getString("userid");
        String name = object.getString("name");
        Map<String, Object> map = new HashMap<>(2);
        map.put("userId", userId);
        map.put("name", name);
        return map;
    }

}
