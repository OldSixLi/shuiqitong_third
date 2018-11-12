package com.greatchn.controller.wechat;

import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.common.utils.weixinUtils.AesException;
import com.greatchn.common.utils.weixinUtils.WXBizMsgCrypt;
import com.greatchn.po.EntUserInfo;
import com.greatchn.service.wechat.AuthService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 企业微信回调控制层
 *
 * @author zy 2018-9-15
 */
@RestController
@RequestMapping("/weChatCall")
public class WeChatCallbackController {

    /**
     * 输出到控制台的日志
     */
    private static Logger logger = LoggerFactory.getLogger(WeChatCallbackController.class);

    @Resource
    AuthService authService;

    @Resource
    RedisUtils redisUtils;

    /**
     * 开启回调模式验证回调Url
     */
    public String checkeCallBackUrl() {

        return null;
    }

    /**
     * 微信推送suit_ticket接收地址
     *
     * @author zy 2018-9-13
     **/
    @RequestMapping("/receive/SuitTicket")
    public String receiveSuitTicket(HttpServletRequest request, String msg_signature, String timestame, String nonce) throws AesException, IOException {
        String sToken = authService.getSuiteAccessToken();
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, Constant.PROVIDER_ENCODING_AES_KEY, Constant.PROVIDER_CORP_ID);
        int len = request.getContentLength();
        byte[] callbackBody = new byte[len];
        try (ServletInputStream sis = request.getInputStream()) {
            sis.read(callbackBody, 0, len);
            String sReqData = new String(callbackBody);
            // post请求的密文数据
            try {
                String sMsg = wxcpt.DecryptMsg(msg_signature, timestame, nonce, sReqData);
                logger.error("解密后的明文msg: " + sMsg);
                // TODO: 解析出明文xml标签的内容进行处理
                // 创建一个解析document的工厂
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                // 创建一个builder对象
                DocumentBuilder db = dbf.newDocumentBuilder();
                // 字符流读取xml
                StringReader sr = new StringReader(sMsg);
                InputSource is = new InputSource(sr);
                // 使用builder对象将流转化为document对象
                Document document = db.parse(is);
                // 获取根节点
                Element root = document.getDocumentElement();
                // 获取相应节点内容
                Node suiteTicketNode = root.getElementsByTagName("SuiteTicket") == null ? null : root.getElementsByTagName("SuiteTicket").item(0);
                String suiteTicket = suiteTicketNode == null ? null : suiteTicketNode.getTextContent();
                if (StringUtils.isNotBlank(suiteTicket)) {
                    // TODO 更新缓存中的数据，若不一致，更新缓存
                    // 服务商的token保持一致直接放入缓存
                    redisUtils.set("suiteTicket", suiteTicket);
                }
            } catch (Exception e) {
                // 解密失败，失败原因请查看异常
                logger.error("解密(解析)失败", e);
            }
        } catch (IOException e) {
            // 获取信息密文错误
            logger.error("获取信息密文错误", e);
        }

