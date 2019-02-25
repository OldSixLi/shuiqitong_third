package com.greatchn.common.utils;

public class Constant {

    /**
     * 编码集 utf-8
     */
    public static final String SYS_CHARSET = "utf-8";

    /**
     * http模拟请求的默认超时时间，超时将抛出异常ConnectTimeoutException
     */
    public static final Integer REQUEST_TIME_OUT_DEFALUT = 5000;

    /**
     * 服务商的企业微信企业id
     */
    public static final String PROVIDER_CORP_ID = "wxa059996e5d72516b";

    /**
     * 服务商的secret
     */
    public static final String PROVIDER_SECRET = "3_Geu0XCltoo9dF4lLpiJPnqFPn8J27QF9Ihte5eQugSsyO-9KVR03vEtWp81aOZ";

    /**
     * 税企通企业版第三方应用的suite_Id
     */
    public static final String ENTERPRISE_SUITE_ID = "wx37df32191487c6e8";

    /**
     * 税企通企业版第三方应用的secret
     */
    public static final String ENTERPRISE_SUITE_SECRET = "CkwL6788NLfLU4Ngm1uicrxxXarLqtVqnMQB6egY_xo";

    /**
     * 税企通企业版第三方应用回调配置中生成签名的token
     */
    public static final String ENTERPRISE_CALLBACK_TOKEN = "TtTr41okUYRmOsPtkLkbMnmbKKRDYh";

    /**
     * 税企通企业版第三方应用回调配置的EncodingAESKey
     */
    public static final String ENTERPRISE_CALLBACK_ENCODING_AES_KEY = "R12HcvicvjQiODdLICWDyDPmKNtgsmPIFtACWWOjYzv";


    /**
     * 税企通税务局第三方应用的suite_Id
     */
    public static final String TAX_SUITE_ID = "wxd8ed9a1dc046aaf8";

    /**
     * 税企通税务局第三方应用的secret
     */
    public static final String TAX_SUITE_SECRET = "z60D6yIKpe2Zd_GsNbTHvfaoVueyclffqJX6l3GBJjM";

    /**
     * 税企通税务局第三方应用回调配置中生成签名的token
     */
    public static final String TAX_CALLBACK_TOKEN = "oVbaPuGnmL4OqSiHmeguYCnJgfQ";

    /**
     * 税企通税务局第三方应用回调配置的EncodingAESKey
     */
    public static final String TAX_CALLBACK_ENCODING_AES_KEY = "MSO2G6EKj43TrYDCJSO7u8ObPsoO0CK5EzRbXf3jiG8";


    /**
     * 获取不同类型的信息-企业
     */
    public static final String GET_INFO_TYPE_ENT = "ent";

    /**
     * 获取不同类型的信息-税务分局
     */
    public static final String GET_INFO_TYPE_TAX = "tax";
    /**
     * 是否启用超级校验码
     */
    public static final boolean START_SUPER_CODE = false;

    /**
     * 超级校验码
     */
    public static final String SUPER_CODE = "1234";

    /**
     * 企业应用管理员角色名
     */
    public static final String MANAGER_NAME = "sys";
    /**
     * 普通角色名
     */
    public static final String NORMAL_NAME = "normal";

    /**
     * 普通角色描述
     */
    public static final String NORMAL_DESCRIPTION = "普通用户";


    /**
     * 除管理员外，其他角色不可添加的权限
     */
    public static final String TAX_CANNNOT_ADD_TO_ROLE_OTHIER_THAN_MANAGER = "system-userlist,system-role";

    /**
     * 所有角色必有的权限
     */
    public static final String TAX_ALL_ROLE_HAVE_RIGHT = "system-account";

    /**
     * token过期时间
     */
    public static final Integer ENT_TOKEN_TIME_OUT = 60 * 60;

    /**
     * 税务分局token过期时间
     */
    public static final Integer TAX_TOKEN_TIME_OUT = 60 * 60;

    /**
     * accessToken提前过期时间（60秒）
     */
    public static final Integer ACCESS_TOKEN_ADVANCE_TIME = 60;

    /**
     * 是否为自建应用
     */
    public static final boolean IS_SELF_BUILT_LOGIN = false;

    /**
     * 企业微信接口错误信息的code的key
     */
    public static final String WECHAT_ERROR_CODE_KEY = "errcode";

    /**
     * 企业微信接口错误信息的code的key
     */
    public static final String WECHAT_ERROR_MSG_KEY = "errmsg";

    /**
     * 只查询在title中有关键字的
     */
    public static final String SEARCH_SCOPE_TITLE = "title";


    /**
     * 只查询在content中有关键字的
     */
    public static final String SEARCH_SCOPE_CONTENT = "content";

    /**
     * 查询在title和content都有中有关键字的
     */
    public static final String SEARCH_SCOPE_TITLE_AND_CONTENT = "and";

    /**
     * 审核通过
     */
    public static final String AUDIT_SUCCESS = "1";

    /**
     * 审核未通过
     */
    public static final String AUDIT_FAIL = "2";

    /**
     * 默认密码六个1
     */
    public static final String DEFAULT_PASSWORD = "111111";

    /**
     * 有效-Y
     */
    public static final String EFFECTIVE_STATE = "Y";


    //////////////////企业微信-自建应用//////////////////////////

    /**
     * 神州浩天企业微信企业id
     */
    public static final String corpId = ConstantUtil.getValue("SELF_BUILT_CORP_ID");

    /**
     * 企业版自建应用的普通的secret
     */
    public static final String secret = ConstantUtil.getValue("SELF_BUILT_ENT_NORMAL_SECRET");

    /**
     * 神州浩天企业微信的通讯录secret
     */
    public static final String paramentSecret = ConstantUtil.getValue("SELF_BUILT_PHONE_SECRET");

    /**
     * 企业版自建应用的自建应用agentId
     */
    public static final String agentId = ConstantUtil.getValue("SELF_BUILT_ENT_AGENT_ID");

    /**
     * 调试用的超级token，即不进行token校验
     */
    public static final String superToken = "abcd";

    /**
     * 是否开启调试（调试时缓存不过期）
     */
    public static final boolean IS_TEST = true;

    /**
     * 第三方应该接口 企业版
     */
    public static final String messageUrl = "http://dev-ws.htyfw.com.cn:18301/message/detail/%s";

    public static final String questionUrl = "http://dev-ws.htyfw.com.cn:18301/questionnaire/quesinfo/%s";

    public static final String qrUrl = "http://dev-ws.htyfw.com.cn:18301/ask/detail/%s";

    /*
    //本地应用0
    public static final String messageUrl = "http://dev-ws.htyfw.com.cn/message/detail/%s";

    public static final String questionUrl = "http://dev-ws.htyfw.com.cn/questionnaire/quesinfo/%s";

    public static final String qrUrl = "http://dev-ws.htyfw.com.cn/ask/detail/%s";*/

    /**
     * 税务分局自建应用的secret
     */
    public static final String TAX_NORMAL_SECRET = ConstantUtil.getValue("SELF_BUILT_TAX_NORMAL_SECRET");


}
