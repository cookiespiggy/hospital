package com.example.hospital.patient.wx.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.felord.payment.wechat.v3.WechatApiProvider;
import cn.felord.payment.wechat.v3.model.ResponseSignVerifyParams;
import cn.hutool.core.bean.BeanUtil;
import com.example.hospital.patient.wx.api.common.PageUtils;
import com.example.hospital.patient.wx.api.common.R;
import com.example.hospital.patient.wx.api.config.tencent.TrtcUtil;
import com.example.hospital.patient.wx.api.controller.form.*;
import com.example.hospital.patient.wx.api.db.pojo.VideoDiagnoseEntity;
import com.example.hospital.patient.wx.api.service.VideoDiagnoseService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/video_diagnose")
public class VideoDiagnoseController {

    @Resource
    private  VideoDiagnoseService videoDiagnoseService;

    @Resource
    private WechatApiProvider wechatApiProvider;

    @Value("${tencent.trtc.appId}")
    private String appId;

    @Resource
    private TrtcUtil trtcUtil;

    @PostMapping("searchOnlineDoctorList")
    @SaCheckLogin
    public R searchOnlineDoctorList(@RequestBody @Valid SearchOnlineDoctorListForm form) {
        ArrayList<HashMap> list = videoDiagnoseService.searchOnlineDoctorList(form.getSubName(), form.getJob());
        return R.ok().put("result", list);
    }

    @PostMapping("createVideoDiagnose")
    @SaCheckLogin
    public R createVideoDiagnose(@RequestBody @Valid CreateVideoDiagnoseForm form) {
        int userId = StpUtil.getLoginIdAsInt();
        VideoDiagnoseEntity entity = BeanUtil.toBean(form, VideoDiagnoseEntity.class);
        HashMap result = videoDiagnoseService.createVideoDiagnose(userId, entity);
        return R.ok().put("result", result);
    }

    @SneakyThrows
    @PostMapping("/transactionCallback")
    public Map transactionCallback(@RequestHeader("Wechatpay-Serial") String serial, @RequestHeader("Wechatpay-Signature") String signature, @RequestHeader("Wechatpay-Timestamp") String timestamp, @RequestHeader("Wechatpay-Nonce") String nonce, HttpServletRequest request) {
        String body = request.getReader().lines().collect(Collectors.joining());

        ResponseSignVerifyParams params = new ResponseSignVerifyParams();
        params.setWechatpaySerial(serial);
        params.setWechatpaySignature(signature);
        params.setWechatpayTimestamp(timestamp);
        params.setWechatpayNonce(nonce);
        params.setBody(body);

        // 对请求头进行检验数字签名 以确保是微信服务器发送的消息
        return wechatApiProvider.callback("patient-wx-api").transactionCallback(params, data -> {
            String outTradeNo = data.getOutTradeNo();
            String transactionId = data.getTransactionId();

            //更新挂号记录的付款状态和付款单ID
            videoDiagnoseService.updatePayment(new HashMap() {{
                put("outTradeNo", outTradeNo);
                put("transactionId", transactionId);
                put("paymentStatus", 2);
            }});
        });
    }

    @PostMapping("/searchPaymentResult")
    @SaCheckLogin
    public R searchPaymentResult(@RequestBody @Valid SearchPaymentResultForm form) {
        boolean bool = videoDiagnoseService.searchPaymentResult(form.getOutTradeNo());
        return R.ok().put("result", bool);
    }

    @PostMapping("/searchImageByVideoDiagnoseId")
    @SaCheckLogin
    public R searchImageByVideoDiagnoseId(@RequestBody @Valid SearchImageByVideoDiagnoseIdForm form) {
        ArrayList<HashMap> list = videoDiagnoseService.searchImageByVideoDiagnoseId(form.getVideoDiagnoseId());
        return R.ok().put("result", list);
    }

    @PostMapping("/uploadImage")
    @SaCheckLogin
    public R uploadImage(@Param("file") MultipartFile file, @Param("orderId") Integer videoDiagnoseId) {
        String filename = videoDiagnoseService.uploadImage(file, videoDiagnoseId);
        return R.ok().put("filename", filename);
    }

    @PostMapping("/deleteImage")
    @SaCheckLogin
    public R deleteImage(@RequestBody @Valid DeleteVideoDiagnoseImageForm form) {
        Map param = BeanUtil.beanToMap(form);
        videoDiagnoseService.deleteImage(param);
        return R.ok();
    }
    @GetMapping("/searchUserSig")
    @SaCheckLogin
    public R searchUserSig() {
        int userId = StpUtil.getLoginIdAsInt();
        String userSig = trtcUtil.genUserSig(userId + "");
        return R.ok().put("userSig", userSig).put("userId", userId).put("appId", appId);
    }

    @PostMapping("/searchRoomId")
    @SaCheckLogin
    public R searchRoomId(@RequestBody @Valid SearchRoomIdForm form) {
        String roomId = videoDiagnoseService.searchRoomId(form.getDoctorId());
        return R.ok().put("result", roomId);
    }

    @PostMapping("/searchByPage")
    @SaCheckLogin
    public R searchByPage(@RequestBody @Valid SearchVideoDiagnoseByPageForm form) {
        int userId = StpUtil.getLoginIdAsInt();
        form.setUserId(userId);
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        Map param = BeanUtil.beanToMap(form);
        param.put("start", start);
        PageUtils pageUtils = videoDiagnoseService.searchByPage(param);
        return R.ok().put("result", pageUtils);
    }
}