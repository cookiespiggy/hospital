package com.example.hospital.patient.wx.api.db.dao;

import com.example.hospital.patient.wx.api.db.pojo.VideoDiagnoseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface VideoDiagnoseDao {
    
    @Transactional
    public void insert(VideoDiagnoseEntity entity);
    public HashMap searchByOutTradeNo(String outTradeNo);

    public void updatePayment(Map param);


    public ArrayList<HashMap> searchByPage(Map param);
    public long searchCount(Map param);
    
}




