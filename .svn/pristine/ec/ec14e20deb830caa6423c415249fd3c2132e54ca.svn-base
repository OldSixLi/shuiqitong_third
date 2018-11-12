package com.greatchn.common.config;

import com.alibaba.fastjson.JSONObject;
import com.greatchn.common.exception.GlobalExecption;
import com.greatchn.po.ExceptionLog;
import com.greatchn.po.OperateLog;
import com.greatchn.service.LogService;
import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志切面
 *
 * @author zy 2018-9-12
 */
@Component
@Aspect
public class LogAspect {

    /**
     * 输出到控制台的日志
     */
    private static Logger LOG = LoggerFactory.getLogger(LogAspect.class);

    @Resource
    LogService logService;

    /**
     * 日志切入点
     * 两个..代表所有子目录，最后括号里的两个..代表所有参数
     *
     * @author zy 2018-9-12
     */
    @Pointcut("execution(public * com.greatchn..controller..*.*(..))")
    public void logPointCut() {
    }

    /**
     * 保存异常日志
     *
     * @param e 异常信息对象
     * @author zy 2018-9-12
     */
    private Integer saveExceptionLog(Throwable e) {
        Integer id = null;
        StringWriter writer;
        PrintWriter out;
        try {
            //异常发生时间
            long happenTime = System.currentTimeMillis();
            ExceptionLog exceptionLog = new ExceptionLog();
            exceptionLog.setTime(new Timestamp(happenTime));
            exceptionLog.setMessage(e.getMessage());
            // 具体异常信息
            writer = new StringWriter();
            out = new PrintWriter(writer, true);
            e.printStackTrace(out);
            String stack = writer.toString();
            exceptionLog.setStack(stack);
            logService.saveExceptionLog(exceptionLog);
            id = exceptionLog.getId();
        } catch (Exception ex) {
            LOG.error("保存异常日志错误", ex);
        }
        return id;
    }

    /**
     * 环绕记录日志，并将异常抛出进行统一处理
     *
     * @param pjp 切入点
     * @author zy 2018-9-12
     */
    @Around("logPointCut()")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //请求URI
        String URI=request.getRequestURI();
        // 请求参数
        String parameterStr= JSONObject.toJSONString(request.getParameterMap());

        //控制台日志信息
        String logInfo = "\n%s执行方法：%s.%s %s";
        Integer errorId;
        Integer logId = null;
        OperateLog operateLog = null;
        // 保存日志
        try {
            operateLog = getLogInfo(pjp,request);
            // 访问参数
            operateLog.setParameter(parameterStr);
            logService.saveLog(operateLog);
            logId = operateLog.getId();
        } catch (Exception ex) {
            LOG.error("保存访问日志异常", ex);
        }
        LOG.info(String.format(logInfo, "准备", pjp.getTarget().getClass().getName(), pjp.getSignature().getName(), ",请求的URI为【"+URI+"】，请求参数为" + parameterStr));
        //开始执行方法时间
        long begin = System.currentTimeMillis();
        Object result;
        try {
            result = pjp.proceed();
            //正常结束，设置返回的状态码
        } catch (Throwable t) {
            LOG.info("异常信息", t);
            if (logId != null) {
                errorId = saveExceptionLog(t);
                if (errorId != null) {
                    logService.updateOpreatLog(operateLog, errorId);
                    //将异常抛出统一返回全局异常
                    throw new GlobalExecption(errorId, t);
                } else {
                    //保存异常日志错误直接将错误或异常抛出
                    throw new GlobalExecption(-1, t);
                }
            } else {
                throw new GlobalExecption(-1, t);
            }
        } finally {
            long end = System.currentTimeMillis();
            LOG.info(String.format(logInfo, "结束", pjp.getTarget().getClass().getName(), pjp.getSignature().getName(), "共耗时" + (end - begin) + "毫秒"));
        }
        return result;
    }

    /**
     * 获取操作日志信息
     *
     * @param joinPoint 切入点
     * @author zy 2018-9-12
     */
    private OperateLog getLogInfo(JoinPoint joinPoint,HttpServletRequest request ) {
        OperateLog operateLog = new OperateLog();
        // 记录下请求内容
        // 请求地址
        operateLog.setRequestUrl(request.getRequestURL().toString());
        // get/post
        operateLog.setMethodType(request.getMethod());
        // 访问的类名
        operateLog.setClassName(joinPoint.getSignature().getDeclaringTypeName());
        // 访问的方法名
        operateLog.setMethodName(joinPoint.getSignature().getName());
        // contentType
        operateLog.setRequestContentType(request.getContentType());
        // 客户端ip
        operateLog.setRequestIp(getIpAddr(request));
        // 访问时间
        operateLog.setRequestTime(new Timestamp(System.currentTimeMillis()));

        // 请求头
        Map<String, String> headerMap = new HashMap<>(16);
        Enumeration<String> headerKeys = request.getHeaderNames();
        while (headerKeys.hasMoreElements()) {
            String headerKey = headerKeys.nextElement();
            headerMap.put(headerKey, request.getHeader(headerKey));
        }
        String headerString = JSONObject.toJSONString(headerMap);
        operateLog.setRequest_header(headerString);
        return operateLog;
    }



    /**
     * 获取登录用户远程主机ip地址
     *
     * @param request 请求对象
     * @author zy 2018-9-12
     */
    private String getIpAddr(HttpServletRequest request) {
        String unknownStr="unknown";
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || unknownStr.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknownStr.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknownStr.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
