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

## Client data and immutable snapshots

Client code can read the data Cobblemon has synchronized to the local player:

```java
PokedexSnapshot snapshot = ClientPokedexQueries.snapshot();
PokedexSnapshot.SpeciesEntry pikachu = snapshot
        .findSpecies(ResourceLocation.parse("cobblemon:pikachu"))
        .orElse(null);
```

Each species entry contains its progress, known aspects and form records. Form
entries contain the progress and shiny states recorded by the Pokédex. Form
names are enumerated from the species currently registered by Cobblemon; form
records for unavailable species definitions cannot be expanded into names.
All maps and sets in the snapshot are immutable copies. The snapshot represents
the data at the time of the call, so request a fresh one after updates.

`ClientPokedexQueries` is client-only. Cobblemon 1.8.1 does not expose a public
client-side callback for full or incremental Pokédex packet updates, so the
library does not provide an update listener. The manager may be empty before
Cobblemon finishes synchronizing the player's data.
