package com.example.hospital.patient.wx.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface MedicalDeptService {
    public ArrayList<HashMap> searchMedicalDeptList(Map param);

    public HashMap searchDeptAndSub();
}

