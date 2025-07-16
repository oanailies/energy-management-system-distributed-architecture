FROM maven:3.8.3-openjdk-17-slim AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src


RUN mvn clean package -DskipTests





FROM openjdk:17-jdk-slim-buster

ENV TZ=UTC
ENV DB_IP=localhost
ENV DB_PORT=5432
ENV DB_USER=postgres
ENV DB_PASSWORD=oana2002
ENV DB_DBNAME=user_db

COPY --from=builder /app/target/users-0.0.1-SNAPSHOT.jar app.jar



CMD ["java", "-jar","app.jar"]

