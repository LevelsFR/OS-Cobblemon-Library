# OS Cobblemon Library — project guide

A shared Cobblemon API and compatibility library for Fabric and NeoForge.

Source snapshot: **2026-10-27**. This guide describes the checked-out source; a download page can contain an earlier release.

## Status and official links

- [Source repository](https://github.com/LevelsFR/OS-Cobblemon-Library/tree/1.21.1/dev) (public).
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/os-cobblemon-library) — published project verified under LevelsFR.
- Modrinth: no matching project found in the public LevelsFR project list checked on 2026-10-21.

This guide belongs to `1.21.1/dev`. Stable releases remain on `1.21.1/main`; development APIs and dependency requirements may precede the latest stable release.

## Features in the current source

- Pokémon, species, forms, aspects, abilities, marks, types, EVs and size helpers.
- Immutable display snapshots, entity/ownership inspection and per-tick cached nearest-player distance queries.
- Party, PC storage, event and Pokédex integration.
- Battle inspection and version-gated initial and active-weather bridges.
- Documented public APIs, Maven Local publishing and separate development/stable branches.

## Compatibility and dependencies

| Component | Value |
| --- | --- |
| Latest stable release | `1.5.1` |
| Development version | `1.6.0-SNAPSHOT` |
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
| Fabric | `cobblemon` | `>=1.8.2 <1.8.5` | required |
| NeoForge | `neoforge` | `[21.1.195,)` | required; BOTH |
| NeoForge | `minecraft` | `[1.21.1]` | required; BOTH |
| NeoForge | `cobblemon` | `[1.8.2,1.8.5)` | required; BOTH |

The table reflects loader metadata. Build-time version pins are in [gradle.properties](../gradle.properties). A dependency used only for compilation or optional integration is not automatically required for every installation.

## Installation and first use

OS Cobblemon Library is a dependency library and does not add standalone
gameplay content, commands or a configuration screen. Install the loader JAR
when a consuming mod declares the library as a required dependency. Match the
loader, Minecraft, Java and Cobblemon versions listed in this guide. For an
unreleased source build, publish it to Maven Local or use a separate test
instance; the consuming mod controls its own commands and configuration.

## Persistence boundary

The library has no standalone world or player save format. Its `PokemonData`
helper stores namespaced values on Cobblemon Pokémon; see
[POKEMON_DATA.md](POKEMON_DATA.md) for its read, write and codec behavior.

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

- [Changelog](../CHANGELOG.md) · [README](../README.md)
- [API overview](API_OVERVIEW.md) · [API stability](API_STABILITY.md)
- [Battle](BATTLE.md) · [Events](EVENTS.md) · [Storage](STORAGE.md) · [Pokédex](POKEDEX.md)
- [Cobblemon updates](COBBLEMON_UPDATES.md) · [Branching](BRANCHING.md) · [Roadmap](ROADMAP.md) · [Scope](SCOPE.md)
- [Getting started](GETTING_STARTED.md) · [Local Maven](LOCAL_MAVEN.md) · [Releasing](RELEASING.md)
- [Patterns](PATTERNS.md) · [Pokémon](POKEMON.md) · [Abilities](POKEMON_ABILITIES.md) · [Creation](POKEMON_CREATION.md)
- [Persistent data](POKEMON_DATA.md) · [Entities](POKEMON_ENTITIES.md) · [IVs](POKEMON_IVS.md) · [EVs](POKEMON_EVS.md) · [Labels](POKEMON_LABELS.md)
- [Matcher](POKEMON_MATCHER.md) · [Size](POKEMON_SIZE.md) · [Snapshots](POKEMON_SNAPSHOTS.md) · [Types](POKEMON_TYPES.md)

## Validation and reporting a problem

A source audit, successful compilation, automated checks and in-game validation are different forms of evidence. A successful build does not establish runtime compatibility. Preserve the project-specific remaining release gates.

When reporting a problem, include:

- Exact mod version, Minecraft version, loader and dependency versions.
- Client, integrated server or dedicated-server context.
- Minimal reproduction steps and the relevant log/stack trace.
- Relevant datapack or consuming-mod configuration excerpts, with credentials and personal data removed.

Before a release, compile every affected loader and configured version, inspect packaged metadata/resources, and perform the relevant client and dedicated-server checks. Rendering, interactions, networking and persistence need real runtime validation.

## License and attribution

See [LICENSE](../LICENSE). Retain upstream notices and third-party asset attribution.
