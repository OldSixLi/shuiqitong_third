package com.greatchn.controller.web.tax;

import com.greatchn.bean.Result;
import com.greatchn.common.annotation.LoginRequired;
import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.controller.BaseController;
import com.greatchn.po.*;
import com.greatchn.service.tax.TaxService;
import com.greatchn.service.web.tax.TaxRoleManageSrv;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * 税务分局角色管理控制层
 *
 * @author zy 2018-10-29
 */
@RestController
@LoginRequired
@RequestMapping("/tax/roleManage")
public class TaxRoleManageController extends BaseController {

    @Resource
    TaxRoleManageSrv taxRoleManageSrv;

    @Resource
    TaxService taxService;

    @Resource
    RedisUtils redisUtils;

    /**
     * 角色列表
     */
    @RequestMapping("/getRoleList")
    @SuppressWarnings("unchecked")
    public Result getRoleList(@RequestHeader(name = "token") String token) {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (StringUtils.isNotBlank(token) && map != null && !map.isEmpty()) {
            // 获取用户的用户的账户
            TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
            EmployeeInfo employeeInfo = (EmployeeInfo) map.get("employeeInfo");
            if (userInfo != null && employeeInfo != null) {
                TaxInfo taxInfo = null;
                Map<String, Object> taxMap = (Map<String, Object>) redisUtils.get((String) map.get("taxInfoKey"));
                if (taxMap != null && !taxMap.isEmpty()) {
                    taxInfo = (TaxInfo) taxMap.get("taxInfo");
                }
                // 获取公司信息的管理员
                if (taxInfo == null) {
                    taxInfo = taxService.getTaxInfoById(userInfo.getTaxId());
                }
                if (taxInfo != null && StringUtils.equals(Constant.EFFECTIVE_STATE, taxInfo.getState())) {
                    List<TaxRoleInfo> list = taxRoleManageSrv.getRoleList(taxInfo.getId(), null);
                    return Result.success(list);
                } else {
                    return Result.fail("税务分局信息不存在或已被作废！");
                }
            } else {
                // 缓存用户信息不存在
                return Result.fail("-1");
            }
        } else {
            // token对应的登录信息不存在，需要重新登录
            return Result.fail("-1");
        }
    }


    /**
     * 增加角色
     */
    @RequestMapping("/saveOrUpdateRole")
    @SuppressWarnings("unchecked")
    public Result saveOrUpdateRole(String roleName, Integer roleId, @RequestHeader(name = "token") String token) {
        if (StringUtils.isNotBlank(roleName)) {
            Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
            if (StringUtils.isNotBlank(token) && map != null && !map.isEmpty()) {
                // 获取用户的用户的账户
                TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
                EmployeeInfo employeeInfo = (EmployeeInfo) map.get("employeeInfo");
                if (userInfo != null && employeeInfo != null) {
                    TaxInfo taxInfo = null;
                    Map<String, Object> taxMap = (Map<String, Object>) redisUtils.get((String) map.get("taxInfoKey"));
                    if (taxMap != null && !taxMap.isEmpty()) {
                        taxInfo = (TaxInfo) taxMap.get("taxInfo");
                    }
                    // 获取公司信息的管理员
                    if (taxInfo == null) {
                        taxInfo = taxService.getTaxInfoById(userInfo.getTaxId());
                    }
                    if (taxInfo != null && StringUtils.equals(Constant.EFFECTIVE_STATE, taxInfo.getState())) {
                        if (StringUtils.equals(employeeInfo.getUserId(), taxInfo.getManager())) {
                            boolean flag = taxRoleManageSrv.isCannotModifyRoleId(roleId, taxInfo.getId(), null);
                            if (flag) {
                                // 特殊角色（管理员/普通用户）名称不可修改
                                return Result.fail("管理员和普通用户角色名称不可修改");
                            } else {
                                String msg = taxRoleManageSrv.saveOrUpdateRole(roleName, roleId, taxInfo.getId(), userInfo.getId());
                                if (StringUtils.isNotBlank(msg)) {
                                    return Result.fail(msg);
                                } else {
                                    return Result.success("");
                                }
                            }

                        } else {
                            return Result.fail("您不是管理员，不能使用该功能！");
                        }
                    } else {
                        return Result.fail("税务分局信息不存在或已被作废！");
                    }
                } else {
                    // 缓存用户信息不存在
                    return Result.fail("-1");
                }
            } else {
                // token对应的登录信息不存在，需要重新登录
                return Result.fail("-1");
            }
        } else {
            //角色名称不能为空
            return Result.fail("角色名称不能为空");
        }

    }

