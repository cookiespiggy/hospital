package com.example.hospital.patient.wx.api.db.dao;

import com.example.hospital.patient.wx.api.db.pojo.MedicalRegistrationEntity;
import org.apache.hadoop.util.hash.Hash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface MedicalRegistrationDao {

    public long searchRegistrationCountInToday(Map param);
    public Integer hasRegisterRecordInDay(Map param);

    public int insert(MedicalRegistrationEntity entity);

    public int discardPayment(String outTradeNo);
    public HashMap searchWorkPlanIdAndScheduleId(String outTradeNo);

    public int updatePayment(Map param);

    public long searchRegistrationCount(Map param);
    public ArrayList<HashMap> searchRegistrationByPage(Map param);

    public HashMap searchRepayInfo(Map param);
    public int updateRepayInfo(Map param);

    public HashMap searchRegistrationInfo(Map param);
}





