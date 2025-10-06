package com.jov.search_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jov.search_service.events.CarListingEvent;
import com.jov.search_service.model.CarListing;
import com.jov.search_service.service.CarListingService;
import com.jov.search_service.translator.CarListingTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest(properties = {"spring.kafka.admin.auto-create=false"})
@EmbeddedKafka(partitions = 1, topics = {"car-listings"})
class CarListingConsumerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, CarListingEvent> kafkaTemplate;

    @Autowired
    private CarListingConsumer consumer;

    @MockitoBean()
    private CarListingService carListingService;

    @MockitoSpyBean
    private CarListingTranslator carListingTranslator;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @BeforeEach
    void setUp() {
        kafkaListenerEndpointRegistry.getListenerContainers().forEach(container ->
                ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic()));
    }

    @Test
    void testshouldConsumeCreatedEvent() throws Exception {
        CarListingEvent event = new CarListingEvent();
        event.setAction("CREATED");
        event.setId("1");
        event.setMake("Toyota");
        event.setModel("Corolla");
        event.setYear(2018);

        CarListing model = new CarListing("1", "Toyota", "Corolla");
        doReturn(model).when(carListingTranslator).translateToModel(event);

        kafkaTemplate.send("car-listings", event);
        kafkaTemplate.flush();

        // Wait briefly for consumer to process the message
        Thread.sleep(2000); // small delay

        verify(carListingService, atLeastOnce()).createCarListing(any(CarListing.class));
    }

    @Test
    void testShouldConsumeUpdatedEvent() throws InterruptedException {
        CarListingEvent event = new CarListingEvent();
        event.setAction("UPDATED");
        event.setId("2");
        event.setMake("Honda");
        event.setModel("Civic");
        event.setYear(2018);

        kafkaTemplate.send("car-listings", event);
        kafkaTemplate.flush();

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
               verify(carListingService).updateCarListing(eq("2"), any(CarListing.class)));
    }

    @Test
    void shouldConsumeDeletedEvent() {
        CarListingEvent event = new CarListingEvent();
        event.setAction("DELETED");
        event.setId("3");
        event.setMake("Honda");
        event.setModel("Civic");
        event.setYear(2018);

        kafkaTemplate.send("car-listings", event);
        kafkaTemplate.flush();

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                verify(carListingService).deleteCarListing(eq("3")));
    }

    @Test
    void shouldHandleUnknownActionWithoutError() {
        CarListingEvent event = new CarListingEvent();
        event.setAction("Unknown");
        event.setId("3");
        event.setMake("Honda");
        event.setModel("Civic");
        event.setYear(2018);

        kafkaTemplate.send("car-listings", event);
        kafkaTemplate.flush();

        // no exception should be thrown, just verify no service method is called
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() ->
                verifyNoInteractions(carListingService)); // won't be called
    }

}
