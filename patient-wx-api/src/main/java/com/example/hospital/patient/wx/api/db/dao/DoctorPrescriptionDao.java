package com.example.hospital.patient.wx.api.db.dao;


import java.util.HashMap;
import java.util.Map;

public interface DoctorPrescriptionDao {
    public HashMap searchPrescriptionByRegistrationId(Map param);
}
