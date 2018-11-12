package com.greatchn.po;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 税务分局用户实体类
 *
 * @author zy 2018-10-11
 */
@Entity
@Table(name = "tax_user_info")
public class TaxUserInfo implements Serializable {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "EMPLOYEE_ID")
    private Integer employeeId;

    @Column(name = "TAX_ID")
    private Integer taxId;

    @Column(name = "STATE")
    private String state;

    @Column(name = "LAST_TIME")
    private Timestamp lastTime;

    @Column(name = "LOGIN_ACCOUNT")
    private String loginAccount;

    @Column(name = "PASSWORD")
    private String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getTaxId() {
        return taxId;
    }

    public void setTaxId(Integer taxId) {
        this.taxId = taxId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Timestamp getLastTime() {
        return lastTime;
    }

    public void setLastTime(Timestamp lastTime) {
        this.lastTime = lastTime;
    }

    public String getLoginAccount() {
        return loginAccount;
    }

    public void setLoginAccount(String loginAccount) {
        this.loginAccount = loginAccount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
