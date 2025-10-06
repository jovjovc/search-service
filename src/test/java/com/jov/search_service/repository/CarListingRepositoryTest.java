package com.jov.search_service.repository;

import com.jov.search_service.model.CarListing;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataElasticsearchTest
class CarListingRepositoryTest {

    @Container
    static ElasticsearchContainer elasticsearchContainer =
            new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.18.6")
                    .withEnv("discovery.type", "single-node")
                    .withEnv("xpack.security.enabled", "false")
                    .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", elasticsearchContainer::getHttpHostAddress);
    }

    @Autowired
    private CarListingRepository carListingRepository;


    @BeforeEach
    void setup() {
        carListingRepository.deleteAll();

        CarListing car1 = new CarListing("1", "Toyota", "Corolla");
        CarListing car2 = new CarListing("2", "Honda", "Civic");
        CarListing car3 = new CarListing("3", "Toyota", "Camry");

        carListingRepository.saveAll(List.of(car1, car2, car3));
    }

    @Test
    void testFindByMake() {
        List<CarListing> toyotas = carListingRepository.findByMake("Toyota");

        assertThat(toyotas).hasSize(2);
        assertThat(toyotas).extracting(CarListing::getModel)
                .containsExactlyInAnyOrder("Corolla", "Camry");
    }


    @Test
    void testFindByMake_shouldReturnEmptyListWhenNoCarsFound() {
        List<CarListing> fords = carListingRepository.findByMake("Ford");

        assertThat(fords).isNotNull();
        assertThat(fords).isEmpty();
    }


    @Test
    void testFullTextSearchCars() {
        Page<CarListing> page = carListingRepository.searchCars("Civic", PageRequest.of(0, 10));

        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(1);
        CarListing car = page.getContent().get(0);
        assertThat(car.getMake()).isEqualTo("Honda");
        assertThat(car.getModel()).isEqualTo("Civic");
    }


    @Test
    void testFullTextSearchCars_shouldReturnEmptyPageIfNoMatch() {
        Page<CarListing> page = carListingRepository.searchCars("Ford", PageRequest.of(0, 10));

        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(0);
        assertThat(page.getContent()).isEmpty();
    }


}