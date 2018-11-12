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
     * 第三方应用的SuiteId
     */
    public static final String PROVIDER_SUITE_ID = "";

    /**
     * 第三方应用服务商的企业微信企业id
     */
    public static final String PROVIDER_CORP_ID = "";

    /**
     * 第三方应用的secret
     */
    public static final String PROVIDER_SECRET = "";

    /**
     * 第三方应用服务商的EncodingAESKey
     */
    public static final String PROVIDER_ENCODING_AES_KEY = "";

    /**
     * suiteAccessToken有效时长（毫秒）
     */
    public static final long SUITE_ACCESS_TOKEN_TIME = 2 * 60 * 60 * 1000;

    /**
     * 提前获取accessToken时长（毫秒）
     */
    public static final long ACCESS_TOKEN_ADVANCE_TIME = 3 * 60 * 1000;

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
     * token过期时间
     */
    public static final Integer ENT_TOKEN_TIME_OUT = 60 * 60;

    /**
     * 税务分局token过期时间
     */
    public static final Integer TAX_TOKEN_TIME_OUT = 60 * 60;

    /**
     * accessToken提起过期时间
     */
    public static final Integer ENT_ACCESSTOKEN_ADVANCE_TIME = 60;

    /**
     * 是否为自建应用
     */
    public static final boolean IS_SELF_BUILT_LOGIN = true;

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
     * */
    public static final String EFFECTIVE_STATE="Y";


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

    public static final String messageUrl = "http://dev-ws.htyfw.com.cn/message/detail/%s";

    public static final String questionUrl = "http://dev-ws.htyfw.com.cn/questionnaire/quesinfo/%s";

    public static final String qrUrl = "http://dev-ws.htyfw.com.cn/ask/detail/%s";


    /**
     * 税务分局自建应用的secret
     */
    public static final String TAX_NORMAL_SECRET = ConstantUtil.getValue("SELF_BUILT_TAX_NORMAL_SECRET");


    /**
     * 除管理员外，其他角色不可添加的权限
     */
    public static final String TAX_CANNNOT_ADD_TO_ROLE_OTHIER_THAN_MANAGER = "system-userlist,system-role";

    /**
     * 所有角色必有的权限
     */
    public static final String TAX_ALL_ROLE_HAVE_RIGHT = "system-account";



}