    /**
     * 删除角色
     */
    @RequestMapping("/deleteRole")
    @SuppressWarnings("unchecked")
    public Result deleteRole(Integer roleId, @RequestHeader(name = "token") String token) {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (StringUtils.isNotBlank(token) && map != null && !map.isEmpty()) {
            // 获取用户的用户的账户
            TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
            EmployeeInfo employeeInfo = (EmployeeInfo) map.get("employeeInfo");
            if (userInfo != null && employeeInfo != null) {
                TaxInfo taxInfo = null;
                Map<String, Object> taxMap = (Map<String, Object>) redisUtils.get((String) map.get("taxInfoKey"));
                if (taxMap != null && !taxMap.isEmpty()) {
                    taxInfo = (TaxInfo) taxMap.get("taxInfo");
                }
                // 获取公司信息的管理员
                if (taxInfo == null) {
                    taxInfo = taxService.getTaxInfoById(userInfo.getTaxId());
                }
                if (taxInfo != null && StringUtils.equals(Constant.EFFECTIVE_STATE, taxInfo.getState())) {
                    if (StringUtils.equals(employeeInfo.getUserId(), taxInfo.getManager())) {
                        boolean flag = taxRoleManageSrv.isCannotModifyRoleId(roleId, taxInfo.getId(), null);
                        if (flag) {
                            // 特殊角色（管理员/普通用户）不可删除
                            return Result.fail("管理员和普通用户角色不可删除");
                        } else {
                            taxRoleManageSrv.deleteRole(roleId);
                            return Result.success("");
                        }
                    } else {
                        return Result.fail("您不是管理员，不能使用该功能！");
                    }
                } else {
                    return Result.fail("税务分局信息不存在或已被作废！");
                }
            } else {
                // 缓存用户信息不存在
                return Result.fail("-1");
            }
        } else {
            // token对应的登录信息不存在，需要重新登录
            return Result.fail("-1");
        }
    }

    /**
     * 角色权限列表
     */
    @RequestMapping("/getRoleRights")
    public Result getRoleRights(Integer roleId) {
        if (roleId != null) {
            List<TaxRoleRight> list = taxRoleManageSrv.getRoleRights(roleId);
            Set<String> rights = new HashSet<>();
            if (list != null && list.size() > 0) {
                for (TaxRoleRight roleRight : list) {
                    rights.add(roleRight.getRightName());
                }
            }
            return Result.success(rights);
        } else {
            return Result.fail("缺少必要参数");
        }

    }


    /**
     * 角色修改权限
     */
    @RequestMapping("/saveOrUpdateRoleRight")
    @SuppressWarnings("unchecked")
    public Result saveOrUpdateRoleRight(Integer roleId, String rightNames, @RequestHeader(name = "token") String token) {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (StringUtils.isNotBlank(token) && map != null && !map.isEmpty()) {
            // 获取用户的用户的账户
            TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
            EmployeeInfo employeeInfo = (EmployeeInfo) map.get("employeeInfo");
            if (userInfo != null && employeeInfo != null) {
                TaxInfo taxInfo = null;
                Map<String, Object> entMap = (Map<String, Object>) redisUtils.get((String) map.get("taxInfoKey"));
                if (entMap != null && !entMap.isEmpty()) {
                    taxInfo = (TaxInfo) entMap.get("taxInfo");
                }
                // 获取公司信息的管理员
                if (taxInfo == null) {
                    taxInfo = taxService.getTaxInfoById(userInfo.getTaxId());
                }
                if (taxInfo != null && StringUtils.equals(Constant.EFFECTIVE_STATE, taxInfo.getState())) {
                    if (StringUtils.equals(employeeInfo.getUserId(), taxInfo.getManager())) {
                        boolean flag = taxRoleManageSrv.isCannotModifyRoleId(roleId, taxInfo.getId(), "modifyRight");
                        if (flag) {
                            // 特殊角色（管理员/普通用户）不可修改权限
                            return Result.fail("管理员角色不可修改权限");
                        } else {
                            String msg = taxRoleManageSrv.saveOrUpdateRoleRight(roleId, rightNames, userInfo.getId());
                            if (StringUtils.isNotBlank(msg)) {
                                return Result.fail(msg);
                            } else {
                                return Result.success("");
                            }
                        }
                    } else {
                        return Result.fail("您不是管理员，不能使用该功能！");
                    }
                } else {
                    return Result.fail("税务分局信息不存在或已被作废！");
                }
            } else {
                // 缓存用户信息不存在
                return Result.fail("-1");
            }
        } else {
            // token对应的登录信息不存在，需要重新登录
            return Result.fail("-1");
        }

    }

