package com.example.hospital.patient.wx.api.service;

import com.example.hospital.patient.wx.api.common.PageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface RegistrationService {
    public ArrayList<String> searchCanRegisterInDateRange(Map param);
    public ArrayList<HashMap> searchDeptSubDoctorPlanInDay(Map param);

    public String checkRegisterCondition(Map param);

    public ArrayList<HashMap> searchDoctorWorkPlanSchedule(Map param);

    public HashMap registerMedicalAppointment(Map param);

    public void updatePayment(Map param);

    public boolean searchPaymentResult(String outTradeNo);

    public PageUtils searchRegistrationByPage(Map param);

    public HashMap repayRegistration(Map param);

    public HashMap searchRegistrationInfo(Map param);
}
