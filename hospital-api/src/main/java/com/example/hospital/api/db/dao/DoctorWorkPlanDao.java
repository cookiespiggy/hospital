package com.example.hospital.api.db.dao;


import com.example.hospital.api.db.pojo.DoctorWorkPlanEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface DoctorWorkPlanDao {
    public ArrayList<HashMap> searchWorkPlanInRange(Map param);

    public Integer searchId(Map param);

    @Transactional
    public void insert(DoctorWorkPlanEntity entity);

    public void updateMaximum(Map param);

    public Integer searchNumById(int id);
    public void deleteById(int id);
}



