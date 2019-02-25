package com.greatchn.service;

import com.greatchn.bean.Page;
import com.greatchn.bean.PageData;
import com.greatchn.common.dao.BaseDao;
import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.TimeUtils;
import com.greatchn.po.MessageInfo;
import com.greatchn.po.Receipt;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.type.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

/**
 * @author Administrator
 * @date 2018-09-12 19:38
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class UserMessageSrv {

    @Resource
    BaseDao baseDao;
    @Resource
    RestfulSrv restfulSrv;

    /**
     * 功能描述: 消息列表
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    public List<Map<String, Object>> getList(Page page, Integer userId, Integer taxId,Integer enterpriseId) {
        List<Object> params = new ArrayList<>();
        String sql = "select ROLE_ID roleId from ent_user_role where 1=1 ";
        if (userId != null) {
            sql += " and USER_ID = ?";
            params.add(userId);
        }
        Map<String, Type> types = new HashMap<>(1);
        types.put("roleId", IntegerType.INSTANCE);
        List<Map<String, Object>> list = baseDao.queryBySQL(sql, types, params.toArray());
        String sqlTime = "select PASS_TIME from audit_info where ENT_TAX_ID = ? and AUDIT_TYPE = '1' ";
        Map<String,Type> timeTypes = new HashMap<>(1);
        timeTypes.put("PASS_TIME",TimestampType.INSTANCE);
        Map<String,Object> passTime = baseDao.uniqueBySQL(sqlTime,timeTypes,enterpriseId);
        List<Map<String, Object>> message = new ArrayList<>();
        if (list != null && list.size() > 0) {
            String roles = "";
            for (Map<String, Object> map : list) {
                roles += map.get("roleId").toString() + "|";
            }
            roles = roles.substring(0, roles.length() - 1);
            message.addAll(getUserList(userId, roles, page, taxId,enterpriseId,passTime.get("PASS_TIME").toString()));
        } else {
            message.addAll(getUserList(userId, null, page, taxId,enterpriseId,passTime.get("PASS_TIME").toString()));
        }

        return message;
    }

    /**
     * 功能描述: 获取用户所可以查看的信息
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    public List<Map<String, Object>> getUserList(Integer userId, String roleId, Page page, Integer taxId,Integer enterpriseId,String passTime) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT " +
                "m.*, " +
                "uir.userId " +
                "FROM " +
                "( " +
                "SELECT " +
                "mi.ID id, " +
                "mi.TITLE title, " +
                "mi.CONTENT content, " +
                "mi.CREATE_TIME createTime, " +
                "mi.MSGTYPE msgtype " +
                "FROM " +
                "message_info mi " +
                "WHERE 1=1 ";
        if (StringUtils.isNotEmpty(roleId)) {
            sql += "and ( ROLE_ID regexp ? OR ROLE_ID is NULL ) ";
            params.add(roleId);
        } else {
            sql += "and ROLE_ID is NULL ";
        }
        if (taxId != null) {
            sql += "and TAX_ID = ? ";
            params.add(taxId);
        }
        if (enterpriseId!=null) {
            sql += "and (ENTERPRISE_ID REGEXP ? OR ENTERPRISE_ID is NULL) ";
            params.add(enterpriseId);
        }
        sql += "and STATE <> '2' and STATE <> '0'" +
                ") AS m " +
                "LEFT JOIN ( " +
                "SELECT " +
                "ui.userId, " +
                "r.MESSAGE_ID messageId " +
                "FROM " +
                "receipt r " +
                "inner join ( " +
                "SELECT " +
                "eui.ID userId, " +
                "eui.ENTERPRISE_ID enterpriseId " +
                "FROM " +
                "ent_user_info eui " +
                "INNER JOIN audit_info ai ON eui.ENTERPRISE_ID = ai.ENT_TAX_ID " +
                "WHERE 1=1 AND ai.AUDIT_TYPE = '1' AND ai.STATE = '1' ";
        if (userId != null) {
            sql += "and eui.ID = ? ";
            params.add(userId);
        }
        sql += ") AS ui ON r.USER_ID = ui.userId " +
                ") AS uir ON m.Id = uir.messageId " +
                "where 1=1 ";
        if (StringUtils.isNotEmpty(passTime)){
            sql += "and m.createTime >= ? ";
            params.add(passTime);
        }
        sql += " order by m.createTime desc";
        String sqlCount = "select count(s.ID) num from ( " + sql + ") s ";
        Map<String, Type> types = new HashMap<>(16);
        types.put("id", IntegerType.INSTANCE);
        types.put("title", StringType.INSTANCE);
        types.put("content", StringType.INSTANCE);
        types.put("createTime", TimestampType.INSTANCE);
        types.put("msgtype", StringType.INSTANCE);
        types.put("userId", IntegerType.INSTANCE);
        Map<String, Type> typeNum = new HashMap<>(16);
        typeNum.put("num", LongType.INSTANCE);
        Map<String, Object> countNum = baseDao.uniqueBySQL(sqlCount, typeNum, params.toArray());
        int count = Integer.valueOf(String.valueOf(countNum.get("num")));
        // 记录总数
        page.setTotalResult(count);
        // 记录页数
        page.setTotalPage(page.getTotalPage());
        List<Map<String, Object>> list = baseDao.queryBySQL(sql, types, page.getCurrentPage(), page.getShowCount(), params.toArray());
        return list;
    }

    /**
     * 功能描述: 消息详情
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    public MessageInfo getMessageDetail(Integer messageId) {
        return baseDao.get(MessageInfo.class, messageId);
    }

    /**
     * 功能描述: 回执
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean stateReceipt(Integer messageId, Integer userId,Integer enterpriseId) {
        boolean flag = false;
        List<Receipt> receipt = findReceipt(messageId, userId);
        if (receipt == null || receipt.size() == 0) {
            saveReceipt(messageId, userId, enterpriseId);
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    /**
     * 功能描述: 保存回执
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveReceipt(Integer messageId, Integer userId, Integer enterpriseId) {
        Receipt receipt = new Receipt();
        receipt.setMessageId(messageId);
        receipt.setUserId(userId);
        receipt.setTime(new Timestamp(System.currentTimeMillis()));
        receipt.setEnterpriseId(enterpriseId);
        baseDao.save(receipt);
    }

    /**
     * 功能描述: 查询回执
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    public List<Receipt> findReceipt(Integer messageId, Integer userId) {
        List<Object> params = new ArrayList<>();
        String sql = "select * from receipt where MESSAGE_ID = ? and USER_ID = ?";
        params.add(messageId);
        params.add(userId);
        return baseDao.queryBySql(sql, Receipt.class, params.toArray());
    }

    /**
     * 功能描述: 生成消息
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveMessage(Integer messageId, String type, String title, String content, Integer userId, String roleIds, Integer taxId, String enterpriseIds) {
        MessageInfo messageInfo = new MessageInfo();
        if (messageId != null) {
            messageInfo = getMessageDetail(messageId);
        }
        messageInfo.setType("1");
        messageInfo.setTitle(title);
        messageInfo.setContent(content);
        messageInfo.setCreateUserId(userId);
        messageInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
        messageInfo.setMsgtype("textcard");
        if (StringUtils.isNotEmpty(roleIds)) {
            messageInfo.setRoleId(roleIds);
            messageInfo.setType(type);
        }
        messageInfo.setState("0");
        messageInfo.setTaxId(taxId);
        if (StringUtils.isNotEmpty(enterpriseIds)){
            messageInfo.setEnterpriseId(enterpriseIds);
        }
        baseDao.saveOrUpdate(messageInfo);
    }

    /**
     * 功能描述:消息列表
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    public List<Map<String, Object>> messageList(Page page, Integer taxId) {
        // modify by zy 2018-10-18 增加查询条件
        List<Object> params = new ArrayList<>();
        PageData pd = page.getPd();
        // 关键字的搜索范围
        String scope = pd.getString("scope");
        String keywords = pd.getString("keywords");
        // 创建时间起
        String startTime = pd.getString("startTime");
        // 创建时间止
        String endTime = pd.getString("endTime");
        // 创建人
        String userId = pd.getString("userId");
        // 消息的发送状态（0-未发送 1-已发送）
        String state = pd.getString("state");
        String conditionSql = "from message_info mi inner join tax_user_info tui on mi.CREATE_USER_ID = tui.ID inner join employee_info ei on ei.ID = tui.EMPLOYEE_ID where mi.STATE <> '2' ";
        if (StringUtils.isNotEmpty(keywords)) {
            //title-仅搜索标题包含关键字的
            if (StringUtils.equals(Constant.SEARCH_SCOPE_TITLE, scope)) {
                conditionSql += "and mi.TITLE like ? ";
            } else if (StringUtils.equals(Constant.SEARCH_SCOPE_CONTENT, scope)) {
                // content-内容包含关键字的
                conditionSql += "and mi.CONTENT like ? ";
            } else if (StringUtils.equals(Constant.SEARCH_SCOPE_TITLE_AND_CONTENT, scope)) {
                // and-标题和内容都包含关键字
                conditionSql += "and mi.TITLE like ? and mi.CONTENT like ? ";
                params.add("%" + keywords + "%");
            } else {
                // or/其他-标题或内容包含关键字
                conditionSql += "and (mi.TITLE like ? or mi.CONTENT like ?) ";
                params.add("%" + keywords + "%");
            }
            params.add("%" + keywords + "%");
        }
        // 开始时间
        if (StringUtils.isNotEmpty(startTime)) {
            conditionSql += "and mi.CREATE_TIME>=? ";
            params.add(startTime);
        }
        // 结束时间
        if (StringUtils.isNotEmpty(endTime)) {
            conditionSql += "and mi.CREATE_TIME<=? ";
            params.add(endTime);
        }
        // 创建人
        if (StringUtils.isNotEmpty(userId) && Integer.valueOf(userId) > 0) {
            conditionSql += "and mi.CREATE_USER_ID=? ";
            params.add(Integer.valueOf(userId));
        }
        // 消息状态
        if (StringUtils.isNotEmpty(state)) {
            conditionSql += "and mi.STATE=? ";
            params.add(state);
        }
        //筛选分局
        if (taxId != null) {
            conditionSql += "and mi.TAX_ID = ? ";
            params.add(taxId);
        }
        String sql = "select mi.*,ei.NAME createName " + conditionSql + "order by mi.CREATE_TIME desc ";
        // modify by zy 简化原有的查询总记录数sql,不使用子查询
        String sqlCount = "select count(mi.ID) num " + conditionSql;
        Map<String, Type> types = new HashMap<>(16);
        types.put("ID", IntegerType.INSTANCE);
        types.put("TYPE", StringType.INSTANCE);
        types.put("TITLE", StringType.INSTANCE);
        types.put("CONTENT", StringType.INSTANCE);
        types.put("STATE", StringType.INSTANCE);
        types.put("CREATE_USER_ID", IntegerType.INSTANCE);
        types.put("CREATE_TIME", TimestampType.INSTANCE);
        types.put("MSGTYPE", StringType.INSTANCE);
        types.put("createName", StringType.INSTANCE);
        Map<String, Type> typeNum = new HashMap<>(1);
        typeNum.put("num", LongType.INSTANCE);
        Map<String, Object> countNum = baseDao.uniqueBySQL(sqlCount, typeNum, params.toArray());
        int count = Integer.valueOf(String.valueOf(countNum.get("num")));
        // 记录总数
        page.setTotalResult(count);
        return baseDao.queryBySQL(sql, types, page.getCurrentPage(), page.getShowCount(), params.toArray());
    }

    /**
     * 功能描述:保存并发送
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @Transactional(rollbackFor = Exception.class)
    public String saveAndSend(String type, String title, String content, Integer userId, String roleIds, Integer taxId, String enterpriseIds) throws Exception {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setType(type);
        messageInfo.setTitle(title);
        messageInfo.setContent(content);
        messageInfo.setCreateUserId(userId);
        messageInfo.setCreateTime(new Timestamp(System.currentTimeMillis()));
        messageInfo.setMsgtype("textcard");
        if (StringUtils.isNotEmpty(roleIds)) {
            messageInfo.setRoleId(roleIds);
        }
        messageInfo.setState("1");
        messageInfo.setTaxId(taxId);
        if (StringUtils.isNotEmpty(enterpriseIds)){
            messageInfo.setEnterpriseId(enterpriseIds);
        }
        baseDao.save(messageInfo);
        Integer messageId = messageInfo.getId();
        String[] roleId = roleIds.split(",");
        String result = "";
        String url = String.format(Constant.messageUrl, messageId);
        if (StringUtils.isNotEmpty(enterpriseIds)){
            String[] enterpriseId = enterpriseIds.split(",");
            for (int i =0;i<enterpriseId.length;i++){
                if (StringUtils.isNotEmpty(roleIds)) {
                    String role = "";
                    for (int j = 0; j < roleId.length; j++) {
                        role += roleId[j] + "|";
                    }
                    result += restfulSrv.sendMessageOrQuestion("【消息推送】" + title, content, role.substring(0, role.length() - 1), url,enterpriseId[i],taxId);
                } else {
                    result = restfulSrv.sendMessageOrQuestion("【消息推送】" + title, content, null, url,enterpriseId[i],taxId);
                }
            }
        } else {
            if (StringUtils.isNotEmpty(roleIds)) {
                String role = "";
                for (int i = 0; i < roleId.length; i++) {
                    role += roleId[i] + "|";
                }
                result += restfulSrv.sendMessageOrQuestion("【消息推送】" + title, content, role.substring(0, role.length() - 1), url,null,taxId);
            } else {
                result = restfulSrv.sendMessageOrQuestion("【消息推送】" + title, content, null, url,null,taxId);
            }
        }
        return result;
    }


    /**
     * 功能描述:作废信息
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteMessage(Integer messageId, Integer userId) {
        MessageInfo messageInfo = getMessageDetail(messageId);
        messageInfo.setState("2");
        messageInfo.setModifyTime(new Timestamp(System.currentTimeMillis()));
        messageInfo.setModifyUserId(userId);
        baseDao.saveOrUpdate(messageInfo);
    }

    /**
     * 功能描述:修改信息状态
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void updateMessage(MessageInfo messageInfo, Integer userId, String state) {
        messageInfo.setState(state);
        messageInfo.setModifyTime(new Timestamp(System.currentTimeMillis()));
        messageInfo.setModifyUserId(userId);
        baseDao.saveOrUpdate(messageInfo);
    }


    /**
     * 固定格式时间获取
     */
    public static Map<String, Object> getTimesSimpleFormate(String startTime, String endTime) throws ParseException {
        Map<String, Object> map = new HashMap<>(2);
        Date startDate = null;
        Date endDate = null;
        if (StringUtils.isNotEmpty(startTime)) {
            startTime = startTime + " 00:00:00";
            startDate = TimeUtils.parse(startTime, TimeUtils.FULL_DATE_TIME_FORMAT);
        }
        if (StringUtils.isNotEmpty(endTime)) {
            endTime = endTime + " 23:59:59";
            endDate = TimeUtils.parse(endTime, TimeUtils.FULL_DATE_TIME_FORMAT);
        }
        if (StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
            if (endDate.before(startDate)) {
                // 结束时间必须大于开始时间
                map.put("msg", "结束时间不能大于开始时间");
            }
        }
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        return map;
    }

    public List<Map<String, Object>> receiptList(Integer messageId, Integer enterpriseId,String role) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT " +
                "ur.roleName, " +
                "ur.name, " +
                "r.userId, " +
                "r.time " +
                "FROM " +
                "( " +
                "SELECT " +
                "eri.DESCRIPTION roleName, " +
                "ei.`NAME` NAME, " +
                "eui.ID userId, " +
                "eui.ENTERPRISE_ID enterpriseId " +
                "FROM " +
                "ent_role_info eri " +
                "INNER JOIN ent_user_role eur ON eri.ID = eur.ROLE_ID " +
                "INNER JOIN ent_user_info eui ON eur.USER_ID = eui.ID " +
                "INNER JOIN employee_info ei ON eui.EMPLOYEE_ID = ei.ID " +
                "WHERE 1 = 1 " +
                "AND eri.ID <> 6 " +
                "AND eri.ID REGEXP ? " +
                "AND eui.ENTERPRISE_ID = ? " +
                ") AS ur " +
                "LEFT JOIN ( " +
                "SELECT " +
                "ri.USER_ID userId, " +
                "ri.TIME time " +
                "FROM " +
                "receipt ri " +
                "WHERE " +
                "ri.MESSAGE_ID = ? " +
                ") AS r ON ur.userId = r.userId";
        params.add(role);
        params.add(enterpriseId);
        params.add(messageId);
        Map<String, Type> types = new HashMap<>();
        types.put("roleName", StringType.INSTANCE);
        types.put("name", StringType.INSTANCE);
        types.put("userId",IntegerType.INSTANCE);
        types.put("time",TimestampType.INSTANCE);
        List<Map<String, Object>> list = baseDao.queryBySQL(sql, types, params.toArray());
        return list;
    }
}
