# ==============================================================================
# Multi-stage Dockerfile for Verdant (Spring Boot 3.1.2 / Java 17)
# Optimized for Render Free Web Service (512 MB RAM limit)
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build Stage
# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /build

# Copy Maven wrapper and POM configuration
COPY Grow-Garden-main/Grow-Graden/.mvn/ Grow-Garden-main/Grow-Graden/.mvn/
COPY Grow-Garden-main/Grow-Graden/mvnw Grow-Garden-main/Grow-Graden/mvnw
COPY Grow-Garden-main/Grow-Graden/pom.xml Grow-Garden-main/Grow-Graden/pom.xml

WORKDIR /build/Grow-Garden-main/Grow-Graden

# Ensure UNIX line endings and executable bit on wrapper
RUN sed -i 's/\r$//' ./mvnw && chmod +x ./mvnw

# Pre-fetch dependencies to leverage Docker layer caching
RUN ./mvnw dependency:go-offline -B || true

# Copy application source code
COPY Grow-Garden-main/Grow-Graden/src ./src

# Compile and package Spring Boot executable JAR (skipping test execution during Docker build)
RUN ./mvnw clean package -DskipTests -B

# ------------------------------------------------------------------------------
# Stage 2: Production Runtime Stage
# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-jammy

# Create non-root system user and group for container security
RUN groupadd -r appgroup && useradd -r -g appgroup -d /app -s /sbin/nologin appuser

WORKDIR /app

# Ensure writable data directory for embedded H2 database
RUN mkdir -p /app/data && chown -R appuser:appgroup /app

# Copy packaged Spring Boot JAR from builder stage
COPY --from=builder --chown=appuser:appgroup /build/Grow-Garden-main/Grow-Graden/target/Grow-Graden-0.0.1-SNAPSHOT.jar /app/app.jar

USER appuser

# Render injects the PORT environment variable dynamically (defaults to 8080 locally)
ENV PORT=8080

# Configure JVM ergonomics specifically for Render's 512 MB RAM Free tier
# - MaxRAMPercentage=75.0: caps max heap memory at ~384MB, leaving headroom for non-heap/metaspace
# - Xss512k: conservative thread stack size saving valuable heap headroom
# - ExitOnOutOfMemoryError: guarantees rapid container restart instead of hanging on OOM
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Xss512k -XX:+ExitOnOutOfMemoryError"

EXPOSE 8080

# Launch Spring Boot with dynamic port binding and graceful signal forwarding
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Dserver.port=${PORT} -jar /app/app.jar"]
