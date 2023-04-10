package com.example.hospital.patient.wx.api.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SearchPaymentResultForm {
    @NotBlank(message = "outTradeNo不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{32}$", message = "outTradeNo内容不正确")
    private String outTradeNo;
}

