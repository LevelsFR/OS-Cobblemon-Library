# Pokémon creation and spawning

The library exposes command-free helpers built directly on Cobblemon's native
`PokemonProperties` API.

## Species resolution

Bare species IDs default to the Cobblemon namespace:

```java
Optional<Species> pikachu =
        PokemonSpeciesResolver.resolve("pikachu");

Optional<Species> addonSpecies =
        PokemonSpeciesResolver.resolve("my_addon:custom_species");
```

Use `require` when an invalid configured species should fail immediately:

```java
Species species = PokemonSpeciesResolver.require(configuredId);
```

This removes repeated manual namespace stripping and rebuilding.

## Native property parsing

```java
PokemonProperties properties =
        PokemonCreation.properties(
                "species=pikachu level=25 shiny=false"
        );
```

The query is parsed by Cobblemon itself, so registered custom properties remain
available.

## Create without spawning

```java
Pokemon pokemon =
        PokemonCreation.create("pikachu level=25");

PokemonEntity entity =
        PokemonCreation.createEntity(level, "pikachu level=25");
```

A `ServerPlayer` overload is available when Cobblemon rolls should use that
player as context.

## Spawn directly

```java
Optional<PokemonEntity> spawned =
        PokemonCreation.spawn(
                level,
                position,
                "species=pikachu level=25 shiny=false"
        );
```

The helper creates a real `PokemonEntity`, positions it and adds it directly
to the server level. No Minecraft command dispatcher is involved.

## Dynamic species, level and shiny

For the common case where those values already exist separately:

```java
PokemonCreation.spawnSpecies(
        level,
        position,
        configuredSpecies,
        rolledLevel,
        shiny
);
```

The species is validated against Cobblemon's registry before creation and the
level is passed through Cobblemon's native property parser.

## Prepared properties

For more complex setup, prepare a normal `PokemonProperties` instance and
spawn it afterward:

```java
PokemonProperties properties =
        PokemonCreation.forSpecies("pikachu");

properties.setShiny(true);

PokemonCreation.spawn(level, position, properties);
```

This keeps the full Cobblemon API available instead of replacing it with a
second builder system.
