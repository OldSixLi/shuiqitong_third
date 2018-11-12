package com.greatchn.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 通用的http请求工具类（使用httpClient）<br>
 * 请求和响应处理的编码都为utf-8 <br>
 * 若调用无超时时间的请求方法则默认超时时间为sys_config中的default_connection_timeout设置的超时时间，
 * 若数据库默认超时不存在，默认为5000毫秒
 * <p>
 *
 * @author zy 2018-7-23
 */
public class HttpUtils {

    private HttpUtils() {
    }

    private static final Integer DEFAULT_TIMEOUT = Constant.REQUEST_TIME_OUT_DEFALUT;

    /**
     * 发送get请求，默认超时使时间，参数放在map里
     *
     * @param url    请求地址
     * @param params 参数map,无参则传入null
     * @return 响应内容
     * @author zy 2018-7-23
     */
    public static String requstByGet(String url, Map<String, String> params) throws IOException, URISyntaxException {
        return requstByGet(url, params, null);
    }

    /**
     * 发送get请求，可设置超时时间，参数放在map里
     *
     * @param url     请求地址
     * @param params  参数map,无参则传入null
     * @param timeOut 建立连接超时时间 先读取数据库中的默认值，若无，则默认为5000毫秒
     * @return 响应内容
     * @author zy 2018-7-23
     */
    public static String requstByGet(String url, Map<String, String> params, Integer timeOut) throws IOException, URISyntaxException {
        //判断传入url是否为空
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            //判断timeout是否为null
            timeOut = timeOut == null ? DEFAULT_TIMEOUT : timeOut;
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(timeOut).build();
            HttpGet get = null;
            if (params != null) {
                //将参数放入转化为对应的NameValuePair对象
                List<NameValuePair> pairs = new ArrayList<>();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                URIBuilder builder = new URIBuilder(url).addParameters(pairs);
                //请求
                get = new HttpGet(builder.build());
            } else {
                //无参直接请求
                get = new HttpGet(url);
            }
            //设置超时时间
            get.setConfig(requestConfig);
            //获取响应对象
            try (CloseableHttpResponse response = httpClient.execute(get)) {
                //判断响应码，验证是否正确访问
                if (response != null && response.getStatusLine().getStatusCode() == 200) {
                    // 获取响应实体
                    HttpEntity entity = response.getEntity();
                    //返回响应内容
                    return EntityUtils.toString(entity, Constant.SYS_CHARSET);
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * 发送post请求，默认超时时间，参数放在map里
     *
     * @param url    地址
     * @param params 参数map,无参则传入null
     * @return 请求的响应内容
     * @author zy 2018-7-23
     */
    public static String requestByPost(String url, Map<String, String> params) throws IOException {
        return requestByPost(url, params, null);
    }

    /**
     * 发送post请求，参数放在map里
     *
     * @param url     地址
     * @param params  参数map,无参则传入null
     * @param timeOut 建立连接超时时间 先读取数据库中的默认值，若无，则默认为5000毫秒
     * @return 请求的响应内容
     * @author zy 2018-7-23
     */
    public static String requestByPost(String url, Map<String, String> params, Integer timeOut) throws IOException {
        //判断传入url是否为空
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            //判断timeout是否为null
            timeOut = timeOut == null ? DEFAULT_TIMEOUT : timeOut;
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(timeOut).build();
            HttpPost post = new HttpPost(url);
            if (params != null) {
                //将参数放入转化为对应的NameValuePair对象
                List<NameValuePair> pairs = new ArrayList<>();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                //设置编码，并将参数放入post请求对象中
                post.setEntity(new UrlEncodedFormEntity(pairs, Constant.SYS_CHARSET));
            }
            //设置超时时间
            post.setConfig(requestConfig);
            //发起请求
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                if (response != null && response.getStatusLine().getStatusCode() == 200) {
                    //获取响应体
                    HttpEntity entity = response.getEntity();
                    //返回响应内容
                    return EntityUtils.toString(entity, Constant.SYS_CHARSET);

                } else {
                    return null;
                }
            }
        }
    }

    /**
     * 发送post请求，默认超时时间，请求体中放入信息
     *
     * @param url    地址
     * @param bodyStr 请求体中数据
     * @return 请求的响应内容
     * @author zy 2018-7-23
     */
    public static String requestByPost(String url, String bodyStr) throws IOException {
        return requestByPost(url, bodyStr, null);
    }

    /**
     * 发送post请求，参数放在map里
     *
     * @param url     地址
     * @param bodyStr  参数map,无参则传入null
     * @param timeOut 建立连接超时时间 先读取数据库中的默认值，若无，则默认为5000毫秒
     * @return 请求的响应内容
     * @author zy 2018-7-23
     */
    public static String requestByPost(String url, String bodyStr, Integer timeOut) throws IOException {
        //判断传入url是否为空
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            //判断timeout是否为null
            timeOut = timeOut == null ? DEFAULT_TIMEOUT : timeOut;
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(timeOut).build();
            HttpPost post = new HttpPost(url);
            if(StringUtils.isNotBlank(bodyStr)){
                //设置编码，并将参数放入post请求对象中
                StringEntity entity= new StringEntity(bodyStr,Constant.SYS_CHARSET);
                post.setEntity(entity);
            }
            //设置超时时间
            post.setConfig(requestConfig);
            //发起请求
            try (CloseableHttpResponse response = httpClient.execute(post)) {
                if (response != null && response.getStatusLine().getStatusCode() == 200) {
                    //获取响应体
                    HttpEntity entity = response.getEntity();
                    //返回响应内容
                    return EntityUtils.toString(entity, Constant.SYS_CHARSET);

                } else {
                    return null;
                }
            }
        }
    }

}
