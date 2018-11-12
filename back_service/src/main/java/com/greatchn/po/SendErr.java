package com.greatchn.po;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Administrator
 * @date 2018-09-12 17:41
 *
 */
@Entity
@Table(name = "send_err")
public class SendErr implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "INVALIDUSER")
    private String invalIdUser;

    @Column(name = "INVALIDPARTY")
    private String invalIdParty;

    @Column(name = "INVALIDTAG")
    private String invalIdTag;

    @NotNull
    @Column(name = "ENTERPRISE_ID")
    private Integer enterpriseId;

    @NotNull
    @Column(name = "ERR_TIME")
    private Timestamp errTime;

    @Column(name = "ERR_REASON")
    private String errReason;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInvalIdUser() {
        return invalIdUser;
    }

    public void setInvalIdUser(String invalIdUser) {
        this.invalIdUser = invalIdUser;
    }

    public String getInvalIdParty() {
        return invalIdParty;
    }

    public void setInvalIdParty(String invalIdParty) {
        this.invalIdParty = invalIdParty;
    }

    public String getInvalIdTag() {
        return invalIdTag;
    }

    public void setInvalIdTag(String invalIdTag) {
        this.invalIdTag = invalIdTag;
    }

    public Integer getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Integer enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Timestamp getErrTime() {
        return errTime;
    }

    public void setErrTime(Timestamp errTime) {
        this.errTime = errTime;
    }

    public String getErrReason() {
        return errReason;
    }

    public void setErrReason(String errReason) {
        this.errReason = errReason;
    }
}
