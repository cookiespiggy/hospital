package com.example.hospital.api.controller.form;

import com.example.hospital.api.controller.form.vo.DoctorScheduleSlotVO;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@Data
public class UpdateDoctorScheduleForm {
    @NotNull(message = "workPlanId不能为空")
    @Min(value = 1, message = "workPlanId内容不正确")
    private Integer workPlanId;

    @NotNull(message = "maximum不能为空")
    @Range(min = 1, max = 150, message = "maximum内容不正确")
    private Integer maximum;

    @Valid
    @NotEmpty(message = "slots不能为空")
    private ArrayList<DoctorScheduleSlotVO> slots;

}

