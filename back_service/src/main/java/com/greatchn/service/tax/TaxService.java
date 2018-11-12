package com.greatchn.service.tax;

import com.alibaba.fastjson.JSONObject;
import com.greatchn.bean.Page;
import com.greatchn.bean.PageData;
import com.greatchn.common.dao.BaseDao;
import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.HttpUtils;
import com.greatchn.po.*;
import com.greatchn.service.wechat.AccessTokenService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.type.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 税务分局信息业务层
 *
 * @author zy 2018-10-12
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class TaxService {

    @Resource
    BaseDao baseDao;

    @Resource
    AccessTokenService accessTokenService;

    /**
     * 通过id,查询税务分局信息
     *
     * @author zy 2018-9-17
     */
    public TaxInfo getTaxInfoById(Integer id) {
        return baseDao.get(TaxInfo.class, id);
    }


    /**
     * 更具员工id查询员工
     */
    public EmployeeInfo getEmployeeInfoById(Integer id) {
        return baseDao.get(EmployeeInfo.class, id);
    }

    /**
     * 删除userInfo表信息
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserInfo(TaxUserInfo userInfo) {
        baseDao.remove(userInfo);
    }

    /**
     * 更具corpId获取税务分局信息
     */
    public TaxInfo findTaxInfoByCorpId(String corpId, String type) {
        String typeStr = "all";
        String hql = "from TaxInfo where corpId=?0";
        if (!StringUtils.equals(typeStr, type)) {
            hql += " and state='Y'";
        }
        TaxInfo[] taxInfos = baseDao.query(hql, TaxInfo.class, corpId);
        return taxInfos != null && taxInfos.length > 0 ? taxInfos[0] : null;
    }

    /**
     * 通过员工id，税务分局企业id获取用户信息
     *
     * @author zy 2018-9-17
     */
    public TaxUserInfo findUserInfo(Integer employeeId, Integer enterpriseId) {
        String sql = "select * from tax_user_info where EMPLOYEE_ID=? and TAX_ID=? ";
        // 放入参数
        List<Object> parameters = new ArrayList<>();
        parameters.add(employeeId);
        parameters.add(enterpriseId);
        List<TaxUserInfo> list = baseDao.queryBySql(sql, TaxUserInfo.class, parameters.toArray());
        return list != null && list.size() > 0 ? list.get(0) : null;

    }

    /**
     * 通过用户id获取用户信息
     *
     * @author zy 2018-10-26
     */
    public TaxUserInfo findUserInfoById(Integer id, String type) {
        String sql;
        String typeKey = "update";
        if (StringUtils.equals(type, typeKey)) {
            sql = "select * from tax_user_info where ID=? for update";
        } else {
            sql = "select * from tax_user_info where ID=?";
        }
        // 放入参数
        List<Object> parameters = new ArrayList<>();
        parameters.add(id);
        List<TaxUserInfo> list = baseDao.queryBySql(sql, TaxUserInfo.class, parameters.toArray());
        return list != null && list.size() > 0 ? list.get(0) : null;
    }


    /**
     * 根据登录账号查询用户
     */
    public TaxUserInfo findUserInfo(String loginAccount) {
        String sql = "select * from tax_user_info where LOGIN_ACCOUNT=?";
        List<TaxUserInfo> list = baseDao.queryBySql(sql, TaxUserInfo.class, loginAccount);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 根据角色id查询角色信息
     **/
    public TaxRoleInfo findTaxRoleInfoById(Integer roleId, String type) {
        String update = "update";
        if (StringUtils.equals(update, type)) {
            String sql = "select * from tax_role_info where ID=? for update";
            List<TaxRoleInfo> list = baseDao.queryBySql(sql, TaxRoleInfo.class, roleId);
            return list != null && list.size() > 0 ? list.get(0) : null;
        } else {
            return baseDao.get(TaxRoleInfo.class, roleId);
        }
    }

    /**
     * 保存用户信息
     *
     * @author zy 2018-9-17
     */
    @Transactional(rollbackFor = Exception.class)
    public TaxUserInfo saveUserInfo(Integer employeeId, Integer taxId) {
        TaxUserInfo taxUserInfo = new TaxUserInfo();
        taxUserInfo.setEmployeeId(employeeId);
        taxUserInfo.setTaxId(taxId);
        taxUserInfo.setState("Y");
        taxUserInfo.setLastTime(new Timestamp(System.currentTimeMillis()));
        baseDao.save(taxUserInfo);
        return taxUserInfo;
    }

    /**
     * 查询用户的角色
     */
    public List<Map<String, Object>> getUserRole(Integer userId, Integer taxId) {
        List<Object> parameters = new ArrayList<>();
        parameters.add(taxId);
        parameters.add(userId);
        String sql = "select tur.ROLE_ID as roleId,tur.USER_ID as userId,tri.DESCRIPTION as roleName " +
                "from tax_user_role tur join tax_user_info tui on tur.USER_ID=tui.ID " +
                "join tax_role_info tri on tur.ROLE_ID=tri.ID and (tri.TAX_ID=0 or tri.TAX_ID=?) " +
                "where tur.USER_ID=? group by tur.ROLE_ID";
        Map<String, Type> types = new HashMap<>(16);
        types.put("roleId", IntegerType.INSTANCE);
        types.put("userId", IntegerType.INSTANCE);
        types.put("roleName", StringType.INSTANCE);
        return baseDao.queryBySQL(sql, types, parameters.toArray());
    }

    /**
     * 查询用户是否已有某个角色
     */
    public boolean userHaveRole(Integer roleId, Integer userId) {
        List<Object> parameters = new ArrayList<>();
        boolean flag = false;
        String sql = "select * from tax_user_role where ROLE_ID=? and USER_ID=? for update";
        parameters.add(roleId, userId);
        List<TaxUserRole> list = baseDao.queryBySql(sql, TaxUserRole.class, parameters.toArray());
        if (list != null && list.size() > 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * 查询管理员角色信息
     */
    public TaxRoleInfo findManangerRoleInfo() {
        // 查询管理员角色id,角色表第一个为管理员
        String sql = "select * from tax_role_info where NAME=? and TAX_ID=0 for update";
        List<TaxRoleInfo> list = baseDao.queryBySql(sql, TaxRoleInfo.class, Constant.MANAGER_NAME);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 为用户添加管理员角色
     */
    @Transactional(rollbackFor = Exception.class)
    public void userAddManagerRole(Integer userId, Integer taxId) {
        // 查询管理员角色id,角色表第一个为管理员
        TaxRoleInfo taxRoleInfo = findManangerRoleInfo();
        if (taxRoleInfo == null) {
            // 管理员角色不存在,创建管理员角色
            taxRoleInfo = new TaxRoleInfo();
            taxRoleInfo.setName(Constant.MANAGER_NAME);
            taxRoleInfo.setTaxId(0);
            baseDao.save(taxRoleInfo);
            // 为管理员角色增加所有权限
            addAllRight(taxRoleInfo.getId());
        }

        // 查询用户是否有管理员角色
        String userRoleSql = "select count(ID) as num from tax_user_role where USER_ID=? and ROLE_ID=?";
        Map<String, Type> types = new HashMap<>(1);
        types.put("num", LongType.INSTANCE);
        Map<String, Object> map = baseDao.uniqueBySQL(userRoleSql, types, userId, taxRoleInfo.getId());
        long num = (long) map.get("num");
        if (num <= 0) {
            // 删除当前企业的管理员用户
            String deleteSql = "delete tur from tax_user_role tur join tax_user_info tui on tur.USER_ID=tui.ID where tur.ROLE_ID=? and tui.TAX_ID=?";
            baseDao.executeSQL(deleteSql, taxRoleInfo.getId(), taxId);
            TaxUserRole userRole = new TaxUserRole();
            userRole.setRoleId(taxRoleInfo.getId());
            userRole.setUserId(userId);
            baseDao.save(userRole);
        }
    }

    /**
     * 为角色增加所有权限
     */
    @Transactional(rollbackFor = Exception.class)
    public void addAllRight(Integer roleId) {
        // 增加权限
        String rightSql = "select * from tax_right_info";
        List<TaxRightInfo> rightInfos = baseDao.queryBySql(rightSql, TaxRightInfo.class);
        if (rightInfos != null && rightInfos.size() > 0) {
            for (TaxRightInfo taxRightInfo : rightInfos) {
                TaxRoleRight roleRight = new TaxRoleRight();
                roleRight.setRightName(taxRightInfo.getName());
                roleRight.setRoleId(roleId);
                baseDao.save(roleRight);
            }
        }
    }

    /**
     * 根据用户id获取用户权限
     *
     * @param userId 用户表主键id
     */
    public List<Map<String, Object>> getUserRight(Integer userId, Integer taxId) {
        List<Object> parameters = new ArrayList<>();
        parameters.add(taxId);
        parameters.add(userId);
        // 根据角色查询权限
        String sql = "select tri.NAME as rightName,tri.LEVEL as level " +
                "from tax_role_right trr " +
                "JOIN tax_user_role tur ON tur.ROLE_ID = trr.ROLE_ID " +
                "JOIN tax_role_info role ON role.ID = tur.ROLE_ID and (role.TAX_ID=0 or role.TAX_ID=?) " +
                "JOIN tax_right_info tri ON trr.RIGHT_NAME = tri.NAME " +
                "where tur.USER_ID=? group by trr.RIGHT_NAME";
        Map<String, Type> types = new HashMap<>(8);
        // 权限名称
        types.put("rightName", StringType.INSTANCE);
        //权限级别
        types.put("level", StringType.INSTANCE);
        return baseDao.queryBySQL(sql, types, parameters.toArray());
    }


    /**
     * 获取普通用户角色信息
     */
    public TaxRoleInfo findNormalRoleInfo(Integer taxId, String type) {
        String forUpdate = "update";
        String sql = "select * from tax_role_info where NAME=? and (TAX_ID=0 or TAX_ID=?) ";
        if (StringUtils.equals(forUpdate, type)) {
            sql += "for update";
        }
        List<TaxRoleInfo> list = baseDao.queryBySql(sql, TaxRoleInfo.class, Constant.NORMAL_NAME, taxId);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 获取用户拥有的角色信息
     */
    @Transactional(rollbackFor = Exception.class)
    public List<Map<String, Object>> findUserRoleInfos(Integer id, Integer taxId) {
        List<Object> parameters = new ArrayList<>();
        parameters.add(taxId);
        parameters.add(id);
        String sql = "select tri.ID as roleId,tri.`NAME` as roleName,tri.DESCRIPTION as description from tax_user_role tur join tax_role_info tri on tur.ROLE_ID=tri.ID and (tri.TAX_ID=0 or tri.TAX_ID=?) where tur.USER_ID=? GROUP BY tri.ID";
        Map<String, Type> types = new HashMap<>(3);
        types.put("roleId", IntegerType.INSTANCE);
        types.put("roleName", StringType.INSTANCE);
        types.put("description", StringType.INSTANCE);
        return baseDao.queryBySQL(sql, types, parameters.toArray());
    }


    private static final String GET_USER_DETAIL_INFO_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token=%s&userid=%s";

    /**
     * 保存员工初始化信息（employee\ent_user_info）表中的信息
     *
     * @author zy 2018-9-20
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> saveInitUserInfo(EmployeeInfo employeeInfo, String corpId, String userId, Integer taxId) throws IOException, URISyntaxException {
        Map<String, Object> map = new HashMap<>(2);
        // 若员工信息不存在，调用微信接口获取成员信息，并进行保存
        if (employeeInfo == null) {
            Map<String, Object> accessTokeMap = null;
            String accessToken;
            if (Constant.IS_SELF_BUILT_LOGIN) {
                accessToken = accessTokenService.getAccessTokenBySelfBuilt("phone");
            } else {
                accessTokeMap = accessTokenService.getAccessTokenByPermanentCode(corpId, Constant.GET_INFO_TYPE_TAX);
                // 获取当前企业token
                accessToken = (String) accessTokeMap.get("accessToken");
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
                saveUserInfo(employeeInfo.getId(), taxId);
                map.put("employeeInfo", employeeInfo);
            } else {
                // 微信接口返回错误信息
                String msg = jsonObject == null ? "访问企业微信接口失败,请重试" : "访问企业微信错误：" + jsonObject.getString(errCodeKey) + "," + jsonObject.getString(Constant.WECHAT_ERROR_MSG_KEY);
                map.put("msg", msg);
            }
        }
        return map;
    }


    /**
     * 查询当前登录用户的税务分局的用户
     *
     * @author zy 2018-10-29
     */
    public List<Map<String, Object>> getTaxUserInfoList(Page page) {
        String exclude = "N";
        String haveAccount = "Y";
        String notHaveAccount = "N";
        PageData pd = page.getPd();
        // 账号设置状态
        String loginAccountState = pd.getString("loginAccountState");
        // 所属分局
        Integer taxId = (Integer) pd.get("taxId");
        // 用户名称
        String userName = pd.getString("userName");
        // 用户手机号
        String phone = pd.getString("phone");
        // 角色
        String roleId = pd.getString("roleId");
        String manager = null;
        // 是否包含管理员，查询税务分局信息
        String includeState = pd.getString("includeState");
        if (StringUtils.equals(exclude, includeState)) {
            TaxInfo taxInfo = getTaxInfoById(taxId);
            manager = taxInfo.getManager();
        }
        List<Object> parameters = new ArrayList<>();
        String sql = "select tui.ID,tui.TAX_ID,tui.STATE,tui.LAST_TIME,tui.LOGIN_ACCOUNT,if(tui.`PASSWORD` is not null AND TRIM(tui.`PASSWORD`)<>'','Y','N') as HAVE_PW,tui.EMPLOYEE_ID,ei.USER_ID,ei.NAME,ei.MOBILE,ei.GENDER,ei.AVATAR,ei.ENTERPRISE_ID,GROUP_CONCAT(DISTINCT trir.DESCRIPTION) AS roleNames ";
        String countSql = "select count(tui.ID) as num ";
        String conditionalSql = "from tax_user_info tui join employee_info ei on tui.EMPLOYEE_ID=ei.ID ";
        // 具体信息的查询sql连接相应的表
        sql += conditionalSql + "LEFT JOIN tax_user_role turr ON turr.USER_ID = tui.ID LEFT JOIN tax_role_info trir ON turr.ROLE_ID = trir.ID ";
        countSql += conditionalSql;
        conditionalSql = "";
        if (StringUtils.isNotBlank(roleId)) {
            conditionalSql += "LEFT JOIN tax_user_role tur ON tur.USER_ID=tui.ID where tur.ROLE_ID=? and tui.STATE='Y' and tui.TAX_ID=? ";
            parameters.add(Integer.valueOf(roleId));
        } else {
            conditionalSql += "where tui.STATE='Y' and tui.TAX_ID=? ";
        }
        parameters.add(taxId);
        if (StringUtils.equals(loginAccountState, haveAccount)) {
            conditionalSql += "and tui.LOGIN_ACCOUNT is not null and TRIM(tui.LOGIN_ACCOUNT)<>'' ";
        } else if (StringUtils.equals(loginAccountState, notHaveAccount)) {
            conditionalSql += "and (tui.LOGIN_ACCOUNT is null or TRIM(tui.LOGIN_ACCOUNT)='') ";
        }
        if (StringUtils.isNotEmpty(manager)) {
            conditionalSql += "and ei.USER_ID<>? ";
            parameters.add(manager);
        }
        if (StringUtils.isNotEmpty(userName)) {
            conditionalSql += "and ei.NAME like ? ";
            parameters.add("%" + userName + "%");
        }
        if (StringUtils.isNotEmpty(phone)) {
            conditionalSql += "and ei.MOBILE=? ";
            parameters.add(phone);
        }
        sql += conditionalSql + "GROUP BY tui.ID";
        countSql += conditionalSql;
        Map<String, Type> types = new HashMap<>(16);
        types.put("ID", IntegerType.INSTANCE);
        types.put("EMPLOYEE_ID", IntegerType.INSTANCE);
        types.put("TAX_ID", IntegerType.INSTANCE);
        types.put("STATE", StringType.INSTANCE);
        types.put("LAST_TIME", TimestampType.INSTANCE);
        types.put("LOGIN_ACCOUNT", StringType.INSTANCE);
        types.put("HAVE_PW", StringType.INSTANCE);
        types.put("USER_ID", StringType.INSTANCE);
        types.put("NAME", StringType.INSTANCE);
        types.put("MOBILE", StringType.INSTANCE);
        types.put("GENDER", StringType.INSTANCE);
        types.put("AVATAR", StringType.INSTANCE);
        types.put("ENTERPRISE_ID", StringType.INSTANCE);
        types.put("roleNames", StringType.INSTANCE);
        Map<String, Type> countTypes = new HashMap<>();
        countTypes.put("num", IntegerType.INSTANCE);
        Map<String, Object> map = baseDao.uniqueBySQL(countSql, countTypes, parameters.toArray());
        int num = map != null ? (Integer) map.get("num") : 0;
        // 放入总记录数
        page.setTotalResult(num);
        return baseDao.queryBySQL(sql, types, page.getCurrentPage(), page.getShowCount(), parameters.toArray());
    }

}
