# OS Cobblemon Library 1.7.0

Status: unreleased development build on `1.21.1/dev`. The latest published GitHub release is 1.6.0.

## Added

- Server-local Spawn Modifier API for adjusting relative weights of already eligible natural Cobblemon spawn details.
- Global, dimension and UUID-attributed player scopes; form-aware filters; deterministic stacking, expiry, generation-safe handles and source cleanup.
- FormData and PokemonProperties overloads for inspecting spawn definitions without constructing a temporary Pokémon.
- `/oscl` command on Fabric and NeoForge to display the installed version from loader metadata.

## Changed

- Upgrade the Gradle wrapper and CI to 9.5.1, including the JUnit platform launcher for tests.
- Extend NeoForge 1.8.4 startup smoke coverage to require registration of the natural spawn-weight influence detector.

## Compatibility and boundaries

Minecraft 1.21.1, Java 21, Fabric and NeoForge, Cobblemon 1.8.2 through 1.8.4 (exclusive upper bound 1.8.5). Native eligibility, rarity buckets, explicit percentage entries and Poké Snack effects remain unchanged. No standalone spawn system is introduced.

## Verification

[Build run 45524029601](https://github.com/LevelsFR/OS-Cobblemon-Library/actions/runs/45524029601) passed for source commit `2183d11f041f9982d0e850dbd029cfb06b158b4c`: both loader builds, common tests, packaged metadata and Maven publication across all three supported Cobblemon versions. NeoForge 1.8.4 startup smoke also passed. Full natural-spawn gameplay acceptance and equivalent Fabric runtime verification are not established by that run.

See [Spawn Modifier API](docs/SPAWNING.md), [source audit](docs/COBBLEMON_1_7_SPAWNING_AUDIT.md) and [complete changelog](CHANGELOG.md).
