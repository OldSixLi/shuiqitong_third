package com.greatchn.po;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * @author Administrator
 * @date 2018-09-17 10:44
 *
 */
@Entity
@Table(name = "receipt")
public class Receipt {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "MESSAGE_ID")
    private Integer messageId;

    @NotNull
    @Column(name = "USER_ID")
    private Integer userId;

    @NotNull
    @Column(name = "TIME")
    private Timestamp time;

    @NotNull
    @Column(name = "ENTERPRISE_ID")
    private Integer enterpriseId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
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

    public Integer getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Integer enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
}
