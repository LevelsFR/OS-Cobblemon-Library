# Pokédex helpers

The `pokedex` package provides read-only helpers for the player Pokédex.

## Player Pokédex

```java
PokedexManager manager = PokedexQueries.manager(player);
```

A UUID overload is also available for server-side code that does not currently
hold a `ServerPlayer`.

## Species progress

```java
boolean seen = PokedexQueries.hasSeen(player, speciesId);
boolean caught = PokedexQueries.hasCaught(player, speciesId);

PokedexEntryProgress progress =
        PokedexQueries.progress(player, speciesId);
```

The helpers use Cobblemon's own `PokedexEntryProgress` values rather than
maintaining a second progression model.

## Form progress

```java
boolean seenForm = PokedexQueries.hasSeenForm(player, pokemon);
boolean caughtForm = PokedexQueries.hasCaughtForm(player, pokemon);
```

Form lookups use the species record and current Cobblemon form name.

## Known variations

```java
Set<String> aspects =
        PokedexQueries.seenAspects(player, speciesId);

boolean knowsAspect =
        PokedexQueries.hasSeenAspect(player, speciesId, "special");

boolean knowsCurrentShinyState =
        PokedexQueries.hasSeenShinyState(player, pokemon);
```

## Counts and percentages

```java
int seen = PokedexQueries.seenCount(player);
int caught = PokedexQueries.caughtCount(player);

float seenPercent = PokedexQueries.seenPercent(player);
float caughtPercent = PokedexQueries.caughtPercent(player);
```

The same queries accept a Pokédex ID for regional or custom dex calculations.
Cobblemon's own cached Pokédex calculators are used.
