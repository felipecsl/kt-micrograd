plugins {
    kotlin("jvm") version "1.9.23"
}

group = "com.felipecsl"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("guru.nidi:graphviz-java:0.18.1")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("org.graalvm.js:js:20.0.0")
    testImplementation(kotlin("test"))
    testImplementation("com.google.truth:truth:1.4.2")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}
