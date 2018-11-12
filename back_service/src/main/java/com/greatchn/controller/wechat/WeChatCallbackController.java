package com.greatchn.controller.wechat;

import com.greatchn.common.asynchronous.WeChatCallBackTask;
import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.service.wechat.AccessTokenService;
import com.greatchn.service.wechat.AuthService;
import com.greatchn.vo.CallBackInfoVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.*;

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

    @Resource
    WeChatCallBackTask weChatCallBackTask;

    @Resource
    AccessTokenService accessTokenService;

    /**
     * 企业版第三方应用的微信指令回调地址，微信推送suit_ticket接收地址
     *
     * @param request       获取请求包体的request对象
     * @param msg_signature 签名数据
     * @param timestamp     时间戳
     * @param nonce         随机数
     * @param echostr       验证url特有的加密信息
     * @author zy 2018-9-13
     **/
    @RequestMapping("/receiveEnt")
    public String receiveTheCallbackOfEnt(HttpServletRequest request, String msg_signature, String timestamp, String nonce, String echostr) {
        if (StringUtils.isNotBlank(echostr)) {
            // 该次访问为验证url，直接调用解密方法
            return weChatCallBackTask.decryptAndVerifyURL(msg_signature, timestamp, nonce, echostr, Constant.GET_INFO_TYPE_ENT);
        } else {
            int len = request.getContentLength();
            byte[] callbackBody = new byte[len];
            try (ServletInputStream sis = request.getInputStream()) {
                sis.read(callbackBody, 0, len);
                String sReqData = new String(callbackBody);
                CallBackInfoVo callBackInfoVo = new CallBackInfoVo(msg_signature, timestamp, nonce, sReqData);
                weChatCallBackTask.decryptEntCallbackInfo(callBackInfoVo);
            } catch (IOException e) {
                // 获取信息密文错误
                logger.error("获取信息密文错误", e);
            }
        }
        return "success";
    }

    /**
     * 税务局第三方应用的微信指令回调地址，微信推送suit_ticket接收地址
     *
     * @param request       获取请求包体的request对象
     * @param msg_signature 签名数据
     * @param timestamp     时间戳
     * @param nonce         随机数
     * @param echostr       验证url特有的信息
     * @author zy 2018-9-13
     **/
    @RequestMapping("/receiveTax")
    public String receiveTheCallbackOfTax(HttpServletRequest request, String msg_signature, String timestamp, String nonce, String echostr) {
        if (StringUtils.isNotBlank(echostr)) {
            // 该次访问为验证url，直接调用解密方法
            return weChatCallBackTask.decryptAndVerifyURL(msg_signature, timestamp, nonce, echostr, Constant.GET_INFO_TYPE_TAX);
        } else {
            int len = request.getContentLength();
            byte[] callbackBody = new byte[len];
            try (ServletInputStream sis = request.getInputStream()) {
                sis.read(callbackBody, 0, len);
                String sReqData = new String(callbackBody);
                CallBackInfoVo callBackInfoVo = new CallBackInfoVo(msg_signature, timestamp, nonce, sReqData);
                weChatCallBackTask.decryptTaxCallbackInfo(callBackInfoVo);
            } catch (IOException e) {
                // 获取信息密文错误
                logger.error("获取信息密文错误", e);
            }
        }
        return "success";
    }


    /**
     * 微信用户消息回调地址
     *
     * @param request       获取请求包体的request对象
     * @param msg_signature 签名数据
     * @param timestamp     时间戳
     * @param nonce         随机数
     * @param echostr       验证url特有的加密信息
     * @author zy 2018-11-07
     **/
    @RequestMapping("/userMsgAccept")
    public String userMsgAccept(HttpServletRequest request, String msg_signature, String timestamp, String nonce, String echostr) {
        if (StringUtils.isNotBlank(echostr)) {
            // 该次访问为验证url，直接调用解密方法
            return weChatCallBackTask.decryptAndVerifyURL(msg_signature, timestamp, nonce, echostr, Constant.GET_INFO_TYPE_ENT);
        }
        // TODO 接收用户消息的处理
        return "success";
    }

    /**
     * 税务局第三方应用
     *
     * @param request       获取请求包体的request对象
     * @param msg_signature 签名数据
     * @param timestamp     时间戳
     * @param nonce         随机数
     * @param echostr       验证url特有的加密信息
     * @author zy 2018-11-07
     **/
    @RequestMapping("/userMsgAcceptTax")
    public String userMsgAcceptTax(HttpServletRequest request, String msg_signature, String timestamp, String nonce, String echostr) {
        if (StringUtils.isNotBlank(echostr)) {
            // 该次访问为验证url，直接调用解密方法
            return weChatCallBackTask.decryptAndVerifyURL(msg_signature, timestamp, nonce, echostr, Constant.GET_INFO_TYPE_TAX);
        }
        // TODO 接收用户消息的处理
        return "success";
    }

    /**
     * 获取节点内容测试
     */
    @RequestMapping("/getXmlRoot")
    public void getXmlRoot() {
       /* String sMsg ="<xml><SuiteId><![CDATA[wx37df32191487c6e8]]></SuiteId><InfoType><![CDATA[suite_ticket]]></InfoType><TimeStamp>1541553630</TimeStamp><SuiteTicket><![CDATA[vGbC1CH8DIl89pfpiYhvvFxd07ienGpiHiI85olJM_wmFWlO-JXv_PptQSmz2knM]]></SuiteTicket></xml>";
        logger.error("解密后的明文msg: " + sMsg);
        // 创建一个解析document的工厂
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // 创建一个builder对象
        DocumentBuilder db = dbf.newDocumentBuilder();
        // 字符流读取xml
        StringReader sr = new StringReader(sMsg);
        // 将字符流转化为inputSource
        InputSource is = new InputSource(sr);
        // 使用builder对象将流转化为document对象
        Document document = db.parse(is);
        // 获取根节点
        Element root = document.getDocumentElement();

        Node node = root.getElementsByTagName("SuiteTicket") == null ? null : root.getElementsByTagName("SuiteTicket").item(0);
      String value=(node == null ? null : node.getTextContent());
       logger.info("value===="+value);

        String suiteAccesstoken=accessTokenService.getSuiteAccessToken(Constant.ENTERPRISE_SUITE_ID,Constant.ENTERPRISE_SUITE_SECRET);
        logger.info("suiteAccessToken"+suiteAccesstoken);*/
    }

}
