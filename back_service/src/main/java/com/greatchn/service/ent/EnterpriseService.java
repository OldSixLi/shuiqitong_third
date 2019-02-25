package com.greatchn.service.ent;

import com.greatchn.common.dao.BaseDao;
import com.greatchn.common.utils.Constant;
import com.greatchn.po.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企业业务层
 *
 * @author zy 2018-9-12
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class EnterpriseService {

    @Resource
    BaseDao baseDao;


    /**
     * 校验企业是否已授权应用（是否有有效的企业信息）
     */
    public EnterpriseInfo findEnterpriseByCorpId(String corpId, String type) {
        String typeStr = "all";
        String hql = "from EnterpriseInfo where corpId=?0";
        if (!StringUtils.equals(typeStr, type)) {
            hql += " and state='Y'";
        }
        EnterpriseInfo[] enterpriseInfos = baseDao.query(hql, EnterpriseInfo.class, corpId);
        return enterpriseInfos != null && enterpriseInfos.length > 0 ? enterpriseInfos[0] : null;
    }

    /**
     * 查询员工信息
     */
    public EmployeeInfo findEmplyeeInfo(String corpId, String userId) {
        EmployeeInfo employeeInfo = null;
        //若员工信息
        String sql = "select * from employee_info where ENTERPRISE_ID=? and USER_ID=?";
        List<EmployeeInfo> list = baseDao.queryBySql(sql, EmployeeInfo.class, corpId, userId);
        if (list != null && list.size() > 0) {
            employeeInfo = list.get(0);
        }
        return employeeInfo;
    }


    /**
     * 通过员工id，企业id获取用户信息表
     *
     * @author zy 2018-9-17
     */
    public EntUserInfo findUserInfo(Integer employeeId, Integer enterpriseId) {
        String sql = "select * from ent_user_info where EMPLOYEE_ID=? and ENTERPRISE_ID=? ";
        // 放入参数
        List<Object> paramter = new ArrayList<>();
        paramter.add(employeeId);
        paramter.add(enterpriseId);
        List<EntUserInfo> list = baseDao.queryBySql(sql, EntUserInfo.class, paramter.toArray());
        return list != null && list.size() > 0 ? list.get(0) : null;

    }

    /**
     * 保存用户信息
     *
     * @author zy 2018-9-17
     */
    @Transactional(rollbackFor = Exception.class)
    public EntUserInfo saveUserInfo(Integer employeeId, Integer enterpriseId) {
        EntUserInfo entUserInfo = new EntUserInfo();
        entUserInfo.setEmployeeId(employeeId);
        entUserInfo.setEnterpriseId(enterpriseId);
        entUserInfo.setState("Y");
        entUserInfo.setLastTime(new Timestamp(System.currentTimeMillis()));
        baseDao.save(entUserInfo);
        return entUserInfo;
    }

    /**
     * 查询用户的角色
     */
    public List<Map<String, Object>> getUserRole(Integer userId) {
        String sql = "select eur.ROLE_ID as roleId,eur.USER_ID as userId,eri.NAME as roleName " +
                "from ent_user_role eur join ent_user_info eui on eur.USER_ID=eui.ID " +
                "join ent_role_info eri on eur.ROLE_ID=eri.ID " +
                "where eur.USER_ID=? group by eur.ROLE_ID";
        Map<String, Type> types = new HashMap<>(16);
        types.put("roleId", IntegerType.INSTANCE);
        types.put("userId", IntegerType.INSTANCE);
        types.put("roleName", StringType.INSTANCE);
        return baseDao.queryBySQL(sql, types, userId);
    }

    /**
     * 为用户添加管理员角色
     */
    @Transactional(rollbackFor = Exception.class)
    public void userAddManagerRole(Integer userId, Integer entTd) {
        // 查询管理员角色id,角色表第一个为管理员
        String sql = "select * from ent_role_info where NAME=? for update";
        List<EntRoleInfo> list = baseDao.queryBySql(sql, EntRoleInfo.class, Constant.MANAGER_NAME);
        EntRoleInfo entRoleInfo = list != null && list.size() > 0 ? list.get(0) : null;
        if (entRoleInfo == null) {
            // 管理员角色不存在,创建管理员角色
            entRoleInfo = new EntRoleInfo();
            entRoleInfo.setName(Constant.MANAGER_NAME);
            baseDao.save(entRoleInfo);
            // 为管理员角色增加所有权限
            addAllRight(entRoleInfo.getId());
        }

        // 查询用户是否有管理员角色
        String userRoleSql = "select count(ID) as num from ent_user_role where USER_ID=? and ROLE_ID=?";
        Map<String, Type> types = new HashMap<>(1);
        types.put("num", LongType.INSTANCE);
        Map<String, Object> map = baseDao.uniqueBySQL(userRoleSql, types, userId, entRoleInfo.getId());
        long num = (long) map.get("num");
        if (num <= 0) {
            // 删除当前企业的管理员用户
            String deleteSql = "delete eur from ent_user_role eur join ent_user_info eui on eur.USER_ID=eui.ID where eur.ROLE_ID=? and eui.ENTERPRISE_ID=?";
            baseDao.executeSQL(deleteSql, entRoleInfo.getId(), entTd);
            EntUserRole userRole = new EntUserRole();
            userRole.setRoleId(entRoleInfo.getId());
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
        String rightSql = "select * from ent_right_info";
        List<EntRightInfo> rightInfos = baseDao.queryBySql(rightSql, EntRightInfo.class);
        if (rightInfos != null && rightInfos.size() > 0) {
            for (EntRightInfo entRightInfo : rightInfos) {
                EntRoleRight roleRight = new EntRoleRight();
                roleRight.setRightId(entRightInfo.getId());
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
    public List<Map<String, Object>> getUserRight(Integer userId) {
        // 根据角色查询权限
        String sql = "select err.RIGHT_ID as rightId,eri.NAME as rightName,eri.LEVEL as level " +
                "from ent_role_right err " +
                "JOIN ent_user_role eur ON eur.ROLE_ID = err.ROLE_ID " +
                "JOIN ent_role_info role ON role.ID = eur.ROLE_ID " +
                "JOIN ent_right_info eri ON err.RIGHT_ID = eri.ID " +
                "where eur.USER_ID=? group by err.RIGHT_ID";
        Map<String, Type> types = new HashMap<>(8);
        //权限id
        types.put("rightId", IntegerType.INSTANCE);
        // 权限名称
        types.put("rightName", StringType.INSTANCE);
        //权限级别
        types.put("level", StringType.INSTANCE);
        return baseDao.queryBySQL(sql, types, userId);
    }

    /**
     * 查询除管理员外的四个角色,以及这四个角色分配的用户
     */
    public List<Map<String, Object>> getRoleUser(int entId) {
        List<Map<String, Object>> result = null;
        String sql = "select * from ent_role_info where NAME<>? and NAME<>?";
        String[] names = {Constant.MANAGER_NAME, Constant.NORMAL_NAME};
        List<EntRoleInfo> list = baseDao.queryBySql(sql, EntRoleInfo.class, names);
        if (list != null && list.size() > 0) {
            String userSql = "select eri.Id as roleId," +
                    "eri.`NAME` as roleName," +
                    "eri.DESCRIPTION as description," +
                    "a.userId," +
                    "a.userName " +
                    "from ent_role_info eri " +
                    "LEFT JOIN (select eur.ROLE_ID as roleId,ei.USER_ID as userId,ei.`NAME` as userName from ent_user_role eur " +
                    "JOIN ent_user_info eui ON eur.USER_ID = eui.ID and eui.ENTERPRISE_ID=? " +
                    "JOIN employee_info ei ON ei.ID = eui.EMPLOYEE_ID) as a ON a.roleId=eri.ID " +
                    "where eri.`NAME`<>'" + Constant.MANAGER_NAME + "' and eri.`NAME`<>'" + Constant.NORMAL_NAME + "' GROUP BY eri.ID";
            Map<String, Type> types = new HashMap<>(5);
            // 角色id
            types.put("roleId", IntegerType.INSTANCE);
            // 角色名称
            types.put("roleName", StringType.INSTANCE);
            // 角色描述
            types.put("description", StringType.INSTANCE);
            // 角色用户企业微信userId
            types.put("userId", StringType.INSTANCE);
            // 用户名称
            types.put("userName", StringType.INSTANCE);
            // 放入参数
            List<Object> paramter = new ArrayList<>();
            paramter.add(entId);
            result = baseDao.queryBySQL(userSql, types, paramter.toArray());
        }
        return result;
    }


    /**
     * 获取普通用户角色信息
     */
    public EntRoleInfo findNormalRoleInfo() {
        String sql = "select * from ent_role_info where NAME=?";
        List<EntRoleInfo> list = baseDao.queryBySql(sql, EntRoleInfo.class, Constant.NORMAL_NAME);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 获取用户拥有的角色信息
     */
    @Transactional(rollbackFor = Exception.class)
    public List<Map<String, Object>> findUserRoleInfos(Integer id) {
        String sql = "select eri.ID as roleId,eri.`NAME` as roleName,eri.DESCRIPTION as description from ent_user_role eur join ent_role_info eri on eur.ROLE_ID=eri.ID where eur.USER_ID=? GROUP BY eri.ID";
        Map<String, Type> types = new HashMap<>(3);
        types.put("roleId", IntegerType.INSTANCE);
        types.put("roleName", StringType.INSTANCE);
        types.put("description", StringType.INSTANCE);
        List<Map<String, Object>> list = baseDao.queryBySQL(sql, types, id);
        return list;
    }

    /**
     * 根据企业id获取企业信息
     */
    public EnterpriseInfo findEnterpriseInfoByEntId(Integer entId) {
        return baseDao.get(EnterpriseInfo.class, entId);
    }


    /**
     * 校验是否已授权过
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveEnterpriseInfoByAuth(EnterpriseInfo newEnterpriseInfo) {
        // 校验是否已存在有效的企业
        EnterpriseInfo enterprisseInfo = findEnterpriseByCorpId(newEnterpriseInfo.getCorpId(), null);

        // 若存在，删除原有企业，并将

    }
}
