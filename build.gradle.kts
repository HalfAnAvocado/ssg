import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.10"
    kotlin("plugin.serialization") version "1.9.10"
    alias(libs.plugins.detekt)
    application
}

group = "com.marvinelsen"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.handlebars)
    implementation(libs.kaml)
    implementation(libs.slf4j.api)
    implementation(libs.slf4j.nop)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.framework.datatest)
    testImplementation(libs.kotest.assertions.core)
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(20))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "20"
}

application {
    mainClass.set("com.marvinelsen.ssg.MainKt")
}

detekt {
    buildUponDefaultConfig = true
    allRules = true
}