package com.greatchn.common.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.greatchn.bean.Result;
import com.greatchn.common.annotation.LoginRequired;
import com.greatchn.common.annotation.NotNeedLogin;
import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.po.AuditInfo;
import com.greatchn.po.EnterpriseInfo;
import com.greatchn.po.TaxInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * token验证拦截
 *
 * @author zy 2018-9-19
 */
@Component
public class TokenAuthorInterceptor implements HandlerInterceptor {

    /**
     * 输出到控制台的日志
     */
    private static Logger logger = LoggerFactory.getLogger(TokenAuthorInterceptor.class);


    @Resource
    RedisUtils redisUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 验证权限
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 获取request的地址
            String url = request.getRequestURI();
            // 获取方法上的注解
            LoginRequired loginRequired = handlerMethod.getMethod().getAnnotation(LoginRequired.class);
            // 不需要登录
            NotNeedLogin notNeedLogin = handlerMethod.getMethod().getAnnotation(NotNeedLogin.class);
            if (notNeedLogin != null) {
                return true;
            }
            // 如果方法上的注解为空 则获取类的注解
            if (loginRequired == null) {
                loginRequired = handlerMethod.getMethod().getDeclaringClass().getAnnotation(LoginRequired.class);
            }

            Result result = null;
            // 如果标记了注解，则判断权限
            if (loginRequired != null) {
                // 判断是否存在令牌信息，如果存在，则允许登录
                String token = request.getHeader("token");
                if (StringUtils.isBlank(token)) {
                    result = Result.fail("-1");
                } else {
                    // 判断当前用户是否有访问该方法的权限
                    String msg = volidateToken(token, url);
                    if (StringUtils.isNotEmpty(msg)) {
                        result = Result.fail(msg);
                    }
                }
                if (result != null) {
                    try (OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream(),
                            "UTF-8"); PrintWriter writer = new PrintWriter(osw, true)) {
                        String jsonStr = JSON.toJSONString(result);
                        writer.write(jsonStr);
                        writer.flush();
                    } catch (UnsupportedEncodingException e) {
                        logger.error("返回信息失败:" + e.getMessage(), e);
                    } catch (IOException e) {
                        logger.error("返回信息失败:" + e.getMessage(), e);
                    }
                    logger.info("请求【" + url + "】,请求被拦截，请求参数【" + JSONObject.toJSONString(request.getParameterMap()) + "】，返回结果" + JSONObject.toJSONString(result));
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 校验登录是否有效,并延长失效时间
     *
     * @author zy 2018-9-20
     */
    @SuppressWarnings("unchecked")
    public String volidateToken(String token, String url) {
        String msg = null;
        if (StringUtils.equals(token, Constant.superToken)) {
            return msg;
        }
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map == null || map.isEmpty()) {
            msg = "-1";
        } else {
            //若不为调试模式，则更新缓存
            if (!Constant.IS_TEST) {
                redisUtils.set(token, map, Constant.ENT_TOKEN_TIME_OUT);
            }
            String taxPrefix = "/tax/";
            // 判断是否为税务分局的方法校验
            if (StringUtils.startsWith(url, taxPrefix)) {
                String taxInfoKey = (String) map.get("taxInfoKey");
                if (StringUtils.isNotEmpty(taxInfoKey)) {
                    // 获取公司信息进行判断
                    Map<String, Object> taxInfoMap = (Map<String, Object>) redisUtils.get(taxInfoKey);
                    if (taxInfoMap == null || taxInfoMap.isEmpty()) {
                        //公司信息不存在，请重新登录!
                        msg = "-2";
                    } else {
                        TaxInfo taxInfo = (TaxInfo) taxInfoMap.get("taxInfo");
                        String effictiveStr = "Y";
                        if (taxInfo != null && StringUtils.equals(effictiveStr, taxInfo.getState())) {
                            AuditInfo auditInfo = (AuditInfo) taxInfoMap.get("auditInfo");
                            if (auditInfo == null) {
                                msg = "-3";
                            } else if (StringUtils.equals("2", auditInfo.getState())) {
                                // 该企业状态为未通过，进行重新登录到对应的页面";
                                msg = "-4";
                            } else if (StringUtils.equals("0", auditInfo.getState()) || StringUtils.equals("3", auditInfo.getState())) {
                                msg = "-5";
                            }
                        } else {
                            // 该企业已被作废
                            msg = "-2";
                        }
                    }
                } else {
                    msg = "-1";
                }
            } else {
                // 获取企业状态信息，若不为1-审核通过，直接返回-1
                String entInfoKey = (String) map.get("entInfoKey");
                if (StringUtils.isNotEmpty(entInfoKey)) {
                    // 获取公司信息进行判断
                    Map<String, Object> entInfoMap = (Map<String, Object>) redisUtils.get(entInfoKey);
                    if (entInfoMap == null || entInfoMap.isEmpty()) {
                        //公司信息不存在，请重新登录!
                        msg = "-2";
                    } else {
                        EnterpriseInfo enterpriseInfo = (EnterpriseInfo) entInfoMap.get("entInfo");
                        String effictiveStr = "Y";
                        if (enterpriseInfo != null && StringUtils.equals(effictiveStr, enterpriseInfo.getState())) {
                            AuditInfo auditInfo = (AuditInfo) entInfoMap.get("auditInfo");
                            if (auditInfo == null) {
                                msg = "-3";
                            } else if (StringUtils.equals("2", auditInfo.getState())) {
                                // 该企业状态为未通过，进行重新登录到对应的页面";
                                msg = "-4";
                            } else if (StringUtils.equals("0", auditInfo.getState()) || StringUtils.equals("3", auditInfo.getState())) {
                                msg = "-5";
                            }
                        } else {
                            // 该企业已被作废
                            msg = "-2";
                        }
                    }
                } else {
                    msg = "-1";
                }
            }
        }
        return msg;
    }
}
