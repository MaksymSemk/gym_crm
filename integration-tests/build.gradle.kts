plugins {
    java
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "integration-tests"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.security:spring-security-test")


    // Project dependencies (for models, DTOs, clients)
    testImplementation(project(":core-service"))
    testImplementation(project(":trainer-workload-service"))

    // Spring Boot Test & Web
    testImplementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.kafka:spring-kafka")
    testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")

    testImplementation("io.cucumber:cucumber-java:7.34.8")
    testImplementation("io.cucumber:cucumber-spring:7.34.8")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.34.8")

    testImplementation("org.junit.platform:junit-platform-suite:1.11.4")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.22.0")


    testImplementation("org.awaitility:awaitility:4.3.0")

    testImplementation("org.testcontainers:testcontainers:1.21.4")
    testImplementation("org.testcontainers:junit-jupiter:1.21.4")
    testImplementation("org.testcontainers:postgresql:1.21.4")
    testImplementation("org.testcontainers:mongodb:1.21.4")
    testImplementation("org.testcontainers:kafka:1.21.4")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
