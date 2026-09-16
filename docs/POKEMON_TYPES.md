# Pokémon type helpers

`PokemonTypes` provides reusable checks against a Pokémon's current form.

## Current typing

```java
ElementalType primary = PokemonTypes.primary(pokemon);
Optional<ElementalType> secondary = PokemonTypes.secondary(pokemon);

List<ElementalType> types = PokemonTypes.types(pokemon);
```

Form-specific type changes are respected because the helpers query the current
`FormData`.

## Type names

```java
Set<String> names = PokemonTypes.typeNames(pokemon);
// for example: ["fire", "flying"]
```

## Check one type

```java
boolean fire = PokemonTypes.hasType(pokemon, "fire");
```

Type names are resolved through Cobblemon's active `ElementalTypes` registry,
so registered custom types can also be matched.

## Match station or feature requirements

At least one accepted type:

```java
boolean compatible = PokemonTypes.hasAnyType(
        pokemon,
        List.of("grass", "bug", "flying")
);
```

Require every configured type:

```java
boolean exactRequirement = PokemonTypes.hasAllTypes(
        pokemon,
        List.of("fire", "flying")
);
```

The helper does not assign gameplay meaning to a type. Consuming mods remain
responsible for deciding which types are compatible with a station, feature or
rule.
