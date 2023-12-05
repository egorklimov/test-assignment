import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("org.openapi.generator") version "7.0.1"

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

    openApiValidate {
        inputSpec.set("$projectDir/src/main/resources/static/openapi.yaml")
        recommend.set(true)
    }

    this[openApiGenerate.name].dependsOn(ktlintCheck)
    openApiGenerate {
        generatorName.set("kotlin-spring")
        inputSpec.set("$projectDir/src/main/resources/static/openapi.yaml")
        outputDir.set("$buildDir/generated")
        apiPackage.set("$group.generated.api")
        modelPackage.set("$group.generated.api.model")
        configOptions.set(
            mapOf(
                "groupId" to group.toString(),
                "gradleBuildFile" to "false",
                "dateLibrary" to "java8",
                "interfaceOnly" to "true",
                "useTags" to "true",
                "useSpringBoot3" to "true",
                "useBeanValidation" to "true",
                "swaggerDocketConfig" to "false",
                "documentationProvider" to "source",
                "useSwaggerUI" to "true",
                "serializationLibrary" to "jackson"
            )
        )
    }

    task<GenerateTask>("fastAPIGenerate") {
        cleanupOutput.set(true)
        generatorName.set("python-fastapi")
        inputSpec.set("$projectDir/cat-recommender-py/spec/openapi.yaml")
        outputDir.set("$projectDir/cat-recommender-py/cat-recommender")
        configOptions.set(
            mapOf(
                "fastapiImplementationPackage" to "cat_recommender",
                "packageName" to "cat_recommender",
                "packageVersion" to "0.0.1"
            )
        )
    }

    task<GenerateTask>("feignClientGenerate") {
        val group = "org.jetbrains"
        cleanupOutput.set(false)
        generatorName.set("kotlin-spring")
        library.set("spring-cloud")
        inputSpec.set("$projectDir/cat-recommender-py/spec/openapi.yaml")
        outputDir.set("$buildDir/generated")
        apiPackage.set("$group.generated.petshop.client")
        modelPackage.set("$group.generated.petshop.client.model")
        configOptions.set(
            mapOf(
                "groupId" to group.toString(),
                "gradleBuildFile" to "false",
                "dateLibrary" to "java8",
                "interfaceOnly" to "true",
                "useTags" to "true",
                "useSpringBoot3" to "true",
                "useBeanValidation" to "true",
                "swaggerDocketConfig" to "false",
                "documentationProvider" to "source",
                "useSwaggerUI" to "true",
                "useFeignClientUrl" to "false",
                "serializationLibrary" to "jackson"
            )
        )
    }
    this["feignClientGenerate"].dependsOn(ktlintCheck)

    compileKotlin {
        dependsOn("feignClientGenerate")
        dependsOn(openApiGenerate)
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
        // https://docs.spring.io/spring-boot/docs/2.0.x/reference/html/boot-features-kotlin.html#boot-features-kotlin-null-safety
        compilerOptions.freeCompilerArgs.add("-Xjsr305=strict")

        sourceSets {
            main {
                kotlin {
                    srcDirs(
                        "src/main/kotlin/",
                        "$buildDir/generated/src/main/kotlin"
                    )
                }
            }
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
