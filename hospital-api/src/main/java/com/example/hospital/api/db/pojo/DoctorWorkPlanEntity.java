package com.example.hospital.api.db.pojo;

import lombok.Data;

@Data
public class DoctorWorkPlanEntity {
    private Integer id;
    private Integer doctorId;
    private Integer deptSubId;
    private String date;
    private Integer maximum;
    private Integer num;

}