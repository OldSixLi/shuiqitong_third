package com.greatchn.controller.web.greatchn;

import com.greatchn.bean.Page;
import com.greatchn.bean.PageData;
import com.greatchn.bean.Result;
import com.greatchn.bean.SimplePage;
import com.greatchn.common.utils.Constant;
import com.greatchn.controller.BaseController;
import com.greatchn.po.EnterpriseInfo;
import com.greatchn.service.web.greatchn.AuditCheckSrv;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * 审核企业/税务分局控制层
 *
 * @author zy 2018-10-19
 */
@RestController
@RequestMapping("/greatchn/audit")
public class AuditCheckController extends BaseController {

    @Resource
    AuditCheckSrv auditCheckSrv;

    /**
     * 已审核通过的企业
     */
    @RequestMapping("/entList")
    public Result getAduitEnt(Page page) {
        PageData pd = this.getPageData();
        page.setPd(pd);
        List<Map<String, Object>> list = auditCheckSrv.getAuditEntList(page);
        SimplePage simplePage = new SimplePage(page, list);
        return Result.success(simplePage);
    }

    /**
     * 更新企业审核信息状态
     *
     * @author zy 2018-10-19
     */
    @RequestMapping("updateAuditState")
    public Result updateAuditState(Integer auditId, Integer auditor, String state, String reason) throws IOException, URISyntaxException {
        if (auditId != null && auditor != null && StringUtils.isNotBlank(state)) {
            if (StringUtils.equals(Constant.AUDIT_SUCCESS, state) || StringUtils.equals(Constant.AUDIT_FAIL, state)) {
                if (StringUtils.equals(Constant.AUDIT_FAIL, state) && StringUtils.isBlank(reason)) {
                    //  未通过原因不能为空
                    return Result.fail("未通过原因不能为空");
                } else {
                    String msg = auditCheckSrv.updateAuditState(auditId, auditor, state, reason);
                    if (StringUtils.isNotBlank(msg)) {
                        return Result.fail(msg);
                    } else {
                        return Result.success("");
                    }
                }
            } else {
                // 要更改的状态错误
                return Result.fail("要更改的状态错误");
            }
        } else {
            // 缺少必要参数
            return Result.fail("缺少必要参数");
        }

    }

    /**
     * 作废企业
     */
    public void CancelEntInfo() {

        String sql = "select * from enterprise_info left join audit_info where STATE='N'";

        List<EnterpriseInfo> list = null;

    }


    /**
     * 还原企业审核信息，
     * <p>
     * 将企业审核信息删除，清除
     */
    public void changeBack(Integer auditId) {
        // 删除企业审核
        auditCheckSrv.changeBack(auditId);
    }
}
