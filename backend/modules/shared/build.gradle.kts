plugins {
    id("io.spring.dependency-management")
    id("io.freefair.lombok")
    java
}

dependencies {
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-web")
    implementation("jakarta.validation:jakarta.validation-api")
}
