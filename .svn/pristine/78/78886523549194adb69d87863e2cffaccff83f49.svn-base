package com.greatchn.po;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Administrator
 * @date 2018-09-14 10:15
 *
 */
@Entity
@Table(name = "qna_que_options")
public class QnaQueOptions implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "QNA_ID")
    private Integer qnaId;

    @NotNull
    @Column(name = "QUES_ID")
    private Integer quesId;

    @NotNull
    @Column(name = "CONTENT")
    private String content;

    @Column(name = "POLL")
    private Integer poll;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQnaId() {
        return qnaId;
    }

    public void setQnaId(Integer qnaId) {
        this.qnaId = qnaId;
    }

    public Integer getQuesId() {
        return quesId;
    }

    public void setQuesId(Integer quesId) {
        this.quesId = quesId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPoll() {
        return poll;
    }

    public void setPoll(Integer poll) {
        this.poll = poll;
    }
}
