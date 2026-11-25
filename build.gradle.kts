plugins {
    java
    `java-library`

    id("dev.architectury.loom") version "1.17.493" apply false
    id("architectury-plugin") version "3.5.170" apply false
    id("com.gradleup.shadow") version "9.5.1" apply false
    id("org.jetbrains.kotlin.jvm") version "2.4.21" apply false
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

    val javaVersion = property("java_version").toString().toInt()

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))
        withSourcesJar()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(javaVersion)
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
