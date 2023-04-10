package com.example.hospital.patient.wx.api.service;

import com.fasterxml.jackson.databind.node.ObjectNode;

public interface PaymentService {
    public ObjectNode unifiedOrder(String outTradeNo, String openId, int total,
                                   String desc, String notifyUrl, String timeExpire);

    public String searchPaymentResult(String outTradeNo);
}
