val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.31"
    kotlin("kapt") version "1.5.31"
    //   id("com.google.protobuf") version "0.8.17"
}

group = "dev.arpan"
version = "0.0.1"
application {
    mainClass.set("dev.arpan.ApplicationKt")
}

repositories {
    mavenCentral()
}


dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-websockets:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    implementation("mysql:mysql-connector-java:8.0.26")

    implementation("com.squareup.okio:okio:3.0.0-alpha.10")
    implementation("com.squareup.moshi:moshi:1.12.0")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:1.12.0")
    implementation("com.squareup.moshi:moshi-adapters:1.12.0")

}
