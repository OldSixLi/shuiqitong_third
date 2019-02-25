package com.greatchn.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.greatchn.bean.Page;
import com.greatchn.bean.PageData;
import com.greatchn.common.dao.BaseDao;
import com.greatchn.common.utils.Constant;
import com.greatchn.po.QNAInfo;
import com.greatchn.po.QnaAnswer;
import com.greatchn.po.QnaQueOptions;
import com.greatchn.po.QnaQuestion;
import com.greatchn.vo.QuestionVo;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.type.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Administrator
 * @date 2018-09-14 09:09
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class QuestionSrv {

    @Resource
    BaseDao baseDao;
    @Resource
    RestfulSrv restfulSrv;

    /**
     * 功能描述: 查询问卷信息
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    public QNAInfo findQNA(Integer qnaId){
        return baseDao.get(QNAInfo.class, qnaId);
    }

    /**
     * 功能描述: 问卷详情
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    public Map<String,Object> getDetail(Integer qnaId) {
        Map<String, Object> qnaMap = new HashMap<>();
        QNAInfo qna = findQNA(qnaId);
        if (qna != null) {
            String sql = "select * from qna_question where QNA_ID=?";
            List<QnaQuestion> questions = baseDao.queryBySql(sql, QnaQuestion.class, qnaId);
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            if (questions != null) {
                for (QnaQuestion question : questions) {
                    Map<String,Object> map=new HashMap<String,Object>();
                    map.put("qustion", question);
                    if(StringUtils.equals(question.getType(), "1")||StringUtils.equals(question.getType(), "2")||StringUtils.equals(question.getType(), "3")||StringUtils.equals(question.getType(), "4")){
                        // 2.1查询问题所有选项信息
                        String optionSql="select * from qna_que_options where QUES_ID=?";
                        List<QnaQueOptions> options=baseDao.queryBySql(optionSql, QnaQueOptions.class, question.getId());
                        // 2.2拼接问题及选项信息，问题Map
                        map.put("options", options);
                    }
                    list.add(map);
                }
            }
            Map<String,Object> qnaInfo=formatQnaTime(qna);
            qnaMap.put("qna", qnaInfo);
            qnaMap.put("questions", list);
        }
        return qnaMap;
    }

    /**
     * 功能描述: 整型问卷
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    public static Map<String,Object> formatQnaTime(QNAInfo qna){
        Map<String,Object> map=new HashMap<String,Object>();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime=qna.getStartTime()==null?null:sdf.format(new Date(qna.getStartTime().getTime()));
        String endTime=qna.getEndTime()==null?null:sdf.format(new Date(qna.getEndTime().getTime()));
        map.put("id", qna.getId());
        map.put("title", qna.getTitle());
        map.put("content",qna.getContent());
        map.put("createTime", sdf.format(new Date(qna.getCreateTime().getTime())));
        map.put("createUserId", qna.getCreateUserId());
        map.put("modifyUserId", qna.getModifyUserId());
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        map.put("state", qna.getState());
        map.put("roleId",qna.getRoleId());
        map.put("taxId",qna.getTaxId());
        map.put("enterpriseId",qna.getEnterpriseId());
        return map;
    }

    /**
     * 功能描述: 检查问卷是否填写过
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    public boolean queryUserIsAnswered(Integer qnaId,Integer userId){
        boolean flag=true;
        List<Object> params = new ArrayList<>();
        String sql="select count(ID) as num from qna_answer where QNA_ID=? and USER_ID=?";
        params.add(qnaId);
        params.add(userId);
        Map<String, Type> typeNum = new HashMap<>();
        typeNum.put("num", LongType.INSTANCE);
        Map<String, Object> countNum = baseDao.uniqueBySQL(sql, typeNum, params.toArray());
        int count = Integer.valueOf(String.valueOf(countNum.get("num")));
        if(count>0){
            flag=false;
        }
        return flag;
    }

    /**
     * 功能描述: 检查问卷状态
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    public Map<String,Object> queryQnaInfo(Integer qnaId){
        List<Object> params = new ArrayList<>();
        String sql="select STATE state from qna_info where ID=?";
        params.add(qnaId);
        Map<String, Type> types = new HashMap<>();
        types.put("state",StringType.INSTANCE);
        Map<String,Object> map = baseDao.uniqueBySQL(sql,types,params.toArray());
        return map;
    }

    /**
     * 功能描述: 保存问卷答案
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public boolean saveAnswer(Map<String,Object> map,Integer userId,String answer){
        boolean flag = false;
        Integer qnaId = (Integer) map.get("qnaId");
        boolean f = queryUserIsAnswered(qnaId,userId);
        if (f==true) {
            JSONArray jsonArray = JSONArray.parseArray(answer);
            if (jsonArray!=null){
                for(int i=0;i<jsonArray.size();i++){
                    JSONObject obj=jsonArray.getJSONObject(i);
                    Integer queId=obj.getInteger("queId");
                    String textAnswer=obj.getString("textAnswer");
                    String optAnswer=obj.getString("optAnswer");
                    QnaAnswer qnaAnswer = new QnaAnswer();
                    qnaAnswer.setId(get32UUID());
                    qnaAnswer.setQnaId(qnaId);
                    qnaAnswer.setQueId(queId);
                    qnaAnswer.setUserId(userId);
                    qnaAnswer.setTime(new Timestamp(System.currentTimeMillis()));
                    qnaAnswer.setChoice(optAnswer);
                    qnaAnswer.setText(textAnswer);
                    baseDao.save(qnaAnswer);
                    if(StringUtils.isNotEmpty(optAnswer)){
                        String[] optIds=optAnswer.split(",");
                        if(optIds!=null){
                            for(String optId:optIds){
                                Integer id=Integer.valueOf(optId);
                                QnaQueOptions qnaQueOptions = baseDao.get(QnaQueOptions.class,id);
                                if (qnaQueOptions.getPoll()!=null){
                                    qnaQueOptions.setPoll(qnaQueOptions.getPoll()+1);
                                } else{
                                    qnaQueOptions.setPoll(1);
                                }
                                baseDao.update(qnaQueOptions);
                            }
                        }
                    }
                }
            }
            flag=true;
        }
        return flag;
    }

    /**
     * 得到32位的uuid
     *
     * @return
     */
    public String get32UUID() {
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
        return uuid;
    }

    /**
     * 功能描述: 生成问卷
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public String saveOrUpdateQuestion(QNAInfo qna,String questions){
        String result = "问卷信息不存在";
        baseDao.saveOrUpdate(qna);
        if (qna.getId()!=null){
            String qustionSql = "select ID as id from qna_question where QNA_ID=?";
            Map<String, Type> types = new HashMap<>();
            types.put("id",IntegerType.INSTANCE);
            List<Map<String, Object>> questionList = baseDao.queryBySQL(qustionSql,types,qna.getId());
            JSONArray jsonArray = JSONArray.parseArray(questions);
            if (jsonArray != null) {
                for (int i=0;i<jsonArray.size();i++){
                    JSONObject obj=jsonArray.getJSONObject(i);
                    QuestionVo vo = new QuestionVo(obj.getString("stem"),obj.getString("type"),obj.getString("options"));
                    QnaQuestion question = new QnaQuestion();
                    if (questionList!=null&&i<questionList.size()){
                        question.setId((Integer)questionList.get(i).get("id"));
                    }
                    question.setQuaId(qna.getId());
                    question.setStem(vo.getStem());
                    question.setType(vo.getType());
                    baseDao.saveOrUpdate(question);
                    if (StringUtils.equals(question.getType(), "1") || StringUtils.equals(question.getType(), "2") || StringUtils.equals(question.getType(), "3")
                            || StringUtils.equals(question.getType(), "4")) {
                        if (StringUtils.isNotBlank(vo.getOptions())) {
                            String optionsql = "select ID as id from qna_que_options where QUES_ID=?";
                            List<Map<String,Object>> optionList=baseDao.queryBySQL(optionsql, types, question.getId());
                            List<QnaQueOptions> options = vo.getOptionArray(qna.getId(),question.getId());
                            for (int j=0;j<options.size();j++){
                                if (optionList!=null&&j<optionList.size()){
                                    options.get(j).setId((Integer)optionList.get(j).get("id"));
                                }
                                baseDao.saveOrUpdate(options.get(j));
                            }
                            if(optionList!=null&&optionList.size()>options.size()){
                                String optionIds="";
                                for(int n=options.size();n<optionList.size();n++){
                                    optionIds+=(Integer)optionList.get(n).get("id")+",";
                                }
                                optionIds=optionIds.substring(0, optionIds.lastIndexOf(","));
                                String optionDeletesql = "delete from qna_que_options where ID in("+optionIds+")";
                                baseDao.executeSQL(optionDeletesql);
                            }
                        } else {
                            result = question.getStem()+"的选项不能为空";
                        }
                    }
                }
                if (questionList!=null&&questionList.size()>jsonArray.size()){
                    String quesIds="";
                    for (int k=jsonArray.size();k<questionList.size();k++){
                        quesIds+=(Integer)questionList.get(k).get("id")+",";
                    }
                    quesIds=quesIds.substring(0, quesIds.lastIndexOf(","));
                    String qustionDeleteSql = "delete from qna_question where ID in("+quesIds+")";
                    baseDao.executeSQL(qustionDeleteSql);
                }
                result=null;
            }
        }
        return result;
    }

    /**
     * 功能描述: 开始问卷
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public String updateQnaStartAndSend(QNAInfo qna,Integer userId,Timestamp time, Integer taxId) throws Exception {
        qna.setModifyTime(time);
        qna.setModifyUserId(userId);
        if(StringUtils.equals(qna.getState(),"1")){
            qna.setState("2");
        } else {
            return "该问卷不能开启";
        }
        qna.setStartTime(time);
        baseDao.update(qna);
        Integer qnaId = qna.getId();
        String roleIds = qna.getRoleId();
        String result = "";
        String url = String.format(Constant.questionUrl,qnaId);
        if (StringUtils.isNotEmpty(qna.getEnterpriseId())){
            String corpIds = qna.getEnterpriseId();
            String[] corpId = corpIds.split(",");
            for (int i = 0;i<corpId.length;i++){
                if (StringUtils.isNotEmpty(roleIds)){
                    String[] roleId = roleIds.split(",");
                    String role = "";
                    for (int j = 0;j<roleId.length;j++){
                        role += roleId[j]+"|";
                    }
                    result += restfulSrv.sendMessageOrQuestion("【问卷调查】"+qna.getTitle(),qna.getContent(),role.substring(0,role.length()-1),url,corpId[i],taxId);
                } else {
                    result = restfulSrv.sendMessageOrQuestion("【问卷调查】"+qna.getTitle(),qna.getContent(),null,url,corpId[i],taxId);
                }
            }
        } else {
            if (StringUtils.isNotEmpty(roleIds)){
                String[] roleId = roleIds.split(",");
                String role = "";
                for (int i = 0;i<roleId.length;i++){
                    role += roleId[i]+"|";
                }
                result += restfulSrv.sendMessageOrQuestion("【问卷调查】"+qna.getTitle(),qna.getContent(),role.substring(0,role.length()-1),url,null,taxId);
            } else {
                result = restfulSrv.sendMessageOrQuestion("【问卷调查】"+qna.getTitle(),qna.getContent(),null,url,null,taxId);
            }
        }
        return result;
    }


    /**
     * 功能描述: 问卷列表
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    public List<Map<String, Object>> qnaList(Page page,Integer taxId){
        List<Object> params = new ArrayList<>();
        PageData pd = page.getPd();
        // 关键字的搜索范围
        String scope = pd.getString("scope");
        String keywords = pd.getString("keywords");
        // 创建时间起
        String startTime = pd.getString("startTime");
        // 创建时间止
        String endTime = pd.getString("endTime");
        String conditionSql = "from qna_info qi inner join tax_user_info tui on qi.CREATE_USER_ID = tui.ID inner join employee_info ei on ei.ID = tui.EMPLOYEE_ID where qi.STATE <> 4 ";
        if (taxId!=null){
            conditionSql += "and qi.TAX_ID = ? ";
            params.add(taxId);
        }
        if (StringUtils.isNotEmpty(keywords)) {
            //title-仅搜索标题包含关键字的
            if (StringUtils.equals(Constant.SEARCH_SCOPE_TITLE, scope)) {
                conditionSql += "and qi.TITLE like ? ";
            } else if (StringUtils.equals(Constant.SEARCH_SCOPE_CONTENT, scope)) {
                // content-内容包含关键字的
                conditionSql += "and qi.CONTENT like ? ";
            } else if (StringUtils.equals(Constant.SEARCH_SCOPE_TITLE_AND_CONTENT, scope)) {
                // and-标题和内容都包含关键字
                conditionSql += "and qi.TITLE like ? and qi.CONTENT like ? ";
                params.add("%" + keywords + "%");
            } else {
                // or/其他-标题或内容包含关键字
                conditionSql += "and (qi.TITLE like ? or qi.CONTENT like ?) ";
                params.add("%" + keywords + "%");
            }
            params.add("%" + keywords + "%");
        }
        // 开始时间
        if (StringUtils.isNotEmpty(startTime)) {
            conditionSql += "and qi.CREATE_TIME>=? ";
            params.add(startTime);
        }
        // 结束时间
        if (StringUtils.isNotEmpty(endTime)) {
            conditionSql += "and qi.CREATE_TIME<=? ";
            params.add(endTime);
        }
        String sqlCount = "select count(qi.ID) num " + conditionSql;
        String sql = "select qi.*,ei.NAME createName " + conditionSql + " order by qi.CREATE_TIME desc ";
        Map<String,Type> types = new HashMap<>();
        types.put("ID",IntegerType.INSTANCE);
        types.put("TITLE",StringType.INSTANCE);
        types.put("CONTENT",StringType.INSTANCE);
        types.put("CREATE_TIME",TimestampType.INSTANCE);
        types.put("CREATE_USER_ID",IntegerType.INSTANCE);
        types.put("MODIFY_TIME",TimestampType.INSTANCE);
        types.put("MODIFY_USER_ID",IntegerType.INSTANCE);
        types.put("START_TIME",TimestampType.INSTANCE);
        types.put("END_TIME",TimestampType.INSTANCE);
        types.put("STATE",StringType.INSTANCE);
        types.put("ROLE_ID",StringType.INSTANCE);
        types.put("createName",StringType.INSTANCE);
        Map<String, Type> typeNum = new HashMap<>();
        typeNum.put("num", LongType.INSTANCE);
        Map<String, Object> countNum = baseDao.uniqueBySQL(sqlCount, typeNum, params.toArray());
        int count = Integer.valueOf(String.valueOf(countNum.get("num")));
        // 记录总数
        page.setTotalResult(count);
        // 记录页数
        page.setTotalPage(page.getTotalPage());
        List<Map<String,Object>> list = baseDao.queryBySQL(sql, types, page.getCurrentPage(), page.getShowCount(), params.toArray());
        return list;
    }

    /**
     * 功能描述: 修改问卷状态
     *
     * @author songyue
     * @date 2018-09-15 14:13
     */
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public boolean updateQnaState(QNAInfo qnaInfo,Integer userId,String state){
        boolean flag = false;
        qnaInfo.setModifyTime(new Timestamp(System.currentTimeMillis()));
        qnaInfo.setModifyUserId(userId);
        qnaInfo.setState(state);
        if (StringUtils.equals("3",state)){
            qnaInfo.setEndTime(new Timestamp(System.currentTimeMillis()));
            baseDao.update(qnaInfo);
            flag=true;
        } else {
            baseDao.update(qnaInfo);
            flag=true;
        }
        return flag;
    }

    /**
     *
     * 功能描述:问卷填写列表
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    public List<Map<String, Object>> getUserAnswerList(Page page) {
        List<Object> params = new ArrayList<>();
        List<Map<String, Object>> list = null;
        PageData pd = page.getPd();
        Integer qnaId = pd.getInteger("qnaId");
        String sql = "SELECT " +
                "qi.ID id, " +
                "ui.ID userId, " +
                "ui.name, " +
                "ui.entName, " +
                "ui.roleName, " +
                "a.time " +
                "FROM " +
                "( " +
                "SELECT " +
                "qa.USER_ID AS userId, " +
                "qa.TIME AS time, " +
                "qa.QNA_ID AS qnaId " +
                "FROM " +
                "qna_answer qa " +
                "WHERE " +
                "qa.QNA_ID = ? " +
                "GROUP BY " +
                "qa.USER_ID " +
                ") a " +
                "LEFT JOIN ( " +
                "SELECT " +
                "eu.ID, " +
                "eu.name, " +
                "eu.entName, " +
                "eri.DESCRIPTION roleName " +
                "FROM " +
                "(SELECT " +
                "eui.ID, " +
                "ei.`NAME` name, " +
                "en.ENT_NAME entName, " +
                "MIN(eur.ROLE_ID) roleId " +
                "FROM " +
                "ent_user_info eui " +
                "INNER JOIN employee_info ei ON eui.EMPLOYEE_ID = ei.ID " +
                "INNER JOIN enterprise_info en ON eui.ENTERPRISE_ID = en.ID " +
                "INNER JOIN ent_user_role eur ON eui.ID = eur.USER_ID " +
                "GROUP BY eui.ID) eu " +
                "  INNER JOIN ent_role_info eri ON eri.ID = eu.roleId " +
                ") ui ON a.userId = ui.ID " +
                "LEFT JOIN qna_info qi ON a.qnaId = qi.ID";
        params.add(qnaId);
        Map<String, Type> types = new HashMap<>();
        types.put("id",IntegerType.INSTANCE);
        types.put("userId",IntegerType.INSTANCE);
        types.put("name",StringType.INSTANCE);
        types.put("entName",StringType.INSTANCE);
        types.put("roleName",StringType.INSTANCE);
        types.put("time",TimestampType.INSTANCE);
        String totalSql = "select count(b.id) as num from (" + sql + ") b";
        Map<String, Type> totalType = new HashMap<>();
        totalType.put("num", BigIntegerType.INSTANCE);
        Map<String, Object> totalMap = baseDao.uniqueBySQL(totalSql, totalType, params.toArray());
        int count = 0;
        if(totalMap!=null){
            count= Integer.valueOf(totalMap.get("num").toString());
        }
        page.setTotalResult(count);
        sql+=" order by time desc";
        list = baseDao.queryBySQL(sql, types, page.getCurrentPage(), page.getShowCount(), params.toArray());
        if (list!=null && list.size()>0){
            list = formatTime(list, new String[] { "time" });
        }
        return list;
    }

    public List<Map<String,Object>> formatTime(List<Map<String,Object>> list,String[] names){
        for(Map<String,Object> map:list){
            if (names != null) {
                for (String name : names) {
                    String time = map.get(name).toString();
                    if (StringUtils.isNotEmpty(time)) {
                        int index=time.lastIndexOf(".");
                        if(index>0){
                            time=time.substring(0, index);
                            map.put(name, time);
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     *
     * 功能描述:问卷填写详情
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    public Map<String, Object> getUserAnswerDetail(Integer qnaId,Integer userId){
        Map<String, Object> qnaMap = null;
        QNAInfo qnaInfo = baseDao.get(QNAInfo.class, qnaId);
        if (qnaInfo!=null){
            qnaMap = QuestionSrv.formatQnaTime(qnaInfo);
            String queSql = "select * from qna_question where QNA_ID=?";
            List<QnaQuestion> questions = baseDao.queryBySql(queSql,QnaQuestion.class,qnaId);
            List<Map<String, Object>> list = new ArrayList<>();
            if (questions!=null){
                for (int i=0;i <questions.size();i++){
                    Map<String, Object> map = new HashMap<>();
                    QnaQuestion question = questions.get(i);
                    map.put("question",question);
                    if (StringUtils.equals(question.getType(), "1") || StringUtils.equals(question.getType(), "2") || StringUtils.equals(question.getType(), "3")
                            || StringUtils.equals(question.getType(), "4")){
                        String optionSql = "select * from qna_que_options where QUES_ID=?";
                        List<QnaQueOptions> options = baseDao.queryBySql(optionSql, QnaQueOptions.class, question.getId());
                        map.put("options", options);
                    }
                    List<Object> params = new ArrayList<>();
                    String answerSql = "select * from qna_answer where QUE_ID=? and USER_ID=?";
                    params.add(question.getId());
                    params.add(userId);
                    List<QnaAnswer> answers = baseDao.queryBySql(answerSql, QnaAnswer.class,params.toArray());
                    String optionAnswer = null;
                    String textAnswer = null;
                    String answerId=null;
                    QnaAnswer qnaAnswer = null;
                    if (answers != null && answers.size() > 0) {
                        qnaAnswer = answers.get(0);
                        answerId= qnaAnswer.getId();
                        optionAnswer = qnaAnswer.getChoice();
                        textAnswer = qnaAnswer.getText();
                    }
                    map.put("answerId", answerId);
                    map.put("optionAnswer", optionAnswer);
                    map.put("textAnswer", textAnswer);
                    list.add(map);
                }
            }
            qnaMap.put("questions", list);
        }
        return qnaMap;
    }

    /**
     *
     * 功能描述:问卷统计列表
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    public List<Map<String,Object>> findStaticsQuestionList(Page page,Integer taxId){
        List<Object> params = new ArrayList<>();
        PageData pd = page.getPd();
        // 关键字的搜索范围
        String scope = pd.getString("scope");
        String keywords = pd.getString("keywords");
        // 创建时间起
        String startTime = pd.getString("startTime");
        // 创建时间止
        String endTime = pd.getString("endTime");
        String conditionSql = "from qna_info qi inner join tax_user_info tui on qi.CREATE_USER_ID = tui.ID inner join employee_info ei on ei.ID = tui.EMPLOYEE_ID where qi.STATE = '2' or qi.STATE = '3' ";
        if (taxId!=null){
            conditionSql += "and qi.TAX_ID = ? ";
            params.add(taxId);
        }
        if (StringUtils.isNotEmpty(keywords)) {
            //title-仅搜索标题包含关键字的
            if (StringUtils.equals(Constant.SEARCH_SCOPE_TITLE, scope)) {
                conditionSql += "and qi.TITLE like ? ";
            } else if (StringUtils.equals(Constant.SEARCH_SCOPE_CONTENT, scope)) {
                // content-内容包含关键字的
                conditionSql += "and qi.CONTENT like ? ";
            } else if (StringUtils.equals(Constant.SEARCH_SCOPE_TITLE_AND_CONTENT, scope)) {
                // and-标题和内容都包含关键字
                conditionSql += "and qi.TITLE like ? and qi.CONTENT like ? ";
                params.add("%" + keywords + "%");
            } else {
                // or/其他-标题或内容包含关键字
                conditionSql += "and (qi.TITLE like ? or qi.CONTENT like ?) ";
                params.add("%" + keywords + "%");
            }
            params.add("%" + keywords + "%");
        }
        // 开始时间
        if (StringUtils.isNotEmpty(startTime)) {
            conditionSql += "and qi.CREATE_TIME>=? ";
            params.add(startTime);
        }
        // 结束时间
        if (StringUtils.isNotEmpty(endTime)) {
            conditionSql += "and qi.CREATE_TIME<=? ";
            params.add(endTime);
        }
        String sqlCount = "select count(qi.ID) num " + conditionSql;
        String sql = "select qi.*,ei.NAME createName " + conditionSql + " order by qi.CREATE_TIME desc ";
        Map<String,Type> types = new HashMap<>();
        types.put("ID",IntegerType.INSTANCE);
        types.put("TITLE",StringType.INSTANCE);
        types.put("CONTENT",StringType.INSTANCE);
        types.put("CREATE_TIME",TimestampType.INSTANCE);
        types.put("CREATE_USER_ID",IntegerType.INSTANCE);
        types.put("MODIFY_TIME",TimestampType.INSTANCE);
        types.put("MODIFY_USER_ID",IntegerType.INSTANCE);
        types.put("START_TIME",TimestampType.INSTANCE);
        types.put("END_TIME",TimestampType.INSTANCE);
        types.put("STATE",StringType.INSTANCE);
        types.put("ROLE_ID",StringType.INSTANCE);
        types.put("createName",StringType.INSTANCE);
        Map<String, Type> typeNum = new HashMap<>();
        typeNum.put("num", LongType.INSTANCE);
        Map<String, Object> countNum = baseDao.uniqueBySQL(sqlCount, typeNum, params.toArray());
        int count = Integer.valueOf(String.valueOf(countNum.get("num")));
        // 记录总数
        page.setTotalResult(count);
        // 记录页数
        page.setTotalPage(page.getTotalPage());
        List<Map<String,Object>> list = baseDao.queryBySQL(sql, types, page.getCurrentPage(), page.getShowCount(), params.toArray());
        return list;
    }

    /**
     *
     * 功能描述:统计问题
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    public Map<String,Object> getStatisticsResult(Integer qnaId){
        Map<String, Object> qnaMap = null;
        QNAInfo qnaInfo = baseDao.get(QNAInfo.class,qnaId);
        if (qnaInfo != null && (StringUtils.equals("2", qnaInfo.getState()) || StringUtils.equals("3", qnaInfo.getState()))){
            qnaMap = formatQnaTime(qnaInfo);
            String queSql = "select * from qna_question where QNA_ID=?";
            List<QnaQuestion> questions = baseDao.queryBySql(queSql, QnaQuestion.class, qnaId);
            List<Map<String,Object>> list = new ArrayList<>();
            if (questions!=null){
                for (int i = 0;i<questions.size();i++){
                    Map<String, Object> map = new HashMap<String, Object>();
                    QnaQuestion question = questions.get(i);
                    map.put("qustion", question);
                    if (StringUtils.equals(question.getType(), "1") || StringUtils.equals(question.getType(), "2") || StringUtils.equals(question.getType(), "3")
                            || StringUtils.equals(question.getType(), "4")) {
                        String optionSql = "select * from qna_que_options where QUES_ID=?";
                        List<QnaQueOptions> options = baseDao.queryBySql(optionSql, QnaQueOptions.class, question.getId());
                        map.put("options", options);
                        if (StringUtils.equals(question.getType(), "3") || StringUtils.equals(question.getType(), "4")) {
                            String otherNumSql = "select count(a.id) as num from (SELECT qa.ID AS id," +
                                    "TEXT AS text" +
                                    " FROM qna_answer qa WHERE QUE_ID=? and TEXT IS NOT NULL and TRIM(TEXT)<>'') a";
                            Map<String, Type> otherType = new HashMap<String, Type>();
                            otherType.put("num", BigIntegerType.INSTANCE);
                            Map<String, Object> otherMap = baseDao.uniqueBySQL(otherNumSql, otherType, question.getId());
                            Long count = (long) 0;
                            if (otherMap != null) {
                                BigInteger num = (BigInteger) otherMap.get("num");
                                count = num.longValue();
                            }
                            map.put("otherNum", count);
                        }
                    }
                    list.add(map);
                }
            }
            qnaMap.put("questions", list);
            String totalSql = "select COUNT(DISTINCT USER_ID) as num from qna_answer where QNA_ID=?";
            Map<String, Type> types = new HashMap<String, Type>();
            types.put("num", LongType.INSTANCE);
            List<Map<String, Object>> total = baseDao.queryBySQL(totalSql, types, qnaId);
            Long num = (total != null && total.size() > 0) ? (Long) total.get(0).get("num") : (long) 0;
            qnaMap.put("totalNum", num);
        }
        return qnaMap;
    }

    /**
     *
     * 功能描述:单项问题回答列表
     *
     * @author songyue
     * @date 2018-09-15 13:15
     */
    public List<Map<String, Object>> getUserInputList(Page page) {
        PageData pd=page.getPd();
        Integer queId=pd.getInteger("queId");
        List<Object> params = new ArrayList<>();
        List<Map<String, Object>> list = null;
        String sql = "SELECT " +
                "qa.ID id, " +
                "qa.TIME time, " +
                "ei.`NAME` userName, " +
                "qa.TEXT text " +
                "FROM " +
                "qna_answer qa " +
                "LEFT JOIN ent_user_info eui ON qa.USER_ID = eui.ID " +
                "LEFT JOIN employee_info ei ON eui.EMPLOYEE_ID = ei.ID " +
                "where qa.QUE_ID = ? AND qa.TEXT IS NOT NULL and TRIM(TEXT)<>''";
        params.add(queId);
        Map<String,Type> types = new HashMap<>();
        types.put("id",StringType.INSTANCE);
        types.put("time",TimestampType.INSTANCE);
        types.put("userName",StringType.INSTANCE);
        types.put("text",StringType.INSTANCE);
        String totalSql = "select count(a.id) as num from (" + sql + ") a";
        Map<String, Type> totalType = new HashMap<String, Type>();
        totalType.put("num", BigIntegerType.INSTANCE);
        Map<String, Object> totalMap = baseDao.uniqueBySQL(totalSql, totalType, queId);
        long count =  0;
        if (totalMap != null) {
            BigInteger num= (BigInteger) totalMap.get("num");
            count=num.longValue();
        }
        page.setTotalResult(count);
        sql += " order by qa.TIME desc";
        list = baseDao.queryBySQL(sql, types, page.getCurrentPage(), page.getShowCount(),params.toArray());
        return list;
    }
}
