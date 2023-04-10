package com.example.hospital.patient.wx.api.db.pojo;

import java.io.Serializable;

import lombok.Data;

@Data
public class DoctorPrescriptionEntity{
    private Integer id;
    private String uuid;
    private Integer patientCardId;
    private String diagnosis;
    private Integer subDeptId;
    private Integer doctorId;
    private Integer registrationId;
    private String rp;

}