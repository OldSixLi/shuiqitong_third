package com.greatchn.controller;

import com.greatchn.bean.Page;
import com.greatchn.bean.PageData;
import com.greatchn.bean.Result;
import com.greatchn.bean.SimplePage;
import com.greatchn.common.annotation.LoginRequired;
import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.po.*;
import com.greatchn.service.EnterpriseSrv;
import com.greatchn.service.QuestionSrv;
import com.greatchn.service.RestfulSrv;
import com.greatchn.service.UserMessageSrv;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @date 2018-10-15 09:16
 */
@RestController
@RequestMapping("/tax/message")
@LoginRequired
public class TaxUserMessageController extends BaseController {

    @Resource
    UserMessageSrv userMessageSrv;
    @Resource
    RestfulSrv restfulSrv;
    @Resource
    QuestionSrv questionSrv;
    @Resource
    RedisUtils redisUtils;
    @Resource
    EnterpriseSrv enterpriseSrv;

    /**
     * 功能描述:消息列表
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/messageList")
    @SuppressWarnings("unchecked")
    public Result messageList(Page page, @RequestHeader("token") String token) {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map != null && !map.isEmpty()) {
            Map<String, Object> taxMap = (Map<String, Object>) redisUtils.get((String) map.get("taxInfoKey"));
            String taxInfoRedisKey = "taxInfo";
            if (taxMap != null && !taxMap.isEmpty() && taxMap.get(taxInfoRedisKey) != null) {
                TaxInfo taxInfo = (TaxInfo) taxMap.get(taxInfoRedisKey);
                PageData pd = this.getPageData();
                page.setPd(pd);
                List<Map<String, Object>> list = userMessageSrv.messageList(page, taxInfo.getId());
                SimplePage simplePage = new SimplePage(page, list);
                return Result.success(simplePage);
            } else {
                // 税务分局信息丢失，需要重新登录
                return Result.fail("-1");
            }
        } else {
            return Result.fail("-1");
        }
    }

    /**
     * 功能描述:保存并发送
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/saveAndSend")
    @SuppressWarnings("unchecked")
    public Result saveAndSend(String type, String title, String content, @RequestHeader("token") String token, String roleId, String enterpriseIds) throws Exception {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map != null && !map.isEmpty()) {
            Map<String, Object> taxMap = (Map<String, Object>) redisUtils.get((String) map.get("taxInfoKey"));
            String taxInfoRedisKey = "taxInfo";
            if (taxMap != null && !taxMap.isEmpty() && taxMap.get(taxInfoRedisKey) != null) {
                TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
                TaxInfo taxInfo = (TaxInfo) taxMap.get("taxInfo");
                if (StringUtils.isNotEmpty(title) && StringUtils.isNotEmpty(content)) {
                    String result = userMessageSrv.saveAndSend(type, title, content, userInfo.getId(), roleId, taxInfo.getId(), enterpriseIds);
                    return Result.success(result);
                } else {
                    return Result.fail("缺少必要参数");
                }
            } else {
                return Result.fail("-1");
            }
        } else {
            return Result.fail("无用户信息");
        }

    }

    /**
     * 功能描述:作废信息
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/deleteMessage")
    @SuppressWarnings("unchecked")
    public Result deleteMessage(Integer messageId, @RequestHeader("token") String token) {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map != null && !map.isEmpty()) {
            TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
            if (messageId != null) {
                userMessageSrv.deleteMessage(messageId, userInfo.getId());
                return Result.success("");
            } else {
                return Result.fail("缺少必要参数");
            }
        } else {
            return Result.fail("无用户信息");
        }
    }

    /**
     * 功能描述:发送消息或问卷
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/sendMessageOrQuestion")
    @SuppressWarnings("unchecked")
    public Result sendMessageOrQuestion(Integer messageId, Integer qnaId, @RequestHeader("token") String token) throws Exception {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map != null && !map.isEmpty()) {
            TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
            String url = "";
            String title = "";
            String content = "";
            String corpIds = "";
            List<Integer> roleIds = new ArrayList<>();
            if (messageId != null || qnaId != null) {
                if (messageId != null) {
                    url = String.format(Constant.messageUrl, messageId);
                    MessageInfo messageInfo = userMessageSrv.getMessageDetail(messageId);
                    title = "【消息推送】" + messageInfo.getTitle();
                    content = messageInfo.getContent();
                    if (StringUtils.isNotEmpty(messageInfo.getRoleId())) {
                        String[] roleId = messageInfo.getRoleId().split(",");
                        for (int i = 0; i < roleId.length; i++) {
                            roleIds.add(Integer.valueOf(roleId[i]));
                        }
                    }
                    corpIds = messageInfo.getEnterpriseId();
                    userMessageSrv.updateMessage(messageInfo, userInfo.getId(), "1");
                }
                if (qnaId != null) {
                    url = String.format(Constant.questionUrl, qnaId);
                    QNAInfo qnaInfo = questionSrv.findQNA(qnaId);
                    title = "【问卷调查】" + qnaInfo.getTitle();
                    content = qnaInfo.getContent();
                    if (StringUtils.isNotEmpty(qnaInfo.getRoleId())) {
                        String[] roleId = qnaInfo.getRoleId().split(",");
                        for (int i = 0; i < roleId.length; i++) {
                            roleIds.add(Integer.valueOf(roleId[i]));
                        }
                        for (int i = 0; i < roleId.length; i++) {
                            roleIds.add(Integer.valueOf(roleId[i]));
                        }
                    }
                    corpIds = qnaInfo.getEnterpriseId();
                    questionSrv.updateQnaState(qnaInfo, userInfo.getId(), "5");
                }
                String result = "";
                if (StringUtils.isNotEmpty(corpIds)) {
                    String[] corpId = corpIds.split(",");
                    for (int i=0;i<corpId.length;i++){
                        if (roleIds.size() > 0 && roleIds != null) {
                            String role = "";
                            for (Integer roleId : roleIds) {
                                role += roleId + "|";
                            }
                            result += restfulSrv.sendMessageOrQuestion(title, content, role.substring(0, role.length() - 1), url, corpId[i]);
                        } else {
                            result = restfulSrv.sendMessageOrQuestion(title, content, null, url, corpId[i]);
                        }
                    }
                } else {
                    if (roleIds.size() > 0 && roleIds != null) {
                        String role = "";
                        for (Integer roleId : roleIds) {
                            role += roleId + "|";
                        }
                        result += restfulSrv.sendMessageOrQuestion(title, content, role.substring(0, role.length() - 1), url, null);
                    } else {
                        result = restfulSrv.sendMessageOrQuestion(title, content, null, url, null);
                    }
                }
                return Result.success(result);
            } else {
                return Result.fail("无消息id或无问卷id");
            }
        } else {
            return Result.fail("无用户信息");
        }
    }

    /**
     * 功能描述: 保存消息
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/saveMessage")
    @SuppressWarnings("unchecked")
    public Result saveMessageInfo(Integer messageId, String type, String title, String content, @RequestHeader("token") String token, String roleId, String enterpriseIds) {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map != null && !map.isEmpty()) {
            Map<String, Object> taxMap = (Map<String, Object>) redisUtils.get((String) map.get("taxInfoKey"));
            String taxInfoRedisKey = "taxInfo";
            if (taxMap != null && !taxMap.isEmpty() && taxMap.get(taxInfoRedisKey) != null) {
                TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
                TaxInfo taxInfo = (TaxInfo) taxMap.get("taxInfo");
                if (StringUtils.isNotEmpty(title) && StringUtils.isNotEmpty(content)) {
                    userMessageSrv.saveMessage(messageId, type, title, content, userInfo.getId(), roleId, taxInfo.getId(), enterpriseIds);
                    return Result.success("");
                } else {
                    return Result.fail("缺少必要参数");
                }
            } else {
                return Result.fail("-1");
            }
        } else {
            return Result.fail("无用户信息");
        }
    }

    /**
     * 功能描述: 回执列表
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/receiptList")
    @SuppressWarnings("unchecked")
    public Result receiptList(Page page, Integer messageId,@RequestHeader("token") String token) {
        Map<String,Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map!=null && !map.isEmpty()){
            Map<String, Object> mapTax = (Map<String, Object>) redisUtils.get(map.get("taxInfoKey").toString());
            String taxInfoRedisKey = "taxInfo";
            if (mapTax != null && !mapTax.isEmpty() && mapTax.get(taxInfoRedisKey) != null) {
                TaxInfo taxInfo = (TaxInfo) mapTax.get(taxInfoRedisKey);
                List<Map<String,Object>> list = new ArrayList<>();
                List<Map<String,Object>> enterpriseList = new ArrayList<>();
                MessageInfo message = userMessageSrv.getMessageDetail(messageId);
                String enterpriseIds = message.getEnterpriseId();
                String roleIds = message.getRoleId();
                if(StringUtils.isNotEmpty(enterpriseIds)){
                    String[] enterpriseId = enterpriseIds.split(",");
                    String enterprise = "";
                    for (int i = 0;i<enterpriseId.length;i++){
                        enterprise += enterpriseId[i] + "|";
                    }
                    enterpriseList = enterpriseSrv.getReceiptList(page,taxInfo.getId(),enterprise.substring(0,enterprise.length()-1));
                } else {
                    enterpriseList = enterpriseSrv.getList(page,taxInfo.getId());
                }
                String role = "";
                if (StringUtils.isNotEmpty(roleIds)) {
                    String[] roleId = roleIds.split(",");
                    for (int i = 0; i < roleId.length; i++) {
                        role += roleId[i] + "|";
                    }
                }
                for (Map<String,Object> enterprise : enterpriseList){
                    List<Map<String,Object>> receipt = userMessageSrv.receiptList(messageId,Integer.valueOf(enterprise.get("enterpriseId").toString()),role.substring(0,role.length()-1));
                    enterprise.put("receipt",receipt);
                    list.add(enterprise);
                }
                SimplePage simplePage = new SimplePage(page,list);
                return Result.success(simplePage);
            } else {
                return Result.fail("无当前分局信息");
            }
        } else {
            return Result.fail("无当前分局信息");
        }
    }
}
