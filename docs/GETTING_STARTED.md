# Getting started

This guide shows the intended integration pattern for a multi-loader mod using
OS Cobblemon Library.

## 1. Choose a library version

For local development on the current development branch:

```text
1.1.0-SNAPSHOT
```

The current stable release is `1.0.0`.

## 2. Publish a local development build

From the OS Cobblemon Library repository:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\publish-local.ps1
```

This publishes the common, Fabric and NeoForge artifacts to Maven Local.

## 3. Add Maven Local

In the consuming project:

```kotlin
repositories {
    mavenLocal()
}
```

For public releases, replace Maven Local with the repository that hosts the
published artifacts.

## 4. Common module

Compile common code against the common artifact:

```kotlin
val osCobblemonLibraryVersion = "1.1.0-SNAPSHOT"

dependencies {
    compileOnly(
        "com.ourstory:os-cobblemon-library-common:$osCobblemonLibraryVersion"
    )
}
```

This lets shared source code import classes such as:

```java
import com.ourstory.oscobblemon.pokemon.PokemonMatcher;
import com.ourstory.oscobblemon.storage.PokemonStorage;
```

## 5. Fabric module

Add the Fabric runtime artifact:

```kotlin
dependencies {
    modImplementation(
        "com.ourstory:os-cobblemon-library-fabric:$osCobblemonLibraryVersion"
    )
}
```

The consuming Fabric mod should also declare `os_cobblemon_library` as a
required dependency in its `fabric.mod.json`:

```json
"depends": {
  "os_cobblemon_library": ">=1.0.0"
}
```

Keep the rest of the consuming mod's existing dependencies alongside it.

## 6. NeoForge module

Add the NeoForge runtime artifact:

```kotlin
dependencies {
    modImplementation(
        "com.ourstory:os-cobblemon-library-neoforge:$osCobblemonLibraryVersion"
    )
}
```

The consuming NeoForge mod should also declare `os_cobblemon_library` as a
required dependency in its mod metadata:

```toml
[[dependencies.your_mod_id]]
modId="os_cobblemon_library"
type="required"
versionRange="[1.0.0,)"
ordering="AFTER"
side="BOTH"
```

Replace `your_mod_id` with the consuming mod's real mod ID.

## 7. Example use

Find the first owned shiny Alpha Pikachu:

```java
PokemonMatcher matcher = PokemonMatcher
        .fromProperties("species=pikachu shiny=true")
        .alpha(true);

Optional<Pokemon> result =
        PokemonStorage.findFirstOwned(player, matcher);
```

Count owned Pokémon with a configured label:

```java
PokemonMatcher matcher = PokemonMatcher
        .any()
        .label("legendary");

int count = PokemonStorage.countOwned(player, matcher);
```

Subscribe to captures and clean up later:

```java
EventSubscriptionGroup subscriptions = new EventSubscriptionGroup();

subscriptions.track(
        CobblemonEventHooks.onCapture(this::handleCapture)
);

// When the feature is disabled or reloaded:
subscriptions.close();
```

## 8. Keep the dependency boundary clean

Consuming mods should use public packages under:

```text
com.ourstory.oscobblemon
```

but must not depend on:

```text
com.ourstory.oscobblemon.internal
```

When a new repeated Cobblemon integration pattern appears across multiple mods,
prefer adding a focused helper to the library instead of copying the same logic
again.
