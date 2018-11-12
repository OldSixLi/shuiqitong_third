package com.greatchn.controller;

import com.greatchn.bean.PageData;
import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 控制层基类
 *
 * @author zy 2018-9-18
 */
public abstract class BaseController {

    /**
     * 控制台日志对象
     */
    protected final Logger log = Logger.getLogger(this.getClass());

    /**
     * 得到PageData
     */
    public PageData getPageData() {
        return new PageData(this.getRequest());
    }

    /**
     * 得到request对象(经源码研究得出RequestContextHolder底层采用ThreadLocal实现，所以无需担心线程安全问题)
     */
    public HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 得到32位的uuid
     */
    public static String get32UUID() {
        return UUID.randomUUID().toString().trim().replaceAll("-", "");
    }

}
