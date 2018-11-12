package com.greatchn.controller.ent;

import com.greatchn.bean.Result;
import com.greatchn.common.annotation.LoginRequired;
import com.greatchn.common.annotation.NotNeedLogin;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.controller.BaseController;
import com.greatchn.po.EmployeeInfo;
import com.greatchn.service.ent.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

@RestController
@LoginRequired
@RequestMapping("/ent/login")
public class LoginController extends BaseController {

    @Resource
    LoginService loginService;

    @Resource
    RedisUtils redisUtils;

    /**
     * 获取部门下的子部门
     *
     * @author zy 2018-9-19
     */

    @RequestMapping("/getDepartments")
    @SuppressWarnings("unchecked")
    public Result getDepartments(@RequestHeader(name = "token") String token) throws IOException, URISyntaxException {
        // 获取当前登录用户的corpId
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (StringUtils.isNotBlank(token) && map != null && !map.isEmpty()) {
            // 获取用户的用户的账户
            EmployeeInfo employeeInfo = (EmployeeInfo) map.get("employeeInfo");
            if (employeeInfo != null) {
                return Result.success(loginService.getDeparmentByDeparmentId(employeeInfo.getEnterpriseId(), null));
            } else {
                return Result.fail("-1");
            }
        } else {
            return Result.fail("-1");
        }
    }

    /**
     * 获取部门下的员工，并返回(直接访问微信接口返回信息)
     *
     * @param departmentId 部门id，为空或null都默认查询第一级部门
     * @author zy 2018-9-19
     */
    @RequestMapping("/getEmployee")
    @SuppressWarnings("unchecked")
    public Result getDepartmentEmployee(String departmentId, @RequestHeader(name = "token") String token) throws IOException, URISyntaxException {
        // 获取当前登录用户的corpId
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (StringUtils.isNotBlank(token) && map != null && !map.isEmpty()) {
            // 获取用户的用户的账户
            EmployeeInfo employeeInfo = (EmployeeInfo) map.get("employeeInfo");
            if (employeeInfo != null) {
                return Result.success(loginService.getDepartmentEmployee(employeeInfo.getEnterpriseId(), departmentId));
            } else {
                return Result.fail("-1");
            }
        } else {
            return Result.fail("-1");
        }
    }

    /**
     * 为特定角色设置用户
     *
     * @param userId 企业微信userId
     * @param roleId 角色id
     * @author zy 2018-9-19
     */
    @RequestMapping("/saveOrUpdateUserRole")
    @SuppressWarnings("unchecked")
    public Result saveOrUpdateUserRole(String userId, Integer roleId, @RequestHeader(name = "token") String token) throws IOException, URISyntaxException {
        if (StringUtils.isNotEmpty(userId) && roleId != null) {
            // 获取当前登录用户的corpId
            Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
            if (StringUtils.isNotBlank(token) && map != null && !map.isEmpty()) {
                // 获取用户的用户的账户
                EmployeeInfo employeeInfo = (EmployeeInfo) map.get("employeeInfo");
                if (employeeInfo != null) {
                    // 保存成功
                    String msg = loginService.saveOrUpdateRoleUser(userId, roleId, employeeInfo.getEnterpriseId());
                    if (StringUtils.isNotEmpty(msg)) {
                        return Result.fail(msg);
                    } else {
                        return Result.success("");
                    }
                } else {
                    return Result.fail("-1");
                }
            } else {
                return Result.fail("-1");
            }

        } else {
            return Result.fail("缺少必要参数");
        }
    }

    /**
     * 获取四个特定角色的用户以及角色信息
     *
     * @author zy 2018-9-19
     */
    @RequestMapping("/getRoleUser")
    @SuppressWarnings("unchecked")
    public Result getRoleUser(@RequestHeader(name = "token") String token) {
        // 获取当前登录用户的corpId
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (StringUtils.isNotBlank(token) && map != null && !map.isEmpty()) {
            // 获取用户的用户的账户
            EmployeeInfo employeeInfo = (EmployeeInfo) map.get("employeeInfo");
            if (employeeInfo != null) {
                return Result.success(loginService.getRoleUser(employeeInfo.getEnterpriseId()));
            } else {
                return Result.fail("-1");
            }
        } else {
            return Result.fail("-1");
        }

    }

    /**
     * 获取四个特定角色的用户以及角色信息
     *
     * @author zy 2018-9-19
     */
    @RequestMapping("/login")
    @NotNeedLogin
    public Result login(String code, @RequestHeader(name = "token") String token) throws IOException, URISyntaxException {
        return Result.success(loginService.login(code, get32UUID(), token));
    }

    /**
     * token过期，重新进行登录
     */
    @RequestMapping("/reLogin")
    @NotNeedLogin
    public Result reLogin(String userId, String corpId, @RequestHeader(name = "token") String token) throws IOException, URISyntaxException {
        return Result.success(loginService.excuteLogin(corpId, userId, get32UUID(), token));
    }

    /**
     * 获取用户的角色，权限并进行更新
     *
     * @author zy 2018-9-25
     */
    @RequestMapping("/getRoleAndRight")
    public Result getRoleAndRight(@RequestHeader(name = "token") String token) throws IOException, URISyntaxException {
        Map<String, Object> map = loginService.getRoleAndRight(token);
        if (StringUtils.isNotBlank((String) map.get("msg"))) {
            return Result.fail((String) map.get("msg"));
        } else {
            return Result.success(map);
        }
    }


    /**
     * 通过code获取用户的userId和corpId
     */
    @NotNeedLogin
    @RequestMapping("/getUserIdAndCorpIdByCode")
    public Result getUserIdAndCorpIdByCode(String code) throws IOException, URISyntaxException {
        return Result.success(loginService.getUserIdAndCorpIdByCode(code));
    }
}
