# Spawn Modifier API

Introduced in OSCL 1.7.0 (unreleased development build). Publish the current source to Maven Local to use it; the published 1.6.0 library does not include this API.

The Spawn Modifier API adjusts the **selection weight** of Pokémon spawn details that Cobblemon has already accepted for a natural world-spawn attempt. It does not add spawn details, create Pokémon, or replace Cobblemon's conditions and spawn loop.

The API is available on Minecraft 1.21.1 with Cobblemon `1.8.2+1.21.1`, `1.8.3+1.21.1` and `1.8.4+1.21.1`, on Fabric and NeoForge. Check `SpawnModifiers.capabilities()` before advertising an effect. It reports the loaded Cobblemon version, supported scopes and whether form resolution is available. Registration fails explicitly when the native detector is unavailable.

## What the multiplier changes

Cobblemon first selects a rarity bucket. Within that bucket, native spawn details and their conditions are resolved. Cobblemon then calls `SpawnablePosition.getWeight(detail)`, which combines the detail's base weight, native influence weights and detail-specific weight multipliers. OSCL applies its modifier after those values and before `FlatSelector` chooses among the eligible details.

The multiplier changes a relative weight, not an absolute probability. If a detail's weight changes from 10 to 17.5, its final chance depends on the other eligible weights and spawnable positions in that bucket. Cobblemon's explicit `percentage` entries use a separate selection pass; OSCL leaves those details' weights untouched.

Bucket selection remains native. OSCL never calls `affectBucketWeights`, so boosting a detail does not boost its rarity bucket. Datapack filters, biome, time, weather, habitat, altitude, structure, population limits and position requirements remain in Cobblemon's candidate path. Since OSCL only overrides `affectWeight`, it cannot make an ineligible detail eligible.

## Basic global modifier

```java
import com.ourstory.oscobblemon.spawning.SpawnFilter;
import com.ourstory.oscobblemon.spawning.SpawnModifier;
import com.ourstory.oscobblemon.spawning.SpawnModifierHandle;
import com.ourstory.oscobblemon.spawning.SpawnModifiers;
import com.ourstory.oscobblemon.spawning.SpawnStackingPolicy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;

ResourceLocation source = ResourceLocation.fromNamespaceAndPath("examplemod", "seasonal_event");
SpawnModifier iceEvent = SpawnModifier.builder(
                ResourceLocation.fromNamespaceAndPath("examplemod", "winter_ice"), source)
        .filter(SpawnFilter.builder()
                .type("ice")
                .excludeLabels(java.util.List.of("legendary", "mythical"))
                .build())
        .multiplier(1.75)
        .stackingPolicy(SpawnStackingPolicy.max(
                ResourceLocation.fromNamespaceAndPath("examplemod", "seasonal_weather")))
        .build();

SpawnModifierHandle handle = SpawnModifiers.register(server, iceEvent);
```

The type filter matches a form's primary or secondary type. Multiple requested types are alternatives, and a dual-type form still matches this modifier only once. Labels are Cobblemon's data-driven form labels; OSCL does not decide which labels mean legendary, mythical or another category.

## Temporary player modifier

```java
import com.ourstory.oscobblemon.spawning.SpawnFilter;
import com.ourstory.oscobblemon.spawning.SpawnModifier;
import com.ourstory.oscobblemon.spawning.SpawnModifierHandle;
import com.ourstory.oscobblemon.spawning.SpawnModifierScope;
import com.ourstory.oscobblemon.spawning.SpawnModifiers;
import net.minecraft.resources.ResourceLocation;

SpawnModifierHandle charm = SpawnModifiers.register(server,
        SpawnModifier.builder(
                        ResourceLocation.fromNamespaceAndPath(
                                "examplemod", "player_fairy_bonus/" + player.getUUID()),
                        ResourceLocation.fromNamespaceAndPath("examplemod", "consumables"))
                .scope(SpawnModifierScope.player(player.getUUID()))
                .filter(SpawnFilter.builder().type("fairy").build())
                .multiplier(1.25)
                .expiresAfterTicks(36_000)
                .build());
```

