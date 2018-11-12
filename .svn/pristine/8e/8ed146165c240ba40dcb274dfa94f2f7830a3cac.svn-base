package com.greatchn.controller;

import com.greatchn.bean.Result;
import com.greatchn.common.annotation.LoginRequired;
import com.greatchn.common.annotation.NotNeedLogin;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.po.EntUserInfo;
import com.greatchn.service.QuestionSrv;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 问卷
 *
 * @author songyue
 * @date 2018-09-14 09:09
 *
 */

@RestController
@RequestMapping("/question")
@LoginRequired
public class QuestionController extends BaseController {

    @Resource
    QuestionSrv questionSrv;
    @Resource
    RedisUtils redisUtils;

    /**
     * 功能描述: 问卷详情
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    @RequestMapping("/detail")
    @NotNeedLogin
    public Result getDetail(Integer qnaId) {
        if (qnaId != null) {
            Map<String, Object> map = questionSrv.getDetail(qnaId);
            return Result.success(map);
        } else {
            return Result.fail("此问卷不存在");
        }
    }

    /**
     * 功能描述: 查询问卷状态并检查问卷是否填写过
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    @RequestMapping("/getQnaState")
    @SuppressWarnings("unchecked")
    public Result getQnaState(Integer qnaId,@RequestHeader("token") String token) {
        boolean flag = false;
        Map<String, Object> user = (Map<String, Object>) redisUtils.get(token);
        if (user != null && !user.isEmpty()) {
            EntUserInfo userInfo = (EntUserInfo) user.get("userInfo");
            if (qnaId != null) {
                // 查询问卷状态-state
                Map<String, Object> map = questionSrv.queryQnaInfo(qnaId);
                if (map != null) {
                    String state = map.get("state").toString();
                    if (StringUtils.equals(state,"2")){
                        flag = questionSrv.queryUserIsAnswered(qnaId, userInfo.getId());
                        if (flag==true){
                            return Result.success("");
                        }else {
                            return Result.fail("您已回答过此问卷，不能重复回答。");
                        }
                    } else {
                        return Result.fail("该问卷调查已结束。");
                    }
                } else {
                    return Result.fail("该问卷不存在");
                }
            } else {
                return Result.fail("缺少必要参数");
            }
        } else {
            return Result.fail("无用户信息");
        }
    }

    /**
     * 功能描述: 保存问卷答案
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    @RequestMapping("/saveAnswer")
    @SuppressWarnings("unchecked")
    public Result saveAnswer(Integer qnaId, String answer, @RequestHeader("token") String token) {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map != null && !map.isEmpty()) {
            EntUserInfo userInfo = (EntUserInfo) map.get("userInfo");
            Map<String, Object> queryQnaInfo = questionSrv.queryQnaInfo(qnaId);
            if (queryQnaInfo != null) {
                if (StringUtils.equals(queryQnaInfo.get("state").toString(), "2")) {
                    queryQnaInfo.put("qnaId", qnaId);
                    boolean result = questionSrv.saveAnswer(queryQnaInfo, userInfo.getId(), answer);
                    if (result) {
                        return Result.success("操作成功");
                    } else {
                        return Result.fail("操作失败");
                    }
                } else {
                    return Result.fail("该问卷不可填写或调查已结束");
                }
            } else {
                return Result.fail("问卷不存在");
            }
        } else {
            return Result.fail("无用户信息");
        }
    }
}
