package com.example.hospital.api.exception;


import lombok.Data;

@Data
public class HospitalException extends RuntimeException {
    private String msg;
    private int code = 500;

    public HospitalException(Exception e) {
        super(e);
        this.msg = "执行异常";
        this.code = 500;
    }

    public HospitalException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public HospitalException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public HospitalException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public HospitalException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

}