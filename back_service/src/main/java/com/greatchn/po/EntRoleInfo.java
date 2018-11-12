package com.greatchn.po;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * 企业角色实体类
 *
 * @author zy 2018-9-17
 */
@Entity
@Table(name = "ent_role_info")
public class EntRoleInfo {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
