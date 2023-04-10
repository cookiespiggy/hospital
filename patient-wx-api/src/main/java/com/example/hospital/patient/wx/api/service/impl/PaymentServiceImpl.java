package com.example.hospital.patient.wx.api.service.impl;
import cn.felord.payment.PayException;
import cn.felord.payment.wechat.v3.WechatApiProvider;
import cn.felord.payment.wechat.v3.WechatResponseEntity;
import cn.felord.payment.wechat.v3.model.Amount;
import cn.felord.payment.wechat.v3.model.PayParams;
import cn.felord.payment.wechat.v3.model.Payer;
import cn.felord.payment.wechat.v3.model.TransactionQueryParams;
import com.example.hospital.patient.wx.api.service.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Resource
    private WechatApiProvider wechatApiProvider;

    @Override
    public ObjectNode unifiedOrder(String outTradeNo, String openId, int total,
                                   String desc, String notifyUrl, String timeExpire) {
        PayParams params = new PayParams();
        //设置付款人
        Payer payer = new Payer();
        payer.setOpenid(openId);
        params.setPayer(payer);
        //设置金额（货币单位是分）
        Amount amount = new Amount();
        //amount.setTotal(total);
        amount.setTotal(1);
        params.setAmount(amount);
        params.setOutTradeNo(outTradeNo);
        params.setDescription(desc);
        params.setNotifyUrl(notifyUrl);
        if (timeExpire != null) {
            params.setTimeExpire(timeExpire);
        }

        //创建支付订单
        WechatResponseEntity<ObjectNode> response = wechatApiProvider.directPayApi("patient-wx-api").jsPay(params);
        if (response.is2xxSuccessful()) {
            ObjectNode json = response.getBody();
            return json;
        } else {
            throw new PayException("创建微信支付订单失败");
        }
    }

    @Override
    public String searchPaymentResult(String outTradeNo) {
        TransactionQueryParams params = new TransactionQueryParams();
        params.setTransactionIdOrOutTradeNo(outTradeNo);
        WechatResponseEntity<ObjectNode> entity = wechatApiProvider.directPayApi("patient-wx-api")
                .queryTransactionByOutTradeNo(params);
        ObjectNode body = entity.getBody();
        JsonNode tradeState = body.get("trade_state");
        if (tradeState.asText().equals("SUCCESS")) {
            //如果患者已支付挂号费，我们取出支付单transaction_id，这个需要保存到挂号表
            String transactionId = body.get("transaction_id").asText();
            return transactionId;
        }
        return null;
    }
}