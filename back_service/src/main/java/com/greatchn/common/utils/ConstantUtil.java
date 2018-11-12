package com.greatchn.common.utils;

import com.greatchn.common.config.LogAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.sql.*;
import java.util.Properties;

/**
 * <p>
 * 从properties对象中获取信息
 * </p>
 *
 * @author zy 2018-9-30
 */
public class ConstantUtil {

    /**
     * 输出到控制台的日志
     */
    private static Logger logger = LoggerFactory.getLogger(LogAspect.class);

    public ConstantUtil() {

    }

    public static String getValue(String key) {
        return props.getProperty(key);
    }

    public static void updateProperties(String key, String value) {
        props.setProperty(key, value);
    }

    private static Properties props;

    /**
     * <p>
     * 初始化配置文件方法
     * </p>
     *
     * @author zy 2018-9-27
     */
    public static void init(Properties properties) {
        props = properties;
    }
}
