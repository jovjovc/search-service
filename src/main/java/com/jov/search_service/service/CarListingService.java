package com.jov.search_service.service;

import com.jov.search_service.model.CarListing;
import com.jov.search_service.repository.CarListingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 * Service responsible for handling create, update, and delete operations
 * for CarListing entities in the database and Elasticsearch.
 *
 * <p>
 * This service is primarily used by the Kafka consumer to process incoming
 * CarListingEvent messages. It ensures that all changes are persisted
 * to the database and reflected in the Elasticsearch index.
 * </p>
 *
 * <p>
 * All non-recoverable errors, such as missing records during updates or deletes,
 * are thrown as exceptions to integrate with Kafka's DLT (Dead Letter Topic) mechanism.
 * </p>
 */

@Service
public class CarListingService {

    private static final Logger logger= LoggerFactory.getLogger(CarListingService.class);

    private CarListingRepository carListingRepository;


    public CarListingService(CarListingRepository carListingRepository) {
        this.carListingRepository = carListingRepository;
    }

    public void  createCarListing(CarListing carListing) {
        logger.info("Creating car listing: {}", carListing);

        if (carListingRepository.existsById(carListing.getId())) {
            logger.warn("Car listing with ID {} already exists. Ignoring CREATE event.", carListing.getId());
            return;
        }

        CarListing savedCarListing = carListingRepository.save(carListing);
        logger.info("Car listing saved successfully with ID: {}", savedCarListing.getId());
    }

    public void updateCarListing(String id, CarListing carListing) {
        logger.info("Updating car listing with ID: {}", id);

        CarListing existingCarListing = carListingRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Car listing with ID {} not found", id);
                    return new ResourceNotFoundException("Car listing with ID " + id + " not found");
                });

        // Update only fields from the input object
        existingCarListing.setPrice(carListing.getPrice());
        existingCarListing.setMake(carListing.getMake());
        existingCarListing.setModel(carListing.getModel());
        existingCarListing.setYear(carListing.getYear());
        existingCarListing.setMileage(carListing.getMileage());
        existingCarListing.setFuelType(carListing.getFuelType());
        existingCarListing.setDescription(carListing.getDescription());
        existingCarListing.setUpdatedAt(LocalDateTime.now());

        CarListing updatedCarListing = carListingRepository.save(existingCarListing);
        logger.info("Car listing updated successfully with ID: {}", updatedCarListing.getId());
    }

    public void deleteCarListing(String id) {
        logger.info("Deleting  car listing with id"+ id);

        if (!carListingRepository.existsById(id)) {
            logger.error("Car listing with ID {} not found", id);
            throw new ResourceNotFoundException("Car listing with ID " + id + " not found");
        }

        carListingRepository.deleteById(id);
        logger.info("Car listing deleted successfully with ID: {}", id);
    }
}
