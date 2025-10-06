package com.jov.search_service.kafka.consumer;

import com.jov.search_service.events.CarListingEvent;
import com.jov.search_service.service.CarListingService;
import com.jov.search_service.translator.CarListingTranslator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Validated
@KafkaListener(topics = "car-listings", groupId = "car-listing-consumer-group")
public class CarListingConsumer {

    private final CarListingService carListingService;

    private final CarListingTranslator carListingTranslator;

    private static final Logger logger = LoggerFactory.getLogger(CarListingConsumer.class);

    public CarListingConsumer(CarListingService carListingService,
                              CarListingTranslator carListingTranslator) {
        this.carListingService = carListingService;
        this.carListingTranslator = carListingTranslator;
    }

    @KafkaHandler
    public void handle(@Payload @Valid CarListingEvent event) {

        switch (event.getAction()) {
            case "CREATED" -> carListingService.createCarListing(carListingTranslator.translateToModel(event));
            case "UPDATED" ->
                    carListingService.updateCarListing(event.getId(), carListingTranslator.translateToModel(event));
            case "DELETED" -> carListingService.deleteCarListing(event.getId());
            default -> logger.warn("Unknown action: " + event.getId());
        }

    }

    @KafkaHandler(isDefault = true)
    public void handleDefault(Object object) {
        logger.info("Received unknown message type: " + object);
    }
}
