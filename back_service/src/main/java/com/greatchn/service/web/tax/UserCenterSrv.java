package com.greatchn.service.web.tax;


import com.greatchn.common.dao.BaseDao;
import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.MD5Util;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.po.TaxUserInfo;
import com.greatchn.service.tax.TaxService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.security.DigestException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 税务分局登录业务层
 *
 * @author zy 2018-10-13
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class UserCenterSrv {


    @Resource
    BaseDao baseDao;

    @Resource
    TaxService taxService;

    @Resource
    RedisUtils redisUtils;


    /**
     * 设置用户名密码
     */
    @Transactional(rollbackFor = Exception.class)
    public String setLoginAccountAndPassword(Integer id, String loginAccount, String password, String token, Map<String, Object> redisMap) throws DigestException {
        String msg = null;
        // 查询要修改的用户并对用户信息加锁
        String sql = "select * from tax_user_info where ID=? for update";
        List<TaxUserInfo> list = baseDao.queryBySql(sql, TaxUserInfo.class, id);
        TaxUserInfo oldUserInfo = list != null && list.size() > 0 ? list.get(0) : null;
        boolean isExist = checkLoginAccount(loginAccount);
        if (!isExist && oldUserInfo != null) {
            if (StringUtils.isNotEmpty(oldUserInfo.getLoginAccount())) {
                msg = "您已设置过账号,账号为【" + oldUserInfo.getLoginAccount() + "】,设置密码请使用修改密码";
            } else {
                String newPassword = MD5Util.SHA1(id.toString() + password);
                // 调用方法更新用户名密码
                oldUserInfo.setLoginAccount(loginAccount);
                oldUserInfo.setPassword(newPassword);
                baseDao.update(oldUserInfo);
                redisMap.put("userInfo", oldUserInfo);
                // 更详缓存
                if (Constant.IS_TEST) {
                    redisUtils.set(token, redisMap);
                } else {
                    redisUtils.set(token, redisMap, Constant.ENT_TOKEN_TIME_OUT);
                }
            }
        } else {
            if(isExist&&oldUserInfo!=null&&StringUtils.equals(oldUserInfo.getLoginAccount(),loginAccount)){
                msg="您已设置过账号,账号为【" + oldUserInfo.getLoginAccount() + "】,设置密码请使用修改密码";
            } else if (oldUserInfo==null) {
                // 用户不存在
                msg = "用户不存在,设置失败";
            } else {
                // 该账号已存在
                msg = "该账号已存在请重新输入";
            }
        }
        return msg;
    }


    /**
     * 校验账号是否已存在
     */
    public boolean checkLoginAccount(String loginAccount) {
        boolean flag = false;
        TaxUserInfo userInfo = taxService.findUserInfo(loginAccount);
        if (userInfo != null) {
            // 账号存在
            flag = true;
        }
        return flag;
    }


    /**
     * 修改密码
     */
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public String updatePassword(String password, String token) throws DigestException {
        String msg = null;
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (StringUtils.isNotBlank(token)&&map!=null&&!map.isEmpty()) {
            TaxUserInfo taxUserInfo=(TaxUserInfo)map.get("userInfo");
            if(taxUserInfo!=null){
                Integer id =taxUserInfo.getId();
                String allNumberRegx="[0-9]+";
                if(password.matches(allNumberRegx)){
                    // 密码不能为纯数字
                    msg="密码不能为纯数字";
                }else{
                    String passwordPattern = "[a-zA-Z0-9_]{6,}$";
                    boolean passwordIsMatch = Pattern.matches(passwordPattern, password);
                    if (passwordIsMatch) {
                        // 查询要修改的用户并对用户信息加锁
                        String sql = "select * from tax_user_info where ID=? for update";
                        List<TaxUserInfo> list = baseDao.queryBySql(sql, TaxUserInfo.class, id);
                        TaxUserInfo oldUserInfo = list != null && list.size() > 0 ? list.get(0) : null;
                        if (oldUserInfo != null) {
                            String newPassword = MD5Util.SHA1(id.toString() + password);
                            if (StringUtils.equals(password, Constant.DEFAULT_PASSWORD)) {
                                msg = "请勿将密码设置为默认的密码";
                            } else {
                                // 调用方法更新用户名密码
                                oldUserInfo.setPassword(newPassword);
                                baseDao.update(oldUserInfo);
                                map.put("userInfo",oldUserInfo);
                                //更新缓存
                                if(Constant.IS_TEST){
                                    redisUtils.set(token,map);
                                }else{
                                    redisUtils.set(token,map,Constant.ENT_TOKEN_TIME_OUT);
                                }
                            }
                        } else {
                            // 用户不存在
                            msg = "用户不存在,设置失败";
                        }
                    } else {
                        msg = "密码只能有字母数字下划线，且位数不小于6！";
                    }
                }
            }else{
                msg="-1";
            }
        }else{
            msg="-1";
        }
        return msg;
    }

    /**
     * 管理员重置密码
     */
    @Transactional(rollbackFor = Exception.class)
    public String resetTaxUserPassword(Integer id) throws DigestException {
        String msg = null;
        TaxUserInfo taxUserInfo = taxService.findUserInfoById(id, "update");
        if (taxUserInfo != null) {
            if(StringUtils.isNotBlank(taxUserInfo.getLoginAccount())){
                // 重置用户的密码
                String newPassword = MD5Util.SHA1(id.toString() + Constant.DEFAULT_PASSWORD);
                taxUserInfo.setPassword(newPassword);
                // 保存用户信息
                baseDao.save(taxUserInfo);
            }else{
                // 用户未设置登录账号
                msg = "该用户未设置登录账号";
            }
        } else {
            // 用户不存在
            msg = "该用户不存在";
        }
        return msg;
    }

}
