package com.greatchn.po;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 审核信息实体类
 *
 * @author zy 2018-9-13
 */
@Entity
@Table(name = "audit_info")
public class AuditInfo implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "AUDIT_TYPE")
    private String auditType;

    @Column(name = "ENT_TAX_ID")
    private Integer entTaxId;

    @Column(name = "AUDITOR")
    private Integer auditor;

    @Column(name = "PASS_TIME")
    private Timestamp passTime;

    @Column(name = "STATE")
    private String state;

    @Column(name = "REASON")
    private String reason;

    @Column(name = "REQUEST_TIME")
    private Timestamp requestTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuditType() {
        return auditType;
    }

    public void setAuditType(String auditType) {
        this.auditType = auditType;
    }

    public Integer getEntTaxId() {
        return entTaxId;
    }

    public void setEntTaxId(Integer entTaxId) {
        this.entTaxId = entTaxId;
    }

    public Timestamp getPassTime() {
        return passTime;
    }

    public void setPassTime(Timestamp passTime) {
        this.passTime = passTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Timestamp requestTime) {
        this.requestTime = requestTime;
    }

    public Integer getAuditor() {
        return auditor;
    }

    public void setAuditor(Integer auditor) {
        this.auditor = auditor;
    }
}
