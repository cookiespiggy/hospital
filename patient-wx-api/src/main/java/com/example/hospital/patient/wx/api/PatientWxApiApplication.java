package com.example.hospital.patient.wx.api;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@ServletComponentScan
@ComponentScan("com.example.*")
@MapperScan("com.example.hospital.patient.wx.api.db.dao")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class PatientWxApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PatientWxApiApplication.class, args);
    }

}
