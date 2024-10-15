FROM maven:3.9-eclipse-temurin-22-alpine AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:22-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/sep-application.jar
CMD ["java", "-jar", "sep-application.jar"]
