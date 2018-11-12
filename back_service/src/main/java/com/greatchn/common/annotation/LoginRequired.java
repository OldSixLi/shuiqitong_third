package com.greatchn.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在需要登录验证的Controller的方法，或类上使用此注解
 *
 * @author zy 2018-9-19
 */
@Target({ElementType.METHOD, ElementType.TYPE})// 可用在方法名上或类上
@Retention(RetentionPolicy.RUNTIME)// 运行时有效
public @interface LoginRequired {
}
