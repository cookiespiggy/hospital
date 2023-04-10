package com.example.hospital.api.db.pojo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class VideoDiagnoseEntity {
    private Integer id;
    private Integer patientCardId;
    private Integer doctorId;
    private String outTradeNo;
    private BigDecimal amount;
    private Byte paymentStatus;
    private String prepayId;
    private String transactionId;
    private String expectStart;
    private String expectEnd;
    private String realStart;
    private String realEnd;
    private Byte status;
    private String createTime;

}