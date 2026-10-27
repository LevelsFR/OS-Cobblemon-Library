# Local Maven development

OS Cobblemon Library can be published to the local Maven repository for use by
other development projects on the same machine.

Development work normally uses snapshot versions. A release-ready commit may
temporarily use its final version before promotion. Stable releases use the
corresponding non-snapshot semantic version.

The latest stable release is `1.4.1`, targeting Minecraft 1.21.1 and Cobblemon
1.8.2 through 1.8.3. The development branch resumes at `1.5.0-SNAPSHOT`. A
local Maven publication installs artifacts on this machine; it does not
distribute them publicly.

## Publish locally

From the library repository:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\publish-local.ps1
```

The script reads the active version from `gradle.properties`, publishes all
three artifacts and prints their Maven coordinates.

The equivalent direct Gradle task is:

```powershell
.\gradlew.bat publishAllToMavenLocal
```

For library version 1.4.1, this publishes:

```text
com.ourstory:os-cobblemon-library-common:1.4.1
com.ourstory:os-cobblemon-library-fabric:1.4.1
com.ourstory:os-cobblemon-library-neoforge:1.4.1
```

The artifacts are stored in the standard Maven Local repository.

## Consume from another multi-loader project

Add Maven Local to the consuming project's repositories:

```kotlin
repositories {
    mavenLocal()
}
```

The shared/common module can compile against the common artifact:

```kotlin
val osCobblemonLibraryVersion = "1.4.1"

dependencies {
    compileOnly(
        "com.ourstory:os-cobblemon-library-common:$osCobblemonLibraryVersion"
    )
}
```

Then use the loader-specific runtime dependency in each platform module.

Fabric:

```kotlin
dependencies {
    modImplementation(
        "com.ourstory:os-cobblemon-library-fabric:$osCobblemonLibraryVersion"
    )
}
```

NeoForge:

```kotlin
dependencies {
    modImplementation(
        "com.ourstory:os-cobblemon-library-neoforge:$osCobblemonLibraryVersion"
    )
}
```

Consuming mods should also declare OS Cobblemon Library as a required runtime
dependency in their loader metadata when they use its API.

## Updating a local build

After changing the library, run the publish command again:

```powershell
.\gradlew.bat publishAllToMavenLocal
```

Gradle will replace the local artifacts for the same development version.
For published releases, use a new semantic version instead of overwriting an
already released version.
