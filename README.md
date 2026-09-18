<p align="center">
  <img src="fabric/src/main/resources/icon.png" alt="OS Cobblemon Library logo" width="180">
</p>

# OS Cobblemon Library

A lightweight shared Cobblemon API and compatibility library for Fabric and NeoForge.

**Author:** LevelsFR

OS Cobblemon Library provides reusable helpers for Pokémon, entities, storage, events, battles, Pokédex data and compatibility boundaries so Cobblemon mods can share a consistent integration layer across Fabric and NeoForge.

## Supported environment

- Minecraft 1.21.1
- Java 21
- Cobblemon 1.8.x (1.8.1 minimum)
- Fabric
- NeoForge

## Project structure

```text
OS-Cobblemon-Library/
├── common/       Shared Cobblemon logic
├── fabric/       Fabric integration
├── neoforge/     NeoForge integration
├── docs/         Scope, roadmap and versioning notes
└── .github/      Build and release workflows
```

Shared functionality belongs in `common` whenever possible. Loader modules should contain only platform-specific wiring or unavoidable loader differences.

## Library areas

The v1.1 library is organized around a small set of Cobblemon-focused domains:

- `pokemon`: species, forms, aspects, marks, types, native size data, persistent Pokémon data and reusable Pokémon property access
- `entity`: read-only `PokemonEntity` bridge helpers for identity, ownership and Cobblemon entity state
- `storage`: party, PC and Pokémon lookup helpers
- `event`: reusable Cobblemon event integration
- `battle`: battle inspection and classification helpers
- `pokedex`: reusable Pokédex queries

The library intentionally avoids gameplay systems that belong inside individual mods.

## Installation

OS Cobblemon Library is a dependency library rather than a standalone gameplay mod.

For normal gameplay, install the JAR matching your loader when another mod declares
OS Cobblemon Library as a dependency.

Requirements for the current compatibility line:

- Minecraft 1.21.1
- Java 21
- Cobblemon 1.8.1 up to, but not including, 1.9.0
- Fabric or NeoForge

## Building

The Gradle wrapper is committed to the repository.

Windows:

```powershell
.\gradlew.bat build
```

Linux / macOS:

```bash
./gradlew build
```

The bootstrap script is kept only for wrapper regeneration or recovery.

If Windows locks an Architectury transform JAR during a parallel build, use the
safe rebuild helper:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\rebuild-windows.ps1
```

Individual loader builds are also available:

```powershell
.\gradlew.bat :fabric:build
.\gradlew.bat :neoforge:build
```

Built JARs are written to:

```text
fabric/build/libs/
neoforge/build/libs/
```

## Versioning

Dependency versions are centralized in `gradle.properties`.

The current Minecraft compatibility line uses:

```text
1.21.1/dev   development snapshots
1.21.1/main  stable releases
```

See [docs/BRANCHING.md](docs/BRANCHING.md) for the multi-version strategy and [docs/RELEASING.md](docs/RELEASING.md) for the release process.

## Getting started

For integration in another mod, see [docs/GETTING_STARTED.md](docs/GETTING_STARTED.md).

For a compact list of the supported public classes, see [docs/API_OVERVIEW.md](docs/API_OVERVIEW.md).

Common implementation examples are collected in [docs/PATTERNS.md](docs/PATTERNS.md).

## Development

Keep additions focused, reusable and Cobblemon-specific. New helpers should represent a real shared need or isolate a meaningful compatibility boundary.

See [docs/POKEMON.md](docs/POKEMON.md), [docs/POKEMON_MATCHER.md](docs/POKEMON_MATCHER.md), [docs/POKEMON_CREATION.md](docs/POKEMON_CREATION.md), [docs/POKEMON_DATA.md](docs/POKEMON_DATA.md), [docs/POKEMON_IVS.md](docs/POKEMON_IVS.md), [docs/POKEMON_ABILITIES.md](docs/POKEMON_ABILITIES.md), [docs/POKEMON_TYPES.md](docs/POKEMON_TYPES.md), [docs/POKEMON_LABELS.md](docs/POKEMON_LABELS.md), [docs/POKEMON_SIZE.md](docs/POKEMON_SIZE.md), [docs/POKEMON_ENTITIES.md](docs/POKEMON_ENTITIES.md), [docs/STORAGE.md](docs/STORAGE.md), [docs/EVENTS.md](docs/EVENTS.md), [docs/BATTLE.md](docs/BATTLE.md) and [docs/POKEDEX.md](docs/POKEDEX.md) for the current shared helper APIs.

For local development consumption, see [docs/LOCAL_MAVEN.md](docs/LOCAL_MAVEN.md).

See [docs/SCOPE.md](docs/SCOPE.md), [docs/API_STABILITY.md](docs/API_STABILITY.md), [docs/COBBLEMON_UPDATES.md](docs/COBBLEMON_UPDATES.md) and [docs/ROADMAP.md](docs/ROADMAP.md) for the current project direction and compatibility policy.

## License

OS Cobblemon Library is proprietary and distributed under the
[OS Cobblemon Library Proprietary API License](LICENSE).

You may build and distribute your own independent mods against the documented
public API. Using the API does **not** require you to publish your mod's source
code.

The Library itself remains **All Rights Reserved**. Redistribution, modified
builds, rebranding and source-code reuse are not permitted unless LevelsFR
grants separate written permission.
