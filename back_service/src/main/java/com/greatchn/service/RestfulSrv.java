package com.greatchn.service;

import com.alibaba.fastjson.JSONObject;
import com.greatchn.bean.Send;
import com.greatchn.bean.SendText;
import com.greatchn.bean.Textcard;
import com.greatchn.common.dao.BaseDao;
import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.HttpUtils;
import com.greatchn.po.QurRes;
import com.greatchn.po.SendErr;
import com.greatchn.service.wechat.AccessTokenService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Administrator
 * @date 2018-09-11 19:03
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class RestfulSrv {

    @Resource
    BaseDao baseDao;

    @Resource
    AccessTokenService accessTokenService;

    private static final String sendInformationPostURL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=%s";

    public String sendInformation(String corpId, String params) throws Exception {
        String url = String.format(sendInformationPostURL, accessTokenService.getAccessTokenByPermanentCode(corpId, "ent").get("accessToken").toString());
        String result = HttpUtils.requestByPost(url, params);
        return result;
    }

    private String sendSuccess = "发送成功";
    private String sendErr = "发送失败";
    private String someErr = "部分发送失败";

    /**
     * 功能描述: 发送问卷或者消息
     *
     * @author songyue
     * @date 2018-09-15 14:13
     * 没有筛选税务局
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public String sendMessageOrQuestion(String title, String content, String roleId, String url, String enterpriseId, Integer taxId) throws Exception {
        String state = "";
        List<Object> params = new ArrayList<>();
        String sql = "";
        if (roleId != null) {
            sql = "select * FROM " +
                    "( " +
                    "SELECT " +
                    "ei.USER_ID userId, " +
                    "e.AGENT_ID agentId, " +
                    "e.ID enterpriseId, " +
                    "GROUP_CONCAT(eur.ROLE_ID) roleId," +
                    "e.CORP_ID corpId " +
                    "FROM " +
                    "ent_user_role eur " +
                    "INNER JOIN ent_user_info eui ON eur.USER_ID = eui.ID " +
                    "INNER JOIN employee_info ei ON eui.EMPLOYEE_ID = ei.ID " +
                    "INNER JOIN enterprise_info e ON ei.ENTERPRISE_ID = e.CORP_ID " +
                    "INNER JOIN audit_info ai ON e.ID = ai.ENT_TAX_ID " +
                    "where eui.STATE = 'Y' and e.STATE = 'Y' ";
            if (StringUtils.isNotEmpty(enterpriseId)) {
                sql += "AND e.ID = ? ";
                params.add(enterpriseId);
            }
            sql += "AND ai.STATE = 1 " +
                    "AND ai.AUDIT_TYPE = 1 " +
                    "AND e.TAX_ID = ? ";
            params.add(taxId);
            sql += "GROUP BY " +
                    "enterpriseId, " +
                    "userid " +
                    ") a WHERE a.roleId regexp ? ";
            params.add(roleId);
        } else {
            sql = "SELECT " +
                    "ei.ID enterpriseId, " +
                    "ei.AGENT_ID agentId, " +
                    "GROUP_CONCAT(ee.userId SEPARATOR '|') userId," +
                    "ei.CORP_ID corpId " +
                    "FROM " +
                    "( " +
                    "SELECT " +
                    "eui.ID id, " +
                    "emi.USER_ID userId, " +
                    "eui.ENTERPRISE_ID enterpriseId, " +
                    "eui.STATE state " +
                    "FROM " +
                    "ent_user_info eui " +
                    "LEFT JOIN employee_info emi ON eui.EMPLOYEE_ID = emi.ID " +
                    ") AS ee " +
                    "LEFT JOIN enterprise_info ei ON ee.enterpriseId = ei.ID " +
                    "INNER JOIN audit_info ai on ei.ID = ai.ENT_TAX_ID " +
                    "WHERE 1 = 1 ";
            if (StringUtils.isNotEmpty(enterpriseId)) {
                sql += "AND ei.ID = ? ";
                params.add(enterpriseId);
            }
            sql += "AND ee.state = 'Y' " +
                    "AND ei.STATE = 'Y' " +
                    "AND ai.STATE = 1 " +
                    "AND ai.AUDIT_TYPE = 1 " +
                    "AND ei.TAX_ID = ? " +
                    "GROUP BY enterpriseId ";
            params.add(taxId);
        }
        Map<String, Type> types = new HashMap<>();
        types.put("enterpriseId", IntegerType.INSTANCE);
        types.put("userId", StringType.INSTANCE);
        types.put("agentId", StringType.INSTANCE);
        types.put("corpId", StringType.INSTANCE);
        List<Map<String, Object>> list = baseDao.queryBySQL(sql, types, params.toArray());
        if (list != null && list.size() > 0) {
            for (Map<String, Object> map : list) {
                if (map != null && map.size() > 0 && map.get("userId") != null && map.get("agentId") != null) {
                    SendText sendText = getSendText(title, content, url, map);
                    String sendJson = JSONObject.toJSONString(sendText);
                    String result = sendInformation(map.get("corpId").toString(), sendJson);
                    Send send = JSONObject.parseObject(result, Send.class);
                    if (send.getErrcode() == 0) {
                        if (StringUtils.isEmpty(send.getInvaliduser()) && StringUtils.isEmpty(send.getInvalidparty()) && StringUtils.isEmpty(send.getInvalidtag())) {
                            state = sendSuccess;
                        } else {
                            SendErr sendErr = new SendErr();
                            if (send.getInvaliduser() != null) {
                                sendErr.setInvalIdUser(send.getInvaliduser());
                            }
                            if (send.getInvalidparty() != null) {
                                sendErr.setInvalIdParty(send.getInvalidparty());
                            }
                            if (send.getInvalidtag() != null) {
                                sendErr.setInvalIdTag(send.getInvalidtag());
                            }
                            sendErr.setEnterpriseId(Integer.valueOf(map.get("enterpriseId").toString()));
                            sendErr.setErrTime(new Timestamp(System.currentTimeMillis()));
                            baseDao.save(sendErr);
                            state = someErr;
                        }
                    } else {
                        state = sendErr;
                    }
                } else {
                    state = "无可对应的企业人员进行发送信息！";
                }
            }
        } else {
            state = "无可对应的企业人员进行发送信息！";
        }
        return state;
    }

    /**
     * 功能描述: 发送问题回答提示信息
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public String sendRes(Integer qrId, String url) throws Exception {
        String state = "此问题还未被回复";
        List<Object> params = new ArrayList<>();
        QurRes qurRes = baseDao.get(QurRes.class, qrId);
        if ("1".equals(qurRes.getState()) && StringUtils.isNotEmpty(qurRes.getResponse())) {
            String sql = "SELECT " +
                    "ei.AGENT_ID agentId, " +
                    "ee.userId, " +
                    "ei.ID enterpriseId," +
                    "ei.CORP_ID corpId " +
                    "FROM " +
                    "( " +
                    "SELECT  " +
                    "eui.ID id, " +
                    "emi.USER_ID userId, " +
                    "eui.ENTERPRISE_ID enterpriseId, " +
                    "eui.STATE state " +
                    "FROM " +
                    "ent_user_info eui " +
                    "LEFT JOIN employee_info emi ON eui.EMPLOYEE_ID = emi.ID " +
                    ") AS ee " +
                    "LEFT JOIN enterprise_info ei ON ee.enterpriseId = ei.ID " +
                    "WHERE " +
                    "ee.id = ? " +
                    "AND ee.state = 'Y' " +
                    "AND ei.STATE = 'Y'";
            params.add(qurRes.getQueUserId());
            Map<String, Type> types = new HashMap<>();
            types.put("agentId", StringType.INSTANCE);
            types.put("userId", StringType.INSTANCE);
            types.put("enterpriseId", IntegerType.INSTANCE);
            types.put("corpId", StringType.INSTANCE);
            Map<String, Object> map = baseDao.uniqueBySQL(sql, types, params.toArray());
            if (map != null && map.size() > 0 && map.get("userId") != null && map.get("agentId") != null) {
                String resTitle = "您的问题已回复！";
                String content = "";
                if (qurRes.getQueTitle().length() >= 10) {
                    content = qurRes.getQueTitle().substring(0, 9) + "...";
                } else {
                    content = qurRes.getQueTitle();
                }
                String resDescription = "您的问题【" + content + "】已解答！请您查看！";
                SendText sendText = getSendText(resTitle, resDescription, url, map);
                String sendJson = JSONObject.toJSONString(sendText);
                String result = sendInformation(map.get("corpId").toString(), sendJson);
                Send send = JSONObject.parseObject(result, Send.class);
                if (send.getErrcode() == 0) {
                    if (StringUtils.isEmpty(send.getInvaliduser()) && StringUtils.isEmpty(send.getInvalidparty()) && StringUtils.isEmpty(send.getInvalidtag())) {
                        state = sendSuccess;
                    } else {
                        SendErr sendErr = new SendErr();
                        if (send.getInvaliduser() != null) {
                            sendErr.setInvalIdUser(send.getInvaliduser());
                        }
                        if (send.getInvalidparty() != null) {
                            sendErr.setInvalIdParty(send.getInvalidparty());
                        }
                        if (send.getInvalidtag() != null) {
                            sendErr.setInvalIdTag(send.getInvalidtag());
                        }
                        sendErr.setEnterpriseId(Integer.valueOf(map.get("enterpriseId").toString()));
                        sendErr.setErrTime(new Timestamp(System.currentTimeMillis()));
                        baseDao.save(sendErr);
                        state = someErr;
                    }
                } else {
                    state = sendErr;
                }
            }
        } else {
            state = "无可对应的企业人员进行发送信息！";
        }
        return state;
    }

    /**
     * 功能描述: 生成问卷或消息报名
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    private SendText getSendText(String title, String content, String url, Map<String, Object> map) {
        SendText sendText = new SendText();
        Textcard textCard = new Textcard();
        textCard.setTitle(title);
        if (content.length() >= 20) {
            textCard.setDescription(content.substring(0, 19) + "...");
        } else {
            textCard.setDescription(content);
        }
        textCard.setUrl(url);
        textCard.setBtntxt("详情");
        sendText.setMsgtype("textcard");
        sendText.setAgentid(map.get("agentId").toString());
        sendText.setTouser(map.get("userId").toString());
        sendText.setTextcard(textCard);
        return sendText;
    }
}