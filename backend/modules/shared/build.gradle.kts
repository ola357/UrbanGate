plugins {
    id("io.spring.dependency-management")
    id("io.freefair.lombok")
    java
}
val lombokVersion = "1.18.32"
val springDocVersion = "2.3.0"
val jakartaPersistenceVersion = "3.1.0"

dependencies {
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-web")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("jakarta.persistence:jakarta.persistence-api:$jakartaPersistenceVersion")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocVersion")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.apache.commons:commons-pool2")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    compileOnly("org.projectlombok:lombok:$lombokVersion")
}
