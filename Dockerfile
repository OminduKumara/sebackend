# ==========================
# Build stage
# ==========================
FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Copy only required files for Maven caching
COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src ./src

# Build without running tests
RUN mvn -B -DskipTests package

# ==========================
# Run stage
# ==========================
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the jar
COPY --from=builder /workspace/target/*.jar ./app.jar

EXPOSE 8080

# Limit JVM memory for small container
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# Respect Railway's PORT env variable
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
