package com.greatchn.po;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 员工实体类
 *
 * @author zy 2018-9-17
 */
@Entity
@Table(name = "employee_info")
public class EmployeeInfo implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name="USER_ID")
    private String userId;

    @Column(name="NAME")
    private String name;

    @Column(name="MOBILE")
    private String mobile;

    @Column(name="GENDER")
    private Integer gender;

    @Column(name="EMAIL")
    private String email;

    @Column(name="AVATAR")
    private String avatar;

    @NotNull
    @Column(name="STATUS")
    private Integer status;

    @NotNull
    @Column(name="DEPARTMENT_ID")
    private String departmnetId;

    @NotNull
    @Column(name="ENTERPRISE_ID")
    private String enterpriseId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDepartmnetId() {
        return departmnetId;
    }

    public void setDepartmnetId(String departmnetId) {
        this.departmnetId = departmnetId;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
}
