package com.example.hospital.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import com.example.hospital.api.common.PageUtils;
import com.example.hospital.api.common.R;
import com.example.hospital.api.controller.form.*;
import com.example.hospital.api.db.pojo.MedicalDeptSubEntity;
import com.example.hospital.api.service.MedicalDeptSubService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/medical/dept/sub")
public class MedicalDeptSubController {
    @Resource
    private MedicalDeptSubService medicalDeptSubService;

    @PostMapping("/searchByPage")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "MEDICAL_DEPT_SUB:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@RequestBody @Valid SearchMedicalDeptSubByPageForm form) {
        Map param = BeanUtil.beanToMap(form);
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        param.put("start", start);
        PageUtils pageUtils = medicalDeptSubService.searchByPage(param);
        return R.ok().put("result", pageUtils);
    }

    @PostMapping("/insert")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "MEDICAL_DEPT_SUB:INSERT"}, mode = SaMode.OR)
    public R insert(@RequestBody @Valid InsertMedicalDeptSubForm form) {
        MedicalDeptSubEntity entity = BeanUtil.toBean(form, MedicalDeptSubEntity.class);
        medicalDeptSubService.insert(entity);
        return R.ok();
    }

    @PostMapping("/searchById")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "MEDICAL_DEPT_SUB:SELECT"}, mode = SaMode.OR)
    public R searchById(@RequestBody @Valid SearchMedicalDeptSubByIdForm form) {
        HashMap map = medicalDeptSubService.searchById(form.getId());
        return R.ok(map);
    }

    @PostMapping("/update")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "MEDICAL_DEPT_SUB:UPDATE"}, mode = SaMode.OR)
    public R update(@RequestBody @Valid UpdateMedicalDeptSubForm form) {
        MedicalDeptSubEntity entity = BeanUtil.toBean(form, MedicalDeptSubEntity.class);
        medicalDeptSubService.update(entity);
        return R.ok();
    }

    @PostMapping("/deleteByIds")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "MEDICAL_DEPT_SUB:DELETE"}, mode = SaMode.OR)
    public R deleteByIds(@RequestBody @Valid DeleteMedicalDeptSubByIdsForm form) {
        medicalDeptSubService.deleteByIds(form.getIds());
        return R.ok();
    }
}

