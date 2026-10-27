plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
    `maven-publish`
}

architectury {
    common("fabric", "neoforge")
}

loom {
    silentMojangMappingsLicense()
}

dependencies {
    compileOnly("net.fabricmc:sponge-mixin:0.15.4+mixin.0.8.7")

    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())

    // Loader-neutral Cobblemon classes for common code.
    modImplementation("com.cobblemon:mod:${property("cobblemon_version")}") {
        isTransitive = false
    }

    // Cobblemon's public API is implemented in Kotlin. Expose the stdlib to
    // consumers' compile classpaths without bundling it into this library JAR.
    api("org.jetbrains.kotlin:kotlin-stdlib:${property("kotlin_version")}")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = rootProject.property("maven_group").toString()
            artifactId = "${rootProject.property("archives_base_name")}-common"
            version = project.version.toString()
            from(components["java"])
        }
    }
}
