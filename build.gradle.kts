plugins {
    kotlin("jvm") version "1.6.21"
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.21")
    implementation("com.google.code.gson:gson:2.8.9")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("com.example.StatusCheckerKt")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.example.StatusCheckerKt"
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
