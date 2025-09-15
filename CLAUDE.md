# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Architecture Overview

AirCrowd is a microservices-based data processing platform for airport management, built with Spring Boot 4.0.0-SNAPSHOT and Java 24. The system consists of seven main modules:

- **Common**: Shared utilities, models, and configurations (Kafka, Spark, validation)
- **ConfigServer**: Spring Cloud Config Server for centralized configuration
- **DiscoveryServer**: Spring Eureka Discovery Server for service registration and discovery
- **FlightSchedule**: Processes flight scheduling data (XLSX/CSV) using Apache Spark ETL
- **Weather**: Fetches weather data from external APIs and publishes to Kafka
- **PassengerFlowPrediction**: ML-based passenger flow prediction using weather and flight data
- **AirportManagement**: REST API Gateway providing dashboard access to predictions, weather, and flight data

The platform uses Kafka for event streaming, Apache Spark for ETL processing, Redis for caching, Apache Iceberg for data lakehouse storage, and Eureka for service discovery.

## Development Commands

### Build and Package
```bash
# Clean and package all modules
mvn clean package

# Build specific module
mvn clean package -pl Weather

# Skip tests during build
mvn clean package -Pno-tests

# Build with test coverage
mvn clean package -Pcoverage
```

### Testing
```bash
# Run all tests
mvn test

# Run tests for specific module
mvn test -pl Common

# Run specific test class
mvn test -Dtest=WeatherDataTest

# Skip tests
mvn test -DskipTests=true
```

### Code Formatting
```bash
# Apply code formatting (Palantir Java Format)
mvn spotless:apply

# Check code formatting
mvn spotless:check
```

### Docker and Infrastructure
```bash
# Start all infrastructure services (Kafka, Spark, MinIO, Iceberg, Eureka, Redis)
docker-compose up -d

# Start core infrastructure first (recommended)
docker-compose up -d redis discovery-service

# Build and run specific service
docker-compose build weather-service
docker-compose up weather-service

# View service logs
docker-compose logs -f redis
docker-compose logs -f discovery-service
docker-compose logs -f weather-service
```

### Spring Boot Services
Each microservice can be run individually:
```bash
# Start Discovery Server first
cd DiscoveryServer && mvn spring-boot:run

# Run Weather service (will register with Eureka)
cd Weather && mvn spring-boot:run

# Run Airport Management API service
cd AirportManagement && mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring.profiles.active=dev
```

## Module Dependencies

- All modules inherit from parent POM with Spring Boot 4.0.0-SNAPSHOT
- **Common** is a shared dependency used by FlightSchedule, Weather, PassengerFlowPrediction, and AirportManagement
- FlightSchedule depends on Apache POI (Excel) and Commons CSV
- Weather uses Apache HttpClient5 for external API calls
- AirportManagement provides REST APIs with PostgreSQL for persistence and OpenAPI documentation
- All modules use Kafka for event streaming, Spring Cloud Config for configuration, and Eureka for service discovery

## Key Technologies

- **Java 24** with Lombok for boilerplate reduction
- **Spring Boot 4.0.0-SNAPSHOT** with Spring Cloud 2025.0.0-SNAPSHOT
- **Apache Spark 4.0.0** for ETL processing and data transformations
- **Apache Kafka 4.0.0** for event streaming and message passing
- **Spring Cloud Config** for externalized configuration management
- **Netflix Eureka** for service registration and discovery
- **Apache Iceberg** with MinIO S3-compatible storage for data lakehouse
- **Redis** for caching layer
- **Maven 3.14.0** with centralized dependency management

## Configuration Management

The system uses Spring Cloud Config Server for centralized configuration. Each service connects to the config server on startup to retrieve environment-specific settings. Configuration includes:

- Kafka bootstrap servers and producer/consumer settings  
- Redis connection details for caching
- External API endpoints and authentication
- Spark configuration for ETL processing
- Service discovery and inter-service communication

## Data Flow Architecture

1. **FlightSchedule** processes flight data files (XLSX/CSV) using Spark ETL
2. **Weather** fetches forecast data from external APIs 
3. Both services publish processed data to Kafka topics
4. **PassengerFlowPrediction** consumes data streams to generate ML-based predictions
5. All processed data is stored in Apache Iceberg tables via MinIO S3 storage

## ETL Processing Pattern

The Common module provides a standardized Spark ETL framework:
- `SparkExtractor`: Data extraction from various sources
- `SparkTransformer`: Data transformation and cleansing 
- `SparkLoader`: Data loading to target destinations
- `SparkETLProcess`: Orchestrates the complete ETL pipeline

## Testing Strategy

- Unit tests use JUnit 5 and Spring Boot Test
- Integration tests with Testcontainers for Kafka
- WireMock for external API testing (Weather module)
- Clover for test coverage reporting (use `-Pcoverage` profile)

## REST API Endpoints

The AirportManagement service exposes the following REST APIs for dashboard integration:

### Predictions API
- `GET /api/v1/predictions/latest` - Latest passenger flow predictions
- `GET /api/v1/predictions/{id}` - Specific prediction by ID
- `GET /api/v1/predictions/range` - Predictions within time range
- `GET /api/v1/predictions/next-hours` - Predictions for next N hours

### Weather API  
- `GET /api/v1/weather/current` - Current weather conditions
- `GET /api/v1/weather/forecast` - Weather forecast for N days
- `GET /api/v1/weather/history` - Historical weather data

### Flights API
- `GET /api/v1/flights/today` - Today's flight schedule
- `GET /api/v1/flights/next-24h` - Flights in next 24 hours
- `GET /api/v1/flights/range` - Flights within time range
- `GET /api/v1/flights/by-type` - Flights filtered by ARRIVAL/DEPARTURE
- `GET /api/v1/flights/{flightCode}` - Specific flight details

All APIs return data in JSON format with standardized `ApiResponse<T>` wrapper including success status, timestamp, and error handling.

**API Documentation**: Available at `/swagger-ui.html` when service is running.

## Code Quality

- Spotless Maven plugin enforces Palantir Java Format
- Code formatting is automatically applied during compilation
- SonarQube integration for static code analysis
- Lombok reduces boilerplate with proper annotation processor configuration
- Java Records used for DTOs providing immutability and conciseness