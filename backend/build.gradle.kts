import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.quality.CheckstyleExtension

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

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
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

// SonarCloud config (fill required props in CI)
sonarqube {
    properties {
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.java.coveragePlugin", "jacoco")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            fileTree(".") { include("**/build/reports/jacoco/test/jacocoTestReport.xml") }
                .files.joinToString(",") { it.path },
        )
    }
}
