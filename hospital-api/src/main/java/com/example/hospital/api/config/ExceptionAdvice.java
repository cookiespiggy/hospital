package com.example.hospital.api.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.felord.payment.PayException;
import cn.hutool.json.JSONObject;
import com.example.hospital.api.exception.HospitalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e) {
        JSONObject json = new JSONObject();
        if (e instanceof HttpMessageNotReadableException) {
            HttpMessageNotReadableException exception = (HttpMessageNotReadableException) e;
            log.error("error", exception);
            json.set("error", "请求未提交数据或者数据有误");
        } else if (e instanceof MissingServletRequestPartException) {
            MissingServletRequestPartException exception = (MissingServletRequestPartException) e;
            log.error("error", exception);
            json.set("error", "请求提交数据错误");
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            HttpRequestMethodNotSupportedException exception = (HttpRequestMethodNotSupportedException) e;
            log.error("error", exception);
            json.set("error", "HTTP请求方法类型错误");
        }
        //处理后端验证失败产生的异常
        else if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            json.set("error", exception.getBindingResult().getFieldError().getDefaultMessage());
        }
        //处理业务异常
        else if (e instanceof HospitalException) {
            log.error("执行异常", e);
            HospitalException exception = (HospitalException) e;
            json.set("error", exception.getMsg());
        } else if (e instanceof PayException) {
            PayException exception = (PayException) e;
            log.error("微信支付异常", exception);
            json.set("error", "微信支付异常");
        }
        //处理其余的异常
        else {
            log.error("执行异常", e);
            json.set("error", "执行异常");
        }
        return json.toString();
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotLoginException.class)
    public String unLoginHandler(Exception e) {
        JSONObject json = new JSONObject();
        json.set("error", e.getMessage());
        return json.toString();
    }

}