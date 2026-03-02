plugins {
    id("io.spring.dependency-management")
    id("io.freefair.lombok")
    java
}

dependencies {
    implementation(project(":modules:shared"))
    implementation("org.springframework:spring-context")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

    implementation("org.springframework:spring-tx")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.security:spring-security-oauth2-jose")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    implementation("org.keycloak:keycloak-admin-client:23.0.7")
    // Keep modules clean: no web starter here. Add JPA only when the module owns persistence.
}
