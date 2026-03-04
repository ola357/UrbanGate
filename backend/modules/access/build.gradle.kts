plugins {
    id("io.spring.dependency-management")
    id("io.freefair.lombok")
    java
}

val jakartaPersistenceVersion = "3.1.0"
val jakartaServletVersion = "6.0.0"
val keyCloakAdminVersion = "23.0.7"

dependencies {
    implementation(project(":modules:shared"))

    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-tx")

    implementation("jakarta.validation:jakarta.validation-api")
    compileOnly("jakarta.servlet:jakarta.servlet-api:$jakartaServletVersion")
    implementation("jakarta.persistence:jakarta.persistence-api:$jakartaPersistenceVersion")

    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    implementation("org.springframework.security:spring-security-oauth2-jose")

    implementation("com.fasterxml.jackson.core:jackson-annotations")

    implementation("org.keycloak:keycloak-admin-client:$keyCloakAdminVersion")

    // Keep modules clean: no web starter here. Add JPA only when the module owns persistence.
}
