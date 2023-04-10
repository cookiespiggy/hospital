package com.example.hospital.patient.wx.api.db.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MedicalDeptEntity implements Serializable {
    private Object id;
    private String name;
    private Boolean outpatient;
    private String description;
    private String location;
    private Boolean recommended;
}