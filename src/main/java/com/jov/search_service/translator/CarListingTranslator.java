package com.jov.search_service.translator;

import com.jov.search_service.dto.CarListingDto;
import com.jov.search_service.events.CarListingEvent;
import com.jov.search_service.model.CarListing;
import org.springframework.stereotype.Component;

@Component
public class CarListingTranslator {

    public CarListing translateToModel(CarListingEvent event) {

        if (event == null) {
            return null;
        }

        CarListing carListing = new CarListing();
        carListing.setMake(event.getMake());
        carListing.setModel(event.getModel());
        carListing.setId(event.getId());
        carListing.setYear(event.getYear());
        carListing.setFuelType(event.getFuelType());
        carListing.setPrice(event.getPrice());
        carListing.setMileage(event.getMileage());
        carListing.setDescription(event.getDescription());
        carListing.setColor(event.getColor());
        return carListing;
    }


    public static CarListingDto translateToDto(CarListing carListing) {

        if (carListing == null) {
            return null;
        }

        CarListingDto dto = new CarListingDto();
        dto.setMake(carListing.getMake());
        dto.setModel(carListing.getModel());
        dto.setYear(carListing.getYear());
        dto.setColor(carListing.getColor());
        dto.setFuelType(carListing.getFuelType());
        dto.setPrice(carListing.getPrice());
        dto.setDescription(carListing.getDescription());
        dto.setMileage(carListing.getMileage());
        return dto;
    }
}

