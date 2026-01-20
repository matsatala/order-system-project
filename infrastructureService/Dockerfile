# --- ETAP 1: BUILD (Używamy obrazu Maven z JDK 21) ---
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# --- ETAP 2: RUNTIME (Używamy obrazu JRE 21) ---
# Wersja Alpine jest lżejsza, idealna do mikroserwisów
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Odkomentuj, jeśli to order-service (8081) lub inny wystawiający port
# EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
