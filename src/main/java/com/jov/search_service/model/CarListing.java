package com.jov.search_service.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(indexName = "car_listings")
public class CarListing {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String make;

    @Field(type = FieldType.Text)
    private String model;

    @NotBlank(message = "Color is required")
    @Field(type = FieldType.Keyword)
    private String color;

    @Field(type = FieldType.Integer)
    private Integer year;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private String fuelType;

    @Field(type = FieldType.Integer)
    private Integer mileage;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime updatedAt;

    // Default constructor
    public CarListing() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor with required fields
    public CarListing(String id, String make, String model) {
        this();
        this.id = id;
        this.make = make;
        this.model = model;
    }
}
