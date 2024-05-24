FROM maven:3.9-eclipse-temurin-22-alpine as builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .
RUN mvn clean package

FROM eclipse-temurin:22-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/sep-app.jar
CMD ["java", "-jar",  "sep-app.jar"]
