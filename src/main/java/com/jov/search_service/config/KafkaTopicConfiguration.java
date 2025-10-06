package com.jov.search_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.internals.Topic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfiguration {

    @Bean
    public NewTopic carListingTopic() {
        return TopicBuilder
                .name("car-listings")
                .partitions(2)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic carListingsDlt() {
        return TopicBuilder
                .name("car-listings.DLT")
                .partitions(2)
                .replicas(1)
                .build();
    }

}
