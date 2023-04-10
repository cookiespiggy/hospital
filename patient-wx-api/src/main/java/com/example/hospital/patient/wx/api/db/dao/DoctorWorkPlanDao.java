package com.example.hospital.patient.wx.api.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface DoctorWorkPlanDao {
    public ArrayList<HashMap> searchWorkPlanInRange(Map param);


    public ArrayList<String> searchCanRegisterInDateRange(Map param);
    public ArrayList<HashMap> searchDeptSubDoctorPlanInDay(Map param);

    public int updateNumById(Map param);

    public int releaseNumByOutTradeNo(String outTradeNo);

}




