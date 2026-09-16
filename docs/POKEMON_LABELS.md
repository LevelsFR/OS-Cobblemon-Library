# Pokémon labels

Cobblemon species and forms can expose data-driven labels. The library keeps
label access generic so consuming mods can define their own meaning.

## Read labels

```java
Set<String> labels = PokemonLabels.labels(pokemon);
```

Labels are normalized to lowercase and returned as an immutable snapshot.

## Check labels

```java
boolean matches = PokemonLabels.hasLabel(
        pokemon,
        "legendary"
);

boolean special = PokemonLabels.hasAnyLabel(
        pokemon,
        List.of("legendary", "mythical")
);

boolean all = PokemonLabels.hasAllLabels(
        pokemon,
        requiredLabels
);
```

The library deliberately does not define hard-coded categories such as
"legendary Pokémon". It only exposes the labels present in the active Cobblemon
data. This keeps addon-provided labels usable without library updates.

## Matchers

Labels can be combined directly with `PokemonMatcher`:

```java
PokemonMatcher matcher = PokemonMatcher
        .fromProperties("level=70")
        .anyLabel(List.of("legendary", "mythical"))
        .alpha(false);
```

That matcher can then be used directly with `PokemonStorage` predicate
queries.
