package com.example.hospital.patient.wx.api.controller.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class SearchMedicalDeptSubListForm {
    @NotNull(message = "deptId不能为空")
    @Min(value = 1, message = "deptId不能小于1")
    private Integer deptId;

}
