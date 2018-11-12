package com.greatchn.po;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Administrator
 * @date 2018-09-14 09:25
 *
 */

@Entity
@Table(name = "qna_question")
public class QnaQuestion implements Serializable {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "QNA_ID")
    private Integer quaId;

    @NotNull
    @Column(name = "STEM")
    private String stem;

    @NotNull
    @Column(name = "TYPE")
    private String type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuaId() {
        return quaId;
    }

    public void setQuaId(Integer quaId) {
        this.quaId = quaId;
    }

    public String getStem() {
        return stem;
    }

    public void setStem(String stem) {
        this.stem = stem;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
