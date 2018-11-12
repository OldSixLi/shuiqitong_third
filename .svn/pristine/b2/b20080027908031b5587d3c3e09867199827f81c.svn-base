package com.greatchn.service;

import com.greatchn.bean.Page;
import com.greatchn.bean.PageData;
import com.greatchn.common.dao.BaseDao;
import com.greatchn.common.utils.Constant;
import com.greatchn.po.QurRes;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.type.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @date 2018-09-13 13:57
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class MyQueResSrv {

    @Resource
    BaseDao baseDao;
    @Resource
    RestfulSrv restfulSrv;

    /**
     * 功能描述: 提问列表
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    public List<Map<String, Object>> queResList(Page page, Integer userId) {
        List<Object> params = new ArrayList<>();
        String sql = "select ID id,QUE_TITLE queTitle,QUESTION question,QUE_USER_ID queUserId,QUE_TIME queTime,STATE state,RES_TIME resTime from que_res where 1=1 ";
        if (userId != null) {
            sql += "and QUE_USER_ID = ? ";
            params.add(userId);
        }
        if (page.getPd().get("state") != null && !"".equals(page.getPd().getString("state"))) {
            sql += "and STATE = ? ";
            params.add(page.getPd().getString("state"));
            if ("0".equals(page.getPd().getString("state"))) {
                sql += " order by queTime desc";
            }
            if ("1".equals(page.getPd().getString("state"))) {
                sql += " order by resTime desc";
            }
        }
        String sqlCount = "select count(s.ID) num from ( " + sql + ") s ";
        Map<String, Type> types = new HashMap<>();
        types.put("id", IntegerType.INSTANCE);
        types.put("queTitle", StringType.INSTANCE);
        types.put("question", StringType.INSTANCE);
        types.put("queUserId", StringType.INSTANCE);
        types.put("queTime", TimestampType.INSTANCE);
        types.put("state", StringType.INSTANCE);
        types.put("resTime", TimestampType.INSTANCE);
        Map<String, Type> typeNum = new HashMap<>();
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
     * 功能描述: 提问详情
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    public QurRes getQueResDetail(Integer qurResId) {
        QurRes qurRes = baseDao.get(QurRes.class, qurResId);
        return qurRes;
    }
    public Map<String, Object> findQueResDetail(Integer qurResId) {
        List<Object> params = new ArrayList<>();
        String sql = "select qr.*,ei.NAME resName from que_res qr left join tax_user_info tui on qr.RES_USER_ID = tui.ID left join employee_info ei on ei.ID = tui.EMPLOYEE_ID where qr.ID = ?";
        params.add(qurResId);
        Map<String, Type> types = new HashMap<>();
        types.put("ID",IntegerType.INSTANCE);
        types.put("QUE_TITLE",StringType.INSTANCE);
        types.put("QUESTION",StringType.INSTANCE);
        types.put("QUE_USER_ID",IntegerType.INSTANCE);
        types.put("QUE_TIME",TimestampType.INSTANCE);
        types.put("RESPONSE",StringType.INSTANCE);
        types.put("RES_TIME",TimestampType.INSTANCE);
        types.put("RES_USER_ID",IntegerType.INSTANCE);
        types.put("STATE",StringType.INSTANCE);
        types.put("DEPARTMENT_ID",StringType.INSTANCE);
        types.put("CORP_ID",StringType.INSTANCE);
        types.put("MODIFY_TIME",TimestampType.INSTANCE);
        types.put("MODIFY_USER_ID",IntegerType.INSTANCE);
        types.put("TAX_ID",IntegerType.INSTANCE);
        types.put("resName",StringType.INSTANCE);
        Map<String, Object> map = baseDao.uniqueBySQL(sql,types,params.toArray());
        return map;
    }


    /**
     * 功能描述: 保存提问
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void saveQue(String queTitle, String question, Integer userId, String departmentId, String corpId, Integer taxId) {
        QurRes qurRes = new QurRes();
        qurRes.setQueTitle(queTitle);
        qurRes.setQuestion(question);
        qurRes.setQueUserId(userId);
        qurRes.setQueTime(new Timestamp(System.currentTimeMillis()));
        qurRes.setDepartmentId(departmentId);
        qurRes.setState("0");
        qurRes.setCorpId(corpId);
        qurRes.setTaxId(taxId);
        baseDao.save(qurRes);
    }

    /**
     * 功能描述: 保存回答
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void saveRes(Integer qrId, String response, Integer userId) {
        QurRes qurRes = baseDao.get(QurRes.class, qrId);
        qurRes.setResponse(response);
        qurRes.setResTime(new Timestamp(System.currentTimeMillis()));
        qurRes.setState("1");
        qurRes.setResUserId(userId);
        baseDao.saveOrUpdate(qurRes);
    }

    /**
     * 功能描述:问题列表
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> queList(Page page, Integer taxId) {
        // modify by zy 2018-10-18 增加查询条件
        List<Object> params = new ArrayList<>();
        PageData pd = page.getPd();
        String state = page.getPd().getString("state");
        // 关键字的搜索范围
        String scope = pd.getString("scope");
        String keywords = pd.getString("keywords");
        // 提问时间起
        String quesStartTime = pd.getString("quesStartTime");
        // 提问时间止
        String quesEndTime = pd.getString("quesEndTime");
        // 回复时间起
        String resStartTime = pd.getString("resStartTime");
        // 回复时间止
        String resEndTime = pd.getString("resEndTime");
        // 提问人
        String queUserId = pd.getString("queUserId");
        // 回复人
        String resUserId = pd.getString("resUserId");
        // 查询列表sql
        String sql = "SELECT e.*,ei.`NAME` resName from " +
                "(SELECT " +
                "qr.ID id, " +
                "qr.QUE_TITLE queTitle, " +
                "qr.QUE_USER_ID queUserId, " +
                "qr.QUESTION question, " +
                "qr.QUE_TIME queTime, " +
                "qr.RESPONSE response, " +
                "qr.RES_USER_ID resUserId, " +
                "qr.STATE state, " +
                "qr.RES_TIME resTime, " +
                "qr.TAX_ID taxId, " +
                "ei.`NAME` queName " +
                "FROM " +
                "que_res qr " +
                "INNER JOIN ent_user_info eui ON qr.QUE_USER_ID = eui.ID " +
                "INNER JOIN employee_info ei ON eui.EMPLOYEE_ID = ei.ID) as e " +
                "LEFT JOIN tax_user_info tui ON e.resUserId = tui.ID " +
                "LEFT JOIN employee_info ei ON tui.EMPLOYEE_ID = ei.ID " +
                "where e.state <> '2' ";
        if (StringUtils.isNotEmpty(state)) {
            sql += "and e.state = ? ";
            params.add(state);
        }
        // 标题和问题内容
        if (StringUtils.isNotEmpty(keywords)) {
            //title-仅搜索标题包含关键字的
            if (StringUtils.equals(Constant.SEARCH_SCOPE_TITLE, scope)) {
                sql += "and e.queTitle like ? ";
            } else if (StringUtils.equals(Constant.SEARCH_SCOPE_CONTENT, scope)) {
                // content-内容包含关键字的
                sql += "and e.question like ? ";
            } else if (StringUtils.equals(Constant.SEARCH_SCOPE_TITLE_AND_CONTENT, scope)) {
                // and-标题和内容都包含关键字
                sql += "and e.queTitle like ? and e.question like ? ";
                params.add("%" + keywords + "%");
            } else {
                // or/其他-标题或内容包含关键字
                sql += "and (e.queTitle like ? or e.question like ?) ";
                params.add("%" + keywords + "%");
            }
            params.add("%" + keywords + "%");
        }
        // 放入提问时间条件信息
        if (StringUtils.isNotEmpty(quesStartTime)) {
            sql += "and e.queTime>=? ";
            params.add(quesStartTime);
        }
        if (StringUtils.isNotEmpty(quesEndTime)) {
            sql += "and e.queTime<=? ";
            params.add(quesEndTime);
        }
        // 放入回复时间条件信息
        if (StringUtils.isNotEmpty(resStartTime)) {
            sql += "and e.resTime>=? ";
            params.add(resStartTime);
        }
        if (StringUtils.isNotEmpty(resEndTime)) {
            sql += "and e.resTime<=? ";
            params.add(resEndTime);
        }
        if (taxId != null) {
            sql += "and e.taxId=? ";
            params.add(taxId);
        }
        if (StringUtils.isNotEmpty(queUserId)) {
            sql += "and e.queUserId=? ";
            params.add(Integer.valueOf(queUserId));
        }
        if (resUserId != null) {
            sql += "and e.resUserId=? ";
            params.add(Integer.valueOf(resUserId));
        }
        String totalSql = "select count(a.id) as num from (" + sql + ") a";
        sql += "order by e.queTime desc";
        Map<String, Type> types = new HashMap<>(16);
        types.put("id", IntegerType.INSTANCE);
        types.put("queTitle", StringType.INSTANCE);
        types.put("question", StringType.INSTANCE);
        types.put("queTime", TimestampType.INSTANCE);
        types.put("response", StringType.INSTANCE);
        types.put("resUserId", IntegerType.INSTANCE);
        types.put("state", StringType.INSTANCE);
        types.put("resTime", TimestampType.INSTANCE);
        types.put("queName", StringType.INSTANCE);
        types.put("resName", StringType.INSTANCE);
        Map<String, Type> typeNum = new HashMap<>(1);
        typeNum.put("num", LongType.INSTANCE);
        Map<String, Object> countNum = baseDao.uniqueBySQL(totalSql, typeNum, params.toArray());
        int count = Integer.valueOf(String.valueOf(countNum.get("num")));
        // 记录总数
        page.setTotalResult(count);
        // 记录页数
        page.setTotalPage(page.getTotalPage());
        List<Map<String, Object>> list = baseDao.queryBySQL(sql, types, page.getCurrentPage(), page.getShowCount(), params.toArray());
        return list;
    }

    /**
     * 功能描述:作废问题
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void deleteQue(Integer userId, Integer qrId) {
        QurRes qurRes = getQueResDetail(qrId);
        qurRes.setState("2");
        qurRes.setModifyTime(new Timestamp(System.currentTimeMillis()));
        qurRes.setModifyUserId(userId);
        baseDao.saveOrUpdate(qurRes);
    }

    /**
     * 功能描述:保存并发送
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public String saveAndSend(Integer qrId, String response, Integer userId) throws Exception {
        QurRes qurRes = baseDao.get(QurRes.class, qrId);
        qurRes.setResponse(response);
        qurRes.setResTime(new Timestamp(System.currentTimeMillis()));
        qurRes.setState("1");
        qurRes.setResUserId(userId);
        baseDao.saveOrUpdate(qurRes);
        String result = "";
        if (qurRes.getId() != null) {
            String url = String.format(Constant.qrUrl, qrId);
            result = restfulSrv.sendRes(qrId, url);
        } else {
            result = "出现异常";
        }
        return result;
    }
}
