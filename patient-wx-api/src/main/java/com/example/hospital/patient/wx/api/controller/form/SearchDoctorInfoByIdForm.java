package com.example.hospital.patient.wx.api.controller.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class SearchDoctorInfoByIdForm {
    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "id不能小于1")
    private Integer id;
}
