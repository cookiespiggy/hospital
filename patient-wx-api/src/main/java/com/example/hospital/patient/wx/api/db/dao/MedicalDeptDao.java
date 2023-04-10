package com.example.hospital.patient.wx.api.db.dao;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface MedicalDeptDao {
    public ArrayList<HashMap> searchMedicalDeptList(Map param);

    public ArrayList<HashMap> searchDeptAndSub();
}





