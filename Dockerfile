# ==============================================================================
# Premium Calculator - Multi-stage Production Dockerfile
# Stage 1: Build & Package with Maven and Eclipse Temurin JDK 17
# Stage 2: Minimal Distroless / Hardened Alpine JRE 17 Runtime with Non-root User
# ==============================================================================

# ----------------- STAGE 1: Builder -----------------
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /build

# Copy Maven wrapper and pom.xml first to cache dependencies layer
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Give execution permission to Maven wrapper
RUN chmod +x mvnw

# Download dependencies (offline cache layer)
RUN ./mvnw dependency:go-offline -B || true

# Copy source code and static assets
COPY src ./src

# Compile and package Spring Boot fat jar (skipping browser e2e tests)
RUN ./mvnw clean package -DskipTests=false -B

# ----------------- STAGE 2: Runtime -----------------
FROM eclipse-temurin:17-jre-jammy AS runtime

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError" \
    SERVER_PORT=8080

# Install curl for Actuator health checks
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

# Create dedicated non-root application user and group
RUN groupadd -g 1001 appgroup && \
    useradd -u 1001 -g appgroup -s /bin/bash -m appuser

WORKDIR /app

# Copy packaged JAR file from builder stage
COPY --from=builder --chown=appuser:appgroup /build/target/premium-calculator-*.jar app.jar

# Expose Spring Boot web port
EXPOSE 8080

# Configure container health check targeting Actuator endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run with non-root user for security hardening
USER appuser:appgroup

# Launch application with JVM memory and optimization flags
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
