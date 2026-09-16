# Local Maven development

OS Cobblemon Library can be published to the local Maven repository for use by
other development projects on the same machine.

## Publish locally

From the library repository:

```powershell
.\gradlew.bat publishAllToMavenLocal
```

This publishes three artifacts:

```text
com.ourstory:os-cobblemon-library-common:1.0.0
com.ourstory:os-cobblemon-library-fabric:1.0.0
com.ourstory:os-cobblemon-library-neoforge:1.0.0
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
dependencies {
    compileOnly("com.ourstory:os-cobblemon-library-common:1.0.0")
}
```

Then use the loader-specific runtime dependency in each platform module.

Fabric:

```kotlin
dependencies {
    modImplementation("com.ourstory:os-cobblemon-library-fabric:1.0.0")
}
```

NeoForge:

```kotlin
dependencies {
    modImplementation("com.ourstory:os-cobblemon-library-neoforge:1.0.0")
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
