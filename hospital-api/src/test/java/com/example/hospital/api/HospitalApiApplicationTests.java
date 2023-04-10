package com.example.hospital.api;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HospitalApiApplicationTests {

    @Resource
    private AmqpTemplate amqpTemplate;

    @Test
    void contextLoads() {
        ArrayList rp = new ArrayList<>() {{
            add(new HashMap<>() {{
                put("name", "甲硝唑片");
                put("spec", "200mg×24片");
                put("method", "1片/次；每日三次；口服");
                put("num",1);
            }});
            add(new HashMap<>() {{
                put("name", "头孢拉定胶囊");
                put("spec", "250mg×24片");
                put("method", "1片/次；每日两次；口服");
                put("num",1);
            }});
        }};
        HashMap map = new HashMap() {{
            put("uuid", IdUtil.simpleUUID().toUpperCase());
            //自己查询数据库中就诊卡ID
            put("patientCardId", 1);
            put("diagnosis", "急性牙髓炎");
            put("subDeptId", 2);
            put("doctorId", 18);
            //自己查询数据库中挂号单ID
            put("registrationId", 1);
            put("rp", rp);
        }};
        amqpTemplate.convertAndSend("prescription", JSONUtil.parseObj(map).toString());
    }

}

