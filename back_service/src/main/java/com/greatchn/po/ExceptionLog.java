package com.greatchn.po;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 异常日志实体类
 *
 * @author zy 2018-9-11
 */
@Entity
@Table(name = "exception_log")
public class ExceptionLog implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "message")
    private String message;

    @Column(name = "stack")
    private String stack;

    @Column(name = "time")
    private Timestamp time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStack() {
        return stack;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
