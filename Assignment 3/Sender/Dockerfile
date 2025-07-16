# Faza 1: Build-ul aplicației
FROM maven:3.8.3-openjdk-17 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Rulează comanda Maven pentru a genera JAR-ul
RUN mvn package -DskipTests

# Faza 2: Imaginea runtime
FROM openjdk:17-jdk-slim-buster

# Configurare variabile de mediu
ENV TZ=UTC
ENV SPRING_RABBITMQ_HOST=rabbitmq
ENV SPRING_RABBITMQ_PORT=5672
ENV SPRING_RABBITMQ_USERNAME=guest
ENV SPRING_RABBITMQ_PASSWORD=guest

# Copiază JAR-ul generat în container
COPY --from=builder /app/target/Sender-0.0.1-SNAPSHOT.jar app.jar

# Copiază fișierul sensor.csv în container
COPY sensor.csv /app/sensor.csv

# Punctul de intrare
CMD ["java", "-jar", "app.jar"]
