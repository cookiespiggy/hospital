package com.example.hospital.patient.wx.api.controller.form;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class SearchRegistrationByPageForm {
    private int userId;

    @NotNull(message = "page不能为空")
    @Min(value = 1, message = "page不能小于1")
    private Integer page;

    @NotNull(message = "length不能为空")
    @Range(min = 10, max = 50, message = "length内容不正确")
    private Integer length;
}
