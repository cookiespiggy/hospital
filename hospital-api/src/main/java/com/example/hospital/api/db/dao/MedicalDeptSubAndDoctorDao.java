package com.example.hospital.api.db.dao;

import com.example.hospital.api.db.pojo.MedicalDeptSubAndDoctorEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface MedicalDeptSubAndDoctorDao {
    @Transactional
    public void insert(MedicalDeptSubAndDoctorEntity entity);

    public void updateDoctorSubDept(Map param);
}





