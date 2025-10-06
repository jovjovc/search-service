package com.jov.search_service.controller;

import com.jov.search_service.events.CarListingEvent;
import com.jov.search_service.kafka.producer.TestCarListingProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Test controller for producing car listing events to Kafka.
 * This controller is intended for **testing purposes only**. It allows you to trigger
 * Kafka message production via REST endpoints instead of manually publishing messages
 * to the Kafka topic.
 */
@RestController
@RequestMapping("/api/car-listings")
public class TestController {

    private final TestCarListingProducer carListingProducer;

    public TestController(TestCarListingProducer carListingProducer) {
        this.carListingProducer = carListingProducer;
    }

    @PostMapping
    public ResponseEntity<CarListingEvent> sendKafkaMessage( @RequestBody CarListingEvent request) {
        carListingProducer.produceCarListing(request);
        return ResponseEntity.ok(request);
    }
}
