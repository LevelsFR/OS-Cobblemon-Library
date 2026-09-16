# Common development patterns

This page shows the preferred library patterns for recurring Cobblemon mod
features.

## Resolve configured species IDs

Instead of manually stripping or rebuilding namespaces:

```java
Species species = PokemonSpeciesResolver.require(configuredSpecies);
```

Bare IDs default to the Cobblemon namespace and explicit addon namespaces are
preserved.

## Spawn without commands

Instead of dispatching `/pokespawn` from code:

```java
PokemonCreation.spawnSpecies(
        level,
        position,
        configuredSpecies,
        rolledLevel,
        shiny
);
```

For complex properties:

```java
PokemonCreation.spawn(
        level,
        position,
        "species=pikachu level=25 shiny=true"
);
```

## Match Pokémon once, reuse everywhere

```java
PokemonMatcher matcher = PokemonMatcher
        .fromProperties("type=fire level=50")
        .alpha(false)
        .anyLabel(List.of("legendary", "mythical"));
```

The matcher can then be reused with streams, storage helpers or Pokémon
entities.

## Bridge a Pokémon entity

```java
Pokemon pokemon = PokemonEntities.pokemon(entity);
Optional<UUID> owner = PokemonEntities.ownerId(entity);

boolean matches = PokemonEntities.matches(entity, matcher);
```

Use the entity bridge for Cobblemon-specific state. Keep movement, AI,
navigation and despawn behavior inside the consuming mod.

## Read native size safely

```java
PokemonSizeCategory category = PokemonSize.category(pokemon);
float effectiveScale = PokemonSize.effectiveScale(pokemon);
```

The scale helpers normalize invalid, non-finite or non-positive values to
`1.0F`.

## Query player-owned Pokémon

```java
Optional<Pokemon> first =
        PokemonStorage.findFirstOwned(player, matcher);

List<Pokemon> all =
        PokemonStorage.findAllOwned(player, matcher);

int count =
        PokemonStorage.countOwned(player, matcher);
```

Party-only and PC-only variants are also available.

## Type-based compatibility

```java
boolean compatible = PokemonTypes.hasAnyType(
        pokemon,
        List.of("grass", "bug", "flying")
);
```

The current form is used, so form-specific typing is respected.

## Guarantee perfect IVs

```java
PokemonIVs.ensureMinimumPerfectNatural(
        pokemon,
        5,
        level.getRandom()
);
```

Natural IVs are kept separate from Hyper Trained effective IVs.

## Hidden Ability checks

```java
if (PokemonAbilities.hasHiddenAbility(pokemon)) {
    // Hidden Ability behavior.
}
```

Use `PokemonAbilities.kind(...)` when forced or unknown ability states also
matter.

## Pokédex-aware behavior

```java
if (PokedexQueries.hasSeen(player, pokemon)) {
    // The species has been encountered.
}

if (PokedexQueries.hasCaughtForm(player, pokemon)) {
    // This current form has been caught.
}
```

## Lifecycle-safe event subscriptions

```java
EventSubscriptionGroup subscriptions = new EventSubscriptionGroup();

subscriptions.track(
        CobblemonEventHooks.onCapture(this::handleCapture)
);

subscriptions.track(
        CobblemonEventHooks.onBattleVictory(this::handleVictory)
);

// Feature shutdown or reload:
subscriptions.close();
```

## KO and battle progression

```java
Pokemon defeated = BattleInspector.faintedPokemon(event);

if (BattleInspector.isWild(event.getBattle())) {
    // Wild battle progression.
}
```

Victory events can be inspected without manually walking internal battle
wrappers:

```java
boolean won = BattleInspector.didPlayerWin(event, player.getUUID());
```

## Stable identity for caches or saved state

```java
PokemonIdentitySnapshot identity =
        PokemonIdentity.snapshot(pokemon);
```

Use namespaced species IDs and normalized form IDs instead of display names for
persistent identifiers.