The natural `PlayerSpawner` carries the responsible player's UUID. Player scope uses that UUID directly and never guesses from proximity. The three supported scopes are global to one server, one dimension, or one player. The capability is unavailable for a context that Cobblemon does not attribute to a `PlayerSpawner`; OSCL never turns that effect into a global one.

Modifier IDs are unique per server, so a per-player registration must include the player's UUID (as above) or another player's registration will collide. Keep the source ID shared by the consuming feature if `removeSource` should clear bonuses for all of its players.

```java
var capabilities = SpawnModifiers.capabilities();
if (!capabilities.available() || !capabilities.supportsPlayerContext()) {
    // Keep the consumer feature disabled for this runtime.
    return;
}
```

## Dimension, species and form filters

```java
SpawnModifier ghostsInTheNether = SpawnModifier.builder(
                ResourceLocation.fromNamespaceAndPath("examplemod", "nether_ghosts"),
                ResourceLocation.fromNamespaceAndPath("examplemod", "dimension_event"))
        .scope(SpawnModifierScope.dimension(ResourceLocation.fromNamespaceAndPath("minecraft", "the_nether")))
        .filter(SpawnFilter.builder().type("ghost").build())
        .multiplier(1.5)
        .build();

SpawnFilter alolanVulpix = SpawnFilter.builder()
        .form("cobblemon:vulpix", "alola")
        .type("ice")
        .build();
```

Bare species IDs default to the `cobblemon` namespace. Forms use the form-only Showdown ID; an empty form ID denotes the standard form. Addon species are resolved with their namespace preserved.

When `PokemonProperties.form` names a registered form, the candidate includes that form. If the form is absent or cannot be resolved, the candidate includes all registered forms for that species. Type, form and label criteria must match every possible form. This conservative rule prevents a type boost for Alolan Vulpix from also boosting a Vulpix definition whose form has not been resolved. Aspect filters use the aspect set Cobblemon has already resolved on the spawn properties. No full `Pokemon` object is constructed for matching.

`PokemonMatcher` remains intended for actual `Pokemon` values: its native property matcher and extra checks require a materialized Pokémon. Using it during weight selection would require constructing that object in a hot path, so Spawn Modifier filters reuse `PokemonSpeciesResolver`, `PokemonIdentity`, `PokemonTypes` and `PokemonLabels` with spawn-definition data instead.

## Stacking and reductions

Modifiers use a namespaced stacking group. The default group uses `MAX`:

- boosts in the same group use the largest multiplier;
- reductions in the same group use the strongest reduction;
- the selected boost and reduction are multiplied together;
- different groups multiply together.

Stacking groups are shared server-wide across mods. Leaving the default policy places a modifier in `os_cobblemon_library:default`, so default-`MAX` modifiers from different mods intentionally share one group. Use a feature-specific namespaced group such as `examplemod:winter_event` when the feature should stack independently. Mods should share an explicit group only when they intentionally coordinate and use the same policy; registering a different policy for an already-used group throws. For example, boosts `1.75` and `1.25` plus a reduction `0.5` in one MAX group produce `0.875`. An explicit `multiplicativeCapped(group, cap)` policy multiplies all matching values in that group and caps the upper result at `cap`; reductions are not raised to the cap. Remove that group's existing registrations before changing its policy.

Each modifier multiplier must be finite and in `[1e-6, 1e6]`. Zero is unsupported. If combining different groups would overflow Cobblemon's `Float` weight range, OSCL preserves the original native weight for that candidate. A result below the representable positive range is kept at `Float.MIN_VALUE`, so OSCL never turns a candidate into a zero-weight candidate.

## Remove and reload definitions

