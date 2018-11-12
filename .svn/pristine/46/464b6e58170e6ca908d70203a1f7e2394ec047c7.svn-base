package com.greatchn.po;

import org.hibernate.type.TimestampType;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @author Administrator
 * @date 2018-09-14 15:41
 *
 */

@Entity
@Table(name = "qna_answer")
public class QnaAnswer {

    @Id
    @Column(name = "ID")
    private String id;

    @NotNull
    @Column(name = "QNA_ID")
    private Integer qnaId;

    @NotNull
    @Column(name = "QUE_ID")
    private Integer queId;

    @NotNull
    @Column(name = "USER_ID")
    private Integer userId;

    @NotNull
    @Column(name = "TIME")
    private Timestamp time;

    @Column(name = "CHOICE")
    private String choice;

    @Column(name = "TEXT")
    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getQnaId() {
        return qnaId;
    }

    public void setQnaId(Integer qnaId) {
        this.qnaId = qnaId;
    }

    public Integer getQueId() {
        return queId;
    }

    public void setQueId(Integer queId) {
        this.queId = queId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getChoice() {
        return choice;
    }

    public void setChoice(String choice) {
        this.choice = choice;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
