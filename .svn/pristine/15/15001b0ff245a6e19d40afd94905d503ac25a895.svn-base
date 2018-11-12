package com.greatchn.po;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Administrator
 * @date 2018-09-13 10:09
 *
 */
@Entity
@Table(name = "que_res")
public class QurRes implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "QUESTION")
    private String question;

    @NotNull
    @Column(name = "QUE_TITLE")
    private String queTitle;

    @NotNull
    @Column(name = "QUE_USER_ID")
    private Integer queUserId;

    @NotNull
    @Column(name = "QUE_TIME")
    private Timestamp queTime;

    @NotNull
    @Column(name = "DEPARTMENT_ID")
    private String departmentId;

    @Column(name = "RESPONSE")
    private String response;

    @Column(name = "RES_TIME")
    private Timestamp resTime;

    @Column(name = "RES_USER_ID")
    private Integer resUserId;

    @NotNull
    @Column(name = "STATE")
    private String state;

    @NotNull
    @Column(name = "CORP_ID")
    private String corpId;

    @Column(name = "MODIFY_TIME")
    private Timestamp modifyTime;

    @Column(name = "MODIFY_USER_ID")
    private Integer modifyUserId;

    @NotNull
    @Column(name = "TAX_ID")
    private Integer taxId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQueTitle() {
        return queTitle;
    }

    public void setQueTitle(String queTitle) {
        this.queTitle = queTitle;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getQueUserId() {
        return queUserId;
    }

    public void setQueUserId(Integer queUserId) {
        this.queUserId = queUserId;
    }

    public Timestamp getQueTime() {
        return queTime;
    }

    public void setQueTime(Timestamp queTime) {
        this.queTime = queTime;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Timestamp getResTime() {
        return resTime;
    }

    public void setResTime(Timestamp resTime) {
        this.resTime = resTime;
    }

    public Integer getResUserId() {
        return resUserId;
    }

    public void setResUserId(Integer resUserId) {
        this.resUserId = resUserId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public Timestamp getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Integer getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(Integer modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public Integer getTaxId() {
        return taxId;
    }

    public void setTaxId(Integer taxId) {
        this.taxId = taxId;
    }
}
