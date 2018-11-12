package com.greatchn.controller;

import com.greatchn.bean.Page;
import com.greatchn.bean.PageData;
import com.greatchn.bean.Result;
import com.greatchn.bean.SimplePage;
import com.greatchn.common.annotation.LoginRequired;
import com.greatchn.common.utils.Constant;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.po.TaxInfo;
import com.greatchn.po.TaxUserInfo;
import com.greatchn.service.MyQueResSrv;
import com.greatchn.service.RestfulSrv;
import com.greatchn.service.UserMessageSrv;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @date 2018-10-15 10:02
 */
@RestController
@RequestMapping("/tax/queres")
@LoginRequired
public class TaxQueResController extends BaseController {

    @Resource
    MyQueResSrv myQueResSrv;
    @Resource
    RestfulSrv restfulSrv;
    @Resource
    RedisUtils redisUtils;

    /**
     * 功能描述: 回答问题
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/saveRes")
    @SuppressWarnings("unchecked")
    public Result saveRes(Integer qrId, String response, @RequestHeader("token") String token) {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map != null && !map.isEmpty()) {
            TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
            if (qrId != null && StringUtils.isNotEmpty(response)) {
                myQueResSrv.saveRes(qrId, response, userInfo.getId());
                return Result.success("");
            } else {
                return Result.fail("缺少必要参数");
            }
        } else {
            return Result.fail("无用户信息");
        }
    }

    /**
     * 功能描述: 发送回答提示
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/sendRes")
    public Result sendRes(Integer qrId) throws Exception {
        if (qrId != null) {
            String url = String.format(Constant.qrUrl, qrId);
            String result = restfulSrv.sendRes(qrId, url);
            return Result.success(result);
        } else {
            return Result.fail("缺少必要参数");
        }
    }

    /**
     * 功能描述:问题列表
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/queList")
    @SuppressWarnings("unchecked")
    public Result queList(Page page, @RequestHeader(name = "token") String token) throws ParseException {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map != null && !map.isEmpty()) {
            Map<String, Object> taxMap = (Map<String, Object>) redisUtils.get((String) map.get("taxInfoKey"));
            String taxInfoRedisKey = "taxInfo";
            if (taxMap != null && !taxMap.isEmpty() && taxMap.get(taxInfoRedisKey) != null) {
                TaxInfo taxInfo = (TaxInfo) taxMap.get(taxInfoRedisKey);
                PageData pd = this.getPageData();
                page.setPd(pd);
                // modify by zy 增加查询条件
                // 提问时间起
                String quesStartTime = pd.getString("quesStartTime");
                // 提问时间止
                String quesEndTime = pd.getString("quesEndTime");
                Map<String, Object> quesMap = UserMessageSrv.getTimesSimpleFormate(quesStartTime, quesEndTime);
                String quesMsg = (String) quesMap.get("msg");
                if (StringUtils.isNotEmpty(quesMsg)) {
                    return Result.fail(quesMsg);
                } else {
                    // 回复时间起
                    String resStartTime = pd.getString("resStartTime");
                    // 回复时间止
                    String resEndTime = pd.getString("resEndTime");
                    Map<String, Object> resMap = UserMessageSrv.getTimesSimpleFormate(resStartTime, resEndTime);
                    String resMsg = (String) resMap.get("msg");
                    if (StringUtils.isNotEmpty(resMsg)) {
                        return Result.fail(resMsg);
                    } else {
                        // 放入提问时间条件信息
                        quesStartTime = (String) quesMap.get("startTime");
                        if (StringUtils.isNotEmpty(quesStartTime)) {
                            pd.put("quesStartTime", quesStartTime);
                        }
                        quesEndTime = (String) quesMap.get("endTime");
                        if (StringUtils.isNotEmpty(quesEndTime)) {
                            pd.put("quesEndTime", quesEndTime);
                        }
                        // 放入回复时间条件信息
                        resStartTime = (String) resMap.get("startTime");
                        if (StringUtils.isNotEmpty(resStartTime)) {
                            pd.put("resStartTime", resStartTime);
                        }
                        resEndTime = (String) resMap.get("endTime");
                        if (StringUtils.isNotEmpty(resEndTime)) {
                            pd.put("resEndTime", resEndTime);
                        }
                        List<Map<String, Object>> list = myQueResSrv.queList(page, taxInfo.getId());
                        SimplePage simplePage = new SimplePage(page, list);
                        return Result.success(simplePage);
                    }
                }
            } else {
                // 税务分局信息丢失，需要重新登录
                return Result.fail("-1");
            }
        } else {
            return Result.fail("-1");
        }

    }

    /**
     * 功能描述:作废问题
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/deleteQue")
    @SuppressWarnings("unchecked")
    public Result deleteQue(@RequestHeader("token") String token, Integer qrId) {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map != null && !map.isEmpty()) {
            TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
            if (qrId != null) {
                myQueResSrv.deleteQue(userInfo.getId(), qrId);
                return Result.success("");
            } else {
                return Result.fail("缺少必要参数");
            }
        } else {
            return Result.fail("无用户信息");
        }
    }

    /**
     * 功能描述:保存并发送
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/saveAndSend")
    @SuppressWarnings("unchecked")
    public Result saveAndSend(Integer qrId, String response, @RequestHeader("token") String token) throws Exception {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map != null && !map.isEmpty()) {
            TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
            if (qrId != null && StringUtils.isNotEmpty(response)) {
                String result = myQueResSrv.saveAndSend(qrId, response, userInfo.getId());
                return Result.success(result);
            } else {
                return Result.fail("缺少必要参数");
            }
        } else {
            return Result.fail("无用户信息");
        }
    }
}
