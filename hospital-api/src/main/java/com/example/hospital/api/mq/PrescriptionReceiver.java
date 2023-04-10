package com.example.hospital.api.mq;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.hospital.api.db.dao.DoctorPrescriptionDao;
import com.example.hospital.api.db.pojo.DoctorPrescriptionEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Component
@RabbitListener(queues = "prescription")
@Slf4j
public class PrescriptionReceiver {

    @Resource
    private DoctorPrescriptionDao doctorPrescriptionDao;

    @RabbitHandler
    @Transactional
    public void receive(String msg) {
        //把消息转换成JSON对象
        JSONObject json = JSONUtil.parseObj(msg);

        String uuid = json.getStr("uuid");
        Integer patientCardId = json.getInt("patientCardId");
        String diagnosis = json.getStr("diagnosis");
        Integer subDeptId = json.getInt("subDeptId");
        Integer doctorId = json.getInt("doctorId");
        Integer registrationId = json.getInt("registrationId");
        String rp = json.getStr("rp");

        DoctorPrescriptionEntity entity = new DoctorPrescriptionEntity();
        entity.setUuid(uuid);
        entity.setPatientCardId(patientCardId);
        entity.setDiagnosis(diagnosis);
        entity.setSubDeptId(subDeptId);
        entity.setDoctorId(doctorId);
        entity.setRegistrationId(registrationId);
        entity.setRp(rp);
        //把处方保存到数据库
        doctorPrescriptionDao.insert(entity);
        log.debug(msg);
        log.debug("成功保存医生处方");
    }
}
