package com.example.hospital.patient.wx.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.hospital.patient.wx.api.common.R;
import com.example.hospital.patient.wx.api.controller.form.SearchMedicalDeptSubListForm;
import com.example.hospital.patient.wx.api.service.MedicalDeptSubService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/medical/dept/sub")
public class MedicalDeptSubController {
    @Resource
    private MedicalDeptSubService medicalDeptSubService;

    @PostMapping("/searchMedicalDeptSubList")
    @SaCheckLogin
    public R searchMedicalDeptSubList(@RequestBody @Valid SearchMedicalDeptSubListForm form) {
        ArrayList<HashMap> list = medicalDeptSubService.searchMedicalDeptSubList(form.getDeptId());
        return R.ok().put("result", list);
    }
}
