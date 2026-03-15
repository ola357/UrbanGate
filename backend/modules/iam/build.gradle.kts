plugins {
    id("io.spring.dependency-management")
    id("io.freefair.lombok")
    java
}

dependencies {
    implementation(project(":modules:shared"))
    implementation("org.springframework.boot:spring-boot")
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-web")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("jakarta.persistence:jakarta.persistence-api")
    implementation("org.springframework.data:spring-data-jpa")
    // Keep modules clean: no web starter here. Add JPA only when the module owns persistence.
}
