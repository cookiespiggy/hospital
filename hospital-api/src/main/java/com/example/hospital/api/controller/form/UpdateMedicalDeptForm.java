package com.example.hospital.api.controller.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class UpdateMedicalDeptForm {
    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "id不能小于1")
    private Integer id;

    @NotBlank(message = "name不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]{2,10}$", message = "name内容不正确")
    private String name;

    @NotNull(message = "outpatient不能为空")
    private Boolean outpatient;

    @NotNull(message = "recommended不能为空")
    private Boolean recommended;

    @NotNull(message = "description不能为空")
    private String description;
}

