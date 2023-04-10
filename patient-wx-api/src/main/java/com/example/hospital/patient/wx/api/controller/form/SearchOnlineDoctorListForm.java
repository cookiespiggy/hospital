package com.example.hospital.patient.wx.api.controller.form;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class SearchOnlineDoctorListForm {
    private String subName;

    @Pattern(regexp = "^主任医师$|^副主任医师$|^主治医师$|^副主治医师$", message = "job内容不正确")
    private String job;
}
