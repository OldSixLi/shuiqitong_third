package com.greatchn.common.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.alibaba.druid.support.spring.stat.DruidStatInterceptor;
import com.greatchn.common.utils.ConstantUtil;
import com.greatchn.common.utils.FastJsonRedisSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.JdkRegexpMethodPointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.*;
import java.util.Properties;

/***
 * 数据源、redis缓存等配置
 *
 * @author zy 2018-9-11
 * */
@Configuration
@EnableConfigurationProperties(DataSourceProperties.class)
@PropertySource(value = {"classpath:base.properties"}, encoding = "utf-8")
public class DruidConfig {

    /**
     * 输出到控制台的日志
     */
    private static Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Resource
    private ConfigurableEnvironment configurableEnvironment;

    @Value("${baseDriver}")
    private String diver;

    @Value("${baseUrl}")
    private String url;

    @Value("${baseUser}")
    private String username;

    @Value("${basePassword}")
    private String password;

    @PostConstruct
    public void initDatabasePropertySourceUsage() {
        // 获取系统属性集合
        MutablePropertySources propertySources = configurableEnvironment.getPropertySources();
        Properties properties = new Properties();
        // 获取数据库链接，读取常量信息
        try {
            logger.info("开始读取配置");
            Class.forName(diver);
            // try自动关闭
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement preparedStatement = connection.prepareStatement("select * from third_config");
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                properties.clear();
                // 将读取到的配置信息放入properties文件中
                while (resultSet.next()) {
                    // 获取配置信息的key
                    String key = resultSet.getString("KEY");
                    // 获取配置信息的value
                    String value = resultSet.getString("VALUE");
                    // 将配置信息的放入properties文件对象中
                    properties.setProperty(key, value);
                }
            }
            // 将读取的配置信息的properties对象放入ConstantUtils，以便使用
            ConstantUtil.init(properties);
            // 将属性转换为属性集合，并指定名称
            PropertiesPropertySource constants = new PropertiesPropertySource("application", properties);
            // 没找到默认添加到第一位
            propertySources.addFirst(constants);
            logger.error("动态加载properties成功");
        } catch (Exception e) {
            logger.error("动态加载properties失败");
        }
    }


    /**
     * 创建druid的数据源
     *
     * @author zy 2018-9-11
     */
    @Bean("druidDataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DruidDataSource dataSource(DataSourceProperties properties) {
        // 读取以spring.datasource开头的配置信息
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(properties.determineDriverClassName());
        dataSource.setUrl(properties.determineUrl());
        dataSource.setUsername(properties.determineUsername());
        dataSource.setPassword(properties.determinePassword());
        DatabaseDriver databaseDriver = DatabaseDriver
                .fromJdbcUrl(properties.determineUrl());
        String validationQuery = databaseDriver.getValidationQuery();
        if (validationQuery != null) {
            dataSource.setTestOnBorrow(false);
            dataSource.setValidationQuery(validationQuery);
        }
        try {
            //开启Druid的监控统计功能，mergeStat代替stat表示sql合并,wall表示防御SQL注入攻击
            dataSource.setFilters("mergeStat,wall,log4j");
        } catch (SQLException e) {
            logger.info("开启Druid的监控统计功能异常", e);
        }
        return dataSource;
    }


    /**
     * 注册一个Druid内置的StatViewServlet，用于展示Druid的统计信息。
     *
     * @author zy 2018-9-13
     */
    @Bean
    public ServletRegistrationBean druidstatviewservlet() {
        //org.springframework.boot.context.embedded.ServletRegistrationBean提供类的进行注册.
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");

        //添加初始化参数：initParams

        //白名单 (没有配置或者为空，则允许所有访问)
        servletRegistrationBean.addInitParameter("allow", "127.0.0.1");
        //IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not permitted to view this page.
        servletRegistrationBean.addInitParameter("deny", "192.168.1.80");
        //登录查看信息的账号密码.
        servletRegistrationBean.addInitParameter("loginUsername", "root");
        servletRegistrationBean.addInitParameter("loginPassword", "123456");
        //是否能够重置数据(禁用HTML页面上的“Reset All”功能)
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }

    /**
     * 注册一个：filterRegistrationBean,添加请求过滤规则
     *
     * @author zy 2018-9-13
     */
    @Bean
    public FilterRegistrationBean druidStatFilter() {

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean<>(new WebStatFilter());

        //添加过滤规则.
        filterRegistrationBean.addUrlPatterns("/*");

        //添加不需要的忽略的格式信息.
        filterRegistrationBean.addInitParameter(
                "exclusions", "*.js,*.gif,*.jpg,*.png,*.css");
        return filterRegistrationBean;
    }


    /**
     * 监听Spring
     * 1.定义拦截器
     * 2.定义切入点
     * 3.定义通知类
     */
    @Bean
    public DruidStatInterceptor druidStatInterceptor() {
        return new DruidStatInterceptor();
    }

    @Bean
    public JdkRegexpMethodPointcut druidStatPointcut() {
        JdkRegexpMethodPointcut druidStatPointcut = new JdkRegexpMethodPointcut();
        String patterns = "com.greatchn.*.*.service.*";
        druidStatPointcut.setPatterns(patterns);
        return druidStatPointcut;
    }

    @Bean
    public Advisor druidStatAdvisor() {
        return new DefaultPointcutAdvisor(druidStatPointcut(), druidStatInterceptor());
    }

    /***
     * redis缓存配置
     *
     * @author zy 2018-9-15
     * */
    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();

        template.setConnectionFactory(factory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用fastJson
        template.setValueSerializer(fastJsonRedisSerializer);
        // hash的value序列化方式采用fastJson
        template.setHashValueSerializer(fastJsonRedisSerializer);
        template.afterPropertiesSet();
        return template;

    }

    /**
     * 跨域设置
     *
     * @author zy 2018-9-18
     */
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        // 设置跨域缓存实践为30分钟
        corsConfiguration.setMaxAge(1800L);
        return corsConfiguration;
    }

    /**
     * 跨域过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }


}
