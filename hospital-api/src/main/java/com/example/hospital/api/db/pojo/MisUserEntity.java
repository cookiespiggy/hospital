package com.example.hospital.api.db.pojo;

import lombok.Data;

@Data
public class MisUserEntity {
    private Integer id;
    private String username;
    private String password;
    private String name;
    private String sex;
    private String tel;
    private String email;
    private Integer deptId;
    private String job;
    private Integer refId;
    private Byte status;
    private String createTime;
}