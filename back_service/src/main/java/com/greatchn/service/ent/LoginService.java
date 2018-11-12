package com.greatchn.service.ent;

import com.alibaba.fastjson.JSONObject;
import com.greatchn.common.dao.BaseDao;
import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.HttpUtils;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.po.*;
import com.greatchn.service.AuditService;
import com.greatchn.service.wechat.AccessTokenService;
import com.greatchn.service.wechat.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 登录业务层
 *
 * @author zy 2018-9-17
 */
@Service
public class LoginService {

    @Resource
    BaseDao baseDao;
    @Resource
    EnterpriseService enterpriseService;
    @Resource
    RedisUtils redisUtils;
    @Resource
    AuthService authService;
    @Resource
    AuditService auditService;

    @Resource
    AccessTokenService accessTokenService;

   /**
     * 控制台日志对象
     */
    protected final Logger log = Logger.getLogger(this.getClass());

   // private static final String GET_USER_INFO_BY_CODE_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=%s&code=%s";

    private static final String GET_USER_INFO_BY_CODE_URL ="https://qyapi.weixin.qq.com/cgi-bin/service/getuserinfo3rd?access_token=%s&code=%s";

    /**
     * 用户登录，返回登录结果
     *
     * @param code 获取用户信息的授权code
     * @author zy 2018-9-18
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> login(String code,String uuid,String oldToken) throws IOException, URISyntaxException {
        log.info("--code登录--");
        Map<String, Object> map = new HashMap<>(3);
        String returnFlag;
        String msg = null;
        // 获取服务商的第三方suitAccessToken
        String accessToken = authService.getSuiteAccessToken();
        if(StringUtils.isNotEmpty(code)){
            // 获取userId
            String url = GET_USER_INFO_BY_CODE_URL;
            url = String.format(url, accessToken, code);
            // 访问微信接口
            String result = HttpUtils.requstByGet(url, null);
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject == null || jsonObject.getIntValue(Constant.WECHAT_ERROR_CODE_KEY) != 0) {
                // 访问微信接口失败
                returnFlag = "0";
                msg = jsonObject == null ? "访问微信接口错误,请重试" : "访问企业微信错误：" + jsonObject.getString(Constant.WECHAT_ERROR_CODE_KEY) + "," + jsonObject.getString("errmsg") + ",请重试";
            } else {
                if (StringUtils.isNotEmpty(jsonObject.getString("OpenId"))) {
                    // 该用户未添加企业
                    returnFlag = "1";
                    msg = "您不是该企业的成员";
                } else {
                    String corpId = Constant.corpId;
                    String userId = jsonObject.getString("UserId");
                    Map<String,Object> executeMap=excuteLogin(corpId,userId,uuid,oldToken);
                    returnFlag=(String)executeMap.get("flag");
                    if(StringUtils.isNotBlank((String)executeMap.get("msg"))){
                        msg=(String)executeMap.get("msg");
                    }
                    map.put("corpId", corpId);
                    map.put("userId", userId);
                    map.put("manager",executeMap.get("manager"));
                    map.put("token", executeMap.get("token"));
                    map.put("userRight", executeMap.get("userRight"));
                    map.put("roles",executeMap.get("roles"));
                    if(StringUtils.equals("6",returnFlag)){
                        map.put("reason", executeMap.get("reason"));
                    }
                }
            }
        }else{
            // code为null或空
            returnFlag = "0";
            msg = "缺少code";
        }
        if (StringUtils.isNotEmpty(msg)) {
            map.put("msg", msg);
        }
        map.put("flag", returnFlag);
        return map;
    }

    /**
     * 具体的登录操作
     *
     * @param corpId 用户的企业微信企业id
     * @param userId 用户userId
     *
     * @author zy 2018-9-18
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> excuteLogin(String corpId,String userId,String uuid,String oldToken) throws IOException, URISyntaxException {
        Map<String, Object> map = new HashMap<>(3);
        // 获取用户部分信息
        String returnFlag = "";
        String msg = null;
        //判断企业状态
        EnterpriseInfo enterpriseInfo = enterpriseService.findEnterpriseByCorpId(corpId, null);
        if (enterpriseInfo != null) {
            // 查询企业审核信息
            AuditInfo auditInfo = auditService.getAuditInfoByEntTaxId(enterpriseInfo.getId(), null, "1");
            if (auditInfo == null) {
                // 企业未提交审核
                returnFlag = "3";
            } else if (StringUtils.equals(auditInfo.getState(), "0") || StringUtils.equals(auditInfo.getState(), "3")) {
                // 审核中或企业信息更新，重新审核中
                returnFlag = "4";
            } else if (StringUtils.equals(auditInfo.getState(), "1")) {
                // 企业审核通过
                returnFlag = "5";
            } else if (StringUtils.equals(auditInfo.getState(), "2")) {
                // 企业审核未通过,返回未通过原因
                returnFlag = "6";
                map.put("reason", auditInfo.getReason());
            }
            // 查询缓存中是否有企业的信息，若有更新
            Map<String, Object> entInfoMap = new HashMap<>(2);
            entInfoMap.put("entInfo", enterpriseInfo);
            entInfoMap.put("auditInfo", auditInfo);
            if (!redisUtils.set(RedisUtils.REDIS_PREFIX_ENT + corpId, entInfoMap)) {
                // 重试一次
                if (!redisUtils.set(RedisUtils.REDIS_PREFIX_ENT + corpId, entInfoMap)) {
                    // 重试失败，返回错误
                    log.error("企业信息缓存失败");
                }
            }
            // 用户登录信息放入缓存
            Map<String, Object> resultMap = getLoginToken(enterpriseInfo.getId(), corpId, userId, enterpriseInfo.getManager(),uuid);
            msg = (String) resultMap.get("msg");
            if (StringUtils.isNotEmpty(msg)) {
                // 登录出现错误返回错误信息
                returnFlag = "0";
                msg="企业微信corpId【" + corpId + "】,userId【" + userId + "】的员工登录失败，原因：" + msg;
                log.info(msg);
            } else {
                // 登录成功返回token
                String token = (String) resultMap.get("token");
                map.put("manager",enterpriseInfo.getManager());
                map.put("token", token);
                map.put("userRight", resultMap.get("userRight"));
                map.put("roles",resultMap.get("roles"));
            }
        } else {
            // 企业微信未添加授权(无企业信息,需要企业微信的管理员授权添加)
            returnFlag = "2";
        }
        // 旧的token未失效，清除
        if(StringUtils.isNotBlank(oldToken)){
            Object o=redisUtils.get(oldToken);
            if(o!=null){
                redisUtils.del(oldToken);
            }
        }
        if (StringUtils.isNotEmpty(msg)) {
            map.put("msg", msg);
        }
        map.put("flag", returnFlag);
        return map;
    }


    /**
     * 校验用户信息，将登录信息放入缓存，并返回登录token
     *
     * @author zy 2018-9-18
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> getLoginToken(Integer entId, String corpId, String userId, String manager,String uuid) throws IOException, URISyntaxException {
        Map<String, Object> resultMap = new HashMap<>(2);
        String msgKey = "msg";
        String token;
        // 获取员工具体信息,进行登录，将信息放入缓存，
        // 查询员工信息是否存在
        EmployeeInfo employeeInfo = enterpriseService.findEmplyeeInfo(corpId, userId);
        if (employeeInfo == null) {
            // 获取当前企业token
            String accessToken = accessTokenService.getAccessTokenByPermanentCode(corpId,"ent");
            if (StringUtils.isNotEmpty(accessToken)) {
                Map<String,Object> employeeMap=saveInitUserInfo(employeeInfo,corpId,userId,entId);
                if(StringUtils.isBlank((String)employeeMap.get(msgKey))){
                    employeeInfo=(EmployeeInfo) employeeMap.get("employeeInfo");
                }else{
                    resultMap.put(msgKey,employeeMap.get(msgKey));
                }
            } else {
                // 获取accessToken失败
                resultMap.put(msgKey, "获取微信accessToken失败");
            }
        }
        if (employeeInfo != null) {
            // 查询是否有用户信息，没有则增加用户信息
            EntUserInfo entUserInfo = enterpriseService.findUserInfo(employeeInfo.getId(), entId);
            if (entUserInfo == null) {
                entUserInfo = enterpriseService.saveUserInfo(employeeInfo.getId(), entId);
            }
            // 查询当前用户是否有角色，若没有，增加当前用户角色为普通用户
            List<Map<String, Object>> userRoleInfos = enterpriseService.getUserRole(entUserInfo.getId());
            if (userRoleInfos == null || userRoleInfos.size() <= 0) {
                // 查询普通用户角色信息
                EntRoleInfo entRoleInfo = enterpriseService.findNormalRoleInfo();
                // 保存用户角色信息
                EntUserRole entUserRole = new EntUserRole();
                entUserRole.setRoleId(entRoleInfo.getId());
                entUserRole.setUserId(entUserInfo.getId());
                baseDao.save(entUserRole);
            }
            // 更新用户的最后登录时间
            entUserInfo.setLastTime(new Timestamp(System.currentTimeMillis()));
            baseDao.update(entUserInfo);
            // 若存在直接放入缓存，并返回缓存的token
            Map<String, Object> loginMap = new HashMap<>(4);
            loginMap.put("entInfoKey", RedisUtils.REDIS_PREFIX_ENT + corpId);
            loginMap.put("employeeInfo", employeeInfo);
            loginMap.put("userInfo", entUserInfo);
            // 若为管理员，查询是否有角色信息，没有直接添加
            if (StringUtils.equals(manager, userId)) {
                enterpriseService.userAddManagerRole(entUserInfo.getId(),entId);
            }
            List<Map<String, Object>> list = enterpriseService.getUserRight(entUserInfo.getId());
            loginMap.put("userRight", list);
            // 获取角色信息，并返回
            List<Map<String,Object>> roles=enterpriseService.findUserRoleInfos(entUserInfo.getId());
            loginMap.put("roles",roles);
            token = uuid;
            // 是否为调试模式，若为调试则缓存不过期
            if(Constant.IS_TEST){
                redisUtils.set(token, loginMap);
            }else{
                redisUtils.set(token, loginMap,Constant.ENT_TOKEN_TIME_OUT);
            }
            resultMap.put("token", token);
            // 权限一起返回
            resultMap.put("userRight", list);
            // 角色也一起返回
            resultMap.put("roles",roles);
        }
        return resultMap;
    }

    private static final String GET_USER_DETAIL_INFO_URL="https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=%s&userid=%s";
    /**
     * 保存员工初始化信息（employee\ent_user_info）表中的信息
     *
     * @author zy 2018-9-20
     * */
    public Map<String,Object> saveInitUserInfo(EmployeeInfo employeeInfo,String corpId,String userId,Integer entId) throws IOException, URISyntaxException {
        Map<String,Object> map=new HashMap<>(2);
        // 若员工信息不存在，调用微信接口获取成员信息，并进行保存
        if(employeeInfo==null){
            String accessToken;
            if (Constant.IS_SELF_BUILT_LOGIN) {
                accessToken=accessTokenService.getAccessTokenBySelfBuilt("phone");
            } else {
                accessToken=accessTokenService.getAccessTokenByPermanentCode(corpId,"ent");
            }
            String url = GET_USER_DETAIL_INFO_URL;
            url = String.format(url, accessToken, userId);
            String errCodeKey = Constant.WECHAT_ERROR_CODE_KEY;
            String result = HttpUtils.requstByGet(url, null);
            // 解析企业微信接口返回内容
            JSONObject jsonObject = JSONObject.parseObject(result);
            // 访问接口是否成功
            if (jsonObject != null && jsonObject.getIntValue(errCodeKey) == 0) {
                // 访问成功，解析对应的成员信息
                employeeInfo = new EmployeeInfo();
                employeeInfo.setUserId(userId);
                employeeInfo.setEmail(jsonObject.getString("email"));
                employeeInfo.setGender(jsonObject.getInteger("gender"));
                employeeInfo.setEnterpriseId(corpId);
                employeeInfo.setAvatar(jsonObject.getString("avatar"));
                employeeInfo.setMobile(jsonObject.getString("mobile"));
                employeeInfo.setName(jsonObject.getString("name"));
                // 登录设置为激活
                employeeInfo.setStatus(jsonObject.getInteger("status"));
                employeeInfo.setDepartmnetId(jsonObject.getString("department"));
                baseDao.save(employeeInfo);
                // 保存用户信息
                enterpriseService.saveUserInfo(employeeInfo.getId(), entId);
                map.put("employeeInfo",employeeInfo);
            } else {
                // 微信接口返回错误信息
                String msg = jsonObject==null?"访问企业微信接口失败,请重试":"访问企业微信错误：" + jsonObject.getString(errCodeKey) + "," + jsonObject.getString("errmsg");
                map.put("msg",msg);
            }
        }
        return map;
    }