    /**
     * 新增用户为某个角色
     */
    @RequestMapping("/saveOrUpdateUserRoles")
    @SuppressWarnings("unchecked")
    public Result saveOrUpdateUserRoles(String roleIds, Integer userId, @RequestHeader(name = "token") String token) {
        if (userId == null) {
            return Result.fail("缺少必要参数");
        }
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (StringUtils.isNotBlank(token) && map != null && !map.isEmpty()) {
            // 获取用户的用户的账户
            TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
            EmployeeInfo employeeInfo = (EmployeeInfo) map.get("employeeInfo");
            if (userInfo != null && employeeInfo != null) {
                TaxInfo taxInfo = null;
                Map<String, Object> entMap = (Map<String, Object>) redisUtils.get((String) map.get("taxInfoKey"));
                if (entMap != null && !entMap.isEmpty()) {
                    taxInfo = (TaxInfo) entMap.get("taxInfo");
                }
                // 获取公司信息的管理员
                if (taxInfo == null) {
                    taxInfo = taxService.getTaxInfoById(userInfo.getTaxId());
                }
                if (taxInfo != null && StringUtils.equals(Constant.EFFECTIVE_STATE, taxInfo.getState())) {
                    if (StringUtils.equals(employeeInfo.getUserId(), taxInfo.getManager())) {
                        String msg = taxRoleManageSrv.saveOrUpdateUserRoles(roleIds, userId, taxInfo.getId());
                        if (StringUtils.isNotEmpty(msg)) {
                            return Result.fail(msg);
                        } else {
                            return Result.success("");
                        }
                    } else {
                        return Result.fail("您不是管理员，不能使用该功能！");
                    }
                } else {
                    return Result.fail("税务分局信息不存在或已被作废！");
                }
            } else {
                // 缓存用户信息不存在
                return Result.fail("-1");
            }
        } else {
            // token对应的登录信息不存在，需要重新登录
            return Result.fail("-1");
        }
    }

    /**
     * 查询当前用户角色
     */
    @RequestMapping("/getAllRolesOfUser")
    @SuppressWarnings("unchecked")
    public Result getAllRolesOfUser(Integer userId, @RequestHeader(name = "token") String token) {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (StringUtils.isNotBlank(token) && map != null && !map.isEmpty()) {
            // 获取用户的用户的账户
            TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
            if (userInfo != null) {
                if (userId == null) {
                    return Result.fail("缺少必要参数");
                } else {
                    return Result.success(taxService.getUserRole(userId, userInfo.getTaxId()));
                }
            } else {
                // 缓存用户信息不存在
                return Result.fail("-1");
            }
        } else {
            // 缓存用户信息不存在
            return Result.fail("-1");
        }
    }


    /**
     * 移交管理员
     */
    @RequestMapping("/transformAdministrator")
    @SuppressWarnings("unchecked")
    public Result transformAdministrator(String userId, @RequestHeader(name = "token") String token) throws IOException, URISyntaxException {
        if (StringUtils.isBlank(userId)) {
            return Result.fail("缺少必要参数");
        }
        // 校验当前登录用户是否为管理员
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (StringUtils.isNotBlank(token) && map != null && !map.isEmpty()) {
            // 获取用户的用户的账户
            TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
            EmployeeInfo employeeInfo = (EmployeeInfo) map.get("employeeInfo");
            if (userInfo != null && employeeInfo != null) {
                TaxInfo taxInfo = null;
                Map<String, Object> taxMap = (Map<String, Object>) redisUtils.get((String) map.get("taxInfoKey"));
                if (taxMap != null && !taxMap.isEmpty()) {
                    taxInfo = (TaxInfo) taxMap.get("taxInfo");
                }
                // 获取公司信息的管理员
                if (taxInfo == null) {
                    taxInfo = taxService.getTaxInfoById(userInfo.getTaxId());
                }
                if (taxInfo != null && StringUtils.equals(Constant.EFFECTIVE_STATE, taxInfo.getState())) {
                    if (StringUtils.equals(employeeInfo.getUserId(), taxInfo.getManager())) {
                        Map<String,Object> resultMap=taxRoleManageSrv.initEmployeeInfoAndTransform(taxInfo.getCorpId(),userId,taxInfo.getId());
                        String msg=(String)resultMap.get("msg");
                        if(StringUtils.isNotBlank(msg)){
                            return Result.fail(msg);
                        }else{
                            // 更新当前用户的缓存
                            map.put("userRight", resultMap.get("userRight"));
                            map.put("roles", resultMap.get("roles"));
                            if(Constant.IS_TEST){
                                redisUtils.set(token,map);
                            }else{
                                redisUtils.set(token,map,Constant.ENT_TOKEN_TIME_OUT);
                            }
                            return Result.success("");
                        }
                    } else {
                        return Result.fail("您不是管理员，不能使用该功能！");
                    }
                } else {
                    return Result.fail("税务分局信息不存在或已被作废！");
                }
            } else {
                // 缓存用户信息不存在
                return Result.fail("-1");
            }
        } else {
            // token对应的登录信息不存在，需要重新登录
            return Result.fail("-1");
        }

    }


}
