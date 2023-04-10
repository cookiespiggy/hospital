package com.example.hospital.patient.wx.api.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.hospital.patient.wx.api.db.dao.UserDao;
import com.example.hospital.patient.wx.api.db.dao.UserInfoCardDao;
import com.example.hospital.patient.wx.api.db.pojo.UserEntity;
import com.example.hospital.patient.wx.api.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
public class UserServiceImpl implements UserService {
    @Value("${wechat.app-id}")
    private String appId;

    @Value("${wechat.app-secret}")
    private String appSecret;

    @Resource
    private UserDao userDao;

    @Resource
    private UserInfoCardDao userInfoCardDao;

    @Override
    public HashMap loginOrRegister(String code, String nickname, String photo, String sex) {
        //用临时授权兑换open_id
        String openId = this.getOpenId(code);

        HashMap map = new HashMap();
        //是否为已注册用户
        Integer id = userDao.searchAlreadyRegistered(openId);
        if (id == null) {
            UserEntity entity = new UserEntity();
            entity.setOpenId(openId);
            entity.setNickname(nickname);
            entity.setPhoto(photo);
            entity.setSex(sex);
            entity.setStatus((byte) 1);
            //执行新用户注册
            userDao.insert(entity);
            //查询新用户的主键值
            id = userDao.searchAlreadyRegistered(entity.getOpenId());
            map.put("msg", "注册成功");
        }
        else{
            map.put("msg", "登陆成功");
        }
        //查询患者信息卡中的电话号码
        String tel = userInfoCardDao.searchUserTel(id);
        map.put("id", id);
        map.put("tel",tel);
        return map;
    }

    @Override
    public HashMap searchUserInfo(int userId) {
        HashMap map = userDao.searchUserInfo(userId);
        String tel = userInfoCardDao.searchUserTel(userId);
        map.put("tel", tel);
        return map;
    }



    //获取患者微信的open_id字符串
    private String getOpenId(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        HashMap map = new HashMap();
        map.put("appid", appId);
        map.put("secret", appSecret);
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String response = HttpUtil.post(url, map);
//        System.out.print(response);
        JSONObject json = JSONUtil.parseObj(response);
        String openId = json.getStr("openid");
        if (openId == null || openId.length() == 0) {
            throw new RuntimeException("临时登陆凭证错误");
        }

        return openId;
    }
}
