package com.example.hospital.api.db.dao;

import com.example.hospital.api.db.pojo.MedicalDeptEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface MedicalDeptDao {
    public ArrayList<HashMap> searchAll();

    public ArrayList<HashMap> searchDeptAndSub();

    public ArrayList<HashMap> searchByPage(Map param);
    public long searchCount(Map param);

    public void insert(MedicalDeptEntity entity);

    public HashMap searchById(int id);
    public void update(MedicalDeptEntity entity);

    public long searchSubCount(Integer[] ids);
    public void deleteByIds(Integer[] ids);


}




