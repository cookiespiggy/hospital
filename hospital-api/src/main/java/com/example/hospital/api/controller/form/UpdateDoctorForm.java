package com.example.hospital.api.controller.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;

@Data
public class UpdateDoctorForm {
    @NotNull(message = "id不能为空")
    @Min(value = 1, message = "id不能小于1")
    private Integer id;

    @NotBlank(message = "name不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9f5a]{2,20}$", message = "name内容不正确")
    private String name;

    @NotBlank(message = "pid不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$", message = "pid内容不正确")
    private String pid;

    @NotBlank(message = "sex不能为空")
    @Pattern(regexp = "^男$|^女$", message = "sex内容不正确")
    private String sex;

    @NotBlank(message = "birthday不能为空")
    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$",
            message = "birthday内容不正确")
    private String birthday;

    @NotBlank(message = "school不能为空")
    @Length(min = 2, max = 50, message = "school内容不正确")
    private String school;

    @NotBlank(message = "degree不能为空")
    @Pattern(regexp = "^本科$|^研究生$|^博士$", message = "degree内容不正确")
    private String degree;

    @NotBlank(message = "tel不能为空")
    @Pattern(regexp = "^1[1-9][0-9]{9}$", message = "tel内容不正确")
    private String tel;

    @NotBlank(message = "address不能为空")
    @Length(max = 200, message = "address内容不正确")
    private String address;

    @NotBlank(message = "email不能为空")
    @Email
    @Length(max = 200, message = "email内容不正确")
    private String email;

    @NotBlank(message = "job不能为空")
    @Pattern(regexp = "^主治医师$|^副主治医师$|^主任医师$|^副主任医师$", message = "job内容不正确")
    private String job;

    @NotBlank(message = "remark不能为空")
    @Length(max = 50, message = "remark内容不正确")
    private String remark;

    @NotBlank(message = "description不能为空")
    private String description;

    @NotBlank(message = "birthday不能为空")
    @Pattern(regexp = "^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-))$",
            message = "birthday内容不正确")
    private String hiredate;

    private String[] tag;

    @NotNull(message = "recommended不能为空")
    private Boolean recommended;

    @NotNull(message = "status不能为空")
    @Range(min = 1, max = 3, message = "status不能为空")
    private Byte status;

    @NotNull(message = "subId不能为空")
    @Min(value = 1, message = "subId不能小于1")
    private Integer subId;
}
