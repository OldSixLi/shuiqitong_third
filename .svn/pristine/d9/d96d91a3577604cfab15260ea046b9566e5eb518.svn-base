package com.greatchn.controller.ent;

import com.greatchn.bean.Result;
import com.greatchn.common.utils.*;
import com.greatchn.controller.BaseController;
import com.greatchn.po.AuditInfo;
import com.greatchn.po.EnterpriseInfo;
import com.greatchn.po.TaxInfo;
import com.greatchn.service.AuditService;
import com.greatchn.service.ent.EnterpriseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 审核控制层
 *
 * @author zy 2018-9-14
 */
@RestController
@RequestMapping("/audit")
public class AuditController extends BaseController {

    @Resource
    AuditService auditService;

    @Resource
    EnterpriseService enterpriseService;

    @Resource
    RedisUtils redisUtils;

    /**
     * 校验是否管理员添加应用授权成功,授权成功返回企业id
     */
    @RequestMapping("/checkAuthSuccess")
    public Result checkAuthSuccess(String corpId) {
        //查询是否已初始化企业
        if (StringUtils.isNotBlank(corpId)) {
            EnterpriseInfo enterpriseInfo = enterpriseService.findEnterpriseByCorpId(corpId, null);
            if (enterpriseInfo != null) {
                return Result.success(enterpriseInfo);
            } else {
                return Result.fail("企业未授权");
            }

        } else {
            return Result.fail("缺少必要参数");
        }
    }

    /**
     * 获取税务分局列表（审核通过状态的）
     */
    @RequestMapping("/getTaxList")
    public Result getTaxList() {
        // 获取已审核通过的税务分局列表
        return Result.success(auditService.getTaxList());
    }

    /**
     * 提交审核信息
     */
    @RequestMapping("/submissionOfAudit")
    public Result submissionOfAudit(AuditInfo auditInfo, EnterpriseInfo enterpriseInfo) {
        //审核信息
        // 企业微信的企业id
        String corpId = enterpriseInfo.getCorpId();
        // 审核类型
        String auditType = auditInfo.getAuditType();
        // 管理员企业微信userId
        String manager = enterpriseInfo.getManager();

        if (enterpriseInfo.getTaxId() != null && StringUtils.isNotEmpty(corpId) && StringUtils.isNotEmpty(auditType) && StringUtils.isNotEmpty(manager)) {
            if (StringUtils.isNotEmpty(enterpriseInfo.getSh()) && StringUtils.isNotEmpty(enterpriseInfo.getEntName()) && StringUtils.isNotEmpty(enterpriseInfo.getPhone())) {
                if (StringUtils.equals(auditType, "1")) {
                    String message = auditService.submissionOfAuditEnt(auditInfo, enterpriseInfo);
                    if (StringUtils.isNotEmpty(message)) {
                        return Result.fail(message);
                    } else {
                        return Result.success(message);
                    }
                } else {
                    return Result.fail("审核类型参数错误");
                }
            } else {
                return Result.fail("必填项不能为空");
            }
        } else {
            return Result.fail("缺少必要参数");
        }
    }

    /**
     * 提交审核信息(税务分局提交审核)
     */
    @RequestMapping("/submissionOfAuditTax")
    public Result submissionOfAuditTax(AuditInfo auditInfo, TaxInfo taxInfo) {
        //审核信息
        // 企业微信的企业id
        String corpId = taxInfo.getCorpId();
        // 审核类型
        String auditType = auditInfo.getAuditType();
        // 管理员企业微信userId
        String manager = taxInfo.getManager();

        if (StringUtils.isNotEmpty(corpId) && StringUtils.isNotEmpty(auditType) && StringUtils.isNotEmpty(manager)) {
            if (StringUtils.equals(auditType, "2")) {
                String message = auditService.submissionOfAuditTax(auditInfo, taxInfo);
                if (StringUtils.isNotEmpty(message)) {
                    return Result.fail(message);
                } else {
                    return Result.success(message);
                }
            } else {
                return Result.fail("审核类型参数错误");
            }
        } else {
            return Result.fail("缺少必要参数");
        }
    }


