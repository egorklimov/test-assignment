import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("com.google.cloud.tools.jib") version "3.3.1"
    kotlin("jvm") version "1.8.10"
    kotlin("plugin.spring") version "1.8.10"

    id("org.jlleitschuh.gradle.ktlint") version "11.5.0"
    id("com.ryandens.javaagent-jib") version "0.4.2"
}

group = "org.jetbrains"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.data:spring-data-jdbc")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")

    implementation("org.flywaydb:flyway-core:9.0.0")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.postgresql:postgresql:42.6.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.openapitools:jackson-databind-nullable:0.2.2")

    implementation("io.micrometer:micrometer-registry-prometheus:1.11.5")
    implementation("net.logstash.logback:logstash-logback-encoder:7.3")

    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:1.31.0")
    javaagent("io.opentelemetry.javaagent:opentelemetry-javaagent:1.31.0")

    testImplementation("com.intuit.karate:karate-junit5:1.2.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:testcontainers:1.18.3")
    testImplementation("org.testcontainers:postgresql:1.18.3")
    testImplementation("org.testcontainers:junit-jupiter:1.18.3")
    testImplementation(kotlin("test"))
}

ext {
    set("springCloudVersion", "2022.0.3")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.3")
    }
}

tasks {
    ktlint {
        ignoreFailures.set(false)
        filter {
            exclude { it.file.path.contains("$buildDir/generated/") }
        }
    }

    compileKotlin {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
        // https://docs.spring.io/spring-boot/docs/2.0.x/reference/html/boot-features-kotlin.html#boot-features-kotlin-null-safety
        compilerOptions.freeCompilerArgs.add("-Xjsr305=strict")

        sourceSets {
            test {
                // Load karate files as resources
                resources {
                    srcDir("src/test/kotlin")
                    exclude("**/*.kt")
                }
            }
        }
    }

    jib {
        from {
            platforms {
                platform {
                    architecture = if (System.getProperty("os.arch").equals("aarch64")) "arm64" else "amd64"
                    os = "linux"
                }
            }
        }
        to {
            image = "ghcr.io/egorklimov/test-assignment/cat-api:$version"
            tags = setOf("latest", "$version")
        }
    }

    test {
        useJUnitPlatform()
        // Show test results.
        testLogging {
            exceptionFormat = TestExceptionFormat.FULL
            showStandardStreams = true
            events("passed", "skipped", "failed")
        }
    }
}
