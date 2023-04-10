package com.example.hospital.api.mq;


import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectConfig {
    @Bean
    public Queue prescriptionQueue(){
        //在RabbitMQ上创建名字叫做prescription的队列
        return new Queue("prescription");
    }
}
