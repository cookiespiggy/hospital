package com.example.hospital.patient.wx.api.db.pojo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class DoctorWorkPlanEntity {
    private Integer id;
    private Integer doctorId;
    private Integer deptSubId;
    private Date date;
    private Short maximum;
    private Short num;

}