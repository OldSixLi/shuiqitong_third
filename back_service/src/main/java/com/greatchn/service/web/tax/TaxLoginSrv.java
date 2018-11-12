package com.greatchn.service.web.tax;

import com.alibaba.fastjson.JSONObject;
import com.greatchn.common.dao.BaseDao;
import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.HttpUtils;
import com.greatchn.common.utils.MD5Util;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.controller.wechat.WeChatCallbackController;
import com.greatchn.po.*;
import com.greatchn.service.AuditService;
import com.greatchn.service.ent.EnterpriseService;
import com.greatchn.service.tax.TaxService;
import com.greatchn.service.wechat.AccessTokenService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.DigestException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 税务分局登录业务层
 *
 * @author zy 2018-10-13
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class TaxLoginSrv {

    @Resource
    AccessTokenService accessTokenService;

    @Resource
    TaxService taxService;

    @Resource
    AuditService auditService;

    @Resource
    RedisUtils redisUtils;

    @Resource
    EnterpriseService enterpriseService;

    @Resource
    BaseDao baseDao;

    @Resource
    UserCenterSrv userCenterSrv;

    /**
     * 输出到控制台的日志
     */
    private static Logger log = LoggerFactory.getLogger(WeChatCallbackController.class);

    private static final String TAX_GET_USER_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=%s&code=%s";

    /**
     * 自建应用使用的少嘛登录
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> selfLoginByQRCode(String code, String uuid, String oldToken) throws IOException, URISyntaxException {
        Map<String, Object> map = new HashMap<>(16);
        String returnFlag;
        String msg = null;
        String url = TAX_GET_USER_INFO_URL;
        String accessToken = accessTokenService.getAccessTokenBySelfBuilt(null);
        url = String.format(url, accessToken, code);
        String result = HttpUtils.requestByPost(url, "");
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject != null && jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) == 0) {
            String userId = jsonObject.getString("UserId");
            // 获取userId
            String corpId = Constant.corpId;
            // 根据corpId查询税务分局信息
            Map<String, Object> executeMap = excuteLogin(corpId, userId, uuid, oldToken);
            returnFlag = (String) executeMap.get("flag");
            if (StringUtils.isNotBlank((String) executeMap.get("msg"))) {
                msg = (String) executeMap.get("msg");
            }
            map.put("corpId", corpId);
            map.put("userId", userId);
            map.put("manager", executeMap.get("manager"));
            map.put("token", executeMap.get("token"));
            map.put("userRight", executeMap.get("userRight"));
            map.put("roles", executeMap.get("roles"));
            map.put("employeeInfo", executeMap.get("employeeInfo"));
            map.put("corpInfo", executeMap.get("corpInfo"));
            if (StringUtils.equals("6", returnFlag)) {
                map.put("reason", executeMap.get("reason"));
            }
        } else {
            // 访问微信接口失败
            returnFlag = "0";
            msg = jsonObject == null ? "访问企业微信接口失败,请重试" : "访问企业微信错误：" + jsonObject.getString(Constant.WECHAT_ERROR_CODE_KEY) + "," + jsonObject.getString(Constant.WECHAT_ERROR_MSG_KEY);
        }
        if (StringUtils.isNotEmpty(msg)) {
            map.put("msg", msg);
        }
        map.put("flag", returnFlag);
        return map;
    }

    private static final String GET_WECHAT_USER_INFO = " https://qyapi.weixin.qq.com/cgi-bin/service/get_login_info?access_token=%S";

    /**
     * 第三方应用的扫码登录
     */
    public Map<String, Object> loginByQRCode(String authCode, String uuid, String oldToken) throws IOException, URISyntaxException {
        Map<String, Object> map = new HashMap<>(16);
        String msg = null;
        String returnFlag;
        String providerAccessToken = accessTokenService.getProviderAccessToken();
        String url = String.format(GET_WECHAT_USER_INFO, providerAccessToken);
        Map<String, Object> params = new HashMap<>(1);
        params.put("auth_code", authCode);
        // 获取用户信息
        String result = HttpUtils.requestByPost(url, JSONObject.toJSONString(params));
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject != null && jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) == 0) {
            // 获取用户的userId,corpId
            JSONObject userInfo = jsonObject.getJSONObject("user_info");
            if (userInfo != null && StringUtils.isNotBlank(userInfo.getString("userid"))) {
                String userId = userInfo.getString("userid");
                JSONObject corpInfo = jsonObject.getJSONObject("corp_info");
                String corpId = corpInfo.getString("corpid");

                Map<String, Object> executeMap = excuteLogin(corpId, userId, uuid, oldToken);
                returnFlag = (String) executeMap.get("flag");
                if (StringUtils.isNotBlank((String) executeMap.get("msg"))) {
                    msg = (String) executeMap.get("msg");
                }
                map.put("corpId", corpId);
                map.put("userId", userId);
                map.put("manager", executeMap.get("manager"));
                map.put("token", executeMap.get("token"));
                map.put("userRight", executeMap.get("userRight"));
                map.put("roles", executeMap.get("roles"));
                map.put("employeeInfo", executeMap.get("employeeInfo"));
                map.put("corpInfo", executeMap.get("corpInfo"));
                if (StringUtils.equals("6", returnFlag)) {
                    map.put("reason", executeMap.get("reason"));
                }
            } else {
                // 该用户不在通讯录中
                returnFlag = "1";
                msg = "您不在通讯录中,无法进行登录";
            }

        } else {
            // 访问微信接口失败
            returnFlag = "0";
            msg = jsonObject == null ? "访问企业微信接口失败,请重试" : "访问企业微信错误：" + jsonObject.getString(Constant.WECHAT_ERROR_CODE_KEY) + "," + jsonObject.getString(Constant.WECHAT_ERROR_MSG_KEY);
        }

        if (StringUtils.isNotEmpty(msg)) {
            map.put("msg", msg);
        }
        map.put("flag", returnFlag);
        return map;
    }


    /**
     * 执行登录的税务分局审核状态校验
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> excuteLogin(String corpId, String userId, String uuid, String oldToken) throws IOException, URISyntaxException {
        String msg = null;
        String returnFlag = null;
        Map<String, Object> resultMap = new HashMap<>(2);
        TaxInfo taxInfo = taxService.findTaxInfoByCorpId(corpId, null);
        // 查询税务局信息是否存在
        if (taxInfo != null) {
            // 判断税务局信息审核状态
            AuditInfo auditInfo = auditService.getAuditInfoByEntTaxId(taxInfo.getId(), null, "2");
            // 税务分局为审核通过，执行登录
            if (auditInfo == null) {
                // 税务分局未提交审核
                returnFlag = "3";
            } else if (StringUtils.equals(auditInfo.getState(), "0") || StringUtils.equals(auditInfo.getState(), "3")) {
                // 审核中或税务分局信息更新，重新审核中
                returnFlag = "4";
            } else if (StringUtils.equals(auditInfo.getState(), "1")) {
                // 税务分局审核通过
                returnFlag = "5";
            } else if (StringUtils.equals(auditInfo.getState(), "2")) {
                // 税务分局审核未通过,返回未通过原因
                returnFlag = "6";
                resultMap.put("reason", auditInfo.getReason());
            }
            // 获取税务分局用户登录信息
            Map<String, Object> taxMap = new HashMap<>(2);
            taxMap.put("taxInfo", taxInfo);
            taxMap.put("auditInfo", auditInfo);
            String taxInfoKey = RedisUtils.REDIS_PREFIX_TAX + corpId;
            redisUtils.set(taxInfoKey, taxMap);
            // 用户登录信息放入缓存
            Map<String, Object> map = getLoginToken(taxInfo, userId, uuid);
            msg = (String) map.get("msg");
            if (StringUtils.isNotEmpty(msg)) {
                // 登录出现错误返回错误信息
                returnFlag = "0";
                msg = "企业微信corpId【" + corpId + "】,userId【" + userId + "】的员工登录失败，原因：" + msg;
                log.info(msg);
            } else {
                // 登录成功返回token
                String token = (String) map.get("token");
                resultMap.put("manager", taxInfo.getManager());
                resultMap.put("token", token);
                resultMap.put("userRight", map.get("userRight"));
                resultMap.put("roles", map.get("roles"));
                resultMap.put("employeeInfo", map.get("employeeInfo"));
                resultMap.put("corpInfo", map.get("corpInfo"));

            }
        } else {
            // 用户未授权或已被作废
            returnFlag = "2";
        }
        // 旧的token未失效，清除
        if (StringUtils.isNotBlank(oldToken)) {
            Object o = redisUtils.get(oldToken);
            if (o != null) {
                redisUtils.del(oldToken);
            }
        }
        if (StringUtils.isNotEmpty(msg)) {
            resultMap.put("msg", msg);
        }
        resultMap.put("flag", returnFlag);
        return resultMap;
    }

    /**
     * 执行实际的登录操作，获取用户各项信息
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> getLoginToken(TaxInfo taxInfo, String userId, String uuid) throws IOException, URISyntaxException {
        String corpId = taxInfo.getCorpId();
        // 不存在，访问微信接口获取员工信息，保存新的员工
        Map<String, Object> resultMap = new HashMap<>(2);
        String msgKey = "msg";
        String token;
        // 获取员工具体信息,进行登录，将信息放入缓存，
        // 查询员工信息是否存在
        EmployeeInfo employeeInfo = enterpriseService.findEmplyeeInfo(corpId, userId);
        if (employeeInfo == null) {
            // 获取当前企业token
            String accessToken;
            if (Constant.IS_SELF_BUILT_LOGIN) {
                accessToken = accessTokenService.getAccessTokenBySelfBuilt("phone");
            } else {
                accessToken = accessTokenService.getAccessTokenByPermanentCode(corpId, "tax");
            }
            if (StringUtils.isNotEmpty(accessToken)) {
                Map<String, Object> employeeMap = taxService.saveInitUserInfo(employeeInfo, corpId, userId, taxInfo.getId());
                if (StringUtils.isBlank((String) employeeMap.get(msgKey))) {
                    employeeInfo = (EmployeeInfo) employeeMap.get("employeeInfo");
                } else {
                    resultMap.put(msgKey, employeeMap.get(msgKey));
                }
            } else {
                // 获取accessToken失败
                resultMap.put(msgKey, "获取微信accessToken失败");
            }
        }
        if (employeeInfo != null) {
            // 查询是否有用户信息，没有则增加用户信息
            TaxUserInfo taxUserInfo = taxService.findUserInfo(employeeInfo.getId(), taxInfo.getId());
            if (taxUserInfo == null) {
                taxUserInfo = taxService.saveUserInfo(employeeInfo.getId(), taxInfo.getId());
            }
            // 查询当前用户是否有角色，若没有，增加当前用户角色为普通用户
            List<Map<String, Object>> userRoleInfos = taxService.getUserRole(taxUserInfo.getId(), taxInfo.getId());
            if (userRoleInfos == null || userRoleInfos.size() <= 0) {
                // 查询普通用户角色信息
                TaxRoleInfo taxRoleInfo = taxService.findNormalRoleInfo(taxInfo.getId(), "update");
                if (taxRoleInfo == null) {
                    taxRoleInfo = new TaxRoleInfo();
                    taxRoleInfo.setName(Constant.NORMAL_NAME);
                    taxRoleInfo.setDescription(Constant.NORMAL_DESCRIPTION);
                    taxRoleInfo.setTaxId(taxInfo.getId());
                    taxRoleInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
                    taxRoleInfo.setCreator(0);
                    baseDao.save(taxRoleInfo);
                    // 保存普通用户角色的默认权限
                    TaxRoleRight taxRoleRight = new TaxRoleRight();
                    taxRoleRight.setRightName(Constant.TAX_ALL_ROLE_HAVE_RIGHT);
                    taxRoleRight.setRoleId(taxRoleInfo.getId());
                    baseDao.save(taxRoleRight);
                }
                // 保存用户角色信息
                TaxUserRole taxUserRole = new TaxUserRole();
                taxUserRole.setRoleId(taxRoleInfo.getId());
                taxUserRole.setUserId(taxUserInfo.getId());
                baseDao.save(taxUserRole);
            }
            // 更新用户的最后登录时间
            taxUserInfo.setLastTime(new Timestamp(System.currentTimeMillis()));
            baseDao.update(taxUserInfo);
            // 若存在直接放入缓存，并返回缓存的token
            Map<String, Object> loginMap = new HashMap<>(4);
            loginMap.put("taxInfoKey", RedisUtils.REDIS_PREFIX_TAX + corpId);
            loginMap.put("employeeInfo", employeeInfo);
            loginMap.put("userInfo", taxUserInfo);
            // 若为管理员，查询是否有角色信息，没有直接添加
            if (StringUtils.equals(taxInfo.getManager(), userId)) {
                taxService.userAddManagerRole(taxUserInfo.getId(), taxInfo.getId());
            }
            List<Map<String, Object>> list = taxService.getUserRight(taxUserInfo.getId(), taxInfo.getId());
            loginMap.put("userRight", list);
            // 获取角色信息，并返回
            List<Map<String, Object>> roles = taxService.findUserRoleInfos(taxUserInfo.getId(), taxInfo.getId());
            loginMap.put("roles", roles);
            token = RedisUtils.REDIS_PREFIX_TAX + uuid;
            // 是否为调试模式，若为调试则缓存不过期
            if (Constant.IS_TEST) {
                redisUtils.set(token, loginMap);
            } else {
                redisUtils.set(token, loginMap, Constant.ENT_TOKEN_TIME_OUT);
            }
            resultMap.put("employeeInfo", employeeInfo);
            resultMap.put("corpInfo", taxInfo);
            resultMap.put("token", token);
            // 权限一起返回
            resultMap.put("userRight", list);
            // 角色也一起返回
            resultMap.put("roles", roles);
        }
        return resultMap;
    }


    /**
     * 账号名密码登录
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> loginByPassword(String loginAccount, String password, String uuid, String oldToken) throws DigestException, IOException, URISyntaxException {
        Map<String, Object> resultMap = new HashMap<>(16);
        String returnFlag = null;
        String msg;
        String effectiveState = "Y";
        // 根据用户名查询用户
        TaxUserInfo userInfo = taxService.findUserInfo(loginAccount);
        if (userInfo != null && StringUtils.equals(effectiveState, userInfo.getState())) {
            // 获取混淆后输入密码
            String inputPassword = MD5Util.SHA1(userInfo.getId().toString() + password);
            // 比较混淆后的输入密码是否与数据库中的相同
            if (StringUtils.equals(inputPassword, userInfo.getPassword())) {
                // 查询是否有员工信息
                EmployeeInfo employeeInfo = taxService.getEmployeeInfoById(userInfo.getEmployeeId());
                if (employeeInfo != null) {
                    // 若存在查询税务分局信息以及该税务分局的审核信息
                    TaxInfo taxInfo = taxService.getTaxInfoById(userInfo.getTaxId());
                    if (taxInfo != null && StringUtils.equals(effectiveState, taxInfo.getState())) {
                        // 查询税务分局的审核信息，并根据相关状态进行判断
                        AuditInfo auditInfo = auditService.getAuditInfoByEntTaxId(taxInfo.getId(), null, "2");
                        // 税务分局为审核通过，执行登录
                        if (auditInfo == null) {
                            // 税务分局未提交审核
                            returnFlag = "3";
                        } else if (StringUtils.equals(auditInfo.getState(), "0") || StringUtils.equals(auditInfo.getState(), "3")) {
                            // 审核中或税务分局信息更新，重新审核中
                            returnFlag = "4";
                        } else if (StringUtils.equals(auditInfo.getState(), "1")) {
                            // 税务分局审核通过
                            returnFlag = "5";
                        } else if (StringUtils.equals(auditInfo.getState(), "2")) {
                            // 税务分局审核未通过,返回未通过原因
                            returnFlag = "6";
                            resultMap.put("reason", auditInfo.getReason());
                        }
                        String corpId = employeeInfo.getEnterpriseId();
                        String userId = employeeInfo.getUserId();
                        // 获取税务分局用户登录信息
                        Map<String, Object> taxMap = new HashMap<>(2);
                        taxMap.put("taxInfo", taxInfo);
                        taxMap.put("auditInfo", auditInfo);
                        String taxInfoKey = RedisUtils.REDIS_PREFIX_TAX + corpId;
                        redisUtils.set(taxInfoKey, taxMap);
                        // 用户登录信息放入缓存
                        Map<String, Object> map = getLoginToken(taxInfo, userId, uuid);
                        msg = (String) map.get("msg");
                        if (StringUtils.isNotEmpty(msg)) {
                            // 登录出现错误返回错误信息
                            returnFlag = "7";
                            msg = "企业微信corpId【" + corpId + "】,userId【" + userId + "】的员工登录失败，原因：" + msg;
                            log.info(msg);
                        } else {
                            // 登录成功返回token
                            String token = (String) map.get("token");
                            resultMap.put("manager", taxInfo.getManager());
                            resultMap.put("token", token);
                            resultMap.put("userRight", map.get("userRight"));
                            resultMap.put("roles", map.get("roles"));
                            resultMap.put("employeeInfo", map.get("employeeInfo"));
                            resultMap.put("corpInfo", map.get("corpInfo"));

                        }
                    } else {
                        // 未授权或已被作废
                        returnFlag = "8";
                        msg = "该税务分局未授权或已作废";
                    }
                    // 旧的token未失效，清除
                    if (StringUtils.isNotBlank(oldToken)) {
                        Object o = redisUtils.get(oldToken);
                        if (o != null) {
                            redisUtils.del(oldToken);
                        }
                    }
                } else {
                    // 若员工信息不存在，提示 企业微信信息丢失，请使用企业微信扫码登录
                    returnFlag = "2";
                    msg = "信息丢失，请使用企业微信扫码登录";
                    // 删除userInfo的脏数据
                    taxService.deleteUserInfo(userInfo);
                }
            } else {
                // 输入密码错误
                returnFlag = "1";
                msg = "密码错误";
            }
        } else {
            // 用户不存在或不可使用
            returnFlag = "0";
            msg = "账号不存在";
        }
        if (StringUtils.isNotBlank(msg)) {
            resultMap.put("msg", msg);
        }
        resultMap.put("flag", returnFlag);
        return resultMap;
    }

}
