# Stage 1: Build & Resolve Dependencies
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /workspace

# Copy Gradle wrapper and configuration files first to cache dependencies
COPY gradlew .
COPY gradle gradle
COPY build.gradle* settings.gradle* ./

RUN ./gradlew dependencies --no-daemon || true

# Copy source and build executable bootJar
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

# Extract Spring Boot layers (Spring Boot 3.3+)
RUN java -Djarmode=tools -jar build/libs/*.jar extract --layers --launcher --destination extracted


# Stage 2: Minimal Production Runtime
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

COPY --from=builder /workspace/extracted/dependencies/ ./
COPY --from=builder /workspace/extracted/spring-boot-loader/ ./
COPY --from=builder /workspace/extracted/snapshot-dependencies/ ./
COPY --from=builder /workspace/extracted/application/ ./

USER 10001:10001

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "org.springframework.boot.loader.launch.JarLauncher"]