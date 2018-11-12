package com.greatchn.po;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 税务分局权限信息实体类
 *
 * @author zy 2018-10-11
 */
@Entity
@Table(name = "tax_right_info")
public class TaxRightInfo implements Serializable {

    @Id
    @NotNull
    @Column(name = "NAME")
    private String name;

    @NotNull
    @Column(name = "LEVEL")
    private String level;

    @Column(name = "DESCRIPTION")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
