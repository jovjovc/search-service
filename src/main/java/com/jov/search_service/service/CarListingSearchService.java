package com.jov.search_service.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import com.jov.search_service.dto.CarListingDto;
import com.jov.search_service.dto.CarListingSearchRequest;
import com.jov.search_service.model.CarListing;
import com.jov.search_service.repository.CarListingRepository;
import com.jov.search_service.translator.CarListingTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service responsible for querying CarListing entities from Elasticsearch.
 *
 * <p>
 * This service is used by REST controllers to perform searches, full-text queries,
 * and filters on car listings.
 * </p>
 *
 * <p>
 * It focuses  on read operations and is separate from the create/update/delete
 * operations
 * </p>
 */
@Service
public class CarListingSearchService {

    private static final Logger logger= LoggerFactory.getLogger(CarListingSearchService.class);

    private CarListingRepository carListingRepository;

    private final ElasticsearchOperations elasticsearchOperations;


    public CarListingSearchService(CarListingRepository carListingRepository,
                                   ElasticsearchOperations elasticsearchOperations) {
        this.carListingRepository = carListingRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public List<CarListing> searchCarsByRequest(CarListingSearchRequest request) {
                                                                 // Pageable pageable) {
        List<Query> queries = new ArrayList<>();
        addMatchQueryIfNotNull(queries, "make", request.getMake());
        addMatchQueryIfNotNull(queries, "model", request.getModel());
        addTermQueryIfNotNull(queries, "color.keyword", request.getColor());
        addTermQueryIfNotNull(queries, "year.keyword", request.getYear());

        BoolQuery boolQuery = new BoolQuery.Builder()
                .must(queries)
                .build();

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(boolQuery._toQuery())
                .build();

        List<CarListing> cars = elasticsearchOperations.search(nativeQuery, CarListing.class)
                .stream()
                .map(SearchHit::getContent)
                .toList();
        return cars;
    }


    public Page<CarListingDto> searchAllCars(Pageable pageable) {
        Page<CarListing> carListingPage = carListingRepository.findAll(pageable);
        return carListingPage.map(CarListingTranslator:: translateToDto);
    }

    public List<CarListingDto> seachByMake(String make) {
        List<CarListing> carListings = carListingRepository.findByMake(make);

        if (carListings.isEmpty()) {
            logger.info("No car listings of make: {} found", make);
            return Collections.emptyList();
        }

        return carListings.stream()
                .map(CarListingTranslator::translateToDto)
                .toList();
    }

    public Page<CarListingDto> fullTextSearchCars(String searchTerm, Pageable pageable) {
        logger.info("Searching cars with term: {}", searchTerm);

        Page<CarListing> carListingPage = carListingRepository.searchCars(searchTerm, pageable);
        return carListingPage.map(CarListingTranslator :: translateToDto);
    }


    private void addMatchQueryIfNotNull(List<Query> queries, String field, String value) {
        if (value != null && !value.isBlank()) {
            queries.add(new MatchQuery.Builder()
                    .field(field)
                    .query(value)
                    .build()
                    ._toQuery());
        }
    }

    private void addTermQueryIfNotNull(List<Query> queries, String field, Object value) {
        if (value != null) {
            TermQuery.Builder builder = new TermQuery.Builder().field(field);

            if (value instanceof String s) {
                builder.value(s);
            } else if (value instanceof Integer i) {
                builder.value(i.longValue());
            } else if (value instanceof Long l) {
                builder.value(l);
            } else {
                throw new IllegalArgumentException("Unsupported type for term query: " + value.getClass());
            }

            queries.add(builder.build()._toQuery());
        }
    }
}
