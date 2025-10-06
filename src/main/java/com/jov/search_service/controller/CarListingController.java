package com.jov.search_service.controller;

import com.jov.search_service.dto.CarListingDto;
import com.jov.search_service.dto.CarListingSearchRequest;
import com.jov.search_service.model.CarListing;
import com.jov.search_service.service.CarListingSearchService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * REST controller for managing car listings.
 * Provides endpoints to retrieve, search, and filter car listings using pagination,
 * full-text search, and specific criteria such as make.
 */
@RestController
@RequestMapping("/api/car-listings")
public class CarListingController {

    Logger logger = LoggerFactory.getLogger(CarListingController.class);

    private CarListingSearchService carListingSearchService;

    public CarListingController(CarListingSearchService carListingSearchService) {
        this.carListingSearchService = carListingSearchService;
    }

    /**
     * Get all car listings with pagination
     */
    @GetMapping
    public ResponseEntity<Page<CarListingDto>> getAllCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        logger.info("Getting all cars - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<CarListingDto> result = carListingSearchService.searchAllCars(pageable);
        return ResponseEntity.ok(result);
    }

    /**
     * Get all car listings with request
     */
    @PostMapping("/search")
    public ResponseEntity<List<CarListing>> searchCarsByRequest(
            @RequestBody @Valid CarListingSearchRequest request) {

     //   Pageable pageable = PageRequest.of(page, size);
        List<CarListing> result = carListingSearchService.searchCarsByRequest(request);
        return ResponseEntity.ok(result);
    }


    /**
     * Full-text search across make, model, and color
     */
    @GetMapping("/search")
    public ResponseEntity<Page<CarListingDto>> searchCars(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        logger.info("Full-text search with query: {}", q);
        Pageable pageable = PageRequest.of(page, size);
        Page<CarListingDto> cars = carListingSearchService.fullTextSearchCars(q, pageable);
        return ResponseEntity.ok(cars);
    }


    /**
     * Get car listings by make
     */
    @GetMapping("/search/make/{make}")
    public ResponseEntity<List<CarListingDto>> searchByMake(@PathVariable String make) {
        List<CarListingDto> result = carListingSearchService.seachByMake(make);
        return ResponseEntity.ok(result);
    }
}