    /**
     * 保存或更新分配角色
     *
     * @author zy 2018-9-19
     **/
    @Transactional(rollbackFor = Exception.class)
    public String saveOrUpdateRoleUser(String userId, Integer roleId, String corpId) throws IOException, URISyntaxException {
        String msg = null;
        EnterpriseInfo enterpriseInfo = enterpriseService.findEnterpriseByCorpId(corpId, null);
        if (enterpriseInfo != null) {
            // 删除原有角色的关联信息
            String sql = "delete eur from ent_user_role eur join ent_user_info eui on eur.USER_ID=eui.ID where eur.ROLE_ID=? and eui.ENTERPRISE_ID=?";
            baseDao.executeSQL(sql, roleId,enterpriseInfo.getId());
            // 判断是否为管理员角色
            if(roleId==-1){
                enterpriseInfo.setManager(userId);
                baseDao.update(enterpriseInfo);
            }
            // 查询
            Map<String, Object> map = saveEmployeeInfo(corpId, userId, enterpriseInfo.getId(), enterpriseInfo.getManager());
            if (map.get("employeeInfo") != null) {
                EmployeeInfo employeeInfo = (EmployeeInfo) map.get("employeeInfo");
                EntUserInfo entUserInfo = (EntUserInfo) map.get("userInfo");
                if (entUserInfo == null) {
                    entUserInfo = enterpriseService.saveUserInfo(employeeInfo.getId(), enterpriseInfo.getId());
                }
                // 判断是否为管理员
                if(roleId!=-1){
                    EntUserRole entUserRole = new EntUserRole();
                    entUserRole.setUserId(entUserInfo.getId());
                    entUserRole.setRoleId(roleId);
                    baseDao.save(entUserRole);
                }
            } else {
                msg = (String) map.get("msg");
            }
        } else {
            msg = "企业信息不存在，或已作废";
        }
        return msg;
    }

