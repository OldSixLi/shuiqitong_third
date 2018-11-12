package com.greatchn.controller;

import com.greatchn.bean.Page;
import com.greatchn.bean.PageData;
import com.greatchn.bean.Result;
import com.greatchn.bean.SimplePage;
import com.greatchn.common.annotation.LoginRequired;
import com.greatchn.common.utils.RedisUtils;
import com.greatchn.po.EntUserInfo;
import com.greatchn.po.EnterpriseInfo;
import com.greatchn.po.MessageInfo;
import com.greatchn.po.Receipt;
import com.greatchn.service.UserMessageSrv;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @date 2018-09-12 19:14
 *
 */
@RestController
@RequestMapping("/message")
@LoginRequired
public class UserMessageController extends BaseController {

    @Resource
    UserMessageSrv userMessageSrv;
    @Resource
    RedisUtils redisUtils;

    /**
     * 功能描述:我的消息列表
     *
     * @author songyue
     * @date 2018-09-17 9:54
     */
    @RequestMapping("/list")
    @SuppressWarnings("unchecked")
    public Result getList(Page page,@RequestHeader("token") String token){
        PageData pd = this.getPageData();
        page.setPd(pd);
        Map<String,Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map!=null && !map.isEmpty()){
            Map<String,Object> mapEnt = (Map<String, Object>) redisUtils.get(map.get("entInfoKey").toString());
            if(mapEnt!=null && !mapEnt.isEmpty()){
                EnterpriseInfo entInfo = (EnterpriseInfo) mapEnt.get("entInfo");
                EntUserInfo userInfo = (EntUserInfo) map.get("userInfo");
                List<Map<String, Object>> list = userMessageSrv.getList(page,userInfo.getId(),entInfo.getTaxId(),userInfo.getEnterpriseId());
                SimplePage simplePage = new SimplePage(page,list);
                return  Result.success(simplePage);
            } else {
                return Result.fail("无用户企业信息");
            }
        } else {
            return Result.fail("无用户信息");
        }
    }

    /**
     * 功能描述: 消息详情
     *
     * @author songyue
     * @date 2018-09-17 9:55
     */
    @RequestMapping("/detail")
    public Result getDetail(Integer messageId){
        if (messageId != null) {
            MessageInfo messageInfo = userMessageSrv.getMessageDetail(messageId);
            if (messageInfo!=null&&!"2".equals(messageInfo.getState())){
                return Result.success(messageInfo);
            } else {
                return Result.fail("该消息已过期");
            }
        } else {
            return Result.fail("无此消息");
        }
    }

    /**
     *
     * 功能描述:保存回执
     *
     * @author Administrator
     * @date 2018-09-17 11:25
     */
    @RequestMapping("/receipt")
    @SuppressWarnings("unchecked")
    public Result saveReceipt(Integer messageId, @RequestHeader("token") String token){
        Map<String,Object> map = (Map<String, Object>) redisUtils.get(token);
        if (map!=null && !map.isEmpty()){
            if (messageId!=null){
                EntUserInfo userInfo = (EntUserInfo) map.get("userInfo");
                boolean flag = userMessageSrv.stateReceipt(messageId,userInfo.getId(),userInfo.getEnterpriseId());
                if (flag==true){
                    return Result.success("记录成功");
                } else {
                    return Result.fail("已查看");
                }
            } else {
                return Result.fail("无此信息");
            }
        } else {
            return Result.fail("无用户信息");
        }
    }
}
