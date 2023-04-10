package com.example.hospital.api.service;

import cn.hutool.json.JSONArray;

import java.util.ArrayList;
import java.util.Map;

public interface MedicalDeptSubWorkPlanService {
    public JSONArray searchWorkPlanInRange(Map param, ArrayList dateList);

    public String insert(Map param);

    public void deleteWorkPlan(int workPlanId);
}
