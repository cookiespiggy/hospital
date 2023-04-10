package com.example.hospital.patient.wx.api.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.example.hospital.patient.wx.api.db.dao.UserInfoCardDao;
import com.example.hospital.patient.wx.api.db.pojo.UserInfoCardEntity;
import com.example.hospital.patient.wx.api.service.UserInfoCardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
public class UserInfoCardServiceImpl implements UserInfoCardService {
    @Resource
    private UserInfoCardDao userInfoCardDao;

    @Override
    @Transactional
    public void insert(UserInfoCardEntity entity) {

        userInfoCardDao.insert(entity);
    }
    @Override
    public HashMap searchUserInfoCard(int userId) {
        HashMap map = userInfoCardDao.searchUserInfoCard(userId);
        if (map != null) {
            String medicalHistory = MapUtil.getStr(map, "medicalHistory");
            JSONArray array = JSONUtil.parseArray(medicalHistory);
            map.replace("medicalHistory", array);
        }
        return map;
    }

    @Override
    @Transactional
    public void update(UserInfoCardEntity entity) {
        userInfoCardDao.update(entity);
    }

    @Override
    public boolean hasUserInfoCard(int userId) {
        Integer id = userInfoCardDao.hasUserInfoCard(userId);
        boolean bool = (id != null);
        return bool;
    }
}