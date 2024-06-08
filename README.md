# Sep

Simple microservice project with spring cloud and various other technologies

---

## Infrastructure Services
### 1. sep-discovery-service
1. Java 22 spring boot 3.5
2. Spring cloud eureka server
### 2. sep-config-service
1. Java 22 spring boot 3.5
2. Spring cloud config server
    1. Encrypt a value using config server `curl <CONFIG_SERVICE_URL>/encrypt -s -d <VALUE>`
3. Spring actuator
    1. Refresh variables `curl -X POST <APPLICATION_SERVICE_URL>/actuator/refresh`
### 2. sep-gateway-service
1. Java 22 spring boot 3.5
2. Spring cloud gateway
3. Spring cache
	1. Caffeine
4. Central swagger for services
    1. `<GATEWAY_SERVICE_URL>/swagger`
5. Eureka dashboard through gateway
    1. `<GATEWAY_SERVICE_URL>/eureka-dashboard`
6. Jwt filter for authentication

## Application Services
### 1. sep-product-service
1. Java 22 spring boot 3.5
2. Spring web
    1. Classical controllers
3. Relational db 
    1. Mysql
    2. Flyway migrations
4. Specification pattern
5. Spring cache
	1. Redis
6. Event sourcing
	1. Kafka
    2. Retry and dlt topics
7. Circuit breaker pattern
    1. Resilience4j
8. Structured logging
    1. Logback
	2. Elk stack
9. Helpers
    1. Feign client
10. Tests
    1. Unit tests
        1. Without starting spring container
    2. Integration tests
        1. H2 db
        2. Embedded kafka
    3. Coverage
        1. Jacoco `mvn clean verify`, `mvn jacoco:report`
### 2. sep-payment-service
1. Java 22 spring boot 3.5
2. Spring webflux
    1. Handlers
    2. Functional api
3. No-sql db
    1. Mongodb
4. Event sourcing
	1. Kafka
    2. Retry and dlt topics
5. Circuit breaker pattern
    1. Resilience4j
6. Structured logging
    1. Logback
	2. Elk stack
7. Tests
    1. Unit tests
        1. Without starting spring container
    2. Integration tests
        1. Test containers mongo
        2. Embedded kafka
    3. Coverage
        1. Jacoco `mvn clean verify`, `mvn jacoco:report`
### 3. sep-search-service
1. Java 22 spring boot 3.5
2. Kotlin 2.0.0
    1. Kotlin for pojo classes
3. Spring web
    1. Classical controllers
4. No-sql db
    1. Elasticsearch
5. Event sourcing
	1. Kafka
    2. Retry and dlt topics
6. Circuit breaker pattern
    1. Resilience4j
7. Structured logging
    1. Logback
	2. Elk stack
8. Helpers
    1. Feign client
9. Tests
    1. Unit tests
        1. Without starting spring container
    2. Integration tests
        1. Test containers elasticsearch
    3. Coverage
        1. Jacoco `mvn clean verify`, `mvn jacoco:report`

## Development

### Creating new service
[Spring initializr template](https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.2.5&packaging=jar&jvmVersion=21&groupId=com.kurtcan&artifactId=sep-product-service&name=sep-product-service&description=Simple%20ecommerce%20platform.&packageName=com.kurtcan.sep-product-service&dependencies=lombok,cloud-eureka)

### Running
```shell
mvn spring-boot:run
```

## Testing

### Create coverage report
```shell
mvn clean verify
```
```shell
mvn jacoco:report
```

