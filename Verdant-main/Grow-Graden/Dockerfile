# ==============================================================================
# Multi-stage Dockerfile for Verdant (Module-level build)
# Optimized for Render Free Web Service (512 MB RAM limit)
# ==============================================================================

# Stage 1: Build
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /build

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN sed -i 's/\r$//' ./mvnw && chmod +x ./mvnw
RUN ./mvnw dependency:go-offline -B || true

COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy
RUN groupadd -r appgroup && useradd -r -g appgroup -d /app -s /sbin/nologin appuser

WORKDIR /app
RUN mkdir -p /app/data && chown -R appuser:appgroup /app
COPY --from=builder --chown=appuser:appgroup /build/target/Grow-Graden-0.0.1-SNAPSHOT.jar /app/app.jar

USER appuser
ENV PORT=8080
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Xss512k -XX:+ExitOnOutOfMemoryError"
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Dserver.port=${PORT} -jar /app/app.jar"]
