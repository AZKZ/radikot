import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

group = "com.azkz"
version = "0.4.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // HTTPクライアント関係
    implementation("io.ktor:ktor-client-core:2.0.2")
    implementation("io.ktor:ktor-client-cio:2.0.2")

    // ログ関係
    implementation("io.ktor:ktor-client-logging:2.0.2")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.20")
    implementation("org.slf4j:slf4j-api:1.7.29")
    implementation("ch.qos.logback:logback-core:1.2.11")
    implementation("ch.qos.logback:logback-classic:1.2.11")

    // 通知関係
    implementation("com.slack.api:slack-api-client:1.12.1")

    // JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.3.2")

    // 日時型
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.3")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.shadowJar {
    manifest {
        attributes(Pair("Main-Class", "MainKt"))
    }
}

application {
    mainClass.set("MainKt")
}