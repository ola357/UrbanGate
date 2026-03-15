import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    id("io.spring.dependency-management")
    id("io.freefair.lombok")
    java
}

val jacocoExcludes =
    listOf(
        "com/urbangate/iam/domain/**",
        "com/urbangate/iam/dto/**",
        "com/urbangate/iam/repository/**",
        "com/urbangate/iam/config/**",
        "com/urbangate/iam/keycloak/**",
        "com/urbangate/iam/outbox/OutboxEvent*",
    )

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

    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.springframework.security:spring-security-core")
    testImplementation("org.springframework.security:spring-security-oauth2-jose")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JacocoReport>().configureEach {
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(jacocoExcludes)
                }
            },
        ),
    )
}

tasks.withType<JacocoCoverageVerification>().configureEach {
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(jacocoExcludes)
                }
            },
        ),
    )
}
