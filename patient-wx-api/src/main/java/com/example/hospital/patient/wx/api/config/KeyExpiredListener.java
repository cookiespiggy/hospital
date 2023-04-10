package com.example.hospital.patient.wx.api.config;

import cn.hutool.core.map.MapUtil;
import com.example.hospital.patient.wx.api.db.dao.DoctorWorkPlanDao;
import com.example.hospital.patient.wx.api.db.dao.DoctorWorkPlanScheduleDao;
import com.example.hospital.patient.wx.api.db.dao.MedicalRegistrationDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;

@Slf4j
@Component
public class KeyExpiredListener extends KeyExpirationEventMessageListener {
    @Resource
    private MedicalRegistrationDao medicalRegistrationDao;

    @Resource
    private DoctorWorkPlanDao doctorWorkPlanDao;

    @Resource
    private DoctorWorkPlanScheduleDao doctorWorkPlanScheduleDao;

    @Resource
    private RedisTemplate redisTemplate;

    public KeyExpiredListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    @Transactional
    public void onMessage(Message message, byte[] pattern) {
        //获取过期数据的Key
        String key = message.toString();
        //判断过期的数据是否为挂号单
        if (key.startsWith("registration_payment_")) {
            //从Key中提取挂号单流水号
            String outTradeNo = key.split("_")[2];
            //更新挂号支付订单状态为4
            medicalRegistrationDao.discardPayment(outTradeNo);
            //出诊计划已挂号人数减去1
            doctorWorkPlanDao.releaseNumByOutTradeNo(outTradeNo);
            //出诊时段已挂号人数减去1
            doctorWorkPlanScheduleDao.releaseNumByOutTradeNo(outTradeNo);

            //查询挂号单的workPlanId和scheduleId
            HashMap map = medicalRegistrationDao.searchWorkPlanIdAndScheduleId(outTradeNo);
            int scheduleId = MapUtil.getInt(map, "doctorScheduleId");
            int workPlanId = MapUtil.getInt(map, "workPlanId");

            key = "doctor_schedule_" + scheduleId;
            if (redisTemplate.hasKey(key)) {
                //更新缓存中已挂号人数的数量减去1
                redisTemplate.opsForHash().increment(key, "num", -1);
            }

        }
        super.onMessage(message, pattern);
    }
}


