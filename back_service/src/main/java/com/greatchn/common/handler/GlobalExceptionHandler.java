package com.greatchn.common.handler;

import com.greatchn.bean.Result;
import com.greatchn.common.exception.GlobalExecption;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常处理
 *
 * @author zy 2018-9-12
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalExecption.class)
    @ResponseBody
    public Result processException(GlobalExecption ex, HttpServletResponse response) {
        String result;
        if (ex.getExceptionId() == -1) {
            result = "系统发生未知异常";
        } else {
            result = "系统发生异常，异常编号" + ex.getExceptionId();
        }
        return Result.fail(result);
    }
    //TODO 处理其他错误，并进行分别处理异常

}
