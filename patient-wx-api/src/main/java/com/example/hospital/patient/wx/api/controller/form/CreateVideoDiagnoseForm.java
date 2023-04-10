package com.example.hospital.patient.wx.api.controller.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class CreateVideoDiagnoseForm {
    @NotNull(message = "doctorId不能为空")
    @Min(value = 1, message = "doctorId不能小于1")
    private Integer doctorId;

}
