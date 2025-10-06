package com.jov.search_service.repository;

import com.jov.search_service.model.CarListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarListingRepository extends ElasticsearchRepository<CarListing, String> {
    /**
     * Find cars by brand
     */
    List<CarListing> findByMake(String make);

    /**
     * Search cars by make or model using full-text search
     */
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"make\", \"model\", \"color\"]}}")
    Page<CarListing> searchCars(String searchTerm, Pageable pageable);
}
