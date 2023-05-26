import org.gradle.kotlin.dsl.compileJava

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.micronaut.application") version "3.7.9"
}

version = "0.1"
group = "com.github.skienex"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-http-validation")
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-jackson-databind")
    implementation("jakarta.annotation:jakarta.annotation-api")
    runtimeOnly("ch.qos.logback:logback-classic")
}

application {
    mainClass.set("com.github.skienex.monopoly.Application")
    applicationDefaultJvmArgs = listOf("--enable-preview")
}
java {
    sourceCompatibility = JavaVersion.toVersion("20")
    targetCompatibility = JavaVersion.toVersion("20")
}

tasks.compileJava {
    options.compilerArgs.add("--enable-preview")
}

graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.github.skienex.*")
    }
}



