package com.greatchn.service.wechat;

import com.alibaba.fastjson.JSONObject;
import com.greatchn.common.dao.BaseDao;
import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.HttpUtils;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.controller.wechat.WeChatCallbackController;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 获取AccessToken
 *
 * @author zy 2018-9-30
 */
@Service
public class AccessTokenService {

    @Resource
    BaseDao baseDao;

    @Resource
    AuthService authService;

    @Resource
    RedisUtils redisUtils;

    /**
     * 输出到控制台的日志
     */
    private static Logger log = LoggerFactory.getLogger(WeChatCallbackController.class);

    /**
     * 获取企业/税务局的永久授权码
     */
    public String getPermanentCode(String type, String corpId) {
        String sql = "select PERMANENT_CODE as permanentCode from ";
        if (StringUtils.equals(type, "tax")) {
            sql += "enterprise_info ";
        } else {
            sql += "tax_info ";
        }
        sql += "where CORP_ID=? and STATE='Y'";

        Map<String, Type> types = new HashMap<>(1);
        types.put("permanentCode", StringType.INSTANCE);
        List<Map<String, Object>> list = baseDao.queryBySQL(sql, types, corpId);
        Map<String, Object> map = list != null && list.size() > 0 ? list.get(0) : null;
        return map == null ? null : (String) map.get("permanentCode");
    }

    private static final String GET_CORP_TOKEN="https://qyapi.weixin.qq.com/cgi-bin/service/get_corp_token?suite_access_token=%s";

