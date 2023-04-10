package com.example.hospital.api.db.dao;

import com.example.hospital.api.db.pojo.MedicalDeptSubEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface MedicalDeptSubDao {
    public ArrayList<HashMap> searchByPage(Map param);
    public long searchCount(Map param);

    public void insert(MedicalDeptSubEntity entity);

    public HashMap searchById(int id);
    public void update(MedicalDeptSubEntity entity);

    public long searchDoctorCount(Integer[] ids);
    public void deleteByIds(Integer[] ids);
}




