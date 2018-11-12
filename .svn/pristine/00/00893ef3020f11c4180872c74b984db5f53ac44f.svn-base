package com.greatchn.controller.ent;

import com.greatchn.bean.Result;
import com.greatchn.common.annotation.LoginRequired;
import com.greatchn.common.annotation.NotNeedLogin;
import com.greatchn.common.utils.Constant;
import com.greatchn.controller.BaseController;
import com.greatchn.service.SelfbuiltService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 自建应用使用的控制层
 *
 * @author zy 2018-9-19
 */
@RestController
@RequestMapping("/self")
@LoginRequired
public class SelfbuiltController extends BaseController {

    @Resource
    SelfbuiltService selfbuiltService;

    /**
     * 获取部门下的子部门
     *
     * @author zy 2018-9-19
     */

    @RequestMapping("/getDepartments")
    public Result getDepartments() throws IOException, URISyntaxException {
        return Result.success(selfbuiltService.getDeparmentByDeparmentId(null));
    }

    /**
     * 获取部门下的员工，并返回(直接访问微信接口返回信息)
     *
     * @param departmentId 部门id，为空或null都默认查询第一级部门
     * @author zy 2018-9-19
     */
    @RequestMapping("/getEmployee")
    public Result getDepartmentEmployee(String departmentId) throws IOException, URISyntaxException {
        return Result.success(selfbuiltService.getDepartmentEmployee(Constant.corpId, departmentId));
    }

    /**
     * 获取部门下的员工，并返回(直接访问微信接口返回信息)
     *
     * @param userId 企业微信userId
     * @param roleId 角色id
     * @author zy 2018-9-19
     */
    @RequestMapping("/saveOrUpdateUserRole")
    public Result saveOrUpdateUserRole(String userId, Integer roleId) throws IOException, URISyntaxException {
        if (StringUtils.isNotEmpty(userId) && roleId != null) {
            // 保存成功
            String msg = selfbuiltService.saveOrUpdateRoleUser(userId, roleId, Constant.corpId);
            if (StringUtils.isNotEmpty(msg)) {
                return Result.fail(msg);
            } else {
                return Result.success("");
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
    public Result getRoleUser() {
        return Result.success(selfbuiltService.getRoleUser());
    }


    private static String aAuth2URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&agentid=%s&state=%s#wechat_redirect";

    /**
     * 静默授权，可获取成员的的基础信息（UserId与DeviceId）
     */
    public static final String snsapiBase = "snsapi_base";
    /**
     * 静默授权，可获取成员的详细信息，但不包含手机、邮箱；
     */
    public static final String snsapiUserinfo = "snsapi_userinfo";
    /**
     * 手动授权，可获取成员的详细信息，包含手机、邮箱
     */
    public static final String snsapiPrivateinfo = "snsapi_privateinfo";

    /**
     * 微信跳转授权
     *
     * @param redirectUri 重定向地址
     * @param scope       授权范围
     * @param state       其他参数
     * @author zy 2018-9-19
     */
    @RequestMapping("/getAuthURL")
    @NotNeedLogin
    public static String getAuthURL(String redirectUri, String scope, String state) throws UnsupportedEncodingException {
        // 默认静默授权
        if (scope == null) {
            scope = snsapiBase;
        }
        return String.format(aAuth2URL, Constant.corpId, URLEncoder.encode(redirectUri, "UTF-8"), scope, Constant.agentId, state);
    }

    /**
     * 获取四个特定角色的用户以及角色信息
     *
     * @author zy 2018-9-19
     */
    @RequestMapping("/login")
    @NotNeedLogin
    public Result login(String code, @RequestHeader(name = "token") String token) throws IOException, URISyntaxException {
        return Result.success(selfbuiltService.login(code, get32UUID(), token));
    }

    /**
     * token过期，重新进行登录
     */
    @RequestMapping("/reLogin")
    @NotNeedLogin
    public Result reLogin(String userId, String corpId, @RequestHeader(name = "token") String token) throws IOException, URISyntaxException {
        return Result.success(selfbuiltService.excuteLogin(StringUtils.isNotBlank(corpId) ? corpId : Constant.corpId, userId, get32UUID(), token));
    }

    /**
     * 获取用户的角色，权限并进行更新
     *
     * @author zy 2018-9-25
     */
    @RequestMapping("/getRoleAndRight")
    public Result getRoleAndRight(@RequestHeader(name = "token") String token) throws IOException, URISyntaxException {
        Map<String, Object> map = selfbuiltService.getRoleAndRight(token);
        if (StringUtils.isNotBlank((String) map.get("msg"))) {
            return Result.fail((String) map.get("msg"));
        } else {
            return Result.success(map);
        }
    }

}
