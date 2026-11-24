# Cobblemon spawn engine audit for OSCL 1.7

This audit records the source contract used by the Spawn Modifier API. The inspected artifacts were the local Cobblemon `*-sources.jar` files for `1.8.2+1.21.1`, `1.8.3+1.21.1` and `1.8.4+1.21.1`; `1.8.4` was the compile reference. The project compiles common code against the corresponding `com.cobblemon:mod` artifact and loader modules against the matching Fabric or NeoForge Cobblemon artifacts.

## Natural attempt and influence flow

1. Cobblemon's `PlayerSpawner.tick()` creates a `SpawnCause` with itself as the spawner and the player resolved from its UUID as the cause entity. Its world-spawn path calls `runForArea`.
2. `Spawner.calculateSpawnActionsForArea()` first constrains the area, checks loaded chunks and native population limits, and only then generates a `SpawningZone` and resolves spawnable positions.
3. `CobblemonSpawningZoneGenerator.generate()` calls every `SpawningInfluenceDetector.detectFromBlock()` while it scans the already-needed zone blocks, then calls `detectFromInput(spawner, input)` once for the zone.
4. `SpawningZone` separates unconditional and position-conditional influences. `AreaSpawnablePositionResolver.resolve()` attaches applicable influences to each native `SpawnablePosition`.
5. `Spawner.getMatchingSpawns(bucket, position)` obtains details from the native `SpawnPool`, checks `SpawnDetail.isSatisfiedBy(position)`, and only then permits the separate native influence injection hook. OSCL does not inject details or change this predicate.
6. `SpawnablePosition.getWeight(detail)` starts with `detail.weight`, applies position influences and `SpawnCause`, and then applies `detail.weightMultipliers`. `SpawningInfluence.affectWeight()` is documented as running after typical native weight multipliers.
7. `FlatSelector` uses each eligible detail's highest weight over its eligible positions to perform weighted selection. It first handles details with positive explicit `percentage`; OSCL leaves those details' weights unchanged.
8. `Spawner.chooseBucket()` applies bucket influences and the native bucket event before detail selection. OSCL does not override bucket methods, so native bucket weights and `SpawnBucketChosenEvent` remain in control.

The detector returns an empty list from `detectFromBlock()` and only creates an influence for a `PlayerSpawner` in `detectFromInput()`. It uses the spawner UUID for player scope and `input.world.dimension()` for dimension scope. No nearest-player search or world scan is introduced.

## Poké Snacks and other spawners

`PokeSnackSpawnerFactory.create()` creates `PokeSnackBlockEntity.Spawner`, a `FixedAreaSpawner` with Cobblemon's configured `pokeSnackBuckets`. Its native influence builders add `BucketNormalizingInfluence` for rarity-bucket bait and `SpawnBaitInfluence`, then enabled spawn rules. `PokeSnackBlockEntity.attemptSpawn()` uses that spawner before posting the snack spawn pre-event and completing the selected action.

Because this spawner is not a `PlayerSpawner`, the OSCL detector returns no influence. Snack bait values, bucket normalization and native snack events are unchanged. Fishing and other non-player spawners likewise remain outside the v1.7 API.

## Pokémon properties, forms and labels

`PokemonSpawnDetail.pokemon` and each `PokemonHerdSpawnDetail.Herdable.pokemon` are `PokemonProperties`. The spawn detail exposes its bucket, base weight and percentage. Cobblemon properties expose the resolved species string, optional form string and aspects. `PokemonSpeciesResolver` resolves addon namespaces. `Species` exposes registered forms; `FormData` exposes form-only Showdown IDs, types and native labels.

OSCL translates these values into a small per-zone candidate snapshot and never creates a full `Pokemon` to evaluate the filter. An explicit registered form yields one form profile. If a form is absent or unresolved (including an unknown explicit form ID), the candidate carries every registered form. Positive type/form/label criteria must hold for each possible form; a forbidden form or label in the possibilities rejects the modifier match. This avoids using base species types as a substitute for regional form types. Aspect criteria use the aspect set present on Cobblemon's spawn properties.

