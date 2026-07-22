plugins {
    java
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "gym_crm"

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
    implementation("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.22.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:4.1.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:4.1.0")
    implementation("org.springframework.boot:spring-boot-starter-aop:4.0.0-M2")
    runtimeOnly("org.postgresql:postgresql:42.7.13")
    implementation("org.springframework.boot:spring-boot-starter-validation:4.1.0")
    implementation("org.springframework.boot:spring-boot-starter-web:4.1.0")

    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test:4.1.0")
    testImplementation("org.springframework.security:spring-security-test")
    implementation("org.springframework.boot:spring-boot-starter-json")

    testImplementation("org.testcontainers:testcontainers:1.21.4")
    testImplementation("org.testcontainers:junit-jupiter:1.21.4")

    testImplementation("org.testcontainers:postgresql:1.21.4")

    implementation("org.mapstruct:mapstruct:1.7.0.Beta2")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.7.0.Beta2")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
