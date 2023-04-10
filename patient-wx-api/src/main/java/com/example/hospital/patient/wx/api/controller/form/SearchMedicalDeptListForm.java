package com.example.hospital.patient.wx.api.controller.form;

import lombok.Data;

@Data
public class SearchMedicalDeptListForm {
    private Boolean recommended;

    private Boolean outpatient;
}
