package com.greatchn.controller.web.tax;

import com.greatchn.bean.Page;
import com.greatchn.bean.PageData;
import com.greatchn.bean.Result;
import com.greatchn.bean.SimplePage;
import com.greatchn.common.annotation.LoginRequired;
import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.controller.BaseController;
import com.greatchn.po.EmployeeInfo;
import com.greatchn.po.TaxInfo;
import com.greatchn.po.TaxUserInfo;
import com.greatchn.service.tax.TaxService;
import com.greatchn.service.web.tax.UserCenterSrv;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.security.DigestException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 税务分局用户中心控制层
 *
 * @author zy 2018-10-19
 */
@RestController
@LoginRequired
@RequestMapping("/tax/userCenter")
public class UserCenterController extends BaseController {

    @Resource
    RedisUtils redisUtils;

    @Resource
    UserCenterSrv userCenterSrv;

    @Resource
    TaxService taxService;

    /**
     * 设置用户名密码
     */
    @RequestMapping("/setAccountAndPassword")
    @SuppressWarnings("unchecked")
    public Result setLoginAccountAndPassword(String account, String password, @RequestHeader(name = "token") String token) throws DigestException {
        if (StringUtils.isNotBlank(account) && StringUtils.isNotBlank(password)) {
            // 校验token
            if (StringUtils.isNotBlank(token)) {
                // 获取用户的用户的账户
                Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
                String userInfoKey = "userInfo";
                if (map != null && !map.isEmpty() && map.get(userInfoKey) != null) {
                    TaxUserInfo userInfo = (TaxUserInfo) map.get(userInfoKey);
                    if (StringUtils.isNotBlank(userInfo.getLoginAccount())) {
                        // 您已设置过账号，不能再次设置
                        return Result.fail("您已设置过账号,账号为【" + userInfo.getLoginAccount() + "】,设置密码请使用修改密码");
                    } else {
                        String allNumberRegx = "[0-9]+";
                        if (password.matches(allNumberRegx)) {
                            // 密码不能为纯数字
                            return Result.fail("密码不能为纯数字");
                        } else {
                            String accountPatter = "[a-zA-Z0-9_]+$";
                            String passwordPattern = "[a-zA-Z0-9_]{6,}$";
                            boolean accountIsMatch = Pattern.matches(accountPatter, account);
                            boolean passwordIsMatch = Pattern.matches(passwordPattern, password);
                            // 用户名是否只为字母、数字或下划线
                            if (passwordIsMatch && accountIsMatch) {
                                String msg = userCenterSrv.setLoginAccountAndPassword(userInfo.getId(), account, password, token, map);
                                if (StringUtils.isNotEmpty(msg)) {
                                    return Result.fail(msg);
                                } else {
                                    return Result.success("");
                                }
                            } else {
                                //账号密码只能数字、字母、下划线以外的字符,密码长度不小于6，密码不能为纯数字
                                if (!accountIsMatch && !passwordIsMatch) {
                                    return Result.fail("账号密码只能有字母数字下划线，且密码不能为纯数字，位数不小于6！");
                                } else if (!accountIsMatch && passwordIsMatch) {
                                    return Result.fail("账号只能有字母数字下划线！");
                                } else {
                                    return Result.fail("密码只能有字母数字下划线，不能为纯数字，位数不小于6！");
                                }
                            }
                        }

                    }
                } else {
                    // token对应的登录信息不存在，需要重新登录
                    return Result.fail("-1");
                }
            } else {
                // token对应的登录信息不存在，需要重新登录
                return Result.fail("-1");
            }
        } else {
            return Result.fail("缺少必要参数");
        }
    }

