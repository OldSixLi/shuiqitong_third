package com.greatchn.controller;

import com.greatchn.bean.Page;
import com.greatchn.bean.PageData;
import com.greatchn.bean.Result;
import com.greatchn.bean.SimplePage;
import com.greatchn.common.annotation.LoginRequired;
import com.greatchn.common.annotation.NotNeedLogin;
import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.po.EntUserInfo;
import com.greatchn.po.EnterpriseInfo;
import com.greatchn.po.QurRes;
import com.greatchn.service.MyQueResSrv;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @date 2018-09-13 11:50
 */
@RestController
@RequestMapping("/queres")
@LoginRequired
public class MyQueResController extends BaseController {

    @Resource
    MyQueResSrv myQueResSrv;
    @Resource
    RedisUtils redisUtils;

    /**
     * 功能描述: 所提问题列表
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    @RequestMapping("/list")
    @SuppressWarnings("unchecked")
    public Result getList(Page page, @RequestHeader("token") String token) {
        PageData pd = this.getPageData();
        page.setPd(pd);
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map != null && !map.isEmpty()) {
            EntUserInfo userInfo = (EntUserInfo) map.get("userInfo");
            List<Map<String, Object>> list = myQueResSrv.queResList(page, userInfo.getId());
            SimplePage simplePage = new SimplePage(page, list);
            return Result.success(simplePage);
        } else {
            return Result.fail("无用户信息");
        }
    }

    /**
     * 功能描述: 问题详情
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/detail")
    @NotNeedLogin
    public Result getDetail(Integer qurResId) {
        if (qurResId != null) {
            Map<String, Object> qurRes = myQueResSrv.findQueResDetail(qurResId);
            if (qurRes != null && !"2".equals(qurRes.get("STATE").toString())) {
                return Result.success(qurRes);
            } else {
                return Result.fail("该问题已被作废");
            }
        } else {
            return Result.fail("无所查询的问题");
        }
    }

    /**
     * 功能描述: 问题记录
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/saveQue")
    @SuppressWarnings("unchecked")
    public Result saveQue(String queTitle, String question, @RequestHeader("token") String token, String departmentId) {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map != null && !map.isEmpty()) {
            Map<String, Object> mapEnt = (Map<String, Object>) redisUtils.get(map.get("entInfoKey").toString());
            if (mapEnt != null && !mapEnt.isEmpty()) {
                EnterpriseInfo entInfo = (EnterpriseInfo) mapEnt.get("entInfo");
                EntUserInfo userInfo = (EntUserInfo) map.get("userInfo");
                if (StringUtils.isNotEmpty(queTitle) && StringUtils.isNotEmpty(question)) {
                    myQueResSrv.saveQue(queTitle, question, userInfo.getId(), departmentId, Constant.corpId, entInfo.getTaxId());
                    return Result.success("");
                } else {
                    return Result.fail("缺少必要参数");
                }
            } else {
                return Result.fail("无用户企业信息");
            }
        } else {
            return Result.fail("无用户信息");
        }
    }
}
