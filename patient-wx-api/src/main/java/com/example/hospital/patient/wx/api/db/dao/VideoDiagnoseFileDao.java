package com.example.hospital.patient.wx.api.db.dao;


import com.example.hospital.patient.wx.api.db.pojo.VideoDiagnoseFileEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface VideoDiagnoseFileDao {

    public ArrayList<HashMap> searchImageByVideoDiagnoseId(int videoDiagnoseId);

    public void insert(VideoDiagnoseFileEntity entity);

    public void delete(Map param);
    public ArrayList<String> searchVideoDiagnoseImage(int videoDiagnoseId);
}




