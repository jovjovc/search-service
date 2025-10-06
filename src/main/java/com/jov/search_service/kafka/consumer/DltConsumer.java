package com.jov.search_service.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * DeadLetterTopicConsumer is responsible for handling messages that could not be
 * successfully processed by the main Kafka consumers and were automatically routed
 * to the dead-letter topic.
 **/
@Component
public class DltConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DltConsumer.class);

    @KafkaListener(topics = "car-listings.DLT", groupId = "dlt-consumer-group")
    public void listen(ConsumerRecord<Object, Object> failedEvent) {
        logger.info("Received message from DLT:");
        logger.info("Topic: {}", failedEvent.topic());
        logger.info("Partition: {}", failedEvent.partition());
        logger.info("Offset: {}", failedEvent.offset());
        logger.info("Key: {}", failedEvent.key());
        logger.info("Value: {}", failedEvent.value());
    }
}