    /**
     * 获取当前登录用户账号信息
     */
    @RequestMapping("/getUserLoginAccount")
    @SuppressWarnings("unchecked")
    public Result getUserLoginAccount(@RequestHeader(name = "token") String token) {
        Map<String, Object> resultMap = new HashMap<>(3);
        String userInfoKey = "userInfo";
        // 获取用户的用户的账户
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map != null && !map.isEmpty() && map.get(userInfoKey) != null) {
            TaxUserInfo userInfo = (TaxUserInfo) map.get(userInfoKey);
            if (StringUtils.isBlank(userInfo.getLoginAccount())) {
                userInfo = taxService.findUserInfoById(userInfo.getId(), "all");
                //更新缓存
                map.put(userInfoKey, userInfo);
                if (Constant.IS_TEST) {
                    redisUtils.set(token, map);
                } else {
                    redisUtils.set(token, map, Constant.ENT_TOKEN_TIME_OUT);
                }
            }
            if (userInfo != null) {
                resultMap.put("loginAccount", userInfo.getLoginAccount());
                resultMap.put("havePw", StringUtils.isNotBlank(userInfo.getPassword()) ? "Y" : "N");
            }
            return Result.success(resultMap);
        } else {
            return Result.fail("-1");
        }
    }

    /**
     * 校验账号是否已存在
     */
    @RequestMapping("/checkAccount")
    public Result checkLoginAccount(String account) {
        if (StringUtils.isNotBlank(account)) {
            String accountPatter = "[a-zA-Z0-9_]+$";
            boolean accountIsMatch = Pattern.matches(accountPatter, account);
            // 用户名是否只为字母、数字或下划线
            if (accountIsMatch) {
                // 获取是否存在的结果
                return Result.success(userCenterSrv.checkLoginAccount(account));
            } else {
                //账号密码只能数字、字母、下划线以外的字符,账号长度不小于1，密码长度不小于6，密码的第一个不能为数字
                return Result.fail("账号只能包含字母、数字,长度不小于6位！");
            }
        } else {
            return Result.fail("缺少必要参数");
        }
    }


    /**
     * 管理员重置密码
     */
    @RequestMapping("/reset")
    @SuppressWarnings("unchecked")
    public Result resetTaxUserPassword(Integer id, @RequestHeader(name = "token") String token) throws DigestException {
        if (id != null) {
            Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
            if (StringUtils.isNotBlank(token) && map != null && !map.isEmpty()) {
                // 获取用户的用户的账户
                TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
                if (userInfo != null) {
                    TaxInfo taxInfo = null;
                    Map<String, Object> entMap = (Map<String, Object>) redisUtils.get((String) map.get("taxInfoKey"));
                    if (entMap != null && !entMap.isEmpty()) {
                        taxInfo = (TaxInfo) entMap.get("taxInfo");
                    }
                    // 获取公司信息的管理员
                    if (taxInfo == null) {
                        taxInfo = taxService.getTaxInfoById(userInfo.getTaxId());
                    }
                    EmployeeInfo employeeInfo = (EmployeeInfo) map.get("employeeInfo");
                    // 校验当前用户是否为公司的管理员
                    if (employeeInfo != null && StringUtils.equals(taxInfo.getManager(), employeeInfo.getUserId())) {
                        if (id.equals(userInfo.getId())) {
                            return Result.fail("您不能重置管理员的密码");
                        } else {
                            // 允许管理员重置其他用户密码
                            String msg = userCenterSrv.resetTaxUserPassword(id);
                            if (StringUtils.isNotEmpty(msg)) {
                                return Result.fail(msg);
                            } else {
                                return Result.success("操作成功");
                            }
                        }
                    } else if (employeeInfo == null) {
                        // 缓存用户信息不存在
                        return Result.fail("-1");
                    } else {
                        // 您不是管理员，不能重置密码
                        return Result.fail("您不是管理员，不能重置密码");
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
            return Result.fail("缺少必要参数");
        }
    }

    /***
     *  查询当前税务分局已登录过的员工列表
     *
     * */
    @RequestMapping("/getTaxUserList")
    @SuppressWarnings("unchecked")
    public Result getTaxUserList(Page page, @RequestHeader(name = "token") String token) {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (StringUtils.isNotBlank(token) && map != null && !map.isEmpty()) {
            // 获取用户的用户的账户
            TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
            if (userInfo != null && userInfo.getTaxId() != null) {
                PageData pd = this.getPageData();
                pd.put("taxId", userInfo.getTaxId());
                page.setPd(pd);
                // 获取当前税务分局的用户信息
                List<Map<String, Object>> list = taxService.getTaxUserInfoList(page);
                SimplePage sp = new SimplePage(page, list);
                return Result.success(sp);
            } else {
                // 缓存用户信息不存在,需要重新登录
                return Result.fail("-1");
            }
        } else {
            // token对应的登录信息不存在，需要重新登录
            return Result.fail("-1");
        }
    }

    /**
     * 当前用户修改密码
     */
    @RequestMapping("/updatePassword")
    public Result updatePassword(String password, @RequestHeader(name = "token") String token) throws DigestException {
        if (StringUtils.isNotEmpty(password)) {
            String msg = userCenterSrv.updatePassword(password, token);
            if (StringUtils.isNotEmpty(msg)) {
                return Result.fail(msg);
            } else {
                return Result.success("");
            }
        } else {
            return Result.fail("缺少必要参数");
        }
    }

}
