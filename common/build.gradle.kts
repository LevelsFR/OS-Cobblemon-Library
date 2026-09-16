plugins {
    id("dev.architectury.loom")
    id("architectury-plugin")
}

architectury {
    common("fabric", "neoforge")
}

loom {
    silentMojangMappingsLicense()
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings(loom.officialMojangMappings())

    // Loader-neutral Cobblemon classes for common code.
    modImplementation("com.cobblemon:mod:${property("cobblemon_version")}") {
        isTransitive = false
    }

    // Cobblemon's public API is implemented in Kotlin. Keep the stdlib on the
    // common compile classpath without bundling it into the library JAR.
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.2.20")
}
