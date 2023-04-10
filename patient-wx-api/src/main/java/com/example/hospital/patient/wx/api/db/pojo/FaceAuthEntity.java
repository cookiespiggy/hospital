package com.example.hospital.patient.wx.api.db.pojo;

import java.util.Date;

import lombok.Data;

@Data
public class FaceAuthEntity {
    private Integer id;
    private Integer patientCardId;
    private String date;
}