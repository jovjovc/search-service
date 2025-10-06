package com.jov.search_service.kafka.producer;

import com.jov.search_service.events.CarListingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * A test Kafka producer for CarListingEvent messages.
 *
 * <p>
 * This component is used to produce messages to the "car-listings" Kafka topic,
 * for testing purposes. It allows developers or integration tests to send events
 * without manually producing them via external tools or Kafka consoles.
 * </p>
 */
@Component
public class TestCarListingProducer {

    private static final Logger logger = LoggerFactory.getLogger(TestCarListingProducer.class);

    public static final String CAR_LISTING_TOPIC = "car-listings";
    private final KafkaTemplate<String, CarListingEvent> kafkaTemplate;

    public TestCarListingProducer(KafkaTemplate<String, CarListingEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void produceCarListing(CarListingEvent event) {
        kafkaTemplate.send(CAR_LISTING_TOPIC, event.getId(), event);
        logger.info ("Produced CarListingEvent: " + event);
    }
}
