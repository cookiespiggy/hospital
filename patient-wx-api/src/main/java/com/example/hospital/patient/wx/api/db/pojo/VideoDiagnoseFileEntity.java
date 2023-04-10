package com.example.hospital.patient.wx.api.db.pojo;

import lombok.Data;

@Data
public class VideoDiagnoseFileEntity {
    private Integer id;
    private Integer videoDiagnoseId;
    private String filename;
    private String path;
    private String createTime;
}