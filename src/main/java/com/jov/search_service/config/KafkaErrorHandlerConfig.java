package com.jov.search_service.config;

import jakarta.validation.ConstraintViolationException;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlerConfig {

    public static final Logger logger = LoggerFactory.getLogger(KafkaErrorHandlerConfig.class);

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<Object, Object> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (event, ex) -> {
                    logger.error(
                            "Error processing record from topic {}: partition {}, offset {}, key {}. Exception: {}",
                            event.topic(), event.partition(), event.offset(), event.key(), ex.getMessage(), ex);
                    return new TopicPartition("car-listings.DLT", event.partition());
                });

        FixedBackOff backOff = new FixedBackOff(3000L, 5);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        errorHandler.setRetryListeners((record, ex, deliveryAttempt) -> {
            if (ex instanceof ConstraintViolationException) {
                logger.error("ConstraintViolationException for record key {}: {}", record.key(), ex.getMessage());
            }
        });

        // Validation errors should NOT be retried
       errorHandler.addNotRetryableExceptions(ConstraintViolationException.class);

        return errorHandler;
    }
}