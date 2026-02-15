plugins {
    id("io.spring.dependency-management")
    id("io.freefair.lombok")
    java
}

dependencies {
    implementation(project(":modules:shared"))
    implementation("org.springframework:spring-context")
    // Keep modules clean: no web starter here. Add JPA only when the module owns persistence.
}
