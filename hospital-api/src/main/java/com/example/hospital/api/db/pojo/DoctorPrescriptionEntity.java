package com.example.hospital.api.db.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DoctorPrescriptionEntity {
    private Integer id;
    private String uuid;
    private Integer patientCardId;
    private String diagnosis;
    private Integer subDeptId;
    private Integer doctorId;
    private Integer registrationId;
    private String rp;

}