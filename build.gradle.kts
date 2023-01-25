import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.LocalDateTime

val googleGsonVersion: String by project
val kotlinxHtmlJsVersion: String by project
val kotlinxSerializationJsonVersion: String by project

plugins {
    kotlin("jvm") version "1.6.21"
    application
}

group = "org.example"
version = "0.0.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("com.google.code.gson:gson:$googleGsonVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:$kotlinxHtmlJsVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationJsonVersion")

    testImplementation(kotlin("test"))
}

tasks.register("printHelloWorld") {
    println("Hello world!")
}

tasks.register("incrementPatchInVersion") {
    val version = project.version.toString()

    val (major, minor, patch) = version.split(".").map(String::toInt)
    val newVersion = "$major.$minor.${patch + 1}"
    println("old version = $version")
    println("new version = $newVersion")
}

tasks.register("log-meta") {
    shouldRunAfter("build")

    println("Start time: ${LocalDateTime.now()}")
    println("group: ${project.group}")
    println("version: $version")
    println("artifactId: ${project.name}")
}

tasks {
    this.named(name = "jar")
    withType<Jar> {
        manifest {
            attributes("Main-Class" to "MainKt")
        }
    }

    task<Jar>("fatJar") {
        archiveBaseName.set(project.name + "-fat-jar-" + project.version)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        manifest {
            attributes("Main-Class" to "MainKt")
        }
        from(
            configurations.runtimeClasspath.get()
                .map { if (it.isDirectory) it else zipTree(it) }
        )
        with(named("jar").get() as CopySpec)
    }

    build {
        dependsOn("fatJar")
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
    kotlinOptions.languageVersion = "1.6"
    kotlinOptions.apiVersion = "1.6"
    kotlinOptions.freeCompilerArgs = kotlinOptions.freeCompilerArgs + "-Xjvm-default=enable"
}

application {
    mainClass.set("MainKt")
}