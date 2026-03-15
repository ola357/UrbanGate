import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("io.freefair.lombok")
    id("com.github.spotbugs")
}

val jacocoExcludes =
    listOf(
        "com/urbangate/app/security/**",
        "com/urbangate/app/config/**",
        "com/urbangate/app/UrbanGateApplication*",
        "com/urbangate/app/web/OnboardingController*",
        "com/urbangate/app/web/PasswordResetController*",
    )

dependencies {
    implementation(project(":modules:shared"))
    implementation(project(":modules:iam"))
    implementation(project(":modules:property"))
    implementation(project(":modules:access"))
    implementation(project(":modules:billing"))
    implementation(project(":modules:wallet"))
    implementation(project(":modules:directory"))
    implementation(project(":modules:notifications"))
    implementation(project(":modules:audit"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core:11.20.3")
    implementation("org.flywaydb:flyway-database-postgresql:11.20.3")
    runtimeOnly("org.postgresql:postgresql")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.h2database:h2")
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
