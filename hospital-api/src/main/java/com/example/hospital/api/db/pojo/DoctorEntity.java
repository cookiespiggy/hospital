package com.example.hospital.api.db.pojo;

import lombok.Data;

@Data
public class DoctorEntity {
    private Integer id;
    private String name;
    private String pid;
    private String uuid;
    private String sex;
    private String photo;
    private String birthday;
    private String school;
    private String degree;
    private String tel;
    private String address;
    private String email;
    private String job;
    private String remark;
    private String description;
    private String hiredate;
    private String tag;
    private Boolean recommended;
    private Byte status;
    private String createTime;

}