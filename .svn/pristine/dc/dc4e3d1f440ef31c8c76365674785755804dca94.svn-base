package com.greatchn.po;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 企业用户实体类
 *
 * @author zy 2018-9-17
 */
@Entity
@Table(name="ent_user_info")
public class EntUserInfo implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "EMPLOYEE_ID")
    private Integer employeeId;

    @Column(name = "ENTERPRISE_ID")
    private Integer enterpriseId;

    @Column(name = "STATE")
    private String state;

    @Column(name = "LAST_TIME")
    private Timestamp lastTime;

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

    public Integer getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Integer enterpriseId) {
        this.enterpriseId = enterpriseId;
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
}
