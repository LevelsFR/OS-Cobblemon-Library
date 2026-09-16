# Pokémon identity helpers

The `pokemon` package provides a small shared layer for identity checks that
otherwise tend to be repeated across Cobblemon mods.

## Canonical species IDs

Use namespace-aware species IDs instead of display names or bare Showdown IDs:

```java
String speciesId = PokemonIdentity.speciesId(pokemon);
// cobblemon:pikachu
```

This keeps identifiers unambiguous when species are supplied by addons or other
namespaces.

## Forms

The standard form is represented by an empty form ID:

```java
String formId = PokemonIdentity.formId(pokemon);

if (PokemonIdentity.isNonStandardForm(pokemon)) {
    // Handle a non-standard form.
}
```

For non-standard forms the library uses Cobblemon's form-only Showdown ID.

## Aspects and marks

```java
boolean hasAspect = PokemonIdentity.hasAspect(pokemon, "some_aspect");
boolean hasMark = PokemonIdentity.hasMark(pokemon, markId);

Set<String> aspects = PokemonIdentity.aspects(pokemon);
Set<ResourceLocation> marks = PokemonIdentity.markIds(pokemon);
```

Mark lookups should prefer full `ResourceLocation` identifiers. The
`hasCobblemonMark` helper is available when a Cobblemon mark path is known.

## Alpha state

```java
boolean alpha = PokemonIdentity.isAlpha(pokemon);
```

Cobblemon 1.8 provides native Alpha state. The helper also recognizes the
legacy Alpha aspect and Alpha mark so consuming mods do not need to repeat that
compatibility handling.

## Snapshots

For caches, comparisons or persistence:

```java
PokemonIdentitySnapshot snapshot = PokemonIdentity.snapshot(pokemon);
```

The snapshot contains:

- Pokémon UUID
- namespaced species ID
- normalized form ID
- shiny state
- Alpha state

The snapshot intentionally does not include gameplay-specific state.
