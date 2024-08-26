## Infrastructure Setup

### Create bridge network
```shell
docker network create sep-bridge-network
```

### MySql
- Phpmyadmin https://localhost:1001
- MySql https://localhost:3306
```shell
docker compose -f docker-compose-mysql.yaml up -d 
```
---
### MongoDb
- Mongo express https://localhost:1002
- MongoDb https://localhost:27017
```shell
docker compose -f docker-compose-mongo.yaml up -d 
```
---
### Redis
- Redis commander https://localhost:1003
- Redis https://localhost:6379
```shell
docker compose -f docker-compose-redis.yaml up -d 
```
---
### ELK
- Kibana https://localhost:1004
- Elasticsearch https://localhost:9200
- Logstash https://localhost:5000
```shell
docker compose -f docker-compose-elk.yaml up -d 
```
---
### Kafka
- Kafka ui https://localhost:1005
- Kafka https://localhost:9092
```shell
docker compose -f docker-compose-kafka.yaml up -d 
```
---
### Keycloak
- Keycloak https://localhost:1006
```shell
docker compose -f docker-compose-keycloak.yaml up -d 
```
### Setup Keycloak
1. Create `sep` realm
    - Copy realm public key `Realm settings > Keys > RS256 > Public key`
2. Create Keycloak clients for services
    - Repeat these for all clients
        1. While creating enable service accounts roles
        2. While creating enable client authentication
        3. Copy client secret `Clients > Client details > Credentials > Client Secret`
    1. Create client: `sep-product-service`
    2. Create client: `sep-payment-service`
    3. Create client: `sep-search-service`
    4. Go to `Client Scopes > roles > Mappers > create`
        - Name: "roles"
        - Mapper Type: "User Client Role"
        - Multivalued: True
        - Token Claim Name: "roles"
        - Add to access token: True
- Get token from keycloak
```shell
curl -X POST https://<KEYCLOAK_SERVER>/realms/sep/protocol/openid-connect/token -H "Content-Type: application/x-www-form-urlencoded" -d "client_id=sep-product-service&client_secret=<CLIENT_SECRET>&grant_type=client_credentials"
```
---

### Applications
```shell
docker compose -f ./sep-discovery-service/docker-compose.yaml up -d --remove-orphans
```
```shell
docker compose -f ./sep-config-service/docker-compose.yaml up -d --remove-orphans
```
```shell
docker compose -f ./sep-gateway-service/docker-compose.yaml up -d --remove-orphans
```
```shell
docker compose -f ./sep-product-service/docker-compose.yaml up -d --remove-orphans
```
```shell
docker compose -f ./sep-payment-service/docker-compose.yaml up -d --remove-orphans
```
```shell
docker compose -f ./sep-search-service/docker-compose.yaml up -d --remove-orphans
```
---