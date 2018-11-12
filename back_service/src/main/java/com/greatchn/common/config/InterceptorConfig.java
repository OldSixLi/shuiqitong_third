package com.greatchn.common.config;


import com.greatchn.common.interceptor.TokenAuthorInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


/**
 * token校验拦截
 *
 * @author zy 2018-9-20
 */
@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {

    @Bean
    public TokenAuthorInterceptor tokenAuthorInterceptor() {
        return new TokenAuthorInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //addPathPatterns 用于添加拦截规则
        //excludePathPatterns 用于排除拦截
        registry.addInterceptor(tokenAuthorInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
