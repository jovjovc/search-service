package com.jov.search_service.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CarListingSearchRequest {

    private String make;

    private String model;

    @Size(max = 50, message = "Color must not exceed 30 characters")
    private String color;

    @Positive
    private Integer year;
}
