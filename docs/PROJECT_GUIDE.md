# OS Cobblemon Library — project guide

A shared Cobblemon API and compatibility library for Fabric and NeoForge.

Source snapshot: **2026-11-20**. This guide describes the checked-out source; download pages carry the latest stable release.

## Status and official links

- [Source repository](https://github.com/LevelsFR/OS-Cobblemon-Library/tree/1.21.1/dev) (public).
- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/os-cobblemon-library) — published project verified under LevelsFR.
- Modrinth: [OS Cobblemon Library v1.5.2](https://modrinth.com/mod/os-cobblemon-library/version/1.5.2).

Stable releases remain on `1.21.1/main`; `1.21.1/dev` continues at `1.6.0-SNAPSHOT` and may include APIs ahead of the latest stable release.

## Features in the current source

- Pokémon, species, forms, aspects, abilities, marks, types, EVs and size helpers.
- Immutable display snapshots, entity/ownership inspection and per-tick cached nearest-player distance queries.
- Party, PC storage, event and Pokédex integration.
- Battle inspection and version-gated initial and active-weather bridges.
- Documented public APIs, Maven Local publishing and separate development/stable branches.

## Compatibility and dependencies

| Component | Value |
| --- | --- |
| Latest stable release | `1.5.2` |
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

## v1.5.2 release provenance

The CurseForge Fabric and NeoForge files (IDs `9363559` and `9363562`) and the Modrinth Fabric version (`hT3yLdDC`) are public releases. Rebuilding source commit `7775087c601de4b41bad1c0186794f8083822695` with Gradle 8.14.3 produced byte-identical CurseForge JARs:

- Fabric SHA-256: `1CF0B5F2FDD8661F0D8532641727B1F7D89BD5E379D7F6FE78152AC70217D1A3`
- NeoForge SHA-256: `412139525C5DA38ADEC7676094F06AD437288A70ED291CF843AA5EF7B57B450A`

The GitHub release list still ends at v1.5.0. This synchronization creates no v1.5.2 tag or GitHub binary release because pushing a v* tag triggers the repository release workflow.

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