    /**
     * 保存员工信息
     *
     * @author zy 2018-9-19
     **/
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> saveEmployeeInfo(String corpId, String userId, Integer entId, String manager) throws IOException, URISyntaxException {
        Map<String, Object> map = new HashMap<>(2);
        String msg = null;
        EmployeeInfo employeeInfo;
        // 获取成员信息并保存
        employeeInfo=enterpriseService.findEmplyeeInfo(corpId,userId);
        if (employeeInfo == null) {
            Map<String,Object> employeeMap=saveInitUserInfo(employeeInfo,corpId,userId,entId);
            msg=(String)employeeMap.get("msg");
            if(StringUtils.isBlank(msg)){
                employeeInfo=(EmployeeInfo) employeeMap.get("employeeInfo");
            }
        }
        if (employeeInfo != null) {
            // 查询是否有用户信息，没有则增加用户信息
            EntUserInfo entUserInfo = enterpriseService.findUserInfo(employeeInfo.getId(), entId);
            if (entUserInfo == null) {
                entUserInfo = enterpriseService.saveUserInfo(employeeInfo.getId(), entId);
            }
            // 是否有普通用户角色，没有则创建
            EntRoleInfo roleInfo = enterpriseService.findNormalRoleInfo();
            if (roleInfo == null) {
                roleInfo = new EntRoleInfo();
                roleInfo.setName(Constant.NORMAL_NAME);
                roleInfo.setDescription("普通用户");
                baseDao.save(roleInfo);
            }
            // 查询当前用户是否有角色，没有默认为普通角色
            List<Map<String,Object>> list=enterpriseService.getUserRole(entUserInfo.getId());
            if(list==null||list.size()<=0){
                EntUserRole entUserRole=new EntUserRole();
                entUserRole.setUserId(entUserInfo.getId());
                entUserRole.setRoleId(roleInfo.getId());
                baseDao.save(entUserRole);
            }
            // 若为管理员，查询是否有角色信息，没有直接添加
            if (StringUtils.equals(manager, userId)) {
                enterpriseService.userAddManagerRole(entUserInfo.getId(),entId);
            }
            map.put("employeeInfo", employeeInfo);
            map.put("userInfo", entUserInfo);
        }
        map.put("msg", msg);
        return map;
    }

    /**
     * 获取角色用户信息
     **/
    @Transactional(rollbackFor = Exception.class)
    public List<Map<String, Object>> getRoleUser() {
        List<Map<String, Object>> list = null;
        EnterpriseInfo enterpriseInfo = enterpriseService.findEnterpriseByCorpId(Constant.corpId, null);
        if (enterpriseInfo != null) {
            list = enterpriseService.getRoleUser(enterpriseInfo.getId());
        }
        return list;
    }

}
