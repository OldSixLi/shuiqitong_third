package com.greatchn.po;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Administrator
 * @date 2018-09-11 19:19
 *
 */
@Entity
@Table(name = "message_info")
public class MessageInfo implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "TITLE")
    private String title;

    @NotNull
    @Column(name = "TYPE")
    private String type;

    @Column(name = "CONTENT")
    private String content;

    @NotNull
    @Column(name = "CREATE_USER_ID")
    private Integer createUserId;

    @NotNull
    @Column(name = "CREATE_TIME")
    private Timestamp createTime;

    @NotNull
    @Column(name = "MSGTYPE")
    private String msgtype;

    @Column(name = "ROLE_ID")
    private String roleId;

    @NotNull
    @Column(name = "STATE")
    private String state;

    @Column(name = "MODIFY_TIME")
    private Timestamp modifyTime;

    @Column(name = "MODIFY_USER_ID")
    private Integer modifyUserId;

    @NotNull
    @Column(name = "TAX_ID")
    private Integer taxId;

    @Column(name = "ENTERPRISE_ID")
    private String enterpriseId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
}
