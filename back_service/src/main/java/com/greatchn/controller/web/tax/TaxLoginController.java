package com.greatchn.controller.web.tax;


import com.greatchn.bean.Result;
import com.greatchn.common.annotation.LoginRequired;
import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.controller.BaseController;
import com.greatchn.service.web.tax.TaxLoginSrv;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.DigestException;
import java.util.Map;


/**
 * 税务局web端登录控制层
 *
 * @author zy 2018-10-11
 */
@RequestMapping("/tax/login")
@RestController
public class TaxLoginController extends BaseController {

    @Resource
    TaxLoginSrv taxLoginSrv;

    @Resource
    RedisUtils redisUtils;

    /**
     * 税务局企业微信扫码登录
     *
     * @author zy 2018-10-11
     */
    @RequestMapping("/qr_code")
    public Result loginByQRCode(String code, @RequestHeader(name = "token") String token) throws IOException, URISyntaxException {
        Map<String, Object> map;
        if (Constant.IS_SELF_BUILT_LOGIN) {
            map = taxLoginSrv.selfLoginByQRCode(code, get32UUID(), token);
        } else {
            map = taxLoginSrv.loginByQRCode(code, get32UUID(), token);
        }
        return Result.success(map);
    }

    /**
     * 税务分局账号密码登录
     *
     * @author zy 2018-10-11
     */
    @RequestMapping("/loginByPassword")
    public Result loginByPassword(String account, String password, @RequestHeader(name = "token") String token) throws URISyntaxException, IOException, DigestException {
        // 检验是否对应的参数
        if (StringUtils.isNotBlank(account) && StringUtils.isNotBlank(password)) {
            Map<String, Object> resultMap = taxLoginSrv.loginByPassword(account, password, get32UUID(), token);
            return Result.success(resultMap);
        } else {
            return Result.fail("账号或密码不能为空");
        }
    }

    /**
     * 税务局第三方应用登录
     *
     * @author zy 2018-11-12
     */
    @RequestMapping("/login")
    public Result login(String code, @RequestHeader(name = "token") String token) throws IOException, URISyntaxException {
        return Result.success(taxLoginSrv.login(code, get32UUID(), token));
    }

    /**
     * 税务局第三方应用重新登录
     *
     * @author zy 2018-11-12
     */
    @RequestMapping("/reLogin")
    public Result reLogin(String userId, String corpId, @RequestHeader(name = "token") String token) throws IOException, URISyntaxException {
        return Result.success(taxLoginSrv.excuteLogin(corpId, userId, get32UUID(), token));
    }

    /**
     * 退出登录
     *
     * @author zy 2018-10-11
     */
    @RequestMapping("/logout")
    @SuppressWarnings("unchecked")
    public Result logout(@RequestHeader(name = "token") String token) {
        // 校验token
        if (StringUtils.isNotBlank(token)) {
            // 清除缓存中的登录信息
            Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
            if (map != null && !map.isEmpty()) {
                redisUtils.del(token);
            }
        }
        return Result.success("退出登录成功");
    }


    /**
     * 获取用户的角色，权限并进行更新
     *
     * @author zy 2018-11-14
     */
    @LoginRequired
    @RequestMapping("/getRoleAndRight")
    public Result getRoleAndRight(@RequestHeader(name = "token") String token) throws IOException, URISyntaxException {
        Map<String, Object> map = taxLoginSrv.getRoleAndRight(token);
        if (StringUtils.isNotBlank((String) map.get("msg"))) {
            return Result.fail((String) map.get("msg"));
        } else {
            return Result.success(map);
        }
    }

}
