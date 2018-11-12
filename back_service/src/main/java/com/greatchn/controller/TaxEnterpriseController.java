package com.greatchn.controller;

import com.greatchn.bean.Page;
import com.greatchn.bean.PageData;
import com.greatchn.bean.Result;
import com.greatchn.bean.SimplePage;
import com.greatchn.common.annotation.LoginRequired;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.po.TaxInfo;
import com.greatchn.service.EnterpriseSrv;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Auther: Administrator
 * @Date: 2018-10-15 15:22
 * @Description:
 */
@RestController
@RequestMapping("/tax/enterprise")
@LoginRequired
public class TaxEnterpriseController extends BaseController {

    @Resource
    RedisUtils redisUtils;
    @Resource
    EnterpriseSrv enterpriseSrv;

    /**
     *
     * 功能描述:企业列表
     *
     * @auther: songyue
     * @date: 2018-09-15 13:15
     */
    @RequestMapping("/enterpriseList")
    @SuppressWarnings("unchecked")
    public Result enterpriseList(Page page, @RequestHeader("token") String token){
        Map<String,Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map!=null && !map.isEmpty()){
            Map<String, Object> mapTax = (Map<String, Object>) redisUtils.get(map.get("taxInfoKey").toString());
            String taxInfoRedisKey = "taxInfo";
            if (mapTax != null && !mapTax.isEmpty() && mapTax.get(taxInfoRedisKey) != null) {
                TaxInfo taxInfo = (TaxInfo) mapTax.get(taxInfoRedisKey);
                PageData pd = this.getPageData();
                page.setPd(pd);
                List<Map<String,Object>> list = enterpriseSrv.getList(page,taxInfo.getId());
                SimplePage simplePage = new SimplePage(page,list);
                return Result.success(simplePage);
            } else {
                return Result.fail("无当前分局信息");
            }
        } else {
            return Result.fail("无当前分局信息");
        }
    }

    @RequestMapping("/enterpriseRole")
    @SuppressWarnings("unchecked")
    public Result enterpriseRole(Integer enterpriseId){
        if (enterpriseId!=null){
            List<Map<String,Object>> list = enterpriseSrv.getRole(enterpriseId);
            return Result.success(list);
        } else {
            return Result.fail("缺少必要参数");
        }
    }

    @RequestMapping("/queResList")
    @SuppressWarnings("unchecked")
    public Result queResList(Page page){
        PageData pd = this.getPageData();
        page.setPd(pd);
        if (pd.getInteger("enterpriseId")!=null){
            List<Map<String,Object>> list = enterpriseSrv.getQueResList(page);
            SimplePage simplePage = new SimplePage(page,list);
            return Result.success(simplePage);
        } else {
            return Result.fail("缺少必要参数");
        }
    }
}
