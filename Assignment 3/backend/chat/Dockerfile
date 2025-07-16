FROM maven:3.8.3-openjdk-17-slim AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-slim-buster


ENV TZ=UTC


ENV DB_HOST=localhost
ENV DB_PORT=5432
ENV DB_USER=postgres
ENV DB_PASSWORD=oana2002
ENV DB_DBNAME=chatdb


COPY --from=builder /app/target/chat-0.0.1-SNAPSHOT.jar app.jar


EXPOSE 8090


CMD ["java", "-jar", "app.jar"]
