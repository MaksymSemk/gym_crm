plugins {
    id("org.springframework.boot") version "4.1.0" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.google.protobuf") version "0.9.6" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}