        return "success";
    }

    /**
     * 测试加密
     *
     * @author zy 2018-9-13
     **/
    @RequestMapping("/getEnstr")
    public String getEnStr() throws AesException, UnsupportedEncodingException {
        String result = null;
        String sToken = "QDG6eK";
        String sCorpID = "wx5823bf96d3bd56c7";
        String sEncodingAESKey = "jWmYm7qr5nMoAUwZRjGtBxmz3KA1tkAj3ykkR6q2B2C";

        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, sEncodingAESKey, sCorpID);
        String data = "<xml><SuiteId><![CDATA[ww4asffe99e54c0f4c]]></SuiteId><AuthCode><![CDATA[AUTHCODE]]></AuthCode><InfoType><![CDATA[create_auth]]></InfoType><TimeStamp>1403610513</TimeStamp></xml>";
        String timestamp = "1403610513";
        String nonce = "380320359";
        String sEncryptMsg = wxcpt.EncryptMsg(data, timestamp, nonce);
        URLEncoder.encode(sEncryptMsg, "UTF-8");
        return sEncryptMsg;
    }

    /**
     * 从应用市场授权成功的回调
     *
     * @author zy 2018-9-13
     */
    @RequestMapping("/authSuccessFromAppStore")
    public String authSuccessFromAppStore(HttpServletRequest request, String msg_signature, String timestamp, String nonce) throws IOException, AesException {

        String sToken = authService.getSuiteAccessToken();

        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(sToken, Constant.PROVIDER_ENCODING_AES_KEY, Constant.PROVIDER_CORP_ID);

        int len = request.getContentLength();
        byte[] callbackBody = new byte[len];

        try (ServletInputStream sis = request.getInputStream();) {
            sis.read(callbackBody, 0, len);
            String sReqData = new String(callbackBody);
            // post请求的密文数据
            try {
                String sMsg = wxcpt.DecryptMsg(msg_signature, timestamp, nonce, sReqData);
                // TODO: 将解析放入线程中进行
                logger.info("解密后的明文msg: " + sMsg);

                // 创建一个解析document的工厂
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                // 创建一个builder对象
                DocumentBuilder db = dbf.newDocumentBuilder();
                // 字符流读取xml
                StringReader sr = new StringReader(sMsg);
                InputSource is = new InputSource(sr);
                // 使用builder对象将流转化为document对象
                Document document = db.parse(is);
                // 获取根节点
                Element root = document.getDocumentElement();
                // 获取相应节点内容
                Node authCodeNode = root.getElementsByTagName("AuthCode") == null ? null : root.getElementsByTagName("AuthCode").item(0);
                String authCode = authCodeNode == null ? null : authCodeNode.getTextContent();
                if (StringUtils.isNotBlank(authCode)) {
                    //解析出auth_code的具体信息
                    try {
                        // TODO 将获取的信息放入缓存
                        Map<String, Object> map = authService.getAuthInfoByAuthCode(authCode);
                        if (StringUtils.isNotEmpty((String) map.get("msg"))) {
                            logger.error((String) map.get("msg"));
                        }
                    } catch (Exception ex) {
                        logger.error("保存企业初始化信息异常", ex);
                    }
                } else {
                    logger.error("获取临时授权码失败！");
                }

            } catch (Exception e) {
                // 解密失败，失败原因请查看异常
                logger.error("解密(解析)失败", e);
            }
        } catch (IOException e) {
            // 获取信息密文错误
            logger.error("获取信息密文错误", e);
        }
        return "success";

    }

    /**
     * 从服务商网站授权成功的回调
     *
     * @author zy 2018-9-13
     */
    @RequestMapping("/authSuccessFromServiceProvider")
    public void authSuccessFromServiceProvider(String auth_code) {
        // 保存初始化
        //Map<String,Object> map=authService.getAuthInfoByAuthCode(auth_code);


    }


    /* *//**
     * 放置缓存测试
     *
     * *//*
    @RequestMapping("/setRedisTest")
    public void setRedisTest(){
        // String存放
        redisUtils.set("test-String","测试呀测试");
        // Map<String,Object>
        Map<String,Object> map=new HashMap<>();
        map.put("test","测试map");
        redisUtils.set("test_map",map);
        // List<Object>
        List<Object> list=new ArrayList<>();
        list.add("123");
        list.add("456");
        list.add("789");
        list.add(map);
        redisUtils.set("test_list",list);
    }

    */

    /**
     * 获取缓存中信息的测试
     */
    @RequestMapping("/getRedisTest")
    @SuppressWarnings("unchecked")
    public void getRedisTest() {
        // String存放
        System.out.println("String获取测试" + redisUtils.get("abcd"));
        Map<String, Object> map = new HashMap<>();
        map.put("test", "测试map");
        EntUserInfo userInfo = new EntUserInfo();
        userInfo.setId(1);
        map.put("user", userInfo);
        redisUtils.set("test_map", map);
        Map<String, Object> newM = (Map<String, Object>) redisUtils.get("test_map");
        System.out.println("Map<String,Object>获取测试" + ((EntUserInfo) map.get("user")).getId());
        // List<Object>
        System.out.println("List<Object>获取测试" + redisUtils.get("test_list"));
    }
}
