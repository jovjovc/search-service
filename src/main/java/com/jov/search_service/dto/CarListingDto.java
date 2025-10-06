package com.jov.search_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Data
public class CarListingDto {

    private String make;

    private String model;

    private String color;

    private Integer year;

    private BigDecimal price;

    private String description;

    private String fuelType;

    private Integer mileage;

    public CarListingDto(String make, String model) {
        this.make = make;
        this.model = model;
    }
}
