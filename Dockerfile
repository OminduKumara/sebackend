## Multi-stage Dockerfile for building and running the mybungalow Spring Boot app
## - Build stage uses Maven with Eclipse Temurin JDK 17 (matches pom.xml java.version)
## - Run stage uses a lightweight Temurin JRE image

FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Copy only the files needed for a Maven build to leverage layer caching
COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src ./src

# Package the application (skip tests to speed up builds on deploy)
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built jar from the builder stage. Use wildcard to be resilient to SNAPSHOT version.
COPY --from=builder /workspace/target/*.jar ./app.jar

EXPOSE 8080

# Allow passing extra JVM options via JAVA_OPTS and respect PORT environment variable injected by Railway
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
