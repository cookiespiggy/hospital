package com.example.hospital.api.controller.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class SearchImageByVideoDiagnoseIdForm {
    @NotNull(message = "videoDiagnoseId不能为空")
    @Min(value = 1, message = "videoDiagnoseId不能小于1")
    private Integer videoDiagnoseId;
}

