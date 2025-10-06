# Car Listing Search Service

A microservice built with Spring Boot 3.5.6, Java 21, Kafka 3.5, and Elasticsearch 8.11. This service provides a RESTful API for efficient searching and management of car listings using Elasticsearch as the search engine and Kafka for event-driven architecture.

## 🚀 Features

- *Car Listing Management*: Create, update, and delete car listing records
- *Advanced Search*: Full-text search across make, model, color, and description
- *Filtered Search*: Search by specific criteria like make
- *Multiple Search*: Search optionally by make, model, color and year 
- *Event-Driven Architecture*: Kafka integration for real-time car listing updates
- *Elasticsearch Integration*: High-performance search with Elasticsearch
- *RESTful APIs*: Well-documented REST endpoints with proper HTTP status codes
- *Global Exception Handling*: Centralized error handling with meaningful error messages
- *Dead Letter Topic (DLT)*: Error handling for failed Kafka message processing

## 📋 Prerequisites

- Java 21 or higher
- Gradle 8.14.3
- Docker and Docker Compose
- Elasticsearch 8.11.0
- Kafka 7.4.0 (Confluent)
- Kibana 8.11.0 (for Elasticsearch management)

## 🚀 How to Start the Application

### 1. Start Infrastructure Services
```bash
# Start Kafka, Elasticsearch, and Kibana
docker-compose up -d
```

### 2. Verify Services are Running
```bash
# Check if Kafka is running
docker exec -it kafka /opt/kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092

# Check if Elasticsearch is running
curl -u elastic:password "http://localhost:9200/_cluster/health"

# Check if Kibana is accessible
curl "http://localhost:5601/api/status"
```

### 3. Run the Application
```bash
# Using Gradle
./gradlew bootRun
```

## 🚀 Access the application
- **API Base URL**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Kibana Dashboard**: http://localhost:5601
- **Elasticsearch**: http://localhost:9200

## 📚 API Documentation

### Car Listing Management

| Method | Endpoint | Description | Parameters |
|--------|----------|-------------|------------|
| GET | `/api/car-listings` | Get all car listings with pagination | `page`, `size`, `sortBy`, `sortDir` |
| GET | `/api/car-listings/search` | Full-text search across make, model, color, description | `q`, `page`, `size` |
| POST | `/api/car-listings/search` | Advanced search with custom criteria | `CarListingSearchRequest` body |
| GET | `/api/car-listings/search/make/{make}` | Search car listings by make | `make` (path variable) |


## Swagger

* use http://localhost:8080/swagger-ui.html once you start the application to interact with APIs

## Testing

Run the test suite:
./gradlew test

 Run specific test class
./gradlew test --tests "CarListingTranslatorTest"

## Monitor Application

The application includes Spring Boot Actuator for monitoring:

- Health checks:  http://localhost:8080/actuator/health
- Application info:  http://localhost:8080/actuator/info
- Metrics: http://localhost:8080/actuator/metrics

## Monitor Kafka Messages
```bash
# Monitor all messages in car-listings topic
docker exec -it kafka /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic car-listings \
  --from-beginning

# Check consumer group status
docker exec -it kafka /opt/kafka/bin/kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --group car-listing-consumer-group \
  --describe

