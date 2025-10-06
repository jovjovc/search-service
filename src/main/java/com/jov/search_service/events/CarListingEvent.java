package com.jov.search_service.events;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarListingEvent {

    @NotBlank(message = "Id must not be blank ")
    private String id;

    //could use enum instead
    @NotBlank(message = "Action must not be blank")
    @Pattern(regexp = "CREATED|UPDATED|DELETED", message = "Action must be one of CREATED, UPDATED, or DELETED")
    private String action;

    @NotBlank(message = "Make must not be blank")
    private String make;

    @NotBlank(message = "Model must not be blank")
    private String model;

    @Positive
    private int year;

    private String color;

    @PositiveOrZero(message = "Mileage must be zero or positive")
    private int mileage;

    @Pattern(regexp = "PETROL|DIESEL|ELECTRIC|HYBRID|CNG",
            message = "Fuel type must be one of PETROL, DIESEL, ELECTRIC, HYBRID, CNG")
    private String fuelType;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    private String sellerContactEmail;
    private String sellerContactName;
    private String sellerContactPhone;

    public CarListingEvent(String id, String action, String make, String model) {
        this.id = id;
        this.action = action;
        this.make = make;
        this.model = model;
    }
}
