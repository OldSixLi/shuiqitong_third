package com.greatchn.controller.ent;

import com.greatchn.bean.Result;
import com.greatchn.common.annotation.LoginRequired;
import com.greatchn.common.annotation.NotNeedLogin;
import com.greatchn.controller.BaseController;
import com.greatchn.controller.wechat.WeChatCallbackController;
import com.greatchn.service.ent.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@LoginRequired
@RequestMapping("/ent/login")
public class LoginController extends BaseController {

    /**
     * 输出到控制台的日志
     */
    private static Logger log = LoggerFactory.getLogger(WeChatCallbackController.class);

    @Resource
    LoginService loginService;

    /**
     * 授权成功，根据code获取用户信息进行登录
     *
     * @author zy 2018-9-13
     */
    @RequestMapping("/employeeLogin")
    public Result employeeLogin(String code){
        if (StringUtils.isNotEmpty(code)) {
            return Result.success("");
        } else {
            // 缺少必要参数
            return Result.fail("缺少必要参数");
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
     * 使用userId和公司id重新登录
     */
    @RequestMapping("/reLogin")
    public Result reLogin() {
        // 直接调用登录
        return Result.success("");
    }

    /**
     * 为特定角色设置用户
     *
     * @param userId 企业微信userId
     * @param roleId 角色id
     * @author zy 2018-9-19
     */
    @RequestMapping("/saveOrUpdateUserRole")
    public Result saveOrUpdateUserRole(String userId, Integer roleId, String corpId) throws IOException, URISyntaxException {
        if (StringUtils.isNotEmpty(userId) && roleId != null) {
            // 保存成功
            String msg = loginService.saveOrUpdateRoleUser(userId, roleId, corpId);
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
        return Result.success(loginService.getRoleUser());
    }


}
