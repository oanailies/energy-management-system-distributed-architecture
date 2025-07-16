FROM maven:3.8.3-openjdk-17 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src


RUN mvn package -DskipTests




FROM openjdk:17-jdk-slim-buster

ENV TZ=UTC
ENV DB_IP=localhost
ENV DB_PORT=5432
ENV DB_USER=postgres
ENV DB_PASSWORD=oana2002
ENV DB_DBNAME=devices_db

# Set environment variables for RabbitMQ and PostgreSQL
ENV SPRING_RABBITMQ_HOST=rabbitmq
ENV SPRING_RABBITMQ_PORT=5672
ENV SPRING_RABBITMQ_USERNAME=guest
ENV SPRING_RABBITMQ_PASSWORD=guest

COPY --from=builder /app/target/devices-0.0.1-SNAPSHOT.jar app.jar




CMD ["java", "-jar","app.jar"]