```java
ResourceLocation source = ResourceLocation.fromNamespaceAndPath("examplemod", "seasonal_event");
SpawnModifiers.removeSource(server, source);
SpawnModifier refreshed = SpawnModifier.builder(
                ResourceLocation.fromNamespaceAndPath("examplemod", "winter_ice"), source)
        .filter(SpawnFilter.builder().type("ice").build())
        .multiplier(2.0)
        .build();
SpawnModifiers.register(server, refreshed);
```

Removing by source makes configuration reloads idempotent. If only one modifier needs updating, use `replace` with its existing ID; it throws if the ID is not registered. A handle from the old generation cannot remove the replacement.

## Registration lifecycle

Modifier IDs are unique within one server. `register` throws on a collision; `replace` explicitly installs a new generation. A previous handle cannot remove the replacement. `remove`, `SpawnModifierHandle.remove()` and `removeSource` are safe to call repeatedly. `snapshot` returns an immutable list.

`expiresAfterTicks` measures server ticks rather than real-world time. It pauses with an integrated server pause, follows server tick progress under lag, and is discarded when the server stops. OSCL does not persist modifiers or decide how a consumer restores them after restart or player reconnection. Consumers should remove their source's old definitions and register the current configuration after a reload.

The registry is isolated by `MinecraftServer`; two integrated or dedicated servers in one process do not share modifiers. It uses immutable snapshots, per-zone candidate caching and a weak server key. No world, chunk or player scans are added. An attempt without active modifiers uses a shared empty snapshot.

## Native spawning and Poké Snacks

The detector contributes an influence only for Cobblemon's `PlayerSpawner` natural world-spawn path. Poké Snacks use a separate fixed-area spawner, `pokeSnackBuckets`, `BucketNormalizingInfluence` and `SpawnBaitInfluence`, and are not changed by this API. Fishing and other non-`PlayerSpawner` spawners are also outside this API's current scope. See [the v1.7 audit](COBBLEMON_1_7_SPAWNING_AUDIT.md) for source-level ordering and version comparisons.

## API overview

- `SpawnModifiers`: capability query, register, replace, find, snapshot, remove and source cleanup.
- `SpawnModifier`: immutable namespaced definition, scope, filter, multiplier, policy and optional duration.
- `SpawnModifierScope`: `global()`, `dimension(ResourceLocation)` or `player(UUID)`.
- `SpawnFilter`: types, species, exact forms, aspects, native labels, exclusions and bucket IDs.
- `SpawnStackingPolicy`: deterministic `max(group)` or `multiplicativeCapped(group, cap)`.
- `SpawnModifierHandle`, `SpawnModifierSnapshot` and `SpawnModifierCapabilities`: lifecycle and inspection values.

The Cobblemon detector and influence implementations remain under the unsupported internal package. Consumers should depend only on the documented `spawning` package.

## Manual gameplay acceptance

The automated suite tests the filter-to-weight evaluator and seeded weight distribution. To check the complete natural-spawn path in a client that starts successfully:

1. Use a fresh test world and a test datapack containing two valid, otherwise-equivalent Pokémon details in the same bucket and location, one Ice-type and one control species. Add a third Ice detail with a deliberately false native condition.
2. From a small test consumer, register the global Ice modifier shown above, then confirm `SpawnModifiers.snapshot(server)` contains it. Run a no-modifier baseline in a cloned world and an enabled run in a second clone, keeping the seed, player position, time, weather, pool and tick interval the same.
3. Record selections over a large sample. The Ice-to-control ratio should rise according to the native weighted selection in that bucket; the false-condition Ice detail must remain absent. Confirm the selected rarity bucket is unchanged.
4. Run the same world with an active modifier while triggering a Poké Snack. Its configured bucket and bait behavior should remain unchanged.
5. Register a `PLAYER` modifier for one of two separated players; verify only that player's natural spawner receives the weight change. Then remove the handle and reload the consumer configuration with `removeSource` to confirm the effect is gone.

This manual gameplay scenario has not been run as part of the v1.7 implementation; builds and the loader startup checks are reported separately from this acceptance evidence.
