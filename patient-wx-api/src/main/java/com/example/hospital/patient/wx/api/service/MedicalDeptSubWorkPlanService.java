package com.example.hospital.patient.wx.api.service;

import cn.hutool.json.JSONArray;

import java.util.ArrayList;
import java.util.Map;

public interface MedicalDeptSubWorkPlanService {
    public JSONArray searchWorkPlanInRange(Map param, ArrayList dateList);
}
