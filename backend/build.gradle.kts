plugins {
    id("org.springframework.boot") version "3.5.10" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.diffplug.spotless") version "6.25.0"
    id("com.github.spotbugs") version "6.0.26" apply false
    id("org.sonarqube") version "7.1.0.6387"
    id("io.freefair.lombok") version "8.7.1" apply false
    jacoco
    checkstyle
}

allprojects {
    group = "com.urbangate"
    version = "0.0.1-SNAPSHOT"

    repositories { mavenCentral() }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "checkstyle")
    apply(plugin = "jacoco")
    apply(plugin = "io.spring.dependency-management")

    extensions.configure<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:3.5.10")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    extensions.configure<CheckstyleExtension> {
        toolVersion = "10.12.5"
        configDirectory.set(rootProject.file("config/checkstyle"))
        isIgnoreFailures = false
    }

    tasks.withType<JacocoReport> {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }

        classDirectories.setFrom(
            files(
                classDirectories.files.map {
                    fileTree(it) {
                        include(
                            "**/web/**",
                        )
                        exclude(
                            "**/dto/**",
                            "**/entity/**",
                            "**/model/**",
                            "**/util/**",
                            "**/config/**",
                            "**/configuration/**",
                            "**/repository/**",
                            "**/exceptions/**",
                            "**/enums/**",
                            "**/mapper/**",
                            "**/*Application.class",
                            "**/*Request.class",
                            "**/*Response.class",
                        )
                    }
                },
            ),
        )
    }
    tasks.withType<JacocoCoverageVerification>().named("jacocoTestCoverageVerification") {
        dependsOn(tasks.named<Test>("test"))
        dependsOn(tasks.named("jacocoTestReport"))

        classDirectories.setFrom(
            files(
                classDirectories.files.map {
                    fileTree(it) {
                        include(
                            "**/web/**",
                        )
                        exclude(
                            "**/dto/**",
                            "**/entity/**",
                            "**/model/**",
                            "**/config/**",
                            "**/util/**",
                            "**/configuration/**",
                            "**/repository/**",
                            "**/exceptions/**",
                            "**/enums/**",
                            "**/mapper/**",
                            "**/*Application.class",
                            "**/*Request.class",
                            "**/*Response.class",
                        )
                    }
                },
            ),
        )

        violationRules {
            rule {
                element = "BUNDLE"
                limit {
                    counter = "LINE"
                    value = "COVEREDRATIO"
                    minimum = "0.80".toBigDecimal()
                }
            }
        }
    }

    tasks.named("check") {
        dependsOn("jacocoTestCoverageVerification")
    }

    tasks.withType<Test>().configureEach {
        finalizedBy(tasks.named("jacocoTestReport"))
    }
}

// Spotless formatting
spotless {
    java {
        target("**/*.java")
        googleJavaFormat("1.23.0")
        trimTrailingWhitespace()
        endWithNewline()
        licenseHeader("// Copyright (c) UrbanGate\n")
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint()
    }
    format("misc") {
        target("**/*.md", "**/*.yml", "**/*.yaml", "**/*.json", "**/*.properties", "**/.gitignore")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks.register("format") {
    group = "verification"
    description = "Applies Spotless formatting"
    dependsOn("spotlessApply")
}

tasks.register("lint") {
    group = "verification"
    description = "Runs style and static analysis checks"
    dependsOn("spotlessCheck", "checkstyleMain", "checkstyleTest")
}



sonar {
    properties {

        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.coverage.jacoco.xmlReportPaths", "$buildDir/reports/jacoco/test/jacocoTestReport.xml")

        property(
            "sonar.coverage.exclusions",
            """
            **/dto/**,
            **/entity/**,
            **/model/**,
            **/config/**,
            "**/util/**",
            **/configuration/**,
            **/repository/**,
            **/exceptions/**,
            **/enums/**,
            **/service/**,
            **/mapper/**,
            **/*Application.java,
            **/*Request.java,
            **/*Response.java
            """.trimIndent(),
        )

        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            subprojects.joinToString(",") { subproject ->
                "${subproject.buildDir}/reports/jacoco/test/jacocoTestReport.xml"
            },
        )
    }
}
