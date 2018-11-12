package com.greatchn.controller;

import com.greatchn.bean.Page;
import com.greatchn.bean.PageData;
import com.greatchn.bean.Result;
import com.greatchn.bean.SimplePage;
import com.greatchn.common.annotation.LoginRequired;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.po.TaxInfo;
import com.greatchn.service.QuestionSrv;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Auther: Administrator
 * @Date: 2018-10-25 14:05
 * @Description:
 */
@RestController
@RequestMapping("/tax/questionStatics")
@LoginRequired
public class TaxQuestionStaticsController extends BaseController {

    @Resource
    RedisUtils redisUtils;
    @Resource
    QuestionSrv questionSrv;

    /**
     *
     * 功能描述:问卷统计列表
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/qnaList")
    @SuppressWarnings("unchecked")
    public Result qnaList (Page page, @RequestHeader("token") String token){
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map != null && !map.isEmpty()) {
            Map<String, Object> mapTax = (Map<String, Object>) redisUtils.get(map.get("taxInfoKey").toString());
            String taxInfoRedisKey = "taxInfo";
            if (mapTax != null && !mapTax.isEmpty() && mapTax.get(taxInfoRedisKey) != null) {
                TaxInfo taxInfo = (TaxInfo) mapTax.get(taxInfoRedisKey);
                PageData pd = this.getPageData();
                page.setPd(pd);
                List<Map<String,Object>> list = questionSrv.findStaticsQuestionList(page,taxInfo.getId());
                SimplePage simplePage = new SimplePage(page, list);
                return Result.success(simplePage);
            } else {
                return Result.fail("-1");
            }
        } else {
            return Result.fail("-1");
        }
    }

    /**
     *
     * 功能描述:统计问题
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/statistics")
    @SuppressWarnings("unchecked")
    public Result getStatistics(Integer qnaId){
        if (qnaId!=null){
            Map<String,Object> map = questionSrv.getStatisticsResult(qnaId);
            return Result.success(map);
        } else {
            return Result.fail("缺少必要参数");
        }
    }

    /**
     *
     * 功能描述:单项问题回答列表
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/inputList")
    @SuppressWarnings("unchecked")
    public Result getInputList(Page page){
        PageData pd = this.getPageData();
        page.setShowCount(5);
        page.setPd(pd);
        Integer queId = pd.getInteger("queId");
        if (queId!=null){
            List<Map<String,Object>> list = questionSrv.getUserInputList(page);
            SimplePage simplePage = new SimplePage(page, list);
            return Result.success(simplePage);
        } else {
            return Result.fail("缺少必要参数");
        }
    }
}
