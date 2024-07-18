import org.panteleyev.jpackage.ImageType

plugins {
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    id("org.panteleyev.jpackageplugin") version "1.6.0"
}

group = "com.hykj"
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
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("io.github.sevdokimov.logviewer:log-viewer-spring-boot:1.0.11")

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
}

tasks.jpackage {
    dependsOn("bootJar" )

    input  = "$buildDir/libs"
    destination = "$buildDir/dist"

    type = ImageType.APP_IMAGE
    appVersion = "1.0"
    mainJar = tasks.bootJar.get().archiveFileName.get()


}
