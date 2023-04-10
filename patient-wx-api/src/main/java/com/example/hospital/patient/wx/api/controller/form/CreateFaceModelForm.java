package com.example.hospital.patient.wx.api.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateFaceModelForm {

    private Integer userId;

    @NotBlank(message = "photo不能为空")
    private String photo;
}