    /**
     * 通过永久授权码获取AccessToken
     *
     * @param corpId  企业微信的企业id
     * @param type 获取accessToken的类型,tax-税务分局 其他-企业
     */
    public String getAccessTokenByPermanentCode(String corpId,String type) throws IOException {
        Map<String,Object> map=new HashMap<>(2);
        // 从缓存中获取若不为null直接返回
        type=StringUtils.equals("tax",type)?"tax":"ent";
        String key=corpId+type+"accessToken";
        String acccessToken=(String)redisUtils.get(key);
        if(StringUtils.isBlank(acccessToken)){
            String url=GET_CORP_TOKEN;
            url=String.format(url,(String)authService.getSuiteAccessToken());
            Map<String,Object> parameters=new HashMap<>(2);
            parameters.put("auth_corpid",corpId);
            String permanent=getPermanentCode(type, corpId);
            // 若permanent不为空或null访问微信接口获取对应企业的accessToken
            if(StringUtils.isNotBlank(permanent)){
                parameters.put("permanent",permanent);
                String result=HttpUtils.requestByPost(url,JSONObject.toJSONString(parameters));
                if(StringUtils.isNotBlank(result)){
                    JSONObject jsonObject=JSONObject.parseObject(result);
                    if(jsonObject!=null&&jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY)==0) {
                        acccessToken = jsonObject.getString("access_token");
                        Integer expiresIn=jsonObject.getInteger("expires_in")- Constant.ENT_ACCESSTOKEN_ADVANCE_TIME;
                        // 更新accessToken缓存，缓存的accessToken提前一分钟失效
                        redisUtils.set(key,acccessToken,expiresIn);
                    }else{
                        map.put("msg",jsonObject==null?"访问微信接口失败":"访问微信接口错误"+jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY)+jsonObject.getString(Constant.WECHAT_ERROR_MSG_KEY));
                    }
                }else{
                    map.put("msg","访问微信接口失败");
                }
            }else{
                map.put("msg","未查询到该企业的授权码");
            }
        }
        map.put("accessToken",acccessToken);
        return  acccessToken;
    }

    private static final String GET_PROVIDER_ACCESS_TOKEN_URL="https://qyapi.weixin.qq.com/cgi-bin/service/get_provider_token";

    /**
     * 获取服务商的token
     *
     * */
    public String getProviderAccessToken() throws IOException {
        // 服务商token在缓存中的key
        String providerAccessTokenKey="provider_access_token";
        // 获取缓存中的服务商token
        String providerAccessToken=(String)redisUtils.get(providerAccessTokenKey);
        // 判断缓存中的服务商token是否存在，若不存在访问微信接口，更新缓存中的服务商token
        if(StringUtils.isBlank(providerAccessToken)){
            Map<String,Object> params=new HashMap<>(2);
            params.put("corpid",Constant.PROVIDER_CORP_ID);
            params.put("provider_secret",Constant.PROVIDER_SECRET);
           String result=HttpUtils.requestByPost(GET_PROVIDER_ACCESS_TOKEN_URL,JSONObject.toJSONString(params));
           JSONObject jsonObject=JSONObject.parseObject(result);
           if(jsonObject!=null&&jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY)==0){
               providerAccessToken=jsonObject.getString(providerAccessTokenKey);
               Integer expiresIn=jsonObject.getInteger("expires_in");
               redisUtils.set(providerAccessTokenKey,providerAccessToken,expiresIn-Constant.ENT_ACCESSTOKEN_ADVANCE_TIME);
            }else{
               log.info("访问微信接口获取provider_access_token"+(jsonObject==null?"失败":"异常"+jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY)+","+jsonObject.getString(Constant.WECHAT_ERROR_MSG_KEY)));
           }
        }
        return providerAccessToken;
    }

    private static final String GET_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=%s&corpsecret=%s";
    /**
     * 税务分局获取自建应用的 Token
     *
     * @param type phone获取通讯录accessToken,null或空串获取普通股的token
     */
    public String getAccessTokenBySelfBuilt(String type) throws IOException, URISyntaxException {
        String phoneType="phone";
        String corpId = Constant.corpId;
        String corpSecret = Constant.paramentSecret;
        if(!StringUtils.equals(type,phoneType)){
            type="";
            corpSecret=Constant.TAX_NORMAL_SECRET;
        }
        // 从缓存中获取accessToken,判断是否过期
        String accessToken = (String) redisUtils.get("selfBuiltTaxAccessToken"+type);
        //若过期，或未过期，但到期时间与当前时间相隔1分钟以内，需要访问微信接口更新accessToken
        boolean refreshFlag = !StringUtils.isNotBlank(accessToken);
        // 判断是否更新accessToken
        if (refreshFlag) {
            // 企业微信获取accessToken
            String url = String.format(GET_TOKEN_URL, corpId, corpSecret);
            String result = HttpUtils.requstByGet(url, null);
            // 处理微信接口返回的信息，将AccessToken放入缓存，缓存中记录时间
            JSONObject jsonObject = JSONObject.parseObject(result);
            // 若失败进行一次重试
            if (jsonObject == null || jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) != 0) {
                log.info("访问微信接口获取selfBuiltTaxAccessToken" + (jsonObject == null ? "失败" : "异常" + jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) + "," + jsonObject.getString(Constant.WECHAT_ERROR_MSG_KEY)));
                result = HttpUtils.requstByGet(url, null);
                // 处理微信接口返回的信息，将AccessToken放入缓存，缓存中记录时间
                jsonObject = JSONObject.parseObject(result);
                if (jsonObject != null && jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) == 0) {
                    // 获取返回的信息
                    accessToken = jsonObject.getString("access_token");
                    Long expiresIn = jsonObject.getLong("expires_in");
                    // 设置提前一分钟过期
                    redisUtils.set("selfBuiltTaxAccessToken"+type, accessToken, expiresIn - 60);
                } else {
                    log.info("访问微信接口获取selfBuiltAccessToken重试" + (jsonObject == null ? "失败" : "异常" + jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) + "," + jsonObject.getString(Constant.WECHAT_ERROR_MSG_KEY)));
                }
            } else {
                //获取成功，更新缓存中的信息
                accessToken = jsonObject.getString("access_token");
                // 获取企业微信返回的过期时间
                Long expiresIn = jsonObject.getLong("expires_in");
                // 设置提前1分钟过期
                redisUtils.set("selfBuiltTaxAccessToken"+type, accessToken, expiresIn - Constant.ENT_ACCESSTOKEN_ADVANCE_TIME);
            }
            log.info("获取成功"+type+"，更新缓存中的信息"+accessToken);
        }
        log.info("获取成功"+type+"，selfBuiltTaxAccessToken的信息为"+accessToken);
        // 返回accessToken
        return accessToken;
    }

}
