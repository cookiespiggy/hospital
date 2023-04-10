package com.example.hospital.patient.wx.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.felord.payment.wechat.v3.WechatApiProvider;
import cn.felord.payment.wechat.v3.model.ResponseSignVerifyParams;
import cn.hutool.core.bean.BeanUtil;
import com.example.hospital.patient.wx.api.common.PageUtils;
import com.example.hospital.patient.wx.api.common.R;
import com.example.hospital.patient.wx.api.controller.form.*;
import com.example.hospital.patient.wx.api.service.RegistrationService;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/registration")
public class RegistrationController {
    @Resource
    private RegistrationService registrationService;

    @Resource
    private WechatApiProvider wechatApiProvider;

    @PostMapping("/searchCanRegisterInDateRange")
    @SaCheckLogin
    public R searchCanRegisterInDateRange(@RequestBody @Valid SearchCanRegisterInDateRangeForm form) {
        Map<String, Object> param = BeanUtil.beanToMap(form);
        ArrayList<String> list = registrationService.searchCanRegisterInDateRange(param);
        return R.ok().put("result", list);
    }

    @PostMapping("/searchDeptSubDoctorPlanInDay")
    @SaCheckLogin
    public R searchDeptSubDoctorPlanInDay(@RequestBody @Valid SearchDeptSubDoctorPlanInDayForm form) {
        Map param = BeanUtil.beanToMap(form);
        ArrayList<HashMap> list = registrationService.searchDeptSubDoctorPlanInDay(param);
        return R.ok().put("result", list);
    }


    @PostMapping("/checkRegisterCondition")
    @SaCheckLogin
    public R checkRegisterCondition(@RequestBody @Valid CheckRegisterConditionForm form) {
        int userId = StpUtil.getLoginIdAsInt();
        form.setUserId(userId);
        Map param = BeanUtil.beanToMap(form);
        String result = registrationService.checkRegisterCondition(param);
        return R.ok().put("result", result);
    }

    @PostMapping("/searchDoctorWorkPlanSchedule")
    @SaCheckLogin
    public R searchDoctorWorkPlanSchedule(@RequestBody @Valid SearchDoctorWorkPlanScheduleForm form) {
        Map param = BeanUtil.beanToMap(form);
        ArrayList<HashMap> list = registrationService.searchDoctorWorkPlanSchedule(param);
        return R.ok().put("result", list);
    }

    @PostMapping("/registerMedicalAppointment")
    @SaCheckLogin
    public R registerMedicalAppointment(@RequestBody @Valid RegisterMedicalAppointmentForm form) {
        int userId = StpUtil.getLoginIdAsInt();
        Map param = BeanUtil.beanToMap(form);
        param.put("userId", userId);
        HashMap result = registrationService.registerMedicalAppointment(param);
        if (result == null) {
            return R.ok();
        }
        return R.ok(result);
    }
    @SneakyThrows
    @PostMapping("/transactionCallback")
    public Map transactionCallback(
            @RequestHeader("Wechatpay-Serial") String serial,
            @RequestHeader("Wechatpay-Signature") String signature,
            @RequestHeader("Wechatpay-Timestamp") String timestamp,
            @RequestHeader("Wechatpay-Nonce") String nonce,
            HttpServletRequest request) {

        String body = request.getReader().lines().collect(Collectors.joining());
        //创建封装对象，添加相关参数，验证请求真伪需要用到
        ResponseSignVerifyParams params = new ResponseSignVerifyParams();
        params.setWechatpaySerial(serial);
        params.setWechatpaySignature(signature);
        params.setWechatpayTimestamp(timestamp);
        params.setWechatpayNonce(nonce);
        params.setBody(body);

        //验证通知消息的真伪
        return wechatApiProvider.callback("patient-wx-api").transactionCallback(params, data -> {
            String outTradeNo = data.getOutTradeNo();
            String transactionId = data.getTransactionId();

            //更新挂号记录的付款状态和付款单ID
            registrationService.updatePayment(new HashMap() {{
                put("outTradeNo", outTradeNo);
                put("transactionId", transactionId);
                put("paymentStatus", 2);
            }});

        });
    }

    @PostMapping("/searchPaymentResult")
    @SaCheckLogin
    public R searchPaymentResult(@RequestBody @Valid SearchPaymentResultForm form) {
        boolean bool = registrationService.searchPaymentResult(form.getOutTradeNo());
        return R.ok().put("result", bool);
    }


    @PostMapping("/searchRegistrationByPage")
    @SaCheckLogin
    public R searchRegistrationByPage(@RequestBody @Valid SearchRegistrationByPageForm form) {
        int userId = StpUtil.getLoginIdAsInt();
        form.setUserId(userId);
        Map param = BeanUtil.beanToMap(form);
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        param.put("start", start);
        PageUtils pageUtils = registrationService.searchRegistrationByPage(param);
        return R.ok().put("result", pageUtils);
    }

    @PostMapping("/repayRegistration")
    @SaCheckLogin
    public R repayRegistration(@RequestBody @Valid RepayRegistrationForm form) {
        int userId = StpUtil.getLoginIdAsInt();
        form.setUserId(userId);
        Map param = BeanUtil.beanToMap(form);
        HashMap result = registrationService.repayRegistration(param);
        return R.ok(result);
    }

    @PostMapping("/searchRegistrationInfo")
    @SaCheckLogin
    public R searchRegistrationInfo(@RequestBody @Valid SearchRegistrationInfoForm form) {
        int userId = StpUtil.getLoginIdAsInt();
        form.setUserId(userId);
        Map param = BeanUtil.beanToMap(form);
        HashMap map = registrationService.searchRegistrationInfo(param);
        return R.ok(map);
    }
}