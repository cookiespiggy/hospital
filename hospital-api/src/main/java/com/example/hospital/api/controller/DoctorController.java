package com.example.hospital.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.example.hospital.api.common.PageUtils;
import com.example.hospital.api.common.R;
import com.example.hospital.api.controller.form.*;
import com.example.hospital.api.service.DoctorService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/doctor")
public class DoctorController {
    @Resource
    private DoctorService doctorService;

    @PostMapping("/searchByPage")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "DOCTOR:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@RequestBody @Valid SearchDoctorByPageForm form) {
        Map param = BeanUtil.beanToMap(form);
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        param.put("start", start);
        PageUtils pageUtils = doctorService.searchByPage(param);
        return R.ok().put("result", pageUtils);
    }

    @PostMapping("/searchContent")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "DOCTOR:SELECT"}, mode = SaMode.OR)
    public R searchContent(@RequestBody @Valid SearchDoctorContentForm form) {
        HashMap map = doctorService.searchContent(form.getId());
        return R.ok(map);
    }

    @PostMapping("/updatePhoto")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "DOCTOR:UPDATE"}, mode = SaMode.OR)
    public R updatePhoto(@Param("file") MultipartFile file, @Param("doctorId") Integer doctorId) {
        doctorService.updatePhoto(file, doctorId);
        return R.ok();
    }

    @PostMapping("/insert")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "DOCTOR:INSERT"}, mode = SaMode.OR)
    public R insert(@RequestBody @Valid InsertDoctorForm form) {
        Map param = BeanUtil.beanToMap(form);
        String json = JSONUtil.parseArray(form.getTag()).toString();
        param.replace("tag", json);
        param.put("uuid", IdUtil.simpleUUID().toUpperCase());
        doctorService.insert(param);
        return R.ok();
    }

    @PostMapping("/searchById")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "DOCTOR:SELECT"}, mode = SaMode.OR)
    public R searchById(@RequestBody @Valid SearchDoctorByIdForm form) {
        HashMap map = doctorService.searchById(form.getId());
        return R.ok(map);
    }

    @PostMapping("/update")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "DOCTOR:UPDATE"}, mode = SaMode.OR)
    public R update(@RequestBody @Valid UpdateDoctorForm form) {
        Map param = BeanUtil.beanToMap(form);
        String json = JSONUtil.parseArray(form.getTag()).toString();
        param.replace("tag", json);
        doctorService.update(param);
        return R.ok();
    }

    @PostMapping("/deleteByIds")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "DOCTOR:DELETE"}, mode = SaMode.OR)
    public R deleteByIds(@RequestBody @Valid DeleteDoctorByIdsForm form) {
        doctorService.deleteByIds(form.getIds());
        return R.ok();
    }
    @PostMapping("/searchByDeptSubId")
    @SaCheckLogin
    public R searchByDeptSubId(@RequestBody @Valid SearchDoctorByDeptSubIdForm form) {
        ArrayList<HashMap> list = doctorService.searchByDeptSubId(form.getDeptSubId());
        return R.ok().put("result", list);
    }
}
