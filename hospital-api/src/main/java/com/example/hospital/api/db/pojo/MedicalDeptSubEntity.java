package com.example.hospital.api.db.pojo;

import lombok.Data;

@Data
public class MedicalDeptSubEntity {
    private Integer id;
    private String name;
    private Integer deptId;
    private String location;
}