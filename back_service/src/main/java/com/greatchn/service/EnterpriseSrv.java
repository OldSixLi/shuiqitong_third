package com.greatchn.service;

import com.greatchn.bean.Page;
import com.greatchn.bean.PageData;
import com.greatchn.common.dao.BaseDao;
import com.greatchn.po.TaxUserRole;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.type.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: Administrator
 * @Date: 2018-10-15 15:36
 * @Description:
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class EnterpriseSrv {

    @Resource
    BaseDao baseDao;

    public List<Map<String,Object>> getList(Page page,Integer enterpriseId){
        List<Object> params = new ArrayList<>();
        String sql = "SELECT " +
                "ei.ID enterpriseId, " +
                "ei.SH sh, " +
                "ei.ENT_NAME entName, " +
                "ei.PHONE phone, " +
                "ei.MANAGER manager, " +
                "ai.REQUEST_TIME requestTime, " +
                "ai.PASS_TIME passTime, " +
                "ei.CORP_ID corpId," +
                "e.`NAME` managerName " +
                "FROM " +
                "audit_info ai " +
                "INNER JOIN enterprise_info ei ON ei.ID = ai.ENT_TAX_ID " +
                "INNER JOIN employee_info e ON ei.MANAGER = e.USER_ID AND ei.CORP_ID = e.ENTERPRISE_ID " +
                "where 1=1 ";
        if(enterpriseId!=null){
            sql += "and ei.TAX_ID = ? ";
            params.add(enterpriseId);
        }
        sql += "and ai.AUDIT_TYPE = '1' and ai.STATE = '1' order by passTime desc ";
        String sqlCount = "select count(s.enterpriseId) num from ( " + sql + ") s ";
        Map<String, Type> types = new HashMap<>();
        types.put("enterpriseId", IntegerType.INSTANCE);
        types.put("sh", StringType.INSTANCE);
        types.put("entName", StringType.INSTANCE);
        types.put("phone", StringType.INSTANCE);
        types.put("manager", StringType.INSTANCE);
        types.put("requestTime", TimestampType.INSTANCE);
        types.put("passTime", TimestampType.INSTANCE);
        types.put("corpId", StringType.INSTANCE);
        types.put("managerName", StringType.INSTANCE);
        Map<String, Type> typeNum = new HashMap<>();
        typeNum.put("num", LongType.INSTANCE);
        Map<String, Object> countNum = baseDao.uniqueBySQL(sqlCount, typeNum, params.toArray());
        int count = Integer.valueOf(String.valueOf(countNum.get("num")));
        // 记录总数
        page.setTotalResult(count);
        // 记录页数
        page.setTotalPage(page.getTotalPage());
        List<Map<String,Object>> list = baseDao.queryBySQL(sql, types, page.getCurrentPage(), page.getShowCount(), params.toArray());
        return list;
    }

    public List<Map<String,Object>> getRole(Integer enterpriseId){
        List<Object> params = new ArrayList<>();
        String sql = "SELECT " +
                "eri.DESCRIPTION roleName, " +
                "ei.`NAME` name," +
                "ei.MOBILE mobile, " +
                "ei.GENDER gender, " +
                "ei.EMAIL email," +
                "ei.AVATAR avatar " +
                "FROM " +
                "ent_role_info eri " +
                "LEFT JOIN ent_user_role eur ON eur.ROLE_ID = eri.ID " +
                "LEFT JOIN ent_user_info eui ON eui.ID = eur.USER_ID " +
                "LEFT JOIN employee_info ei ON ei.ID = eui.EMPLOYEE_ID " +
                "WHERE " +
                "( " +
                "eui.ENTERPRISE_ID = ? " +
                "OR eui.ENTERPRISE_ID IS NULL " +
                ") " +
                "AND eri.ID <> 6";
        params.add(enterpriseId);
        Map<String, Type> types = new HashMap<>();
        types.put("roleName", StringType.INSTANCE);
        types.put("name", StringType.INSTANCE);
        types.put("mobile", StringType.INSTANCE);
        types.put("gender",IntegerType.INSTANCE);
        types.put("email",StringType.INSTANCE);
        types.put("avatar",StringType.INSTANCE);
        List<Map<String,Object>> list = baseDao.queryBySQL(sql,types,params.toArray());
        return list;
    }

    public List<Map<String,Object>> getQueResList(Page page){
        PageData pd = page.getPd();
        List<Object> params = new ArrayList<>();
        String sql = "SELECT " +
                "qr.ID qrId, " +
                "ei.`NAME` name, " +
                "qr.QUE_TITLE title, " +
                "qr.QUESTION question, " +
                "qr.QUE_TIME queTime, " +
                "qr.STATE state " +
                "FROM " +
                "que_res qr " +
                "LEFT JOIN ent_user_info eui ON qr.QUE_USER_ID = eui.ID " +
                "LEFT JOIN employee_info ei ON eui.EMPLOYEE_ID = ei.ID " +
                "WHERE 1 = 1 ";
        if (pd.getInteger("enterpriseId")!=null){
            sql += "AND eui.ENTERPRISE_ID = ? ";
            params.add(pd.getInteger("enterpriseId"));
        }
        sql += "AND qr.STATE <> 2 order by qr.QUE_TIME desc ";
        String sqlCount = "select count(s.qrId) num from ( " + sql + ") s ";
        Map<String, Type> types = new HashMap<>();
        types.put("qrId",IntegerType.INSTANCE);
        types.put("name",StringType.INSTANCE);
        types.put("title",StringType.INSTANCE);
        types.put("question",StringType.INSTANCE);
        types.put("queTime",TimestampType.INSTANCE);
        types.put("state",StringType.INSTANCE);
        Map<String, Type> typeNum = new HashMap<>();
        typeNum.put("num", LongType.INSTANCE);
        Map<String, Object> countNum = baseDao.uniqueBySQL(sqlCount, typeNum, params.toArray());
        int count = Integer.valueOf(String.valueOf(countNum.get("num")));
        // 记录总数
        page.setTotalResult(count);
        // 记录页数
        page.setTotalPage(page.getTotalPage());
        List<Map<String,Object>> list = baseDao.queryBySQL(sql, types, page.getCurrentPage(), page.getShowCount(), params.toArray());
        return list;
    }

    public List<Map<String,Object>> getReceiptList(Page page,Integer enterpriseId,String enterprise){
        List<Object> params = new ArrayList<>();
        String sql = "SELECT " +
                "ei.ID enterpriseId, " +
                "ei.SH sh, " +
                "ei.ENT_NAME entName, " +
                "ei.PHONE phone, " +
                "ei.MANAGER manager, " +
                "ai.REQUEST_TIME requestTime, " +
                "ai.PASS_TIME passTime, " +
                "ei.CORP_ID corpId " +
                "FROM " +
                "audit_info ai " +
                "INNER JOIN enterprise_info ei ON ei.ID = ai.ENT_TAX_ID " +
                "LEFT JOIN receipt r ON ei.ID = r.ENTERPRISE_ID " +
                "WHERE 1 = 1 ";
        if(enterpriseId!=null){
            sql += "and ei.TAX_ID = ? ";
            params.add(enterpriseId);
        }
        if (StringUtils.isNotEmpty(enterprise)){
            sql += "AND ei.ID REGEXP ? ";
            params.add(enterprise);
        }
        sql += "AND ai.AUDIT_TYPE = '1' " +
                "AND ai.STATE = '1' " +
                "AND r.MESSAGE_ID IS NOT NULL " +
                "GROUP BY ei.ID " +
                "ORDER BY ai.PASS_TIME DESC ";
        String sqlCount = "select count(s.enterpriseId) num from ( " + sql + ") s ";
        Map<String, Type> types = new HashMap<>();
        types.put("enterpriseId", IntegerType.INSTANCE);
        types.put("sh", StringType.INSTANCE);
        types.put("entName", StringType.INSTANCE);
        types.put("phone", StringType.INSTANCE);
        types.put("manager", StringType.INSTANCE);
        types.put("requestTime", TimestampType.INSTANCE);
        types.put("passTime", TimestampType.INSTANCE);
        types.put("corpId", StringType.INSTANCE);
        Map<String, Type> typeNum = new HashMap<>();
        typeNum.put("num", LongType.INSTANCE);
        Map<String, Object> countNum = baseDao.uniqueBySQL(sqlCount, typeNum, params.toArray());
        int count = Integer.valueOf(String.valueOf(countNum.get("num")));
        // 记录总数
        page.setTotalResult(count);
        // 记录页数
        page.setTotalPage(page.getTotalPage());
        List<Map<String,Object>> list = baseDao.queryBySQL(sql, types, page.getCurrentPage(), page.getShowCount(), params.toArray());
        return list;
    }

    public List<TaxUserRole> findUserRole(Integer userId){
        List<Object> params = new ArrayList<>();
        String sql = "select * from tax_user_role where USER_ID = ?";
        params.add(userId);
        List<TaxUserRole> list = baseDao.queryBySql(sql,TaxUserRole.class,params.toArray());
        return list;
    }

    public List<Map<String,Object>> findUserLabel(String roleId,Integer userId,Integer enterpriseId){
        List<Object> params = new ArrayList<>();
        String sql = "SELECT " +
                "tul.LABEL_ID labelId, "+
                "li.`NAME` labelName, " +
                "li.ENTERPRISE_ID enterpriseId "+
                "FROM " +
                "tax_user_label tul " +
                "INNER JOIN label_info li ON tul.LABEL_ID = li.ID " +
                "WHERE " +
                "tul.TAX_ID = ? ";
        params.add(enterpriseId);
        if(userId!=null){
            sql += "and tul.USER_ID regexp ? ";
            params.add(userId);
        }
        if(StringUtils.isNotEmpty(roleId)){
            sql += "and tul.ROLE_ID regexp ?";
            params.add(roleId);
        }
        if (userId!=null&&StringUtils.isNotEmpty(roleId)){
            sql += "and tul.USER_ID regexp ? or tul.ROLE_ID regexp ? ";
            params.add(userId);
            params.add(roleId);
        }
        sql += "GROUP BY tul.LABEL_ID ";
        Map<String,Type> types = new HashMap<>();
        types.put("labelId",IntegerType.INSTANCE);
        types.put("labelName",StringType.INSTANCE);
        types.put("enterpriseId",StringType.INSTANCE);
        List<Map<String,Object>> list = baseDao.queryBySQL(sql,types,params.toArray());
        return list;
    }

    public List<Map<String,Object>> findUserLabelEnterprise(Page page,String roleId,Integer userId,Integer taxId){
        List<Map<String,Object>> userLabels = findUserLabel(roleId,userId,taxId);
        for (Map<String,Object> userLabel : userLabels){
            String enterpriseIds = userLabel.get("enterpriseId").toString();
            String[] enterpriseId = enterpriseIds.split(",");
            String enterprise = "";
            for (int i=0;i<enterpriseId.length;i++){
                enterprise += enterpriseId[i] + "|";
            }
            List<Object> params = new ArrayList<>();
            String sql = "SELECT " +
                    "ei.ID enterpriseId, " +
                    "ei.SH sh, " +
                    "ei.ENT_NAME entName, " +
                    "ei.PHONE phone, " +
                    "ei.MANAGER manager, " +
                    "ai.REQUEST_TIME requestTime, " +
                    "ai.PASS_TIME passTime, " +
                    "ei.CORP_ID corpId " +
                    "FROM " +
                    "audit_info ai " +
                    "INNER JOIN enterprise_info ei ON ei.ID = ai.ENT_TAX_ID " +
                    "WHERE 1 = 1 ";
            if (taxId!=null){
                sql += "AND ei.TAX_ID = ? ";
                params.add(taxId);
            }
            if (StringUtils.isNotEmpty(enterprise)){
                sql += "AND ai.ID regexp ? ";
                params.add(enterprise.substring(0,enterprise.length()-1));
            }
            sql += "AND ai.AUDIT_TYPE = '1' " +
                    "AND ai.STATE = '1' " +
                    "ORDER BY passTime DESC";
            String sqlCount = "select count(s.enterpriseId) num from ( " + sql + ") s ";
            Map<String, Type> types = new HashMap<>();
            types.put("enterpriseId", IntegerType.INSTANCE);
            types.put("sh", StringType.INSTANCE);
            types.put("entName", StringType.INSTANCE);
            types.put("phone", StringType.INSTANCE);
            types.put("manager", StringType.INSTANCE);
            types.put("requestTime", TimestampType.INSTANCE);
            types.put("passTime", TimestampType.INSTANCE);
            types.put("corpId", StringType.INSTANCE);
            Map<String, Type> typeNum = new HashMap<>();
            typeNum.put("num", LongType.INSTANCE);
            Map<String, Object> countNum = baseDao.uniqueBySQL(sqlCount, typeNum, params.toArray());
            int count = Integer.valueOf(String.valueOf(countNum.get("num")));
            // 记录总数
            page.setTotalResult(count);
            // 记录页数
            page.setTotalPage(page.getTotalPage());
            List<Map<String,Object>> list = baseDao.queryBySQL(sql, types, page.getCurrentPage(), page.getShowCount(), params.toArray());
            userLabel.put("enterpriseList",list);
        }
        return userLabels;
    }
}
