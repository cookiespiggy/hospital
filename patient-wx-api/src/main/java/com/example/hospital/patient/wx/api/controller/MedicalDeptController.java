package com.example.hospital.patient.wx.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import com.example.hospital.patient.wx.api.common.R;
import com.example.hospital.patient.wx.api.controller.form.SearchMedicalDeptListForm;
import com.example.hospital.patient.wx.api.service.MedicalDeptService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/medical/dept")
public class MedicalDeptController {
    @Resource
    private MedicalDeptService medicalDeptService;

    @PostMapping("/searchMedicalDeptList")
    public R searchMedicalDeptList(@RequestBody @Valid SearchMedicalDeptListForm form) {
        Map param = BeanUtil.beanToMap(form);
        ArrayList<HashMap> list = medicalDeptService.searchMedicalDeptList(param);
        return R.ok().put("result", list);
    }

    @GetMapping("/searchDeptAndSub")
    @SaCheckLogin
    public R searchDeptAndSub() {
        HashMap map = medicalDeptService.searchDeptAndSub();
        return R.ok().put("result", map);
    }
}