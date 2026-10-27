# Pokémon entity helpers

The `entity` package provides a small read-only bridge around Cobblemon's
`PokemonEntity`.

It exists for integrations that start from an in-world Pokémon entity but need
to reuse the library's normal Pokémon APIs.

## Access the underlying Pokémon

```java
Pokemon pokemon = PokemonEntities.pokemon(entity);
UUID pokemonId = PokemonEntities.pokemonId(entity);
PokemonIdentitySnapshot identity = PokemonEntities.identity(entity);
```

The returned `Pokemon` is the live Cobblemon object represented by the entity.

## Ownership

```java
Optional<UUID> ownerId = PokemonEntities.ownerId(entity);
boolean owned = PokemonEntities.hasOwner(entity);
```

`hasOwner` only means that Cobblemon currently exposes an owner UUID. The
library does not reinterpret that state as a gameplay rule.

## Cobblemon entity state

```java
boolean battling = PokemonEntities.isBattling(entity);
boolean busy = PokemonEntities.isBusy(entity);
boolean evolving = PokemonEntities.isEvolving(entity);
boolean present = PokemonEntities.isAliveAndPresent(entity);
```

These helpers are read-only and do not change AI, navigation, persistence,
battle state or despawn behavior.

## Server player distance

```java
double distanceSquared = PokemonEntities.nearestPlayerDistanceSquared(entity);
boolean withinRadius = distanceSquared <= radius * radius;
```

The method returns the squared distance to the nearest player in the entity's
server level. The result is cached per entity for the current server tick, so
call it on the server thread. It returns `Double.MAX_VALUE` for a non-server
entity or a level with no players.

Fabric and NeoForge clear the cache automatically when the server stops.
Custom lifecycle integrations can call
`PokemonEntities.clearNearestPlayerDistanceCache()` directly.

## Reuse matchers

```java
PokemonMatcher matcher =
        PokemonMatcher.fromProperties("species=pikachu shiny=true")
                .alpha(true);

boolean matches = PokemonEntities.matches(entity, matcher);
```

Any `Predicate<Pokemon>` can be used.

## Scope

The entity API deliberately does not wrap generic Minecraft methods such as
`moveTo`, `discard`, navigation or rotation. It only provides a stable bridge
for recurring Cobblemon-specific access.
