package com.example.hospital.patient.wx.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.example.hospital.patient.wx.api.common.R;
import com.example.hospital.patient.wx.api.controller.form.CreateFaceModelForm;
import com.example.hospital.patient.wx.api.controller.form.VerifyFaceModelForm;
import com.example.hospital.patient.wx.api.service.FaceAuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/face/auth")
public class FaceAuthController {
    @Resource
    private FaceAuthService faceAuthService;

    @PostMapping("/createFaceModel")
    @SaCheckLogin
    public R createFaceModel(@RequestBody @Valid CreateFaceModelForm form) {
        int userId = StpUtil.getLoginIdAsInt();
        form.setUserId(userId);
        Map param = BeanUtil.beanToMap(form);
        faceAuthService.createFaceModel(param);
        return R.ok();
    }

    @PostMapping("/verifyFaceModel")
    @SaCheckLogin
    public R verifyFaceModel(@RequestBody @Valid VerifyFaceModelForm form) {
        int userId = StpUtil.getLoginIdAsInt();
        boolean bool = faceAuthService.verifyFaceModel(userId, form.getPhoto());
        return R.ok().put("result", bool);
    }
}

