# ==============================================================================
# Premium Calculator - Production-Oriented Multi-Stage Dockerfile
# Stage 1: Build & Package using Eclipse Temurin JDK 17 & Maven Wrapper
# Stage 2: Hardened Minimal JRE 17 Runtime with Non-Root Security Context
# ==============================================================================

# ----------------- STAGE 1: Builder -----------------
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /build

# Copy Maven wrapper configuration and descriptor first for optimal layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Normalize Windows CRLF line endings to POSIX LF and grant execution permissions
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Pre-fetch project dependencies for reproducible offline builds
RUN ./mvnw dependency:go-offline -B || true

# Copy application source code and resources
COPY src ./src

# Compile and package executable Spring Boot fat JAR
RUN ./mvnw clean package -DskipTests=true -B

# ----------------- STAGE 2: Runtime -----------------
FROM eclipse-temurin:17-jre-jammy AS runtime

LABEL maintainer="Manas Chand <manas.chand@college.edu>" \
      project="premium-calculator" \
      version="1.0.0" \
      description="Production container for Premium Calculator Application"

# JVM memory configuration, optimization flags, and server port
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError" \
    SERVER_PORT=8080

# Install lightweight curl package for Docker container health check probes
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

# Create dedicated non-root application user and group (Principle of Least Privilege)
RUN groupadd -g 1001 appgroup && \
    useradd -u 1001 -g appgroup -s /bin/bash -m appuser

WORKDIR /app

# Copy the packaged Spring Boot fat JAR from the builder stage
COPY --from=builder --chown=appuser:appgroup /build/target/premium-calculator-*.jar app.jar

# Expose default application HTTP port
EXPOSE 8080

# Configure container health check probing Spring Boot Actuator endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=25s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Enforce non-root execution
USER appuser:appgroup

# Launch application using configured JVM flags
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
