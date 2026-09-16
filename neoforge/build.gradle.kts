plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow")
    `maven-publish`
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

loom {
    silentMojangMappingsLicense()
}

val shadowCommon by configurations.creating

configurations {
    compileClasspath.get().extendsFrom(shadowCommon)
    runtimeClasspath.get().extendsFrom(shadowCommon)
}

dependencies {
    minecraft("net.minecraft:minecraft:${project.property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    neoForge("net.neoforged:neoforge:${project.property("neoforge_version")}")

    modImplementation("com.cobblemon:neoforge:${project.property("cobblemon_version")}") {
        isTransitive = false
    }

    // Cobblemon requires Kotlin for Forge on NeoForge development runs.
    implementation("thedarkcolour:kotlinforforge-neoforge:${project.property("kotlin_for_forge_version")}") {
        exclude("net.neoforged.fancymodloader", "loader")
    }

    implementation(project(":common", configuration = "namedElements"))
    "developmentNeoForge"(project(":common", configuration = "namedElements")) {
        isTransitive = false
    }
    shadowCommon(project(":common", configuration = "transformProductionNeoForge"))
}

tasks.processResources {
    filteringCharset = "UTF-8"
    val props = mapOf(
        "version" to project.version,
        "mod_id" to project.property("mod_id"),
        "mod_name" to project.property("mod_name"),
        "mod_description" to project.property("mod_description"),
        "minecraft_version" to project.property("minecraft_version"),
        "cobblemon_min_version" to project.property("cobblemon_min_version"),
        "cobblemon_max_version" to project.property("cobblemon_max_version"),
        "neoforge_min_version" to project.property("neoforge_min_version"),
        "java_version" to project.property("java_version")
    )
    inputs.properties(props)
    filesMatching("META-INF/neoforge.mods.toml") {
        expand(props)
    }
}

tasks {
    jar {
        archiveBaseName.set("${rootProject.property("archives_base_name")}-neoforge")
        archiveClassifier.set("dev-slim")
    }

    shadowJar {
        exclude("fabric.mod.json")
        archiveBaseName.set("${rootProject.property("archives_base_name")}-neoforge")
        archiveClassifier.set("dev-shadow")
        configurations = listOf(shadowCommon)
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        archiveBaseName.set("${rootProject.property("archives_base_name")}-neoforge")
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = rootProject.property("maven_group").toString()
            artifactId = "${rootProject.property("archives_base_name")}-neoforge"
            version = project.version.toString()
            artifact(tasks.named("remapJar"))
        }
    }
}
