plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    id("com.github.johnrengelman.shadow")
}

architectury {
    platformSetupLoomIde()
    fabric()
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
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings(loom.officialMojangMappings())

    modImplementation("net.fabricmc:fabric-loader:${project.property("fabric_loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_api_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("fabric_kotlin_version")}")

    modImplementation("com.cobblemon:fabric:${project.property("cobblemon_version")}") {
        isTransitive = false
    }

    // Cobblemon Showdown runtime dependencies used by development runs.
    modRuntimeOnly("org.graalvm.sdk:graal-sdk:22.3.0")
    modRuntimeOnly("org.graalvm.truffle:truffle-api:22.3.0")
    modRuntimeOnly("org.graalvm.js:js:22.3.0")
    modRuntimeOnly("org.graalvm.regex:regex:22.3.0")
    modRuntimeOnly("com.ibm.icu:icu4j:71.1")
    minecraftServerLibraries("com.ibm.icu:icu4j:71.1")

    implementation(project(":common", configuration = "namedElements"))
    "developmentFabric"(project(":common", configuration = "namedElements"))
    shadowCommon(project(":common", configuration = "transformProductionFabric"))
}

tasks.processResources {
    val props = mapOf(
        "version" to project.version,
        "mod_id" to project.property("mod_id"),
        "mod_name" to project.property("mod_name"),
        "mod_description" to project.property("mod_description"),
        "minecraft_version" to project.property("minecraft_version"),
        "cobblemon_min_version" to project.property("cobblemon_min_version"),
        "fabric_loader_version" to project.property("fabric_loader_version"),
        "java_version" to project.property("java_version")
    )
    inputs.properties(props)
    filesMatching("fabric.mod.json") {
        expand(props)
    }
}

tasks {
    jar {
        archiveBaseName.set("${rootProject.project.property("archives_base_name")}-fabric")
        archiveClassifier.set("dev-slim")
    }

    shadowJar {
        archiveBaseName.set("${rootProject.project.property("archives_base_name")}-fabric")
        archiveClassifier.set("dev-shadow")
        configurations = listOf(shadowCommon)
    }

    remapJar {
        dependsOn(shadowJar)
        inputFile.set(shadowJar.flatMap { it.archiveFile })
        archiveBaseName.set("${rootProject.project.property("archives_base_name")}-fabric")
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("")
    }
}
