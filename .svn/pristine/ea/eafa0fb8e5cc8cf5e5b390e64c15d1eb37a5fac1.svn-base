package com.greatchn.controller.tax;

import com.greatchn.bean.Result;
import com.greatchn.common.utils.Constant;
import com.greatchn.controller.BaseController;
import com.greatchn.service.web.tax.TaxLoginSrv;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * 税务分局企业微信应用登录控制层
 *
 * @author zy 2018-10-19
 */
@RequestMapping("/tax/login")
public class LoginController extends BaseController {

    @Resource
    TaxLoginSrv taxLoginSrv;

    /**
     * 登录
     */
    @RequestMapping("/login")
    public Result login(String code, @RequestHeader(name = "token") String token) throws IOException, URISyntaxException {
        Map<String, Object> map;
        if (Constant.IS_SELF_BUILT_LOGIN) {
            map = taxLoginSrv.selfLoginByQRCode(code, get32UUID(), token);
        } else {
            map = taxLoginSrv.loginByQRCode(code, get32UUID(), token);
        }
        return Result.success(map);
    }

}
