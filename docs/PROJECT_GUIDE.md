# OS Cobblemon Library — project guide

A shared Cobblemon API and compatibility library for Fabric and NeoForge.

Source and publication snapshot: **2026-10-25**. This guide describes the checked-out source; a download page can contain an earlier release.

## Status and official links

- [Source repository](https://github.com/LevelsFR/OS-Cobblemon-Library/tree/1.21.1/dev) (public).
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/os-cobblemon-library) — published project verified under LevelsFR.
- Modrinth: no matching project found in the public LevelsFR project list checked on 2026-10-21.

This guide belongs to `1.21.1/dev`. Stable releases remain on `1.21.1/main`; development APIs and dependency requirements may precede the latest stable release.

## Features in the current source

- Pokémon, species, forms, aspects, abilities, marks, types and size helpers.
- Immutable display snapshots, entity/ownership inspection and per-tick cached nearest-player distance queries.
- Party, PC storage, event and Pokédex integration.
- Battle inspection and version-gated initial and active-weather bridges.
- Documented public APIs, Maven Local publishing and separate development/stable branches.

## Compatibility and dependencies

| Component | Value |
| --- | --- |
| Library version | `1.4.1` |
| Minecraft baseline | `1.21.1` |
| JDK | `21` |
| Active branch | `1.21.1/dev` |

| Loader | Dependency | Declared constraint | Requirement |
| --- | --- | --- | --- |
| Fabric | `fabricloader` | `>=0.17.2` | required |
| Fabric | `fabric-api` | `*` | required |
| Fabric | `fabric-language-kotlin` | `*` | required |
| Fabric | `minecraft` | `1.21.1` | required |
| Fabric | `java` | `>=21` | required |
| Fabric | `cobblemon` | `>=1.8.2 <1.8.4` | required |
| NeoForge | `neoforge` | `[21.1.195,)` | required; BOTH |
| NeoForge | `minecraft` | `[1.21.1]` | required; BOTH |
| NeoForge | `cobblemon` | `[1.8.2,1.8.4)` | required; BOTH |

The table reflects loader metadata. Build-time version pins are in [gradle.properties](../gradle.properties). A dependency used only for compilation or optional integration is not automatically required for every installation.

## Installation and first use

1. Choose a released JAR matching both the Minecraft version and loader, when this project has a release page.
2. Install the required dependencies declared for that loader. A source-only development version may require locally built dependencies.
3. Use a separate development instance for unreleased builds. Keep client-side integrations on the correct side and follow the main README for server installation.
4. Start the game/server once to create configuration, then use the project settings or documented commands.

## Configuration and saved data

Configuration and persistence entry points are listed in the source map below and in the existing project documentation.

Keep world data and player data with the matching world backup. Use the mod-specific reload/configuration route documented in the main README; changing an arbitrary file does not imply live reloading. Do not copy development-world data into the source repository.

## Source map

| Directory | Responsibility |
| --- | --- |
| [common](../common) | Shared behavior and resources; loader-independent contracts where the architecture provides them. |
| [fabric](../fabric) | Fabric entrypoints, metadata and platform integration. |
| [neoforge](../neoforge) | NeoForge entrypoints, metadata and platform integration. |
| [gradle](../gradle) | Committed wrapper and build configuration. |
| [scripts](../scripts) | Project build, validation or publishing helpers. |

Metadata: [fabric/src/main/resources/fabric.mod.json](../fabric/src/main/resources/fabric.mod.json), [neoforge/src/main/resources/META-INF/neoforge.mods.toml](../neoforge/src/main/resources/META-INF/neoforge.mods.toml).

## Build from source

Run commands from the repository root using a matching JDK and the committed Gradle wrapper.

```powershell
.\gradlew.bat :fabric:build :neoforge:build
```

Loader JARs are normally written under the corresponding `build/libs` directory. Aggregate projects document their collected artifact layout in the main README or build helper.

## Documentation index

- [CHANGELOG.md](../CHANGELOG.md)
- [README.md](../README.md)
- [docs/API_OVERVIEW.md](../docs/API_OVERVIEW.md)
- [docs/API_STABILITY.md](../docs/API_STABILITY.md)
- [docs/BATTLE.md](../docs/BATTLE.md)
- [docs/BRANCHING.md](../docs/BRANCHING.md)
- [docs/COBBLEMON_UPDATES.md](../docs/COBBLEMON_UPDATES.md)
- [docs/EVENTS.md](../docs/EVENTS.md)
- [docs/GETTING_STARTED.md](../docs/GETTING_STARTED.md)
- [docs/LOCAL_MAVEN.md](../docs/LOCAL_MAVEN.md)
- [docs/PATTERNS.md](../docs/PATTERNS.md)
- [docs/POKEDEX.md](../docs/POKEDEX.md)
- [docs/POKEMON.md](../docs/POKEMON.md)
- [docs/POKEMON_ABILITIES.md](../docs/POKEMON_ABILITIES.md)
- [docs/POKEMON_CREATION.md](../docs/POKEMON_CREATION.md)
- [docs/POKEMON_DATA.md](../docs/POKEMON_DATA.md)
- [docs/POKEMON_ENTITIES.md](../docs/POKEMON_ENTITIES.md)
- [docs/POKEMON_IVS.md](../docs/POKEMON_IVS.md)
- [docs/POKEMON_LABELS.md](../docs/POKEMON_LABELS.md)
- [docs/POKEMON_MATCHER.md](../docs/POKEMON_MATCHER.md)
- [docs/POKEMON_SIZE.md](../docs/POKEMON_SIZE.md)
- [docs/POKEMON_SNAPSHOTS.md](../docs/POKEMON_SNAPSHOTS.md)
- [docs/POKEMON_TYPES.md](../docs/POKEMON_TYPES.md)
- [docs/RELEASING.md](../docs/RELEASING.md)
- [docs/ROADMAP.md](../docs/ROADMAP.md)
- [docs/SCOPE.md](../docs/SCOPE.md)
- [docs/STORAGE.md](../docs/STORAGE.md)

## Validation and reporting a problem

A source audit, successful compilation, automated checks and in-game validation are different forms of evidence. This documentation update alone does not establish runtime compatibility. Preserve the project-specific remaining release gates.

When reporting a problem, include:

- Exact mod version, Minecraft version, loader and dependency versions.
- Client, integrated server or dedicated-server context.
- Minimal reproduction steps and the relevant log/stack trace.
- Configuration or datapack excerpts required to reproduce it, with credentials and personal data removed.

Before a release, compile every affected loader and configured version, inspect packaged metadata/resources, and perform the relevant client and dedicated-server checks. Rendering, interactions, networking and persistence need real runtime validation.

## License and attribution

See [LICENSE](../LICENSE). Retain upstream notices and third-party asset attribution.
