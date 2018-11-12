package com.greatchn.po;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 税务分局信息实体类
 *
 * @author zy 2018-9-11
 */
@Entity
@Table(name = "tax_info")
public class TaxInfo implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "CODE")
    private String code;

    @NotNull
    @Column(name = "NAME")
    private String name;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "MANAGER")
    private String manager;

    @Column(name = "CORP_ID")
    private String corpId;

    @Column(name = "STATE")
    private String state;

    @Column(name = "CANCEL_USER_ID")
    private Integer cancelUserId;

    @Column(name = "CANCEL_TIME")
    private Timestamp cancelTime;

    @Column(name = "CANCEL_REASON")
    private String cancelReason;

    @NotNull
    @Column(name = "URL")
    private String url;

    @Column(name = "AGENT_ID")
    private String agentId;

    @Column(name = "PERMANENT_CODE")
    private String permanentCode;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getCorpId() {
        return corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getCancelUserId() {
        return cancelUserId;
    }

    public void setCancelUserId(Integer cancelUserId) {
        this.cancelUserId = cancelUserId;
    }

    public Timestamp getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Timestamp cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getPermanentCode() {
        return permanentCode;
    }

    public void setPermanentCode(String permanentCode) {
        this.permanentCode = permanentCode;
    }
}
