# Pokémon matcher

`PokemonMatcher` is a thin reusable matcher that deliberately builds on
Cobblemon's native `PokemonProperties` system.

It does not reimplement Cobblemon's species, form, level, IV, EV, ability or
custom-property matching.

## Native properties

```java
PokemonMatcher matcher =
        PokemonMatcher.fromProperties("species=pikachu shiny=true level=50");

if (matcher.matches(pokemon)) {
    // Match.
}
```

Any property supported by the active Cobblemon installation can be used,
including registered custom properties.

## Alpha

```java
PokemonMatcher alphaPikachu =
        PokemonMatcher.fromProperties("pikachu")
                .alpha(true);
```

Alpha matching uses `PokemonIdentity.isAlpha`, including the compatibility
fallbacks maintained by the library.

## Aspects

Require all configured aspects:

```java
PokemonMatcher matcher =
        PokemonMatcher.any()
                .aspect("boss")
                .aspect("special");
```

Require at least one aspect from a set:

```java
PokemonMatcher matcher =
        PokemonMatcher.any()
                .anyAspect(List.of("boss", "special"));
```

## Marks

```java
PokemonMatcher matcher =
        PokemonMatcher.any()
                .mark(markId);
```

Mark matching uses full namespaced `ResourceLocation` identifiers.

## Java predicates

`PokemonMatcher` implements `Predicate<Pokemon>`, so it can be reused with
streams, collections and other Java APIs:

```java
List<Pokemon> filtered = pokemon.stream()
        .filter(matcher)
        .toList();
```

## Labels

Cobblemon data labels can be added without hard-coding category semantics:

```java
PokemonMatcher matcher =
        PokemonMatcher.any()
                .label("legendary");

PokemonMatcher special =
        PokemonMatcher.any()
                .anyLabel(List.of("legendary", "mythical"));
```

The checks use the current form's effective labels through `PokemonLabels`.

## Native size categories

Cobblemon's native XS/S/M/L/XL size categories can be matched directly:

```java
PokemonMatcher large =
        PokemonMatcher.any()
                .anySize(List.of(
                        PokemonSizeCategory.L,
                        PokemonSizeCategory.XL
                ));
```

Use `size(...)` when exactly one category is accepted. Size matching is kept
outside `PokemonProperties` because the native property syntax does not expose
this category.
