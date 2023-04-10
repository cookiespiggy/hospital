package com.example.hospital.patient.wx.api.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface DoctorWorkPlanScheduleDao {
    public ArrayList<HashMap> searchDoctorWorkPlanSchedule(Map param);

//    public ArrayList<HashMap> searchEligibleSchedule(Map param);

    public int updateNumById(Map param);

    public int releaseNumByOutTradeNo(String outTradeNo);

}




