# --- ETAP 1: BUILD (Budowanie aplikacji) ---
# Używamy sprawdzonego Mavena 3.9 z Javą 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Kopiujemy pom.xml i pobieramy zależności (cache'owanie)
COPY pom.xml .
RUN mvn dependency:go-offline

# Kopiujemy kod źródłowy i budujemy plik .jar
COPY src ./src
RUN mvn clean package -DskipTests

# --- ETAP 2: RUNTIME (Uruchamianie) ---
# Używamy lekkiego obrazu JRE 21 (Alpine)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Kopiujemy zbudowany plik .jar z etapu pierwszego
COPY --from=build /app/target/*.jar app.jar

# WAŻNE: Informujemy Dockera, że ten kontener nasłuchuje na porcie 8081
# (To musi się zgadzać z server.port w application.properties)
EXPOSE 8081

# Uruchamiamy aplikację
ENTRYPOINT ["java", "-jar", "app.jar"]
