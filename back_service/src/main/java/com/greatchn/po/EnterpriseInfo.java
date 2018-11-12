package com.greatchn.po;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 企业信息实体类
 *
 * @author zy 2018-9-13
 */
@Entity
@Table(name = "enterprise_info")
public class EnterpriseInfo implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "SH")
    private String sh;

    @NotNull
    @Column(name = "ENT_NAME")
    private String entName;

    @Column(name = "LICENSE")
    private String license;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "MANAGER")
    private String manager;

    @Column(name = "CORP_ID")
    private String corpId;

    @NotNull
    @Column(name = "STATE")
    private String state;

    @Column(name = "CANCEL_USER_ID")
    private String cancelUserId;

    @Column(name = "CANCEL_TIME")
    private Timestamp cancelTime;

    @Column(name = "CANCEL_REASON")
    private String cancelReason;

    @Column(name = "AGENT_ID")
    private String agentId;

    @Column(name = "PERMANENT_CODE")
    private String permanentCode;

    @Column(name="TAX_ID")
    private Integer taxId;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSh() {
        return sh;
    }

    public void setSh(String sh) {
        this.sh = sh;
    }

    public String getEntName() {
        return entName;
    }

    public void setEntName(String entName) {
        this.entName = entName;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
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

    public String getCancelUserId() {
        return cancelUserId;
    }

    public void setCancelUserId(String cancelUserId) {
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

    public Integer getTaxId() {
        return taxId;
    }
    public void setTaxId(Integer taxId) {
        this.taxId = taxId;
    }
}
