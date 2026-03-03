plugins {
    id("io.spring.dependency-management")
    id("io.freefair.lombok")
    java
}

val springDocVersion = "2.3.0"
val jakartaPersistenceVersion = "3.1.0"
val jakartaServletVersion = "6.0.0"
val httpClientVersion = "5.3.1"
val keyCloakAdminVersion = "23.0.7"

dependencies {
    implementation(project(":modules:shared"))
    implementation("org.springframework:spring-context")
    // Keep modules clean: no web starter here. Add JPA only when the module owns persistence.
    // OAuth2 / Keycloak / JWT
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.security:spring-security-oauth2-jose")
    // Keycloak Admin Client
    implementation("org.keycloak:keycloak-admin-client:$keyCloakAdminVersion")
    // HTTP Client for Keycloak Token Endpoint
    implementation("org.apache.httpcomponents.client5:httpclient5:$httpClientVersion")
    // OpenAPI / Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocVersion")
    implementation("jakarta.validation:jakarta.validation-api")
    compileOnly("jakarta.servlet:jakarta.servlet-api:$jakartaServletVersion")
    implementation("jakarta.persistence:jakarta.persistence-api:$jakartaPersistenceVersion")
    implementation("org.springframework:spring-tx")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
}
