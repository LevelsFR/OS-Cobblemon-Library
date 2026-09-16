# Changelog

All notable public changes to OS Cobblemon Library are documented here.

## Unreleased

## 1.0.1 - 2026-09-17

### Added

- Added the official OS Cobblemon Library logo to Fabric and NeoForge mod listings.

### Changed

- Updated the public mod description to better reflect the library's API and compatibility role.

## 1.0.0 - 2026-09-16

### Added

- Multi-loader Fabric and NeoForge project structure.
- Pokémon identity, matching, species resolution and command-free creation helpers.
- IV, ability, type and data-driven label helpers.
- Native Pokémon size-category, translation-key and safe scale helpers.
- Read-only `PokemonEntity` bridge helpers for Pokémon access, ownership and Cobblemon entity state.
- Party and PC storage queries, matching and counts.
- Cobblemon event subscription and lifecycle helpers.
- Battle classification and event inspection helpers.
- Pokédex species, form, variation and completion queries.
- Local Maven development publishing.
- Public integration, API stability and release documentation.

### Changed

- Runtime metadata explicitly targets Minecraft 1.21.1 and Cobblemon 1.8.x.
- CI builds and Maven publication use the committed Gradle wrapper.
- Architectury build plugins are pinned to reproducible versions.
- Public API boundaries are documented and internal packages are excluded from the supported API.
