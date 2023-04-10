package com.example.hospital.patient.wx.api.db.dao;

import com.example.hospital.patient.wx.api.db.pojo.FaceAuthEntity;

import java.util.Map;

public interface FaceAuthDao {
    public Integer hasFaceAuthInDay(Map param);

    public int insert(FaceAuthEntity entity);
}



