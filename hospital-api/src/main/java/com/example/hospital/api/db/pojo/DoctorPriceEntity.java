package com.example.hospital.api.db.pojo;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DoctorPriceEntity {
    private Integer id;
    private Integer doctorId;
    private String level;
    private BigDecimal price_1;
    private BigDecimal price_2;

}