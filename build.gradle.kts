import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    application
    id("org.openjfx.javafxplugin") version "0.0.8"
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
}
group = "me.tsnan"
version = "1.0-SNAPSHOT"

javafx {
    version = "11.0.2"
    modules = listOf("javafx.controls", "javafx.graphics")
}

repositories {
    mavenCentral()
    jcenter()
}
dependencies {
    implementation("no.tornado:tornadofx:+")
    implementation("com.jcraft:jsch:0.1.55")
    implementation("org.jetbrains.exposed:exposed-core:0.28.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.28.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.28.1")
    implementation("org.xerial:sqlite-jdbc:3.30.1")
    testImplementation(kotlin("test-junit"))
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
