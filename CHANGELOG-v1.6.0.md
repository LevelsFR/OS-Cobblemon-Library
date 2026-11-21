# OS Cobblemon Library v1.6.0

## New

- Added **PokemonData regression tests** for defensive NBT copies, typed codec read/write, malformed stored values, failed-update rollback and idempotent removal.
- Added **species ID parsing tests** for bare Cobblemon IDs and explicitly namespaced addon species IDs.
- Added **cross-version build and test coverage** for Cobblemon 1.8.2, 1.8.3 and 1.8.4 on Fabric and NeoForge.
- Added a **NeoForge 1.8.4 runtime smoke check** for ambient battle-weather initialization, plus regression checks for server-stop distance-cache cleanup and the Cobblemon `boot()V` mixin target.

## Changes

- Added bounded retries for transient HTTP 429 and 5xx errors during Gradle builds and Maven Local publication.
- Added release checks that verify the project version embedded in both the Fabric and NeoForge JAR metadata.
- No public API or gameplay behavior changed in this maintenance release.

## Verification limits

- Fabric runtime injection remains unverified: Cobblemon 1.8.4 Kotlin reflection fails before the OSCL mixin target loads in the official-mapped development launch; the target is checked by an ASM regression test across the compatibility matrix.
- Live addon species registry lookup and active Pokémon form resolution require a fully bootstrapped game runtime and are not covered by the headless unit suite.
