package com.example.hospital.patient.wx.api.db.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class MedicalRegistrationEntity {
    private Integer id;
    private Integer patientCardId;
    private Integer workPlanId;
    private Integer doctorScheduleId;
    private Integer doctorId;
    private Integer deptSubId;
    private String date;
    private Integer slot;
    private BigDecimal amount;
    private String outTradeNo;
    private String prepayId;
    private String transactionId;
    private Byte paymentStatus;
    private Date createTime;
}