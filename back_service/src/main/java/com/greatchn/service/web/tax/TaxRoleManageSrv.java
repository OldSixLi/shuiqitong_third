package com.greatchn.service.web.tax;

import com.greatchn.common.dao.BaseDao;
import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.po.*;
import com.greatchn.service.AuditService;
import com.greatchn.service.ent.EnterpriseService;
import com.greatchn.service.tax.TaxService;
import com.greatchn.service.wechat.AccessTokenService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.*;

/**
 * 税务分局系统业务层
 *
 * @author zy 2018-10-12
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class TaxRoleManageSrv {


    @Resource
    BaseDao baseDao;

    @Resource
    TaxService taxService;

    @Resource
    RedisUtils redisUtils;

    @Resource
    EnterpriseService enterpriseService;

    @Resource
    AccessTokenService accessTokenService;

    @Resource
    AuditService auditService;

    /**
     * 角色列表
     */
    public List<TaxRoleInfo> getRoleList(Integer taxId, String type) {
        String excludeType = "exclude";
        String sql = "select * from tax_role_info where TAX_ID=0 or TAX_ID=? ";
        if (StringUtils.equals(excludeType, type)) {
            sql += " and NAME='" + Constant.MANAGER_NAME + "' ";
        }
        return baseDao.queryBySql(sql, TaxRoleInfo.class, taxId);
    }

    /**
     * 查询某个角色或系统所有的权限
     */
    public List<TaxRoleRight> getRoleRights(Integer roleId) {
        String sql = "select trr.* from tax_role_right trr join tax_right_info tri on trr.RIGHT_NAME=tri.NAME ";
        List<Object> parameters = new ArrayList<>();
        if (roleId != null) {
            sql += "where trr.ROLE_ID=? ";
            parameters.add(roleId);
        }
        return baseDao.queryBySql(sql, TaxRoleRight.class, parameters.toArray());
    }

    /**
     * 查询某个角色或系统所有的权限
     */
    public List<TaxRoleRight> getRoleAllRights(Integer roleId) {
        String sql = "select trr.* from tax_role_right trr left join tax_right_info tri on trr.RIGHT_NAME=tri.NAME ";
        List<Object> parameters = new ArrayList<>();
        if (roleId != null) {
            sql += "where trr.ROLE_ID=? ";
            parameters.add(roleId);
        }
        return baseDao.queryBySql(sql, TaxRoleRight.class, parameters.toArray());
    }


    /**
     * 增加角色
     */
    @Transactional(rollbackFor = Exception.class)
    public String saveOrUpdateRole(String roleName, Integer roleId, Integer taxId, Integer userId) {
        String msg = null;
        if (roleId != null) {
            // 查询已存在的角色信息
            TaxRoleInfo taxRoleInfo = taxService.findTaxRoleInfoById(roleId, "update");
            if (taxRoleInfo != null) {
                if (!StringUtils.equals(roleName, taxRoleInfo.getName())) {
                    // 查询该角色
                    msg = checkRoleName(roleName, taxId);
                    if (StringUtils.isBlank(msg)) {
                        taxRoleInfo.setName(roleName);
                        taxRoleInfo.setDescription(roleName);
                        taxRoleInfo.setModifier(userId);
                        taxRoleInfo.setModifyTime(new Timestamp(System.currentTimeMillis()));
                        baseDao.update(taxRoleInfo);
                    }
                }
            } else {
                msg = "该角色不存在";
            }
        } else {
            // 查询该角色
            msg = checkRoleName(roleName, taxId);
            if (StringUtils.isBlank(msg)) {
                TaxRoleInfo roleInfo = new TaxRoleInfo();
                roleInfo.setTaxId(taxId);
                roleInfo.setName(roleName);
                roleInfo.setDescription(roleName);
                roleInfo.setCreator(userId);
                roleInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
                baseDao.save(roleInfo);
            }
        }
        return msg;
    }


    /**
     * 获取角色名是否存在
     */
    @Transactional(rollbackFor = Exception.class)
    public String checkRoleName(String roleName, Integer taxId) {
        String msg = null;
        List<Object> parameters = new ArrayList<>();
        parameters.add(roleName);
        parameters.add(roleName);
        parameters.add(taxId);
        // 查询是否有相同的角色名称
        String sql = "select count(ID) as num from tax_role_info where NAME=? or DESCRIPTION=? and (TAX_ID=0 or TAX_ID=?)";
        Map<String, Type> types = new HashMap<>(1);
        types.put("num", IntegerType.INSTANCE);
        Map<String, Object> map = baseDao.uniqueBySQL(sql, types, parameters.toArray());
        Integer num = (Integer) map.get("num");
        if (num > 0) {
            msg = "该角色已存在，请重新输入";
        }
        return msg;
    }

    /**
     * 保存/更新角色的权限
     *
     * @param rightNames 逗号分隔的权限id
     * @author zy 2018-10-29
     */
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public String saveOrUpdateRoleRight(Integer roleId, String rightNames, Integer userId) {
        String msg = null;
        // 更新角色的修改人/修改时间
        TaxRoleInfo taxRoleInfo = taxService.findTaxRoleInfoById(roleId, "update");
        if (taxRoleInfo != null) {
            Map<String, Object> managerRightsMap = getRightSet("");
            Set<String> set = (Set<String>) managerRightsMap.get("set");
            // 查询角色原有的所有权限
            List<TaxRoleRight> list = getRoleAllRights(roleId);
            // 添加所有角色都拥有的权限
            if (StringUtils.isNotBlank(rightNames)) {
                rightNames += "," + Constant.TAX_ALL_ROLE_HAVE_RIGHT;
            } else {
                rightNames = Constant.TAX_ALL_ROLE_HAVE_RIGHT;
            }
            Map<String, Object> rightsMap = StringUtils.isNotBlank(rightNames) ? getRightSet(rightNames) : null;
            // 传入权限
            String[] rightNameArray = rightsMap != null ? (String[]) rightsMap.get("rightNames") : null;
            if (rightNameArray != null && rightNameArray.length > 0) {
                int flagNum;
                int updateNum = 0;
                if (list.size() <= rightNames.length()) {
                    flagNum = list.size();
                } else {
                    flagNum = rightNameArray.length;
                }
                for (int i = 0; i < rightNameArray.length; i++) {
                    if (!set.contains(rightNameArray[i])) {
                        if (updateNum <= flagNum && updateNum < list.size()) {
                            //更新
                            TaxRoleRight taxRoleRight = list.get(updateNum);
                            taxRoleRight.setRightName(rightNameArray[i]);
                            baseDao.update(taxRoleRight);
                        } else {
                            //新增
                            TaxRoleRight taxRoleRight = new TaxRoleRight();
                            taxRoleRight.setRoleId(roleId);
                            taxRoleRight.setRightName(rightNameArray[i]);
                            baseDao.save(taxRoleRight);
                        }
                        updateNum++;
                    }
                }
                if (list.size() > updateNum) {
                    // 删除多余的权限
                    for (int j = (list.size() - updateNum - 1); j < list.size(); j++) {
                        baseDao.remove(list.get(j));
                    }
                }
            } else {
                // 删除所有权限
                deleteAllRight(roleId);
            }
            taxRoleInfo.setModifier(userId);
            taxRoleInfo.setModifyTime(new Timestamp(System.currentTimeMillis()));
            baseDao.update(taxRoleInfo);
        } else {
            msg = "该角色不存在";
        }
        return msg;
    }

    /**
     * 获取不能被除管理员外的角色添加的权限
     *
     * @author zy 2018-10-30
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getRightSet(String str) {
        Map<String, Object> map = new HashMap<>(2);
        Set<String> set = new HashSet<>();
        if (StringUtils.isEmpty(str)) {
            str = Constant.TAX_CANNNOT_ADD_TO_ROLE_OTHIER_THAN_MANAGER;
        }
        String[] rightNames = StringUtils.isNotBlank(str) ? str.split(",") : null;
        if (rightNames != null && rightNames.length > 0) {
            Collections.addAll(set, rightNames);
            if (StringUtils.isNotEmpty(str) && set.size() < rightNames.length) {
                rightNames = set.toArray(new String[0]);
            }
        }
        map.put("set", set);
        if (StringUtils.isNotEmpty(str)) {
            map.put("rightNames", rightNames);
        }
        return map;
    }


    /**
     * 删除角色的所有权限
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllRight(Integer roleId) {
        String sql = "delete trr from tax_role_right trr left join tax_role_info tri on trr.ROLE_ID=tri.ID where trr.ROLE_ID=? and tri.TAX_ID<>0";
        baseDao.executeSQL(sql, roleId);
    }

    /**
     * 删除角色
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Integer roleId) {
        // 删除角色
        String sql = "delete from tax_role_info where ID=? and TAX_ID<>0";
        int result = baseDao.executeSQL(sql, roleId);
        if (result > 0) {
            // 删除权限
            deleteAllRight(roleId);
            // 删除用户角色的关联关系
            deleteUserRole(roleId);
        }
    }

    /**
     * 查询是否为删除/可修改权限的角色
     */
    public boolean isCannotModifyRoleId(Integer roleId, Integer taxId, String type) {
        String modifyRight = "modifyRight";
        List<Object> parameters = new ArrayList<>();
        boolean flag = false;
        String sql;
        if (StringUtils.equals(modifyRight, type)) {
            // 不可修改权限的角色
            sql = "select * from tax_role_info where NAME=? and (TAX_ID=0 or TAX_ID=?)";
            parameters.add(Constant.MANAGER_NAME);
            parameters.add(taxId);
        } else {
            sql = "select * from tax_role_info where (NAME=? or NAME=?) and (TAX_ID=0 or TAX_ID=?)";
            parameters.add(Constant.MANAGER_NAME);
            parameters.add(Constant.NORMAL_NAME);
            parameters.add(taxId);
        }
        List<TaxRoleInfo> list = baseDao.queryBySql(sql, TaxRoleInfo.class, parameters.toArray());
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                TaxRoleInfo taxRoleInfo = list.get(i);
                if (taxRoleInfo.getId().equals(roleId)) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    /**
     * 删除角色的用户关联关系
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserRole(Integer roleId) {
        String sql = "delete from tax_user_role where ROLE_ID=?";
        baseDao.executeSQL(sql, roleId);
    }


    /**
     * 传入的角色剔除管理员
     */
    public Map<String, Object> roleIdsHandler(String roleIds, Integer taxId) {
        Map<String, Object> map = new HashMap<>(3);
        String managerRoleId = null;
        TaxRoleInfo taxManagerRoleInfo = taxService.findManangerRoleInfo();
        if (taxManagerRoleInfo != null) {
            managerRoleId = taxManagerRoleInfo.getId().toString();
        }
        // 查询普通用户角色
        TaxRoleInfo taxNormalRoleInfo = taxService.findNormalRoleInfo(taxId, null);
        Integer normalRoleId = taxNormalRoleInfo != null ? taxNormalRoleInfo.getId() : null;
        boolean flag = false;
        String[] roleIdArray = StringUtils.isNoneBlank(roleIds) ? roleIds.split(",") : null;
        Set<Integer> roleIdSet = new HashSet<>();
        if (roleIdArray != null && roleIdArray.length > 0) {
            for (String roleIdStr : roleIdArray) {
                if (!StringUtils.equals(managerRoleId, roleIdStr)) {
                    Integer roleId = Integer.valueOf(roleIdStr);
                    roleIdSet.add(roleId);
                    if (normalRoleId != null && normalRoleId.equals(roleId)) {
                        flag = true;
                    }
                }
            }
        }
        if (!flag && taxNormalRoleInfo != null) {
            // 增加普通用户角色id
            roleIdSet.add(normalRoleId);
        }
        map.put("taxManagerRoleInfo", taxManagerRoleInfo);
        map.put("taxNormalRoleInfo", taxNormalRoleInfo);
        map.put("set", roleIdSet);
        return map;
    }

    /**
     * 查询当前用户的关联关系
     */
    public List<TaxUserRole> getTaxUserRole(Integer userId, Integer managerRoleId) {
        List<Object> parameters = new ArrayList<>();
        String sql = "select * from tax_user_role where USER_ID=? ";
        parameters.add(userId);
        if (managerRoleId != null) {
            sql += "and ROLE_ID<>?";
            parameters.add(managerRoleId);
        }
        return baseDao.queryBySql(sql, TaxUserRole.class, parameters.toArray());
    }

    /**
     * 为用户增加某个角色
     */
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public String saveOrUpdateUserRoles(String roleIds, Integer userId, Integer taxId) {
        String msg = null;
        // 查询用户是否存在
        TaxUserInfo taxUserInfo = taxService.findUserInfoById(userId, "update");
        if (taxUserInfo == null) {
            msg = "该用户不存在";
        } else {
            // 剔除管理员角色和重复的角色
            Map<String, Object> map = roleIdsHandler(roleIds, taxId);
            Set<Integer> roleIdSet = (Set<Integer>) map.get("set");
            TaxRoleInfo taxManagerRoleInfo = (TaxRoleInfo) map.get("taxManagerRoleInfo");
            Integer managerRoleId = taxManagerRoleInfo == null ? null : taxManagerRoleInfo.getId();
            // 查询用户已有的角色信息
            List<TaxUserRole> oldUserRoleList = getTaxUserRole(userId, managerRoleId);
            // 获取系统总该税务分局的所有角色
            List<TaxRoleInfo> roles = getRoleList(taxId, null);

            Set<Integer> sysRoles = new HashSet<>();
            for (TaxRoleInfo roleInfo : roles) {
                sysRoles.add(roleInfo.getId());
            }
            if (roleIdSet.size() > 0) {
                int flagNum;
                int updateNum = 0;
                if (oldUserRoleList.size() <= roleIdSet.size()) {
                    flagNum = oldUserRoleList.size();
                } else {
                    flagNum = roleIdSet.size();
                }
                for (Integer roleId : roleIdSet) {
                    if (sysRoles.contains(roleId)) {
                        if (updateNum <= flagNum && updateNum < oldUserRoleList.size()) {
                            //更新
                            TaxUserRole taxUserRole = oldUserRoleList.get(updateNum);
                            taxUserRole.setRoleId(roleId);
                            baseDao.update(taxUserRole);
                        } else {
                            //新增
                            TaxUserRole taxUserRole = new TaxUserRole();
                            taxUserRole.setRoleId(roleId);
                            taxUserRole.setUserId(userId);
                            baseDao.save(taxUserRole);
                        }
                        updateNum++;
                    }
                }
                if (oldUserRoleList.size() > updateNum) {
                    // 删除多余的权限
                    for (int j = (oldUserRoleList.size() - updateNum); j < oldUserRoleList.size(); j++) {
                        baseDao.remove(oldUserRoleList.get(j));
                    }
                }
            } else {
                // 删除所有角色
                for (int j = 0; j < oldUserRoleList.size(); j++) {
                    baseDao.remove(oldUserRoleList.get(j));
                }
            }
        }
        return msg;
    }


    /**
     * 保存用户初始化信息
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> initEmployeeInfoAndTransform(String corpId, String userId, Integer taxId) throws IOException, URISyntaxException {
        // 不存在，访问微信接口获取员工信息，保存新的员工
        Map<String, Object> resultMap = new HashMap<>(2);
        String msgKey = "msg";
        TaxInfo taxInfo=taxService.getTaxInfoById(taxId);
        if(taxInfo!=null&&StringUtils.equals(Constant.EFFECTIVE_STATE,taxInfo.getState())){
            // 查询员工信息是否存在
            EmployeeInfo employeeInfo = enterpriseService.findEmplyeeInfo(corpId, userId);
            if (employeeInfo == null) {
                Map<String, Object> map = null;
                // 获取当前企业token
                String accessToken;
                if (Constant.IS_SELF_BUILT_LOGIN) {
                    accessToken = accessTokenService.getAccessTokenBySelfBuilt("phone");
                } else {
                    map = accessTokenService.getAccessTokenByPermanentCode(corpId, Constant.GET_INFO_TYPE_TAX);
                    // 获取当前企业token
                    accessToken = (String) map.get("accessToken");
                }
                if (StringUtils.isNotEmpty(accessToken)) {
                    Map<String, Object> employeeMap = taxService.saveInitUserInfo(employeeInfo, corpId, userId, taxId);
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
                // 保存税务分局管理员
                taxInfo.setManager(userId);
                baseDao.save(taxInfo);
                // 查询是否有用户信息，没有则增加用户信息
                TaxUserInfo taxUserInfo = taxService.findUserInfo(employeeInfo.getId(), taxId);
                if (taxUserInfo == null) {
                    taxUserInfo = taxService.saveUserInfo(employeeInfo.getId(), taxId);
                }
                // 查询当前用户是否有角色，若没有，增加当前用户角色为普通用户
                List<Map<String, Object>> userRoleInfos = taxService.getUserRole(taxUserInfo.getId(), taxId);
                if (userRoleInfos == null || userRoleInfos.size() <= 0) {
                    // 查询普通用户角色信息
                    TaxRoleInfo taxRoleInfo = taxService.findNormalRoleInfo(taxId, "update");
                    if (taxRoleInfo == null) {
                        taxRoleInfo = new TaxRoleInfo();
                        taxRoleInfo.setName(Constant.NORMAL_NAME);
                        taxRoleInfo.setDescription(Constant.NORMAL_DESCRIPTION);
                        taxRoleInfo.setTaxId(taxId);
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
                // 保存新的管理员信息
                taxService.userAddManagerRole(taxUserInfo.getId(),taxId);
                // 更新缓存中管理员所属的税务分局信息
                Map<String, Object> taxMap = new HashMap<>(2);
                taxMap.put("taxInfo", taxInfo);
                // 查询该税务分局对应的审核信息
                AuditInfo auditInfo=auditService.getAuditInfoByEntTaxId(taxId,null,"2");
                taxMap.put("auditInfo", auditInfo);
                String taxInfoKey = RedisUtils.REDIS_PREFIX_TAX + corpId;
                redisUtils.set(taxInfoKey, taxMap);
                // 查询当前用户登录信息中的的角色以及权限信息并返回
                List<Map<String, Object>> list = taxService.getUserRight(taxUserInfo.getId(), taxInfo.getId());
                resultMap.put("userRight", list);
                // 获取角色信息，并返回
                List<Map<String, Object>> roles = taxService.findUserRoleInfos(taxUserInfo.getId(), taxInfo.getId());
                resultMap.put("roles", roles);
            }
        }else{
            // 税务分局未授权应用或该税务分局信息已被作废
            resultMap.put(msgKey, "税务分局未授权应用或该税务分局信息已被作废");
        }
        return resultMap;
    }


}
