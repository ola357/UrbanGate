# Build stage
FROM gradle:8.14.4-jdk21 AS builder
WORKDIR /workspace

# Copy only what we need for dependency caching first
COPY settings.gradle.kts build.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY modules ./modules

# Build the boot jar (skip tests for image build; CI should run tests)
RUN gradle --no-daemon :modules:app:bootJar -x test

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy jar from builder
COPY --from=builder /workspace/modules/app/build/libs/*.jar app.jar

ENV JAVA_OPTS=""
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
