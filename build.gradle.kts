plugins {
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    // id("io.sentry.jvm.gradle") version "4.12.0"
    kotlin("plugin.jpa") version "1.9.24"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
}

group = "lt.skafis"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.google.firebase:firebase-admin:9.3.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("ch.qos.logback:logback-classic")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
    // implementation("io.sentry:sentry-spring-boot-starter:6.27.0")

    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs = listOf("-XX:+EnableDynamicAgentLoading", "-Djdk.instrument.traceUsage")
}

// sentry {
//    // Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
//    // This enables source context, allowing you to see your source
//    // code as part of your stack traces in Sentry.
//    includeSourceContext.set(true) // In Kotlin DSL, you use `.set()` for boolean properties.
//    org.set("mb-skafis")
//    projectName.set("java-spring-boot")
//    authToken.set(System.getenv("SENTRY_AUTH_TOKEN"))
// }
