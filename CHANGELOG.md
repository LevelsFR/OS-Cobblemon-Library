# Changelog

All notable public changes to OS Cobblemon Library are documented here.

## 1.7.0 - Natural spawn modifiers and version reporting

### Added

- Add the server-local Spawn Modifier API for changing weights of already eligible natural Cobblemon spawn details.
- Add global, dimension and UUID-attributed player scopes, form-aware data filters, source cleanup, generation-safe handles, tick expiry and deterministic stacking.
- Add source-verified Cobblemon 1.8.2–1.8.4 spawn-engine audit, Java integration examples and seeded weighted-distribution tests.

- Add `/oscl` on Fabric and NeoForge to report the installed version from loader metadata.

### Changed

- Upgrade the Gradle wrapper and CI to 9.5.1 and add the JUnit platform launcher for tests.
- Require natural spawn-weight detector registration in the NeoForge 1.8.4 startup smoke.

### Verification

- [Build run 45524029601](https://github.com/LevelsFR/OS-Cobblemon-Library/actions/runs/45524029601) passed for all three Cobblemon versions, including both loader builds, tests, metadata and Maven publication; NeoForge 1.8.4 startup smoke passed.
- Full natural-spawn gameplay acceptance and equivalent Fabric runtime validation remain unverified by that run. See [v1.7.0 notes](CHANGELOG-v1.7.0.md).

### Preserved

- Keep native eligibility checks, rarity bucket selection, explicit percentage entries and Poké Snack effects unchanged.
- Retain source-compatible Pokémon helper APIs while adding `FormData`/`PokemonProperties` overloads for spawn-definition inspection.

## 1.6.0 - CI, runtime, and API verification hardening (2026-11-20)

### Changed

- Retry Gradle builds and Maven Local publication up to three times for transient HTTP 429 and 5xx responses.
- Build and run common tests across Cobblemon 1.8.2, 1.8.3 and 1.8.4, with Fabric and NeoForge artifacts.

### Added

- Add regression checks for server-stop distance-cache cleanup and the Cobblemon `boot()V` mixin target.
- Run a Cobblemon 1.8.4 NeoForge runtime smoke that requires the ambient-weather hook to initialize successfully.
- Verify both loader JARs embed the project version in CI and the release workflow.
- Test PokemonData defensive copies, typed codecs and malformed values; cover addon namespace parsing.

### Verification limit

- Fabric runtime injection remains unverified: Cobblemon 1.8.4 Kotlin reflection fails before the OSCL mixin target loads in the official-mapped development launch. The target is checked by the ASM regression test across the matrix.
- Live addon species registry lookup and active-Pokémon form resolution require a fully bootstrapped game runtime and are not covered by the headless unit suite.
- This maintenance release changes CI and regression coverage; it adds no public API or gameplay behavior.

## 1.5.2 - Loader version detection (2026-10-29)

### Fixed

- Resolve Fabric versions through the public `net.fabricmc.loader.api.Version` interface.
- Match the single Graal `execute` call in `startBattle` across Fabric and NeoForge relocation namespaces, retaining the required injection check.
- Disable the battle-weather bridge safely when the Cobblemon version cannot be detected, instead of throwing during mixin configuration.

### Release verification

- CurseForge published Fabric and NeoForge builds on 2026-10-29; Modrinth published the Fabric build on 2026-11-10.
- Both published CurseForge JARs match a Gradle 8.14.3 rebuild of source commit 7775087c601de4b41bad1c0186794f8083822695 byte-for-byte.

## 1.5.1 - Cobblemon 1.8.4 compatibility (2026-10-28)

### Changed

- Extend the version-gated initial and active battle-weather bridge to Cobblemon 1.8.4.
- Support Cobblemon `1.8.2+1.21.1` through `1.8.4+1.21.1` on Fabric and NeoForge.
- Build both loaders against all three supported Cobblemon versions in CI.

## 1.5.0 - Cobblemon API and lifecycle improvements (2026-10-27)

### Added

- Add `PokemonEVs` helpers to inspect registered permanent stats, take EV
  snapshots, calculate totals and apply source-aware EV changes.
- Add Java hooks for Cobblemon evolution eligibility checks through
  `CobblemonEventHooks.onEvolutionTested`.
- Accept `ResourceLocation` keys in persistent Pokémon data helpers.
- Add focused common-module tests for defensive snapshots and grouped event
  subscription lifecycle.

### Changed

- Build Fabric and NeoForge against Cobblemon 1.8.2 and 1.8.3 in CI.
- Support Cobblemon `1.8.2+1.21.1` and `1.8.3+1.21.1` on both loaders.

### Fixed

- Clear the nearest-player distance cache automatically when Fabric or
  NeoForge servers stop, preventing cached entity data from carrying over
  between server lifecycles.

### Removed

- None.

## 1.4.1 - Cobblemon 1.8.3 battle weather bridge (2026-10-27)

### Changed

- Extend the version-gated initial and active battle weather bridge to Cobblemon 1.8.3.

### Compatibility

- Support Cobblemon `1.8.2+1.21.1` and `1.8.3+1.21.1` on Fabric and NeoForge.
- Require NeoForge 21.1.195 or newer.

## 1.4.0 - Entity proximity and ambient battle weather updates (2026-10-25)

### Added

- Added `PokemonEntities.nearestPlayerDistanceSquared`, a server-thread-only query that caches each Pokémon's squared distance to its nearest player for the current tick.
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
