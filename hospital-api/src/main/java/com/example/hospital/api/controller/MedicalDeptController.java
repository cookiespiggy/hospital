package com.example.hospital.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import com.example.hospital.api.common.PageUtils;
import com.example.hospital.api.common.R;
import com.example.hospital.api.controller.form.*;
import com.example.hospital.api.db.pojo.MedicalDeptEntity;
import com.example.hospital.api.service.MedicalDeptService;
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

    @GetMapping("/searchAll")
    @SaCheckLogin
    public R searchAll() {
        ArrayList<HashMap> list = medicalDeptService.searchAll();
        return R.ok().put("result", list);
    }

    @GetMapping("/searchDeptAndSub")
    @SaCheckLogin
    public R searchDeptAndSub() {
        HashMap map = medicalDeptService.searchDeptAndSub();
        return R.ok().put("result", map);
    }

    @PostMapping("/searchByPage")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "MEDICAL_DEPT:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@RequestBody @Valid SearchMedicalDeptByPageForm form) {
        Map param = BeanUtil.beanToMap(form);
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        param.put("start", start);
        PageUtils pageUtils = medicalDeptService.searchByPage(param);
        return R.ok().put("result", pageUtils);
    }

    @PostMapping("/insert")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "MEDICAL_DEPT:INSERT"}, mode = SaMode.OR)
    public R insert(@RequestBody @Valid InsertMedicalDeptForm form) {
        MedicalDeptEntity entity = BeanUtil.toBean(form, MedicalDeptEntity.class);
        medicalDeptService.insert(entity);
        return R.ok();
    }

    @PostMapping("/searchById")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "MEDICAL_DEPT:SELECT"}, mode = SaMode.OR)
    public R searchById(@RequestBody @Valid SearchMedicalDeptByIdForm form) {
        HashMap map = medicalDeptService.searchById(form.getId());
        return R.ok(map);
    }

    @PostMapping("/update")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "MEDICAL_DEPT:UPDATE"}, mode = SaMode.OR)
    public R update(@RequestBody @Valid UpdateMedicalDeptForm form) {        MedicalDeptEntity entity = BeanUtil.toBean(form, MedicalDeptEntity.class);
        medicalDeptService.update(entity);
        return R.ok();
    }

    @PostMapping("/deleteByIds")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "MEDICAL_DEPT:DELETE"}, mode = SaMode.OR)
    public R deleteByIds(@RequestBody @Valid DeleteMedicalDeptByIdsForm form) {
        medicalDeptService.deleteByIds(form.getIds());
        return R.ok();
    }
}





