package com.greatchn.service.web.greatchn;

import com.greatchn.bean.Page;
import com.greatchn.bean.PageData;
import com.greatchn.common.dao.BaseDao;
import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.po.AuditInfo;
import com.greatchn.po.EnterpriseInfo;
import com.greatchn.service.AuditService;
import com.greatchn.service.ent.EnterpriseService;
import com.greatchn.service.ent.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.type.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 审核企业/税务分局控制层
 *
 * @author zy 2018-10-19
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class AuditCheckSrv {

    @Resource
    BaseDao baseDao;

    @Resource
    AuditService auditService;

    @Resource
    RedisUtils redisUtils;

    @Resource
    EnterpriseService enterpriseService;

    @Resource
    LoginService loginService;


    /**
     * 已审核通过的企业列表
     *
     * @author zy 2018-10-22
     */
    public List<Map<String, Object>> getAuditEntList(Page page) {
        PageData pd = page.getPd();
        String auditState = pd.getString("state");
        String sql;
        String countSql;
        List<Object> params = new ArrayList<>();
        if (StringUtils.equals(Constant.AUDIT_SUCCESS, auditState)) {
            // 获取已审核通过的企业
            sql = "select ei.*,ai.REQUEST_TIME,ai.PASS_TIME,ai.ID as auditId,ai.AUDITOR as auditor,ti.`NAME` as taxName from enterprise_info ei join audit_info ai on ei.ID=ai.ENT_TAX_ID and AUDIT_TYPE='1' JOIN tax_info ti on ti.ID=ei.TAX_ID where ei.STATE='Y' and ai.STATE='1' GROUP BY ei.ID ORDER BY ai.PASS_TIME DESC";
            countSql = "select COUNT(DISTINCT ei.ID) as num from enterprise_info ei join audit_info ai on ei.ID=ai.ENT_TAX_ID and AUDIT_TYPE='1' JOIN tax_info ti on ti.ID=ei.TAX_ID where ei.STATE='Y' and ai.STATE='1' ";

        } else if (StringUtils.equals(Constant.AUDIT_FAIL, auditState)) {
            // 获取审核未通过的企业
            sql = "select ei.*,ai.REQUEST_TIME,ai.REASON,ai.ID as auditId,ti.`NAME` as taxName from enterprise_info ei join audit_info ai on ei.ID=ai.ENT_TAX_ID and AUDIT_TYPE='1' JOIN tax_info ti on ti.ID=ei.TAX_ID where ei.STATE='Y' and ai.STATE='2' GROUP BY ei.ID ORDER BY ai.REQUEST_TIME DESC";
            countSql = "select COUNT(DISTINCT ei.ID) as num from enterprise_info ei join audit_info ai on ei.ID=ai.ENT_TAX_ID and AUDIT_TYPE='1' JOIN tax_info ti on ti.ID=ei.TAX_ID where ei.STATE='Y' and ai.STATE='2'";

        } else {
            // 获取正在审核的企业
            sql = "select ei.*,ai.REQUEST_TIME,ai.ID as auditId,ai.STATE as auditState,ti.`NAME` as taxName from enterprise_info ei join audit_info ai on ei.ID=ai.ENT_TAX_ID and AUDIT_TYPE='1' JOIN tax_info ti on ti.ID=ei.TAX_ID where ei.STATE='Y' and (ai.STATE='0' or ai.STATE='3') GROUP BY ei.ID ORDER BY ai.REQUEST_TIME";
            countSql = "select COUNT(DISTINCT ei.ID) as num from enterprise_info ei join audit_info ai on ei.ID=ai.ENT_TAX_ID and AUDIT_TYPE='1' JOIN tax_info ti on ti.ID=ei.TAX_ID where ei.STATE='Y' and (ai.STATE='0' or ai.STATE='3') ";

        }
        Map<String, Type> types = new HashMap<>(16);
        //企业id
        types.put("ID", IntegerType.INSTANCE);
        // 企业名称
        types.put("ENT_NAME", StringType.INSTANCE);
        // 企业联系人手机号
        types.put("PHONE", StringType.INSTANCE);
        // 企业税号
        types.put("SH", StringType.INSTANCE);
        // 营业执照
        types.put("LICENSE", StringType.INSTANCE);
        // 企业微信id
        types.put("CORP_ID", StringType.INSTANCE);
        // 税务分局id
        types.put("TAX_ID", IntegerType.INSTANCE);
        // 税务分局名称
        types.put("taxName", StringType.INSTANCE);
        // 审核信息id
        types.put("auditId", IntegerType.INSTANCE);
        // 申请审核时间
        types.put("REQUEST_TIME", TimestampType.INSTANCE);
        if (StringUtils.equals(Constant.AUDIT_SUCCESS, auditState)) {
            types.put("auditor", IntegerType.INSTANCE);
            types.put("PASS_TIME", TimestampType.INSTANCE);
        } else if (StringUtils.equals(Constant.AUDIT_FAIL, auditState)) {
            types.put("REASON", StringType.INSTANCE);
        } else {
            types.put("auditState", StringType.INSTANCE);

        }
        Map<String, Type> typeNum = new HashMap<>(1);
        typeNum.put("num", LongType.INSTANCE);
        Map<String, Object> countNum = baseDao.uniqueBySQL(countSql, typeNum, params.toArray());
        int count = Integer.valueOf(String.valueOf(countNum.get("num")));
        // 记录总数
        page.setTotalResult(count);
        // 记录页数
        page.setTotalPage(page.getTotalPage());
        return baseDao.queryBySQL(sql, types, page.getCurrentPage(), page.getShowCount(), params.toArray());
    }

    /**
     * 更新企业审核信息状态
     *
     * @author zy 2018-10-22
     */
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public String updateAuditState(Integer auditId, Integer auditor, String state, String reason) throws IOException, URISyntaxException {
        String msg = null;
        AuditInfo auditInfo = auditService.getAuditInfoByAuditId(auditId, "update", "1");
        if (auditInfo != null) {
            // 审核状态
            auditInfo.setState(state);
            // 审核人
            auditInfo.setAuditor(auditor);
            if (StringUtils.isNotBlank(reason)) {
                auditInfo.setReason(reason);
            }
            if (StringUtils.equals(Constant.AUDIT_SUCCESS, state)) {
                auditInfo.setPassTime(new Timestamp(System.currentTimeMillis()));
            }
            baseDao.update(auditInfo);
            // 获取企业信息
            EnterpriseInfo enterpriseInfo=enterpriseService.findEnterpriseInfoByEntId(auditInfo.getEntTaxId());
            // 更新缓存
            if(enterpriseInfo!=null){
                String key=RedisUtils.REDIS_PREFIX_ENT+enterpriseInfo.getCorpId();
                // 更新缓存中的企业信息
                Map<String,Object> map=(Map<String,Object>)redisUtils.get(key);
                if(map!=null&&!map.isEmpty()){
                    map.put("entInfo",enterpriseInfo);
                    map.put("auditInfo",auditInfo);
                    redisUtils.set(key,map);
                }
            }
            // 保存管理员的信息
            Map<String,Object> map=loginService.saveEmployeeInfo(enterpriseInfo.getCorpId(),enterpriseInfo.getManager(),enterpriseInfo.getId(),enterpriseInfo.getManager());
            String msgInfo=(String)map.get("msg");
            if(StringUtils.isNotBlank(msgInfo)){
                msg=msgInfo;
            }
        } else {
            msg = "审核信息不存在";
        }
        return msg;
    }

    /**
     * 更新企业审核信息状态
     *
     * @author zy 2018-10-22
     */
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public String changeBack(Integer auditId) {
        String msg = null;
        AuditInfo auditInfo = auditService.getAuditInfoByAuditId(auditId, "update", "1");
        if (auditInfo != null) {
            baseDao.remove(auditInfo);
            // 获取企业信息
            EnterpriseInfo enterpriseInfo=enterpriseService.findEnterpriseInfoByEntId(auditInfo.getEntTaxId());
            // 更新缓存
            if(enterpriseInfo!=null){
                String key=RedisUtils.REDIS_PREFIX_ENT+enterpriseInfo.getCorpId();
                // 更新缓存中的企业信息
                Map<String,Object> map=(Map<String,Object>)redisUtils.get(key);
                if(map!=null&&!map.isEmpty()){
                    map.put("entInfo",enterpriseInfo);
                    map.put("auditInfo",null);
                    redisUtils.set(key,map);
                }
            }
        } else {
            msg = "审核信息不存在";
        }
        return msg;
    }
}
