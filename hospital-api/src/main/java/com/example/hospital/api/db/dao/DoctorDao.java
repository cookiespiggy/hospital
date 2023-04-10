package com.example.hospital.api.db.dao;

import com.example.hospital.api.db.pojo.DoctorEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface DoctorDao {
    public ArrayList<HashMap> searchByPage(Map param);
    public long searchCount(Map param);
    public HashMap searchContent(int id);

    public void updatePhoto(Map param);

    @Transactional
    public void insert(DoctorEntity entity);
    public Integer searchIdByUuid(String uuid);
    public HashMap searchById(int id);
    public void update(Map param);
    public void deleteByIds(Integer[] ids);

    public ArrayList<HashMap> searchByDeptSubId(int deptSubId);

    public HashMap searchDataForOnlineCache(int doctorId);
}





