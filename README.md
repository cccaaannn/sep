# SEP

**S**pring **E**-commerce **P**roject is a simple microservice project with spring cloud and various other technologies

![GitHub top language](https://img.shields.io/github/languages/top/cccaaannn/sep?color=blue&style=flat-square) ![GitHub repo size](https://img.shields.io/github/repo-size/cccaaannn/sep?color=orange&style=flat-square) [![GitHub](https://img.shields.io/github/license/cccaaannn/sep?color=green&style=flat-square)](https://github.com/cccaaannn/sep/blob/master/LICENSE)

---

## Table of Contents
- [Architecture Diagram](#architecture-diagram)
- [Events Diagram](#events-diagram)
- [Infrastructure Services](#infrastructure-services)
  - [sep-discovery-service](#1-sep-discovery-service)
  - [sep-config-service](#2-sep-config-service)
  - [sep-gateway-service](#3-sep-gateway-service)
- [Application Services](#application-services)
  - [sep-product-service](#1-sep-product-service)
  - [sep-payment-service](#2-sep-payment-service)
  - [sep-search-service](#3-sep-search-service)
  - [sep-aggregator-service](#4-sep-aggregator-service)
- [Development](#development)
- [Testing](#testing)

## Architecture Diagram

![architecture-diagram](/sep-docs/architecture-diagram.drawio.svg)

---

## Events Diagram

![architecture-diagram](/sep-docs/events-diagram.drawio.svg)

---

## Infrastructure Services
### 1. sep-discovery-service
1. Java 22 spring boot 3.3
2. Spring cloud eureka server
### 2. sep-config-service
1. Java 22 spring boot 3.3
2. Spring cloud config server
    1. Encrypt a value using config server `curl <CONFIG_SERVICE_URL>/encrypt -s -d <VALUE>`
3. Spring actuator
    1. Refresh variables `curl -X POST <APPLICATION_SERVICE_URL>/actuator/refresh`
### 3. sep-gateway-service
1. Java 22 spring boot 3.3
2. Spring cloud gateway
3. Spring cache
	1. Caffeine
4. Central swagger for services through gateway
    1. `<GATEWAY_SERVICE_URL>/swagger`
5. Eureka dashboard through gateway
    1. `<GATEWAY_SERVICE_URL>/eureka-dashboard`
6. Graphiql ui through gateway
    1. `<GATEWAY_SERVICE_URL>/graphiql?path=/aggregator/graphql`
7. Jwt filter for authentication

## Application Services
### 1. sep-product-service
- Manges products.
1. Java 22 spring boot 3.3
2. Spring web
    1. Classical controllers
3. Relational db 
    1. MySql
    2. Flyway migrations
4. Specification pattern
5. Spring cache
	1. Redis
    2. In memory cache as fallback
6. Event sourcing
	1. Kafka
    2. Retry and dlt topics
7. Circuit breaker pattern
    1. Resilience4j
8. Structured logging
    1. Logback
	2. Elk stack
9. Authentication
    1. Keycloak
10. Helpers
    1. Feign client
11. Tests
    1. Unit tests
        1. Without starting spring container
    2. Integration tests
        1. H2 db
        2. Embedded kafka
    3. Coverage
        1. Jacoco `mvn clean verify`, `mvn jacoco:report`
### 2. sep-payment-service
- Manages user payments.
1. Java 22 spring boot 3.3
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
7. Authentication
    1. Keycloak
8. Tests
    1. Unit tests
        1. Without starting spring container
    2. Integration tests
        1. Test containers mongo
        2. Embedded kafka
    3. Coverage
        1. Jacoco `mvn clean verify`, `mvn jacoco:report`
### 3. sep-search-service
- Saves event sourced resources to elasticsearch, exposes endpoints for fuzzy search for saved resources.
1. Java 22 spring boot 3.3
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
8. Authentication
    1. Keycloak
9. Helpers
    1. Feign client
10. Tests
    1. Unit tests
        1. Without starting spring container
    2. Integration tests
        1. Test containers elasticsearch
    3. Coverage
        1. Jacoco `mvn clean verify`, `mvn jacoco:report`
### 4. sep-aggregator-service
- Aggregates resources from other services, caches responses per user, exposes them as graphql.
1. Java 22 spring boot 3.3
2. Spring graphql
    1. GraphQL query mappers
3. Spring cache
	1. Hazelcast
        1. Eureka discovery for cluster
    2. In memory cache as fallback
4. Event sourcing
	1. Kafka
    2. Retry and dlt topics
5. Circuit breaker pattern
    1. Resilience4j
6. Structured logging
    1. Logback
	2. Elk stack
7. Authentication
    1. Keycloak
8. Helpers
    1. Feign client
9. Tests
    1. Unit tests
        1. Without starting spring container
    2. Integration tests
    3. Coverage
        1. Jacoco `mvn clean verify`, `mvn jacoco:report`

## Development

### Running services
```shell
mvn spring-boot:run
```

### Running for development with infrastructure
You need to run many infrastructure services before starting the actual applications, to have a better dev experience I suggest running infrastructure related applications on a cloud vps.
1. Preliminary steps
    1. Have `Java >22`, `Maven >3` and `Kotlin >2` on your path
    2. Move to project's root folder
        - Commands are adjusted for project's root
2. Run infrastructure in docker
    - Check [Infrastructure Setup](/sep-infra/README.md) for more details
    1. Create internal network
        1. `docker network create sep-bridge-network`
    2. Run containers
        1. `docker compose -f sep-infra/docker-compose-mysql.yaml up -d`
        2. `docker compose -f sep-infra/docker-compose-mongo.yaml up -d`
        3. `docker compose -f sep-infra/docker-compose-kafka.yaml up -d`
        4. `docker compose -f sep-infra/docker-compose-keycloak.yaml up -d`
        5. (Optional) `docker compose -f sep-infra/docker-compose-redis.yaml up -d`
        6. (Optional) `docker compose -f sep-infra/docker-compose-elk.yaml up -d`
3. Populate config files under `/sep-config`
    1. Global config
        ```
        <KEYCLOAK-REALM-PUBLIC-KEY>
        <KEYCLOAK-HOST>
        <REDIS-HOST> (Optional)
        <KAFKA-HOST>
        <LOGSTASH-HOST> (Optional)
        <ELASTIC-HOST>
        ```
    2. Product service
        ```
        <MYSQL-HOST>
        <MYSQL-PASSWORD>
        <KEYCLOAK-CLIENT-SECRET>
        ```
    3. Payment service
        ```
        <MONGO-HOST>
        <MONGO-PASSWORD>
        <KEYCLOAK-CLIENT-SECRET>
        ```
    4. Search service
        ```
        <KEYCLOAK-CLIENT-SECRET>
        ```
    5. Aggregator service
        ```
        <KEYCLOAK-CLIENT-SECRET>
        ```
    5. Init a local git repo for config files under `/sep-config` 
        - Config service uses git repo for dynamic config values
        1. `git -C sep-config init`
        2. `git -C sep-config add .`
        3. `git -C sep-config commit -m "init"`
4. Run Spring cloud services
    1. `mvn -f sep-discovery-service/pom.xml spring-boot:run`
    2. `mvn -f sep-config-service/pom.xml spring-boot:run`
    3. `mvn -f sep-gateway-service/pom.xml spring-boot:run` (optional)
        - You can use gateway to access all services from a single place 
        1. eureka-dashboard from gateway http://localhost:8093/eureka-dashboard
        2. Swagger ui for all services from gateway http://localhost:8093/swagger
        3. Graphiql ui for aggregator service from gateway http://localhost:8093/graphiql?path=/aggregator/graphql
5. Run application services for development with your ide
6. Authentication while developing
    1. Get token from Keycloak with any service name and secret key.
        ```shell
        curl -X POST https://<KEYCLOAK_SERVER>/realms/sep/protocol/openid-connect/token -H "Content-Type: application/x-www-form-urlencoded" -d "client_id=<SERVICE-NAME>&client_secret=<CLIENT_SECRET>&grant_type=client_credentials"
        ```
    2. Swagger-ui's has `Authorize` section for the jwt
    3. GraphiQL has headers section, add authorization header with jwt `{ "Authorization": "Bearer <jwt>" }`

### Creating new service with spring initializr
[Spring initializr template](https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.2.5&packaging=jar&jvmVersion=21&groupId=com.kurtcan&artifactId=sep-product-service&name=sep-product-service&description=Simple%20ecommerce%20platform.&packageName=com.kurtcan.sep-product-service&dependencies=lombok,cloud-eureka)

## Testing

### Create coverage report
- Go to service directory
```shell
mvn clean verify
```
```shell
mvn jacoco:report
```

