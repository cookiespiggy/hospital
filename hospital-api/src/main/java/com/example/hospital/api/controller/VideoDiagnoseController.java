package com.example.hospital.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.example.hospital.api.common.R;
import com.example.hospital.api.config.tencent.TrtcUtil;
import com.example.hospital.api.controller.form.SearchImageByVideoDiagnoseIdForm;
import com.example.hospital.api.controller.form.UpdateCanRegisterForm;
import com.example.hospital.api.service.VideoDiagnoseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/video_diagnose")
@Slf4j
public class VideoDiagnoseController {
    @Value("${tencent.trtc.appId}")
    private int appId;

    @Resource
    private TrtcUtil trtcUtil;

    @Resource
    private VideoDiagnoseService videoDiagnoseService;

    @GetMapping("/searchMyUserSig")
    @SaCheckLogin
    public R searchMyUserSig() {
        int userId = StpUtil.getLoginIdAsInt();
        String userSig = trtcUtil.genUserSig(userId + "");
        return R.ok().put("userSig", userSig).put("userId", userId).put("appId", appId);
    }

    @GetMapping("/online")
    @SaCheckLogin
    @SaCheckPermission(value = {"VIDEO_DIAGNOSE:DIAGNOSE"}, mode = SaMode.OR)
    public R online() {
        int userId = StpUtil.getLoginIdAsInt();
        videoDiagnoseService.online(userId);
        return R.ok();
    }

    @GetMapping("/offline")
    @SaCheckLogin
    @SaCheckPermission(value = {"VIDEO_DIAGNOSE:DIAGNOSE"}, mode = SaMode.OR)
    public R offline() {
        int userId = StpUtil.getLoginIdAsInt();
        boolean bool = videoDiagnoseService.offline(userId);
        return R.ok().put("result", bool);
    }

    @PostMapping("/updateOpenFlag")
    @SaCheckLogin
    @SaCheckPermission(value = {"VIDEO_DIAGNOSE:DIAGNOSE"}, mode = SaMode.OR)
    public R updateOpenFlag(@RequestBody @Valid UpdateCanRegisterForm form) {
        int userId = StpUtil.getLoginIdAsInt();
        videoDiagnoseService.updateOpenFlag(userId, form.getOpen());
        return R.ok();
    }

    @GetMapping("/refreshInfo")
    @SaCheckLogin
    @SaCheckPermission(value = {"VIDEO_DIAGNOSE:DIAGNOSE"}, mode = SaMode.OR)
    public R refreshInfo() {
        int userId = StpUtil.getLoginIdAsInt();
        HashMap map = videoDiagnoseService.refreshInfo(userId);
        return R.ok().put("result", map);
    }

    @GetMapping("/searchVideoDiagnoseInfo")
    @SaCheckLogin
    @SaCheckPermission(value = {"VIDEO_DIAGNOSE:DIAGNOSE"}, mode = SaMode.OR)
    public R searchVideoDiagnoseInfo() {
        int userId = StpUtil.getLoginIdAsInt();
        HashMap map = videoDiagnoseService.searchVideoDiagnoseInfo(userId);
        return R.ok().put("result", map);
    }

    @PostMapping("/searchImageByVideoDiagnoseId")
    @SaCheckLogin
    @SaCheckPermission(value = {"VIDEO_DIAGNOSE:DIAGNOSE"}, mode = SaMode.OR)
    public R searchImageByVideoDiagnoseId(@RequestBody @Valid SearchImageByVideoDiagnoseIdForm form) {
        ArrayList<String> list = videoDiagnoseService.searchImageByVideoDiagnoseId(form.getVideoDiagnoseId());
        return R.ok().put("result", list);
    }

    @GetMapping("/searchMyStatistics")
    @SaCheckLogin
    @SaCheckPermission(value = {"VIDEO_DIAGNOSE:DIAGNOSE"}, mode = SaMode.OR)
    public R searchVideoDiagnoseMyStatistics() {
        int userId = StpUtil.getLoginIdAsInt();
        HashMap map = videoDiagnoseService.searchMyStatistics(userId);
        return R.ok().put("result", map);
    }
}