package com.greatchn.service;

import com.greatchn.common.dao.BaseDao;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.po.AuditInfo;
import com.greatchn.po.EnterpriseInfo;
import com.greatchn.po.TaxInfo;
import com.greatchn.service.ent.EnterpriseService;
import com.greatchn.service.tax.TaxService;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 审核业务层
 *
 * @author zy 2018-9-15
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class AuditService {

    @Resource
    BaseDao baseDao;

    @Resource
    TaxService taxService;

    @Resource
    EnterpriseService enterpriseService;

    @Resource
    RedisUtils redisUtils;

    /**
     * 企业提交审核信息
     * （使用到的信息有corpId-企业微信的企业id、phone-手机号 sh-税号 entName-企业名称 license-营业执照 manager-申请审核的人的企业微信userId）
     *
     * @param auditInfo 审核记录信息
     * @param info      企业信息对象
     * @author zy 2018-9-15
     */
    @Transactional(rollbackFor = Exception.class)
    public String submissionOfAuditEnt(AuditInfo auditInfo, EnterpriseInfo info) {
        String message = null;
        EnterpriseInfo enterpriseInfo = enterpriseService.findEnterpriseByCorpId(info.getCorpId(), "all");
        // 是否有有效的企业信息
        if (enterpriseInfo != null) {
            // 查询税务分局是否有效且存在
            TaxInfo taxInfo = taxService.getTaxInfoById(info.getTaxId());
            if (taxInfo != null && StringUtils.equals(taxInfo.getState(), "Y")) {
                // 查询税务局的审核状态
                AuditInfo taxAuditInfo = getAuditInfoByEntTaxId(info.getTaxId(), null, "2");
                if (taxAuditInfo == null) {
                    // 该税务分局未提交审核，不可操作
                    message = "该税务分局未提交审核，不可操作";
                } else if (StringUtils.equals(taxAuditInfo.getState(), "0") || StringUtils.equals(taxAuditInfo.getState(), "3")) {
                    // 该税务分局审核中，不可操作
                    message = "该税务分局审核中，不可操作";
                } else if (StringUtils.equals(taxAuditInfo.getState(), "2")) {
                    // 该税务分局审核未通过，不可操作
                    message = "该税务分局审核未通过，不可操作";
                } else if (StringUtils.equals(taxAuditInfo.getState(), "1")) {
                    message = checkAndSaveEntAuditInfo(auditInfo, info, enterpriseInfo);
                }
            } else {
                message = "该税务分局未授权，或已作废";
            }
        } else {
            // 没有有效的企业信息
            message = "企业未授权,请企业微信管理员添加应用后提交审核";
        }
        return message;
    }

    /**
     * 企业提交审核信息
     * （使用到的信息有corpId-企业微信的企业id、phone-手机号 sh-税号 entName-企业名称 license-营业执照 manager-申请审核的人的企业微信userId）
     *
     * @param auditInfo  审核记录信息
     * @param info       企业信息对象
     * @param oldEntInfo 原企业信息
     * @author zy 2018-9-15
     */
    @Transactional(rollbackFor = Exception.class)
    private String checkAndSaveEntAuditInfo(AuditInfo auditInfo, EnterpriseInfo info, EnterpriseInfo oldEntInfo) {
        String message = null;
        // 税务局审核通过,查询企业是否有审核信息
        AuditInfo oldInfo = getAuditInfoByEntTaxId(oldEntInfo.getId(), "update", "1");
        // false-修改 true-新增 null-不操作
        Boolean auditFlag = null;
        // false-修改 true-作废恢复 null-不操作
        Boolean entFlag = null;
        // 第一次提交审核
        if (oldInfo == null) {
            auditFlag = true;
            entFlag = false;
        } else if (StringUtils.equals(oldEntInfo.getState(), "N")) {
            auditFlag = false;
            entFlag = true;
        } else if (StringUtils.equals(oldInfo.getState(), "0")) {
            // 审核中，不能提交审核信息
            message = "审核中，请等待审核完成";
        } else if (StringUtils.equals(oldInfo.getState(), "1")) {
            // 审核已通过
            if (StringUtils.equals(oldEntInfo.getManager(), info.getManager())) {
                // 当前提交审核用户为企业的超级管理员
                auditFlag = false;
                entFlag = false;
            } else {
                // 不允许除超级管理员以外的人修改审核信息
                message = "您不是企业的超级管理员，无法更新企业信息";
            }
        } else if (StringUtils.equals(oldInfo.getState(), "2")) {
            // 审核信息未通过，重新提交审核
            auditFlag = false;
            entFlag = false;
        } else if (StringUtils.equals(oldInfo.getState(), "3")) {
            // 企业信息更新，重新审核中,不允许提交审核信息
            message = "重新审核中，请等待审核完成";
        }
        // TODO 保存审核信息历史记录
        // 保存审核审核信息
        saveEntAuditInfo(auditFlag, entFlag, oldInfo, auditInfo, info, oldEntInfo);
        return message;
    }

    /**
     * 企业提交审核信息
     * （使用到的信息有corpId-企业微信的企业id、phone-手机号 sh-税号 entName-企业名称 license-营业执照 manager-申请审核的人的企业微信userId）
     *
     * @param auditInfo  审核记录信息
     * @param info       企业信息对象
     * @param oldEntInfo 原企业信息
     * @author zy 2018-9-15
     */
    @Transactional(rollbackFor = Exception.class)
    private void saveEntAuditInfo(Boolean auditFlag, Boolean entFlag, AuditInfo oldInfo, AuditInfo auditInfo, EnterpriseInfo info, EnterpriseInfo oldEntInfo) {
        // 更新审核信息
        if (auditFlag != null && auditFlag) {
            //新增
            auditInfo.setEntTaxId(oldEntInfo.getId());
            auditInfo.setState("0");
            auditInfo.setRequestTime(new Timestamp(System.currentTimeMillis()));
            baseDao.save(auditInfo);
            // 更新缓存中的公司审核信息
            updateAuditInfoRedis(oldEntInfo.getCorpId(), auditInfo, null);
        } else if (auditFlag != null && !auditFlag) {
            //修改，将审核信息状态设置为3重新审核中
            oldInfo.setRequestTime(new Timestamp(System.currentTimeMillis()));
            oldInfo.setState("3");
            baseDao.update(oldInfo);
            // 更新缓存中的公司审核信息
            updateAuditInfoRedis(oldEntInfo.getCorpId(), oldInfo, null);
        }
        //更新企业信息
        if (entFlag != null) {
            // 手机号
            if (StringUtils.isNotEmpty(info.getPhone())) {
                oldEntInfo.setPhone(info.getPhone());
            }
            // 税号
            if (StringUtils.isNotEmpty(info.getEntName())) {
                oldEntInfo.setEntName(info.getEntName());
            }
            // 公司名称
            if (StringUtils.isNotEmpty(info.getSh())) {
                oldEntInfo.setSh(info.getSh());
            }
            // 营业执照
            if (StringUtils.isNotEmpty(info.getLicense())) {
                oldEntInfo.setLicense(info.getLicense());
            }
            // 审核申请人企业微信userId
            if (StringUtils.isNotEmpty(info.getManager())) {
                oldEntInfo.setManager(info.getManager());
            }
            if (entFlag) {
                oldEntInfo.setState("Y");
            }
            oldEntInfo.setTaxId(info.getTaxId());
            // 第一次提交审核信息,设置为管理员
            if (auditFlag != null && auditFlag) {
                oldEntInfo.setManager(info.getManager());
            }
            baseDao.update(oldEntInfo);
            // 更新缓存中的公司信息
            updateEnterpriseOrTaxInfoRedis(oldEntInfo.getCorpId(), oldEntInfo, null, null);
        }
    }

    /**
     * 更新缓存中企业的审核信息
     *
     * @param corpId    企业微信中企业的id
     * @param auditInfo 企业审核信息对象
     * @author zy 2018-9-25
     */
    @SuppressWarnings("unchecked")
    public void updateAuditInfoRedis(String corpId, AuditInfo auditInfo, String type) {
        String taxType = "tax";
        if (auditInfo != null) {
            String corpKey;
            if (StringUtils.equals(type, taxType)) {
                corpKey = RedisUtils.REDIS_PREFIX_TAX + corpId;
            } else {
                corpKey = RedisUtils.REDIS_PREFIX_ENT + corpId;
            }
            Map<String, Object> map = redisUtils.get(corpKey) == null ? null : (Map<String, Object>) redisUtils.get(corpKey);
            if (map == null) {
                // 缓存中不存在直接更新
                map = new HashMap<>(2);
            }
            // 放入审核信息，存入缓存
            map.put("auditInfo", auditInfo);
            redisUtils.set(corpKey, map);
        }
    }

    /**
     * 更新企业信息缓存
     *
     * @param corpId         企业微信中企业的id
     * @param enterpriseInfo 企业信息对象
     * @author zy 2018-9-25
     */
    @SuppressWarnings("unchecked")
    public void updateEnterpriseOrTaxInfoRedis(String corpId, EnterpriseInfo enterpriseInfo, TaxInfo taxInfo, String type) {
        String taxType = "tax";
        if (enterpriseInfo != null) {
            String corpKey;
            if (StringUtils.equals(type, taxType)) {
                corpKey = RedisUtils.REDIS_PREFIX_TAX + corpId;
            } else {
                corpKey = RedisUtils.REDIS_PREFIX_ENT + corpId;
            }
            Map<String, Object> map = redisUtils.get(corpKey) == null ? null : (Map<String, Object>) redisUtils.get(corpKey);
            if (map == null) {
                // 缓存中不存在直接更新
                map = new HashMap<>(2);
            }
            // 放入审核信息，存入缓存
            if (StringUtils.equals(type, taxType)) {
                map.put("taxInfo", taxInfo);
            } else {
                map.put("entInfo", enterpriseInfo);
            }
            redisUtils.set(corpKey, map);
        }

    }


    /**
     * 根据企业/税务分局id获取审核信息
     */
    public AuditInfo getAuditInfoByEntTaxId(Integer id, String type, String auditType) {
        AuditInfo auditInfo;
        if (StringUtils.equals(type, "update")) {
            // for update加锁的查询
            String auditSql = "select * from audit_info where ENT_TAX_ID=? and AUDIT_TYPE=? for update";
            List<AuditInfo> auditInfos = baseDao.queryBySql(auditSql, AuditInfo.class, id, auditType);
            auditInfo = auditInfos != null && auditInfos.size() > 0 ? auditInfos.get(0) : null;
        } else {
            // 查询是否有审核信息i
            String auditHql = "from AuditInfo where entTaxId=?0 and auditType=?1";
            AuditInfo[] auditInfos = baseDao.query(auditHql, AuditInfo.class, id, auditType);
            auditInfo = auditInfos != null && auditInfos.length > 0 ? auditInfos[0] : null;
        }
        return auditInfo;
    }

    /**
     * 根据审核信息id获取企业审核信息
     */
    public AuditInfo getAuditInfoByAuditId(Integer auditId, String type, String auditType) {
        AuditInfo auditInfo;
        if (StringUtils.equals(type, "update")) {
            // for update加锁的查询
            String auditSql = "select * from audit_info where ID=? and AUDIT_TYPE=? for update";
            List<AuditInfo> auditInfos = baseDao.queryBySql(auditSql, AuditInfo.class, auditId, auditType);
            auditInfo = auditInfos != null && auditInfos.size() > 0 ? auditInfos.get(0) : null;
        } else {
            // 查询是否有审核信息i
            String auditHql = "from AuditInfo where ID=?0 and auditType=?1";
            AuditInfo[] auditInfos = baseDao.query(auditHql, AuditInfo.class, auditId, auditType);
            auditInfo = auditInfos != null && auditInfos.length > 0 ? auditInfos[0] : null;
        }
        return auditInfo;
    }

    /**
     * 税务分局提交审核信息
     * （使用到的信息有corpId-企业微信的企业id、phone-手机号 sh-税号 entName-企业名称 license-营业执照 manager-申请审核的人的企业微信userId）
     *
     * @param auditInfo 审核记录信息
     * @param info      企业信息对象
     * @author zy 2018-10-12
     */
    @Transactional(rollbackFor = Exception.class)
    public String submissionOfAuditTax(AuditInfo auditInfo, TaxInfo info) {
        String message;
        TaxInfo taxInfo = taxService.findTaxInfoByCorpId(info.getCorpId(), "all");
        // 是否有有效的企业信息
        if (taxInfo != null) {
            message = checkAndSaveTaxAuditInfo(auditInfo, info, taxInfo);
        } else {
            // 没有有效的企业信息
            message = "未授权,请企业微信管理员添加应用后提交审核";
        }
        return message;
    }

    /**
     * 企业提交审核信息
     * （使用到的信息有corpId-企业微信的企业id、phone-手机号 sh-税号 entName-企业名称 license-营业执照 manager-申请审核的人的企业微信userId）
     *
     * @param auditInfo  审核记录信息
     * @param info       税务分局信息对象
     * @param oldTaxInfo 原税务分局信息信息
     * @author zy 2018-9-15
     */
    private String checkAndSaveTaxAuditInfo(AuditInfo auditInfo, TaxInfo info, TaxInfo oldTaxInfo) {
        String message = null;
        // 税务局审核通过,查询企业是否有审核信息
        AuditInfo oldInfo = getAuditInfoByEntTaxId(oldTaxInfo.getId(), "update", "1");
        // false-修改 true-新增 null-不操作
        Boolean auditFlag = null;
        // false-修改 true-作废恢复 null-不操作
        Boolean taxFlag = null;
        // 第一次提交审核
        if (oldInfo == null) {
            auditFlag = true;
            taxFlag = false;
        } else if (StringUtils.equals(oldTaxInfo.getState(), "N")) {
            auditFlag = false;
            taxFlag = true;
        } else if (StringUtils.equals(oldInfo.getState(), "0")) {
            // 审核中，不能提交审核信息
            message = "审核中，请等待审核完成";
        } else if (StringUtils.equals(oldInfo.getState(), "1")) {
            // 审核已通过
            if (StringUtils.equals(oldTaxInfo.getManager(), info.getManager())) {
                // 当前提交审核用户为企业的超级管理员
                auditFlag = false;
                taxFlag = false;
            } else {
                // 不允许除超级管理员以外的人修改审核信息
                message = "您不是企业的超级管理员，无法更新信息";
            }
        } else if (StringUtils.equals(oldInfo.getState(), "2")) {
            // 审核信息未通过，重新提交审核
            auditFlag = false;
            taxFlag = false;
        } else if (StringUtils.equals(oldInfo.getState(), "3")) {
            // 企业信息更新，重新审核中,不允许提交审核信息
            message = "重新审核中，请等待审核完成";
        }
        // TODO 保存审核信息历史记录
        // 保存审核审核信息
        saveTaxAuditInfo(auditFlag, taxFlag, oldInfo, auditInfo, info, oldTaxInfo);
        return message;
    }

    /**
     * 保存税务分局审核信息
     */
    private void saveTaxAuditInfo(Boolean auditFlag, Boolean taxFlag, AuditInfo oldInfo, AuditInfo auditInfo, TaxInfo info, TaxInfo oldTaxInfo) {
        // 更新审核信息
        if (auditFlag != null && auditFlag) {
            //新增
            auditInfo.setEntTaxId(oldTaxInfo.getId());
            auditInfo.setState("0");
            auditInfo.setRequestTime(new Timestamp(System.currentTimeMillis()));
            baseDao.save(auditInfo);
            // 更新缓存中的公司审核信息
            updateAuditInfoRedis(oldTaxInfo.getCorpId(), auditInfo, "tax");
        } else if (auditFlag != null && !auditFlag) {
            //修改，将审核信息状态设置为3重新审核中
            oldInfo.setRequestTime(new Timestamp(System.currentTimeMillis()));
            oldInfo.setState("3");
            baseDao.update(oldInfo);
            // 更新缓存中的公司审核信息
            updateAuditInfoRedis(oldTaxInfo.getCorpId(), auditInfo, "tax");
        }
        //更新企业信息
        if (taxFlag != null) {
            // 机关代码
            if (StringUtils.isNotEmpty(info.getCode())) {
                oldTaxInfo.setPhone(info.getCode());
            }
            // 公司名称
            if (StringUtils.isNotEmpty(info.getName())) {
                oldTaxInfo.setName(info.getName());
            }
            // 手机号
            if (StringUtils.isNotEmpty(info.getPhone())) {
                oldTaxInfo.setPhone(info.getPhone());
            }
            // 审核申请人企业微信userId
            if (StringUtils.isNotEmpty(info.getManager())) {
                oldTaxInfo.setManager(info.getManager());
            }
            if (taxFlag) {
                oldTaxInfo.setState("Y");
            }
            // 第一次提交审核信息,设置为管理员
            if (auditFlag != null && auditFlag) {
                oldTaxInfo.setManager(info.getManager());
            }
            baseDao.update(oldTaxInfo);
            // 更新缓存中的公司信息
            updateEnterpriseOrTaxInfoRedis(oldTaxInfo.getCorpId(), null, oldTaxInfo, "tax");
        }
    }

    /**
     * 获取审核通过的税务分局
     *
     * @author zy 2018-9-20
     */
    public List<Map<String, Object>> getTaxList() {
        String sql = "select ti.ID as id,ti.NAME as name from tax_info ti left join audit_info ai on ti.ID=ai.ENT_TAX_ID and ti.STATE='Y' and ai.STATE='1' and ai.AUDIT_TYPE='2' where ai.ID is not null";
        Map<String, Type> types = new HashMap<>(2);
        types.put("id", IntegerType.INSTANCE);
        types.put("name", StringType.INSTANCE);
        return baseDao.queryBySQL(sql, types);
    }


}
