package com.jov.search_service.service;

import com.jov.search_service.dto.CarListingDto;
import com.jov.search_service.dto.CarListingSearchRequest;
import com.jov.search_service.model.CarListing;
import com.jov.search_service.repository.CarListingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarListingSearchServiceTest {
    @Mock
    private CarListingRepository carListingRepository;

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @InjectMocks
    private CarListingSearchService carListingSearchService;

    @Test
    void testSearchAllCars() {
        CarListing toyota = new CarListing("1", "Toyota", "Corolla");
        CarListing honda = new CarListing("2", "Honda", "Civic");

        Page<CarListing> page = new PageImpl<>(List.of(toyota, honda));
        when(carListingRepository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<CarListingDto> result = carListingSearchService.searchAllCars(PageRequest.of(0, 10));

        assertEquals(2, result.getContent().size());
        assertEquals("Toyota", result.getContent().get(0).getMake());
        assertEquals("Honda", result.getContent().get(1).getMake());
    }

    @Test
    void testSeachByMake() {
        CarListing carListing = new CarListing("1", "Toyota", "Corolla");
        when(carListingRepository.findByMake("Toyota")).thenReturn(List.of(carListing));

        List<CarListingDto> result = carListingSearchService.seachByMake("Toyota");

        assertEquals(1, result.size());
        assertEquals("Toyota", result.get(0).getMake());
    }

    @Test
    void testSeachByMake_shouldReturnEmptyListIfNoCarsFound() {
        when(carListingRepository.findByMake("Random")).thenReturn(List.of());

        List<CarListingDto> result = carListingSearchService.seachByMake("Random");

        assertTrue(result.isEmpty());
    }

    @Test
    void fullTextSearchCars_shouldReturnMappedDtos() {
        CarListing carListing = new CarListing("1", "Toyota", "Corolla");
        Page<CarListing> page = new PageImpl<>(List.of(carListing));
        when(carListingRepository.searchCars(eq("Corolla"), any(PageRequest.class))).thenReturn(page);

        Page<CarListingDto> result = carListingSearchService.fullTextSearchCars("Corolla", PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        assertEquals("Toyota", result.getContent().get(0).getMake());
    }


    @Test
    void searchCarsByRequest_shouldBuildQueryAndReturnResults() {
        CarListing carListing = new CarListing("1", "Toyota", "Corolla");

        // Mock search hits from ElasticsearchOperations
        SearchHit<CarListing> hit = mock(SearchHit.class);
        when(hit.getContent()).thenReturn(carListing);

        SearchHits<CarListing> searchHits = mock(SearchHits.class);
        when(searchHits.stream()).thenReturn(List.of(hit).stream());

        when(elasticsearchOperations.search(any(org.springframework.data.elasticsearch.core.query.Query.class),
                eq(CarListing.class)))
                .thenReturn(searchHits);

        CarListingSearchRequest request = new CarListingSearchRequest();
        request.setMake("Toyota");
        request.setModel("Corolla");

        List<CarListing> result = carListingSearchService.searchCarsByRequest(request);

        assertEquals(1, result.size());
        assertEquals("Toyota", result.get(0).getMake());
        assertEquals("Corolla", result.get(0).getModel());
    }
}