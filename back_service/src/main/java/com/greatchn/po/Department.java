package com.greatchn.po;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 部门实体类
 *
 * @author zy 2018-9-17
 */
@Entity
@Table(name="department")
public class Department implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "ENTERPRISE_ID")
    private String enterpriseId;

    @NotNull
    @Column(name = "DEPARTMENT_ID")
    private String departmentId;

    @NotNull
    @Column(name = "NAME")
    private String name;

    @NotNull
    @Column(name = "PARENT_ID")
    private Integer parentId;

    @NotNull
    @Column(name = "ORDER")
    private Integer order;

    @NotNull
    @Column(name = "STATE")
    private String state;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
