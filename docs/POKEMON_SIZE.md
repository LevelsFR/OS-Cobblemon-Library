# Pokémon size helpers

`PokemonSize` centralizes recurring access to Cobblemon's native Pokémon size
model.

## Native size category

```java
PokemonSizeCategory category = PokemonSize.category(pokemon);

boolean isXs =
        PokemonSize.isCategory(pokemon, PokemonSizeCategory.XS);
```

Entity overloads are available when the caller starts from a `PokemonEntity`.

## Translation key

```java
String key = PokemonSize.translationKey(pokemon);
```

The returned key is Cobblemon's own size-category translation key, so consuming
mods can decide how and where it should be rendered.

## Scale values

```java
float scale = PokemonSize.scaleModifier(pokemon);
float effective = PokemonSize.effectiveScale(pokemon);
```

Both methods defensively return `1.0F` when Cobblemon exposes a non-finite or
non-positive scale.

`effectiveScale` should be used when an integration cares about the final
effective Pokémon scale, including modifiers such as native Alpha scaling.

## Scope

The library exposes size data and matching only. Colors, labels, formatting and
other UI presentation remain inside the consuming mod.