    /**
     * 根据corpId获取企业信息
     */
    @RequestMapping("/getEntepriseInfo")
    @SuppressWarnings("unchecked")
    public Result getEntepriseInfo(String corpId) {
        Map<String, Object> map;
        if (StringUtils.isEmpty(corpId)) {
            return Result.fail("缺少必要参数");
        } else {
            Map<String, Object> redisMap = (Map<String, Object>) redisUtils.get(RedisUtils.REDIS_PREFIX_ENT + corpId);
            EnterpriseInfo enterpriseInfo;
            if (redisMap != null && !redisMap.isEmpty()) {
                // 获取缓存中的信息
                enterpriseInfo = (EnterpriseInfo) redisMap.get("entInfo");
            } else {
                enterpriseInfo = enterpriseService.findEnterpriseByCorpId(corpId, "all");
            }
            //查询企业信息，企业审核信息
            if (enterpriseInfo != null) {
                map = new HashMap<>(3);
                map.put("entInfo", enterpriseInfo);
                // 查询该企业的审核信息
                AuditInfo auditInfo = auditService.getAuditInfoByEntTaxId(enterpriseInfo.getId(), null, "1");
                String state = null;
                if (auditInfo == null) {
                    // 未提交审核
                    state = "4";
                } else if (StringUtils.equals(auditInfo.getState(), "0") || StringUtils.equals(auditInfo.getState(), "3") || StringUtils.equals(auditInfo.getState(), "2") || StringUtils.equals(auditInfo.getState(), "1")) {
                    state = auditInfo.getState();
                    // 未通过，放入审核未通过原因
                    if (StringUtils.equals(auditInfo.getState(), "2")) {
                        map.put("reason", auditInfo.getReason());
                    }
                }
                // 更新缓存
                if (redisMap == null) {
                    redisMap = new HashMap<>(2);
                }
                redisMap.put("auditInfo", auditInfo);
                redisMap.put("entInfo", enterpriseInfo);
                // 更新缓存
                redisUtils.set(RedisUtils.REDIS_PREFIX_ENT + corpId, redisMap);
                map.put("auditState", state);
                return Result.success(map);
            } else {
                // 该企业未授权，或已被作废
                return Result.fail("该企业未授权");
            }
        }
    }

    /**
     * 获取企业审核结果
     */
    @RequestMapping("/getEntAuditResult")
    public Result getEntAuditResult(String corpId) {
        if (StringUtils.isEmpty(corpId)) {
            return Result.fail("缺少必要参数");
        } else {
            String resultFlag;
            EnterpriseInfo enterpriseInfo = enterpriseService.findEnterpriseByCorpId(corpId, null);
            if (enterpriseInfo != null) {
                // 查询该企业的审核信息
                AuditInfo auditInfo = auditService.getAuditInfoByEntTaxId(enterpriseInfo.getId(), null, "1");
                if (auditInfo == null) {
                    // 未提交审核
                    resultFlag = "4";
                } else if (StringUtils.equals(auditInfo.getState(), "0") || StringUtils.equals(auditInfo.getState(), "3") || StringUtils.equals(auditInfo.getState(), "2") || StringUtils.equals(auditInfo.getState(), "1")) {
                    // 审核已通过
                    resultFlag = auditInfo.getState();
                } else {
                    return Result.fail("审核信息数据错误");
                }
                return Result.success(resultFlag);
            } else {
                // 该企业未授权，或已被作废
                return Result.fail("该企业未授权，或已被作废");
            }
        }
    }

    /**
     * 发送邮箱手机验证码
     *
     * @author zy 2018-9-16
     */
    @RequestMapping("/sendCode")
    public Result sendCode(String phone) throws Exception {
        // 发送手机验证码
        String sendMessageUrl = ConstantUtil.getValue("SEND_SMS_URL");
        if (StringUtils.isEmpty(phone)) {
            return Result.fail("手机号不能为空");
        } else {
            // 手机发送验证码
            String code;
            if (!(Constant.START_SUPER_CODE)) {
                code = RandomNumberUtil.getRandom(4);
                Map<String, String> params = new HashMap<>(5);
                params.put("bizName", "smsBiz");
                params.put("method", "saveSendMessage");
                params.put("templateCode", "SMS_49135009");
                params.put("content", "{\"code\": \"" + code + "\",\"product\": \"安全绑定\"}");
                params.put("phoneNum", phone);
                HttpUtils.requestByPost(sendMessageUrl, params);
            } else {
                code = Constant.SUPER_CODE;
            }
            // 将验证码存入缓存五分钟过期
            redisUtils.set(RedisUtils.REDIS_PREFIX_ENT + "Code" + phone, code, 300);
            return Result.success("发送成功");
        }
    }

    /**
     * 验证验证码是否过期
     *
     * @author zy 2018-9-16
     */
    @RequestMapping("/checkCode")
    public Result checkCode(String phone, String code) {
        // 校验参数
        if (StringUtils.isEmpty(code) || (StringUtils.isEmpty(phone))) {
            return Result.fail("缺少必要参数");
        } else {
            // 从缓存中获取验证码
            String result = (String) redisUtils.get(RedisUtils.REDIS_PREFIX_ENT + "Code" + phone);
            if (StringUtils.isNotEmpty(result)) {
                if (StringUtils.equals(result, code)) {
                    return Result.success("验证通过");
                } else {
                    return Result.fail("您的验证码输入错误，请修改后重试！");
                }
            } else {
                return Result.fail("该验证码已失效，请重新获取！");
            }
        }
    }


}
