package com.example.hospital.api.db.pojo;

import lombok.Data;

@Data
public class MedicalDeptEntity {
    private Integer id;
    private String name;
    private Boolean outpatient;
    private String description;
    private Boolean recommended;
}