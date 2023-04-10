package com.example.hospital.patient.wx.api.controller.form;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UpdateUserInfoCardForm {
    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "id不能小于1")
    private Integer id;

    @NotBlank(message = "name不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{2,15}$", message = "name内容不正确")
    private String name;

    @NotBlank(message = "sex不能为空")
    @Pattern(regexp = "^男$|^女$", message = "性别内容不正确")
    private String sex;

    @NotBlank(message = "pid不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", message = "pid内容不正确")
    private String pid;

    @NotBlank(message = "tel不能为空")
    @Pattern(regexp = "^1[0-9]{10}$", message = "tel内容不正确")
    private String tel;

    @NotBlank(message = "birthday不能为空")
    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$",
            message = "birthday内容不正确")
    private String birthday;

    @NotEmpty(message = "medicalHistory不能为空")
    private String[] medicalHistory;

    @NotBlank(message = "insuranceType不能为空")
    private String insuranceType;
}
