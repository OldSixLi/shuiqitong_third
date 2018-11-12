package com.greatchn.vo;

import com.alibaba.fastjson.JSONArray;
import com.greatchn.po.QnaQueOptions;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @date 2018-09-20 11:42
 */
public class QuestionVo {
    private String stem;
    private String type;
    private String options;

    public QuestionVo(String stem, String type, String options) {
        this.stem = stem;
        this.type = type;
        this.options = options;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public List<QnaQueOptions> getOptionArray(Integer qnaId,Integer quesId){
        List<QnaQueOptions> list=new ArrayList<QnaQueOptions>();
        JSONArray jsonArray = JSONArray.parseArray(options);
        Object[] strs = jsonArray.toArray();
        if (strs!=null){
            for (Object obj : strs){
                String content=(String) obj;
                QnaQueOptions op=new QnaQueOptions();
                op.setContent(content);
                op.setQnaId(qnaId);
                op.setQuesId(quesId);
                op.setPoll(0);
                list.add(op);
            }
        }
        return list;
    }
}
