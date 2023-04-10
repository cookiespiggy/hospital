package com.example.hospital.patient.wx.api.controller.form;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class RegisterMedicalAppointmentForm {
    @NotNull(message = "workPlanId不能为空")
    @Min(value = 1, message = "workPlanId不能小于1")
    private Integer workPlanId;

    @NotNull(message = "scheduleId不能为空")
    @Min(value = 1, message = "scheduleId不能小于1")
    private Integer scheduleId;

    @NotBlank(message = "date不能为空")
    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29))$",
            message = "date内容不正确")
    private String date;

    @NotNull(message = "doctorId不能为空")
    @Min(value = 1, message = "doctorId不能小于1")
    public Integer doctorId;

    @NotNull(message = "deptSubId不能为空")
    @Min(value = 1, message = "deptSubId不能小于1")
    private Integer deptSubId;

    @NotBlank(message = "amount不能为空")
    @Pattern(regexp = "^[1-9]\\d*\\.\\d{1,2}$|^0\\.\\d{1,2}$|^[1-9]\\d*$", message = "amount内容不正确")
    private String amount;

    @NotNull(message = "slot不能为空")
    @Range(min = 1, max = 15, message = "slot内容不正确")
    private Byte slot;
}
