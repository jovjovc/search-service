package com.jov.search_service.translator;


import com.jov.search_service.dto.CarListingDto;
import com.jov.search_service.events.CarListingEvent;
import com.jov.search_service.model.CarListing;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CarListingTranslatorTest {

    private CarListingTranslator translator = new CarListingTranslator();

    @Test
    void testTranslateToModel() {
        CarListingEvent event = new CarListingEvent();
        event.setId("1");
        event.setMake("Toyota");
        event.setModel("Corolla");
        event.setYear(2020);
        event.setColor("Blue");
        event.setFuelType("Petrol");
        event.setPrice(BigDecimal.valueOf(20000));
        event.setMileage(15000);
        event.setDescription("A reliable car");

        CarListing carListing = translator.translateToModel(event);

        assertNotNull(carListing);
        assertEquals(event.getId(), carListing.getId());
        assertEquals(event.getMake(), carListing.getMake());
        assertEquals(event.getModel(), carListing.getModel());
        assertEquals(event.getYear(), carListing.getYear());
        assertEquals(event.getColor(), carListing.getColor());
        assertEquals(event.getFuelType(), carListing.getFuelType());
        assertEquals(event.getPrice(), carListing.getPrice());
        assertEquals(event.getMileage(), carListing.getMileage());
        assertEquals(event.getDescription(), carListing.getDescription());
    }

    @Test
    void testTranslateToModel_shouldReturnNull() {
        CarListing carListing = translator.translateToModel(null);
        assertNull(carListing);
    }

    @Test
    void testTranslateToDto() {
        CarListing carListing = new CarListing();
        carListing.setId("1");
        carListing.setMake("Honda");
        carListing.setModel("Civic");
        carListing.setYear(2019);
        carListing.setColor("Red");
        carListing.setFuelType("Diesel");
        carListing.setPrice(BigDecimal.valueOf(18000));
        carListing.setMileage(20000);
        carListing.setDescription("Good condition");

        CarListingDto dto = CarListingTranslator.translateToDto(carListing);

        assertNotNull(dto);
        assertEquals(carListing.getMake(), dto.getMake());
        assertEquals(carListing.getModel(), dto.getModel());
        assertEquals(carListing.getYear(), dto.getYear());
        assertEquals(carListing.getColor(), dto.getColor());
        assertEquals(carListing.getFuelType(), dto.getFuelType());
        assertEquals(carListing.getPrice(), dto.getPrice());
        assertEquals(carListing.getMileage(), dto.getMileage());
        assertEquals(carListing.getDescription(), dto.getDescription());
    }

    @Test
    void testTranslateToDto_shouldReturnNull() {
        CarListingDto dto = CarListingTranslator.translateToDto(null);
        assertNull(dto);
    }
}