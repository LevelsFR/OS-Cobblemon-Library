# Changelog

All notable public changes to OS Cobblemon Library are documented here.

## 1.4.0 - Ambient battle weather updates (2026-10-19)

### Added

- Added `BattleWeatherController.queueAmbientWeather` and `AmbientBattleWeather`
  for version-gated weather updates in an active Cobblemon 1.8.2 battle.
- Ambient weather changes now produce normal Showdown weather records and are
  ignored while a move- or ability-controlled battle weather is active.
- Initial ambient weather is marked separately so it can be updated or cleared
  as the sampled world weather changes.

### Compatibility

- The active-battle bridge is verified only for Cobblemon
  `1.8.2+1.21.1`; other versions fail closed.

## 1.3.1 - Release candidate (2026-10-17)

### Added

- Added a public pre-battle weather initializer for Cobblemon 1.8.2.
- Added a version-gated Showdown bridge that applies the baseline before player
  entry and startup abilities.

## 1.3.0 - 2026-10-11

### Added

- Added Cobblemon 1.8.2 compatibility.
- Added battle-ended hooks for all outcomes and winner/loser inspection for
  completed battles.
- Added a fossil-revival event hook.
- Added pre/post hooks for Pokémon held-item and cosmetic-item swaps.

## 1.2.0 - 2026-10-08

### Added

- Added client-side Pokédex snapshots and client party lookup.
- Added immutable Pokémon display and condition snapshots, slot-preserving party snapshots, and Cobblemon Poké Ball ID/name helpers.
- Added a server Party/PC store-change subscription.
- Added codec-backed persistent Pokémon data reads with defaults and explicit decode errors.

## 1.1.0 - 2026-09-18

### Added

- Added **persistent Pokémon data** for storing custom mod data safely.
- Added reusable **Cobblemon event hooks** for captures, evolutions, battles, gimmicks, healing, fainting, sending, recalling, spawning and Pokédex updates.

## 1.0.1 - 2026-09-17

### Added

- Added the official OS Cobblemon Library logo to Fabric and NeoForge mod listings.

### Changed

- Updated the public mod description to better reflect the library's API and compatibility role.
- Normalized in-game metadata text and resource processing to avoid accented-character encoding issues on Windows builds.
- Clarified the proprietary license so third-party mods may use the documented public API without source-disclosure requirements while the Library itself remains protected.

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
