plugins {
    id("io.spring.dependency-management")
    id("io.freefair.lombok")
    java
}

dependencies {
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-web")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.apache.commons:commons-pool2")
}
