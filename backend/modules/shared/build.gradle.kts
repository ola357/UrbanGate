plugins {
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.lombok)
    java
}

dependencies {
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-web")
    implementation("jakarta.validation:jakarta.validation-api")
}
