package com.greatchn.controller;

import com.greatchn.bean.Page;
import com.greatchn.bean.PageData;
import com.greatchn.bean.Result;
import com.greatchn.bean.SimplePage;
import com.greatchn.common.annotation.LoginRequired;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.po.QNAInfo;
import com.greatchn.po.TaxInfo;
import com.greatchn.po.TaxUserInfo;
import com.greatchn.service.QuestionSrv;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @date 2018-10-15 09:08
 */
@RestController
@RequestMapping("/tax/question")
@LoginRequired
public class TaxQuestionController extends BaseController {

    @Resource
    QuestionSrv questionSrv;
    @Resource
    RedisUtils redisUtils;

    /**
     * 功能描述: 编辑问卷
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    @RequestMapping("/saveQna")
    @SuppressWarnings("unchecked")
    public Result saveQna(QNAInfo qnaInfo, String questions, String type, @RequestHeader("token") String token) {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map != null && !map.isEmpty()) {
            Map<String, Object> mapTax = (Map<String, Object>) redisUtils.get(map.get("taxInfoKey").toString());
            TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
            String taxInfoRedisKey = "taxInfo";
            if (mapTax != null && !mapTax.isEmpty() && mapTax.get(taxInfoRedisKey) != null) {
                TaxInfo taxInfo = (TaxInfo) mapTax.get(taxInfoRedisKey);
                if (StringUtils.isNotEmpty(type) && qnaInfo != null && StringUtils.isNotBlank(qnaInfo.getTitle()) && StringUtils.isNotBlank(qnaInfo.getContent())) {
                    Timestamp time = new Timestamp(System.currentTimeMillis());
                    if (StringUtils.equals("add", type)) {
                        qnaInfo.setCreateTime(time);
                        qnaInfo.setCreateUserId(userInfo.getId());
                        qnaInfo.setState("1");
                        qnaInfo.setTaxId(taxInfo.getId());
                    } else {
                        if (qnaInfo.getId() != null) {
                            QNAInfo old = questionSrv.findQNA(qnaInfo.getId());
                            if (old != null) {
                                qnaInfo.setCreateTime(old.getCreateTime());
                                qnaInfo.setCreateUserId(old.getCreateUserId());
                                qnaInfo.setModifyTime(time);
                                qnaInfo.setModifyUserId(userInfo.getId());
                                qnaInfo.setState(old.getState());
                                qnaInfo.setTaxId(taxInfo.getId());
                            } else {
                                return Result.fail("该id的问卷信息不存在");
                            }
                        } else {
                            return Result.fail("缺少必要参数");
                        }
                    }
                    String result = questionSrv.saveOrUpdateQuestion(qnaInfo, questions);
                    if (StringUtils.isEmpty(result)) {
                        return Result.success("操作成功");
                    } else {
                        return Result.fail("操作失败,失败原因" + result);
                    }
                } else {
                    return Result.fail("缺少必要参数");
                }
            } else {
                return Result.fail("-1");
            }
        } else {
            return Result.fail("无用户信息");
        }
    }

    /**
     * 功能描述: 开始问卷并发送
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    @RequestMapping("/qnaStart")
    @SuppressWarnings("unchecked")
    public Result qnaStart(Integer qnaId, @RequestHeader("token") String token) throws Exception {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map != null && !map.isEmpty()) {
            TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
            Map<String, Object> taxMap = (Map<String, Object>) redisUtils.get((String) map.get("taxInfoKey"));
            String taxInfoRedisKey = "taxInfo";
            if (taxMap != null && !taxMap.isEmpty() && taxMap.get(taxInfoRedisKey) != null) {
                TaxInfo taxInfo = (TaxInfo) taxMap.get("taxInfo");
                if (qnaId != null) {
                    QNAInfo qna = questionSrv.findQNA(qnaId);
                    if (qna != null) {
                        if (StringUtils.equals(qna.getState(), "1")) {
                            Timestamp time = new Timestamp(System.currentTimeMillis());
                            String result = questionSrv.updateQnaStartAndSend(qna, userInfo.getId(), time, taxInfo.getId());
                            return Result.success(result);
                        } else {
                            return Result.fail("该问卷不可作废");
                        }
                    } else {
                        return Result.fail("该问卷不存在");
                    }
                } else {
                    return Result.fail("缺少必要参数");
                }
            } else {
                return Result.fail("-1");
            }
        } else {
            return Result.fail("无用户信息");
        }
    }

    /**
     * 功能描述:问卷列表
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/qnaList")
    @SuppressWarnings("unchecked")
    public Result qnaList(Page page, @RequestHeader("token") String token) {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map != null && !map.isEmpty()) {
            Map<String, Object> mapTax = (Map<String, Object>) redisUtils.get(map.get("taxInfoKey").toString());
            String taxInfoRedisKey = "taxInfo";
            if (mapTax != null && !mapTax.isEmpty() && mapTax.get(taxInfoRedisKey) != null) {
                TaxInfo taxInfo = (TaxInfo) mapTax.get(taxInfoRedisKey);
                PageData pd = this.getPageData();
                page.setPd(pd);
                List<Map<String, Object>> list = questionSrv.qnaList(page, taxInfo.getId());
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
     * 功能描述:作废问卷
     * 问卷状态为未开启才能进行作废的操作，该操作只进行了问卷的状态的更改
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/deleteQna")
    @SuppressWarnings("unchecked")
    public Result deleteQuestion(Integer qnaId, @RequestHeader("token") String token) {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map != null && !map.isEmpty()) {
            TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
            if (qnaId != null) {
                QNAInfo qnaInfo = questionSrv.findQNA(qnaId);
                if (qnaInfo != null) {
                    if (StringUtils.equals(qnaInfo.getState(), "1")) {
                        boolean flag = false;
                        flag = questionSrv.updateQnaState(qnaInfo, userInfo.getId(), "4");
                        if (flag) {
                            return Result.success("操作成功");
                        } else {
                            return Result.fail("操作失败");
                        }
                    } else {
                        return Result.fail("该问卷不可作废");
                    }
                } else {
                    return Result.fail("问卷不存在");
                }
            } else {
                return Result.fail("缺少必要参数");
            }
        } else {
            return Result.fail("无用户信息");
        }
    }

    /**
     * 功能描述:结束问卷
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/qnaEnd")
    @SuppressWarnings("unchecked")
    public Result qnaEnd(Integer qnaId, @RequestHeader("token") String token) {
        Map<String, Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map != null && !map.isEmpty()) {
            TaxUserInfo userInfo = (TaxUserInfo) map.get("userInfo");
            if (qnaId != null) {
                QNAInfo qnaInfo = questionSrv.findQNA(qnaId);
                if (qnaInfo != null) {
                    if (StringUtils.equals(qnaInfo.getState(), "2")) {
                        boolean flag = false;
                        flag = questionSrv.updateQnaState(qnaInfo, userInfo.getId(), "3");
                        if (flag) {
                            return Result.success("操作成功");
                        } else {
                            return Result.fail("操作失败");
                        }
                    } else {
                        return Result.fail("该问卷为状态非开启，不可结束调查");
                    }
                } else {
                    return Result.fail("问卷不存在");
                }
            } else {
                return Result.fail("缺少必要参数");
            }
        } else {
            return Result.fail("无用户信息");
        }
    }

    /**
     * 功能描述:问卷填写列表
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/inputList")
    public Result getInputList(Page page) {
        PageData pd = this.getPageData();
        page.setPd(pd);
        Integer qnaId = pd.getInteger("qnaId");
        if (qnaId != null) {
            List<Map<String, Object>> list = questionSrv.getUserAnswerList(page);
            SimplePage simplePage = new SimplePage(page, list);
            return Result.success(simplePage);
        } else {
            return Result.fail("缺少必要参数");
        }
    }

    /**
     * 功能描述:问卷填写详情
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    @RequestMapping("/anwerDetail")
    @SuppressWarnings("unchecked")
    public Result getAnwerDetail(Integer qnaId, Integer userId) {
        if (qnaId != null && userId != null) {
            Map<String, Object> detail = questionSrv.getUserAnswerDetail(qnaId, userId);
            if (detail != null) {
                return Result.success(detail);
            } else {
                return Result.fail("该问卷不存在");
            }
        } else {
            return Result.fail("缺少必要参数");
        }
    }

    /**
     * 功能描述: 问卷详情
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    @RequestMapping("/detail")
    public Result getDetail(Integer qnaId) {
        if (qnaId != null) {
            Map<String, Object> map = questionSrv.getDetail(qnaId);
            return Result.success(map);
        } else {
            return Result.fail("此问卷不存在");
        }
    }
}
