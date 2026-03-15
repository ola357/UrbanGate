plugins {
    id("io.spring.dependency-management")
    id("io.freefair.lombok")
    java
}

dependencies {
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-web")
    implementation("org.springframework.security:spring-security-core")
    implementation("org.springframework.security:spring-security-oauth2-jose")
    implementation("jakarta.validation:jakarta.validation-api")
}
