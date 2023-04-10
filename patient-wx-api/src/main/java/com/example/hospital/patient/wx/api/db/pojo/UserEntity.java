package com.example.hospital.patient.wx.api.db.pojo;

import lombok.Data;

@Data
public class UserEntity {
    private Integer id;
    private String openId;
    private String nickname;
    private String photo;
    private String sex;
    private byte status;
    private String createTime;
}
