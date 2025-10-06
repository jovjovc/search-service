package com.jov.search_service.service;

import com.jov.search_service.model.CarListing;
import com.jov.search_service.repository.CarListingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarListingServiceTest {

    @Mock
    private CarListingRepository carListingRepository;

    @InjectMocks
    private CarListingService carListingService;

    @Test
    void testCreateCarListing() {
        CarListing carListing = new CarListing();
        carListing.setId("1");
        carListing.setMake("Toyota");

        when(carListingRepository.save(carListing)).thenReturn(carListing);

        carListingService.createCarListing(carListing);

        verify(carListingRepository).save(carListing);
    }

    @Test
    void testUpdateCarListing() {
        String id = "1";
        CarListing existingCar = new CarListing();
        existingCar.setId(id);
        existingCar.setMake("Honda");
        existingCar.setModel("Civic");

        CarListing update = new CarListing();
        update.setMake("Toyota");
        update.setModel("Corolla");
        update.setPrice(BigDecimal.valueOf(20000));
        update.setMileage(15000);
        update.setFuelType("Petrol");
        update.setYear(2020);
        update.setDescription("Updated car");

        when(carListingRepository.findById(id)).thenReturn(Optional.of(existingCar));
        when(carListingRepository.save(any(CarListing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        carListingService.updateCarListing(id, update);

        ArgumentCaptor<CarListing> captor = ArgumentCaptor.forClass(CarListing.class);
        verify(carListingRepository).save(captor.capture());

        CarListing saved = captor.getValue();
        assertEquals("Toyota", saved.getMake());
        assertEquals("Corolla", saved.getModel());
        assertEquals(BigDecimal.valueOf(20000), saved.getPrice());
        assertEquals(15000, saved.getMileage());
        assertEquals("Petrol", saved.getFuelType());
        assertEquals(2020, saved.getYear());
        assertEquals("Updated car", saved.getDescription());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void testUpdateCarListing_shouldThrowWhenCarNotFound() {
        String id = "1";
        CarListing update = new CarListing();

        when(carListingRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> carListingService.updateCarListing(id, update));

        assertEquals("Car listing with ID 1 not found", ex.getMessage());
    }

    @Test
    void testDeleteCarListing() {
        String id = "1";

        when(carListingRepository.existsById(id)).thenReturn(true);
        doNothing().when(carListingRepository).deleteById(id);

        carListingService.deleteCarListing(id);

        verify(carListingRepository).deleteById(id);
    }

    @Test
    void testDeleteCarListing_shouldThrowWhenCarNotFound() {
        String id = "1";

        when(carListingRepository.existsById(id)).thenReturn(false);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> carListingService.deleteCarListing(id));

        assertEquals("Car listing with ID 1 not found", ex.getMessage());
    }
}