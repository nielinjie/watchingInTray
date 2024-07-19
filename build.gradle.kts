import org.panteleyev.jpackage.ImageType

plugins {
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    id("org.panteleyev.jpackageplugin") version "1.6.0"
    id("maven-publish")
}

group = "xyz.nietongxue"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    val ktor_version="2.3.12"

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("io.github.sevdokimov.logviewer:log-viewer-spring-boot:1.0.11")

    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-java:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-starter:4.1.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

//publish to local

publishing{
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

}

tasks.withType<Test> {
    useJUnitPlatform()
}

//task to clean dir
tasks.register("cleanDist") {
    doLast {
        file("$buildDir/dist").deleteRecursively()
    }
}


tasks.jpackage {
    dependsOn("bootJar","cleanDist" )

    input  = "$buildDir/libs"
    destination = "$buildDir/dist"

    type = ImageType.APP_IMAGE
    appVersion = "1.0"
    appName = "watching"
    appContent = listOf("./config")
    mainJar = tasks.bootJar.get().archiveFileName.get()


}