`PokemonMatcher` is intentionally not used in the hot path. It matches `Pokemon` using `PokemonProperties.matches(Pokemon)`, marks, size and current-form helpers. Turning a spawn detail into a Pokémon only to call it would add random initialization and per-candidate work. OSCL reuses the stable `PokemonSpeciesResolver`, `PokemonIdentity`, `PokemonTypes` and `PokemonLabels` utilities through property/form overloads instead.

Herd details can represent several Pokémon properties under one spawn weight. OSCL applies a filter to a herd detail only when every possible member matches; it does not let one matching member boost the whole mixed herd.

## Version comparison

The following spawning files have identical SHA-256 source bytes in all three target artifacts:

| Source file | SHA-256 |
|---|---|
| `api/spawning/BestSpawner.kt` | `9192d2ea4c8edf87280b93b47179dce7644b0282ef42abfc899f843712ea9774` |
| `api/spawning/selection/FlatSelector.kt` | `29bf0912e76caed8dbd76caf8507d0341418df0be4e3e1c2de862a13e0a34b04` |
| `api/spawning/position/SpawnablePosition.kt` | `2bddf1b29167e1b0d2b0370bd7cda7f9dd0633f7b35472d6beb5f48ad0863635` |
| `api/spawning/influence/detector/SpawningInfluenceDetector.kt` | `e145ff581a570e4388b1f7af235699b668d4ae5aaa02326c201aea7b845dab83` |
| `api/spawning/CobblemonSpawningZoneGenerator.kt` | `ae8e5ecf6e41715072ee9565c6afce5418620cf3d335140a8d8a0087f55a8bba` |
| `api/spawning/detail/PokemonSpawnDetail.kt` | `52f9f21eeb1b764922ed67421ad5449cb12717f29bb230fd2924755365731c17` |
| `api/spawning/detail/PokemonHerdSpawnDetail.kt` | `feb554bd296f0d585f38631b4f13ef546fc23e87a8f80e7af60d3f43356565f6` |
| `api/spawning/spawner/PlayerSpawner.kt` | `251f8c8dc28feebe3c38ca3211a6de8a2618df8825fd67a0ff78989c12b52857` |
| `api/spawning/influence/SpawningInfluence.kt` | `36809aec48b106cdbd06603538f67a406de6989d1cae5ad30a3a5fdfc88ac5fa` |

Differences found within the inspected spawning source roots:

- Between `1.8.2` and `1.8.3`, `SubmergedSpawningCondition` and `SurfaceSpawningCondition` correct `maxDepth` merging to use the maximum-depth fields. `AreaSpawningCondition.copyFrom` now assigns merged minimum and maximum heights. These affect native datapack condition merging; OSCL keeps those conditions intact.
- Between `1.8.3` and `1.8.4`, `Spawner.isValidStartPoint` rewrites an equivalent Boolean expression as two explicit early returns for the non-solid-above and non-air-position checks. The `Spawner` signatures used by this API and the spawn selection order remain unchanged.
- The detector, influence, player-spawner, position-weight and selector sources listed in the table are byte-identical across all three versions. Thus no Mixin or version overlay is required for this weight integration on the supported matrix.

The `1.8.2` condition-merge defects are upstream and remain upstream. OSCL does not patch or work around them; it applies only after Cobblemon has produced its eligible candidate set.

## Implementation and proof boundary

The integration is one stateless Cobblemon detector plus server-local immutable modifier snapshots. It implements only `affectWeight`; `affectBucketWeights`, `injectSpawns`, position eligibility, spawn actions and spawned entities are untouched. Each zone influence uses a bounded-by-zone identity cache over encountered spawn details, discarded with the zone. Data reloads produce new details and new zone influences; no species/form cache survives a reload.

Automated unit tests exercise filter-to-weight evaluation, form ambiguity, scope/value validation, registry collisions, replacement generations, expiry, server-store isolation, stacking and a seeded weighted-distribution simulation. On Cobblemon 1.8.4, the local NeoForge client loaded Cobblemon, registered the spawn detector and weather hook, and completed asset loading. The local Fabric client registered the spawn detector, then Cobblemon crashed during `SpeciesAdditions` initialization with `ClassNotFoundException: net.minecraft.class_2960` from Kotlin reflection before the title screen. That failure occurs in Cobblemon's startup path; no live-world weight change was observed on either loader. The manual scenario in [SPAWNING.md](SPAWNING.md) remains the gameplay acceptance check.
