# OS Cobblemon Library

Shared Cobblemon development library for Our Story mods.

The project centralizes repeated Cobblemon integration so OS mods can share stable helpers instead of reimplementing Pokémon identity, storage, events, battles and Pokédex access in every project.

## Target

- Minecraft 1.21.1
- Java 21
- Cobblemon 1.8.1+
- Fabric
- NeoForge

## Structure

```text
OS-Cobblemon-Library/
├── common/       Shared Cobblemon logic
├── fabric/       Fabric entrypoint and loader integration
├── neoforge/     NeoForge entrypoint and loader integration
├── docs/         Scope, roadmap and branch policy
├── .github/      CI and release workflows
└── AGENTS.md     Rules for Codex and coding agents
```

## First build on Windows

Run:

```bat
FIRST_BUILD_WINDOWS.bat
```

The script downloads Gradle 8.14.3, verifies its SHA-256 checksum, generates the Gradle wrapper and builds all three modules.

After the first run, normal commands are available:

```bat
gradlew.bat build
gradlew.bat :fabric:build
gradlew.bat :neoforge:build
```

Final loader JARs are created under:

```text
fabric/build/libs/
neoforge/build/libs/
```

## Development rule

Put code in `common` unless it truly depends on Fabric or NeoForge.

The initial shared domains are:

- `pokemon`
- `storage`
- `event`
- `battle`
- `pokedex`

Read `AGENTS.md` and `docs/SCOPE.md` before adding shared functionality.

## Git branches

Current compatibility line:

```text
1.21.1/dev
1.21.1/main
```

See `docs/BRANCHING.md` for the future multi-version model.

## Version updates

Dependency versions are intentionally centralized in `gradle.properties`. When Cobblemon updates on Minecraft 1.21.1, start by changing `cobblemon_version` and running both loader builds.

## License

The project metadata currently marks the mod as All Rights Reserved. Decide the repository source license before public release and add a matching `LICENSE` file.
