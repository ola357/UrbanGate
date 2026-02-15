plugins {
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.lombok)
    java
}

dependencies {
    implementation(project(":modules:shared"))
    implementation("org.springframework:spring-context")
    // Keep modules clean: no web starter here. Add JPA only when the module owns persistence.
}
