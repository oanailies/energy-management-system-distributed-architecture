# Stage 1: Build the application
FROM maven:3.8.3-openjdk-17 AS builder

WORKDIR /app

# Copy the project files to the container
COPY pom.xml .
COPY src ./src

# Run Maven to build the application
RUN mvn package -DskipTests

# Stage 2: Runtime environment
FROM openjdk:17-jdk-slim-buster

# Set environment variables for RabbitMQ and PostgreSQL
ENV SPRING_RABBITMQ_HOST=rabbitmq
ENV SPRING_RABBITMQ_PORT=5672
ENV SPRING_RABBITMQ_USERNAME=guest
ENV SPRING_RABBITMQ_PASSWORD=guest

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/consumer_db
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=oana2002

# Set timezone
ENV TZ=UTC

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/Receiver-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8084

# Run the application
CMD ["java", "-jar", "app.jar"]
