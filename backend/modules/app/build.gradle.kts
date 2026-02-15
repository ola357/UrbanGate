plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.lombok)
    alias(libs.plugins.spotbugs)
}

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

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.postgresql:postgresql")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
