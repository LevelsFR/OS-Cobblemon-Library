plugins {
    java
    `java-library`

    id("dev.architectury.loom") version "1.11-SNAPSHOT" apply false
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")

    group = property("maven_group")!!
    version = property("mod_version")!!

    repositories {
        mavenCentral()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev/")
        maven("https://maven.neoforged.net/releases")
        maven("https://thedarkcolour.github.io/KotlinForForge/")
        maven("https://maven.impactdev.net/repository/development/")
        maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
        withSourcesJar()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(21)
    }
}

tasks.register("publishAllToMavenLocal") {
    group = "publishing"
    description = "Publishes common, Fabric and NeoForge artifacts to Maven Local."
    dependsOn(
        ":common:publishToMavenLocal",
        ":fabric:publishToMavenLocal",
        ":neoforge:publishToMavenLocal"
    )
}
