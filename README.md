# OS Cobblemon Library

A lightweight shared library for Cobblemon mod development.

OS Cobblemon Library provides reusable Cobblemon-specific utilities and compatibility helpers so mods can share a consistent integration layer across Fabric and NeoForge.

## Supported environment

- Minecraft 1.21.1
- Java 21
- Cobblemon 1.8.1+
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

The initial library is organized around a small set of Cobblemon-focused domains:

- `pokemon`: species, forms, aspects, marks and reusable Pokémon property access
- `storage`: party, PC and Pokémon lookup helpers
- `event`: reusable Cobblemon event integration
- `battle`: battle inspection and classification helpers
- `pokedex`: reusable Pokédex queries

The library intentionally avoids gameplay systems that belong inside individual mods.

## Building

On Windows, bootstrap the Gradle wrapper once:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\bootstrap-gradle.ps1
```

Then build the full project:

```powershell
.\gradlew.bat build
```

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
1.21.1/dev
1.21.1/main
```

See [docs/BRANCHING.md](docs/BRANCHING.md) for the multi-version strategy.

## Development

Keep additions focused, reusable and Cobblemon-specific. New helpers should represent a real shared need or isolate a meaningful compatibility boundary.

See [docs/POKEMON.md](docs/POKEMON.md), [docs/POKEMON_MATCHER.md](docs/POKEMON_MATCHER.md), [docs/POKEMON_CREATION.md](docs/POKEMON_CREATION.md), [docs/POKEMON_IVS.md](docs/POKEMON_IVS.md), [docs/POKEMON_ABILITIES.md](docs/POKEMON_ABILITIES.md), [docs/POKEMON_TYPES.md](docs/POKEMON_TYPES.md), [docs/STORAGE.md](docs/STORAGE.md), [docs/EVENTS.md](docs/EVENTS.md), [docs/BATTLE.md](docs/BATTLE.md) and [docs/POKEDEX.md](docs/POKEDEX.md) for the current shared helper APIs.

For local development consumption, see [docs/LOCAL_MAVEN.md](docs/LOCAL_MAVEN.md).

See [docs/SCOPE.md](docs/SCOPE.md) and [docs/ROADMAP.md](docs/ROADMAP.md) for the current project direction.
